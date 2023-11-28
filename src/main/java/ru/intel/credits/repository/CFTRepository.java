package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public interface CFTRepository {

    Set<Long> getIDAllCredInSet();

    CopyOnWriteArrayList<Long> getIDAllCreds();

    PrCred getCred(long id);

    Map<Long, VidDebt> getAllVidDebts();

    Collection<VidOperDog> getAllVidOperDogs();

    Collection<FactOper> getAllFOByCreds(long id);

    Collection<PlanOper> getAllPOByCreds(long id);

    Collection<TakeInDebt> getAllTakeInDebt();
}
