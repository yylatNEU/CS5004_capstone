package view;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Utility class for loading and scaling game images. Provides fallback to generic placeholder images
 * when a requested image file is not found.
 */
public final class ImageUtils {

  private static final String DEFAULT_IMAGE_DIR = "./resources/images/";

  private static final String GENERIC_LOCATION = "generic_location.png";
  private static final String GENERIC_ITEM = "generic_item.png";
  private static final String GENERIC_MONSTER = "generic_monster.png";
  private static final String GENERIC_PUZZLE = "generic_puzzle.png";

  /**
   * Relative to {@code ./resources/images/}: team logo for the About the Game dialog. Place extra art under
   * {@code resources/images/additional/}.
   */
  public static final String TEAM_ABOUT_LOGO_PATH = "additional/teamlogo.png";

  private ImageUtils() {}

  /**
   * Loads an image from the resources/images directory. Returns {@code null} if the file cannot be
   * read.
   *
   * @param filename the image filename (e.g. "courtyard.png")
   * @return the loaded image, or {@code null}
   */
  public static BufferedImage loadImage(String filename) {
    if (filename == null || filename.isBlank()) {
      return null;
    }
    File file = new File(DEFAULT_IMAGE_DIR + filename);
    if (!file.exists()) {
      return null;
    }
    try {
      return ImageIO.read(file);
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Loads an image and scales it to fit within the given dimensions while preserving aspect ratio.
   *
   * @param filename the image filename
   * @param maxWidth the maximum width
   * @param maxHeight the maximum height
   * @return a scaled ImageIcon, or {@code null} if the image cannot be loaded
   */
  public static ImageIcon loadScaledIcon(String filename, int maxWidth, int maxHeight) {
    BufferedImage img = loadImage(filename);
    if (img == null) {
      return null;
    }
    return scaleToFit(img, maxWidth, maxHeight);
  }

  /**
   * Loads an image for a room, falling back to the generic location image if the specified file is
   * missing.
   *
   * @param filename the room picture filename (may be {@code null})
   * @param maxWidth the maximum width
   * @param maxHeight the maximum height
   * @return a scaled ImageIcon (never {@code null} unless even the fallback is missing)
   */
  public static ImageIcon loadRoomIcon(String filename, int maxWidth, int maxHeight) {
    return loadWithFallback(filename, GENERIC_LOCATION, maxWidth, maxHeight);
  }

  /**
   * Loads an image for an item, falling back to the generic item image if the specified file is
   * missing.
   *
   * @param filename the item picture filename (may be {@code null})
   * @param maxWidth the maximum width
   * @param maxHeight the maximum height
   * @return a scaled ImageIcon
   */
  public static ImageIcon loadItemIcon(String filename, int maxWidth, int maxHeight) {
    return loadWithFallback(filename, GENERIC_ITEM, maxWidth, maxHeight);
  }

  /**
   * Loads an image for a monster, falling back to the generic monster image if the specified file is
   * missing.
   *
   * @param filename the monster picture filename (may be {@code null})
   * @param maxWidth the maximum width
   * @param maxHeight the maximum height
   * @return a scaled ImageIcon
   */
  public static ImageIcon loadMonsterIcon(String filename, int maxWidth, int maxHeight) {
    return loadWithFallback(filename, GENERIC_MONSTER, maxWidth, maxHeight);
  }

  /**
   * Loads an image for a puzzle, falling back to the generic puzzle image if the specified file is
   * missing.
   *
   * @param filename the puzzle picture filename (may be {@code null})
   * @param maxWidth the maximum width
   * @param maxHeight the maximum height
   * @return a scaled ImageIcon
   */
  public static ImageIcon loadPuzzleIcon(String filename, int maxWidth, int maxHeight) {
    return loadWithFallback(filename, GENERIC_PUZZLE, maxWidth, maxHeight);
  }

  /**
   * Loads a navigation arrow from {@code north.png}, {@code south.png}, {@code east.png}, or {@code
   * west.png}. If {@code north.png} is missing, derives an upward arrow by rotating {@code
   * south.png} 180°.
   *
   * @param direction one of {@code 'N'}, {@code 'S'}, {@code 'E'}, {@code 'W'} (any case)
   * @param maxWidth maximum icon width
   * @param maxHeight maximum icon height
   * @return a scaled icon, or {@code null} if no image could be produced
   */
  public static ImageIcon loadDirectionArrowIcon(char direction, int maxWidth, int maxHeight) {
    char d = Character.toUpperCase(direction);
    String filename =
        switch (d) {
          case 'N' -> "north.png";
          case 'S' -> "south.png";
          case 'E' -> "east.png";
          case 'W' -> "west.png";
          default -> null;
        };
    if (filename == null) {
      return null;
    }
    ImageIcon icon = loadScaledIcon(filename, maxWidth, maxHeight);
    if (icon != null) {
      return icon;
    }
    if (d == 'N') {
      BufferedImage south = loadImage("south.png");
      if (south != null) {
        BufferedImage up = rotate180(south);
        return scaleToFit(up, maxWidth, maxHeight);
      }
    }
    return null;
  }

  private static BufferedImage rotate180(BufferedImage src) {
    int w = src.getWidth();
    int h = src.getHeight();
    AffineTransform tx = AffineTransform.getRotateInstance(Math.PI, w / 2.0, h / 2.0);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    return op.filter(src, null);
  }

  private static ImageIcon loadWithFallback(
      String filename, String fallback, int maxWidth, int maxHeight) {
    ImageIcon icon = loadScaledIcon(filename, maxWidth, maxHeight);
    if (icon != null) {
      return icon;
    }
    icon = loadScaledIcon(fallback, maxWidth, maxHeight);
    return icon;
  }

  private static ImageIcon scaleToFit(BufferedImage img, int maxWidth, int maxHeight) {
    int origW = img.getWidth();
    int origH = img.getHeight();
    double scale = Math.min((double) maxWidth / origW, (double) maxHeight / origH);
    int newW = (int) (origW * scale);
    int newH = (int) (origH * scale);
    Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    return new ImageIcon(scaled);
  }
}
