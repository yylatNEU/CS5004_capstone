package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Panel that displays the current room description, puzzle effects, or monster information. Located
 * in the bottom-left quadrant of the main window.
 */
public class DescriptionPanel extends JPanel {

  private static final int PREF_WIDTH = 380;
  private static final int PREF_HEIGHT = 250;

  private final JTextArea textArea;

  /** Creates the description panel with a titled border and scrollable text area. */
  public DescriptionPanel() {
    setLayout(new BorderLayout());
    setBorder(TeamUiTheme.leftQuadrantBorder("Description"));
    setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

    textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * Updates the description text.
   *
   * @param text the text to display
   */
  public void updateText(String text) {
    textArea.setText(text);
    textArea.setCaretPosition(0);
  }
}
