package org.etongang.leaguegenerator.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class MatchGame {
    LocalDate date;
    DoublesGame doublesGameFirst;
    DoublesGame doublesGameSecond;
}