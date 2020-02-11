package com.ityulkanov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    static void createStatisticsFile(final String timeFrameChosen) throws IOException {
        final List<LogData> logList = generateLogList();
        Collections.sort(logList);
        if (timeFrameChosen.equals(Constants.HOUR_PICKER)) {
            writeToFile(generateStatsByHour(logList), timeFrameChosen);
        } else if (timeFrameChosen.equals(Constants.MINUTE_PICKER)) {
            writeToFile(generateStatsByMinute(logList), timeFrameChosen);
        }

    }

    /**
     * Collects logs from all the available files in chosen folder.
     *
     * @return List of LogData
     */
    private static List<LogData> generateLogList() throws IOException {
        List<LogData> logList = new ArrayList<>();
        for (File file : LogUtils.getResourceFolderFiles(Constants.FOLDER_NAME)) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line = bufferedReader.readLine();
                while (line != null) {
                    final String[] logInput = line.split(Constants.LOG_SPLITTER);
                    final LocalDateTime logDate = convertDate(logInput[0]);
                    LogData logData;
                    if (logInput[1].equals(Constants.LOG_LEVEL)) {
                        logData = new LogData(logDate);
                        logList.add(logData);
                    }
                    line = bufferedReader.readLine();
                }
            }
        }
        return logList;
    }


    /**
     * Generates frequency map by hour
     *
     * @param logList raw list of Logs
     * @return frequency map by hour
     */
    private static Map<LogData, Integer> generateStatsByHour(final List<LogData> logList) {
        int counter = 1;
        final Map<LogData, Integer> tempMap = new LinkedHashMap<>();
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
                    tempMap.put(logEntry, counter);
                    counter = 1;
                }
            } else {
                tempMap.put(logEntry, counter);
            }
        }
        return tempMap;
    }


    /**
     * Generates frequency map by minute
     *
     * @param logList raw list of Logs
     * @return frequency map by minute
     */
    private static Map<LogData, Integer> generateStatsByMinute(final List<LogData> logList) {
        int counter = 1;
        final Map<LogData, Integer> tempMap = new LinkedHashMap<>();
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
                    tempMap.put(logEntry, counter);
                    counter = 1;
                }
            } else {
                tempMap.put(logEntry, counter);
            }
        }
        return tempMap;
    }


    /**
     * Writes hour stats into file
     *
     * @param tempMap frequency map
     * @throws IOException
     */
    private static void writeToFile(Map<LogData, Integer> tempMap, String timeFrameChosen) throws IOException {
        final FileWriter writer = new FileWriter(Constants.FILE_NAME, true);
        String pattern = (timeFrameChosen.equals(Constants.MINUTE_PICKER)) ? Constants.MINUTE_FORMAT : Constants.HOUR_FORMAT;
        for (Map.Entry<LogData, Integer> e : tempMap.entrySet()) {
            final String displayDate = e.getKey().getDate()
                    .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
            final String timestamp = e.getKey().getDate()
                    .format(DateTimeFormatter.ofPattern(pattern));
            String timestampPlusOne = "";
            if (timeFrameChosen.equals(Constants.MINUTE_PICKER)) {
                timestampPlusOne = e.getKey().getDate().plusMinutes(1)
                        .format(DateTimeFormatter.ofPattern(pattern));
            } else if (timeFrameChosen.equals(Constants.HOUR_PICKER)) {
                timestampPlusOne = e.getKey().getDate().plusHours(1)
                        .format(DateTimeFormatter.ofPattern(pattern));
            }
            final String data = displayDate + " "
                    + timestamp + "-"
                    + timestampPlusOne
                    + " "
                    + timeFrameChosen
                    + "s, количество ошибок " + e.getValue() + "\n";
            writer.write(data);
        }
        writer.close();
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
