package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.Collection;
import java.util.HashMap;

public interface FillCollectionOfModel {

    VidOperDog getVidOperByIdDebets(Collection<VidOperDog> opers, int collection);

    HashMap<Integer, VidOperDog> fillOperDebets(Collection<VidOperDog> opers, Collection<TakeInDebt> debets);
}
