package ru.intel.credits.parsing.repository;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.configuration.Connect2DB;
import ru.intel.credits.configuration.DataSource;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;

public class CFTInsert10k implements Connect2DB {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Connection initConnection(DataSource dataSource) {
        Connection connection = null;
        try {
            Class.forName(dataSource.getDriver());
            connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
        } catch (Exception e) {
            LOGGER.error("При подключении к БД ЦФТ произошла ошибка: " + e.fillInStackTrace());
        }
        return connection;
    }

    @Override
    public void closeCon(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            LOGGER.error("При закрытии подключения к БД ЦФТ произошла ошибка: " + e.fillInStackTrace());
        }
    }

    private static String getQuestMark(int cntMarks) {
        StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < cntMarks; i++) {
            sj.add("?");
        }

        return sj.toString();
    }

    private static String getAttrs(TreeMap<String, String> mapAttrs) {
        StringJoiner sj = new StringJoiner(", ");
        for (String str : mapAttrs.navigableKeySet()) {
            sj.add(str);
        }

        return sj.toString();
    }

    //добавить kind_credits, prc_jour
    public void insertParseData(String filepath, DataSource dataSourceCFT, String schemaPattern) throws Exception {
        Connection connection = initConnection(dataSourceCFT);

        TreeMap<String, String> mapAttrs = new TreeMap<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);
        DatabaseMetaData meta = connection.getMetaData();
        String[] splitFilepath = filepath.split("\\.");

        String catalog = null;
        String[] types = {"TABLE"};
        int countAttr = 1;
        int countBatch = 0;
        String tablePrefix = "Z#";

        ResultSet rsTables = meta.getTables(catalog, schemaPattern, tablePrefix +
                splitFilepath[0].toUpperCase(), types);

        while (rsTables.next()) {
            System.out.println(rsTables.getString(3));
            String tableName = rsTables.getString(3);
            String columnNamePattern = null;
            ResultSet rsColumns = meta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

            while (rsColumns.next()) {
                System.out.println(rsColumns.getString("COLUMN_NAME"));
                mapAttrs.put(rsColumns.getString("COLUMN_NAME"), rsColumns.getString("TYPE_NAME"));
            }
            break;
        }

        StringJoiner sql = new StringJoiner(" ");
        sql.add("INSERT INTO")
                .add("\"" + tablePrefix + splitFilepath[0].toUpperCase() + "\"")
                .add("(")
                .add(String.join(", ", getAttrs(mapAttrs)))
                .add(") VALUES (")
                .add(getQuestMark(mapAttrs.size()))
                .add(")");
        System.out.println(sql);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();

        try {
            PreparedStatement statement = connection.prepareStatement(String.valueOf(sql));
            while (line != null) {

                Object obj = new JSONParser().parse(line);
                String[] splitFileName = filepath.split("\\.");
                JSONObject jsonObject = (JSONObject) obj;

                HashMap<String, Object> jsonBlocks = (HashMap<String, Object>) jsonObject;

                for (String keyAttr : mapAttrs.navigableKeySet()) {
                    StatementHelper.setValue(countAttr++, statement, mapAttrs.get(keyAttr), String.valueOf(jsonBlocks.get(keyAttr)));
                }
                statement.addBatch();
                countBatch++;
                countAttr = 1;

                if (countBatch % 10000 == 0) {
                    System.out.println("countBatch = " + countBatch);
                    statement.executeBatch();
                }

                line = reader.readLine();
            }
            statement.executeBatch();
            System.out.println("countBatch all = " + countBatch);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeCon(connection);
        }
    }
}