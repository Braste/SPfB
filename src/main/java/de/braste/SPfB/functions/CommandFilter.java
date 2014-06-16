package de.braste.SPfB.functions;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class CommandFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {

        if(record.getMessage().contains("issued server command: /login")) {
            return false;
        }
        else {
            return true;
        }
    }
}
