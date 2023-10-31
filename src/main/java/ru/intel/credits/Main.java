package ru.intel.credits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.intel.credits.calc.CalcAllDebts;
import ru.intel.credits.configuration.DataSource;
import java.lang.invoke.MethodHandles;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        LOG.info("Начало расчета задолженностей");

        DataSource dataSourceCFT = new DataSource(
                "org.postgresql.Driver",
                "jdbc:postgresql://127.0.0.1:5432/intel_cft_credits",
                "postgres",
                "asd1");

        DataSource dataSourceReceiver = new DataSource(
                "org.postgresql.Driver",
                "jdbc:postgresql://127.0.0.1:5432/intel_receiver",
                "postgres",
                "asd1");

        CalcAllDebts.calcAllCreds(dataSourceCFT, dataSourceReceiver, 5);

        LOG.info("Окончание расчета задолженностей");
    }
}
