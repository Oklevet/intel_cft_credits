package ru.intel.credits.model;

import java.util.List;
import java.util.Objects;

public class CredDebtTransfer {

    private List<PrCred> creds;

    private List<Debt> debts;

    public CredDebtTransfer(List<PrCred> creds, List<Debt> debts) {
        this.creds = creds;
        this.debts = debts;
    }

    public List<PrCred> getCreds() {
        return creds;
    }

    public void setCreds(List<PrCred> creds) {
        this.creds = creds;
    }

    public List<Debt> getDebts() {
        return debts;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CredDebtTransfer that = (CredDebtTransfer) o;
        return Objects.equals(creds, that.creds) && Objects.equals(debts, that.debts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creds, debts);
    }
}
