package com.magusgeek.cg.arena.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.magusgeek.cg.arena.GameResult;

public abstract class Engine {
    private static final Log LOG = LogFactory.getLog(Engine.class);

    private List<String> commandLines;
    protected List<Process> process;
    protected int turn;
    protected boolean end;
    protected GameResult result;

    // Reminder - Keep an empty constructor. We are instanciated by reflection
    public Engine() {
        process = new ArrayList<>();
    }

    public void setCommandLines(List<String> commandLines) {
        this.commandLines = commandLines;
    }

    public GameResult start() {
        boolean debug = LOG.isDebugEnabled();

        if (debug) {
            LOG.debug("Starting engine");
        }

        for (int i = 0; i < commandLines.size(); ++i) {
            if (debug) {
                LOG.debug("Starting player " + (i + 1) + " process");
            }
            
            try {
                process.add(new ProcessBuilder(commandLines.get(i).split(" ")).start());
            } catch (IOException exception) {
                LOG.fatal("Unable to start player " + (i + 1) + " process", exception);
                destroyAll();
                System.exit(1);
            }
        }

        try {
            if (debug) {
                LOG.debug("Initializing");
            }
            
            result = new GameResult();

            initialize();

            if (debug) {
                LOG.debug("Playing loop");
            }

            turn = 1;
            while (!end) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Playing turn " + turn);
                }
                play();

                turn += 1;
            }
        } catch (Exception exception) {
            LOG.fatal("Exception during the play", exception);
            destroyAll();
            System.exit(1);
        }
        
        return result;
    }

    public void destroyAll() {
        process.forEach(p -> p.destroy());
    }

    abstract protected void play() throws Exception;

    abstract protected void initialize() throws Exception;
}
