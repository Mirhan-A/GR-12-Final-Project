package pizzabuilderpro2;

// ===============================
// PIZZA CLASS (Encapsulation + OOP)
// ===============================
class Pizza {
    private String size;
    private String crust;
    private java.util.ArrayList<String> toppings;
    private int quantity;

    public Pizza(String size, String crust, java.util.ArrayList<String> toppings, int quantity) {
        this.size = size;
        this.crust = crust;
        this.toppings = toppings;
        this.quantity = quantity;
    }

    // Getters
    public String getSize() { return size; }
    public String getCrust() { return crust; }
    public java.util.ArrayList<String> getToppings() { return toppings; }
    public int getQuantity() { return quantity; }

    // Setters (used for editing)
    public void setSize(String s) { size = s; }
    public void setCrust(String c) { crust = c; }
    public void setToppings(java.util.ArrayList<String> t) { toppings = t; }
    public void setQuantity(int q) { quantity = q; }

    // Polymorphism: can be overridden
    public double calculatePrice() {
        double price = 5.0;

        if (size.equals("Medium")) price += 3;
        if (size.equals("Large")) price += 5;

        if (crust.equals("Thin")) price += 1;
        if (crust.equals("Deep Dish")) price += 2;

        price += toppings.size() * 1.5;

        return price * quantity;
    }

    // Convert to file format
    public String toFileString() {
        return size + "," + crust + "," + quantity + "," +
                String.join("|", toppings);
    }

    @Override
    public String toString() {
        return "Size: " + size +
               ", Crust: " + crust +
               ", Qty: " + quantity +
               ", Toppings: " + toppings +
               ", Price: $" + String.format("%.2f", calculatePrice());
    }
}

// ===============================
// CUSTOM PIZZA (Inheritance + Polymorphism)
// ===============================
class CustomPizza extends Pizza {
    public CustomPizza(String size, String crust, java.util.ArrayList<String> toppings, int quantity) {
        super(size, crust, toppings, quantity);
    }

    @Override
    public double calculatePrice() {
        double price = super.calculatePrice();
        if (getToppings().size() >= 5) {
            price *= 0.9; // 10% discount
        }
        return price;
    }
}

// ===============================
// ORDER MANAGER (Sorting, Searching, Recursion, File I/O)
// ===============================
class OrderManager {
    private java.util.ArrayList<Pizza> orders = new java.util.ArrayList<>();

    public void addOrder(Pizza p) {
        orders.add(p);
    }

    public java.util.ArrayList<Pizza> getOrders() {
        return orders;
    }

    // Bubble Sort by price
    public void sortByPrice() {
        for (int i = 0; i < orders.size() - 1; i++) {
            for (int j = 0; j < orders.size() - i - 1; j++) {
                if (orders.get(j).calculatePrice() > orders.get(j + 1).calculatePrice()) {
                    java.util.Collections.swap(orders, j, j + 1);
                }
            }
        }
    }

    // Linear Search by size
    public java.util.ArrayList<Pizza> searchBySize(String size) {
        java.util.ArrayList<Pizza> result = new java.util.ArrayList<>();
        for (Pizza p : orders) {
            if (p.getSize().equalsIgnoreCase(size)) {
                result.add(p);
            }
        }
        return result;
    }

    // Recursion: count total toppings
    public int countToppingsRecursive() {
        return countHelper(0);
    }

    private int countHelper(int index) {
        if (index == orders.size()) return 0;
        return orders.get(index).getToppings().size() + countHelper(index + 1);
    }

    // Save to file
    public void saveToFile() throws java.io.IOException {
        java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("orders.txt"));
        for (Pizza p : orders) {
            bw.write(p.toFileString());
            bw.newLine();
        }
        bw.close();
    }

    // Load from file
    public void loadFromFile() throws java.io.IOException {
        orders.clear();
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("orders.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            String size = parts[0];
            String crust = parts[1];
            int qty = Integer.parseInt(parts[2]);

            java.util.ArrayList<String> tops = new java.util.ArrayList<>();
            if (parts.length > 3 && !parts[3].isEmpty()) {
                for (String t : parts[3].split("\\|")) {
                    tops.add(t);
                }
            }

            orders.add(new CustomPizza(size, crust, tops, qty));
        }
        br.close();
    }
}