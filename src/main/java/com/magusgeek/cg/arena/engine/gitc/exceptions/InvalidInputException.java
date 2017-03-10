package com.magusgeek.cg.arena.engine.gitc.exceptions;

public class InvalidInputException extends Exception {

  public InvalidInputException(String message1, String message2) {
    super ("waiting : "+message1+" got : "+message2);
  }

}
