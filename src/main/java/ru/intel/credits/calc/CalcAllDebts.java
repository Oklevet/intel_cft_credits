package ru.intel.credits.calc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.configuration.DataSource;
import ru.intel.credits.model.*;
import ru.intel.credits.repository.FillCollections;
import ru.intel.credits.repository.SqlCFTRepository;
import ru.intel.credits.repository.Sql2oRecieveDBRepository;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

public class CalcAllDebts {

    private SqlCFTRepository sql2oCFT;

    private Sql2oRecieveDBRepository sql2oRecieveDB;

    private static List credsId = new ArrayList();

    static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CalcAllDebts(SqlCFTRepository sql2oCFT, Sql2oRecieveDBRepository sql2oRecieveDB) {
        this.sql2oCFT = sql2oCFT;
        this.sql2oRecieveDB = sql2oRecieveDB;
    }

    /**
     * Получение пачки кредитов по которой будет выполняться расчет задолженностей
     */
    public static PrCred setCredArrs(PrCred cred, Long id, SqlCFTRepository sql2oCFT) {
        cred.setListFO((ArrayList<FactOper>) sql2oCFT.getAllFOByCreds(id));
        cred.setListPO((ArrayList<PlanOper>) sql2oCFT.getAllPOByCreds(id));

        return cred;
    }

    public static HashSet<Long> getCredDebts(PrCred cred, HashMap<Long, VidOperDog> opers) {
        HashSet<Long> debtsOfCred = new HashSet<>();

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
        debtsOfCred.remove(null);
        debtsOfCred.remove(0);

        return debtsOfCred;
    }

    public static List<Debt> credCalc(PrCred cred, HashMap<Long, VidOperDog> opers, HashMap<Long, VidDebt> vidDebts) {
        List<Debt> debts = new ArrayList<>();
        HashSet<Long> debtsOfCred = getCredDebts(cred, opers);
        debtsOfCred.remove(Long.valueOf(0));

        for (long debt : debtsOfCred) {
            switch (vidDebts.get(debt).getTypeDebt()) {
                case "Простая" -> {
                    DebtSimple modelDebt = new DebtSimple(cred.getCollectionDebts(), debt);
                    modelDebt.setSumma(modelDebt.calcDebt(cred, opers, debt));
                    debts.add(modelDebt);
                }
                case "Процентная" -> {
                    DebtPrc modelDebt = new DebtPrc(cred.getCollectionDebts(), debt);
                    modelDebt.setSumma(modelDebt.calcDebt(cred, opers, debt));
                    debts.add(modelDebt);
                }
                case "Комиссионная" -> {
                    DebtComiss modelDebt = new DebtComiss(cred.getCollectionDebts(), debt);
                    modelDebt.setSumma(modelDebt.calcDebt(cred, opers, debt));
                    debts.add(modelDebt);
                }
                default -> debts.isEmpty();
            }
        }
        return debts;
    }

    public synchronized static List getSubList() {
        log.debug("getSubList start " + credsId.size());
        int subSize = 100;
        List result;
        try {
            if (credsId.size() >= subSize) {
                result = credsId.subList(0, subSize);
                credsId = List.copyOf(credsId.subList(subSize, credsId.size()));
            } else {
                if (credsId.size() > 0) {
                    result = credsId.subList(0, credsId.size());
                    credsId = List.of();
                } else {
                    log.debug("getSubList finish" + credsId.size());
                    return List.of();
                }
            }
            log.debug("getSubList finish" + credsId.size());
            return result;
        }catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("getSubList finish" + credsId.size());
        return List.of();
    }

    public static boolean checkSetOfTaskIsDone(Set<Future> setOfTasks) {
        for (Future future : setOfTasks) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    public static int plugTask(DataSource dataSourceCFT, DataSource dataSourceReceiver, HashMap<Long, VidOperDog> opers,
                               HashMap<Long, VidDebt> vidDebts) {
        log.debug("start new run " + Thread.currentThread().getName());

        SqlCFTRepository runSqlCFT = new SqlCFTRepository(dataSourceCFT);
        Sql2oRecieveDBRepository runSqlRecieveDB = new Sql2oRecieveDBRepository(dataSourceReceiver);
        List<Debt> debts = new ArrayList<>();
        List<PrCred> creds = new ArrayList<>();

        List<Long> listIds = getSubList();


        while (listIds.size() > 0) {

            listIds.stream().forEach(x -> {

                PrCred cred = runSqlCFT.getCred(x);

                creds.add(cred);
                cred.setCollectionDebts(runSqlRecieveDB.getSequence());
                cred = setCredArrs(cred, x, runSqlCFT);
                debts.addAll(credCalc(cred, opers, vidDebts));
            });

            listIds = getSubList();

            log.debug("insert debts size = " + debts.size());
            if (debts.size() > 0) {
                runSqlRecieveDB.insertAllDebts(debts, vidDebts);
                runSqlRecieveDB.insertAllCreds(creds);
            }
        }
        return 1;
    }

    public static void calcAllCreds(DataSource dataSourceCFT, DataSource dataSourceReceiver, int countTasks) {

        /**
         * Обновление необходимых для расчетов справочников VID_DEBT и VID_OPER_DOG
         * Получение множества кредитов, по которым осуществляется расчет задолженностей
         */

        final ExecutorService executor = Executors.newFixedThreadPool(10);
        FillCollections fillCollections = new FillCollections();
        HashMap<Long, VidDebt> vidDebts;
        HashMap<Long, VidOperDog> opers;
        SqlCFTRepository sql2oCFT = new SqlCFTRepository(dataSourceCFT);
        Set<Future> setOfTasks = new HashSet<>();

        log.debug("start calcAllCreds");

        Callable<HashMap<Long, VidDebt>> taskActualVidDebts = () -> {
                return (HashMap<Long, VidDebt>) sql2oCFT.getAllVidDebts();
        };

        Callable<HashMap<Long, VidOperDog>> taskActuaOpers = () -> {
                return (HashMap<Long, VidOperDog>)
                fillCollections.fillOperDebets(sql2oCFT.getAllVidOperDogs(), sql2oCFT.getAllTakeInDebt());
        };

        Callable<List> taskGetCredId = () -> {
            return (List) sql2oCFT.getIDAllCreds();
        };

        Future<HashMap<Long, VidDebt>> futureActVidDebts = executor.submit(taskActualVidDebts);
        Future<HashMap<Long, VidOperDog>> futureActOpers = executor.submit(taskActuaOpers);
        Future<List> futureIdCreds = executor.submit(taskGetCredId);

        while (!futureActVidDebts.isDone() || !futureActOpers.isDone() || !futureIdCreds.isDone()) {
            try {
                log.debug("wait calc directories");
                log.debug("vid debts = " + futureActVidDebts.isDone() + ";   opers = " + futureActVidDebts.isDone() + ";  id creds = " + futureActVidDebts.isDone());
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            vidDebts = futureActVidDebts.get();
            opers = futureActOpers.get();
            credsId = futureIdCreds.get();
            log.debug("get futures");
            log.debug("opers = " + opers.size() + "   vidDebts = " + vidDebts.size() + "  credsId = " + credsId.size());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        /**
         * Задание производит расчет данных по кредиту на основе справочных данных и расчитывает задолженности
         * После того как все ID в множестве credsId будут рассчитаны, задание вставит результат в БД получателя
         * и завершит работу
         */

        //лямблы и строим api supply consumer predicat
        //бегать в БД для саб листа единожды. Достать старый код по сортировки массивов кредита
        Callable taskCalcAndInsertDebts = () -> {
            log.debug("start new run " + Thread.currentThread().getName());

            SqlCFTRepository runSqlCFT = new SqlCFTRepository(dataSourceCFT);
            Sql2oRecieveDBRepository runSqlRecieveDB = new Sql2oRecieveDBRepository(dataSourceReceiver);
            List<Debt> debts = new ArrayList<>();
            List<PrCred> creds = new ArrayList<>();

            List<Long> listIds = getSubList();

            while (listIds.size() > 0) {
                listIds.stream().forEach(x -> {
                    PrCred cred = runSqlCFT.getCred(x);
                    creds.add(cred);
                    cred.setCollectionDebts(runSqlRecieveDB.getSequence());
                    cred = setCredArrs(cred, x, runSqlCFT);
                    debts.addAll(credCalc(cred, opers, vidDebts));
                });

                listIds = getSubList();

                log.debug("insert debts size = " + debts.size());
                if (debts.size() > 0) {
                    runSqlRecieveDB.insertAllDebts(debts, vidDebts);
                    runSqlRecieveDB.insertAllCreds(creds);
                }
            }
            return 1;
        };

        for (int i = 0; i < countTasks; i++) {
            setOfTasks.add(executor.submit(taskCalcAndInsertDebts));
            log.debug("submit run index = " + i);
        }

        //plugTask(dataSourceCFT, dataSourceReceiver, opers, vidDebts);

        try {
            log.debug("sleep for 30 sec ");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!checkSetOfTaskIsDone(setOfTasks)) {
            try {
                log.debug("tasks is working ");
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.debug("end of calc method");
    }
}
