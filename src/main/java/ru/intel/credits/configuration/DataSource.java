package ru.intel.credits.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DataSource {
    private String driver;
    private String url;
    private String username;
    private String password;
}