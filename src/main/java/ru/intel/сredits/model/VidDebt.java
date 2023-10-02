package ru.intel.сredits.model;

import java.util.Map;
import java.util.Objects;

public class VidDebt {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "int", "int",
            "code", "code"
    );

    int id;
    String code;

    public VidDebt(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VidDebt vidDebt = (VidDebt) o;
        return id == vidDebt.id && Objects.equals(code, vidDebt.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}