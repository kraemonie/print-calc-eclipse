package printcalc;

import java.awt.Insets;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Eclipse launch class. UI is in PrintCalcUI; business logic is in PrintCalcBackend. */
public class PrintCalc extends PrintCalcUI {
    // ===================== MAIN =====================

    public static void main(String[] args) {
        UIManager.put("TabbedPane.selected", java.awt.Color.WHITE);
        UIManager.put("TabbedPane.contentAreaColor", java.awt.Color.WHITE);
        UIManager.put("TabbedPane.tabInsets", new Insets(12, 20, 12, 20));
        UIManager.put("Panel.background", new java.awt.Color(243, 247, 252));
        UIManager.put("OptionPane.background", new java.awt.Color(243, 247, 252));
        UIManager.put("OptionPane.messageForeground", new java.awt.Color(30, 41, 59));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PrintCalc().setVisible(true);
            }
        });
    }
}
