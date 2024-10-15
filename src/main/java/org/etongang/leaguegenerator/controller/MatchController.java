package org.etongang.leaguegenerator.controller;

import org.etongang.leaguegenerator.HtmlContent;
import org.etongang.leaguegenerator.domain.DoublesGame;
import org.etongang.leaguegenerator.domain.DoublesPair;
import org.etongang.leaguegenerator.domain.MatchGame;
import org.etongang.leaguegenerator.request.UserInput;
import org.etongang.leaguegenerator.response.PlayerData;
import org.etongang.leaguegenerator.service.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController

public class MatchController {
    final HtmlContent htmlContent;
    final MatchService matchService;

    public MatchController(HtmlContent htmlContent, MatchService matchService) {
        this.htmlContent = htmlContent;
        this.matchService = matchService;
    }

    @GetMapping(value = "/v1/api/match/{numberOfPlayer}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String generateMatches(@PathVariable int numberOfPlayer,
                                  @RequestParam List<String> players,
                                  @RequestParam("full") boolean full,
                                  @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {


        Collections.shuffle(players);
        Queue<String> allPlayersQueue = new ArrayDeque<>(players);
        List<String> allPlayersList = new ArrayList<>(players);
        Collections.shuffle(allPlayersList);

        int playersProvided = allPlayersList.size();
        if (numberOfPlayer != playersProvided)
            throw new RuntimeException(String.format("Cannot continue, number of players in league is %d, but provided %d", numberOfPlayer, playersProvided));

        List<DoublesPair> uniquePairing = matchService.generateUniquePairs(playersProvided, allPlayersList);
        List<DoublesGame> doublesMatches = matchService.generateMatches(uniquePairing);

        List<MatchGame> matchGames = new ArrayList<>();
        if (full) {
            matchGames = matchService.generateUniqueMatchDays(doublesMatches, startDate);
        }

        return htmlContent.getHead() +
                String.format("%s", full ? htmlContent.getTableMatchesFull() : htmlContent.getTableMatchesHalf()) +
                String.format("%s", full ? matchService.getMatchDaysRow(matchGames, allPlayersQueue) : matchService.getMatchRow(doublesMatches, allPlayersQueue)) +
                htmlContent.getTableEnd() +
                htmlContent.getCansSummary(doublesMatches.size(), numberOfPlayer) +
                htmlContent.getLineBreak() +
                htmlContent.getTableValidationStart() +
                matchService.validateGeneratedMatchesCounts(playersProvided, doublesMatches, allPlayersList) +
                htmlContent.getTableEnd() +
                htmlContent.getJokeOfTheDay()+
                htmlContent.getBottomHtml();

    }
    @GetMapping(value = "/v2/api/match/{numberOfPlayer}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PlayerData> generateMatches(@RequestBody UserInput userInput ) {

        List<String> players = userInput.getPlayers();
        Collections.shuffle(players);
        Queue<String> allPlayersQueue = new ArrayDeque<>(players);
        List<String> allPlayersList = new ArrayList<>(players);
        Collections.shuffle(allPlayersList);

        int playersProvided = allPlayersList.size();
        if (userInput.getNumberOfPlayer() != playersProvided)
            throw new RuntimeException(String.format("Cannot continue, number of players in league is %d, but provided %d", userInput.getNumberOfPlayer(), playersProvided));

        List<DoublesPair> uniquePairing = matchService.generateUniquePairs(playersProvided, allPlayersList);
        List<DoublesGame> doublesMatches = matchService.generateMatches(uniquePairing);

        List<MatchGame> matchGames = new ArrayList<>();
        boolean isFull = userInput.isFull();
        if (isFull) {
            matchGames = matchService.generateUniqueMatchDays(doublesMatches, userInput.getStartDate());
        }

        PlayerData playerData1 = new PlayerData();
        playerData1.setDate("2022-07-10");
        playerData1.setMatchNo(1);
        playerData1.setPlayer("Ami/Ashish vs Roger/Jose");
        playerData1.setSurface("Blue");

        PlayerData playerData2 = new PlayerData();
        playerData2.setDate("2022-07-10");
        playerData2.setMatchNo(2);
        playerData2.setPlayer("Raj/Adi vs Phani/Mo");
        playerData2.setSurface("Blue");

        PlayerData playerData3= new PlayerData();
        playerData3.setDate("2022-07-10");
        playerData3.setMatchNo(3);
        playerData3.setPlayer("Ami/Roger vs Ashish/Jose");
        playerData3.setSurface("Blue");

        PlayerData playerData4 = new PlayerData();
        playerData4.setDate("2022-07-10");
        playerData4.setMatchNo(4);
        playerData4.setPlayer("Raj/Phani vs Adi/Mo");
        playerData4.setSurface("Blue");



        return List.of(playerData1, playerData2, playerData3, playerData4);

    }

}
