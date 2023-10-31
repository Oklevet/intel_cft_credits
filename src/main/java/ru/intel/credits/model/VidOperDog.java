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
    private int id;

    @EqualsAndHashCode.Include
    private String code;

    private int collectionDebts;

    private int vidDebt;

    private int vidDebtDt;

    private ArrayList<TakeInDebt> debets;
}
