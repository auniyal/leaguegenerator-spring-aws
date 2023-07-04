package org.etongang.leaguegenerator;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class HtmlContent {

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
            table tbody tr {
            	background-color: #f9fafb;
            }
            table tbody tr:nth-child(odd) {
            	background-color: #ffffff;
            }
                </style>
                </head>
                <body>
                <h2>(c) Eton tennis scheduler</h2>
                """;

    String tableMatches = """
                   <table>
                             <tr>
                                <th>Match No.</th>
                                <th>Player </th>
                               <th>Can ownerhip</th>
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
             </body>
             </html>
            """;

    String lineBreak = """
            <br><br><br><br>
            """;
}
