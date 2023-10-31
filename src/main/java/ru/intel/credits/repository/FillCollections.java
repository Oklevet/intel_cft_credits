package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.Collection;
import java.util.HashMap;

public class FillCollections implements FillCollectionOfModel {

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
                .filter(x -> getVidOperByIdDebets(opers, x.getCollectionId()) != null)
                .forEach(t -> getVidOperByIdDebets(opers, t.getCollectionId()).getDebets().add(t));

        opers.forEach(x -> opersMap.put(x.getId(), x));
        return opersMap;
    }
}
