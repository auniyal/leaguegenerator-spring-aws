package org.etongang.leaguegenerator.service;

import org.etongang.leaguegenerator.domain.DoublesGame;
import org.etongang.leaguegenerator.domain.DoublesPair;
import org.etongang.leaguegenerator.domain.MatchGame;
import org.etongang.leaguegenerator.domain.SinglePlayer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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
            stringBuilder.append(String.format("%S: %d", allPlayersList.get(count - 1), totalMatches));
            stringBuilder.append("\n");

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

    public String displayRow(DoublesGame game, int count, int matchCount, String player) {


        return String.format("<td>Match:%s</td><td>%s/%s vs %s/%s</td><td>%s</td><td>%s</td>",
                count,
                game.getDoublesPairOne().getSinglePlayerOne().getName(), game.getDoublesPairOne().getSinglePlayerTwo().getName(),
                game.getDoublesPairTwo().getSinglePlayerOne().getName(), game.getDoublesPairTwo().getSinglePlayerTwo().getName(),
                player,
                matchCount % 2 == 0 ? "Red" : "Blue");
    }

    public String getMatchRow(List<DoublesGame> doublesGames, Queue<String> allPlayersQueue) {

        StringBuilder stringBuffer = new StringBuilder();
        AtomicReference<String> player = new AtomicReference<>("");
        AtomicInteger count = new AtomicInteger(1);
        doublesGames.forEach(x -> {
            player.set(count.get() % 2 == 0 ? player.get() : getPlayerFromPool(allPlayersQueue));
            stringBuffer.append("<tr>").append(displayRow(x, count.getAndIncrement(), count.get(), player.get())).append("</tr>");

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

    public List<MatchGame> generateUniqueMatchDays(List<DoublesGame> doublesMatches, LocalDate startDate, int numberOfMatchesADay, int noOfPlayers) {
        LocalDate dt = startDate;
        boolean twoGameMathDay = noOfPlayers >= 8;
        List<MatchGame> matchGames = new ArrayList<>();
        if (!twoGameMathDay) {

            for (int i = 0; i <= doublesMatches.size() - 1; i++) {

                DoublesGame doublesGame1 = doublesMatches.get(i);

                MatchGame matchGame = new MatchGame(dt, doublesGame1, null);
                if (!(numberOfMatchesADay == 1)) {
                    dt = dt.plusWeeks(1);
                }
                matchGames.add(matchGame);

            }
            return matchGames;
        } else {

            for (int i = 0, j = doublesMatches.size() - 1; i <= j; i++, j--) {

                DoublesGame doublesGame1 = doublesMatches.get(i);
                DoublesGame doublesGame2 = findEligibleMatchDayPair(doublesGame1, doublesMatches, matchGames, true);

                if (doublesGame2 == null) {
                    throw new RuntimeException("cannot find eligible 2nd doubles game for " + doublesGame1);
                }
                MatchGame matchGame = new MatchGame(dt, doublesGame1, doublesGame2);
                matchGames.add(matchGame);
                dt = dt.plusWeeks(1);
            }


            return matchGames;
        }
    }
//    public List<MatchGame> generateUniqueMatchDays(List<DoublesGame> doublesMatches, LocalDate startDate, int numberOfMatchesADay, int noOfPlayers) {
//        LocalDate dt = startDate;
//        boolean isWeekly= numberOfMatchesADay!=1;
//        boolean twoGameMathDay= noOfPlayers>=8;
//
//        List<MatchGame> matchGames = new ArrayList<>();
//       for (int i = 0; i <= doublesMatches.size()-1 ;  i++) {
//
//            DoublesGame doublesGame1 = doublesMatches.get(i);
//            DoublesGame doublesGame2 =
//                    twoGameMathDay? findEligibleMatchDayPair(doublesGame1, doublesMatches, matchGames,isWeekly):null;
//

    /// /            if (twoGameMathDay && doublesGame2 == null) {
    /// /                throw new RuntimeException("cannot find eligible 2nd doubles game for " + doublesGame1);
    /// /            }
//
//            MatchGame matchGame = new MatchGame(dt, doublesGame1,isWeekly? doublesGame2:null);
//
//            matchGames.add(matchGame);
//
//            if (!(numberOfMatchesADay == 1)) {
//                dt = dt.plusWeeks(1);
//            }
//        }

//        for (int i = 0; i <= doublesMatches.size()-1 ;  i++) {
//
//            DoublesGame doublesGame1 = doublesMatches.get(i);
//
//            MatchGame matchGame = new MatchGame(dt, doublesGame1,null);
//            if(!(numberOfMatchesADay==1)){
//                dt = dt.plusWeeks(1);
//            }
//            matchGames.add(matchGame);
//
//        }
//        return matchGames;
//    }
    private DoublesGame findEligibleMatchDayPair(DoublesGame doublesGame1, List<DoublesGame> doublesGames, List<MatchGame> matchGames, boolean isWeekly) {

        for (DoublesGame doublesGame2 : doublesGames) {
            if (isContains(matchGames, doublesGame2, isWeekly) || doublesGame2.equals(doublesGame1))
                continue;
            if (isMatchDayAllowed(doublesGame1, doublesGame2)) {
                return doublesGame2;
            }
        }


        return null;
    }

    private boolean isContains(List<MatchGame> matchGames, DoublesGame doublesGame2, boolean isWeekly) {
        return matchGames.stream().anyMatch(x -> x.getDoublesGameFirst().equals(doublesGame2) || x.getDoublesGameSecond().equals(doublesGame2));

    }

    private boolean isMatchDayAllowed(DoublesGame doublesGameFirst, DoublesGame doublesGameSecond) {


        Set<SinglePlayer> singlePlayers = new HashSet<>();


        //1st Player is part of 2nd Doubles Game
        SinglePlayer player1Match1 = doublesGameFirst.getDoublesPairOne().getSinglePlayerOne();
        SinglePlayer player2Match1 = doublesGameFirst.getDoublesPairOne().getSinglePlayerTwo();

        //2nd Player is part of 2nd Doubles Game
        SinglePlayer player3Match1 = doublesGameFirst.getDoublesPairTwo().getSinglePlayerOne();
        SinglePlayer player4Match1 = doublesGameFirst.getDoublesPairTwo().getSinglePlayerTwo();

        //1st Player is part of 2nd Doubles Game
        SinglePlayer player1Match2 = doublesGameSecond.getDoublesPairOne().getSinglePlayerOne();
        SinglePlayer player2Match2 = doublesGameSecond.getDoublesPairOne().getSinglePlayerTwo();

        //1st Player is part of 2nd Doubles Game
        SinglePlayer player3Match2 = doublesGameSecond.getDoublesPairTwo().getSinglePlayerOne();
        SinglePlayer player4Match2 = doublesGameSecond.getDoublesPairTwo().getSinglePlayerTwo();

        singlePlayers.add(player1Match1);
        singlePlayers.add(player2Match1);
        singlePlayers.add(player3Match1);
        singlePlayers.add(player4Match1);
        singlePlayers.add(player1Match2);
        singlePlayers.add(player2Match2);
        singlePlayers.add(player3Match2);
        singlePlayers.add(player4Match2);

        return singlePlayers.size() == 8;

    }

    public String getMatchDaysRow(List<MatchGame> matchGames, Queue<String> allPlayersQueue) {
        StringBuilder stringBuffer = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        AtomicInteger matchCount = new AtomicInteger(1);
        AtomicReference<String> owner1 = new AtomicReference<>(getPlayerFromPool(allPlayersQueue));
        AtomicReference<String> owner2 = new AtomicReference<>(getPlayerFromPool(allPlayersQueue));
        matchGames.forEach(matchGame -> {

            owner1.set(matchCount.get() % 2 == 0 ? getPlayerFromPool(allPlayersQueue) : owner1.get());
            owner2.set(matchCount.get() % 2 == 0 ? getPlayerFromPool(allPlayersQueue) : owner2.get());
            stringBuffer.append("<tr><th>")
                    .append(matchGame.getDate()).append("</th>")
                    .append(displayRow(matchGame.getDoublesGameFirst(), count.getAndIncrement(), matchCount.get(), owner1.get()))
//                    .append(displayRow(matchGame.getDoublesGameSecond(), count.getAndIncrement(), matchCount.get(), owner2.get()))
                    .append("</tr>");

            matchCount.getAndIncrement();
        });
        return stringBuffer.toString();
    }
}
