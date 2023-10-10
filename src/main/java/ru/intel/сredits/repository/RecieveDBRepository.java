package ru.intel.сredits.repository;

import ru.intel.сredits.model.*;

import java.util.Collection;
import java.util.HashMap;

public interface RecieveDBRepository {

    void insertAllCreds(Collection<PrCred> creds);

    void insertAllDebts(Collection<Debt> debts, HashMap<Integer, VidDebt> dirDebts);

    Integer getSequence(int count);
}
