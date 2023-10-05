package ru.intel.сredits.calc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.сredits.configuration.DatasourceConfiguration;
import ru.intel.сredits.model.Debt;
import ru.intel.сredits.model.PrCred;
import ru.intel.сredits.repository.Sql2oCFTRepository;
import ru.intel.сredits.repository.Sql2oRecieveDBRepository;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Properties;

public class Main {

    Sql2oCFTRepository sql2oCftRepos;

    Sql2oRecieveDBRepository sql2oRecieveDBRepository;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void calcAndTransferCreds(int batch) {

        var main = new Main();
        main.connectToCFT();
        main.connectToRecieveDB();

        var calcAllDebts = new CalcAllDebts(sql2oCftRepos);
        calcAllDebts.actualDirectories();
        var credDebt = calcAllDebts.calcAllCredsByPools(calcAllDebts, batch);

        var prCreds = new ArrayList<PrCred>();
        var debts = new ArrayList<Debt>();
        credDebt.stream().map(x -> {
            prCreds.addAll(x.getCreds());
            return debts.addAll(x.getDebts());
        });

        var insertedCreds = sql2oRecieveDBRepository.insertAllCreds(prCreds);
        var insertedDebets = sql2oRecieveDBRepository.insertAllDebts(debts, calcAllDebts.vidDebts);

        LOG.info("Кредитов вставлено: " + insertedCreds);
        LOG.info("Задолженностей insertedDebets: " + insertedCreds);
    }

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

    public static void main(String[] args) {

    }
}
