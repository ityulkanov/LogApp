package com.ityulkanov;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Holds all the data from single log entry.
 */
@Data
@AllArgsConstructor
public class LogData implements Comparable<LogData> {
    private final LocalDateTime date;

    @Override
    public int compareTo(LogData o) {
        int last = this.date.compareTo(o.getDate());
        return last == 0 ? this.getDate().compareTo(o.getDate()) : last;
    }
}
