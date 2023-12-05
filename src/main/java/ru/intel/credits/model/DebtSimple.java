package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import ru.intel.credits.calc.CalcDebtsStrategy;

import java.util.HashMap;

@AllArgsConstructor
public class DebtSimple extends Debt implements CalcDebtsStrategy {

    public DebtSimple(long collectionId, long id) {
        super(collectionId, id);
    }

    /**
     * Расчет простых задолженностей. Необходим и для расчета процентных задолженностей.
     * @param cred - Кредит рассчитываемой задолженности
     * @param opers - Мапа со списком видов операций договоров
     * @param idDebt - ID задолженности
     * @return - сумма задолженности
     */
    @Override
    public long calcDebt(PrCred cred, HashMap<Long, VidOperDog> opers, long idDebt) {
        long summa = 0;
        for (FactOper fo : cred.getListFO()) {
            for (TakeInDebt debet : opers.get(fo.getOper()).getDebets()) {
                if (debet.getDebt() == idDebt) {
                    summa += debet.isDt() ? fo.getSumma() * -1 : fo.getSumma();
                }
            }
        }
        return summa;
    }
}
