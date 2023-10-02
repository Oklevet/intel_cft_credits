package ru.intel.сredits.repository;

import ru.intel.сredits.model.PrCred;

import java.util.Collection;

public interface RecieverDBRepository {
    boolean insertInCreds(Collection<PrCred> creds);

    boolean insertInDebts(Collection<PrCred> creds);
}
