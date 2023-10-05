package ru.intel.сredits.repository;

import ru.intel.сredits.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface CFTRepository {

    List<Integer> getIDAllCreds();

    Collection<PrCred> getAllCreds(List<Integer> listId);

    HashMap<Integer, VidDebt> getAllVidDebts();

    Collection<VidOperDog> getAllVidOperDogs();

    Collection<FactOper> getAllFOByCreds();

    Collection<PlanOper> getAllPOByCreds();

    Collection<TakeInDebt> getAllTakeInDebt();
}
