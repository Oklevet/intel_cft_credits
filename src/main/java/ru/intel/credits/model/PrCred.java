package ru.intel.credits.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PrCred {

    private long id;

    @EqualsAndHashCode.Include
    private String numDog;

    @EqualsAndHashCode.Include
    private String val;

    private long collectionFO;

    private long collectionPO;

    private List<FactOper> listFO;

    private List<PlanOper> listPO;

    private long collectionDebts;
}