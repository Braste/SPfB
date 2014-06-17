package de.braste.SPfB.functions;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class CommandFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        if(record.getMessage().contains("issued server command: /login")) {
            int endIndex = record.getMessage().indexOf("/login");
            record.setMessage(record.getMessage().substring(0, endIndex + 6));
        }
        record.setMessage(record.getMessage() + "1");
        return true;
    }
}
