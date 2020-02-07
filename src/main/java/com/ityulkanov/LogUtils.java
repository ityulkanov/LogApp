package com.ityulkanov;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;


/**
 * Stores all methods responsible for working with logs extraction.
 */
@UtilityClass
final class LogUtils {

    /**
     * Generates file based on timeFrameChosen.
     *
     * @param timeFrameChosen generating file based on input by user
     */
    @SneakyThrows
    static void createStatisticsFile(final String timeFrameChosen) {
        final List<LogData> logList = generateLogList();
        final FileWriter writer = new FileWriter(Constants.FILE_NAME, true);
        Collections.sort(logList);
        if (timeFrameChosen.equals(Constants.HOUR_PICKER)) {
            generateStatsByHour(logList, writer);
        } else if (timeFrameChosen.equals(Constants.MINUTE_PICKER)) {
            generateStatsByMinute(logList, writer);
        }
    }

    /**
     * Collects logs from all the available files in chosen folder.
     *
     * @return List of LogData
     */
    @SneakyThrows
    private static List<LogData> generateLogList() {
        List<LogData> logList = new ArrayList<>();
        for (File file : LogUtils.getResourceFolderFiles(Constants.FOLDER_NAME)) {
            final Scanner input = new Scanner(file);
            while (input.hasNext()) {
                final String s = input.nextLine();
                final String[] logInput = s.split(Constants.LOG_SPLITTER);
                final LocalDateTime logDate = convertDate(logInput[0]);
                LogData logData;
                if (logInput[1].equals(Constants.LOG_LEVEL)) {
                    logData = new LogData(logDate, logInput[1]);
                    logList.add(logData);
                }
            }
        }
        return logList;
    }

    /**
     * Creates file for hourly stats.
     *
     * @param logList list of all logs available for analysis
     * @param writer  data storage
     */
    @SneakyThrows
    private static void generateStatsByHour(final List<LogData> logList, final FileWriter writer) {

        int counter = 1;
        for (int i = 0; i < logList.size(); i++) {
            LogData logEntry = logList.get(i);
            if (i + 1 < logList.size()) {
                LogData nextLogEntry = logList.get(i + 1);
                final LocalDateTime currDate = logEntry.getDate();
                final LocalDateTime nextDate = nextLogEntry.getDate();
                if (currDate.getDayOfYear() == nextDate.getDayOfYear()
                        && currDate.getHour() == nextDate.getHour()) {
                    counter++;
                } else {
                    writeToFile(writer, counter, logEntry);
                    counter = 1;
                }
            } else {
                writeToFile(writer, counter, logEntry);
            }
        }
        writer.close();
    }

    /**
     * Create file for minutes stats.
     *
     * @param logList list of all logs available for analysis
     * @param writer  data storage
     */
    @SneakyThrows
    private static void generateStatsByMinute(final List<LogData> logList, final FileWriter writer) {
        int counter = 1;
        for (int i = 0; i < logList.size(); i++) {
            LogData logEntry = logList.get(i);
            if (i + 1 < logList.size()) {
                LogData nextLogEntry = logList.get(i + 1);
                final LocalDateTime currDate = logEntry.getDate();
                final LocalDateTime nextDate = nextLogEntry.getDate();
                if (currDate.getDayOfYear() == nextDate.getDayOfYear()
                        && currDate.getHour() == nextDate.getHour()
                        && currDate.getMinute() == nextDate.getMinute()) {
                    counter++;
                } else {
                    writeToFile(writer, counter, logEntry);
                    counter = 1;
                }
            } else {
                writeToFile(writer, counter, logEntry);
            }
        }
        writer.close();
    }

    /**
     * Writes log occurrences in files.
     *
     * @param writer   FileWriter storing our data
     * @param counter  amount of logs in chosen time frame
     * @param logEntry current log we are iterating through
     */
    @SneakyThrows
    private static void writeToFile(final FileWriter writer, final int counter, final LogData logEntry) {
        final LocalDateTime date = logEntry.getDate();
        final String displayDate = date.format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
        final String hour = date.format(DateTimeFormatter.ofPattern(Constants.HOUR_FORMAT));
        final String nextHour = date.plusHours(1).format(DateTimeFormatter.ofPattern(Constants.HOUR_FORMAT));
        final String data = displayDate + " " + hour + "-" + nextHour + " количество ошибок " + counter + "\n";
        writer.write(data);
    }

    /**
     * Collect all the files available in the logs folder.
     *
     * @param folder that has logs stored
     * @return list of files in File[] format
     */
    private static File[] getResourceFolderFiles(final String folder) {
        final String path = Main.class.getClassLoader().getResource(folder).getPath();
        return new File(path).listFiles();
    }

    /**
     * Converts data gathered from logs into LocalDateTime.
     *
     * @param date log date in string format
     * @return value in LocalDateTime format
     */
     static LocalDateTime convertDate(final String date) {
        return LocalDateTime.parse(date,
                DateTimeFormatter.ofPattern(Constants.PARSER_FORMAT));
    }
}
