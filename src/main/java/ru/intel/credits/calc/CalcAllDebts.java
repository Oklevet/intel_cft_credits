package ru.intel.credits.calc;

import ru.intel.credits.calc.debts.CalcComissDebt;
import ru.intel.credits.calc.debts.CalcPrcDebt;
import ru.intel.credits.calc.debts.CalcSimpleDebt;
import ru.intel.credits.configuration.DataSource;
import ru.intel.credits.model.*;
import ru.intel.credits.repository.FillCollections;
import ru.intel.credits.repository.SqlCFTRepository;
import ru.intel.credits.repository.Sql2oRecieveDBRepository;

import java.util.*;
import java.util.concurrent.*;

public class CalcAllDebts {

    SqlCFTRepository sql2oCFT;

    Sql2oRecieveDBRepository sql2oRecieveDB;

    public CalcAllDebts(SqlCFTRepository sql2oCFT, Sql2oRecieveDBRepository sql2oRecieveDB) {
        this.sql2oCFT = sql2oCFT;
        this.sql2oRecieveDB = sql2oRecieveDB;
    }

    /**
     * Получение пачки кредитов по которой будет выполняться расчет задолженностей
     */
    public static PrCred setCredArrs(PrCred cred, int id, SqlCFTRepository sql2oCFT) {
        cred.setListFO((ArrayList<FactOper>) sql2oCFT.getAllFOByCreds(id));
        cred.setListPO((ArrayList<PlanOper>) sql2oCFT.getAllPOByCreds(id));

        return cred;
    }

    public static HashSet<Integer> getCredDebts(PrCred cred, HashMap<Integer, VidOperDog> opers) {
        HashSet<Integer> debtsOfCred = new HashSet<>();

        cred.getListFO().forEach(x -> {
            debtsOfCred.add(x.getVidDebt());
            debtsOfCred.add(x.getVidDebtDt());
            opers.get(x.getOper()).getDebets().forEach(d -> debtsOfCred.add(d.getDebt()));
        });

        cred.getListPO().forEach(x -> {
            debtsOfCred.add(x.getVidDebt());
            debtsOfCred.add(x.getVidDebtDt());
            opers.get(x.getOper()).getDebets().forEach(d -> debtsOfCred.add(d.getDebt()));
        });

        return debtsOfCred;
    }

    public static List<Debt> credCalc(PrCred cred, HashMap<Integer, VidOperDog> opers, HashMap<Integer, VidDebt> vidDebts) {
        CalcSimpleDebt calcSimpleDebt = new CalcSimpleDebt();
        CalcPrcDebt calcPrcDebt = new CalcPrcDebt();
        CalcComissDebt calcComissDebt = new CalcComissDebt();
        List<Debt> debts = new ArrayList<>();
        HashSet<Integer> debtsOfCred = getCredDebts(cred, opers);

        for (Integer debt : debtsOfCred) {
            switch (vidDebts.get(debt).getTypeDebt()) {
                case "Простая" ->
                        debts.add(new Debt(cred.getCollectionDebts(), debt, calcSimpleDebt.calcDebt(cred, opers, debt)));
                case "Процентная" ->
                        debts.add(new Debt(cred.getCollectionDebts(), debt, calcPrcDebt.calcDebt(cred, opers, debt)));
                case "Комиссионная" ->
                        debts.add(new Debt(cred.getCollectionDebts(), debt, calcComissDebt.calcDebt(cred, opers, debt)));
                default -> debts.isEmpty();
            }
        }
        return debts;
    }

    public static void calcAllCreds(DataSource dataSourceCFT, DataSource dataSourceReceiver, int countTasks) {

        /**
         * Обновление необходимых для расчетов справочников VID_DEBT и VID_OPER_DOG
         * Получение множества кредитов, по которым осуществляется расчет задолженностей
         */

        final ExecutorService executor = Executors.newFixedThreadPool(10);
        FillCollections fillCollections = new FillCollections();
        HashMap<Integer, VidDebt> vidDebts;
        HashMap<Integer, VidOperDog> opers;
        HashSet<Integer> credsId;
        SqlCFTRepository sql2oCFT = new SqlCFTRepository(dataSourceCFT);

        Callable<HashMap<Integer, VidDebt>> taskActualVidDebts = () -> {
                return (HashMap<Integer, VidDebt>) sql2oCFT.getAllVidDebts();
        };

        Callable<HashMap<Integer, VidOperDog>> taskActuaOpers = () -> {
                return (HashMap<Integer, VidOperDog>)
                fillCollections.fillOperDebets(sql2oCFT.getAllVidOperDogs(), sql2oCFT.getAllTakeInDebt());
        };

        Callable<HashSet> taskGetCredId = () -> {
            return (HashSet) sql2oCFT.getIDAllCreds();
        };

        Future<HashMap<Integer, VidDebt>> futureActVidDebts = executor.submit(taskActualVidDebts);
        Future<HashMap<Integer, VidOperDog>> futureActOpers = executor.submit(taskActuaOpers);
        Future<HashSet> futureIdCreds = executor.submit(taskGetCredId);

        while (!futureActVidDebts.isDone() || !futureActOpers.isDone() || !futureIdCreds.isDone()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            vidDebts = futureActVidDebts.get();
            opers = futureActOpers.get();
            credsId = futureIdCreds.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        /**
         * Задание производит расчет данных по кредиту на основе справочных данных и расчитывает задолженности
         * После того как все ID в множестве credsId будут рассчитаны, задание вставит результат в БД получателя
         * и завершит работу
         */
        Runnable taskCalcAndInsertDebts = () -> {
            List<Debt> debts = new ArrayList<>();
            int id = 0;
            int size = 1;

            while (size > 0) {
                synchronized (credsId) {
                    for (Integer i : credsId) {
                        id = i;
                    }
                    credsId.remove(id);
                }

                SqlCFTRepository runSqlCFT = new SqlCFTRepository(dataSourceCFT);
                Sql2oRecieveDBRepository runSqlRecieveDB = new Sql2oRecieveDBRepository(dataSourceReceiver);
                CalcAllDebts calcAllDebtsRun = new CalcAllDebts(runSqlCFT, runSqlRecieveDB);

                PrCred cred = runSqlCFT.getCred(id);
                cred.setCollectionDebts(runSqlRecieveDB.getSequence());
                cred = setCredArrs(cred, id, sql2oCFT);
                debts.addAll(credCalc(cred, opers, vidDebts));

                synchronized (credsId) {
                    size = credsId.size();
                }
            }

            Sql2oRecieveDBRepository runSqlReceiveDB = new Sql2oRecieveDBRepository(dataSourceReceiver);
            runSqlReceiveDB.insertAllDebts(debts, vidDebts);
        };

        for (int i = 0; i < countTasks; i++) {
            executor.submit(taskCalcAndInsertDebts);
        }
    }
}
