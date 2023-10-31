package ru.intel.credits.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlanOper {

    @EqualsAndHashCode.Include
    private LocalDate date;
    private double summa;

    @EqualsAndHashCode.Include
    private int oper;
    private int vidDebt;
    private int vidDebtDt;

    @EqualsAndHashCode.Include
    private int collectionId;

    public PlanOper(double summa, int oper, int vidDebt, int vidDebtDt, int collectionId) {
        this.summa = summa;
        this.oper = oper;
        this.vidDebt = vidDebt;
        this.vidDebtDt = vidDebtDt;
        this.collectionId = collectionId;
    }
}
