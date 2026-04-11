package view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/** Colors and titled borders aligned with the Team No Bug logo (red/white left, black/green right). */
public final class TeamUiTheme {

  /** Deep red toward soft off-white (View, Description). */
  public static final Color LEFT_FRAME_START = new Color(0xC80000);

  public static final Color LEFT_FRAME_END = new Color(0xFFF5F5);
  /** Dark toward neon green accent (Navigation, Inventory). */
  public static final Color RIGHT_FRAME_START = new Color(0x0D0D0D);

  public static final Color RIGHT_FRAME_END = new Color(0x39FF14);

  /** Selected row in inventory and selection dialogs (replaces default light blue). */
  public static final Color LIST_SELECTION_BACKGROUND = new Color(0xB8F2C8);

  public static final Color LIST_SELECTION_FOREGROUND = new Color(0x143214);

  /** Rollover / selection in {@code File} menu popups (left-side red theme). */
  public static final Color MENU_SELECTION_BACKGROUND = new Color(0xFFD4D4);

  public static final Color MENU_SELECTION_FOREGROUND = new Color(0x4A1010);

  private static final float FRAME_STROKE = 2.5f;
  private static final int FRAME_ARC = 12;

  private TeamUiTheme() {}

  /** Titled border with red–white gradient outline. */
  public static Border leftQuadrantBorder(String title) {
    Border outline =
        new GradientOutlineBorder(LEFT_FRAME_START, LEFT_FRAME_END, FRAME_STROKE, FRAME_ARC);
    TitledBorder tb = BorderFactory.createTitledBorder(outline, title);
    tb.setTitleColor(new Color(0x8B0000));
    tb.setTitleFont(tb.getTitleFont().deriveFont(Font.BOLD));
    return tb;
  }

  /** Titled border with black–green gradient outline. */
  public static Border rightQuadrantBorder(String title) {
    Border outline =
        new GradientOutlineBorder(RIGHT_FRAME_START, RIGHT_FRAME_END, FRAME_STROKE, FRAME_ARC);
    TitledBorder tb = BorderFactory.createTitledBorder(outline, title);
    tb.setTitleColor(new Color(0x228B22));
    tb.setTitleFont(tb.getTitleFont().deriveFont(Font.BOLD));
    return tb;
  }

  /** Light-green selection highlight for {@link JList} (inventory, Examine pick list, etc.). */
  public static void applyListSelectionTheme(JList<?> list) {
    list.setSelectionBackground(LIST_SELECTION_BACKGROUND);
    list.setSelectionForeground(LIST_SELECTION_FOREGROUND);
  }

  /**
   * Applies light-red highlight for menu bar popups. Call from {@link javax.swing.JFrame} before
   * {@code setJMenuBar} (repeat if the look-and-feel is changed later).
   */
  public static void installMenuHighlightTheme() {
    UIManager.put("MenuItem.selectionBackground", MENU_SELECTION_BACKGROUND);
    UIManager.put("MenuItem.selectionForeground", MENU_SELECTION_FOREGROUND);
    UIManager.put("Menu.selectionBackground", MENU_SELECTION_BACKGROUND);
    UIManager.put("Menu.selectionForeground", MENU_SELECTION_FOREGROUND);
    UIManager.put("MenuItem.acceleratorSelectionForeground", MENU_SELECTION_FOREGROUND);
  }
}
