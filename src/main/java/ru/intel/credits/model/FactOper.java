package ru.intel.credits.model;

import java.time.LocalDate;
import java.util.Objects;

public class FactOper {
    LocalDate date;
    float summa;
    int oper;
    int vidDebt;
    int vidDebtDt;

    public FactOper(LocalDate date, float summa, int oper, int vidDebt, int vidDebtDt) {
        this.date = date;
        this.summa = summa;
        this.oper = oper;
        this.vidDebt = vidDebt;
        this.vidDebtDt = vidDebtDt;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getSumma() {
        return summa;
    }

    public void setSumma(float summa) {
        this.summa = summa;
    }

    public int getOper() {
        return oper;
    }

    public void setOper(int oper) {
        this.oper = oper;
    }

    public int getVidDebt() {
        return vidDebt;
    }

    public void setVidDebt(int vidDebt) {
        this.vidDebt = vidDebt;
    }

    public int getVidDebtDt() {
        return vidDebtDt;
    }

    public void setVidDebtDt(int vidDebtDt) {
        this.vidDebtDt = vidDebtDt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FactOper factOper)) return false;
        return Float.compare(factOper.summa, summa) == 0 && oper == factOper.oper && Objects.equals(date, factOper.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, summa, oper);
    }
}
