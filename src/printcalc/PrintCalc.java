package printcalc;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Application entry point, state, and business rules. Design is in PrintCalcUI. */
public class PrintCalc {
    public enum Role { ADMIN, CASHIER, INVALID }
    public record Payment(String date, String time, double total, double cash, double change) {}
    // ABSTRACTION: all printing services share this contract and cost calculation.
    public static abstract class PrintService {
        // ENCAPSULATION: the service name is private and cannot be changed after construction.
        private final String name;

        protected PrintService(String name) { this.name = name; }
        public final String getName() { return name; }
        public abstract double pricePerPage(int paper);

        public final double costForPages(int paper, int pages) {
            if (pages <= 0) throw new IllegalArgumentException("Enter a valid number of pages!");
            return pricePerPage(paper) * pages;
        }

        protected final double paperPrice(int paper, double shortPrice, double a4Price, double longPrice) {
            switch (paper) {
                case 0: return shortPrice;
                case 1: return a4Price;
                case 2: return longPrice;
                default: throw new IllegalArgumentException("Choose a valid paper size!");
            }
        }
    }

    // INHERITANCE: concrete services inherit naming, validation, and cost calculation.
    public static final class BlackWhitePrinting extends PrintService {
        public BlackWhitePrinting() { super("Print B&W"); }
        @Override
        public double pricePerPage(int paper) { return paperPrice(paper, 3, 5, 7); }
    }

    public static final class ColorPrinting extends PrintService {
        public ColorPrinting() { super("Print Color"); }
        @Override
        public double pricePerPage(int paper) { return paperPrice(paper, 5, 7, 10); }
    }

    public static final class BlackWhiteXerox extends PrintService {
        public BlackWhiteXerox() { super("Xerox B&W"); }
        @Override
        public double pricePerPage(int paper) { return paperPrice(paper, 2, 3, 5); }
    }

    public static final class ColorXerox extends PrintService {
        public ColorXerox() { super("Xerox Color"); }
        @Override
        public double pricePerPage(int paper) { return paperPrice(paper, 5, 6, 8); }
    }

    // POLYMORPHISM: callers use PrintService; Java chooses each overridden price method.
    private static final PrintService[] SERVICES = {
        new BlackWhitePrinting(), new ColorPrinting(), new BlackWhiteXerox(), new ColorXerox()
    };

    private static PrintService serviceAt(int index) {
        if (index < 0 || index >= SERVICES.length) throw new IllegalArgumentException("Choose a valid service!");
        return SERVICES[index];
    }

    // ENCAPSULATION: stock, orders, and sales can only change through backend operations.
    private int paperShort = 500;
    private int paperA4 = 500;
    private int paperLong = 500;
    private final List<String[]> allSales = new ArrayList<>();
    private final List<String[]> currentOrder = new ArrayList<>();

    public Role authenticate(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) return Role.ADMIN;
        if ("cashier".equals(username) && "cashier123".equals(password)) return Role.CASHIER;
        return Role.INVALID;
    }

    public static double pricePerPage(int service, int paper) {
        return serviceAt(service).pricePerPage(paper);
    }

    public int getPaperCount(String type) {
        if (type.equals("Short")) return paperShort;
        if (type.equals("A4")) return paperA4;
        return paperLong;
    }

    public boolean hasPaper(String type, int quantity) { return getPaperCount(type) >= quantity; }

    public void addPaper(String type, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Enter a valid quantity!");
        if (type.equals("Short")) paperShort += quantity;
        else if (type.equals("A4")) paperA4 += quantity;
        else paperLong += quantity;
    }

    private void deductPaper(String type, int quantity) {
        if (type.equals("Short")) paperShort -= quantity;
        else if (type.equals("A4")) paperA4 -= quantity;
        else paperLong -= quantity;
    }

    public String[] addOrderItem(int service, int paper, int pages) {
        if (pages <= 0) throw new IllegalArgumentException("Enter a valid number of pages!");
        PrintService selectedService = serviceAt(service);
        String serviceName = selectedService.getName();
        String paperName = new String[] { "Short", "A4", "Long" }[paper];
        if (!hasPaper(paperName, pages)) throw new IllegalArgumentException(
                "Not enough " + paperName + " paper! (have " + getPaperCount(paperName) + ")");
        String[] item = { serviceName, paperName, String.valueOf(pages),
                String.format("%.2f", selectedService.costForPages(paper, pages)) };
        currentOrder.add(item);
        return item.clone();
    }

    public double orderTotal() {
        double total = 0;
        for (String[] item : currentOrder) total += Double.parseDouble(item[3]);
        return total;
    }

    public void clearOrder() { currentOrder.clear(); }
    public boolean isOrderEmpty() { return currentOrder.isEmpty(); }
    public List<String[]> orderItems() { return snapshot(currentOrder); }
    public List<String[]> sales() { return snapshot(allSales); }

    private List<String[]> snapshot(List<String[]> source) {
        List<String[]> result = new ArrayList<>();
        for (String[] row : source) result.add(row.clone());
        return result;
    }

    /** Record payment; retain the current items until the UI closes the receipt. */
    public Payment checkout(double cash) {
        if (currentOrder.isEmpty()) throw new IllegalArgumentException("No items in order!");
        double total = orderTotal();
        if (cash < total) throw new IllegalArgumentException(
                "Insufficient cash! (Total: Php " + String.format("%.2f", total) + ")");
        for (String[] item : currentOrder) {
            if (!hasPaper(item[1], Integer.parseInt(item[2]))) throw new IllegalArgumentException(
                    "Not enough " + item[1] + " paper for \"" + item[0] + "\"!");
        }
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String date = LocalDate.now().toString();
        for (String[] item : currentOrder) {
            deductPaper(item[1], Integer.parseInt(item[2]));
            allSales.add(new String[] { date, time, item[0], item[1], item[2], item[3] });
        }
        return new Payment(date, time, total, cash, cash - total);
    }

    public String saveDailyReport() throws IOException {
        String today = LocalDate.now().toString();
        String filename = "sales_" + today + ".csv";
        int count = 0;
        double total = 0;
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Path.of(filename)))) {
            writer.println("Date,Time,Service,Paper Type,Quantity,Total Price");
            for (String[] sale : allSales) {
                if (sale[0].equals(today)) {
                    writer.println(String.join(",", sale));
                    count++;
                    total += Double.parseDouble(sale[5]);
                }
            }
            writer.println();
            writer.println("Total Transactions," + count);
            writer.println("Grand Total," + String.format("%.2f", total));
            if (writer.checkError()) throw new IOException("Could not write " + filename);
        }
        return "Saved " + filename + " (" + count + " transactions, Php " + String.format("%.2f", total) + ")";
    }
    public static void main(String[] args) {
        PrintCalcUI.launch(new PrintCalc());
    }
}
