package ru.intel.credits.calc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.configuration.DataSource;
import ru.intel.credits.model.*;
import ru.intel.credits.repository.FillCollections;
import ru.intel.credits.repository.SqlCFTRepository;
import ru.intel.credits.repository.SqlRecieveDBRepository;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class CalcAllDebts {

    private SqlCFTRepository sql2oCFT;

    private SqlRecieveDBRepository sql2oRecieveDB;

    private static List credsId = new ArrayList();

    static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CalcAllDebts(SqlCFTRepository sql2oCFT, SqlRecieveDBRepository sql2oRecieveDB) {
        this.sql2oCFT = sql2oCFT;
        this.sql2oRecieveDB = sql2oRecieveDB;
    }

    /**
     * Получение пачки кредитов по которой будет выполняться расчет задолженностей
     */
    public static PrCred setCredArrs(PrCred cred, Long id, SqlCFTRepository sql2oCFT) {
        cred.setListFO((ArrayList<FactOper>) sql2oCFT.getAllFOByCred(id));
        cred.setListPO((ArrayList<PlanOper>) sql2oCFT.getAllPOByCred(id));

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
                    modelDebt.setSumma((double) modelDebt.calcDebt(cred, opers, debt) / 100);
                    debts.add(modelDebt);
                }
                case "Процентная" -> {
                    DebtPrc modelDebt = new DebtPrc(cred.getCollectionDebts(), debt);
                    modelDebt.setSumma((double) modelDebt.calcDebt(cred, opers, debt) / 100);
                    debts.add(modelDebt);
                }
                case "Комиссионная" -> {
                    DebtComiss modelDebt = new DebtComiss(cred.getCollectionDebts(), debt);
                    modelDebt.setSumma((double) modelDebt.calcDebt(cred, opers, debt) / 100);
                    debts.add(modelDebt);
                }
                default -> debts.isEmpty();
            }
        }
        return debts;
    }

    public static int plugTask(DataSource dataSourceCFT, DataSource dataSourceReceiver, HashMap<Long, VidOperDog> opers,
                               HashMap<Long, VidDebt> vidDebts, Queue queue) throws InterruptedException {
        log.debug("start new run " + Thread.currentThread().getName());

        FillCollections fillCollecThd = new FillCollections();
        SqlCFTRepository runSqlCFT = new SqlCFTRepository(dataSourceCFT);
        SqlRecieveDBRepository runSqlRecieveDB = new SqlRecieveDBRepository(dataSourceReceiver);
        List<Debt> debts = new ArrayList<>();
        List<PrCred> creds = new ArrayList<>();
        HashMap<Long, List<FactOper>> foOpers;
        HashMap<Long, List<PlanOper>> poOpers;
        SqlRecieveDBRepository sqlRecieveDB = new SqlRecieveDBRepository(dataSourceReceiver);
        AtomicLong seqId = sqlRecieveDB.getSequence();

        List<Long> listIds = queue.getSubList();
        log.debug("get first listIds = " + listIds.size());
        while (listIds.size() > 0) {
            creds = runSqlCFT.getCreds(listIds);
            foOpers = runSqlCFT.getAllFOByCreds(listIds);
            poOpers = runSqlCFT.getAllPOByCreds(listIds);
            creds = fillCollecThd.fillFoPoDebtInCreds(creds, foOpers, poOpers, seqId.getAndAdd(listIds.size()));
            creds.forEach(x -> debts.addAll(credCalc(x, opers, vidDebts)));

            listIds = queue.getSubList();

            log.debug("insert debts size = " + debts.size());
            if (debts.size() > 0) {
                runSqlRecieveDB.insertAllDebts(debts, vidDebts);
                runSqlRecieveDB.insertAllCreds(creds);
                debts.clear();
                creds.clear();
            }

            Thread.sleep(100);
        }
        return 1;
    }

    public static void calcAllCreds(DataSource dataSourceCFT, DataSource dataSourceReceiver, int countTasks) throws ExecutionException, InterruptedException {

        /**
         * Обновление необходимых для расчетов справочников VID_DEBT и VID_OPER_DOG
         * Получение множества кредитов, по которым осуществляется расчет задолженностей
         */

        final ExecutorService executor = Executors.newFixedThreadPool(10);
        FillCollections fillCollections = new FillCollections();
        HashMap<Long, VidDebt> vidDebts;
        HashMap<Long, VidOperDog> opers;
        SqlCFTRepository sql2oCFT = new SqlCFTRepository(dataSourceCFT);
        SqlRecieveDBRepository sqlRecieveDB = new SqlRecieveDBRepository(dataSourceReceiver);
        Set<Future> setOfTasks = new HashSet<>();
        AtomicLong seqId = sqlRecieveDB.getSequence();

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

        Queue queue = new Queue(credsId);
        log.debug("queue lst = " + queue.getCredsId().size());
        /**
         * Задание производит расчет данных по кредиту на основе справочных данных и расчитывает задолженности
         * После того как все ID в множестве credsId будут рассчитаны, задание вставит результат в БД получателя
         * и завершит работу
         */
        Callable taskCalcAndInsertDebts = () -> {
            log.debug("start new run " + Thread.currentThread().getName());
            ////
            int sizeCrdes = 0;
            int sizeDebts = 0;
            ///

            FillCollections fillCollecThd = new FillCollections();
            SqlCFTRepository runSqlCFT = new SqlCFTRepository(dataSourceCFT);
            SqlRecieveDBRepository runSqlRecieveDB = new SqlRecieveDBRepository(dataSourceReceiver);
            List<Debt> debts = new ArrayList<>();
            List<PrCred> creds = new ArrayList<>();
            HashMap<Long, List<FactOper>> foOpers;
            HashMap<Long, List<PlanOper>> poOpers;

            List<Long> listIds = queue.getSubList();
            log.debug("get first listIds = " + listIds.size());
            while (listIds.size() > 0) {
                creds = runSqlCFT.getCreds(listIds);
                foOpers = runSqlCFT.getAllFOByCreds(listIds);
                poOpers = runSqlCFT.getAllPOByCreds(listIds);
                creds = fillCollecThd.fillFoPoDebtInCreds(creds, foOpers, poOpers, seqId.getAndAdd(listIds.size()));
                creds.forEach(x -> debts.addAll(credCalc(x, opers, vidDebts)));

                sizeCrdes += listIds.size();
                sizeDebts += debts.size();

                listIds = queue.getSubList();

                log.debug("insert debts size = " + debts.size());
                if (debts.size() > 0) {
                    runSqlRecieveDB.insertAllDebts(debts, vidDebts);
                    runSqlRecieveDB.insertAllCreds(creds);
                    debts.clear();
                    creds.clear();
                }

                Thread.sleep(100);
            }
            log.debug("End RUN sizeCrdes = " + sizeCrdes);
            log.debug("End RUN sizeDebts = " + sizeDebts);
            return 1;
        };

        /**
         * Тестовый метод для запуска расчетов в Main потоке
         */
//        plugTask(dataSourceCFT, dataSourceReceiver, opers, vidDebts, queue);

        for (int i = 0; i < countTasks; i++) {
            setOfTasks.add(executor.submit(taskCalcAndInsertDebts));
            log.debug("submit run index = " + i);
        }

        while (!setOfTasks.stream().allMatch(Future::isDone)) {
            boolean hasError = setOfTasks.stream()
                    .filter(Future::isDone)
                    .map(future -> {
                        try {
                            future.get();
                            return null;
                        } catch (InterruptedException | ExecutionException e) {
                            return e;
                        }
                    })
                    .anyMatch(Objects::nonNull);
            if (hasError) {
                setOfTasks.stream().filter(f -> !f.isDone()).forEach(f -> f.cancel(true));
            }
        }
        executor.shutdown();

        log.debug("end of calc method");
        log.debug("setOfTask = " + setOfTasks.size());
    }
}
