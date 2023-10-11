package ru.intel.credits.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class FactOper {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "summa", "summa",
            "oper", "oper",
            "vid_debt", "vidDebt",
            "vid_debt_dt", "vidDebtDt",
            "collection_id", "collectionId"
    );

    private LocalDate date;
    private double summa;
    private int oper;
    private int vidDebt;
    private int vidDebtDt;
    private int collectionId;

    public FactOper(double summa, int oper, int vidDebt, int vidDebtDt, int collectionId) {
        this.summa = summa;
        this.oper = oper;
        this.vidDebt = vidDebt;
        this.vidDebtDt = vidDebtDt;
        this.collectionId = collectionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getSumma() {
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

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FactOper factOper)) {
            return false;
        }
        return Double.compare(factOper.summa, summa) == 0 && oper == factOper.oper && Objects.equals(date, factOper.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, summa, oper);
    }
}
