package org.etongang.leaguegenerator.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoublesGame {

    int gameId;
    DoublesPair doublesPairOne;
    DoublesPair doublesPairTwo;

}