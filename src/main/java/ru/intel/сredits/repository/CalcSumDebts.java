package ru.intel.сredits.repository;

import ru.intel.сredits.model.PrCred;

public interface CalcSumDebts {

    Double calcLines(PrCred cred);

    Double calcAggregate(PrCred cred);

    Double calcComiss(PrCred cred);

    Double calcSimple(PrCred cred);

    Double calcProc(PrCred cred);
}
