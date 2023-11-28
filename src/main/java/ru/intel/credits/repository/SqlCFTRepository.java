package ru.intel.credits.repository;

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
import java.util.concurrent.CopyOnWriteArrayList;

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
            connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(),
                    dataSource.getPassword());
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
    public Set<Long> getIDAllCredInSet() {
        Connection connection = initConnection(dataSource);
        Set<Long> setIds = new HashSet<>();
        try {
            ResultSet rs = connection.createStatement()
                        .executeQuery("select pr.id "
                                        + "from  \"Z#PR_CRED\" pr "
                                        + "where pr.C_COM_STATUS = 'WORK'");
            while (rs.next()) {
                setIds.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return setIds;
    }

    @Override
    public CopyOnWriteArrayList<Long> getIDAllCreds() {
        Connection connection = initConnection(dataSource);
        CopyOnWriteArrayList<Long> listIds = new CopyOnWriteArrayList<>();
        try {
            ResultSet rs = connection.createStatement()
                    .executeQuery("select pr.id "
                            + "from  \"Z#PR_CRED\" pr "
                            + "where pr.C_COM_STATUS = 'WORK'");
            while (rs.next()) {
                listIds.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return listIds;
    }

    @Override
    public PrCred getCred(long id) {
        Connection connection = initConnection(dataSource);
        PrCred cred = new PrCred();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("select  pr.c_NUM_DOG, pr.C_FT_CREDIT, pr.c_LIST_PAY, pr.c_LIST_PLAN_PAY "
                                        + "from   \"Z#PR_CRED\" pr "
                                        + "where  pr.id = (?);");
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            rs.next();

            cred.setNumDog(rs.getString(1));
            cred.setVal(rs.getString(2));
            cred.setCollectionFO(rs.getLong(3));
            cred.setCollectionPO(rs.getLong(4));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return cred;
    }

    @Override
    public Map<Long, VidDebt> getAllVidDebts() {
        Connection connection = initConnection(dataSource);
        Map<Long, VidDebt> debts = new HashMap<Long, VidDebt>();
        try {
            ResultSet rs = connection.createStatement()
                    .executeQuery("select id, c_code, c_debt_type from \"Z#VID_DEBT\"");
            while (rs.next()) {
                debts.put(rs.getLong(1),
                        new VidDebt(rs.getLong(1), rs.getString(2), rs.getString(3)));
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
                    .executeQuery("select id, c_code, c_take_debt, c_vid_debt, c_vid_debt_dt "
                                    + "from \"Z#VID_OPER_DOG\"");
            while (rs.next()) {
                opers.add(new VidOperDog(rs.getLong(1), rs.getString(2), rs.getLong(3),
                        rs.getLong(4), rs.getLong(5), new ArrayList<>()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return opers;
    }

    @Override
    public List<FactOper> getAllFOByCreds(long id) {
        Connection connection = initConnection(dataSource);
        List<FactOper> opers = new ArrayList<>(List.of());
        //LOG.debug("ID = " + id);
        try {
            PreparedStatement statement = connection.prepareStatement(""
                    + "select fo.c_SUMMA, fo.c_OPER, v.c_VID_DEBT, v.c_VID_DEBT_DT, fo.collection_id "
                    + "from \"Z#FACT_OPER\" fo, \"Z#VID_OPER_DOG\" v "
                    + "where fo.collection_id in ("
                    +        "select pr.c_LIST_PAY "
                    +        "from   \"Z#PR_CRED\" pr "
                    +        "where  pr.id = ?) "
                    + "and fo.c_DATE < current_date + 1 "
                    + "and fo.c_OPER = v.id"
            );
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                opers.add(new FactOper(rs.getDouble(1), rs.getLong(2), rs.getLong(3),
                        rs.getLong(4), rs.getLong(5)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return opers;
    }

    @Override
    public List<PlanOper> getAllPOByCreds(long id) {
        Connection connection = initConnection(dataSource);
        List<PlanOper> opers = new ArrayList<>(List.of());
        try {
            PreparedStatement statement = connection.prepareStatement(""
                    + "select fo.c_SUMMA, fo.c_OPER, v.c_VID_DEBT, v.c_VID_DEBT_DT, fo.collection_id "
                    + "from \"Z#PLAN_OPER\" fo, \"Z#VID_OPER_DOG\" v "
                    + "where fo.collection_id in ("
                    +        "select pr.c_LIST_PLAN_PAY "
                    +        "from   \"Z#PR_CRED\" pr "
                    +        "where  pr.C_COM_STATUS = 'WORK' "
                    + "              and pr.id = ?) "
                    + "and fo.c_DATE < current_date + 1 "
                    + "and fo.c_OPER = v.id"
            );
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                opers.add(new PlanOper(rs.getDouble(1), rs.getLong(2), rs.getLong(3),
                        rs.getLong(4), rs.getLong(5)));
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
                    + "select d.c_debt, d.c_DT, d.collection_id "
                    + "from \"Z#TAKE_IN_DEBT\" d "
                    + "where d.collection_id in ("
                    +        "select v.c_TAKE_DEBT "
                    +        "from   \"Z#VID_OPER_DOG\" v) ");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                opers.add(new TakeInDebt(rs.getLong(1), rs.getBoolean(2),
                        rs.getLong(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return opers;
    }
}
