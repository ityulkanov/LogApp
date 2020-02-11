package com.ityulkanov;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


/**
 * Start file to generate logs statistics.
 * 3 options available - generate stats by hour or by minute or quit.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        boolean done = false;
        while (!done) {
            Scanner userInput = new Scanner(System.in);
            System.out.println("Please pick the desired timeframe: hour / minute,to quit, type \"quit\"");
            String format = userInput.nextLine();
            switch (format) {
                case "hour":
                case "minute":
                    File file = new File("statistics.txt");
                    file.delete();
                    LogUtils.createStatisticsFile(format);
                    done = true;
                    break;
                case "quit":
                    done = true;
                    break;
                default:
                    System.out.println("You've entered a wrong timeframe, please pick another one");
                    break;
            }
        }
    }
}
