package ru.intel.сredits.calc;

import ru.intel.сredits.calc.debtsByCred.CalcPrcDebt;
import ru.intel.сredits.calc.debtsByCred.CalcSimpleDebt;
import ru.intel.сredits.model.Debt;
import ru.intel.сredits.model.PrCred;
import ru.intel.сredits.model.VidDebt;
import ru.intel.сredits.model.VidOperDog;
import ru.intel.сredits.repository.FillCollections;
import ru.intel.сredits.repository.Sql2oCFTRepository;

import java.util.*;

public class CalcAllDebts {

    HashMap<Integer, VidDebt> vidDebts;

    HashMap<Integer, VidOperDog> opers;

    Sql2oCFTRepository sql2oCFT;

    FillCollections fillCollections;

    Collection<PrCred> creds;

    HashSet<Integer> debtsOfCred;

    CalcPrcDebt calcPrcDebt = new CalcPrcDebt();
    CalcSimpleDebt calcSimpleDebt = new CalcSimpleDebt();

    public CalcAllDebts() {
    }

    /**
     * Запускается единожды перед выгрузкой из ЦФТ для актуализации видов задолженностей
     * и видов операций по договорам
     */
    public void actualDirectories() {
        vidDebts = sql2oCFT.getAllVidDebts();
        opers = fillCollections.fillOperDebets(sql2oCFT.getAllVidOperDogs(), sql2oCFT.getAllTakeInDebt());
    }

    /**
     * Получение пачки кредитов по которой будет выполняться расчет задолженностей
     */
    public void loadNewCredsPool() {
        creds.clear();
        creds = sql2oCFT.getAllCreds();
        creds = fillCollections.fillFOInCreds(creds, sql2oCFT.getAllFOByCreds());
        creds = fillCollections.fillPOInCreds(creds, sql2oCFT.getAllPOByCreds());
    }

    public void getCredDebts(PrCred cred) {
        debtsOfCred.clear();

        cred.getListFO().stream().map(x -> {
            debtsOfCred.add(x.getVidDebt());
            debtsOfCred.add(x.getVidDebtDt());
            opers.get(x.getOper()).getDebets().forEach(d -> debtsOfCred.add(d.getDebt()));
            return null;
        });

        cred.getListPO().stream().map(x -> {
            debtsOfCred.add(x.getVidDebt());
            debtsOfCred.add(x.getVidDebtDt());
            opers.get(x.getOper()).getDebets().forEach(d -> debtsOfCred.add(d.getDebt()));
            return null;
        });
    }

    public List<Debt> credPoolCalc(Collection<PrCred> creds) {
        var debts = new ArrayList<Debt>();
        for (PrCred cred : creds) {
            getCredDebts(cred);
            for (Integer debt : debtsOfCred) {
                double summa = 0;
                switch (vidDebts.get(debt).getTypeDebt()) {
                    case "Простая" -> summa += calcSimpleDebt.calcSimpleDebt(cred, opers, debt);
                    case "Процентная" -> summa += calcPrcDebt.calcPrcDebt(cred, opers, debt);
                    case "Комиссионная" -> summa += 0;
                    case "Налоговая" -> summa += 0;
                }
                debts.add(new Debt(cred.getCollectionDebts(), debt, summa));
            }
        }
        return debts;
    }
}
