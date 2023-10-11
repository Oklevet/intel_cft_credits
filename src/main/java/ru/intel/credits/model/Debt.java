package ru.intel.credits.model;

import java.util.Objects;

public class Debt {

    private int collectionId;

    private int id;

    private double summa;

    public Debt(int collectionId, int id, double summa) {
        this.collectionId = collectionId;
        this.id = id;
        this.summa = summa;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSumma() {
        return summa;
    }

    public void setSumma(double summa) {
        this.summa = summa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Debt)) {
            return false;
        }
        Debt debt = (Debt) o;
        return collectionId == debt.collectionId && id == debt.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, id);
    }
}
