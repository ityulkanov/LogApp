package com.ityulkanov;

import lombok.experimental.UtilityClass;

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
    private static Map<LocalDateTime, Integer> generateStatsByHour(final List<LogData> logList) {
        final Map<LocalDateTime, Integer> tempMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.HOUR_FORMATTER);
        return getMap(logList, formatter, tempMap);
    }


    /**
     * Generates frequency map by minute
     *
     * @param logList raw list of Logs
     * @return frequency map by minute
     */
    private static Map<LocalDateTime, Integer> generateStatsByMinute(final List<LogData> logList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.MINUTE_FORMATTER);
        final Map<LocalDateTime, Integer> tempMap = new LinkedHashMap<>();
        return getMap(logList, formatter, tempMap);
    }

    private static Map<LocalDateTime, Integer> getMap(List<LogData> logList,
                                                      DateTimeFormatter formatter,
                                                      Map<LocalDateTime, Integer> tempMap) {
        logList.forEach(a -> {
            LocalDateTime value = LocalDateTime.parse(a.getDate().format(formatter), formatter);
            int count = tempMap.getOrDefault(value, 0);
            tempMap.put(value, count + 1);
        });
        return tempMap;
    }


    /**
     * Writes hour stats into file
     *
     * @param tempMap frequency map
     * @throws IOException
     */
    private static void writeToFile(Map<LocalDateTime, Integer> tempMap,
                                    String timeFrameChosen) throws IOException {
        final FileWriter writer = new FileWriter(Constants.FILE_NAME, true);
        String pattern = (timeFrameChosen.equals(Constants.MINUTE_PICKER)) ? Constants.MINUTE_FORMAT : Constants.HOUR_FORMAT;
        for (Map.Entry<LocalDateTime, Integer> e : tempMap.entrySet()) {
            final String displayDate = e.getKey()
                    .format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
            final String timestamp = e.getKey()
                    .format(DateTimeFormatter.ofPattern(pattern));
            String timestampPlusOne = "";
            if (timeFrameChosen.equals(Constants.MINUTE_PICKER)) {
                timestampPlusOne = e.getKey().plusMinutes(1)
                        .format(DateTimeFormatter.ofPattern(pattern));
            } else if (timeFrameChosen.equals(Constants.HOUR_PICKER)) {
                timestampPlusOne = e.getKey().plusHours(1)
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
