package org.tech.repos.base.lib.log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: xuan
 * Created on 2021/3/18 15:12.
 * <p>
 * Describe:
 */
public class LogManager {
    private LogConfig config;
    private static LogManager instance;
    private List<LogPrinter> printers = new ArrayList<>();

    private LogManager(LogConfig config, LogPrinter[] printers) {
        this.config = config;
        this.printers.addAll(Arrays.asList(printers));
    }

    public static LogManager getInstance() {
        return instance;
    }

    public static void init(@NonNull LogConfig config, LogPrinter... printers) {
        instance = new LogManager(config, printers);
    }

    public LogConfig getConfig() {
        return config;
    }

    public List<LogPrinter> getPrinters() {
        return printers;
    }

    public void addPrinter(LogPrinter printer) {
        printers.add(printer);
    }

    public void removePrinter(LogPrinter printer) {
        if (printers != null)
            printers.remove(printer);
    }
}
