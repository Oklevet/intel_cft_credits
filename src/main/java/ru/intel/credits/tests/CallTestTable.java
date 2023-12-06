package ru.intel.credits.tests;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.intel.credits.configuration.Connect2DB;
import ru.intel.credits.configuration.DataSource;
import ru.intel.credits.model.PrCred;
import ru.intel.credits.repository.SqlCFTRepository;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CallTestTable implements Connect2DB {

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

    public List<PrCred> getCredsSql2O(List<Long> listIds) {
        List<PrCred> creds = new ArrayList<>();
        //Удалён файл конфигуратор
       /* final Map<String, String> COLUMN_MAPPING = Map.of(
                "ID", "id",
                "c_NUM_DOG", "numDog",
                "C_FT_CREDIT", "val",
                "c_LIST_PAY", "collectionFO",
                "c_LIST_PLAN_PAY", "collectionPO"
        );

        DatasourceConfiguration datasourceConfig = new DatasourceConfiguration();
        javax.sql.DataSource dataSourceConfs = datasourceConfig.connectionPool(dataSource.getUrl(),
                dataSource.getUsername(), dataSource.getPassword());
        Sql2o sql2o = datasourceConfig.databaseClient(dataSourceConfs);
        org.sql2o.Connection connectionSql = sql2o.open();

        String sql = ("select  pr.ID, pr.c_NUM_DOG, pr.C_FT_CREDIT, pr.c_LIST_PAY, pr.c_LIST_PLAN_PAY "
                + "from   \"Z#PR_CRED\" pr "
                + "where  pr.id in (:list);");
        Query query = connectionSql.createQuery(sql, true)
                .addParameter("list", listIds);
        creds = query.setColumnMappings(COLUMN_MAPPING).executeAndFetch(PrCred.class);
        creds.forEach(x -> {
            x.setListPO(new ArrayList<>());
            x.setListFO(new ArrayList<>());
        });*/
        return creds;
    }

    public List<PrCred> getCredsConnect(List<Long> listIds) {
        List<PrCred> creds = new ArrayList<>();
        Connection connection = initConnection(dataSource);

        try {
            String stmt = String.format("select  pr.c_NUM_DOG, pr.C_FT_CREDIT, pr.c_LIST_PAY, pr.c_LIST_PLAN_PAY "
                            + "from   \"Z#PR_CRED\" pr "
                            + "where  pr.id in (%s);",
                    listIds.stream()
                            .map(v -> "?")
                            .collect(Collectors.joining(", ")));
            PreparedStatement pstmt = connection.prepareStatement(stmt);
            int index = 1;

            for (Long l : listIds) {
                pstmt.setLong(index++, l);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PrCred cred = new PrCred();
                cred.setNumDog(rs.getString(1));
                cred.setVal(rs.getString(2));
                cred.setCollectionFO(rs.getLong(3));
                cred.setCollectionPO(rs.getLong(4));
                creds.add(cred);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return creds;
    }

    public List<PrCred> getCredsFull(List<Long> listIds) {
        List<PrCred> creds = new ArrayList<>();
        Connection connection = initConnection(dataSource);

        try {
            String stmt = String.format(
                            "select  pr.c_NUM_DOG, pr.C_FT_CREDIT, pr.c_LIST_PAY, pr.c_LIST_PLAN_PAY, " //creds
                                    + " po.c_SUMMA, po.c_OPER, vp.c_VID_DEBT, vp.c_VID_DEBT_DT, po.collection_id " //po
                            + "from   \"Z#PR_CRED\" pr, \"Z#PLAN_OPER\" po, \"Z#VID_OPER_DOG\" vp "
                            + "where  ppr.C_COM_STATUS = 'WORK' "
                            + "       and p.C_LIST_PLAN_PAY = po.COLLECTION_ID\n "
                            +       " and po.C_OPER = vp.ID "
                            + " union all "
                            + " select  pr.c_NUM_DOG, pr.C_FT_CREDIT, pr.c_LIST_PAY, pr.c_LIST_PLAN_PAY, " //creds
                            + " fo.c_SUMMA, fo.c_OPER, vf.c_VID_DEBT, vf.c_VID_DEBT_DT, fo.collection_id " //fo
                            + "from   \"Z#PR_CRED\" pr, \"Z#FACT_OPER\" fo, \"Z#VID_OPER_DOG\" vf "
                            + "where  ppr.C_COM_STATUS = 'WORK' "
                            + "       and p.C_LIST_PAY = fo.COLLECTION_ID\n "
                            +       " and fo.C_OPER = vp.ID "
                                    + ";");
            Statement statement = connection.prepareStatement(stmt);
            ResultSet rs = statement.executeQuery(stmt);

            while (rs.next()) {
                PrCred cred = new PrCred();
                cred.setNumDog(rs.getString(1));
                cred.setVal(rs.getString(2));
                cred.setCollectionFO(rs.getLong(3));
                cred.setCollectionPO(rs.getLong(4));
                creds.add(cred);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCon(connection);
        }
        return creds;
    }

    public static void main(String[] args) {
        DataSource dataSourceCFT = new DataSource(
                "org.postgresql.Driver",
                "jdbc:postgresql://127.0.0.1:5432/intel_cft_credits",
                "postgres",
                "asd1");

        List<PrCred> creds;
//        List<Long> ids = List.of(246792143L, 246800561L, 246871749L, 246863528L, 246865336L, 246870503L, 246875758L,
//                246878038L, 246878851L, 246855445L);
        CallTestTable callTest = new CallTestTable(dataSourceCFT);

        List<Long> ids = new ArrayList<>();
        long l = 246800561L;
        for (int i = 0; i < 20000; i++) {
            ids.add(l++);
        }

        creds = callTest.getCredsConnect(ids);
        creds = callTest.getCredsSql2O(ids);

        //creds.forEach(x -> System.out.println(x.getNumDog()));
    }

    void processStudentsByFirstName(String firstName) {
/*        Slice<Student> slice = repository.findAllByFirstName(firstName, PageRequest.of(0, BATCH_SIZE));
        List<Student> studentsInBatch = slice.getContent();

        while(slice.hasNext()) {
            slice = repository.findAllByFirstName(firstName, slice.nextPageable());
            slice.get().forEach(emailService::sendEmailToStudent);
        }*/
    }
}