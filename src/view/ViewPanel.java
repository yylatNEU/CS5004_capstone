package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel that displays the current room, puzzle, or monster image. Located in the top-left quadrant
 * of the main window.
 */
public class ViewPanel extends JPanel {

  private static final int PREF_WIDTH = 380;
  private static final int PREF_HEIGHT = 280;

  private static final int BORDER_ALLOWANCE = 36;
  private static final int HORIZONTAL_INSET = 8;

  private final JLabel imageLabel;
  private String currentFilename;

  /** Creates the view panel with a titled border and centered image label. */
  public ViewPanel() {
    setLayout(new BorderLayout());
    setBorder(TeamUiTheme.leftQuadrantBorder("View"));
    setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));

    imageLabel = new JLabel();
    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    imageLabel.setVerticalAlignment(SwingConstants.CENTER);
    add(imageLabel, BorderLayout.CENTER);

    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            rescaleCurrentImage();
          }
        });
  }

  /**
   * Updates the displayed image. The image is loaded from resources/images/ and scaled to fit the
   * panel. If the filename is {@code null} or the file is missing, the generic location image is
   * shown.
   *
   * @param filename the image filename (e.g. "courtyard.png")
   */
  public void updateImage(String filename) {
    this.currentFilename = filename;
    rescaleCurrentImage();
    imageLabel.setText(null);
    revalidate();
    repaint();
  }

  private void rescaleCurrentImage() {
    int w = getWidth();
    int h = getHeight();
    if (w <= 0 || h <= 0) {
      return;
    }
    int maxW = Math.max(w - 2 * HORIZONTAL_INSET, 1);
    int maxH = Math.max(h - BORDER_ALLOWANCE, 1);
    ImageIcon icon = ImageUtils.loadRoomIcon(currentFilename, maxW, maxH);
    imageLabel.setIcon(icon);
  }
}
