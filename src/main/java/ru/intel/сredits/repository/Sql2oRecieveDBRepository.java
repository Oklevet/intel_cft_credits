package ru.intel.сredits.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import ru.intel.сredits.model.*;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

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
            var sql = """
                      INSERT INTO PR_CRED(NUM_DOG, DEBTS) VALUES (:list.getNumDog, :list.getDebts)""";
            var query = connection.createQuery(sql, true)
                    .addParameter("NUM_DOG", creds)
                    .addParameter("DEBTS", creds);
            return query.executeUpdate().getResult();
        } catch (Exception e) {
            LOG.error("При вставка кредитов " + creds.toString() + " произошла ошибка: " + e.fillInStackTrace());
        }
        return 0;
    }

    @Override
    public Integer insertAllDebts(Collection<Debt> debts) {
        return null;
    }
}
