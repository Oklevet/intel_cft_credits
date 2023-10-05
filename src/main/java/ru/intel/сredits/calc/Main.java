package ru.intel.сredits.calc;

import ru.intel.сredits.configuration.DatasourceConfiguration;
import ru.intel.сredits.repository.Sql2oCFTRepository;
import ru.intel.сredits.repository.Sql2oRecieveDBRepository;

import java.io.IOException;
import java.util.Properties;

public class Main {
    Sql2oCFTRepository sql2oCftRepos;
    Sql2oRecieveDBRepository sql2oRecieveDBRepository;

    public void connectToCFT() {

        var properties = new Properties();

        try (var inputStream = Sql2oCFTRepository.class.getClassLoader()
                .getResourceAsStream("connectionCFT.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        this.sql2oCftRepos = new Sql2oCFTRepository(sql2o);
    }

    public void connectToRecieveDB() {

        var properties = new Properties();

        try (var inputStream = Sql2oRecieveDBRepository.class.getClassLoader()
                .getResourceAsStream("connectionRecieveDB.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        this.sql2oRecieveDBRepository = new Sql2oRecieveDBRepository(sql2o);
    }

    public void calcAndTransferCreds() {

        var main = new Main();
        main.connectToCFT();
        main.connectToRecieveDB();

        var calcAllDebts = new CalcAllDebts(sql2oCftRepos);
        calcAllDebts.actualDirectories();

    }

    public static void main(String[] args) {

    }
}
