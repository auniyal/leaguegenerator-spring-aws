package org.etongang.leaguegenerator.controller;

import org.etongang.leaguegenerator.HtmlContent;
import org.etongang.leaguegenerator.domain.DoublesGame;
import org.etongang.leaguegenerator.domain.DoublesPair;
import org.etongang.leaguegenerator.service.MatchService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/api/match")
public class MatchController {


    final
    HtmlContent htmlContent;
    final
    MatchService matchService;

    public MatchController(HtmlContent htmlContent, MatchService matchService) {
        this.htmlContent = htmlContent;
        this.matchService = matchService;
    }

    @GetMapping(value = "/{numberOfPlayer}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String generateMatches(@PathVariable int numberOfPlayer,
                                  @RequestParam List<String> players) {

        Queue<String> allPlayersQueue = new ArrayDeque<>(players);
        List<String> allPlayersList = new ArrayList<>(players);


        int playersProvided = allPlayersList.size();
        if (numberOfPlayer != playersProvided)
            throw new RuntimeException(String.format("Cannot continue, number of players in league is %d, but provided %d", numberOfPlayer, playersProvided));

        List<DoublesPair> uniquePairing = matchService.generateUniquePairs(playersProvided, allPlayersList);
        List<DoublesGame> doublesGames = matchService.generateMatches(uniquePairing);




        return htmlContent.getHead() +
                htmlContent.getTableMatches() +
                matchService.getMatchRow(doublesGames, allPlayersQueue) +
                htmlContent.getTableEnd()+
                htmlContent.getLineBreak()+
                htmlContent.getTableValidationStart()+
                matchService.validateGeneratedMatchesCounts(playersProvided, doublesGames,allPlayersList)+
                htmlContent.getTableEnd()+
                htmlContent.getBottomHtml();

    }

}
