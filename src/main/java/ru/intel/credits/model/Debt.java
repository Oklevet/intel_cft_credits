package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Debt {

    @EqualsAndHashCode.Include
    private int collectionId;

    @EqualsAndHashCode.Include
    private int id;

    private double summa;
}
