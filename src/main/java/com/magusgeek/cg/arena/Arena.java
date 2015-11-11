package com.magusgeek.cg.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import com.magusgeek.cg.arena.engine.Engine;

public class Arena {

    private static final Log LOG = LogFactory.getLog(Arena.class);

    public static void main(String[] args) {
        try {
            Options options = new Options();

            options.addOption("h", false, "Print the help");
            options.addOption("v", false, "If given, cg-arena will be very talkative");
            options.addOption("e", true, "The engine to use");
            options.addOption("n", true, "Number of games to play. Default 1");
            options.addOption("p1", true, "Command line for player 1 program");
            options.addOption("p2", true, "Command line for player 2 program");
            options.addOption("p3", true, "Command line for player 3 program");
            options.addOption("p4", true, "Command line for player 4 program");

            CommandLine cmd = new DefaultParser().parse(options, args);
            
            // Need help ?
            if (cmd.hasOption("h")) {
                new HelpFormatter().printHelp("-e <engine> -n <games> -p1 <player1 command line> -p2 <player2 command line> -p3 <player3 command line> -p4 <player4 command line> [-v]", options);
                System.exit(0);
            }

            // Verbose mode
            boolean verbose = cmd.hasOption("v");
            if (verbose) {
                Configurator.setRootLevel(Level.ALL);
                LOG.info("Verbose mode activated");
            }

            // Retrieve the engine
            String engineName = cmd.getOptionValue("e");
            if (engineName == null) {
                LOG.fatal("An engine must be provided. Use -e to give it");
                System.exit(1);
            }
            
            Properties engines = new Properties();
            engines.load(Arena.class.getClassLoader().getResourceAsStream("com/magusgeek/cg/arena/engines.properties"));
            
            if (!engines.containsKey(engineName)) {
                LOG.fatal("Unrecognized engine name : " + engineName);
                LOG.fatal("Possible values :");
                
                for (Object key : engines.keySet()) {
                    LOG.fatal("- " + key);
                }
            }
            
            LOG.info("Engine : " + engineName);

            // Get command lines for payers
            List<String> playersCommandLines = new ArrayList<>();
            for (int i = 1; i < 5; ++i) {
                String p = cmd.getOptionValue("p" + i);

                if (p != null) {
                    playersCommandLines.add(p);
                }
            }
            
            if (playersCommandLines.size() < 2) {
                LOG.fatal("At least 2 players are required, only " + playersCommandLines.size() + " given");
                System.exit(1);
            }
            
            for (int i = 0; i < playersCommandLines.size(); ++i) {
                LOG.info("Player " + (i + 1) + " command line : " + playersCommandLines.get(i));
            }
            
            // Game count
            int n = 1;
            try {
                n = Integer.valueOf(cmd.getOptionValue("n"));
            } catch (Exception exception) {
                
            }
            
            LOG.info("Number of games to play : " + n);
            
            Class<?> clazz = Class.forName(engines.getProperty(engineName));
            
            // Create the engine
            for (int i = 0; i < n; ++i) {
                Engine engine = (Engine) clazz.newInstance();
                engine.setCommandLines(playersCommandLines);
                GameResult result = engine.start();
                
                LOG.info("Winner : " + (result.getWinner() + 1));
            }
        } catch (Exception exception) {
            LOG.fatal("cg-arena fail to start", exception);
            System.exit(1);
        }
    }

}
