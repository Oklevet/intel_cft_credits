package ru.intel.credits.repository;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.configuration.Connect2DB;
import ru.intel.credits.configuration.DataSource;
import ru.intel.credits.model.*;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;

@RequiredArgsConstructor
public class SqlCFTRepository implements Connect2DB, CFTRepository {

    @NonNull
    private final DataSource dataSource;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Connection initConnection(DataSource dataSource) {
        Connection connection = null;
        try {
            Class.forName(dataSource.getDriver());
            connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
        } catch (Exception e) {
            LOG.error("При подключении к БД получателя произошла ошибка: " + e.fillInStackTrace());
        }
        return connection;
    }

    @Override
    public void closeCon(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            LOG.error("При закрытии подключения к БД получателя произошла ошибка: " + e.fillInStackTrace());
        }
    }

    @Override
    public Set<Integer> getIDAllCreds() {
        Connection connection = initConnection(dataSource);
        Set<Integer> setIds = new HashSet<>();
        try {
            ResultSet rs = connection.createStatement()
                        .executeQuery("select pr.id "
                                        + "from  intel_cft_credits.PR_CRED pr "
                                        + "where pr.STATE = 'Открыт'");
            while (rs.next()) {
                setIds.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return setIds;
    }

    @Override
    public PrCred getCred(int id) {
        Connection connection = initConnection(dataSource);
        PrCred cred = new PrCred();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("select  pr.NUM_DOG, pr.VAL, pr.LIST_PAY, pr.LIST_PLAN_PAY "
                                        + "from   intel_cft_credits.PR_CRED pr "
                                        + "where  pr.id = (?);");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            cred.setNumDog(rs.getString(1));
            cred.setVal(rs.getString(2));
            cred.setCollectionFO(rs.getInt(3));
            cred.setCollectionPO(rs.getInt(4));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return cred;
    }

    @Override
    public Map<Integer, VidDebt> getAllVidDebts() {
        Connection connection = initConnection(dataSource);
        Map<Integer, VidDebt> debts = new HashMap<Integer, VidDebt>();
        try {
            ResultSet rs = connection.createStatement()
                    .executeQuery("select id, code, debt_type from intel_cft_credits.VID_DEBT");
            while (rs.next()) {
                debts.put(rs.getInt(1),
                        new VidDebt(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return debts;
    }

    @Override
    public Collection<VidOperDog> getAllVidOperDogs() {
        Connection connection = initConnection(dataSource);
        Set<VidOperDog> opers = new HashSet<VidOperDog>();
        try {
            ResultSet rs = connection.createStatement()
                    .executeQuery("select id, code, take_debt, vid_debt, vid_debt_dt "
                                    + "from intel_cft_credits.VID_OPER_DOG");
            while (rs.next()) {
                opers.add(new VidOperDog(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), new ArrayList<>()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return opers;
    }

    @Override
    public Collection<FactOper> getAllFOByCreds(int id) {
        Connection connection = initConnection(dataSource);
        Set<FactOper> opers = new HashSet<FactOper>();
        try {
            PreparedStatement statement = connection.prepareStatement(""
                    + "select fo.SUMMA, fo.OPER, v.VID_DEBT, v.VID_DEBT_DT, fo.collection_id "
                    + "from intel_cft_credits.FACT_OPER fo, intel_cft_credits.VID_OPER_DOG v "
                    + "where fo.collection_id in ("
                    +        "select pr.LIST_PAY "
                    +        "from   intel_cft_credits.PR_CRED pr "
                    +        "where  pr.STATE = 'Открыт') "
                    + "              and pr.id = ? "
                    + "and fo.DATE < current_date + 1 "
                    + "and fo.OPER = v.id"
            );
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                opers.add(new FactOper(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                        rs.getInt(4), rs.getInt(5)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return opers;
    }

    @Override
    public Collection<PlanOper> getAllPOByCreds(int id) {
        Connection connection = initConnection(dataSource);
        Set<PlanOper> opers = new HashSet<PlanOper>();
        try {
            PreparedStatement statement = connection.prepareStatement(""
                    + "select fo.SUMMA, fo.OPER, v.VID_DEBT, v.VID_DEBT_DT, fo.collection_id "
                    + "from intel_cft_credits.PLAN_OPER fo, intel_cft_credits.VID_OPER_DOG v "
                    + "where fo.collection_id in ("
                    +        "select pr.LIST_PLAN_PAY "
                    +        "from   intel_cft_credits.PR_CRED pr "
                    +        "where  pr.STATE = 'Открыт') "
                    + "              and pr.id = ? "
                    + "and fo.DATE < current_date + 1 "
                    + "and fo.OPER = v.id"
            );
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                opers.add(new PlanOper(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                        rs.getInt(4), rs.getInt(5)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return opers;
    }

    @Override
    public Collection<TakeInDebt> getAllTakeInDebt() {
        Connection connection = initConnection(dataSource);
        Set<TakeInDebt> opers = new HashSet<TakeInDebt>();
        try {
            PreparedStatement statement = connection.prepareStatement(""
                    + "select d.debt, d.DT, d.collection_id "
                    + "from intel_cft_credits.TAKE_IN_DEBT d "
                    + "where d.collection_id in ("
                    +        "select v.TAKE_DEBT "
                    +        "from   intel_cft_credits.VID_OPER_DOG v) ");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                opers.add(new TakeInDebt(rs.getInt(1), rs.getBoolean(2), rs.getInt(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return opers;
    }
}
