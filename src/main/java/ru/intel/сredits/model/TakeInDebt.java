package ru.intel.сredits.model;

import java.util.Map;
import java.util.Objects;

public class TakeInDebt {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "debt", "debt",
            "DT", "DT",
            "collectionId", "collectionId"
    );

    int debt;
    boolean DT;
    int collectionId;

    public TakeInDebt(int debt, boolean DT, int collectionId) {
        this.debt = debt;
        this.DT = DT;
        this.collectionId = collectionId;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }

    public boolean isDT() {
        return DT;
    }

    public void setDT(boolean DT) {
        this.DT = DT;
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
        if (o == null || getClass() != o.getClass()) return false;
        TakeInDebt that = (TakeInDebt) o;
        return debt == that.debt && collectionId == that.collectionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(debt, collectionId);
    }
}
