package org.etongang.leaguegenerator;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

@Getter
@Component
public class HtmlContent {
    double costPerCan = 5.5;
    String head = """
                <!DOCTYPE html>
                <html>
                <head>
                <style>
                 table {
            	border-collapse: collapse;
                font-family: Tahoma, Geneva, sans-serif;
            }
            table td {
            	padding: 20px;
            }
            table thead td {
            	background-color: #54585d;
            	color: #ffffff;
            	font-weight: bold;
            	font-size: 20px;
            	border: 5px solid #54585d;
            }
            table tbody td {
            	color: #636363;
            	border: 1px solid #dddfe1;
            }
              table tbody  {
            	color: #636363;
            	border: 1px solid #dddfe1;
            }
            table tbody tr {
            	background-color: #f9fafb;
            }
             table tbody th {
            	background-color:#f2ffe6;
            }
            table tbody tr:nth-child(odd) {
            	background-color: #ffffff;
            }
                </style>
                </head>
                <body>
                <h1>Eton tennis scheduler</h1>
                """;

    String tableMatchesFull = """
                   <table>
                             <tr>
                                <th>Date</th>
                                <th>Match No.</th>
                                <th>Player </th>
                                <th>Can</th>
                                <th>Surface</th>
                                <th>Match No.</th>
                                <th>Player </th>
                                <th>Can</th>
                                <th>Surface</th>                                
                              </tr>
            """;

    String tableMatchesHalf = """
                   <table>
                             <tr>

                                <th>Match No.</th>
                                <th>Player </th>
                                <th>Can</th>
                                <th>Surface</th>                        
                              </tr>
            """;

    String tableValidationStart = """
            <h3>validation check</h3>
                               <table >
                                         <tr>
                                            <th>Player </th>
                                            <th>Matches </th>
                                          </tr>
                        """;
    String tableEnd = """
                   </table>
            """;
    String bottomHtml = """
             <h4> (c) eton tennis gang </h4>
             </body>
             </html>
            """;

    String lineBreak = """
            <br><br>
            """;
    String jokeOfTheDay = """
            <STYLE>A.vbx{FONT-FAMILY: Arial; TEXT-DECORATION: none; COLOR: #000000; FONT-WEIGHT: bold; font-size: 18px;} A.vbx:link{color: #000000;} A.vbx:active{color: #000000;} A.vbx:visited{color: #000000;} A.vbx:hover{TEXT-DECORATION: underline; }</STYLE><DIV style='margin: auto; padding: 0; width: 160px;'><DIV style='height: 250px; border: 1px solid #000000;'><DIV style='height: 23px; BACKGROUND: #FF0000; text-align: left; padding: 2px;'><a href='https://www.JokestJokes.com' target='_blank' title='www.JokestJokes.com - Joke, Jokes' class='vbx'>Joke of the Day</a></DIV><iframe src='https://www.jokestjokes.com/joke-of-the-day.php?h=FFFFFF&w=160&b=14&sz=000000' width='158' height='223' style='border: 0; margin: 0; padding: 0;'></iframe></DIV></DIV>
            """;

    public String getCansSummary(int totalMatches, int nunberOfplayers) {
        BigDecimal bigDecimal = BigDecimal.valueOf((totalMatches / 2 * costPerCan) / nunberOfplayers);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.FLOOR);
        int cansNeeded = (totalMatches / 2) + 1;
        return String.format("<h2>Cost: £%s</h2>" +
                        "<h4>Total cans needed: %d @ £%.2f, cost per person: %.2f</h4>", cansNeeded * costPerCan,
                cansNeeded, costPerCan, bigDecimal);

    }
}
