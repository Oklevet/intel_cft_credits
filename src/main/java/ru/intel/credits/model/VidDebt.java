package ru.intel.credits.model;

import java.util.Map;
import java.util.Objects;

public class VidDebt {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "int", "int",
            "code", "code",
            "debt_type", "typeDebt"
    );

    private int id;
    private String code;
    private String typeDebt;

    public VidDebt(int id, String code, String typeDebt) {
        this.id = id;
        this.code = code;
        this.typeDebt = typeDebt;
    }

    public String getTypeDebt() {
        return typeDebt;
    }

    public void setTypeDebt(String typeDebt) {
        this.typeDebt = typeDebt;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VidDebt vidDebt = (VidDebt) o;
        return id == vidDebt.id && Objects.equals(code, vidDebt.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}