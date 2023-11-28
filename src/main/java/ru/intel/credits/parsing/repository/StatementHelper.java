package ru.intel.credits.parsing.repository;

import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
public class StatementHelper {
    static PreparedStatement setValue(int index, PreparedStatement statement, String sKey, String sVal) throws SQLException, UnsupportedEncodingException {

        if (sVal == null || sVal == "" || sVal.equals("null") || sVal.equals("\"null\"")) {
            statement.setNull(index, java.sql.Types.NULL);
        } else {

            if (sKey.contains("varchar") || sKey.contains("string")) {
                statement.setString(index, sVal);
                return statement;
            }

            if (sKey.contains("int") || sKey.contains("number")) {
                statement.setLong(index, Long.parseLong(sVal));
                return statement;
            }

            if (sKey.contains("numeric")) {
                statement.setDouble(index, Double.parseDouble(sVal));
                return statement;
            }

            if (sKey.contains("date")) {
                LocalDate dateVal = LocalDate.parse(sVal, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                statement.setDate(index, Date.valueOf(dateVal));
                return statement;
            }

            if (sKey.contains("bool")) {
                statement.setBoolean(index, Boolean.parseBoolean(sVal));
            }
        }
        return statement;
    }
}
