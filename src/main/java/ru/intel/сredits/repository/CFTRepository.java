package ru.intel.сredits.repository;

import java.time.LocalDate;

public interface CFTRepository {
    Object findAllByDate(LocalDate date);
}
