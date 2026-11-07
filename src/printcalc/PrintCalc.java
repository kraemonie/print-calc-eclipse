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

    private JLabel lblShort, lblA4, lblLong;
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

        final JLabel lblErr = label(" ", new Font("Segoe UI", Font.PLAIN, 13), RED);
        lblErr.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblErr.setPreferredSize(new Dimension(360, 36));
        lblErr.setMinimumSize(new Dimension(0, 36));
        lblErr.setOpaque(false);
        lblErr.setBackground(new Color(254, 242, 242));
        lblErr.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
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
                    lblErr.setText(" ");
                    lblErr.setOpaque(false);
                    txtUser.setText("");
                    txtPass.setText("");
                    showPassword.setSelected(false);
                    txtPass.setEchoChar(passwordMask);
                    refreshInventory();
                    refreshAdminSales();
                    cardLayout.show(mainPanel, "ADMIN");
                } else if (u.equals(cashierUser) && p.equals(cashierPass)) {
                    lblErr.setText(" ");
                    lblErr.setOpaque(false);
                    txtUser.setText("");
                    txtPass.setText("");
                    showPassword.setSelected(false);
                    txtPass.setEchoChar(passwordMask);
                    refreshCashierSales();
                    cardLayout.show(mainPanel, "CASHIER");
                } else {
                    lblErr.setText("Invalid username or password");
                    lblErr.setOpaque(true);
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

        // BoxLayout needs the same alignment axis for every full-width row.
        for (Component child : card.getComponents()) {
            if (child instanceof JComponent) {
                JComponent row = (JComponent) child;
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                Dimension preferred = row.getPreferredSize();
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                        row instanceof JTextField ? 38 : row instanceof JButton ? 40 : preferred.height));
            }
        }
        title.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        lblErr.setHorizontalAlignment(SwingConstants.LEFT);
        showPassword.setHorizontalAlignment(SwingConstants.LEFT);
        showPassword.setBorder(BorderFactory.createEmptyBorder());
        showPassword.setMargin(new Insets(0, 0, 0, 0));
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

        JLabel pt = label("Available paper", new Font("Segoe UI", Font.BOLD, 16), CYAN);
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

        tab.add(buildPriceGuide());

        return tab;
    }

    static double pricePerPage(int service, int paper) {
        double[][] prices = {
                { 3, 5, 7 }, { 5, 7, 10 },
                { 2, 3, 5 }, { 5, 6, 8 }
        };
        return prices[service][paper];
    }

    JPanel buildPriceGuide() {
        JPanel card = uiCard("Service pricing / Php per page");
        JPanel sections = new JPanel(new GridLayout(2, 1, 0, 20));
        sections.setOpaque(false);
        String[] sizes = { "Short", "A4", "Long" };
        for (int group = 0; group < 2; group++) {
            JPanel section = new JPanel(new BorderLayout(0, 10));
            section.setOpaque(false);
            section.add(label(group == 0 ? "Printing" : "Xerox", new Font("Segoe UI", Font.BOLD, 16), ACCENT), BorderLayout.NORTH);
            DefaultTableModel prices = new DefaultTableModel(new String[] { "Paper size", "Black & white", "Colored" }, 0) {
                public boolean isCellEditable(int row, int column) { return false; }
            };
            for (int paper = 0; paper < sizes.length; paper++) {
                prices.addRow(new Object[] { sizes[paper], pricePerPage(group * 2, paper), pricePerPage(group * 2 + 1, paper) });
            }
            section.add(tableScroll(makeTable(prices)), BorderLayout.CENTER);
            sections.add(section);
        }
        card.add(sections, BorderLayout.CENTER);
        return card;
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

        pc.add(label("Restock paper", new Font("Segoe UI", Font.BOLD, 16), GREEN));
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

        for (Component child : pc.getComponents()) {
            if (child instanceof JComponent) {
                ((JComponent) child).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        }
        tab.add(pc);

        tab.add(buildPriceGuide());
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
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        final int[] selectedService = { 0 };
        final int[] selectedPaper = { 0 };
        final JTextField tfPages = makeField();
        tfPages.setText("1");
        tfPages.setName("pageQuantity");
        final JLabel unitPrice = label("", new Font("Segoe UI", Font.BOLD, 16), ACCENT);
        final JToggleButton[] services = new JToggleButton[4];
        final JToggleButton[] papers = new JToggleButton[3];
        final String[] serviceNames = { "Print / Black & white", "Print / Colored", "Xerox / Black & white", "Xerox / Colored" };
        Runnable refreshPrice = () -> {
            unitPrice.setText(String.format("Php %.2f / page", pricePerPage(selectedService[0], selectedPaper[0])));
            for (int i = 0; i < services.length; i++) {
                if (services[i] != null) {
                    services[i].setText("<html><b>" + serviceNames[i] + "</b><br>Php "
                            + String.format("%.2f", pricePerPage(i, selectedPaper[0])) + " / page</html>");
                }
            }
        };
        JPanel serviceGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        serviceGrid.setOpaque(false);
        ButtonGroup serviceGroup = new ButtonGroup();
        for (int i = 0; i < services.length; i++) {
            final int index = i;
            services[i] = choiceCard(serviceNames[i], "service" + i);
            serviceGroup.add(services[i]);
            serviceGrid.add(services[i]);
            services[i].addActionListener(e -> { selectedService[0] = index; refreshPrice.run(); });
        }
        services[0].setSelected(true);
        serviceGrid.setPreferredSize(new Dimension(0, 142));
        serviceGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 142));
        form.add(serviceGrid);
        form.add(Box.createVerticalStrut(14));
        JLabel paperLabel = label("Paper size", new Font("Segoe UI", Font.BOLD, 13), DIM);
        form.add(paperLabel);
        form.add(Box.createVerticalStrut(8));
        JPanel paperGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        paperGrid.setOpaque(false);
        ButtonGroup paperGroup = new ButtonGroup();
        String[] paperNames = { "Short", "A4", "Long" };
        for (int i = 0; i < papers.length; i++) {
            final int index = i;
            papers[i] = choiceCard(paperNames[i], "paper" + i);
            paperGroup.add(papers[i]);
            paperGrid.add(papers[i]);
            papers[i].addActionListener(e -> { selectedPaper[0] = index; refreshPrice.run(); });
        }
        papers[0].setSelected(true);
        paperGrid.setPreferredSize(new Dimension(0, 48));
        paperGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        form.add(paperGrid);
        form.add(Box.createVerticalStrut(14));
        JPanel quantity = new JPanel(new BorderLayout(12, 0));
        quantity.setOpaque(false);
        quantity.add(label("Pages", new Font("Segoe UI", Font.BOLD, 13), DIM), BorderLayout.WEST);
        JPanel stepper = new JPanel(new BorderLayout(8, 0));
        stepper.setOpaque(false);
        JButton minus = makeButton("-", DIM);
        JButton plus = makeButton("+", ACCENT);
        minus.setPreferredSize(new Dimension(48, 46));
        plus.setPreferredSize(new Dimension(48, 46));
        minus.addActionListener(e -> adjustQuantity(tfPages, -1));
        plus.addActionListener(e -> adjustQuantity(tfPages, 1));
        tfPages.setHorizontalAlignment(SwingConstants.CENTER);
        stepper.add(minus, BorderLayout.WEST);
        stepper.add(tfPages, BorderLayout.CENTER);
        stepper.add(plus, BorderLayout.EAST);
        quantity.add(stepper, BorderLayout.CENTER);
        quantity.add(unitPrice, BorderLayout.EAST);
        quantity.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        quantity.setPreferredSize(new Dimension(0, 46));
        form.add(quantity);
        for (Component row : form.getComponents()) {
            if (row instanceof JComponent) ((JComponent) row).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        refreshPrice.run();
        entry.add(form, BorderLayout.CENTER);
        JButton btnAdd = makeButton("Add to order", ACCENT);
        btnAdd.setPreferredSize(new Dimension(0, 48));
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

                    int si = selectedService[0];
                    String serviceName = new String[] { "Print B&W", "Print Color", "Xerox B&W", "Xerox Color" }[si];
                    int pi = selectedPaper[0];
                    double price = pricePerPage(si, pi);
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

                    double sub = price * pages;
                    currentOrder.add(new String[] { serviceName, paperName, String.valueOf(pages),
                            String.format("%.2f", sub) });
                    orderModel.addRow(
                            new Object[] { serviceName, paperName, pages, "Php " + String.format("%.2f", sub) });
                    updateOrderTotal();
                    tfPages.setText("1");

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

                    if (!hasPaper(paper, qty)) {
                        err("Not enough " + paper + " paper for \"" + item[0] + "\"!");
                        return;
                    }
                }

                String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
                String date = LocalDate.now().toString();

                for (String[] item : currentOrder) {
                    String paper = item[1];
                    int qty = Integer.parseInt(item[2]);

                    deductPaper(paper, qty);

                    allSales.add(new String[] { date, time, item[0], paper, item[2], item[3] });
                }

                double change = cash - orderTotal;
                showReceipt(date, time, orderTotal, cash, change);

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

    void deductPaper(String type, int qty) {
        if (type.equals("Short"))
            paperShort -= qty;
        else if (type.equals("A4"))
            paperA4 -= qty;
        else
            paperLong -= qty;
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

    JToggleButton choiceCard(String text, String name) {
        JToggleButton button = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getBackground());
                g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 22, 22);
                g.dispose();
                super.paintComponent(graphics);
            }

            @Override
            protected void paintBorder(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(isSelected() ? ACCENT : new Color(210, 222, 237));
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 22, 22);
                g.dispose();
            }
        };
        button.setName(name);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Runnable style = () -> {
            button.setBackground(button.isSelected() ? new Color(219, 234, 254) : BG_INPUT);
            button.setForeground(button.isSelected() ? ACCENT : TEXT);
            button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            button.repaint();
        };
        button.addItemListener(e -> style.run());
        style.run();
        return button;
    }

    void adjustQuantity(JTextField field, int amount) {
        try {
            int current = Integer.parseInt(field.getText().trim());
            field.setText(String.valueOf(Math.max(1L, Math.min(Integer.MAX_VALUE, (long) current + amount))));
        } catch (NumberFormatException e) {
            field.setText("1");
        }
    }

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
        final JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(getModel().isPressed() ? getBackground().darker() : getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
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

    String receiptText(String date, String time, double total, double cash, double change) {
        return receiptPageText(date, time, total, cash, change, currentOrder);
    }

    String receiptPageText(String date, String time, double total, double cash, double change,
            java.util.List<String[]> items) {
        String rule = "------------------------------------------\n";
        StringBuilder receipt = new StringBuilder();
        receipt.append("                PRINTCALC\n");
        receipt.append("             PRINTING SERVICES\n");
        receipt.append("               SALES RECEIPT\n\n");
        receipt.append(date).append("  ").append(time).append("\n");
        receipt.append(rule);
        receipt.append(String.format("%-30s %11s%n", "ITEM / QUANTITY", "AMOUNT"));
        receipt.append(rule);
        for (String[] item : items) {
            receipt.append(item[0]).append(" / ").append(item[1]).append("\n");
            receipt.append(String.format("  %-28s %11s%n", item[2] + " page(s)",
                    String.format("Php %.2f", Double.parseDouble(item[3]))));
        }
        receipt.append(rule);
        receipt.append(String.format("%-22s %19s%n", "TOTAL", String.format("Php %.2f", total)));
        receipt.append(String.format("%-22s %19s%n", "Cash received", String.format("Php %.2f", cash)));
        receipt.append(String.format("%-22s %19s%n", "CHANGE", String.format("Php %.2f", change)));
        receipt.append(rule);
        receipt.append("          Thank you for your order!\n");
        receipt.append("             Please come again.\n");
        return receipt.toString();
    }

    void showReceipt(String date, String time, double total, double cash, double change) {
        JDialog dialog = new JDialog(this, "Sales receipt", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(new Color(234, 237, 241));
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // One continuous receipt, sized to its contents rather than a fixed item viewport.
        JTextArea receipt = new JTextArea(receiptText(date, time, total, cash, change));
        receipt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        receipt.setForeground(new Color(32, 32, 32));
        receipt.setBackground(Color.WHITE);
        receipt.setEditable(false);
        receipt.setFocusable(false);
        receipt.setLineWrap(false);
        receipt.setMargin(new Insets(24, 22, 24, 22));
        JPanel paper = new JPanel(new BorderLayout());
        paper.setBackground(Color.WHITE);
        paper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 2, new Color(200, 204, 210)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        paper.add(receipt, BorderLayout.CENTER);
        content.add(paper, BorderLayout.CENTER);
        Rectangle screen = getGraphicsConfiguration().getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int availableHeight = screen.height - screenInsets.top - screenInsets.bottom - 40;
        int lineHeight = receipt.getFontMetrics(receipt.getFont()).getHeight();
        int itemsPerPage = Math.max(1, ((availableHeight - 180) / lineHeight - 22) / 2);
        int pageCount = Math.max(1, (currentOrder.size() + itemsPerPage - 1) / itemsPerPage);
        final int[] page = { 0 };
        JButton previous = makeButton("Previous", DIM);
        JButton next = makeButton("Next", ACCENT);
        JButton close = makeButton("Done", ACCENT);
        close.setPreferredSize(new Dimension(0, 44));
        close.addActionListener(e -> dialog.dispose());
        JPanel actions = new JPanel(new GridLayout(1, pageCount > 1 ? 3 : 1, 8, 0));
        actions.setOpaque(false);
        if (pageCount > 1) { actions.add(previous); actions.add(next); }
        actions.add(close);
        content.add(actions, BorderLayout.SOUTH);
        Runnable renderPage = () -> {
            int first = page[0] * itemsPerPage;
            int last = Math.min(currentOrder.size(), first + itemsPerPage);
            String text = receiptPageText(date, time, total, cash, change, currentOrder.subList(first, last));
            if (pageCount > 1) text += String.format("             Page %d of %d%n", page[0] + 1, pageCount);
            receipt.setText(text);
            previous.setEnabled(page[0] > 0);
            next.setEnabled(page[0] + 1 < pageCount);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
        };
        previous.addActionListener(e -> { if (page[0] > 0) { page[0]--; renderPage.run(); } });
        next.addActionListener(e -> { if (page[0] + 1 < pageCount) { page[0]++; renderPage.run(); } });
        dialog.setContentPane(content);
        renderPage.run();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.getRootPane().setDefaultButton(close);
        dialog.setVisible(true);
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

