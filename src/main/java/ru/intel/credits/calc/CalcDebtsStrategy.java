package ru.intel.credits.calc;

import ru.intel.credits.model.PrCred;
import ru.intel.credits.model.VidOperDog;

import java.util.HashMap;

public interface CalcDebtsStrategy {
    public double calcDebt(PrCred cred, HashMap<Long, VidOperDog> opers, long idDebt);
}
