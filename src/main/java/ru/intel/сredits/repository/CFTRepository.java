package ru.intel.сredits.repository;

import ru.intel.сredits.model.*;

import java.util.Collection;
import java.util.HashMap;

public interface CFTRepository {

    Collection<PrCred> getAllCreds();

    HashMap<Integer, VidDebt> getAllVidDebts();

    Collection<VidOperDog> getAllVidOperDogs();

    Collection<FactOper> getAllFOByCreds();

    Collection<PlanOper> getAllPOByCreds();

    Collection<TakeInDebt> getAllTakeInDebt();
}
