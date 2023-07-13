package org.etongang.leaguegenerator.controller;

import org.etongang.leaguegenerator.HtmlContent;
import org.etongang.leaguegenerator.domain.DoublesGame;
import org.etongang.leaguegenerator.domain.DoublesPair;
import org.etongang.leaguegenerator.domain.MatchGame;
import org.etongang.leaguegenerator.service.MatchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/v1/api/match")
public class MatchController {
    final HtmlContent htmlContent;
    final MatchService matchService;

    public MatchController(HtmlContent htmlContent, MatchService matchService) {
        this.htmlContent = htmlContent;
        this.matchService = matchService;
    }

    @GetMapping(value = "/{numberOfPlayer}", produces = MediaType.TEXT_HTML_VALUE)
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

}
