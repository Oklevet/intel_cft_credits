package ru.intel.credits.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VidDebt {

    @EqualsAndHashCode.Include
    private int id;

    @EqualsAndHashCode.Include
    private String code;

    private String typeDebt;
}