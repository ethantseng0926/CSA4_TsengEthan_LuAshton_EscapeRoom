/*
* Problem 1: Escape Room
* 
* V1.0
* 10/10/2019
* Copyright(c) 2019 PLTW to present. All rights reserved
*/
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Create an escape room game where the player must navigate
 * to the other side of the screen in the fewest steps, while
 * avoiding obstacles and collecting prizes.
 */
public class EscapeRoom
{
  /* Process game commands from user input:
      right, left, up, down: move player size of move, m, if player try to go off grid or bump into wall, score decreases
      jump over 1 space: player cannot jump over walls
      pick up prize: score increases, if there is no prize, penalty
      help: display all possible commands
      end: reach the far right wall, score increase, game ends, if game ends without reaching far right wall, penalty
      replay: shows number of player steps and resets the board, player or another player can play the same board
        
      if player land on a trap, spring a trap to increase score: the program must first check if there is a trap, if none exists, penalty
      Note that you must adjust the score with any method that returns a score
      Optional: create a custom image for player - use the file player.png on disk
    */

  public static void main(String[] args) 
  {      
    // welcome message
    System.out.println("Welcome to EscapeRoom!");
    System.out.println("Get to the other side of the room, avoiding walls and invisible traps,");
    System.out.println("pick up all the prizes.\n");
    System.out.println("You have 30 seconds to escape! Timer starts now!");
    
    GameGUI game = new GameGUI();
    game.createBoard();

    // size of move
    int m = 60; 
    
    int score = 0;
    boolean gameWon = false;
    boolean gameLost = false;
    boolean timeUp = false;
    int targetScore = 30; // Score needed to win the game
    int timeLimit = 30; // 30 second timer
    int trapPenalty = 10; // Points lost when trapped
    long startTime = System.currentTimeMillis();

    Scanner in = new Scanner(System.in);
    String[] validCommands = { "right", "left", "up", "down", "r", "l", "u", "d",
    "jump", "jr", "jumpleft", "jl", "jumpup", "ju", "jumpdown", "jd",
    "pickup", "p", "quit", "q", "replay", "help", "?", 
    "findtrap right", "ftr", "findtrap left", "ftl", "findtrap up", "ftu", "findtrap down", "ftd",
    "removetrap right", "rtr", "removetrap left", "rtl", "removetrap up", "rtu", "removetrap down", "rtd"};

    // Set up timer for 30 seconds
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        timeUp = true;
        System.out.println("\nTIME'S UP! Game over!");
      }
    }, timeLimit * 1000);
  
    // set up game
    boolean play = true;
    while (play && !timeUp)
    {
      // Calculate remaining time
      long currentTime = System.currentTimeMillis();
      long elapsedTime = (currentTime - startTime) / 1000;
      int remainingTime = timeLimit - (int)elapsedTime;
      
      // Display current score, steps, and time
      System.out.println("Current Score: " + score + " | Steps: " + game.getSteps() + " | Time left: " + remainingTime + "s");
      System.out.println("Target Score to Win: " + targetScore);

      // get user command and validate
      System.out.print("Enter command: ");
      String input = UserInput.getValidInput(validCommands);

      /* process user commands*/
      int result = 0;
      
      // Basic movement commands
      if (input.equals("right") || input.equals("r")) {
        result = game.movePlayer(m, 0);
        if (result < 0) {
          System.out.println("Cannot move right! Penalty applied.");
        }
      }
      else if (input.equals("left") || input.equals("l")) {
        result = game.movePlayer(-m, 0);
        if (result < 0) {
          System.out.println("Cannot move left! Penalty applied.");
        }
      }
      else if (input.equals("up") || input.equals("u")) {
        result = game.movePlayer(0, -m);
        if (result < 0) {
          System.out.println("Cannot move up! Penalty applied.");
        }
      }
      else if (input.equals("down") || input.equals("d")) {
        result = game.movePlayer(0, m);
        if (result < 0) {
          System.out.println("Cannot move down! Penalty applied.");
        }
      }
      // Jump commands - now can jump over walls but not off screen
      else if (input.equals("jr") || input.equals("jumpright")) {
        // Check if jump is possible (won't go off screen)
        if (game.canJump(m, 0)) {
          result = game.jumpPlayer(m, 0);
          System.out.println("Jumped right over obstacles!");
        } else {
          result = -5; // Penalty for attempting jump off screen
          System.out.println("Cannot jump right! Would go off screen. Penalty applied.");
        }
      }
      else if (input.equals("jl") || input.equals("jumpleft")) {
        if (game.canJump(-m, 0)) {
          result = game.jumpPlayer(-m, 0);
          System.out.println("Jumped left over obstacles!");
        } else {
          result = -5;
          System.out.println("Cannot jump left! Would go off screen. Penalty applied.");
        }
      }
      else if (input.equals("ju") || input.equals("jumpup")) {
        if (game.canJump(0, -m)) {
          result = game.jumpPlayer(0, -m);
          System.out.println("Jumped up over obstacles!");
        } else {
          result = -5;
          System.out.println("Cannot jump up! Would go off screen. Penalty applied.");
        }
      }
      else if (input.equals("jd") || input.equals("jumpdown")) {
        if (game.canJump(0, m)) {
          result = game.jumpPlayer(0, m);
          System.out.println("Jumped down over obstacles!");
        } else {
          result = -5;
          System.out.println("Cannot jump down! Would go off screen. Penalty applied.");
        }
      }
      // Pick up prize
      else if (input.equals("pickup") || input.equals("p")) {
        result = game.pickupPrize();
        if (result > 0) {
          System.out.println("Prize collected! +" + result + " points");
        } else {
          System.out.println("No prize here! Penalty applied.");
        }
      }
      // Find trap in specific directions
      else if (input.equals("findtrap right") || input.equals("ftr")) {
        if (game.isTrap(m, 0)) {
          System.out.println("There is a trap to the right! Be careful.");
        } else {
          System.out.println("No trap detected to the right.");
        }
        result = 0;
      }
      else if (input.equals("findtrap left") || input.equals("ftl")) {
        if (game.isTrap(-m, 0)) {
          System.out.println("There is a trap to the left! Be careful.");
        } else {
          System.out.println("No trap detected to the left.");
        }
        result = 0;
      }
      else if (input.equals("findtrap up") || input.equals("ftu")) {
        if (game.isTrap(0, -m)) {
          System.out.println("There is a trap above! Be careful.");
        } else {
          System.out.println("No trap detected above.");
        }
        result = 0;
      }
      else if (input.equals("findtrap down") || input.equals("ftd")) {
        if (game.isTrap(0, m)) {
          System.out.println("There is a trap below! Be careful.");
        } else {
          System.out.println("No trap detected below.");
        }
        result = 0;
      }
      // Remove trap in specific directions
      else if (input.equals("removetrap right") || input.equals("rtr")) {
        if (game.isTrap(m, 0)) {
          result = game.springTrap(m, 0);
          System.out.println("Trap to the right removed! +" + result + " points");
        } else {
          result = -5;
          System.out.println("No trap to the right to remove! Penalty applied.");
        }
      }
      else if (input.equals("removetrap left") || input.equals("rtl")) {
        if (game.isTrap(-m, 0)) {
          result = game.springTrap(-m, 0);
          System.out.println("Trap to the left removed! +" + result + " points");
        } else {
          result = -5;
          System.out.println("No trap to the left to remove! Penalty applied.");
        }
      }
      else if (input.equals("removetrap up") || input.equals("rtu")) {
        if (game.isTrap(0, -m)) {
          result = game.springTrap(0, -m);
          System.out.println("Trap above removed! +" + result + " points");
        } else {
          result = -5;
          System.out.println("No trap above to remove! Penalty applied.");
        }
      }
      else if (input.equals("removetrap down") || input.equals("rtd")) {
        if (game.isTrap(0, m)) {
          result = game.springTrap(0, m);
          System.out.println("Trap below removed! +" + result + " points");
        } else {
          result = -5;
          System.out.println("No trap below to remove! Penalty applied.");
        }
      }
      // Help
      else if (input.equals("help") || input.equals("?")) {
        System.out.println("Available commands:");
        System.out.println("Movement: right(r), left(l), up(u), down(d)");
        System.out.println("Jump: jumpright(jr), jumpleft(jl), jumpup(ju), jumpdown(jd)");
        System.out.println("Actions: pickup(p)");
        System.out.println("Trap Detection: findtrap right(ftr), findtrap left(ftl), findtrap up(ftu), findtrap down(ftd)");
        System.out.println("Trap Removal: removetrap right(rtr), removetrap left(rtl), removetrap up(rtu), removetrap down(rtd)");
        System.out.println("Game: replay, quit(q)");
        System.out.println("Note: Jumps can go over walls but not off screen!");
        System.out.println("Warning: Landing on a trap costs " + trapPenalty + " points!");
        result = 0;
      }
      // Replay
      else if (input.equals("replay")) {
        result = game.replay();
        score = 0; // Reset score for new game
        gameWon = false;
        gameLost = false;
        startTime = System.currentTimeMillis(); // Reset timer
        System.out.println("Game reset! Starting new game.");
        continue;
      }
      // Quit
      else if (input.equals("quit") || input.equals("q")) {
        play = false;
        System.out.println("Thanks for playing!");
        continue;
      }
      
      // Update score
      score += result;
      
      // Check for trap landing - NEW: Lose points when landing on traps
      if (result >= 0 && (input.equals("right") || input.equals("r") || input.equals("left") || input.equals("l") || 
          input.equals("up") || input.equals("u") || input.equals("down") || input.equals("d") ||
          input.equals("jr") || input.equals("jumpright") || input.equals("jl") || input.equals("jumpleft") ||
          input.equals("ju") || input.equals("jumpup") || input.equals("jd") || input.equals("jumpdown"))) {
        if (game.isTrap(0, 0)) {
          // Player landed on a trap - apply penalty
          score -= trapPenalty;
          System.out.println("TRAPPED! You stepped on a trap and lost " + trapPenalty + " points!");
          // Spring the trap so it can't trap again
          game.springTrap(0, 0);
        }
      }
      
      // Check win condition (reach target score and far right side)
      if (score >= targetScore && game.playerAtEnd() > 0) {
        gameWon = true;
        play = false;
        timer.cancel(); // Stop the timer
        System.out.println("Congratulations! You won the game!");
      }
      
      // Check lose condition (trapped with negative score or time up)
// Check lose condition (time up or too many penalties)
if (timeUp) {
    gameLost = true;
    play = false;
    System.out.println("Time's up! You failed to escape in time.");
} else if (score < -20) {
    gameLost = true;
    play = false;
    System.out.println("Game Over! You have had way too many penalties.");
}
      // Check if player reached the end but doesn't have enough points
      if (game.playerAtEnd() > 0 && score < targetScore) {
        System.out.println("You reached the end, but you need " + (targetScore - score) + " more points to win!");
      }

      // Repaint the game board to show updates
      game.repaint();
    }

    // Cancel timer if game ended early
    timer.cancel();

    if (!gameWon && !gameLost && !timeUp) {
      score += game.endGame();
    }

    System.out.println("Final score: " + score);
    System.out.println("Total steps: " + game.getSteps());
    System.out.println("Time taken: " + (int)((System.currentTimeMillis() - startTime) / 1000) + " seconds");
    
    if (gameWon) {
      System.out.println("You successfully escaped the room!");
    } else if (gameLost) {
      if (timeUp) {
        System.out.println("You failed to escape within the time limit.");
      } else {
        System.out.println("You failed to escape the room.");
      }
    } else {
      System.out.println("You quit the game.");
    }
  }
}
