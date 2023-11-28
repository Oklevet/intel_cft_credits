package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FactOper {

    @EqualsAndHashCode.Include
    private LocalDate date;
    private double summa;
    @EqualsAndHashCode.Include
    private long oper;
    private long vidDebt;
    private long vidDebtDt;
    @EqualsAndHashCode.Include
    private long collectionId;

    public FactOper(double summa, long oper, long vidDebt, long vidDebtDt, long collectionId) {
        this.summa = summa;
        this.oper = oper;
        this.vidDebt = vidDebt;
        this.vidDebtDt = vidDebtDt;
        this.collectionId = collectionId;
    }
}
