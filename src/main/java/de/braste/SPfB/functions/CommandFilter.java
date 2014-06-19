package de.braste.SPfB.functions;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class CommandFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        return !record.getMessage().toLowerCase().contains("issued server command: /login");
    }
}
