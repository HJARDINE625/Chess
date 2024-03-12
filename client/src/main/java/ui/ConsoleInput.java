package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ConsoleInput implements InputReader{

    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    @Override
    public String getString() {
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Scanner scannedStuff = new Scanner(System.in);
        return scannedStuff.toString();
    }

    @Override
    public int getNum() {
        boolean isAnInt = false;
        //this will throw an error in the other method if returned...
        int finalReturn = 9999999;
        while(!isAnInt) {
            String convertToInt = getString();
            String oneStepAway = "";
            //this allows them to include escape characters...
            if(convertToInt.matches("[0-9 \n\t]+")) {
                for (int currentChar = 0; currentChar < convertToInt.length(); currentChar++) {
                    char CheckChar = convertToInt.charAt(currentChar);
                    if ((CheckChar != '\n') && (CheckChar != ' ') && (CheckChar != '\t')) {
                        oneStepAway = oneStepAway + CheckChar;
                    }
                }
                finalReturn = Integer.parseInt(oneStepAway);
                isAnInt = true;
            } else {
                out.print(SET_BG_COLOR_YELLOW);
                out.print(SET_TEXT_COLOR_RED);
                out.print("Error: That is not a valid number! Try again!\n");
            }
        }
        return finalReturn;
    }
}
