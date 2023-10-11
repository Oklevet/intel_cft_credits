package ru.intel.credits.calc;

import ru.intel.credits.calc.debts.CalcComissDebt;
import ru.intel.credits.calc.debts.CalcPrcDebt;
import ru.intel.credits.calc.debts.CalcSimpleDebt;
import ru.intel.credits.model.*;
import ru.intel.credits.repository.FillCollections;
import ru.intel.credits.repository.Sql2oCFTRepository;
import ru.intel.credits.repository.Sql2oRecieveDBRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CalcAllDebts {

    HashMap<Integer, VidDebt> vidDebts = new HashMap<>();

    HashMap<Integer, VidOperDog> opers;

    Sql2oCFTRepository sql2oCFT;

    Sql2oRecieveDBRepository sql2oRecieveDB;

    Collection<PrCred> creds;

    HashSet<Integer> debtsOfCred = new HashSet<>();

    FillCollections fillCollections = new FillCollections();

    CalcPrcDebt calcPrcDebt = new CalcPrcDebt();

    CalcSimpleDebt calcSimpleDebt = new CalcSimpleDebt();

    CalcComissDebt calcComissDebt = new CalcComissDebt();

    public CalcAllDebts(Sql2oCFTRepository sql2oCFT, Sql2oRecieveDBRepository sql2oRecieveDB) {
        this.sql2oCFT = sql2oCFT;
        this.sql2oRecieveDB = sql2oRecieveDB;
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
    public Collection<PrCred> loadNewCredsPool(List<Integer> listId) {
        if (creds != null) {
            creds.clear();
        }

        AtomicInteger sequence = new AtomicInteger(sql2oRecieveDB.getSequence(listId.size()));

        creds = sql2oCFT.getAllCreds(listId);
        creds.forEach(x -> {
            x.setListFO(new ArrayList<>());
            x.setListPO(new ArrayList<>());
            x.setCollectionDebts(sequence.getAndIncrement());
        });

        creds = fillCollections.fillFOInCreds(creds, sql2oCFT.getAllFOByCreds());
        creds = fillCollections.fillPOInCreds(creds, sql2oCFT.getAllPOByCreds());

        return creds;
    }

    public void getCredDebts(PrCred cred) {
        if (debtsOfCred != null) {
            debtsOfCred.clear();
        }

        cred.getListFO().forEach(x -> {
            debtsOfCred.add(x.getVidDebt());
            debtsOfCred.add(x.getVidDebtDt());
            opers.get(x.getOper()).getDebets().forEach(d -> debtsOfCred.add(d.getDebt()));
        });

        cred.getListPO().forEach(x -> {
            debtsOfCred.add(x.getVidDebt());
            debtsOfCred.add(x.getVidDebtDt());
            opers.get(x.getOper()).getDebets().forEach(d -> debtsOfCred.add(d.getDebt()));
        });

        debtsOfCred.remove(0);
    }

    public CredDebtTtansfer credPoolCalc(Collection<PrCred> creds) {
        var credDebt = new CredDebtTtansfer(new ArrayList<>(), new ArrayList<>());
        for (PrCred cred : creds) {
            credDebt.getCreds().add(cred);
            getCredDebts(cred);

            for (Integer debt : debtsOfCred) {
                double summa;
                switch (vidDebts.get(debt).getTypeDebt()) {
                    case "Простая" -> summa = calcSimpleDebt.calcSimpleDebt(cred, opers, debt);
                    case "Процентная" -> summa = calcPrcDebt.calcPrcDebt(cred, opers, debt);
                    case "Комиссионная" -> summa = calcComissDebt.calcComissDebt(cred, opers, debt);
                    default -> summa = 0;
                }
                credDebt.getDebts().add(new Debt(cred.getCollectionDebts(), debt, summa));
            }
        }
        return credDebt;
    }

    public static <Integer> Stream<List<Integer>> batchesOfList(List<Integer> source, int length) {
        if (length <= 0) {
            return null;
        }

        int size = source.size();
        if (size <= 0) {
            return Stream.empty();
        }
        int fullChunks = (size - 1) / length;
        return IntStream.range(0, fullChunks + 1).mapToObj(
                n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
    }

    public List<CredDebtTtansfer> calcAllCredsByPools(CalcAllDebts calcAllDebts, int batch) {
        var listCredId = sql2oCFT.getIDAllCreds();
        var credDebt = new ArrayList<CredDebtTtansfer>();
        try {
            batchesOfList(listCredId, batch).forEach(x ->
                    credDebt.add(calcAllDebts.credPoolCalc(calcAllDebts.loadNewCredsPool(x))));
        } catch (NullPointerException ignored) {}
        
        return credDebt;
    }
}
