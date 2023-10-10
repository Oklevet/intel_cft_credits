package ru.intel.сredits.repository;

import ru.intel.сredits.model.*;

import java.util.Collection;
import java.util.HashMap;

public class FillCollections implements FillCollectionOfModel {

    public PrCred getCredByIdFo(Collection<PrCred> creds, int collection) {
        for (PrCred cred : creds) {
            if (cred.getCollectionFO() == collection) {
                return cred;
            }
        }
        return null;
    }

    public PrCred getCredByIdPo(Collection<PrCred> creds, int collection) {
        for (PrCred cred : creds) {
            if (cred.getCollectionPO() == collection) {
                return cred;
            }
        }
        return null;
    }

    public Collection<PrCred> fillFOInCreds(Collection<PrCred> creds, Collection<FactOper> fo) {
        fo.forEach(x-> getCredByIdFo(creds, x.getCollectionId()).getListFO().add(x));
        return creds;
    }

    public Collection<PrCred> fillPOInCreds(Collection<PrCred> creds, Collection<PlanOper> fo) {
        fo.forEach(x-> getCredByIdPo(creds, x.getCollectionId()).getListPO().add(x));
        return creds;
    }

    public VidOperDog getVidOperByIdDebets(Collection<VidOperDog> opers, int collection) {
        for (VidOperDog oper : opers) {
            if (oper.getCollectionDebts() == collection) {
                return oper;
            }
        }
        return null;
    }

    public HashMap<Integer, VidOperDog> fillOperDebets(Collection<VidOperDog> opers, Collection<TakeInDebt> debets) {
        HashMap<Integer, VidOperDog> opersMap = new HashMap<>();

        debets.stream()
                .filter(x-> getVidOperByIdDebets(opers, x.getCollectionId()) != null)
                .forEach(t-> getVidOperByIdDebets(opers, t.getCollectionId()).getDebets().add(t));

        opers.forEach(x -> opersMap.put(x.getId(), x));
        return opersMap;
    }
}
