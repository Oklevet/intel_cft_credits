package ru.intel.credits.model;

import java.util.Map;
import java.util.Objects;

public class TakeInDebt {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "debt", "debt",
            "DT", "DT",
            "collection_id", "collectionId"
    );

    private int debt;
    private boolean dt;
    private int collectionId;

    public TakeInDebt(int debt, boolean dt, int collectionId) {
        this.debt = debt;
        this.dt = dt;
        this.collectionId = collectionId;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }

    public boolean isDt() {
        return dt;
    }

    public void setDt(boolean dt) {
        this.dt = dt;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TakeInDebt that = (TakeInDebt) o;
        return debt == that.debt && collectionId == that.collectionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(debt, collectionId);
    }
}
