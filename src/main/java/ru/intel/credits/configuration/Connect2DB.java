package ru.intel.credits.configuration;

import java.sql.Connection;

public interface Connect2DB {
    Connection initConnection(DataSource dataSource);

    void closeCon(Connection connection);
}
