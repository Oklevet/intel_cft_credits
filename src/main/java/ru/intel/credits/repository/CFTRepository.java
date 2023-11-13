package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public interface CFTRepository {

    Set<Integer> getIDAllCredInSet();

    CopyOnWriteArrayList<Integer> getIDAllCreds();

    PrCred getCred(int id);

    Map<Integer, VidDebt> getAllVidDebts();

    Collection<VidOperDog> getAllVidOperDogs();

    Collection<FactOper> getAllFOByCreds(int id);

    Collection<PlanOper> getAllPOByCreds(int id);

    Collection<TakeInDebt> getAllTakeInDebt();
}
