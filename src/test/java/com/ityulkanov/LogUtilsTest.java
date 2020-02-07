package com.ityulkanov;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogUtilsTest {
    private static final String DATE_SAMPLE = "2019-01-01T00:12:01.001";
    private static final String LOGGER_LEVEL = "ERROR";
    LogData logData;
    LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        this.localDateTime = LocalDateTime.parse(DATE_SAMPLE,
                DateTimeFormatter.ofPattern(Constants.PARSER_FORMAT));
        logData = new LogData(localDateTime, LOGGER_LEVEL);
    }

    @org.junit.jupiter.api.Test
    void generateLogListandReturnLogDataTest() {
        assertEquals(localDateTime, LogUtils.convertDate(DATE_SAMPLE));

    }

}