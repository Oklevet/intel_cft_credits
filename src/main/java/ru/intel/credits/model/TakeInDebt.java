package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TakeInDebt {

    @EqualsAndHashCode.Include
    private int debt;

    private boolean dt;

    @EqualsAndHashCode.Include
    private int collectionId;
}
