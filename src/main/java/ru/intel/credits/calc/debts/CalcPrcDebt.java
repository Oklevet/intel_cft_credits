package ru.intel.credits.calc.debts;

import ru.intel.credits.model.PrCred;
import ru.intel.credits.model.VidOperDog;

import java.util.HashMap;

public class CalcPrcDebt implements CalcDebt {

    CalcSimpleDebt simpleDebt;

    /**
     * Расчет процентых задолженностей по журналу процентов.
     * Список параметров раширить, функционал написать.
     */
    @Override
    public Double calcDebt(PrCred cred, HashMap<Integer, VidOperDog> opers, int idDebt) {
        simpleDebt = new CalcSimpleDebt();
        return (double) 0 + simpleDebt.calcDebt(cred, opers, idDebt);
    }
}
