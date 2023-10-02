package ru.intel.сredits.repository;

import ru.intel.сredits.model.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

public interface CFTRepository {

    Collection<PrCred> getAllCreds();

    HashMap<Integer, String> getAllVidDebts();

    HashMap<Integer, VidOperDog> getAllVidOperDogs();

    Collection<FactOper> getAllFOByCreds();

    Collection<PlanOper> getAllPOByCreds();

    Collection<TakeInDebt> getAllTakeInDebt();
}
