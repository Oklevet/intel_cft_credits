package ru.intel.credits.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class VidOperDog {
    int id;
    String code;
    int vidDebt;
    int vidDebtDt;
    ArrayList<HashMap<Integer, String>> takeDebt;

    public VidOperDog(int id, String code, int vidDebt, int vidDebtDt, ArrayList<HashMap<Integer, String>> takeDebt) {
        this.id = id;
        this.code = code;
        this.vidDebt = vidDebt;
        this.vidDebtDt = vidDebtDt;
        this.takeDebt = takeDebt;
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

    public void setTakeDebt(ArrayList<HashMap<Integer, String>> takeDebt) {
        this.takeDebt = takeDebt;
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

    public ArrayList<HashMap<Integer, String>> getTakeDebt() {
        return takeDebt;
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
}
