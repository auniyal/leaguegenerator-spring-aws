package org.etongang.leaguegenerator.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoublesGame {
    int gameId;
    DoublesPair doublesPairOne;
    DoublesPair doublesPairTwo;

    @Override
    public String toString() {
        return String.format("%s-%s / %s-%s",
                doublesPairOne.getSinglePlayerOne().getName(),
                doublesPairOne.getSinglePlayerTwo().getName(),
                doublesPairTwo.getSinglePlayerOne().getName(),
                doublesPairTwo.getSinglePlayerTwo().getName());
    }

}