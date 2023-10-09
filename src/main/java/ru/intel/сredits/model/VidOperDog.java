package ru.intel.сredits.model;

import java.util.*;

public class VidOperDog {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "id", "id",
            "code", "code",
            "take_debt", "collectionDebts",
            "vid_debt", "vidDebt",
            "vid_debt_dt", "vidDebtDt"
    );

    private int id;
    private String code;
    private int collectionDebts;
    private int vidDebt;
    private int vidDebtDt;
    private ArrayList<TakeInDebt> debets;

    public VidOperDog(int id, String code, int collectionDebts, int vidDebt, int vidDebtDt,
                      ArrayList<TakeInDebt> debets) {
        this.id = id;
        this.code = code;
        this.collectionDebts = collectionDebts;
        this.vidDebt = vidDebt;
        this.vidDebtDt = vidDebtDt;
        this.debets = debets;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setVidDebt(int vidDebt) {
        this.vidDebt = vidDebt;
    }

    public void setVidDebtDt(int vidDebtDt) {
        this.vidDebtDt = vidDebtDt;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public int getVidDebt() {
        return vidDebt;
    }

    public int getVidDebtDt() {
        return vidDebtDt;
    }

    public int getCollectionDebts() {
        return collectionDebts;
    }

    public void setCollectionDebts(int collectionDebts) {
        this.collectionDebts = collectionDebts;
    }

    public ArrayList<TakeInDebt> getDebets() {
        return debets;
    }

    public void setDebets(ArrayList<TakeInDebt> debets) {
        this.debets = debets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VidOperDog that)) return false;
        return id == that.id && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ");
        debets.forEach(x -> sj.add(x.toString()));
        return "VidOperDog{" +
                "code='" + code + '\'' +
                ", debets=" + sj +
                '}';
    }
}
