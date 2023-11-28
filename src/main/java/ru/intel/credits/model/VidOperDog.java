package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VidOperDog {

    @EqualsAndHashCode.Include
    private long id;

    @EqualsAndHashCode.Include
    private String code;

    private long collectionDebts;

    private long vidDebt;

    private long vidDebtDt;

    private ArrayList<TakeInDebt> debets;
}
