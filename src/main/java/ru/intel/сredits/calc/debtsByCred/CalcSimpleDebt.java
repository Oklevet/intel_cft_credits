package ru.intel.сredits.calc.debtsByCred;

import ru.intel.сredits.model.FactOper;
import ru.intel.сredits.model.PrCred;
import ru.intel.сredits.model.TakeInDebt;
import ru.intel.сredits.model.VidOperDog;

import java.util.HashMap;

public class CalcSimpleDebt {

    /**
     * Расчет простых задолженностей. Необходим и для расчета процентных задолженностей.
     * @param cred - Кредит рассчитываемой задолженности
     * @param opers - Мапа со списком видов операций договоров
     * @param idDebt - ID задолженности
     * @return - сумма задолженности
     */
    public Double calcSimpleDebt(PrCred cred, HashMap<Integer, VidOperDog> opers, int idDebt) {
        double summa = 0;
        for (FactOper fo : cred.getListFO()) {
            for (TakeInDebt debet : opers.get(fo.getOper()).getDebets()) {
                if (debet.getDebt() == idDebt) {
                    summa += debet.isDT() ? fo.getSumma() * -1 : fo.getSumma();
                }
            }
        }
        return summa;
    }
}
