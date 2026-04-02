package controller;

import java.io.IOException;
import java.util.Scanner;

import model.IGameModel;


/**
 * Console controller for the game.
 * Reads user input, sends commands to the model, and writes output.
 */
public class GameController {
	private final IGameModel model;
	private final Readable source;
	private final Appendable output;
	
	private boolean hasWon = false;
	
	/**
	 * Constructs a controller.
	 *
	 * @param model the game model
	 * @param source the input source
	 * @param output the output target
	 */
	public GameController(IGameModel model, Readable source, Appendable output) {
		if (model == null || source == null || output == null) {
			throw new IllegalArgumentException("Model, source, and output cannot be null.");
		}
		this.model = model;
		this.source = source;
		this.output = output;
	}
	
	/**
	 * Starts the game loop.
	 *
	 * @throws IOException if output fails
	 */
	public void play() throws IOException {
		Scanner scan = new Scanner(this.source);
		
		writeLine("Welcome to the game!");
		write("Enter your player name: ");
		
		if (!scan.hasNextLine()) {
			return;
		}
		
		String playerName = scan.nextLine().trim();
		if (playerName.isEmpty()) {
			playerName = "Player";
		}
		model.setPlayerName(playerName);
		
		writeLine("");
		writeLine("Game started.");
		model.look();
		
		while (!model.isGameOver() && !hasWon) {
			writeLine("");
			writeLine("Commands: N S E W | L(look) | I(inventory) | T(take) | D(drop) | "
					+ "X(examine) | U(use) | A(answer) | Save | Restore | Q(quit)");
			write("> ");
			
			if (!scan.hasNext()) {
				break;
			}
			
			String command = scan.next().trim();
			
			if (command.equalsIgnoreCase("q") || command.equalsIgnoreCase("quit")) {
				writeLine("Quitting game.");
				return;
			}
			
			try {
				handleCommand(command, scan);
			} catch (IllegalArgumentException | IllegalStateException e) {
				writeLine("Error: " + e.getMessage());
			}
		}
		
		writeLine("");
		if (hasWon) {
			writeLine("Congratulations! You won!");
		} else if (model.isGameOver()) {
			writeLine("Game over. You fell asleep.");
		}
	}
	
	/**
	 * Handles one user command.
	 */
	private void handleCommand(String command, Scanner scan) throws IOException {
		switch (command.toLowerCase()) {
			case "n":
			case "north":
				writeLine(model.move("N"));
				break;
			
			case "s":
			case "south":
				writeLine(model.move("S"));
				break;
			
			case "e":
			case "east":
				writeLine(model.move("E"));
				break;
			
			case "w":
			case "west":
				writeLine(model.move("W"));
				break;
			
			case "l":
			case "look":
				writeLine(model.look());
				break;
			
			case "i":
			case "inventory":
				writeLine(model.getInventoryString());
				break;
			
			case "t":
			case "take":
				handleTake(scan);
				break;
			
			case "d":
			case "drop":
				handleDrop(scan);
				break;
			
			case "x":
			case "examine":
				handleExamine(scan);
				break;
			
			case "u":
			case "use":
				handleUse(scan);
				break;
			
			case "a":
			case "answer":
				handleAnswer(scan);
				break;
			
			case "save":
				handleSave(scan);
				break;
			
			case "restore":
				handleRestore(scan);
				break;
			case "q":
			case "quit":
				// Handled in play() loop
				this.hasWon = true; // Force exit
			default:
				writeLine("Unknown command.");
		}
	}
	
	private void handleTake(Scanner scan) throws IOException {
		if (!scan.hasNextLine()) {
			writeLine("Missing item name.");
			return;
		}
		String itemName = scan.nextLine().trim();
		if (itemName.isEmpty()) {
			writeLine("Usage: take <item name>");
			return;
		}
		writeLine(model.takeItem(itemName));
	}
	
	private void handleDrop(Scanner scan) throws IOException {
		if (!scan.hasNextLine()) {
			writeLine("Missing item name.");
			return;
		}
		String itemName = scan.nextLine().trim();
		if (itemName.isEmpty()) {
			writeLine("Usage: drop <item name>");
			return;
		}
		writeLine(model.dropItem(itemName));
	}
	
	private void handleExamine(Scanner scan) throws IOException {
		if (!scan.hasNextLine()) {
			writeLine("Missing object name.");
			return;
		}
		String target = scan.nextLine().trim();
		if (target.isEmpty()) {
			writeLine("Usage: examine <item or fixture name>");
			return;
		}
		writeLine(model.examine(target));
	}
	
	private void handleUse(Scanner scan) throws IOException {
		if (!scan.hasNextLine()) {
			writeLine("Missing item name.");
			return;
		}
		String itemName = scan.nextLine().trim();
		if (itemName.isEmpty()) {
			writeLine("Usage: use <item name>");
			return;
		}
		writeLine(model.useItem(itemName));
	}
	
	private void handleAnswer(Scanner scan) throws IOException {
		if (!scan.hasNextLine()) {
			writeLine("Missing answer text.");
			return;
		}
		String answer = scan.nextLine().trim();
		if (answer.isEmpty()) {
			writeLine("Usage: answer <text>");
			return;
		}
		writeLine(model.answerPuzzle(answer));
	}
	
	private void handleSave(Scanner scan) throws IOException {
		model.save();
		writeLine("Game saved.");
	}
	
	private void handleRestore(Scanner scan) throws IOException {
		model.restore();
		writeLine("Game restored.");
	}
	
	/**
	 * Renders the current visible game state.
	 */

	
	private void write(String s) throws IOException {
		output.append(s);
	}
	
	private void writeLine(String s) throws IOException {
		output.append(s).append(System.lineSeparator());
	}
}
