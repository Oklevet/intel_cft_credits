package ru.intel.credits.repository;

import ru.intel.credits.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FillCollections implements FillCollectionOfModel {

    public VidOperDog getVidOperByIdDebets(Collection<VidOperDog> opers, long collection) {
        for (VidOperDog oper : opers) {
            if (oper.getCollectionDebts() == collection) {
                return oper;
            }
        }
        return null;
    }

    public HashMap<Long, VidOperDog> fillOperDebets(Collection<VidOperDog> opers, Collection<TakeInDebt> debets) {
        HashMap<Long, VidOperDog> opersMap = new HashMap<>();

        debets.stream()
                .filter(x -> getVidOperByIdDebets(opers, x.getCollectionId()) != null)
                .forEach(t -> getVidOperByIdDebets(opers, t.getCollectionId()).getDebets().add(t));

        opers.forEach(x -> opersMap.put(x.getId(), x));
        return opersMap;
    }

    /**
     * Получение плановых и фактических операций по каждому кредиту из коллекции creds из мап foOpers и poOpers
     * @param creds - колекция кредитов по обрабатываемому саблисту в рамках таска экзекьютора
     * @param foOpers - мапа с фактическими операциями по саблисту кредитов
     * @param poOpers - мапа с плановыми операциями по саблисту кредитов
     * @return список кредитов
     */
    public List<PrCred> fillFoPoDebtInCreds(List<PrCred> creds, HashMap<Long, List<FactOper>> foOpers,
                                            HashMap<Long, List<PlanOper>> poOpers, long seqId) {
        AtomicLong sequencerID = new AtomicLong(seqId);
        creds.stream().forEach(x -> {
            x.setListFO(foOpers.getOrDefault(x.getCollectionFO(), x.getListFO()));
            x.setListPO(poOpers.getOrDefault(x.getCollectionPO(), x.getListPO()));
            x.setCollectionDebts(sequencerID.getAndIncrement());
        });
        return creds;
    }
}
