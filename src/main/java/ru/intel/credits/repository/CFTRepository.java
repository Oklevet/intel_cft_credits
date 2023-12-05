package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public interface CFTRepository {

    Set<Long> getIDAllCredInSet();

    CopyOnWriteArrayList<Long> getIDAllCreds();

    PrCred getCred(long id);

    List<PrCred> getCreds(List<Long> listIds);

    Map<Long, VidDebt> getAllVidDebts();

    Collection<VidOperDog> getAllVidOperDogs();

    Collection<FactOper> getAllFOByCred(long id);

    Collection<PlanOper> getAllPOByCred(long id);

    HashMap<Long, List<FactOper>> getAllFOByCreds(List<Long> ids);

    HashMap<Long, List<PlanOper>> getAllPOByCreds(List<Long> ids);

    Collection<TakeInDebt> getAllTakeInDebt();
}
