package ru.intel.credits.model;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PrCred {

    @EqualsAndHashCode.Include
    private String numDog;

    @EqualsAndHashCode.Include
    private String val;

    private int collectionFO;

    private int collectionPO;

    private ArrayList<FactOper> listFO;

    private ArrayList<PlanOper> listPO;

    private int collectionDebts;
}