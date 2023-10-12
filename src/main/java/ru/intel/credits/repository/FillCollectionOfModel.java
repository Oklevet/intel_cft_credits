package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.Collection;
import java.util.HashMap;

public interface FillCollectionOfModel {
    PrCred getCredByIdFo(Collection<PrCred> creds, int collection);

    PrCred getCredByIdPo(Collection<PrCred> creds, int collection);

    Collection<PrCred> fillFOInCreds(Collection<PrCred> creds, Collection<FactOper> fo);

    Collection<PrCred> fillPOInCreds(Collection<PrCred> creds, Collection<PlanOper> fo);

    VidOperDog getVidOperByIdDebets(Collection<VidOperDog> opers, int collection);

    HashMap<Integer, VidOperDog> fillOperDebets(Collection<VidOperDog> opers, Collection<TakeInDebt> debets);
}