package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Panel displaying the player's inventory and health status. Contains a selectable list of items,
 * action buttons (Inspect, Use, Drop), and a health status label. Located in the bottom-right
 * quadrant of the main window.
 */
public class InventoryPanel extends JPanel {

  private static final int PREF_WIDTH = 380;
  private static final int PREF_HEIGHT = 250;

  private final DefaultListModel<String> listModel;
  private final JList<String> itemList;
  private final RoundedButton inspectButton;
  private final RoundedButton useButton;
  private final RoundedButton dropButton;
  private final JLabel statusLabel;

  private ViewListener listener;

  /** Creates the inventory panel with item list, action buttons, and status label. */
  public InventoryPanel() {
    setLayout(new BorderLayout(0, 5));
    setBorder(TeamUiTheme.rightQuadrantBorder("Inventory"));
    setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

    listModel = new DefaultListModel<>();
    itemList = new JList<>(listModel);
    itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    itemList.setFont(new Font("SansSerif", Font.PLAIN, 13));
    TeamUiTheme.applyListSelectionTheme(itemList);

    JScrollPane scrollPane = new JScrollPane(itemList);
    scrollPane.setPreferredSize(new Dimension(PREF_WIDTH - 20, 100));
    add(scrollPane, BorderLayout.CENTER);

    inspectButton = new RoundedButton("Inspect", RoundedButton.Appearance.TEAM_RIGHT);
    useButton = new RoundedButton("Use", RoundedButton.Appearance.TEAM_RIGHT);
    dropButton = new RoundedButton("Drop", RoundedButton.Appearance.TEAM_RIGHT);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
    buttonPanel.add(inspectButton);
    buttonPanel.add(useButton);
    buttonPanel.add(dropButton);

    statusLabel = new JLabel("You are still healthy and wide awake.");
    statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
    statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(buttonPanel, BorderLayout.NORTH);
    bottomPanel.add(statusLabel, BorderLayout.SOUTH);

    add(bottomPanel, BorderLayout.SOUTH);

    wireListeners();
  }

  /**
   * Registers the view listener for button events.
   *
   * @param listener the controller listener
   */
  public void setListener(ViewListener listener) {
    this.listener = listener;
  }

  /**
   * Replaces the inventory list contents with the given item names.
   *
   * @param items the current inventory item names
   */
  public void updateItems(List<String> items) {
    listModel.clear();
    if (items != null) {
      for (String item : items) {
        listModel.addElement(item);
      }
    }
  }

  /**
   * Updates the health status label.
   *
   * @param text the status text to display
   */
  public void updateStatus(String text) {
    statusLabel.setText(text);
  }

  /**
   * Returns the currently selected item name in the list.
   *
   * @return the selected item name, or {@code null} if nothing is selected
   */
  public String getSelectedItem() {
    return itemList.getSelectedValue();
  }

  private void wireListeners() {
    inspectButton.addActionListener(e -> {
      if (listener != null) listener.onInspect();
    });
    useButton.addActionListener(e -> {
      if (listener != null) listener.onUse();
    });
    dropButton.addActionListener(e -> {
      if (listener != null) listener.onDrop();
    });
  }
}
