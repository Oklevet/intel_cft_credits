package ru.intel.credits.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Debt {

    @EqualsAndHashCode.Include
    private long collectionId;

    @EqualsAndHashCode.Include
    private long id;

    private double summa;

    public Debt(long collectionId, long id) {
        this.collectionId = collectionId;
        this.id = id;
    }
}
