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
    private long oper;
    private long vidDebt;
    private long vidDebtDt;

    @EqualsAndHashCode.Include
    private long collectionId;

    public PlanOper(double summa, long oper, long vidDebt, long vidDebtDt, long collectionId) {
        this.summa = summa;
        this.oper = oper;
        this.vidDebt = vidDebt;
        this.vidDebtDt = vidDebtDt;
        this.collectionId = collectionId;
    }
}
