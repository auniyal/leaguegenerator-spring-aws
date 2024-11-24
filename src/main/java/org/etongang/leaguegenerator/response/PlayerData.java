package org.etongang.leaguegenerator.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerData {
   List<PlayerDataRow> playerDataRow = new ArrayList<>();
   String validation;
}
