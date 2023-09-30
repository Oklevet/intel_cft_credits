package ru.intel.cft_creds.repository;

import java.time.LocalDate;

public interface CFTRepository {
    Object findAllByDate(LocalDate date);
}
