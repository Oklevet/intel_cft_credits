package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface FillCollectionOfModel {

    VidOperDog getVidOperByIdDebets(Collection<VidOperDog> opers, long collection);

    HashMap<Long, VidOperDog> fillOperDebets(Collection<VidOperDog> opers, Collection<TakeInDebt> debets);

    List<PrCred> fillFoPoDebtInCreds(List<PrCred> creds, HashMap<Long, List<FactOper>> foOpers,
                                     HashMap<Long, List<PlanOper>> poOpers, long seqId);
}
