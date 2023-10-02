package ru.intel.credits.model;

import java.util.ArrayList;
import java.util.Objects;

public class PrCred {
    String numDog;
    String val;
    int collectionFO;
    int collectionPO;
    ArrayList<FactOper> listFO;
    ArrayList<FactOper> listPO;

    public PrCred(String numDog, String val, int collectionFO, int collectionPO, ArrayList<FactOper> listFO, ArrayList<FactOper> listPO) {
        this.numDog = numDog;
        this.val = val;
        this.collectionFO = collectionFO;
        this.collectionPO = collectionPO;
        this.listFO = listFO;
        this.listPO = listPO;
    }

    public int getCollectionFO() {
        return collectionFO;
    }

    public void setCollectionFO(int collectionFO) {
        this.collectionFO = collectionFO;
    }

    public int getCollectionPO() {
        return collectionPO;
    }

    public void setCollectionPO(int collectionPO) {
        this.collectionPO = collectionPO;
    }

    public String getNumDog() {
        return numDog;
    }

    public void setNumDog(String numDog) {
        this.numDog = numDog;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public ArrayList<FactOper> getListFO() {
        return listFO;
    }

    public void setListFO(ArrayList<FactOper> listFO) {
        this.listFO = listFO;
    }

    public ArrayList<FactOper> getListPO() {
        return listPO;
    }

    public void setListPO(ArrayList<FactOper> listPO) {
        this.listPO = listPO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrCred prCred)) return false;
        return Objects.equals(numDog, prCred.numDog) && Objects.equals(val, prCred.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numDog, val);
    }
}
