package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Panel containing the four direction buttons and the room-action buttons (Take, Examine, Answer).
 * Located in the top-right quadrant of the main window.
 *
 * <p>Direction buttons are arranged in a compass layout:
 *
 * <pre>
 *         [N]
 *    [W]  [S]  [E]
 * </pre>
 */
public class NavigationPanel extends JPanel {

  private static final int PREF_WIDTH = 380;
  private static final int PREF_HEIGHT = 280;

  /** Max width/height for compass arrow icons inside direction buttons. */
  private static final int DIRECTION_ICON_MAX = 36;

  private final RoundedButton northButton;
  private final RoundedButton southButton;
  private final RoundedButton eastButton;
  private final RoundedButton westButton;

  private final RoundedButton takeButton;
  private final RoundedButton examineButton;
  private final RoundedButton answerButton;

  private ViewListener listener;

  /** Creates the navigation panel with direction buttons and action buttons. */
  public NavigationPanel() {
    setLayout(new BorderLayout(0, 10));
    setBorder(TeamUiTheme.rightQuadrantBorder("Navigation"));
    setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

    northButton = new RoundedButton("N", RoundedButton.Appearance.TEAM_RIGHT);
    southButton = new RoundedButton("S", RoundedButton.Appearance.TEAM_RIGHT);
    eastButton = new RoundedButton("E", RoundedButton.Appearance.TEAM_RIGHT);
    westButton = new RoundedButton("W", RoundedButton.Appearance.TEAM_RIGHT);
    configureDirectionIcons();

    JPanel compassPanel = createCompassPanel();
    add(compassPanel, BorderLayout.CENTER);

    takeButton = new RoundedButton("Take", RoundedButton.Appearance.TEAM_RIGHT);
    examineButton = new RoundedButton("Examine", RoundedButton.Appearance.TEAM_RIGHT);
    answerButton = new RoundedButton("Answer", RoundedButton.Appearance.TEAM_RIGHT);

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
    actionPanel.add(takeButton);
    actionPanel.add(examineButton);
    actionPanel.add(answerButton);
    add(actionPanel, BorderLayout.SOUTH);

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
   * Updates which directions are passable from the current room. Buttons stay clickable in all
   * directions so the controller can show a message (e.g. "You cannot go that direction");
   * directions that are not passable are only drawn in a dimmed style.
   *
   * @param north true if north is passable
   * @param south true if south is passable
   * @param east true if east is passable
   * @param west true if west is passable
   */
  public void setDirectionsEnabled(boolean north, boolean south, boolean east, boolean west) {
    northButton.setEnabled(true);
    southButton.setEnabled(true);
    eastButton.setEnabled(true);
    westButton.setEnabled(true);
    northButton.setDimmed(!north);
    southButton.setDimmed(!south);
    eastButton.setDimmed(!east);
    westButton.setDimmed(!west);
  }

  private JPanel createCompassPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(2, 2, 2, 2);

    Dimension btnSize = new Dimension(52, 48);
    northButton.setPreferredSize(btnSize);
    southButton.setPreferredSize(btnSize);
    eastButton.setPreferredSize(btnSize);
    westButton.setPreferredSize(btnSize);

    gbc.gridx = 1;
    gbc.gridy = 0;
    panel.add(northButton, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(westButton, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    panel.add(southButton, gbc);

    gbc.gridx = 2;
    gbc.gridy = 1;
    panel.add(eastButton, gbc);

    return panel;
  }

  /**
   * Uses arrow images from {@code resources/images} when available; otherwise keeps N/S/E/W text.
   */
  private void configureDirectionIcons() {
    applyDirectionIcon(northButton, 'N', "N");
    applyDirectionIcon(southButton, 'S', "S");
    applyDirectionIcon(eastButton, 'E', "E");
    applyDirectionIcon(westButton, 'W', "W");
  }

  private static void applyDirectionIcon(RoundedButton btn, char direction, String letterFallback) {
    ImageIcon icon = ImageUtils.loadDirectionArrowIcon(direction, DIRECTION_ICON_MAX, DIRECTION_ICON_MAX);
    if (icon != null) {
      btn.setIcon(icon);
      btn.setText("");
      btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }
  }

  private void wireListeners() {
    northButton.addActionListener(e -> {
      if (listener != null) listener.onMove("N");
    });
    southButton.addActionListener(e -> {
      if (listener != null) listener.onMove("S");
    });
    eastButton.addActionListener(e -> {
      if (listener != null) listener.onMove("E");
    });
    westButton.addActionListener(e -> {
      if (listener != null) listener.onMove("W");
    });
    takeButton.addActionListener(e -> {
      if (listener != null) listener.onTake();
    });
    examineButton.addActionListener(e -> {
      if (listener != null) listener.onExamine();
    });
    answerButton.addActionListener(e -> {
      if (listener != null) listener.onAnswer();
    });
  }
}
