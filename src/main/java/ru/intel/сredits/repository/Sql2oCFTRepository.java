package ru.intel.сredits.repository;

import org.sql2o.Sql2o;
import ru.intel.сredits.model.*;

import java.util.Collection;
import java.util.HashMap;

public class Sql2oCFTRepository implements CFTRepository {

    private final Sql2o sql2o;

    public Sql2oCFTRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Collection<PrCred> getAllCreds() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select pr.NUM_DOG, pr.VAL, pr.LIST_PAY, pr.LIST_PLAN_PAY " +
                    "from  PR_CRED pr " +
                    "where pr.[STATE] = 'Открыт'");
            return query.setColumnMappings(PrCred.COLUMN_MAPPING).executeAndFetch(PrCred.class);
        }
    }

    @Override
    public HashMap<Integer, VidDebt> getAllVidDebts() {
        var debts = new HashMap<Integer, VidDebt>();
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("select id, code from VID_DEBT");
            query.setColumnMappings(VidDebt.COLUMN_MAPPING)
                    .executeAndFetch(VidDebt.class)
                    .stream()
                    .map(x -> debts.putIfAbsent(x.getId(), x));
            return debts;
        }
    }

    @Override
    public Collection<VidOperDog> getAllVidOperDogs() {
        HashMap<Integer, VidOperDog> opers = new HashMap<>();
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("select id, code, take_debt, vid_debt, vid_debt_dt " +
                    "from VID_OPER_DOG");
            return query.setColumnMappings(VidOperDog.COLUMN_MAPPING).executeAndFetch(VidOperDog.class);
        }
    }

    @Override
    public Collection<FactOper> getAllFOByCreds() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select fo.DATE, fo.SUMMA, fo.OPER, fo.VID_DEBT, fo.VID_DEBT_DT, fo.collection_id " +
                    "from FACT_OPER fo " +
                    "where fo.collection_id in (" +
                            "select pr.id " +
                            "from  PR_CRED pr " +
                            "where pr.[STATE] = 'Открыт') " +
                    "and fo.[DATE] < sysdate + 1");
            return query.setColumnMappings(FactOper.COLUMN_MAPPING).executeAndFetch(FactOper.class);
        }
    }

    @Override
    public Collection<PlanOper> getAllPOByCreds() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select po.DATE, po.SUMMA, po.OPER, po.VID_DEBT, po.VID_DEBT_DT, po.collection_id " +
                    "from PLAN_OPER po " +
                    "where po.collection_id in (" +
                            "select pr.id " +
                            "from  PR_CRED pr " +
                            "where pr.[STATE] = 'Открыт') " +
                    "and po.[DATE] < sysdate + 1");
            return query.setColumnMappings(PlanOper.COLUMN_MAPPING).executeAndFetch(PlanOper.class);
        }
    }

    @Override
    public Collection<TakeInDebt> getAllTakeInDebt() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select d.debt, d.DT, d.collectionId " +
                    "from TAKE_IN_DEBT d " +
                    "where po.collection_id in (" +
                            "select v.id " +
                            "from  VID_OPER_DOG v) ");
            return query.setColumnMappings(TakeInDebt.COLUMN_MAPPING).executeAndFetch(TakeInDebt.class);
        }
    }
}
