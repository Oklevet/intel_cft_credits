package ru.intel.сredits.calc;

import ru.intel.сredits.calc.debtsByCred.CalcComissDebt;
import ru.intel.сredits.calc.debtsByCred.CalcPrcDebt;
import ru.intel.сredits.calc.debtsByCred.CalcSimpleDebt;
import ru.intel.сredits.model.*;
import ru.intel.сredits.repository.FillCollections;
import ru.intel.сredits.repository.Sql2oCFTRepository;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CalcAllDebts {

    HashMap<Integer, VidDebt> vidDebts;

    HashMap<Integer, VidOperDog> opers;

    Sql2oCFTRepository sql2oCFT;

    FillCollections fillCollections;

    Collection<PrCred> creds;

    HashSet<Integer> debtsOfCred;

    CalcPrcDebt calcPrcDebt = new CalcPrcDebt();
    CalcSimpleDebt calcSimpleDebt = new CalcSimpleDebt();
    CalcComissDebt calcComissDebt = new CalcComissDebt();

    public CalcAllDebts(Sql2oCFTRepository sql2oCF) {
        this.sql2oCFT = sql2oCF;
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
     * @return
     */
    public Collection<PrCred> loadNewCredsPool(List<Integer> listId) {
        creds.clear();
        creds = sql2oCFT.getAllCreds(listId);
        creds = fillCollections.fillFOInCreds(creds, sql2oCFT.getAllFOByCreds());
        creds = fillCollections.fillPOInCreds(creds, sql2oCFT.getAllPOByCreds());
        return creds;
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

    public CredDebtTtansfer credPoolCalc(Collection<PrCred> creds) {
        var credDebt = new CredDebtTtansfer(new ArrayList<PrCred>(), new ArrayList<Debt>());
        for (PrCred cred : creds) {
            credDebt.getCreds().add(cred);
            getCredDebts(cred);
            for (Integer debt : debtsOfCred) {
                double summa = 0;
                switch (vidDebts.get(debt).getTypeDebt()) {
                    case "Простая" -> summa += calcSimpleDebt.calcSimpleDebt(cred, opers, debt);
                    case "Процентная" -> summa += calcPrcDebt.calcPrcDebt(cred, opers, debt);
                    case "Комиссионная" -> summa += calcComissDebt.calcComissDebt(cred, opers, debt);
                    case "Налог" -> summa += 0;
                }
                credDebt.getDebts().add(new Debt(cred.getCollectionDebts(), debt, summa));
            }
        }
        return credDebt;
    }

    public static <Integer> Stream<List<Integer>> batchesOfList(List<Integer> source, int length) {
        if (length <= 0)
            return null;
        int size = source.size();
        if (size <= 0)
            return Stream.empty();
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(
                n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }

    public List<CredDebtTtansfer> calcAllCredsByPools(CalcAllDebts calcAllDebts, int batch) {
        var listCredId = sql2oCFT.getIDAllCreds();
        var credDebt = new ArrayList<CredDebtTtansfer>();
        batchesOfList(listCredId, batch).map(x ->
                credDebt.add(calcAllDebts.credPoolCalc(calcAllDebts.loadNewCredsPool(x))));
        return credDebt;
    }
}
