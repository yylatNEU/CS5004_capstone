package view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * A hand-crafted JButton with rounded corners and a soft vertical gradient, similar in spirit to
 * typical Swing game UIs.
 */
public class RoundedButton extends JButton {

  private static final int ARC = 14;

  /**
   * {@link #NEUTRAL} gray; {@link #TEAM_RIGHT} black–green (main-window right quadrant); {@link
   * #DIALOG_OK} / {@link #DIALOG_CANCEL} pastel green / red for modal OK and Cancel.
   */
  public enum Appearance {
    NEUTRAL,
    TEAM_RIGHT,
    DIALOG_OK,
    DIALOG_CANCEL
  }

  private final Appearance appearance;

  /**
   * When true, paints like a disabled button but {@link #isEnabled()} can stay true so clicks
   * still fire (used for direction hints: passable vs blocked, while the controller shows messages
   * for invalid moves).
   */
  private boolean dimmed;

  private static final Color TOP_ENABLED = new Color(248, 249, 252);
  private static final Color BOTTOM_ENABLED = new Color(200, 206, 218);
  private static final Color BORDER_ENABLED = new Color(130, 138, 152);

  private static final Color TOP_DISABLED = new Color(238, 239, 242);
  private static final Color BOTTOM_DISABLED = new Color(218, 220, 226);
  private static final Color BORDER_DISABLED = new Color(190, 192, 200);

  private static final Color PRESSED = new Color(175, 182, 195);

  private static final Color RT_TOP = new Color(0x1E1E1E);
  private static final Color RT_BOTTOM = new Color(0x2E8B57);
  private static final Color RT_BORDER = new Color(0x32CD32);
  private static final Color RT_TOP_DIM = new Color(0x353535);
  private static final Color RT_BOTTOM_DIM = new Color(0x3D5C3D);
  private static final Color RT_BORDER_DIM = new Color(0x5A7A5A);
  private static final Color RT_PRESSED = new Color(0x145214);

  private static final Color DK_TOP = new Color(0xE5F9E5);
  private static final Color DK_BOTTOM = new Color(0xC5ECC5);
  private static final Color DK_BORDER = new Color(0x52B552);
  private static final Color DK_PRESSED = new Color(0xA5DCA5);
  private static final Color DK_FG = new Color(0x0D3D0D);

  private static final Color DC_TOP = new Color(0xFFE8E8);
  private static final Color DC_BOTTOM = new Color(0xFFC8C8);
  private static final Color DC_BORDER = new Color(0xD07070);
  private static final Color DC_PRESSED = new Color(0xE8A8A8);
  private static final Color DC_FG = new Color(0x5C1818);

  /** Neutral appearance (default). */
  public RoundedButton(String text) {
    this(text, Appearance.NEUTRAL);
  }

  /** @param appearance {@link Appearance#TEAM_RIGHT} for Navigation / Inventory buttons. */
  public RoundedButton(String text, Appearance appearance) {
    super(text);
    this.appearance = appearance;
    setContentAreaFilled(false);
    setFocusPainted(false);
    setOpaque(false);
    setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    applyForegroundDefault();
  }

  private void applyForegroundDefault() {
    switch (appearance) {
      case TEAM_RIGHT -> setForeground(Color.WHITE);
      case DIALOG_OK -> setForeground(DK_FG);
      case DIALOG_CANCEL -> setForeground(DC_FG);
      default -> {}
    }
  }

  /** @see #dimmed */
  public void setDimmed(boolean dimmed) {
    if (this.dimmed == dimmed) {
      return;
    }
    this.dimmed = dimmed;
    if (appearance == Appearance.TEAM_RIGHT) {
      setForeground(dimmed ? new Color(0xBBBBBB) : Color.WHITE);
    }
    repaint();
  }

  /** @see #dimmed */
  public boolean isDimmed() {
    return dimmed;
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int w = getWidth();
    int h = getHeight();
    float x = 1;
    float y = 1;
    float rw = w - 2;
    float rh = h - 2;

    switch (appearance) {
      case TEAM_RIGHT -> paintTeamRight(g2, x, y, rw, rh, h);
      case DIALOG_OK -> paintDialogOk(g2, x, y, rw, rh, h);
      case DIALOG_CANCEL -> paintDialogCancel(g2, x, y, rw, rh, h);
      default -> paintNeutral(g2, x, y, rw, rh, h);
    }
    g2.dispose();

    super.paintComponent(g);
  }

  private void paintNeutral(Graphics2D g2, float x, float y, float rw, float rh, int h) {
    if (getModel().isPressed() && isEnabled()) {
      g2.setColor(PRESSED);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else if (!isEnabled() || dimmed) {
      GradientPaint gp = new GradientPaint(0, 0, TOP_DISABLED, 0, h, BOTTOM_DISABLED);
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else {
      GradientPaint gp = new GradientPaint(0, 0, TOP_ENABLED, 0, h, BOTTOM_ENABLED);
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    }
    g2.setColor(isEnabled() && !dimmed ? BORDER_ENABLED : BORDER_DISABLED);
    g2.draw(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
  }

  private void paintTeamRight(Graphics2D g2, float x, float y, float rw, float rh, int h) {
    if (getModel().isPressed() && isEnabled()) {
      g2.setColor(RT_PRESSED);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else if (!isEnabled()) {
      GradientPaint gp = new GradientPaint(0, 0, RT_TOP_DIM, 0, h, RT_BOTTOM_DIM);
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else if (dimmed) {
      GradientPaint gp = new GradientPaint(0, 0, RT_TOP_DIM, 0, h, RT_BOTTOM_DIM);
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else {
      GradientPaint gp = new GradientPaint(0, 0, RT_TOP, 0, h, RT_BOTTOM);
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    }
    g2.setColor(
        isEnabled() && !dimmed ? RT_BORDER : RT_BORDER_DIM);
    g2.draw(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
  }

  private void paintDialogOk(Graphics2D g2, float x, float y, float rw, float rh, int h) {
    if (getModel().isPressed() && isEnabled()) {
      g2.setColor(DK_PRESSED);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else if (!isEnabled()) {
      GradientPaint gp = new GradientPaint(0, 0, DK_TOP.darker(), 0, h, DK_BOTTOM.darker());
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else {
      GradientPaint gp = new GradientPaint(0, 0, DK_TOP, 0, h, DK_BOTTOM);
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    }
    g2.setColor(DK_BORDER);
    g2.draw(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
  }

  private void paintDialogCancel(Graphics2D g2, float x, float y, float rw, float rh, int h) {
    if (getModel().isPressed() && isEnabled()) {
      g2.setColor(DC_PRESSED);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else if (!isEnabled()) {
      GradientPaint gp = new GradientPaint(0, 0, DC_TOP.darker(), 0, h, DC_BOTTOM.darker());
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    } else {
      GradientPaint gp = new GradientPaint(0, 0, DC_TOP, 0, h, DC_BOTTOM);
      g2.setPaint(gp);
      g2.fill(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
    }
    g2.setColor(DC_BORDER);
    g2.draw(new RoundRectangle2D.Float(x, y, rw, rh, ARC, ARC));
  }
}
