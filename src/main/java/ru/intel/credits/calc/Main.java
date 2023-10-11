package ru.intel.credits.calc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.configuration.DatasourceConfiguration;
import ru.intel.credits.model.Debt;
import ru.intel.credits.model.PrCred;
import ru.intel.credits.repository.Sql2oCFTRepository;
import ru.intel.credits.repository.Sql2oRecieveDBRepository;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Properties;

public class Main {

    Sql2oCFTRepository sql2oCftRepos;

    Sql2oRecieveDBRepository sql2oRecieveDBRepository;
    static Main main = new Main();

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void calcAndTransferCreds(int batch) {

        main.connectToCFT();
        main.connectToRecieveDB();

        var calcAllDebts = new CalcAllDebts(sql2oCftRepos, sql2oRecieveDBRepository);
        calcAllDebts.actualDirectories();
        var credDebt = calcAllDebts.calcAllCredsByPools(calcAllDebts, batch);

        var prCreds = new ArrayList<PrCred>();
        var debts = new ArrayList<Debt>();
        credDebt.forEach(x -> {
            prCreds.addAll(x.getCreds());
            debts.addAll(x.getDebts());
        });

        sql2oRecieveDBRepository.insertAllCreds(prCreds);
        sql2oRecieveDBRepository.insertAllDebts(debts, calcAllDebts.vidDebts);

        LOG.error("Расчет задолженностей произведен");
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
        main.calcAndTransferCreds(5);
    }
}
