package ru.intel.сredits.repository;

import org.sql2o.ResultSetHandlerFactoryBuilder;
import org.sql2o.Sql2o;
import ru.intel.сredits.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Sql2oCFTRepository implements CFTRepository {

    private final Sql2o sql2o;

    public Sql2oCFTRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public List<Integer> getIDAllCreds() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select pr.id " +
                    "from  intel_cft_credits.PR_CRED pr " +
                    "where pr.STATE = 'Открыт'");
            return query.executeScalarList(Integer.class);
        }
    }

    @Override
    public Collection<PrCred> getAllCreds(List<Integer> listId) {
        try (var connection = sql2o.open()) {
            var sql = "select  pr.NUM_DOG, pr.VAL, pr.LIST_PAY, pr.LIST_PLAN_PAY " +
                    "from   intel_cft_credits.PR_CRED pr " +
                    "where  pr.id in (:list);";
            var query = connection.createQuery(sql).addParameter("list", listId);
            return query.setColumnMappings(PrCred.COLUMN_MAPPING).executeAndFetch(PrCred.class);
        }
    }

    @Override
    public HashMap<Integer, VidDebt> getAllVidDebts() {
        var debts = new HashMap<Integer, VidDebt>();
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("select id, code from intel_cft_credits.VID_DEBT");
            var debtsList = query.setColumnMappings(VidDebt.COLUMN_MAPPING)
                    .executeAndFetch(VidDebt.class);
            debtsList.forEach(x -> debts.put(x.getId(), x));
            return debts;
        }
    }

    @Override
    public Collection<VidOperDog> getAllVidOperDogs() {
        //HashMap<Integer, VidOperDog> opers = new HashMap<>();
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("select id, code, take_debt, vid_debt, vid_debt_dt " +
                    "from intel_cft_credits.VID_OPER_DOG");
            var opers = query.setColumnMappings(VidOperDog.COLUMN_MAPPING)
                                            .executeAndFetch(VidOperDog.class);
            opers.forEach(x -> x.setDebets(new ArrayList<>()));
            return opers;
        }
    }

    @Override
    public Collection<FactOper> getAllFOByCreds() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select fo.DATE, fo.SUMMA, fo.OPER, fo.VID_DEBT, fo.VID_DEBT_DT, fo.collection_id " +
                    "from intel_cft_credits.FACT_OPER fo " +
                    "where fo.collection_id in (" +
                            "select pr.id " +
                            "from   intel_cft_credits.PR_CRED pr " +
                            "where  pr.STATE = 'Открыт') " +
                    "and fo.DATE < sysdate + 1");
            return query.setColumnMappings(FactOper.COLUMN_MAPPING).executeAndFetch(FactOper.class);
        }
    }

    @Override
    public Collection<PlanOper> getAllPOByCreds() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select po.DATE, po.SUMMA, po.OPER, po.VID_DEBT, po.VID_DEBT_DT, po.collection_id " +
                    "from intel_cft_credits.PLAN_OPER po " +
                    "where po.collection_id in (" +
                            "select pr.id " +
                            "from   intel_cft_credits.PR_CRED pr " +
                            "where  pr.STATE = 'Открыт') " +
                    "and po.DATE < sysdate + 1");
            return query.setColumnMappings(PlanOper.COLUMN_MAPPING).executeAndFetch(PlanOper.class);
        }
    }

    @Override
    public Collection<TakeInDebt> getAllTakeInDebt() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("" +
                    "select d.debt, d.DT, d.collection_id " +
                    "from intel_cft_credits.TAKE_IN_DEBT d " +
                    "where d.collection_id in (" +
                            "select v.TAKE_DEBT " +
                            "from   intel_cft_credits.VID_OPER_DOG v) ");
            return query.setColumnMappings(TakeInDebt.COLUMN_MAPPING)
                    .executeAndFetch(TakeInDebt.class);
        }
    }
}
