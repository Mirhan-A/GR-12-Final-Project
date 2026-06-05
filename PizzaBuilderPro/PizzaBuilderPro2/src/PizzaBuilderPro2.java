// ICS4U1 Culminating – Pizza Builder Pro

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.util.*;

// ---------------------------
// PIZZA CLASS (OOP: base class, encapsulation)
// ---------------------------
class Pizza {
    private String size, crust;
    private ArrayList<String> toppings;
    private int quantity;
    private int orderId;

    public Pizza(String size, String crust, ArrayList<String> toppings, int quantity, int orderId) {
        this.size = size;
        this.crust = crust;
        this.toppings = toppings;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public String getSize() { return size; }
    public String getCrust() { return crust; }
    public ArrayList<String> getToppings() { return toppings; }
    public int getQuantity() { return quantity; }
    public int getOrderId() { return orderId; }

    public void setSize(String s) { size = s; }
    public void setCrust(String c) { crust = c; }
    public void setToppings(ArrayList<String> t) { toppings = t; }
    public void setQuantity(int q) { quantity = q; }

    // Base price calculation (polymorphic method)
    public double calculatePrice() {
        double price = 5;
        if (size.equals("Medium")) price += 3;
        if (size.equals("Large")) price += 5;
        if (crust.equals("Thin")) price += 1;
        if (crust.equals("Deep Dish")) price += 2;
        price += toppings.size() * 0.75;
        return price * quantity;
    }
}

// ---------------------------
// CUSTOM PIZZA (OOP: inheritance + polymorphism)
// ---------------------------
class CustomPizza extends Pizza {
    public CustomPizza(String size, String crust, ArrayList<String> toppings, int quantity, int orderId) {
        super(size, crust, toppings, quantity, orderId);
    }

    @Override
    public double calculatePrice() {
        double price = super.calculatePrice();
        if (getToppings().size() >= 5) price *= 0.9; // 10% discount
        return price;
    }
}

// ---------------------------
// ORDER MANAGER
// OOP: separate class for managing orders
// Contains: sorting, searching, recursion, file-related helpers
// ---------------------------
class OrderManager {
    private ArrayList<Pizza> orders = new ArrayList<>();
    private ArrayList<Pizza> originalOrder = new ArrayList<>();
    private Random rand = new Random();
    private HashSet<Integer> usedIds = new HashSet<>();

    public void addOrder(Pizza p) {
        orders.add(p);
        originalOrder.add(p);
    }

    public ArrayList<Pizza> getOrders() { return orders; }

    public void restoreDefaultOrder() {
        orders.clear();
        orders.addAll(originalOrder);
    }

    public void clearAll() {
        orders.clear();
        originalOrder.clear();
        usedIds.clear();
    }

    // Generate unique order IDs
    public int generateOrderId() {
        int id;
        do { id = 100 + rand.nextInt(2001); }
        while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }

    // Searching Algorithm: Linear Search
    public Pizza searchByOrderId(int id) {
        for (Pizza p : orders)
            if (p.getOrderId() == id) return p;
        return null;
    }

    // Recursion: count total toppings across all orders
    public int countToppingsRecursive() { return countHelper(0); }
    private int countHelper(int i) {
        if (i == orders.size()) return 0;
        return orders.get(i).getToppings().size() + countHelper(i + 1);
    }

    // Sorting Algorithm: Bubble Sort by Order ID
    public void sortByOrderId() {
        for (int i = 0; i < orders.size() - 1; i++)
            for (int j = 0; j < orders.size() - i - 1; j++)
                if (orders.get(j).getOrderId() > orders.get(j + 1).getOrderId()) {
                    Pizza temp = orders.get(j);
                    orders.set(j, orders.get(j + 1));
                    orders.set(j + 1, temp);
                }
    }

    // Sorting Algorithm: Bubble Sort by Price
    public void sortByPrice() {
        for (int i = 0; i < orders.size() - 1; i++)
            for (int j = 0; j < orders.size() - i - 1; j++)
                if (orders.get(j).calculatePrice() > orders.get(j + 1).calculatePrice()) {
                    Pizza temp = orders.get(j);
                    orders.set(j, orders.get(j + 1));
                    orders.set(j + 1, temp);
                }
    }
}

// ---------------------------
// MAIN GUI CLASS (Java Swing GUI)
// ---------------------------
public class PizzaBuilderPro2 extends JFrame {

    private JTextArea outputArea;
    private JLabel toppingsLabel;
    private OrderManager manager = new OrderManager();
    private String currentSortMode = "default";

    public PizzaBuilderPro2() {
        setTitle("Pizza Builder Pro");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(leftPanel(), BorderLayout.WEST);
        add(centerPanel(), BorderLayout.CENTER);
        add(bottomPanel(), BorderLayout.SOUTH);
    }

    // ---------------------------
    // LEFT PANEL – all actions, but display only via "List Orders"
    // ---------------------------
    private JPanel leftPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.setBorder(new TitledBorder("Controls"));

        String[] names = {
            "Add Pizza","List Orders","Edit Order","Search Orders",
            "Delete Order","Clear Orders","Save Orders","Load Orders",
            "Sort by Order ID","Sort by Price",
            "Back to Default Order",
            "Help","About"
        };

        JButton[] b = new JButton[names.length];
        for (int i = 0; i < names.length; i++) {
            b[i] = new JButton(names[i]);
            p.add(b[i]);
        }

        b[0].addActionListener(e -> addPizzaDialog());
        b[1].addActionListener(e -> refreshOutput());
        b[2].addActionListener(e -> openEditWindow());
        b[3].addActionListener(e -> searchOrdersDialog());
        b[4].addActionListener(e -> deleteOrderDialog());

        // CLEAR ORDERS – no auto List, popup if nothing
        b[5].addActionListener(e -> {
            if (manager.getOrders().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "There are no orders to clear.",
                    "Nothing to Clear",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (JOptionPane.showConfirmDialog(this,
                "Clear ALL orders?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                manager.clearAll();
                JOptionPane.showMessageDialog(this,
                    "All orders cleared.\nClick 'List Orders' to refresh the display.");
            }
        });

        b[6].addActionListener(e -> saveOrdersToFile());
        b[7].addActionListener(e -> loadOrdersFromFile());

        // SORT BY ORDER ID
        b[8].addActionListener(e -> {
            if (currentSortMode.equals("id")) {
                JOptionPane.showMessageDialog(this, "Already sorted by Order ID.");
                return;
            }

            manager.sortByOrderId();
            currentSortMode = "id";

            JOptionPane.showMessageDialog(this,
                "Sorted by Order ID.\nClick 'List Orders' to view.");
        });

        // SORT BY PRICE
        b[9].addActionListener(e -> {
            String[] options = {"Highest Price", "Lowest Price", "Cancel"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "How would you like to sort the prices?",
                "Sort by Price",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (choice == 0) { // Highest
                if (currentSortMode.equals("price_high")) {
                    JOptionPane.showMessageDialog(this, "Already sorted by Highest Price.");
                    return;
                }

                manager.sortByPrice();
                Collections.reverse(manager.getOrders());
                currentSortMode = "price_high";

                JOptionPane.showMessageDialog(this,
                    "Sorted by Highest Price.\nClick 'List Orders' to view.");
            }
            else if (choice == 1) { // Lowest
                if (currentSortMode.equals("price_low")) {
                    JOptionPane.showMessageDialog(this, "Already sorted by Lowest Price.");
                    return;
                }

                manager.sortByPrice();
                currentSortMode = "price_low";

                JOptionPane.showMessageDialog(this,
                    "Sorted by Lowest Price.\nClick 'List Orders' to view.");
            }
        });

        // BACK TO DEFAULT ORDER
        b[10].addActionListener(e -> {
            if (currentSortMode.equals("default")) {
                JOptionPane.showMessageDialog(this, "Already in default order.");
                return;
            }

            manager.restoreDefaultOrder();
            currentSortMode = "default";

            JOptionPane.showMessageDialog(this,
                "Order list restored to original order.\nClick 'List Orders' to view.");
        });

        b[11].addActionListener(e -> showHelp());
        b[12].addActionListener(e -> showAbout());

        return p;
    }

    // ---------------------------
    // DELETE ORDER
    // ---------------------------
    private void deleteOrderDialog() {
        if (manager.getOrders().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "There are no orders to delete.",
                "Nothing to Delete",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter order number to delete:");
        if (input == null) return;

        int id;
        try { id = Integer.parseInt(input); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid number."); return; }

        Pizza p = manager.searchByOrderId(id);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "No order with that number.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this,
            "Delete order #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            manager.getOrders().remove(p);
            JOptionPane.showMessageDialog(this,
                "Order deleted.\nClick 'List Orders' to refresh the display.");
        }
    }

    // ---------------------------
    // SAVE ORDERS (File Write)
// ---------------------------
    private void saveOrdersToFile() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("orders.txt"));
            for (Pizza p : manager.getOrders()) {
                writer.println("Order ID: " + p.getOrderId());
                writer.println("Size: " + p.getSize());
                writer.println("Crust: " + p.getCrust());
                writer.println("Toppings: " + String.join(", ", p.getToppings()));
                writer.println("Quantity: " + p.getQuantity());
                writer.println("Total: " + p.calculatePrice()); // uses polymorphic price (with discount)
                writer.println("---");
            }
            writer.close();
            JOptionPane.showMessageDialog(this, "Saved to orders.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving file.");
        }
    }

    // ---------------------------
    // LOAD ORDERS (File Read)
// ---------------------------
    private void loadOrdersFromFile() {
        try {
            // Prevent loading again if there are already orders
            if (!manager.getOrders().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Orders are already loaded.\nUse 'Clear Orders' first if you want to reload.",
                    "Already Loaded",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            File file = new File("orders.txt");
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "orders.txt not found.");
                return;
            }

            // Clear everything before loading (safety)
            manager.clearAll();

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Order ID:")) {
                    int id = Integer.parseInt(line.substring(10).trim());
                    String size = reader.readLine().substring(6).trim();
                    String crust = reader.readLine().substring(7).trim();

                    String toppingsLine = reader.readLine().substring(10).trim();
                    ArrayList<String> toppings = new ArrayList<>();
                    if (!toppingsLine.isEmpty())
                        toppings.addAll(Arrays.asList(toppingsLine.split(", ")));

                    int quantity = Integer.parseInt(reader.readLine().substring(10).trim());

                    reader.readLine(); // skip total
                    reader.readLine(); // skip ---

                    // Use CustomPizza so discount logic still applies
                    manager.addOrder(new CustomPizza(size, crust, toppings, quantity, id));
                }
            }

            reader.close();
            JOptionPane.showMessageDialog(this,
                "Orders loaded successfully!\nClick 'List Orders' to view.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file.");
        }
    }

    // ---------------------------
    // CENTER PANEL
    // ---------------------------
    private JPanel centerPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder("Orders"));

        outputArea = new JTextArea();
        outputArea.setEditable(false);

        toppingsLabel = new JLabel("Total toppings: 0");

        p.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        p.add(toppingsLabel, BorderLayout.SOUTH);

        return p;
    }

    // ---------------------------
    // BOTTOM PANEL
    // ---------------------------
    private JPanel bottomPanel() {
        JPanel p = new JPanel();
        JButton exit = new JButton("Exit");

        exit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                "Exit program?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Goodbye! Thanks for using Pizza Builder Pro.");
                System.exit(0);
            }
        });

        p.add(exit);
        return p;
    }

    // ---------------------------
    // ADD / EDIT WINDOWS
    // ---------------------------
    private void addPizzaDialog() {
        openPizzaEditor(null);
    }

    private void openEditWindow() {
        String input = JOptionPane.showInputDialog(this, "Enter order number to edit:");
        if (input == null) return;

        int id;
        try { id = Integer.parseInt(input); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Invalid number."); return; }

        Pizza p = manager.searchByOrderId(id);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "No order with that number.");
            return;
        }

        openPizzaEditor(p);
    }

    private void openPizzaEditor(Pizza editing) {
        boolean isEdit = editing != null;
        int orderId = isEdit ? editing.getOrderId() : manager.generateOrderId();

        JDialog dialog = new JDialog(this,
            (isEdit ? "Edit Pizza — Order #" + orderId : "Add Pizza"), true);

        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String[] sizes = {"Small","Medium","Large"};
        String[] crusts = {"Regular","Thin","Deep Dish"};
        String[] toppingOptions = {
            "Cheese","Pepperoni","Mushrooms","Onions","Bacon","Olives",
            "Peppers","Pineapple","Sausage","Ham","Tomatoes","Spinach",
            "Jalapenos","Chicken","Ground Beef"
        };

        JComboBox<String> sizeBox = new JComboBox<>(sizes);
        JComboBox<String> crustBox = new JComboBox<>(crusts);
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1,1,20,1));

        if (isEdit) {
            sizeBox.setSelectedItem(editing.getSize());
            crustBox.setSelectedItem(editing.getCrust());
            qtySpinner.setValue(editing.getQuantity());
        }

        form.add(new JLabel("Size:")); form.add(sizeBox);
        form.add(new JLabel("Crust:")); form.add(crustBox);
        form.add(new JLabel("Quantity:")); form.add(qtySpinner);

        JPanel topsPanel = new JPanel(new GridLayout(0,3,5,5));
        JCheckBox[] checks = new JCheckBox[toppingOptions.length];

        for (int i = 0; i < toppingOptions.length; i++) {
            checks[i] = new JCheckBox(toppingOptions[i]);
            if (isEdit && editing.getToppings().contains(toppingOptions[i])) checks[i].setSelected(true);
            if (!isEdit && toppingOptions[i].equals("Cheese")) checks[i].setSelected(true);
            topsPanel.add(checks[i]);
        }

        JScrollPane scroll = new JScrollPane(topsPanel);
        scroll.setPreferredSize(new Dimension(380,180));
        form.add(new JLabel("Toppings:"));
        form.add(scroll);

        JLabel priceLabel = new JLabel("Price: $0.00");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        form.add(priceLabel);

        // Live price calculation (matches CustomPizza discount logic)
        Runnable updatePrice = () -> {
            double sizePrice = sizeBox.getSelectedItem().equals("Medium") ? 8 :
                               sizeBox.getSelectedItem().equals("Large") ? 10 : 5;

            double crustPrice = crustBox.getSelectedItem().equals("Thin") ? 1 :
                                crustBox.getSelectedItem().equals("Deep Dish") ? 2 : 0;

            int toppingCount = 0;
            for (JCheckBox cb : checks) if (cb.isSelected()) toppingCount++;

            double subtotal = sizePrice + crustPrice + (toppingCount * 0.75);
            if (toppingCount >= 5) subtotal *= 0.9;

            double total = subtotal * (int) qtySpinner.getValue();
            priceLabel.setText("Price: $" + String.format("%.2f", total));
        };

        sizeBox.addActionListener(e -> updatePrice.run());
        crustBox.addActionListener(e -> updatePrice.run());
        qtySpinner.addChangeListener(e -> updatePrice.run());
        for (JCheckBox cb : checks) cb.addActionListener(e -> updatePrice.run());
        updatePrice.run();

        JPanel buttons = new JPanel();
        JButton save = new JButton(isEdit ? "Save" : "Add");
        JButton cancel = new JButton("Cancel");

        save.addActionListener(e -> {
            ArrayList<String> toppings = new ArrayList<>();
            for (JCheckBox cb : checks)
                if (cb.isSelected()) toppings.add(cb.getText());

            if (isEdit) {
                editing.setSize((String) sizeBox.getSelectedItem());
                editing.setCrust((String) crustBox.getSelectedItem());
                editing.setToppings(toppings);
                editing.setQuantity((int) qtySpinner.getValue());
                JOptionPane.showMessageDialog(this,
                    "Order updated.\nClick 'List Orders' to refresh the display.");
            } else {
                manager.addOrder(new CustomPizza(
                    (String) sizeBox.getSelectedItem(),
                    (String) crustBox.getSelectedItem(),
                    toppings,
                    (int) qtySpinner.getValue(),
                    orderId
                ));
                JOptionPane.showMessageDialog(this,
                    "Pizza added! Order #" + orderId +
                    "\nClick 'List Orders' to view it.");
            }

            dialog.dispose();
        });

        cancel.addActionListener(e -> dialog.dispose());
        buttons.add(save);
        buttons.add(cancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ---------------------------
    // SEARCH ORDERS (uses linear search + polymorphic price)
// ---------------------------
    private void searchOrdersDialog() {
        String input = JOptionPane.showInputDialog(this,"Enter order number:");
        if (input == null) return;

        int id;
        try { id = Integer.parseInt(input); }
        catch (Exception e) { JOptionPane.showMessageDialog(this,"Invalid number."); return; }

        Pizza p = manager.searchByOrderId(id);
        if (p == null) {
            JOptionPane.showMessageDialog(this,"No order with that number.");
            return;
        }

        JOptionPane.showMessageDialog(this,
            "Order ID: " + p.getOrderId() +
            "\nSize: " + p.getSize() +
            "\nCrust: " + p.getCrust() +
            "\nToppings: " + String.join(", ", p.getToppings()) +
            "\nQuantity: " + p.getQuantity() +
            "\nTotal: $" + String.format("%.2f", p.calculatePrice())
        );
    }

    // ---------------------------
    // REFRESH OUTPUT – ONLY via "List Orders"
// Shows discount note when 5+ toppings
// ---------------------------
    private void refreshOutput() {
        StringBuilder sb = new StringBuilder();

        for (Pizza p : manager.getOrders()) {
            sb.append("Order ID: ").append(p.getOrderId()).append("\n");
            sb.append("Size: ").append(p.getSize()).append("\n");
            sb.append("Crust: ").append(p.getCrust()).append("\n");

            sb.append("Toppings (").append(p.getToppings().size()).append("): ")
              .append(String.join(", ", p.getToppings()))
              .append("\n");

            sb.append("Quantity: ").append(p.getQuantity()).append("\n");

            double price = p.calculatePrice(); // uses CustomPizza discount if applicable
            boolean discount = p.getToppings().size() >= 5;

            sb.append("Total: $").append(String.format("%.2f", price));
            if (discount) {
                sb.append("  (10% discount applied)");
            }
            sb.append("\n");

            sb.append("----------------------------------------\n\n");
        }

        outputArea.setText(sb.toString());
        toppingsLabel.setText("Total toppings: " + manager.countToppingsRecursive());
    }

    // ---------------------------
    // HELP WINDOW
    // ---------------------------
    private void showHelp() {
        String msg =
            "🍕 PIZZA BUILDER PRO – HELP & DOCUMENTATION 🍕\n\n" +
            "Welcome to Pizza Builder Pro! This program was created as an ICS4U1 Culminating Project.\n" +
            "It demonstrates Object-Oriented Programming, GUI design, algorithms, recursion, and file handling.\n\n" +

            "──────────────────────────────────────────────\n" +
            "PROGRAM FEATURES\n" +
            "──────────────────────────────────────────────\n" +
            "• Add new pizza orders\n" +
            "• Edit existing orders\n" +
            "• Delete orders\n" +
            "• Search for orders by Order ID\n" +
            "• List all orders in the display panel\n" +
            "• Save orders to a text file\n" +
            "• Load orders from a text file\n" +
            "• Sort orders by Order ID or Price\n" +
            "• Restore the original order\n" +
            "• Automatic price calculation\n" +
            "• Recursive topping counter\n\n" +

            "──────────────────────────────────────────────\n" +
            "HOW TO USE THE PROGRAM\n" +
            "──────────────────────────────────────────────\n" +
            "1. Click 'Add Pizza' to create a new order.\n" +
            "2. Click 'List Orders' to refresh the display.\n" +
            "3. Use 'Edit Order' to modify an order.\n" +
            "4. Use 'Search Orders' to find a specific order.\n" +
            "5. Use sorting buttons to organize orders.\n" +
            "6. Use 'Back to Default Order' to undo sorting.\n" +
            "7. Use 'Save Orders' and 'Load Orders' to store or retrieve data.\n\n" +

            "──────────────────────────────────────────────\n" +
            "TIPS\n" +
            "──────────────────────────────────────────────\n" +
            "• Sorting does NOT automatically update the list — click 'List Orders'.\n" +
            "• Adding a pizza does NOT refresh the list — click 'List Orders'.\n" +
            "• Sorting twice in the same mode will warn you.\n" +
            "• CustomPizza applies a discount for 5+ toppings.\n\n" +

            "──────────────────────────────────────────────\n" +
            "Course: ICS4U1 – Grade 12 Computer Science\n" +
            "──────────────────────────────────────────────\n";

        JTextArea textArea = new JTextArea(msg);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 550));

        JOptionPane.showMessageDialog(this, scrollPane, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------
    // ABOUT WINDOW
    // ---------------------------
    private void showAbout() {
        String msg =
            "🍕 ABOUT PIZZA BUILDER PRO 🍕\n\n" +

            "Course: ICS4U1 – Grade 12 Computer Science\n" +
            "School Year: 2025–2026\n\n" +

            "Pizza Builder Pro was created as a culminating project to demonstrate:\n" +
            "• Object-Oriented Programming (OOP)\n" +
            "• Java Swing GUI development\n" +
            "• Sorting algorithms (Bubble Sort)\n" +
            "• Searching algorithms (Linear Search)\n" +
            "• Recursion (topping counter)\n" +
            "• File handling (save/load orders)\n\n" +

            "This program allows users to create, edit, sort, and manage pizza orders\n" +
            "in a clean and interactive interface.\n\n" +

            "Thank you for using Pizza Builder Pro!\n";
        JOptionPane.showMessageDialog(this, msg, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------
    // MAIN
    // ---------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PizzaBuilderPro2().setVisible(true));
    }
}