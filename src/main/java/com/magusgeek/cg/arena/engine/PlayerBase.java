package com.magusgeek.cg.arena.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class PlayerBase {
    protected int id;
    private Process process;
    private PrintStream out;
    private Scanner in;
    private InputStream error;

    public PlayerBase(Process process, int id) {
        this.process = process;
        this.id = id;

        out = new PrintStream(process.getOutputStream());
        in = new Scanner(process.getInputStream());
        error = process.getErrorStream();
    }
    
    public void cleanErrorStream() throws IOException {
       while (error.available() != 0) {
           error.read();
       }
    }

    public int getId() {
        return id;
    }

    public Process getProcess() {
        return process;
    }

    public PrintStream getOut() {
        return out;
    }

    public Scanner getIn() {
        return in;
    }
}
