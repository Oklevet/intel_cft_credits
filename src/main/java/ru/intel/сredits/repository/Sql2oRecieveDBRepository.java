package ru.intel.сredits.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import ru.intel.сredits.model.*;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Sql2oRecieveDBRepository implements RecieveDBRepository {

    private final Sql2o sql2o;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Sql2oRecieveDBRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * ИСПРАВИТЬ МНОЖЕСТВЕННЫЙ ИНСЕРТ
     */

    @Override
    public Integer insertAllCreds(Collection<PrCred> creds) {
        try (var connection = sql2o.open()) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            var sql = """
                      INSERT INTO PR_CRED(NUM_DOG, DEBTS) VALUES (:NUM_DOG, :DEBTS)""";
            creds.stream().map(x -> {
                var query = connection.createQuery(sql, true)
                        .addParameter("NUM_DOG", x.getNumDog())
                        .addParameter("DEBTS", x.getCollectionDebts())
                        .addToBatch();
                atomicInteger.addAndGet(query.executeBatch().getResult());
                return null;
            });
            return atomicInteger.get();
        } catch (Exception e) {
            LOG.error("При вставке кредитов " + creds.toString() + " произошла ошибка: " + e.fillInStackTrace());
        }
        return 0;
    }

    @Override
    public Integer insertAllDebts(Collection<Debt> debts, HashMap<Integer, VidDebt> dirDebts) {

        try (var connection = sql2o.open()) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            var sql = """
                      INSERT INTO DEBTS(CODE, SUMMA, collection_id) VALUES (:CODE, :SUMMA, :collection_id)""";
            debts.stream().map(x -> {
                var query = connection.createQuery(sql, true)
                        .addParameter("CODE", dirDebts.get(x.getId()).getCode())
                        .addParameter("SUMMA", x.getSumma())
                        .addParameter("collection_id", x.getCollectionId())
                        .addToBatch();
                atomicInteger.addAndGet(query.executeBatch().getResult());
                return null;
            });
            return atomicInteger.get();
        } catch (Exception e) {
            LOG.error("При вставке задолженностей " + debts.toString() + " произошла ошибка: " + e.fillInStackTrace());
        }
        return 0;
    }
}
