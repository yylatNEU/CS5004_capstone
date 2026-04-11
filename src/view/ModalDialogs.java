package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/** Modal dialogs that use {@link RoundedButton} instead of default JOptionPane buttons. */
final class ModalDialogs {

  private ModalDialogs() {}

  static void showMessage(JFrame parent, String title, String message) {
    Icon icon = UIManager.getIcon("OptionPane.informationIcon");
    JLabel msgLabel = new JLabel(message);
    JPanel center = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
    if (icon != null) {
      center.add(new JLabel(icon));
    }
    center.add(msgLabel);
    showSingleButtonDialog(parent, title, center);
  }

  static void showPlainContent(JFrame parent, String title, Component content) {
    JPanel wrapped = new JPanel(new BorderLayout());
    wrapped.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    wrapped.add(content, BorderLayout.CENTER);
    showSingleButtonDialog(parent, title, wrapped);
  }

  private static void showSingleButtonDialog(JFrame parent, String title, Component body) {
    JDialog dialog = new JDialog(parent, title, true);
    dialog.setLayout(new BorderLayout());
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.add(body, BorderLayout.CENTER);

    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
    RoundedButton ok = new RoundedButton("OK", RoundedButton.Appearance.DIALOG_OK);
    ok.addActionListener(e -> dialog.dispose());
    south.add(ok);
    dialog.add(south, BorderLayout.SOUTH);
    dialog.getRootPane().setDefaultButton(ok);

    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  static String promptInput(JFrame parent, String title, String prompt) {
    Icon icon = UIManager.getIcon("OptionPane.questionIcon");
    JTextField field = new JTextField(24);

    JPanel northRow = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
    if (icon != null) {
      northRow.add(new JLabel(icon));
    }
    northRow.add(new JLabel(prompt));

    JPanel center = new JPanel(new BorderLayout(0, 8));
    center.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    center.add(northRow, BorderLayout.NORTH);
    center.add(field, BorderLayout.CENTER);

    final String[] holder = new String[1];

    JDialog dialog = new JDialog(parent, title, true);
    dialog.setLayout(new BorderLayout());
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.add(center, BorderLayout.CENTER);

    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
    RoundedButton ok = new RoundedButton("OK", RoundedButton.Appearance.DIALOG_OK);
    RoundedButton cancel = new RoundedButton("Cancel", RoundedButton.Appearance.DIALOG_CANCEL);
    ok.addActionListener(
        e -> {
          holder[0] = field.getText();
          dialog.dispose();
        });
    cancel.addActionListener(e -> dialog.dispose());
    south.add(ok);
    south.add(cancel);
    dialog.add(south, BorderLayout.SOUTH);

    field.addActionListener(e -> ok.doClick());
    dialog.getRootPane().setDefaultButton(ok);

    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
    field.requestFocusInWindow();

    return holder[0];
  }

  static int showConfirm(JFrame parent, String title, Component content) {
    final int[] holder = new int[] {JOptionPane.CLOSED_OPTION};

    JDialog dialog = new JDialog(parent, title, true);
    dialog.setLayout(new BorderLayout());
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    JPanel wrap = new JPanel(new BorderLayout());
    wrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    wrap.add(content, BorderLayout.CENTER);
    dialog.add(wrap, BorderLayout.CENTER);

    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
    RoundedButton ok = new RoundedButton("OK", RoundedButton.Appearance.DIALOG_OK);
    RoundedButton cancel = new RoundedButton("Cancel", RoundedButton.Appearance.DIALOG_CANCEL);
    ok.addActionListener(
        e -> {
          holder[0] = JOptionPane.OK_OPTION;
          dialog.dispose();
        });
    cancel.addActionListener(
        e -> {
          holder[0] = JOptionPane.CANCEL_OPTION;
          dialog.dispose();
        });
    south.add(ok);
    south.add(cancel);
    dialog.add(south, BorderLayout.SOUTH);
    dialog.getRootPane().setDefaultButton(ok);

    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    return holder[0];
  }
}
