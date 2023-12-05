package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import ru.intel.credits.calc.CalcDebtsStrategy;

import java.util.HashMap;

@AllArgsConstructor
public class DebtComiss extends Debt implements CalcDebtsStrategy {

    public DebtComiss(long collectionId, long id) {
        super(collectionId, id);
    }

    @Override
    public long calcDebt(PrCred cred, HashMap<Long, VidOperDog> opers, long idDebt) {
        return 0L;
    }
}
