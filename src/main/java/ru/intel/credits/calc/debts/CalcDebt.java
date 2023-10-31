package ru.intel.credits.calc.debts;

import ru.intel.credits.model.PrCred;
import ru.intel.credits.model.VidOperDog;

import java.util.HashMap;

public interface CalcDebt {

    Double calcDebt(PrCred cred, HashMap<Integer, VidOperDog> opers, int idDebt);
}
