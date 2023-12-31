package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public interface RecieveDBRepository {

    void insertAllCreds(Collection<PrCred> creds);

    void insertAllDebts(Collection<Debt> debts, HashMap<Long, VidDebt> dirDebts);

    AtomicLong getSequence();
}