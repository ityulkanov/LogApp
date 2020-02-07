package com.ityulkanov;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Holds all the data from single log entry.
 */
@Data
@AllArgsConstructor
public class LogData implements Comparable<LogData> {
    private final LocalDateTime date;
    private final String type;

    @Override
    public int compareTo(LogData o) {
        int last = this.date.compareTo(o.getDate());
        return last == 0 ? this.getDate().compareTo(o.getDate()) : last;
    }
}
