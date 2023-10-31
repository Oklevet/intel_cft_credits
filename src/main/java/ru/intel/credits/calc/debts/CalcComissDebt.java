package ru.intel.credits.calc.debts;

import ru.intel.credits.model.PrCred;
import ru.intel.credits.model.VidOperDog;

import java.util.HashMap;

public class CalcComissDebt implements CalcDebt {

    /**
     * Расчет процентых задолженностей по журналу процентов.
     * Список параметров раширить, функционал написать.
     */
    @Override
    public Double calcDebt(PrCred cred, HashMap<Integer, VidOperDog> opers, int idDebt) {
        return (double) 0;
    }
}
