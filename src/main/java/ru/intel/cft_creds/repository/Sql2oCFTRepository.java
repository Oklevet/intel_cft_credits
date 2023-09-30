package ru.intel.cft_creds.repository;

import org.sql2o.Sql2o;

import java.time.LocalDate;

public class Sql2oCFTRepository implements CFTRepository {

    private final Sql2o sql2o;

    public Sql2oCFTRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Object findAllByDate(LocalDate date) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("select * from PR_CRED where )
        }
    }
}
