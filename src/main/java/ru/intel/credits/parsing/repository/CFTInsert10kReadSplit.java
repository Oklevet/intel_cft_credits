package ru.intel.credits.parsing.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.configuration.Connect2DB;
import ru.intel.credits.configuration.DataSource;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.*;

public class CFTInsert10kReadSplit implements Connect2DB {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Connection initConnection(DataSource dataSource) {
        Connection connection = null;
        try {
            Class.forName(dataSource.getDriver());
            connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(),
                    dataSource.getPassword());
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

    public void insertParseData(String filepath, DataSource dataSourceCFT, String schemaPattern)
            throws IOException, SQLException {
        Connection connection = initConnection(dataSourceCFT);
        String[] splitFilepath = filepath.split("\\.");
        int countAttr = 1;
        int countBatch = 0;

        TreeMap<String, String> mapAttrs = new TreeMap<>();
        HashMap<String, String> mapLineAttrs = new HashMap<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);
        DatabaseMetaData meta = connection.getMetaData();

        String line = null;
        String catalog = null;
        String[] types = {"TABLE"};

        ResultSet rsTables = meta.getTables(catalog, schemaPattern, splitFilepath[0], types);

        while (rsTables.next()) {
            String tableName = rsTables.getString(3);
            String columnNamePattern = null;
            ResultSet rsColumns = meta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

            while (rsColumns.next()) {
                mapAttrs.put(rsColumns.getString("COLUMN_NAME"), rsColumns.getString("TYPE_NAME"));
            }
            break;
        }

        StringJoiner sql = new StringJoiner(" ");
        sql.add("INSERT INTO")
                .add(splitFilepath[0])
                .add("(")
                .add(String.join(", ", getAttrs(mapAttrs)))
                .add(") VALUES (")
                .add(getQuestMark(mapAttrs.size()))
                .add(")");
        System.out.println(sql);

        if (inputStream == null) {
            throw new RuntimeException("Некорректно указан путь к файлу json с тестовыми данными " + filepath);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, String.valueOf(Locale.UNICODE_LOCALE_EXTENSION)));
        line = reader.readLine();

        try {
            PreparedStatement statement = connection.prepareStatement(String.valueOf(sql));

            while (line != null) {

                String[] splitAttrs = line.split(";");
                splitAttrs[0] = splitAttrs[0].replace("{", "");
                splitAttrs[splitAttrs.length - 1] = splitAttrs[splitAttrs.length - 1].replace("}", "");

                Arrays.stream(splitAttrs).forEach(x -> {
                    String[] splitStr = x.split(":");
                    System.out.println(x);
                    mapLineAttrs.put(splitStr[0].replace("\"", ""),
                            splitStr[1].replace("\"", ""));
                });

                for (String keyAttr : mapAttrs.navigableKeySet()) {
                    StatementHelper.setValue(countAttr++, statement, mapAttrs.get(keyAttr), mapLineAttrs.get(keyAttr));
                }
                statement.addBatch();
                countBatch++;
                countAttr = 1;
                line = reader.readLine();

                if (countBatch % 1000 == 0) {
                    System.out.println("countBatch = " + countBatch);
                    statement.executeBatch();
                }
            }
            System.out.println("countBatch = " + countBatch);
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeCon(connection);
        }
    }
}
