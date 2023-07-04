package org.etongang.leaguegenerator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class DoublesPair {

    int pairingId;
    SinglePlayer singlePlayerOne;
    SinglePlayer singlePlayerTwo;




    public boolean isPairingAllowed(DoublesPair doublesPair) {

        boolean conflict = (getSinglePlayerOne().equals(doublesPair.getSinglePlayerOne())
                || getSinglePlayerOne() .equals(doublesPair.getSinglePlayerTwo()))
                || (this.getSinglePlayerTwo().equals(doublesPair.getSinglePlayerOne())
                || this.getSinglePlayerTwo().equals(doublesPair.getSinglePlayerTwo()));


        return !conflict;
    }
}
