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
    private String size, crust, date;
    private ArrayList<String> toppings;
    private ArrayList<Drinks> drinks;
    private int quantity;
    private int orderId;

    public Pizza(String size, String crust, ArrayList<String> toppings,
                 ArrayList<Drinks> drinks, int quantity, int orderId, String date) {
        this.size = size;
        this.crust = crust;
        this.toppings = toppings;
        this.drinks = drinks;
        this.quantity = quantity;
        this.orderId = orderId;
        this.date = date;
    }

    public String getSize() { return size; }
    public String getCrust() { return crust; }
    public ArrayList<String> getToppings() { return toppings; }
    public ArrayList<Drinks> getDrinks() { return drinks; }
    public int getQuantity() { return quantity; }
    public int getOrderId() { return orderId; }
    public String getDate() { return date; }

    public void setSize(String s) { size = s; }
    public void setCrust(String c) { crust = c; }
    public void setToppings(ArrayList<String> t) { toppings = t; }
    public void setDrinks(ArrayList<Drinks> d) { drinks = d; }
    public void setQuantity(int q) { quantity = q; }
    public void setDate(String d) { date = d; }

    public double calculatePrice() {
        double price = 0;
        
        if (size.equals("Small")) price += 6;
        else if (size.equals("Medium")) price += 8;
        else if (size.equals("Large")) price += 10;

        if (crust.equals("Thin")) price += 1;
        else if (crust.equals("Deep Dish")) price += 2;

        price += toppings.size() * 0.75;

        if (drinks != null) {
            for (Drinks d : drinks) price += d.getPrice();
        }

        return price * quantity;
    }
}

// ---------------------------
// CUSTOM PIZZA (OOP: inheritance + polymorphism)
// ---------------------------
class CustomPizza extends Pizza {
    public CustomPizza(String size, String crust, ArrayList<String> toppings,
                       ArrayList<Drinks> drinks, int quantity, int orderId, String date) {
        super(size, crust, toppings, drinks, quantity, orderId, date);
    }

    @Override
    public double calculatePrice() {
        double price = super.calculatePrice();
        if (getToppings().size() >= 5) price *= 0.9;
        return price;
    }
}

// ---------------------------
// DRINKS CLASS
// ---------------------------
class Drinks {
    private String name;
    private double price;

    public Drinks(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

// ---------------------------
// ORDER MANAGER
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

    public int generateOrderId() {
        int id;
        do { id = 100 + rand.nextInt(2001); }
        while (usedIds.contains(id));
        usedIds.add(id);
        return id;
    }

    public Pizza searchByOrderId(int id) {
        for (Pizza p : orders)
            if (p.getOrderId() == id) return p;
        return null;
    }

    public int countToppingsRecursive() { return countHelper(0); }
    private int countHelper(int i) {
        if (i == orders.size()) return 0;
        return orders.get(i).getToppings().size() + countHelper(i + 1);
    }

    public void sortByOrderId() {
        for (int i = 0; i < orders.size() - 1; i++)
            for (int j = 0; j < orders.size() - i - 1; j++)
                if (orders.get(j).getOrderId() > orders.get(j + 1).getOrderId()) {
                    Pizza temp = orders.get(j);
                    orders.set(j, orders.get(j + 1));
                    orders.set(j + 1, temp);
                }
    }

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
// MAIN GUI CLASS
// ---------------------------
public class PizzaBuilderPro2 extends JFrame {

    private JTextArea outputArea;
    private JLabel toppingsLabel;
    private OrderManager manager = new OrderManager();
    private String currentSortMode = "default";

    public PizzaBuilderPro2() {
        setTitle("Pizza Builder Pro");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(titlePanel(), BorderLayout.NORTH);
        add(leftPanel(), BorderLayout.WEST);
        add(centerPanel(), BorderLayout.CENTER);
        add(bottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel titlePanel() {
        JPanel p = new JPanel();
        JLabel title = new JLabel("Pizza & Drink Builder Pro");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        p.add(title);
        return p;
    }

    private String[] getDaysForMonth(int year, int month) {
        int daysInMonth;
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12: daysInMonth = 31; break;
            case 4: case 6: case 9: case 11: daysInMonth = 30; break;
            case 2:
                boolean leap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                daysInMonth = leap ? 29 : 28;
                break;
            default: daysInMonth = 31;
        }
        String[] days = new String[daysInMonth];
        for (int i = 0; i < daysInMonth; i++)
            days[i] = String.format("%02d", i + 1);
        return days;
    }

    private JPanel leftPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.setBorder(new TitledBorder("Controls"));

        String[] names = {
            "Add Pizza/Drink","List Orders","Edit Order","Search Orders",
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
        b[5].addActionListener(e -> {
            if (manager.getOrders().isEmpty()) {
                JOptionPane.showMessageDialog(this,"There are no orders to clear.","Nothing to Clear",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this,"Clear ALL orders?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                manager.clearAll();
                JOptionPane.showMessageDialog(this,"All orders cleared.\nClick 'List Orders' to refresh.");
            }
        });

        b[6].addActionListener(e -> saveOrdersToFile());
        b[7].addActionListener(e -> loadOrdersFromFile());

        b[8].addActionListener(e -> {
    if (manager.getOrders().isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "There are no orders to sort.",
            "Nothing to Sort",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (currentSortMode.equals("id")) {
        JOptionPane.showMessageDialog(this,"Already sorted by Order ID.");
        return;
    }

    manager.sortByOrderId();
    currentSortMode = "id";
    JOptionPane.showMessageDialog(this,"Sorted by Order ID.\nClick 'List Orders' to view.");
});

        b[9].addActionListener(e -> {
    if (manager.getOrders().isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "There are no orders to sort.",
            "Nothing to Sort",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    String[] options = {"Highest Price","Lowest Price","Cancel"};
    int choice = JOptionPane.showOptionDialog(this,"How would you like to sort the prices?",
        "Sort by Price", JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

    if (choice == 0) {
        if (currentSortMode.equals("price_high")) {
            JOptionPane.showMessageDialog(this,"Already sorted by Highest Price.");
            return;
        }
        manager.sortByPrice();
        Collections.reverse(manager.getOrders());
        currentSortMode = "price_high";
        JOptionPane.showMessageDialog(this,"Sorted by Highest Price.\nClick 'List Orders' to view.");
    }
    else if (choice == 1) {
        if (currentSortMode.equals("price_low")) {
            JOptionPane.showMessageDialog(this,"Already sorted by Lowest Price.");
            return;
        }
        manager.sortByPrice();
        currentSortMode = "price_low";
        JOptionPane.showMessageDialog(this,"Sorted by Lowest Price.\nClick 'List Orders' to view.");
    }
});

        b[10].addActionListener(e -> {
    if (manager.getOrders().isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "There are no orders to restore.",
            "Nothing to Restore",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (currentSortMode.equals("default")) {
        JOptionPane.showMessageDialog(this,"Already in default order.");
        return;
    }

    manager.restoreDefaultOrder();
    currentSortMode = "default";
    JOptionPane.showMessageDialog(this,"Order list restored.\nClick 'List Orders' to view.");
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
                "Order deleted.\nClick 'List Orders' to refresh.");
        }
    }

    // ---------------------------
    // SAVE ORDERS (with drinks)
// ---------------------------
    private void saveOrdersToFile() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("orders.txt"));
            for (Pizza p : manager.getOrders()) {
                writer.println("Order ID: " + p.getOrderId());
                writer.println("Size: " + p.getSize());
                writer.println("Crust: " + p.getCrust());
                writer.println("Toppings: " + String.join(", ", p.getToppings()));

                StringBuilder drinkNames = new StringBuilder();
                if (p.getDrinks() != null && !p.getDrinks().isEmpty()) {
                    for (Drinks d : p.getDrinks()) {
                        drinkNames.append(d.getName()).append(", ");
                    }
                    if (drinkNames.length() >= 2)
                        drinkNames.setLength(drinkNames.length() - 2);
                }
                writer.println("Drinks: " + drinkNames.toString());

                writer.println("Quantity: " + p.getQuantity());
                writer.println("Date: " + p.getDate());
                writer.println("Total: " + p.calculatePrice());
                writer.println("---");
            }
            writer.close();
            JOptionPane.showMessageDialog(this, "Saved to orders.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving file.");
        }
    }

    // ---------------------------
    // LOAD ORDERS (with optional drinks)
// ---------------------------
    private void loadOrdersFromFile() {
        try {
            if (!manager.getOrders().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Orders already loaded.\nClear first to reload.",
                    "Already Loaded",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            File file = new File("orders.txt");
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "orders.txt not found.");
                return;
            }

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

                    String drinksLine = reader.readLine();
                    ArrayList<Drinks> drinks = new ArrayList<>();
                    if (drinksLine != null && drinksLine.startsWith("Drinks:")) {
                        String drinkData = drinksLine.substring(8).trim();
                        if (!drinkData.isEmpty()) {
                            String[] drinkNames = drinkData.split(", ");
                            for (String dn : drinkNames) {
                                double price = getDrinkPriceByName(dn);
                                drinks.add(new Drinks(dn, price));
                            }
                        }
                    }

                    int quantity = Integer.parseInt(reader.readLine().substring(10).trim());
                    String date = reader.readLine().substring(6).trim();

                    reader.readLine(); // skip total
                    reader.readLine(); // skip ---

                    manager.addOrder(new CustomPizza(size, crust, toppings, drinks, quantity, id, date));
                }
            }

            reader.close();
            JOptionPane.showMessageDialog(this,
                "Orders loaded.\nClick 'List Orders' to view.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file.");
        }
    }

    private double getDrinkPriceByName(String name) {
        String n = name.toLowerCase();
        if (n.contains("pepsi") || n.contains("coca-cola") || n.contains("coca cola")
            || n.contains("7-up") || n.contains("7up") || n.contains("sprite")
            || n.contains("diet pepsi") || n.contains("diet coca-cola") || n.contains("diet coca cola"))
            return 2.00;
        if (n.contains("juice") || n.contains("fruit punch"))
            return 1.50;
        return 1.75;
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

    dialog.setSize(450, 750);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JPanel form = new JPanel();
    form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
    form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    String[] sizes = {"None","Small","Medium","Large"};
    String[] crusts = {"None","Regular","Thin","Deep Dish"};
    String[] toppingOptions = {
        "Cheese","Pepperoni","Mushrooms","Onions","Bacon","Olives",
        "Peppers","Pineapple","Sausage","Ham","Tomatoes","Spinach",
        "Jalapenos","Chicken","Ground Beef"
    };

    String[] drinkOptions = {
        "Pepsi", "Coca-Cola", "7-Up", "Sprite",
        "Diet Pepsi", "Diet Coca-Cola",
        "Orange Juice", "Apple Juice", "Mango Juice",
        "Grape Juice", "Fruit Punch",
        "Water", "Lemonade", "Iced Tea", "Chocolate Milk"
    };

    double[] drinkPrices = {
        2.00, 2.00, 2.00, 2.00, 2.00, 2.00,
        1.50, 1.50, 1.50, 1.50, 1.50,
        1.25, 1.75, 1.75, 1.75
    };

    JComboBox<String> sizeBox = new JComboBox<>(sizes);
    JComboBox<String> crustBox = new JComboBox<>(crusts);
    JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1,1,20,1));
    
    if (isEdit) {
        sizeBox.setSelectedItem(editing.getSize());
        crustBox.setSelectedItem(editing.getCrust());
        qtySpinner.setValue(editing.getQuantity());
    }

    form.add(new JLabel("Size:"));
    form.add(sizeBox);
    form.add(new JLabel("Crust:"));
    form.add(crustBox);
    form.add(new JLabel("Quantity:"));
    form.add(qtySpinner);

    form.add(new JLabel("Order Date (YYYY-MM-DD):"));

    int startYear = 2000;
    int endYear = 2060;
    String[] years = new String[endYear - startYear + 1];
    for (int i = 0; i < years.length; i++)
        years[i] = "" + (startYear + i);

    JComboBox<String> yearBox = new JComboBox<>(years);
    yearBox.setSelectedItem("2026");

    String[] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    JComboBox<String> monthBox = new JComboBox<>(months);

    int initialYear = Integer.parseInt((String) yearBox.getSelectedItem());
    int initialMonth = Integer.parseInt((String) monthBox.getSelectedItem());
    JComboBox<String> dayBox = new JComboBox<>(getDaysForMonth(initialYear, initialMonth));

    JPanel datePanel = new JPanel();
    datePanel.add(yearBox);
    datePanel.add(new JLabel("-"));
    datePanel.add(monthBox);
    datePanel.add(new JLabel("-"));
    datePanel.add(dayBox);

    form.add(datePanel);

    yearBox.addActionListener(e -> {
        int y = Integer.parseInt((String) yearBox.getSelectedItem());
        int m = Integer.parseInt((String) monthBox.getSelectedItem());
        dayBox.setModel(new DefaultComboBoxModel<>(getDaysForMonth(y, m)));
    });

    monthBox.addActionListener(e -> {
        int y = Integer.parseInt((String) yearBox.getSelectedItem());
        int m = Integer.parseInt((String) monthBox.getSelectedItem());
        dayBox.setModel(new DefaultComboBoxModel<>(getDaysForMonth(y, m)));
    });

    if (isEdit) {
        String[] parts = editing.getDate().split("-");
        if (parts.length == 3) {
            yearBox.setSelectedItem(parts[0]);
            monthBox.setSelectedItem(parts[1]);
            int y = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            dayBox.setModel(new DefaultComboBoxModel<>(getDaysForMonth(y, m)));
            dayBox.setSelectedItem(parts[2]);
        }
    }

    // Toppings
    JPanel topsPanel = new JPanel(new GridLayout(0,3,5,5));
    JCheckBox[] checks = new JCheckBox[toppingOptions.length];

    for (int i = 0; i < toppingOptions.length; i++) {
        checks[i] = new JCheckBox(toppingOptions[i]);
        if (isEdit && editing.getToppings().contains(toppingOptions[i])) checks[i].setSelected(true);
        topsPanel.add(checks[i]);
    }

    JScrollPane scroll = new JScrollPane(topsPanel);
    scroll.setPreferredSize(new Dimension(380,180));
    form.add(new JLabel("Toppings:"));
    form.add(scroll);

    // Drinks with quantity spinners (0–100), scrollable
    JPanel drinksPanel = new JPanel(new GridLayout(0,2,5,5));
    JSpinner[] drinkSpinners = new JSpinner[drinkOptions.length];

    // Pre-count existing drinks if editing
    Map<String, Integer> drinkCounts = new HashMap<>();
    if (isEdit && editing.getDrinks() != null) {
        for (Drinks d : editing.getDrinks()) {
            drinkCounts.put(d.getName(), drinkCounts.getOrDefault(d.getName(), 0) + 1);
        }
    }

    for (int i = 0; i < drinkOptions.length; i++) {
        JPanel row = new JPanel(new BorderLayout());
        JLabel label = new JLabel(drinkOptions[i]);
        int initialQty = 0;
        if (isEdit) {
            initialQty = drinkCounts.getOrDefault(drinkOptions[i], 0);
        }
        drinkSpinners[i] = new JSpinner(new SpinnerNumberModel(initialQty, 0, 100, 1));

        row.add(label, BorderLayout.WEST);
        row.add(drinkSpinners[i], BorderLayout.EAST);
        drinksPanel.add(row);
    }

    JScrollPane drinkScroll = new JScrollPane(drinksPanel);
    drinkScroll.setPreferredSize(new Dimension(380,140));
    form.add(new JLabel("Drinks (quantity per item):"));
    form.add(drinkScroll);

    JLabel priceLabel = new JLabel("Price: $0.00");
    priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
    form.add(priceLabel);

    Runnable updatePrice = () -> {
        double sizePrice = 0;
        if (sizeBox.getSelectedItem().equals("Small")) sizePrice = 6;
        else if (sizeBox.getSelectedItem().equals("Medium")) sizePrice = 8;
        else if (sizeBox.getSelectedItem().equals("Large")) sizePrice = 10;

        double crustPrice = 0;
        if (crustBox.getSelectedItem().equals("Thin")) crustPrice = 1;
        else if (crustBox.getSelectedItem().equals("Deep Dish")) crustPrice = 2;

        int toppingCount = 0;
        for (JCheckBox cb : checks) if (cb.isSelected()) toppingCount++;

        double drinksTotal = 0;
        for (int i = 0; i < drinkSpinners.length; i++) {
            int qty = (int) drinkSpinners[i].getValue();
            drinksTotal += qty * drinkPrices[i];
        }

        double subtotal = sizePrice + crustPrice + (toppingCount * 0.75) + drinksTotal;
        if (toppingCount >= 5) subtotal *= 0.9;

        double total = subtotal * (int) qtySpinner.getValue();
        priceLabel.setText("Price: $" + String.format("%.2f", total));
    };

    sizeBox.addActionListener(e -> updatePrice.run());
    crustBox.addActionListener(e -> updatePrice.run());
    qtySpinner.addChangeListener(e -> updatePrice.run());
    for (JCheckBox cb : checks) cb.addActionListener(e -> updatePrice.run());
    for (JSpinner sp : drinkSpinners) sp.addChangeListener(e -> updatePrice.run());
    updatePrice.run();

    JPanel buttons = new JPanel();
    JButton save = new JButton(isEdit ? "Save" : "Add");
    JButton cancel = new JButton("Cancel");
    
    save.addActionListener(e -> {
        ArrayList<String> toppings = new ArrayList<>();
        for (JCheckBox cb : checks)
            if (cb.isSelected()) toppings.add(cb.getText());

        ArrayList<Drinks> drinks = new ArrayList<>();
        for (int i = 0; i < drinkSpinners.length; i++) {
            int qty = (int) drinkSpinners[i].getValue();
            for (int q = 0; q < qty; q++) {
                drinks.add(new Drinks(drinkOptions[i], drinkPrices[i]));
            }
        }

        String date = yearBox.getSelectedItem() + "-" +
                      monthBox.getSelectedItem() + "-" +
                      dayBox.getSelectedItem();
        
        // ---------------------------
// VALIDATION BLOCK
// ---------------------------

String size = (String) sizeBox.getSelectedItem();
String crust = (String) crustBox.getSelectedItem();

boolean hasToppings = !toppings.isEmpty();

boolean hasDrinks = false;
for (int i = 0; i < drinkSpinners.length; i++) {
    if ((int) drinkSpinners[i].getValue() > 0) {
        hasDrinks = true;
        break;
    }
}

if (!crust.equals("Regular") && !crust.equals("Thin") && !crust.equals("Deep Dish")) {
    if (!size.equals("None")) {
        JOptionPane.showMessageDialog(dialog,
            "Please select a crust type for your pizza.",
            "Missing Crust",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
}

if (!size.equals("None") && !hasToppings) {
    JOptionPane.showMessageDialog(dialog,
        "Please select at least one topping for a pizza.",
        "Missing Toppings",
        JOptionPane.WARNING_MESSAGE);
    return;
}

if (size.equals("None") && hasToppings && !hasDrinks) {
    JOptionPane.showMessageDialog(dialog,
        "You cannot order toppings without a pizza.\nPlease select a pizza size or remove toppings.",
        "Invalid Order",
        JOptionPane.WARNING_MESSAGE);
    return;
}

double finalPrice = Double.parseDouble(priceLabel.getText().replace("Price: $", ""));
if (finalPrice <= 0) {
    JOptionPane.showMessageDialog(dialog,
        "You must select at least one pizza item or drink.\nOrder total cannot be $0.",
        "Invalid Order",
        JOptionPane.WARNING_MESSAGE);
    return;
}

// ---------------------------
// END VALIDATION BLOCK
// ---------------------------

        if (isEdit) {
            editing.setSize((String) sizeBox.getSelectedItem());
            editing.setCrust((String) crustBox.getSelectedItem());
            editing.setToppings(toppings);
            editing.setDrinks(drinks);
            editing.setQuantity((int) qtySpinner.getValue());
            editing.setDate(date);
            JOptionPane.showMessageDialog(this,
                "Order updated.\nClick 'List Orders' to refresh.");
        } else {
            manager.addOrder(new CustomPizza(
                (String) sizeBox.getSelectedItem(),
                (String) crustBox.getSelectedItem(),
                toppings,
                drinks,
                (int) qtySpinner.getValue(),
                orderId,
                date
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

        StringBuilder drinkNames = new StringBuilder();
        if (p.getDrinks() == null || p.getDrinks().isEmpty()) {
            drinkNames.append("None");
        } else {
            for (Drinks d : p.getDrinks()) {
                drinkNames.append(d.getName()).append(", ");
            }
            if (drinkNames.length() >= 2)
                drinkNames.setLength(drinkNames.length() - 2);
        }

        JOptionPane.showMessageDialog(this,
            "Order ID: " + p.getOrderId() +
            "\nSize: " + p.getSize() +
            "\nCrust: " + p.getCrust() +
            "\nDate: " + p.getDate() +
            "\nToppings: " + String.join(", ", p.getToppings()) +
            "\nDrinks: " + drinkNames +
            "\nQuantity: " + p.getQuantity() +
            "\nTotal: $" + String.format("%.2f", p.calculatePrice())
        );
    }

    private void refreshOutput() {
        StringBuilder sb = new StringBuilder();

        for (Pizza p : manager.getOrders()) {
            sb.append("Order ID: ").append(p.getOrderId()).append("\n");
            sb.append("Size: ").append(p.getSize()).append("\n");
            sb.append("Crust: ").append(p.getCrust()).append("\n");
            sb.append("Date: ").append(p.getDate()).append("\n");

            sb.append("Toppings (").append(p.getToppings().size()).append("): ")
              .append(String.join(", ", p.getToppings()))
              .append("\n");

            sb.append("Drinks: ");
            if (p.getDrinks() == null || p.getDrinks().isEmpty()) {
                sb.append("None\n");
            } else {
                for (Drinks d : p.getDrinks()) {
                    sb.append(d.getName()).append(", ");
                }
                if (sb.length() >= 2)
                    sb.setLength(sb.length() - 2);
                sb.append("\n");
            }

            sb.append("Quantity: ").append(p.getQuantity()).append("\n");

            double price = p.calculatePrice();
            boolean discount = p.getToppings().size() >= 5;

            sb.append("Total: $").append(String.format("%.2f", price));
            if (discount) sb.append("  (10% discount applied)");
            sb.append("\n");

            sb.append("----------------------------------------\n\n");
        }

        outputArea.setText(sb.toString());
        toppingsLabel.setText("Total toppings: " + manager.countToppingsRecursive());
    }

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
        "• Recursive topping counter\n" +
        "• Dynamic date picker (2000–2060) with leap-year support\n" +
        "• Drinks menu with sodas, juices, and other beverages\n" +
        "• Drink-only orders supported\n" +
        "• Prevents $0 orders\n" +
        "• Prevents pizza with no crust\n\n" +

        "──────────────────────────────────────────────\n" +
        "HOW TO USE THE PROGRAM\n" +
        "──────────────────────────────────────────────\n" +
        "1. Click 'Add Pizza' to create a new order.\n" +
        "2. Select size, crust, toppings, drinks, and date.\n" +
        "3. Click 'List Orders' to refresh the display.\n" +
        "4. Use 'Edit Order' to modify an order.\n" +
        "5. Use 'Search Orders' to find a specific order.\n" +
        "6. Use sorting buttons to organize orders.\n" +
        "7. Use 'Back to Default Order' to undo sorting.\n" +
        "8. Use 'Save Orders' and 'Load Orders' to store or retrieve data.\n\n" +

        "──────────────────────────────────────────────\n" +
        "TIPS\n" +
        "──────────────────────────────────────────────\n" +
        "• Sorting does NOT automatically update the list — click 'List Orders'.\n" +
        "• Adding a pizza does NOT refresh the list — click 'List Orders'.\n" +
        "• CustomPizza applies a discount for 5+ toppings.\n" +
        "• Drinks have different prices: sodas $2.00, juices $1.50, others $1.75.\n" +
        "• Date picker automatically adjusts for 30/31 days and leap years.\n" +
        "• Drink-only orders allowed.\n" +
        "• Pizza cannot be added without a crust.\n" +
        "• Orders cannot total $0.\n\n" +

        "Course: ICS4U1 – Grade 12 Computer Science\n";

    JTextArea textArea = new JTextArea(msg);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(500, 400));

    JDialog dialog = new JDialog(this, "Help", true);
    dialog.add(scrollPane);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}


private void showAbout() {
    String msg =
        "🍕 Pizza Builder Pro\n\n" +
        "ICS4U1 Culminating Project\n\n" +
        "This program demonstrates:\n" +
        "- Object-Oriented Programming\n" +
        "- Inheritance & Polymorphism\n" +
        "- GUI Design with Swing\n" +
        "- Sorting Algorithms\n" +
        "- Searching Algorithms\n" +
        "- Recursion\n" +
        "- File Handling (Save/Load)\n" +
        "- Dynamic Date Picker\n" +
        "- Drink & Pizza Builder System\n" +
        "- Price Calculation System\n" +
        "- Drink-only Orders\n" +
        "- Prevention of $0 Orders\n" +
        "- Crust Requirement for Pizza\n\n" +
        "Course: ICS4U1 – Grade 12 Computer Science\n";

    JTextArea textArea = new JTextArea(msg);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JDialog dialog = new JDialog(this, "About", true);
    dialog.add(scrollPane);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PizzaBuilderPro2().setVisible(true);
        });
    }
}