package ru.intel.сredits.repository;

import ru.intel.сredits.model.*;

import java.util.Collection;
import java.util.HashMap;

public interface RecieveDBRepository {

    Integer insertAllCreds(Collection<PrCred> creds);

    Integer insertAllDebts(Collection<Debt> debts);
}
