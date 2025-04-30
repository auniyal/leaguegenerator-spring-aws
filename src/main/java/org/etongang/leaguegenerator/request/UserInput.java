package org.etongang.leaguegenerator.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserInput {
    private List<String> players;
    private boolean full;
    private LocalDate startDate;
    private List<String> surface;
}
