/*
* Problem 1: Escape Room
* 
* V1.0
* 10/10/2019
* Copyright(c) 2019 PLTW to present. All rights reserved
*/
import java.util.Scanner;

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
    
    GameGUI game = new GameGUI();
    game.createBoard();

    // size of move
    int m = 60; 
    // individual player moves
    int px = 0;
    int py = 0; 
    
    int score = 0;
    boolean gameWon = false;
    boolean gameLost = false;
    int targetScore = 30; // Score needed to win the game

    Scanner in = new Scanner(System.in);
    String[] validCommands = { "right", "left", "up", "down", "r", "l", "u", "d",
    "jump", "jr", "jumpleft", "jl", "jumpup", "ju", "jumpdown", "jd",
    "pickup", "p", "quit", "q", "replay", "help", "?", "findtrap", "ft", "removetrap", "rt"};
  
    // set up game
    boolean play = true;
    while (play)
    {
      // Display current score and steps
      System.out.println("Current Score: " + score + " | Steps: " + game.getSteps());
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
      // Jump commands
      else if (input.equals("jr") || input.equals("jumpright")) {
        // Check if jump is possible (no wall in the way)
        if (game.canJump(m, 0)) {
          result = game.movePlayer(2*m, 0);
          System.out.println("Jumped right!");
        } else {
          result = -5; // Penalty for attempting impossible jump
          System.out.println("Cannot jump right! Wall in the way. Penalty applied.");
        }
      }
      else if (input.equals("jl") || input.equals("jumpleft")) {
        if (game.canJump(-m, 0)) {
          result = game.movePlayer(-2*m, 0);
          System.out.println("Jumped left!");
        } else {
          result = -5;
          System.out.println("Cannot jump left! Wall in the way. Penalty applied.");
        }
      }
      else if (input.equals("ju") || input.equals("jumpup")) {
        if (game.canJump(0, -m)) {
          result = game.movePlayer(0, -2*m);
          System.out.println("Jumped up!");
        } else {
          result = -5;
          System.out.println("Cannot jump up! Wall in the way. Penalty applied.");
        }
      }
      else if (input.equals("jd") || input.equals("jumpdown")) {
        if (game.canJump(0, m)) {
          result = game.movePlayer(0, 2*m);
          System.out.println("Jumped down!");
        } else {
          result = -5;
          System.out.println("Cannot jump down! Wall in the way. Penalty applied.");
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
      // Find trap
      else if (input.equals("findtrap") || input.equals("ft")) {
        if (game.isTrap(0, 0)) {
          System.out.println("There is a trap here! Be careful.");
        } else {
          System.out.println("No trap detected at this location.");
        }
        result = 0; // No score change for checking
      }
      // Remove trap
      else if (input.equals("removetrap") || input.equals("rt")) {
        result = game.springTrap(0, 0);
        if (result > 0) {
          System.out.println("Trap removed! +" + result + " points");
        } else {
          System.out.println("No trap to remove! Penalty applied.");
        }
      }
      // Help
      else if (input.equals("help") || input.equals("?")) {
        System.out.println("Available commands:");
        System.out.println("Movement: right(r), left(l), up(u), down(d)");
        System.out.println("Jump: jumpright(jr), jumpleft(jl), jumpup(ju), jumpdown(jd)");
        System.out.println("Actions: pickup(p), findtrap(ft), removetrap(rt)");
        System.out.println("Game: replay, quit(q)");
        result = 0;
      }
      // Replay
      else if (input.equals("replay")) {
        result = game.replay();
        score = 0; // Reset score for new game
        gameWon = false;
        gameLost = false;
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
      
      // Check for trap landing
      if (result >= 0 && (input.equals("right") || input.equals("r") || input.equals("left") || input.equals("l") || 
          input.equals("up") || input.equals("u") || input.equals("down") || input.equals("d") ||
          input.equals("jr") || input.equals("jumpright") || input.equals("jl") || input.equals("jumpleft") ||
          input.equals("ju") || input.equals("jumpup") || input.equals("jd") || input.equals("jumpdown"))) {
        if (game.isTrap(0, 0)) {
          int trapResult = game.springTrap(0, 0);
          score += trapResult;
          if (trapResult > 0) {
            System.out.println("Landed on a trap! But you sprung it for +" + trapResult + " points");
          } else {
            System.out.println("Trap malfunction! Penalty applied.");
          }
        }
      }
      
      // Check win condition (reach target score and far right side)
      if (score >= targetScore && game.playerAtEnd() > 0) {
        gameWon = true;
        play = false;
        System.out.println("Congratulations! You won the game!");
      }
      
      // Check lose condition (trapped with negative score)
      if (score < -20) {
        gameLost = true;
        play = false;
        System.out.println("Game Over! You've been trapped with too many penalties.");
      }
      
      // Check if player reached the end but doesn't have enough points
      if (game.playerAtEnd() > 0 && score < targetScore) {
        System.out.println("You reached the end, but you need " + (targetScore - score) + " more points to win!");
      }

      // Repaint the game board to show updates
      game.repaint();
    }

    if (!gameWon && !gameLost) {
      score += game.endGame();
    }

    System.out.println("Final score: " + score);
    System.out.println("Total steps: " + game.getSteps());
    
    if (gameWon) {
      System.out.println("You successfully escaped the room!");
    } else if (gameLost) {
      System.out.println("You failed to escape the room.");
    } else {
      System.out.println("You quit the game.");
    }
  }
}
