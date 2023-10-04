package ru.intel.сredits.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class PlanOper {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "date", "date",
            "summa", "summa",
            "oper", "oper",
            "vidDebt", "vidDebt",
            "vidDebtDt", "vidDebtDt",
            "collectionId", "collectionId"
    );

    private LocalDate date;
    private float summa;
    private int oper;
    private int vidDebt;
    private int vidDebtDt;
    private int collectionId;

    public PlanOper(LocalDate date, float summa, int oper, int vidDebt, int vidDebtDt, int collectionId) {
        this.date = date;
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

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanOper planOper)) return false;
        return Float.compare(planOper.summa, summa) == 0 && oper == planOper.oper && Objects.equals(date, planOper.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, summa, oper);
    }
}
