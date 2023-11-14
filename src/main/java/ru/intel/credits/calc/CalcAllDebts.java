package ru.intel.credits.calc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.calc.debts.CalcComissDebt;
import ru.intel.credits.calc.debts.CalcPrcDebt;
import ru.intel.credits.calc.debts.CalcSimpleDebt;
import ru.intel.credits.configuration.DataSource;
import ru.intel.credits.model.*;
import ru.intel.credits.repository.FillCollections;
import ru.intel.credits.repository.SqlCFTRepository;
import ru.intel.credits.repository.Sql2oRecieveDBRepository;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.*;

public class CalcAllDebts {

    private SqlCFTRepository sql2oCFT;

    private Sql2oRecieveDBRepository sql2oRecieveDB;

    private static List credsId = new ArrayList();

    static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
        debtsOfCred.remove(0);

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

    public synchronized static List getSubList() {
        int subSize = 10000;
        List result;
        if (credsId.size() >= subSize) {
            result = credsId.subList(0, 10000);
            credsId.subList(0, 10000).clear();
        } else {
            if (credsId.size() > 0) {
                result = credsId.subList(0, credsId.size());
                credsId = List.of();
            } else {
                return List.of();
            }
        }
        return result;
    }

    public static boolean checkSetOfTaskIsDone(Set<Future> setOfTasks) {
        for (Future future : setOfTasks) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
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
        SqlCFTRepository sql2oCFT = new SqlCFTRepository(dataSourceCFT);
        Set<Future> setOfTasks = new HashSet<>();

        LOG.debug("start calcAllCreds");

        Callable<HashMap<Integer, VidDebt>> taskActualVidDebts = () -> {
                return (HashMap<Integer, VidDebt>) sql2oCFT.getAllVidDebts();
        };

        Callable<HashMap<Integer, VidOperDog>> taskActuaOpers = () -> {
                return (HashMap<Integer, VidOperDog>)
                fillCollections.fillOperDebets(sql2oCFT.getAllVidOperDogs(), sql2oCFT.getAllTakeInDebt());
        };

        Callable<List> taskGetCredId = () -> {
            return (List) sql2oCFT.getIDAllCreds();
        };

        Future<HashMap<Integer, VidDebt>> futureActVidDebts = executor.submit(taskActualVidDebts);
        Future<HashMap<Integer, VidOperDog>> futureActOpers = executor.submit(taskActuaOpers);
        Future<List> futureIdCreds = executor.submit(taskGetCredId);

        while (!futureActVidDebts.isDone() || !futureActOpers.isDone() || !futureIdCreds.isDone()) {
            try {
                LOG.debug("wait calc directories");
                LOG.debug("vid debts = " + futureActVidDebts.isDone() + ";   opers = " + futureActVidDebts.isDone() + ";  id creds = " + futureActVidDebts.isDone());
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            vidDebts = futureActVidDebts.get();
            opers = futureActOpers.get();
            credsId = futureIdCreds.get();
            LOG.debug("get futures");
            LOG.debug("opers = " + opers.size() + "    vidDebts = " + vidDebts.size());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        /**
         * Задание производит расчет данных по кредиту на основе справочных данных и расчитывает задолженности
         * После того как все ID в множестве credsId будут рассчитаны, задание вставит результат в БД получателя
         * и завершит работу
         */
        Callable taskCalcAndInsertDebts = () -> {
            LOG.debug("start new run " + Thread.currentThread().getName());

            SqlCFTRepository runSqlCFT = new SqlCFTRepository(dataSourceCFT);
            Sql2oRecieveDBRepository runSqlRecieveDB = new Sql2oRecieveDBRepository(dataSourceReceiver);
            List<Debt> debts = new ArrayList<>();
            List<PrCred> creds = new ArrayList<>();

            List<Integer> listIds = getSubList();

            while (listIds.size() > 0) {
                LOG.debug("start new cred pack ");

                for (Integer id : listIds) {
                    CalcAllDebts calcAllDebtsRun = new CalcAllDebts(runSqlCFT, runSqlRecieveDB);

                    PrCred cred = runSqlCFT.getCred(id);

                    creds.add(cred);
                    cred.setCollectionDebts(runSqlRecieveDB.getSequence());
                    cred = setCredArrs(cred, id, sql2oCFT);
                    debts.addAll(credCalc(cred, opers, vidDebts));
                }
                LOG.debug("calc debts ");
                listIds = getSubList();
            }


            LOG.debug("debts size = " + debts.size());
            LOG.debug("creds size = " + creds.size());

            LOG.debug("insert debts ");
            if (debts.size() > 0) {
                runSqlRecieveDB.insertAllDebts(debts, vidDebts);
                runSqlRecieveDB.insertAllCreds(creds);
            }
            return 1;
        };

        for (int i = 0; i < countTasks; i++) {
            setOfTasks.add(executor.submit(taskCalcAndInsertDebts));
            LOG.debug("submit run index = " + i);
        }

        try {
            LOG.debug("sleep for 30 sec ");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!checkSetOfTaskIsDone(setOfTasks)) {
            try {
                LOG.debug("tasks is working ");
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LOG.debug("end of calc method");
    }
}
