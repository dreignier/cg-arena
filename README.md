# cg-arena
CodingGame arena emulator

This arena is mean to emulate the behavior of an arena for the [CodinGame website](https://www.codingame.com).

# Usage

The arena is a command line program. With this arguments :

## -e &lt;engine>

The engine to use for the arena. Not all engine of CodinGame are supported at the moment. Supported engines :

* tron
 
## -n &lt;games>

Number of games to play. 1 by default.

## -t &lt;thread>

Number of thread to spawn. 1 by default.

## -pX &lt;command line>

The command line to start the process of the player X (starting with player 1). You must give at least 2 players to the arena.

## -v

For debug purpose. The log level will be set to ALL. Prepare to be spammed.

# Example of Usage

    java -jar cg-arena.jar -e tron -n 50 -t 2 -p1 "./tron1.exe" -p2 "php tron.php" -p3 "java -jar /folder/tron.jar"
