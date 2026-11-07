package printcalc;

/** Runs without a desktop or Swing. */
public class BackendTest {
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        PrintCalc backend = new PrintCalc();
        check(backend.authenticate("admin", "admin123") == PrintCalc.Role.ADMIN, "Admin login");
        check(backend.authenticate("cashier", "cashier123") == PrintCalc.Role.CASHIER, "Cashier login");
        check(backend.authenticate("wrong", "wrong") == PrintCalc.Role.INVALID, "Invalid login");
        double[][] prices = { { 3, 5, 7 }, { 5, 7, 10 }, { 2, 3, 5 }, { 5, 6, 8 } };
        for (int service = 0; service < 4; service++) {
            for (int paper = 0; paper < 3; paper++) {
                check(PrintCalc.pricePerPage(service, paper) == prices[service][paper], "Price mismatch");
            }
        }
        backend.addOrderItem(0, 1, 2);
        backend.addOrderItem(3, 2, 3);
        check(backend.orderTotal() == 34, "Order total");
        backend.orderItems().get(0)[3] = "9999";
        check(backend.orderTotal() == 34, "Snapshots must not change backend state");
        try {
            backend.checkout(1);
            throw new AssertionError("Insufficient cash accepted");
        } catch (IllegalArgumentException expected) {
            check(backend.getPaperCount("A4") == 500 && backend.sales().isEmpty(), "Failed payment changed state");
        }
        PrintCalc.Payment payment = backend.checkout(50);
        check(payment.total() == 34 && payment.change() == 16, "Payment amounts");
        check(backend.getPaperCount("A4") == 498 && backend.getPaperCount("Long") == 497, "Paper deduction");
        check(backend.sales().size() == 2, "Sales recorded");
        check(backend.orderItems().size() == 2, "Receipt items retained until cleared");
        backend.clearOrder();
        check(backend.isOrderEmpty() && backend.orderTotal() == 0, "Clear order");
        backend.addPaper("Short", 20);
        check(backend.getPaperCount("Short") == 520, "Restock");
        try {
            backend.addOrderItem(0, 0, 600);
            throw new AssertionError("Insufficient stock accepted");
        } catch (IllegalArgumentException expected) {
            check(backend.isOrderEmpty(), "Rejected item changed order");
        }
        if (args.length > 0 && args[0].equals("--csv")) {
            backend.saveDailyReport();
            String csv = java.nio.file.Files.readString(java.nio.file.Path.of("sales_" + java.time.LocalDate.now() + ".csv"));
            check(csv.contains("Grand Total,34.00") && csv.contains("Total Transactions,2"), "CSV report");
        }
        System.out.println("PASS: backend login, all prices, snapshots, checkout, stock, and order validation");
    }
}
