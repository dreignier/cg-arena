package com.magusgeek.cg.arena.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Engine {
    private static final Log LOG = LogFactory.getLog(Engine.class);

    private List<String> commandLines;
    private List<Process> process;

    // Reminder - Keep an empty constructor. We are instanciated by reflection
    public Engine() {
        process = new ArrayList<>();
    }

    public void setCommandLines(List<String> commandLines) {
        this.commandLines = commandLines;
    }
    
    public void start() {
        LOG.info("Starting engine");
        
        for (int i = 0; i < commandLines.size(); ++i) {
            LOG.info("Starting player " + (i + 1) + " process");
            try {
                process.add(new ProcessBuilder(commandLines.get(i).split(" ")).start());
            } catch (IOException exception) {
                LOG.fatal("Unable to start player " + (i + 1) + " process", exception);
                process.forEach(p -> p.destroy());
                System.exit(1);
            }
        }
    }
}
