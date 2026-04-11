package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * The main application window for the graphical game mode. Implements {@link IGameView} so the
 * controller can update all visual elements through the view interface.
 *
 * <p>Layout (2x2 grid):
 *
 * <pre>
 *   +-------------------+-------------------+
 *   |    ViewPanel       |  NavigationPanel  |
 *   |  (room image)      | (arrows + buttons)|
 *   +-------------------+-------------------+
 *   | DescriptionPanel   |  InventoryPanel   |
 *   | (room text)        | (items + status)  |
 *   +-------------------+-------------------+
 * </pre>
 */
public class MainFrame extends JFrame implements IGameView {

  private static final int WINDOW_WIDTH = 800;
  private static final int WINDOW_HEIGHT = 600;

  private final ViewPanel viewPanel;
  private final NavigationPanel navigationPanel;
  private final DescriptionPanel descriptionPanel;
  private final InventoryPanel inventoryPanel;

  private ViewListener listener;

  /**
   * Creates the main game window with all panels and the menu bar.
   *
   * @param title the window title (typically the game name)
   */
  public MainFrame(String title) {
    super(title);
    TeamUiTheme.installMenuHighlightTheme();
    setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            if (listener != null) {
              listener.onExit();
            }
          }
        });

    viewPanel = new ViewPanel();
    navigationPanel = new NavigationPanel();
    descriptionPanel = new DescriptionPanel();
    inventoryPanel = new InventoryPanel();

    JPanel contentPanel = new JPanel(new GridLayout(2, 2));
    contentPanel.add(viewPanel);
    contentPanel.add(navigationPanel);
    contentPanel.add(descriptionPanel);
    contentPanel.add(inventoryPanel);

    setLayout(new BorderLayout());
    add(contentPanel, BorderLayout.CENTER);

    setJMenuBar(createMenuBar());
  }

  @Override
  public void setListener(ViewListener viewListener) {
    this.listener = viewListener;
    navigationPanel.setListener(viewListener);
    inventoryPanel.setListener(viewListener);
  }

  @Override
  public void updateRoomImage(String imagePath) {
    viewPanel.updateImage(imagePath);
  }

  @Override
  public void updateDescription(String text) {
    descriptionPanel.updateText(text);
  }

  @Override
  public void updateInventory(List<String> itemNames) {
    inventoryPanel.updateItems(itemNames);
  }

  @Override
  public void updateNavigationButtons(boolean north, boolean south, boolean east, boolean west) {
    navigationPanel.setDirectionsEnabled(north, south, east, west);
  }

  @Override
  public void updateHealthStatus(String statusText) {
    inventoryPanel.updateStatus(statusText);
  }

  @Override
  public void showMessage(String title, String message) {
    ModalDialogs.showMessage(this, title, message);
  }

  @Override
  public void showInspectDialog(String title, String description, String imagePath) {
    ImageIcon icon = ImageUtils.loadItemIcon(imagePath, 150, 150);

    JTextArea descArea = new JTextArea(description);
    descArea.setEditable(false);
    descArea.setLineWrap(true);
    descArea.setWrapStyleWord(true);
    descArea.setColumns(25);
    descArea.setRows(8);

    JScrollPane descScroll = new JScrollPane(descArea);
    descScroll.setBorder(TeamUiTheme.rightQuadrantBorder("Item"));

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    if (icon != null) {
      JLabel imageLabel = new JLabel(icon);
      panel.add(imageLabel, BorderLayout.WEST);
    }
    panel.add(descScroll, BorderLayout.CENTER);

    ModalDialogs.showPlainContent(this, title, panel);
  }

  @Override
  public String promptInput(String title, String prompt) {
    return ModalDialogs.promptInput(this, title, prompt);
  }

  @Override
  public String promptSelection(String title, List<String> options) {
    if (options == null || options.isEmpty()) {
      showMessage(title, "Nothing to select.");
      return null;
    }

    JList<String> selectionList = new JList<>(options.toArray(new String[0]));
    selectionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    TeamUiTheme.applyListSelectionTheme(selectionList);
    JScrollPane scrollPane = new JScrollPane(selectionList);
    scrollPane.setBorder(TeamUiTheme.rightQuadrantBorder("Item"));
    scrollPane.setPreferredSize(new java.awt.Dimension(240, 180));

    int result = ModalDialogs.showConfirm(this, title, scrollPane);

    if (result == JOptionPane.OK_OPTION) {
      return selectionList.getSelectedValue();
    }
    return null;
  }

  @Override
  public void showGameOver(String playerName, int score, String imagePath) {
    ImageIcon icon = ImageUtils.loadScaledIcon(imagePath, 250, 250);

    JPanel panel = new JPanel(new BorderLayout(10, 10));

    JLabel textLabel = new JLabel(
        "<html>Status for " + playerName
            + "<br>Thank you for playing!<br>Your score is " + score + "</html>");
    textLabel.setVerticalAlignment(SwingConstants.TOP);
    panel.add(textLabel, BorderLayout.NORTH);

    if (icon != null) {
      JLabel imageLabel = new JLabel(icon);
      imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
      panel.add(imageLabel, BorderLayout.CENTER);
    }

    ModalDialogs.showPlainContent(this, "Game Over!", panel);
  }

  @Override
  public void showAbout(String imagePath) {
    ImageIcon icon = ImageUtils.loadScaledIcon(imagePath, 200, 200);

    JPanel panel = new JPanel(new BorderLayout(10, 10));

    JLabel titleLabel = new JLabel(
        "<html><div style='text-align:center;'>"
            + "<b>CS5004 Game Engine</b><br>"
            + "This exemplar (c) 2026 Team No Bug"
            + "</div></html>");
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    panel.add(titleLabel, BorderLayout.NORTH);

    if (icon != null) {
      JLabel imageLabel = new JLabel(icon);
      imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
      panel.add(imageLabel, BorderLayout.CENTER);
    }

    ModalDialogs.showPlainContent(this, "About the Game", panel);
  }

  @Override
  public String getSelectedInventoryItem() {
    return inventoryPanel.getSelectedItem();
  }

  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");

    JMenuItem aboutItem = new JMenuItem("About the Game...");
    aboutItem.addActionListener(e -> {
      if (listener != null) listener.onAbout();
    });

    JMenuItem saveItem = new JMenuItem("Save Game");
    saveItem.addActionListener(e -> {
      if (listener != null) listener.onSave();
    });

    JMenuItem restoreItem = new JMenuItem("Restore Game");
    restoreItem.addActionListener(e -> {
      if (listener != null) listener.onRestore();
    });

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(e -> {
      if (listener != null) listener.onExit();
    });

    fileMenu.add(aboutItem);
    fileMenu.add(saveItem);
    fileMenu.add(restoreItem);
    fileMenu.add(exitItem);

    menuBar.add(fileMenu);
    return menuBar;
  }
}
