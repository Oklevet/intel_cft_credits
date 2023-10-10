package ru.intel.сredits.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.intel.сredits.model.*;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Sql2oRecieveDBRepository implements RecieveDBRepository {

    private final Sql2o sql2o;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public Sql2oRecieveDBRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    @Override
    public void insertAllCreds(Collection<PrCred> creds) {

        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO intel_receiver.PR_CRED (NUM_DOG, DEBTS) VALUES (:NUM_DOG, :DEBTS)""";
            var query = connection.createQuery(sql, true);
            creds.forEach(x -> {
                query.addParameter("NUM_DOG", x.getNumDog());
                query.addParameter("DEBTS", x.getCollectionDebts());
                query.addToBatch();
            });
            query.executeBatch();
        }
    }

    @Override
    public void insertAllDebts(Collection<Debt> debts, HashMap<Integer, VidDebt> dirDebts) {

        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO intel_receiver.DEBTS (CODE, SUMMA, collection_id) VALUES (:CODE, :SUMMA, :collection_id)""";
            var query = connection.createQuery(sql, true);
            debts.forEach(x -> {
                query.addParameter("CODE", dirDebts.get(x.getId()).getCode());
                query.addParameter("SUMMA", x.getSumma());
                query.addParameter("collection_id", x.getCollectionId());
                query.addToBatch();
            });
            query.executeBatch();
        }
    }

    /**
     * Обеспечение уникальности ключа для collection_id.
     * 1. Пожертвовать производительностью в пользу уникальности значения.
     * 2. Пожертвовать уникальность collection_id, что не сильно критично, для реализации буферного запроса для большой пачки кредитов
     */
    @Override
    public Integer getSequence(int count) {
        int result = 0;
        var sql = "select nextval('intel_receiver.serial');";
        Query query = null;

        try (var connection = sql2o.open()) {
            query = connection.createQuery(sql, true);
            result = query.executeScalar(Integer.class);
        }

        if (count > 1) {
            try (var connection = sql2o.open()) {
                for (int i = 0; i < count - 1; i++) {
                    query = connection.createQuery(sql, true)
                            .addToBatch();
                }
                query.executeBatch();
            }
        }
        return result;
    }
}