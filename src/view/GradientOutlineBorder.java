package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;

/** A rounded rectangular outline painted with a linear gradient (team quadrant frames). */
final class GradientOutlineBorder extends AbstractBorder {

  private final Color start;
  private final Color end;
  private final float thickness;
  private final int arc;

  GradientOutlineBorder(Color start, Color end, float thickness, int arc) {
    this.start = start;
    this.end = end;
    this.thickness = thickness;
    this.arc = arc;
  }

  @Override
  public Insets getBorderInsets(Component c, Insets insets) {
    int pad = (int) Math.ceil(thickness) + 3;
    insets.top = pad;
    insets.left = pad;
    insets.right = pad;
    insets.bottom = pad;
    return insets;
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setStroke(
        new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    float t = thickness / 2f;
    float rw = width - thickness;
    float rh = height - thickness;
    RoundRectangle2D.Float rr =
        new RoundRectangle2D.Float(x + t, y + t, rw, rh, arc, arc);
    g2.setPaint(
        new LinearGradientPaint(
            x, y, x + width, y + height, new float[] {0f, 1f}, new Color[] {start, end}));
    g2.draw(rr);
    g2.dispose();
  }
}
