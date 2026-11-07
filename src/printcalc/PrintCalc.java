package printcalc;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PrintCalc extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private int paperShort = 500;
    private int paperA4 = 500;
    private int paperLong = 500;
    private int inkBlack = 500;
    private int inkCyan = 500;
    private int inkMagenta = 500;
    private int inkYellow = 500;
    private ArrayList<String[]> allSales = new ArrayList<>();
    private ArrayList<String[]> currentOrder = new ArrayList<>();

    private String adminUser = "admin";
    private String adminPass = "admin123";
    private String cashierUser = "cashier";
    private String cashierPass = "cashier123";

    private static final Color BG_PAGE = new Color(243, 247, 252);
    private static final Color BG_CARD = new Color(255, 255, 255);
    private static final Color BG_INPUT = new Color(248, 250, 253);
    private static final Color ACCENT = new Color(29, 78, 216);
    private static final Color CYAN = new Color(3, 105, 161);
    private static final Color GREEN = new Color(21, 128, 61);
    private static final Color ORANGE = new Color(161, 98, 7);
    private static final Color RED = new Color(185, 28, 28);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color DIM = new Color(82, 100, 122);

    private JLabel lblShort, lblA4, lblLong, lblBlack, lblCyanInk, lblMagenta, lblYellow;
    private DefaultTableModel adminSalesModel;
    private JLabel lblAdminTotalSales, lblAdminTotalAmount;
    private DefaultTableModel orderModel;
    private DefaultTableModel cashierSalesModel;
    private JLabel lblOrderTotal, lblCashierTotal;

    public PrintCalc() {
        setTitle("PrintCalc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 820);
        setMinimumSize(new Dimension(1100, 760));
        setLocationRelativeTo(null);
        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(buildLoginPanel(), "LOGIN");
        mainPanel.add(buildAdminPanel(), "ADMIN");
        mainPanel.add(buildCashierPanel(), "CASHIER");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // ===================== LOGIN =====================

    JPanel buildLoginPanel() {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(BG_PAGE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 222, 237), 1),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)));
        card.setPreferredSize(new Dimension(460, 490));

        JLabel title = label("PRINTCALC", new Font("Segoe UI", Font.BOLD, 26), CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(5));

        JLabel sub = label("Billing and inventory for printing services", new Font("Segoe UI", Font.PLAIN, 13), DIM);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(30));

        JLabel lblU = label("Username", new Font("Segoe UI", Font.PLAIN, 13), DIM);
        lblU.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblU);
        card.add(Box.createVerticalStrut(5));
        final JTextField txtUser = makeField();
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(txtUser);
        card.add(Box.createVerticalStrut(15));

        JLabel lblP = label("Password", new Font("Segoe UI", Font.PLAIN, 13), DIM);
        lblP.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblP);
        card.add(Box.createVerticalStrut(5));
        final JPasswordField txtPass = new JPasswordField();
        styleField(txtPass);
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(txtPass);
        final char passwordMask = txtPass.getEchoChar();
        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPassword.setForeground(DIM);
        showPassword.setBackground(BG_CARD);
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPassword.addActionListener(e -> txtPass.setEchoChar(showPassword.isSelected() ? (char) 0 : passwordMask));
        card.add(Box.createVerticalStrut(8));
        card.add(showPassword);
        card.add(Box.createVerticalStrut(8));

        final JLabel lblErr = label("", new Font("Segoe UI", Font.PLAIN, 13), RED);
        lblErr.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblErr);
        card.add(Box.createVerticalStrut(8));

        final JButton btnLogin = makeButton("LOGIN", ACCENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(20));

        JLabel hint = label("admin / admin123   or   cashier / cashier123", new Font("Segoe UI", Font.ITALIC, 10),
                new Color(82, 100, 122));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(hint);

        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String u = txtUser.getText().trim();
                String p = new String(txtPass.getPassword()).trim();
                if (u.equals(adminUser) && p.equals(adminPass)) {
                    lblErr.setText("");
                    txtUser.setText("");
                    txtPass.setText("");
                    showPassword.setSelected(false);
                    txtPass.setEchoChar(passwordMask);
                    refreshInventory();
                    refreshAdminSales();
                    cardLayout.show(mainPanel, "ADMIN");
                } else if (u.equals(cashierUser) && p.equals(cashierPass)) {
                    lblErr.setText("");
                    txtUser.setText("");
                    txtPass.setText("");
                    showPassword.setSelected(false);
                    txtPass.setEchoChar(passwordMask);
                    refreshCashierSales();
                    cardLayout.show(mainPanel, "CASHIER");
                } else {
                    lblErr.setText("Invalid username or password");
                }
            }
        };

        btnLogin.addActionListener(loginAction);
        txtPass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.doClick();
            }
        });

        bg.add(card);
        return bg;
    }

    // ===================== ADMIN =====================

    JPanel buildAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PAGE);

        panel.add(makeTopBar("PrintCalc  /  Administration", CYAN, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        }), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(new Color(210, 222, 237));
        tabs.setForeground(TEXT);
        tabs.addTab("  Inventory  ", buildInventoryTab());
        tabs.addTab("  Add Stock  ", buildAddStockTab());
        tabs.addTab("  Sales Today  ", buildAdminSalesTab());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    JPanel buildInventoryTab() {
        JPanel tab = new JPanel(new GridLayout(1, 2, 15, 0));
        tab.setBackground(BG_PAGE);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Paper
        JPanel paperPanel = new JPanel();
        paperPanel.setLayout(new BoxLayout(paperPanel, BoxLayout.Y_AXIS));
        paperPanel.setBackground(BG_CARD);
        paperPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 222, 237), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel pt = label("PAPER STOCK", new Font("Segoe UI", Font.BOLD, 16), CYAN);
        pt.setAlignmentX(Component.LEFT_ALIGNMENT);
        paperPanel.add(pt);
        paperPanel.add(Box.createVerticalStrut(15));

        lblShort = new JLabel();
        lblA4 = new JLabel();
        lblLong = new JLabel();
        paperPanel.add(stockRow("Short  (8.5 x 11)", lblShort, paperShort));
        paperPanel.add(Box.createVerticalStrut(10));
        paperPanel.add(stockRow("A4  (8.27 x 11.69)", lblA4, paperA4));
        paperPanel.add(Box.createVerticalStrut(10));
        paperPanel.add(stockRow("Long  (8.5 x 13)", lblLong, paperLong));

        tab.add(paperPanel);

        // Ink
        JPanel inkPanel = new JPanel();
        inkPanel.setLayout(new BoxLayout(inkPanel, BoxLayout.Y_AXIS));
        inkPanel.setBackground(BG_CARD);
        inkPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 222, 237), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel it = label("INK STOCK", new Font("Segoe UI", Font.BOLD, 16), CYAN);
        it.setAlignmentX(Component.LEFT_ALIGNMENT);
        inkPanel.add(it);
        inkPanel.add(Box.createVerticalStrut(15));

        lblBlack = new JLabel();
        lblCyanInk = new JLabel();
        lblMagenta = new JLabel();
        lblYellow = new JLabel();
        inkPanel.add(stockRow("Black", lblBlack, inkBlack));
        inkPanel.add(Box.createVerticalStrut(10));
        inkPanel.add(stockRow("Cyan", lblCyanInk, inkCyan));
        inkPanel.add(Box.createVerticalStrut(10));
        inkPanel.add(stockRow("Magenta", lblMagenta, inkMagenta));
        inkPanel.add(Box.createVerticalStrut(10));
        inkPanel.add(stockRow("Yellow", lblYellow, inkYellow));

        tab.add(inkPanel);

        return tab;
    }

    JPanel stockRow(String name, JLabel valLabel, int qty) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(235, 243, 253));
        row.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel n = label(name, new Font("Segoe UI", Font.PLAIN, 14), TEXT);
        row.add(n, BorderLayout.WEST);

        valLabel.setText(qty + "");
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valLabel.setForeground(stockColor(qty));
        row.add(valLabel, BorderLayout.EAST);

        return row;
    }

    Color stockColor(int q) {
        if (q <= 50)
            return RED;
        if (q <= 200)
            return ORANGE;
        return GREEN;
    }

    void refreshInventory() {
        setStock(lblShort, paperShort);
        setStock(lblA4, paperA4);
        setStock(lblLong, paperLong);
        setStock(lblBlack, inkBlack);
        setStock(lblCyanInk, inkCyan);
        setStock(lblMagenta, inkMagenta);
        setStock(lblYellow, inkYellow);
    }

    void setStock(JLabel l, int q) {
        if (l != null) {
            l.setText(q + "");
            l.setForeground(stockColor(q));
        }
    }

    JPanel buildAddStockTab() {
        JPanel tab = new JPanel(new GridLayout(1, 2, 15, 0));
        tab.setBackground(BG_PAGE);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Paper card
        JPanel pc = new JPanel();
        pc.setLayout(new BoxLayout(pc, BoxLayout.Y_AXIS));
        pc.setBackground(BG_CARD);
        pc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 222, 237), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        pc.add(label("ADD PAPER", new Font("Segoe UI", Font.BOLD, 16), GREEN));
        pc.add(Box.createVerticalStrut(20));
        pc.add(label("Paper Size", new Font("Segoe UI", Font.PLAIN, 13), DIM));
        pc.add(Box.createVerticalStrut(5));
        final JComboBox<String> cbP = combo(new String[] { "Short", "A4", "Long" });
        cbP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        pc.add(cbP);
        pc.add(Box.createVerticalStrut(15));
        pc.add(label("Quantity", new Font("Segoe UI", Font.PLAIN, 13), DIM));
        pc.add(Box.createVerticalStrut(5));
        final JTextField tfPQ = makeField();
        tfPQ.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        pc.add(tfPQ);
        pc.add(Box.createVerticalStrut(20));
        JButton bP = makeButton("Add Paper", GREEN);
        bP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pc.add(bP);

        bP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int q = Integer.parseInt(tfPQ.getText().trim());
                    if (q <= 0) {
                        err("Enter a valid quantity!");
                        return;
                    }
                    String t = (String) cbP.getSelectedItem();
                    if (t.equals("Short"))
                        paperShort += q;
                    else if (t.equals("A4"))
                        paperA4 += q;
                    else
                        paperLong += q;
                    refreshInventory();
                    tfPQ.setText("");
                    ok("Added " + q + " sheets of " + t + " paper!");
                } catch (NumberFormatException x) {
                    err("Enter a number!");
                }
            }
        });

        tab.add(pc);

        // Ink card
        JPanel ic = new JPanel();
        ic.setLayout(new BoxLayout(ic, BoxLayout.Y_AXIS));
        ic.setBackground(BG_CARD);
        ic.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 222, 237), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        ic.add(label("ADD INK", new Font("Segoe UI", Font.BOLD, 16), GREEN));
        ic.add(Box.createVerticalStrut(20));
        ic.add(label("Ink Color", new Font("Segoe UI", Font.PLAIN, 13), DIM));
        ic.add(Box.createVerticalStrut(5));
        final JComboBox<String> cbI = combo(new String[] { "Black", "Cyan", "Magenta", "Yellow" });
        cbI.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        ic.add(cbI);
        ic.add(Box.createVerticalStrut(15));
        ic.add(label("Quantity", new Font("Segoe UI", Font.PLAIN, 13), DIM));
        ic.add(Box.createVerticalStrut(5));
        final JTextField tfIQ = makeField();
        tfIQ.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        ic.add(tfIQ);
        ic.add(Box.createVerticalStrut(20));
        JButton bI = makeButton("Add Ink", GREEN);
        bI.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        ic.add(bI);

        bI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int q = Integer.parseInt(tfIQ.getText().trim());
                    if (q <= 0) {
                        err("Enter a valid quantity!");
                        return;
                    }
                    String c = (String) cbI.getSelectedItem();
                    if (c.equals("Black"))
                        inkBlack += q;
                    else if (c.equals("Cyan"))
                        inkCyan += q;
                    else if (c.equals("Magenta"))
                        inkMagenta += q;
                    else
                        inkYellow += q;
                    refreshInventory();
                    tfIQ.setText("");
                    ok("Added " + q + " units of " + c + " ink!");
                } catch (NumberFormatException x) {
                    err("Enter a number!");
                }
            }
        });

        tab.add(ic);
        return tab;
    }

    JPanel buildAdminSalesTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 10));
        tab.setBackground(BG_PAGE);
        tab.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_PAGE);
        JLabel t = label("SALES TODAY  --  " + LocalDate.now(), new Font("Segoe UI", Font.BOLD, 15), CYAN);
        top.add(t, BorderLayout.WEST);
        JButton btnCSV = makeButton("Save CSV Report", ORANGE);
        btnCSV.setPreferredSize(new Dimension(160, 34));

        btnCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDailyReport();
            }
        });

        top.add(btnCSV, BorderLayout.EAST);
        tab.add(top, BorderLayout.NORTH);

        String[] cols = { "#", "Time", "Service", "Paper", "Qty", "Total (Php)" };
        adminSalesModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = makeTable(adminSalesModel);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(new Color(210, 222, 237)));
        tab.add(sp, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 8));
        bot.setBackground(BG_PAGE);
        lblAdminTotalSales = label("Transactions: 0", new Font("Segoe UI", Font.BOLD, 14), DIM);
        lblAdminTotalAmount = label("Total: Php 0.00", new Font("Segoe UI", Font.BOLD, 16), GREEN);
        bot.add(lblAdminTotalSales);
        bot.add(lblAdminTotalAmount);
        tab.add(bot, BorderLayout.SOUTH);

        return tab;
    }

    void refreshAdminSales() {
        adminSalesModel.setRowCount(0);
        String today = LocalDate.now().toString();
        double gt = 0;
        int cnt = 0;
        for (String[] s : allSales) {
            if (s[0].equals(today)) {
                cnt++;
                gt += Double.parseDouble(s[5]);
                adminSalesModel.addRow(new Object[] { cnt, s[1], s[2], s[3], s[4], s[5] });
            }
        }
        lblAdminTotalSales.setText("Transactions: " + cnt);
        lblAdminTotalAmount.setText("Total: Php " + String.format("%.2f", gt));
    }

    // ===================== CASHIER =====================

    JPanel buildCashierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PAGE);

        panel.add(makeTopBar("PrintCalc  /  Cashier", GREEN, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        }), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setBackground(BG_PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel workspace = new JPanel(new BorderLayout(0, 20));
        workspace.setOpaque(false);
        JPanel entry = uiCard("Add a printing service");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        final JComboBox<String> cbService = combo(new String[] {
                "Print B&W  -  Php 3.00/page", "Print Color  -  Php 8.00/page",
                "Xerox B&W  -  Php 1.50/page", "Xerox Color  -  Php 5.00/page" });
        final JComboBox<String> cbPaper = combo(new String[] { "Short (8.5x11)", "A4 (8.27x11.69)", "Long (8.5x13)" });
        final JTextField tfPages = makeField();
        addFormRow(form, 0, "Service", cbService);
        addFormRow(form, 1, "Paper size", cbPaper);
        addFormRow(form, 2, "Number of pages", tfPages);
        entry.add(form, BorderLayout.CENTER);
        JButton btnAdd = makeButton("Add to order", ACCENT);
        btnAdd.setPreferredSize(new Dimension(0, 44));
        entry.add(btnAdd, BorderLayout.SOUTH);
        workspace.add(entry, BorderLayout.NORTH);

        JPanel orderCard = uiCard("Current order");
        orderModel = new DefaultTableModel(new String[] { "Service", "Paper", "Qty", "Subtotal" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable orderTable = makeTable(orderModel);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(190);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        orderCard.add(tableScroll(orderTable), BorderLayout.CENTER);
        workspace.add(orderCard, BorderLayout.CENTER);
        body.add(workspace, BorderLayout.CENTER);

        JPanel sidebar = new JPanel(new BorderLayout(0, 20));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(380, 0));
        JPanel payment = uiCard("Payment");
        JPanel paymentFields = new JPanel(new BorderLayout(0, 18));
        paymentFields.setOpaque(false);
        lblOrderTotal = label("Total: Php 0.00", new Font("Segoe UI", Font.BOLD, 26), ACCENT);
        paymentFields.add(lblOrderTotal, BorderLayout.NORTH);
        JPanel cashRow = new JPanel(new BorderLayout(0, 8));
        cashRow.setOpaque(false);
        cashRow.add(label("Cash received (Php)", new Font("Segoe UI", Font.PLAIN, 13), DIM), BorderLayout.NORTH);
        final JTextField tfCash = makeField();
        tfCash.setPreferredSize(new Dimension(0, 44));
        cashRow.add(tfCash, BorderLayout.CENTER);
        paymentFields.add(cashRow, BorderLayout.CENTER);
        payment.add(paymentFields, BorderLayout.CENTER);
        JPanel paymentButtons = new JPanel(new GridLayout(2, 1, 0, 10));
        paymentButtons.setOpaque(false);
        JButton btnCheckout = makeButton("Complete payment", ACCENT);
        JButton btnClear = makeButton("Clear order", DIM);
        btnCheckout.setPreferredSize(new Dimension(0, 46));
        paymentButtons.add(btnCheckout);
        paymentButtons.add(btnClear);
        payment.add(paymentButtons, BorderLayout.SOUTH);
        sidebar.add(payment, BorderLayout.NORTH);

        JPanel sales = uiCard("Today's sales");
        cashierSalesModel = new DefaultTableModel(new String[] { "Time", "Service", "Paper", "Total" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        sales.add(tableScroll(makeTable(cashierSalesModel)), BorderLayout.CENTER);
        lblCashierTotal = label("Total: Php 0.00", new Font("Segoe UI", Font.BOLD, 18), ACCENT);
        sales.add(lblCashierTotal, BorderLayout.SOUTH);
        sidebar.add(sales, BorderLayout.CENTER);
        body.add(sidebar, BorderLayout.EAST);
        panel.add(body, BorderLayout.CENTER);

        // --- Action listeners ---

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int pages = Integer.parseInt(tfPages.getText().trim());
                    if (pages <= 0) {
                        err("Enter a valid number of pages!");
                        return;
                    }

                    int si = cbService.getSelectedIndex();
                    String serviceName;
                    double price;
                    String inkType;
                    if (si == 0) {
                        serviceName = "Print B&W";
                        price = 3.00;
                        inkType = "BW";
                    } else if (si == 1) {
                        serviceName = "Print Color";
                        price = 8.00;
                        inkType = "Color";
                    } else if (si == 2) {
                        serviceName = "Xerox B&W";
                        price = 1.50;
                        inkType = "BW";
                    } else {
                        serviceName = "Xerox Color";
                        price = 5.00;
                        inkType = "Color";
                    }

                    int pi = cbPaper.getSelectedIndex();
                    String paperName;
                    if (pi == 0)
                        paperName = "Short";
                    else if (pi == 1)
                        paperName = "A4";
                    else
                        paperName = "Long";

                    if (!hasPaper(paperName, pages)) {
                        err("Not enough " + paperName + " paper! (have " + getPaperCount(paperName) + ")");
                        return;
                    }
                    if (!hasInk(inkType, pages)) {
                        err("Not enough ink for this job!");
                        return;
                    }

                    double sub = price * pages;
                    currentOrder.add(new String[] { serviceName, paperName, String.valueOf(pages),
                            String.format("%.2f", sub), inkType });
                    orderModel.addRow(
                            new Object[] { serviceName, paperName, pages, "Php " + String.format("%.2f", sub) });
                    updateOrderTotal();
                    tfPages.setText("");

                } catch (NumberFormatException x) {
                    err("Enter a valid number!");
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentOrder.clear();
                orderModel.setRowCount(0);
                updateOrderTotal();
            }
        });

        btnCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentOrder.isEmpty()) {
                    err("No items in order!");
                    return;
                }

                double orderTotal = 0;
                for (String[] item : currentOrder) {
                    orderTotal += Double.parseDouble(item[3]);
                }

                double cash = 0;
                try {
                    cash = Double.parseDouble(tfCash.getText().trim());
                } catch (NumberFormatException x) {
                    err("Please enter a valid cash amount!");
                    return;
                }

                if (cash < orderTotal) {
                    err("Insufficient cash! (Total: Php " + String.format("%.2f", orderTotal) + ")");
                    return;
                }

                for (String[] item : currentOrder) {
                    String paper = item[1];
                    int qty = Integer.parseInt(item[2]);
                    String ink = item[4];

                    if (!hasPaper(paper, qty)) {
                        err("Not enough " + paper + " paper for \"" + item[0] + "\"!");
                        return;
                    }
                    if (!hasInk(ink, qty)) {
                        err("Not enough ink for \"" + item[0] + "\"!");
                        return;
                    }
                }

                String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
                String date = LocalDate.now().toString();

                for (String[] item : currentOrder) {
                    String paper = item[1];
                    int qty = Integer.parseInt(item[2]);
                    String ink = item[4];

                    deductPaper(paper, qty);
                    deductInk(ink, qty);

                    allSales.add(new String[] { date, time, item[0], paper, item[2], item[3] });
                }

                double change = cash - orderTotal;
                ok("Order complete!\n\nTotal: Php " + String.format("%.2f", orderTotal) +
                        "\nCash: Php " + String.format("%.2f", cash) +
                        "\nChange: Php " + String.format("%.2f", change));

                currentOrder.clear();
                orderModel.setRowCount(0);
                updateOrderTotal();
                tfCash.setText("");
                refreshCashierSales();
            }
        });

        return panel;
    }

    void updateOrderTotal() {
        double t = 0;
        for (String[] item : currentOrder)
            t += Double.parseDouble(item[3]);
        lblOrderTotal.setText("Total: Php " + String.format("%.2f", t));
    }

    void refreshCashierSales() {
        cashierSalesModel.setRowCount(0);
        String today = LocalDate.now().toString();
        double gt = 0;
        for (String[] s : allSales) {
            if (s[0].equals(today)) {
                cashierSalesModel.addRow(new Object[] { s[1], s[2], s[3], "Php " + s[5] });
                gt += Double.parseDouble(s[5]);
            }
        }
        lblCashierTotal.setText("Total: Php " + String.format("%.2f", gt));
    }

    // ===================== INVENTORY LOGIC =====================

    boolean hasPaper(String type, int qty) {
        return getPaperCount(type) >= qty;
    }

    int getPaperCount(String type) {
        if (type.equals("Short"))
            return paperShort;
        if (type.equals("A4"))
            return paperA4;
        return paperLong;
    }

    boolean hasInk(String ink, int qty) {
        if (ink.equals("BW"))
            return inkBlack >= qty;
        return inkBlack >= qty && inkCyan >= qty && inkMagenta >= qty && inkYellow >= qty;
    }

    void deductPaper(String type, int qty) {
        if (type.equals("Short"))
            paperShort -= qty;
        else if (type.equals("A4"))
            paperA4 -= qty;
        else
            paperLong -= qty;
    }

    void deductInk(String ink, int qty) {
        inkBlack -= qty;
        if (ink.equals("Color")) {
            inkCyan -= qty;
            inkMagenta -= qty;
            inkYellow -= qty;
        }
    }

    // ===================== CSV =====================

    void saveDailyReport() {
        String today = LocalDate.now().toString();
        String filename = "sales_" + today + ".csv";
        try {
            FileWriter fw = new FileWriter(filename);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("Date,Time,Service,Paper Type,Quantity,Total Price");
            double gt = 0;
            int cnt = 0;
            for (String[] s : allSales) {
                if (s[0].equals(today)) {
                    pw.println(s[0] + "," + s[1] + "," + s[2] + "," + s[3] + "," + s[4] + "," + s[5]);
                    gt += Double.parseDouble(s[5]);
                    cnt++;
                }
            }
            pw.println();
            pw.println("Total Transactions," + cnt);
            pw.println("Grand Total," + String.format("%.2f", gt));
            pw.close();
            ok("Saved " + filename + " (" + cnt + " transactions, Php " + String.format("%.2f", gt) + ")");
        } catch (Exception e) {
            err("Error: " + e.getMessage());
        }
    }

    // ===================== UI HELPERS =====================

    JPanel uiCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 222, 237)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.add(label(title, new Font("Segoe UI", Font.BOLD, 18), TEXT), BorderLayout.NORTH);
        return card;
    }

    void addFormRow(JPanel form, int row, String caption, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 12, 18);
        JLabel fieldLabel = label("<html>" + caption + "</html>", new Font("Segoe UI", Font.PLAIN, 13), DIM);
        fieldLabel.setPreferredSize(new Dimension(145, 40));
        fieldLabel.setMinimumSize(new Dimension(145, 40));
        form.add(fieldLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 12, 0);
        field.setPreferredSize(new Dimension(260, 40));
        form.add(field, c);
    }

    JScrollPane tableScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 222, 237)));
        return scroll;
    }

    JPanel makeTopBar(String text, Color accent, ActionListener logoutAction) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel lbl = label(text, new Font("Segoe UI", Font.BOLD, 16), accent);
        bar.add(lbl, BorderLayout.WEST);

        final JButton btn = makeButton("Logout", RED);
        btn.setPreferredSize(new Dimension(100, 36));
        btn.addActionListener(logoutAction);
        bar.add(btn, BorderLayout.EAST);

        return bar;
    }

    JLabel label(String text, Font font, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        return l;
    }

    JTextField makeField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    void styleField(JTextField f) {
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(186, 202, 222), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(Color.WHITE);
        cb.setForeground(Color.BLACK);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel,
                    boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, sel, focus);
                lbl.setBackground(sel ? ACCENT : BG_INPUT);
                lbl.setForeground(sel ? Color.WHITE : TEXT);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return lbl;
            }
        });
        return cb;
    }

    JButton makeButton(String text, final Color color) {
        final JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(color);
            }
        });
        return b;
    }

    JTable makeTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(new Color(255, 255, 255));
        t.setForeground(TEXT);
        t.setGridColor(new Color(210, 222, 237));
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(38);
        t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setPreferredSize(new Dimension(0, 40));
        DefaultTableCellRenderer padded = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                    boolean focused, int row, int column) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setBackground(selected ? ACCENT : (row % 2 == 0 ? BG_CARD : BG_INPUT));
                setForeground(selected ? Color.WHITE : TEXT);
                setHorizontalAlignment(column == table.getColumnCount() - 1 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return this;
            }
        };
        t.setDefaultRenderer(Object.class, padded);
        t.setDefaultRenderer(Number.class, padded);
        t.setSelectionBackground(ACCENT);
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setBackground(new Color(235, 243, 253));
        t.getTableHeader().setForeground(CYAN);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return t;
    }

    void ok(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ===================== MAIN =====================

    public static void main(String[] args) {
        UIManager.put("TabbedPane.selected", BG_CARD);
        UIManager.put("TabbedPane.contentAreaColor", BG_CARD);
        UIManager.put("TabbedPane.tabInsets", new Insets(12, 20, 12, 20));
        UIManager.put("Panel.background", BG_PAGE);
        UIManager.put("OptionPane.background", BG_PAGE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PrintCalc().setVisible(true);
            }
        });
    }
}

