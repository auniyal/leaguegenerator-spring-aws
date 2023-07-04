package org.etongang.leaguegenerator.service;

import org.etongang.leaguegenerator.domain.DoublesGame;
import org.etongang.leaguegenerator.domain.DoublesPair;
import org.etongang.leaguegenerator.domain.SinglePlayer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MatchService {

    public List<DoublesPair> generateUniquePairs(int numberOfPlayers, List<String> allPlayers) {
        List<DoublesPair> uniquePairing = new ArrayList<>();
        int pairingId = 1;
        for (int i = 1; i <= numberOfPlayers; i++) {
            for (int j = i + 1; j <= numberOfPlayers; j++) {
                uniquePairing.add(new DoublesPair(pairingId++, new SinglePlayer(i, allPlayers.get(i - 1)), new SinglePlayer(j, allPlayers.get(j - 1))));
            }
        }

        //TODO shuffle trips unique pairing intermittently
        Collections.shuffle(uniquePairing);

        return uniquePairing;
    }


    public List<DoublesGame> generateMatches(List<DoublesPair> uniquePairing) {
        List<DoublesGame> doublesGames = new ArrayList<>();
        for (int m = 0; m < uniquePairing.size(); m++) {
            DoublesPair firstPair = uniquePairing.get(m);
            DoublesPair secondPair = getSecondPair(uniquePairing, firstPair, doublesGames);
            if (secondPair != null) {
                doublesGames.add(new DoublesGame(m, firstPair, secondPair));
            }
        }
        return doublesGames;
    }

    public String validateGeneratedMatchesCounts(int numberOfPlayers, List<DoublesGame> doublesGames, List<String> allPlayersList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int count = 1; count <= numberOfPlayers; count++) {
            int finalCount = count;
            long totalMatches = doublesGames.stream().filter(x -> x.getDoublesPairOne().getSinglePlayerOne().getPlayerId() == finalCount
                            || x.getDoublesPairOne().getSinglePlayerTwo().getPlayerId() == finalCount
                            || x.getDoublesPairTwo().getSinglePlayerOne().getPlayerId() == finalCount
                            || x.getDoublesPairTwo().getSinglePlayerTwo().getPlayerId() == finalCount)
                    .count();


            if (numberOfPlayers % 4 == 0 && totalMatches != numberOfPlayers - 1) {
                throw new RuntimeException(String.format("Retry, player:%d has %d games. Expected count is:%d ", count, totalMatches, numberOfPlayers - 1));
            }
            stringBuilder.append(String.format("<tr><td> %S </td><td>%d</td></tr>", allPlayersList.get(count-1), totalMatches));

        }

        return stringBuilder.toString();
    }


    private DoublesPair getSecondPair(List<DoublesPair> uniquePairing, DoublesPair firstPair, List<DoublesGame> doublesGames) {

        for (DoublesPair doublesPair : uniquePairing) {
            boolean isPairingAllowed = firstPair.isPairingAllowed(doublesPair);
            boolean firstPairAlreadyPaired = alreadyPaired(doublesGames, firstPair);
            boolean secondPairAlreadyPaired = alreadyPaired(doublesGames, doublesPair);
            if (isPairingAllowed && !firstPairAlreadyPaired && !secondPairAlreadyPaired) {
                return doublesPair;
            }

        }
        return null;
    }


    private boolean alreadyPaired(List<DoublesGame> doublesGames, DoublesPair doublesPair) {
        return !doublesGames.isEmpty() &&
                doublesGames.stream().anyMatch(x -> x.getDoublesPairOne().equals(doublesPair)
                        || x.getDoublesPairTwo().equals(doublesPair));
    }

    public String displayRow(DoublesGame game, int count, String player) {


        return String.format("<td>Match:%s</td>  <td>%s/%s vs -%s/%s</td><td>%s</td>",
                count,
                game.getDoublesPairOne().getSinglePlayerOne().getName(), game.getDoublesPairOne().getSinglePlayerTwo().getName(),
                game.getDoublesPairTwo().getSinglePlayerOne().getName(), game.getDoublesPairTwo().getSinglePlayerTwo().getName(), player);
    }

    public String getMatchRow(List<DoublesGame> doublesGames, Queue<String> allPlayersQueue) {

        StringBuilder stringBuffer = new StringBuilder();
        AtomicReference<String> player = new AtomicReference<>("");
        AtomicInteger count = new AtomicInteger(1);
        doublesGames.forEach(x -> {
            player.set(count.get() % 2 == 0 ? player.get() : getPlayerFromPool(allPlayersQueue));
            stringBuffer.append("<tr>").append(displayRow(x, count.getAndIncrement(), player.get())).append("</tr>");

        });
        return stringBuffer.toString();
    }

    private static String getPlayerFromPool(Queue<String> allPlayersQueue) {
        String player = "NA";
        try {
            player = allPlayersQueue.remove();
        } catch (Exception e) {
            //ignore the can exception for now
        }
        return player;
    }

}
