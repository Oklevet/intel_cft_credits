package ru.intel.сredits.calc.debtsByCred;

import ru.intel.сredits.calc.CalcAllDebts;
import ru.intel.сredits.model.PrCred;
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
        return cred.getListFO().stream()
                .filter(x -> opers.get(x.getOper()).getDebets().contains(idDebt))
                .map(x -> opers.get(x.getOper()).getDebets().stream()
                                                            .filter(d -> d.getDebt() == idDebt)
                                                            .findFirst()
                                                            .get()
                                                            .isDT() ? x.getSumma() * -1 : x.getSumma())
                .mapToDouble(a -> a)
                .sum();
    };
}
