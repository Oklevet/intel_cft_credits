package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.intel.credits.calc.CalcDebtsStrategy;

import java.util.HashMap;

@AllArgsConstructor
public class DebtPrc extends Debt implements CalcDebtsStrategy {

    public DebtPrc(long collectionId, long id) {
        super(collectionId, id);
    }

    @Override
    public long calcDebt(PrCred cred, HashMap<Long, VidOperDog> opers, long idDebt) {
        DebtSimple debt = new DebtSimple(cred.getCollectionDebts(), idDebt);
        return debt.calcDebt(cred, opers, idDebt);
    }
}
