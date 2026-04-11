package view;

import java.util.Arrays;
import javax.swing.SwingUtilities;

/**
 * Temporary demo class to preview the GUI layout. Run this main method to see the window. Delete
 * this file before final submission.
 */
public class ViewDemo {

  /**
   * Demo-only: mirrors {@code description} strings for starter inventory items in {@code
   * resources/alignquest.json}. The real controller should pass the item description from the
   * parsed model instead.
   */
  private static String demoInspectDescriptionForItem(String itemName) {
    if (itemName == null) {
      return "";
    }
    return switch (itemName) {
      case "Lamp" -> "An old oil lamp with flint to spark.";
      case "Thumb Drive" -> "A USB thumb drive for computers";
      case "Modulo 2" ->
          "A old school floppy disk. The kind your parents used. \n"
              + "It has \"Computer Science (number mod 2) operator. Use in case of emergencies\" "
              + "inscribed on it. Wait - it just morphed into a cloud. Hold on - it just turned "
              + "back into a disk. Weird!";
      default -> "Details of " + itemName;
    };
  }

  /**
   * Demo-only: mirrors {@code picture} fields for these items in {@code resources/alignquest.json}.
   * Production code should take the filename from the model, not this switch.
   */
  private static String demoInspectImageForItem(String itemName) {
    if (itemName == null) {
      return null;
    }
    return switch (itemName) {
      case "Lamp" -> "lamp.png";
      case "Thumb Drive", "Modulo 2" -> null;
      default -> null;
    };
  }

  /** Launches the main frame with sample data for visual testing. */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          MainFrame frame = new MainFrame("Align Quest");

          String playerName =
              frame.promptInput("Avatar Name", "Enter a name for your Player Avatar:");
          if (playerName == null || playerName.isBlank()) {
            System.exit(0);
          }
          frame.showMessage("Welcome", "You shalt now be named: " + playerName.toUpperCase());

          // Matches updateNavigationButtons below (demo-only; real controller uses model exits).
          final boolean northOpen = true;
          final boolean southOpen = false;
          final boolean eastOpen = false;
          final boolean westOpen = false;

          frame.setListener(
              new ViewListener() {
                @Override
                public void onMove(String direction) {
                  boolean allowed =
                      switch (direction) {
                        case "N" -> northOpen;
                        case "S" -> southOpen;
                        case "E" -> eastOpen;
                        case "W" -> westOpen;
                        default -> false;
                      };
                  if (!allowed) {
                    frame.showMessage("Move", "You cannot go that direction.");
                  } else {
                    frame.showMessage("Move", "You went " + direction + ".");
                  }
                }

                @Override
                public void onTake() {
                  frame.showMessage("Take", "Take button clicked");
                }

                @Override
                public void onExamine() {
                  String choice =
                      frame.promptSelection("Select", Arrays.asList("Ticket", "Billboard", "Me"));
                  if (choice != null) {
                    if (choice.equals("Me")) {
                      frame.showInspectDialog(
                          "Inspecting...",
                          "Sir Mix A Lot. I know you! You are a fearless adventurer"
                              + " embarking on an amazing quest.",
                          "epic_adventurer.png");
                    } else if (choice.equals("Ticket")) {
                      frame.showInspectDialog(
                          "Inspecting...",
                          "A complimentary museum ticket. It says ADMIT ONE, pwd = Align.",
                          null);
                    } else {
                      frame.showInspectDialog(
                          "Inspecting...", "A large billboard looms in the distance.", "billboard.png");
                    }
                  }
                }

                @Override
                public void onAnswer() {
                  String answer = frame.promptInput("Answer", "Enter your answer:");
                  if (answer != null) {
                    frame.showMessage("Answer Result", "You answered: " + answer);
                  }
                }

                @Override
                public void onInspect() {
                  String item = frame.getSelectedInventoryItem();
                  if (item != null) {
                    frame.showInspectDialog(
                        "Inspecting...",
                        demoInspectDescriptionForItem(item),
                        demoInspectImageForItem(item));
                  } else {
                    frame.showMessage("Inspect", "Select an item first.");
                  }
                }

                @Override
                public void onUse() {
                  String item = frame.getSelectedInventoryItem();
                  if (item != null) {
                    frame.showMessage("Using: " + item, "SUCCESS! You used " + item);
                  } else {
                    frame.showMessage("Use", "Select an item first.");
                  }
                }

                @Override
                public void onDrop() {
                  String item = frame.getSelectedInventoryItem();
                  if (item != null) {
                    frame.showMessage("Drop", "Dropped " + item);
                  } else {
                    frame.showMessage("Drop", "Select an item first.");
                  }
                }

                @Override
                public void onSave() {
                  frame.showMessage("Save Game Complete", "Saved your current game.");
                }

                @Override
                public void onRestore() {
                  frame.showMessage("Restore", "Game restored.");
                }

                @Override
                public void onAbout() {
                  frame.showAbout(ImageUtils.TEAM_ABOUT_LOGO_PATH);
                }

                @Override
                public void onExit() {
                  frame.showGameOver(playerName, 305, "nighty_night.png");
                  System.exit(0);
                }
              });

          frame.updateRoomImage("courtyard.png");
          frame.updateDescription(
              "A beautiful courtyard with flowers on both sides of the stone walkway.\n"
                  + "The walkway leads north. A billboard is in the distance.\n"
                  + "Items you see here: HAIR CLIPPERS");
          frame.updateInventory(Arrays.asList("Thumb Drive", "Modulo 2", "Lamp"));
          frame.updateNavigationButtons(true, false, false, false);
          frame.updateHealthStatus("You are still healthy and wide awake.");

          frame.display();
        });
  }
}
