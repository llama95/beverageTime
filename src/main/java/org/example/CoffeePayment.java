package org.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class CoffeePayment {
    public static void main(String[] args) {
        CoffeePayment paymentSystem = new CoffeePayment();
        paymentSystem.startCoffeePayment();
    }

    // Map to hold spending/paid values
    private Map<String, Double> coworkerSpending = new HashMap<>();
    private Map<String, Double> coworkerPaid = new HashMap<>();
    private double totalSpending = 0;
    //dummy init
    public CoffeePayment() {
        coworkerSpending.put("Bob", 0.0);
        coworkerSpending.put("Jeremy", 0.0);
        coworkerSpending.put("Coworker 3", 0.0);
        coworkerSpending.put("Coworker 4", 0.0);
        coworkerSpending.put("Coworker 5", 0.0);
        coworkerSpending.put("Coworker 6", 0.0);
        coworkerSpending.put("Coworker 7", 0.0);
    }

    public void startCoffeePayment() {
        // dummy menu
        String[] menu = {
                "1. Americano - $3.50",
                "2. Double Espresso - $6.00",
                "3. Black Coffee - $3.50",
                "4. Irish Coffee - $8.75",
                "5. Flat White- $6.25",
                "6. Chai Tea - $8.00",
                "7. Cappuccino - $5.50",
                "8. Iced Coffee - $2.75",
                "9. Frappuccino - $5.00",
                "10. Green Tea - $6.75"
        };
        Scanner scanner = new Scanner(System.in);
        // determine if were simulating or collecting user input
        String mode = selectMode(scanner);
        Random random = new Random();
        int year = 0;
        // manual
        if (mode.equalsIgnoreCase("M")) {
            year = getYear(scanner);
        } else if (mode.equalsIgnoreCase("S")) { // simulated
            year = random.nextInt(7) + 2024; // random # betweeen 2024 and 2024 + 7 (6 bc index)
        }
        int workDays = getWorkDaysInYear(year);
        System.out.println("Work days in " + year + ": " + workDays);
        // loop for each work day in the year
        for (int day = 1; day <= workDays; day++) {
            double daysCost = 0;
            System.out.println("\nDay " + day + ":");
            // Determine the payer for the day
            List<String> sortedList = new ArrayList<>(coworkerSpending.keySet());
            // put our list in abc order
            Collections.sort(sortedList);
            for (String coworker : sortedList) {
                System.out.println("\n" + coworker + ", select your drink:");
                int choice = 0;
                for (String item : menu) {
                    System.out.println("    " + item);
                }
                if (coworker.equalsIgnoreCase("bob") ) {
                    choice = 7;
                } else if (coworker.equalsIgnoreCase("jeremy") ) {
                    choice = 3;
                } else if (mode.equalsIgnoreCase("M")) {
                    choice =  getChoice(scanner);
                } else if (mode.equalsIgnoreCase("S")) {
                    choice = random.nextInt(10) + 1;
                }
                double cost = getPrice(menu[choice - 1]);
                this.recordSpending(coworker, cost);
                System.out.println(coworker + " selected: " + menu[choice - 1]);
                daysCost += cost;
            }
            // use our alg to determine who is paying today
            String todaysPayer = this.calculateWeights();
            System.out.println("\n*** Today's payer: " + todaysPayer + " paying: $" + daysCost + " ***");
            System.out.println("\nTotal cost for each coworker after Day " + day + ":");
            double cost = 0;
            DecimalFormat dr = new DecimalFormat("#.##");
            for (String key : sortedList) {
                cost = cost + coworkerSpending.get(key);
                // if the value (double) when floored is equal to the original value its a whole number
                outputTotalCost(coworkerSpending,key, dr);
            }
            System.out.println("\nTotal amount paid by each coworker after Day " + day + ":");
            if (coworkerPaid.containsKey(todaysPayer)) {
                double currentCost = coworkerPaid.get(todaysPayer);
                coworkerPaid.put(todaysPayer, currentCost + daysCost);
            } else {
                coworkerPaid.put(todaysPayer, daysCost);
            }
            List<String> sortedListPaid = new ArrayList<>(coworkerPaid.keySet());
            Collections.sort(sortedListPaid);
            for (String key : sortedListPaid) {
                outputTotalCost(coworkerPaid,key, dr);
            }
            outputTotalSpent(cost, dr);
            System.out.println("\n---------------------------------------------------------");
        }
        System.out.println("\n" + year + " is complete. Program Finished");
        scanner.close();
    }

    private void outputTotalSpent(double cost, DecimalFormat dr) {
        if (cost == Math.floor(cost)) {
            System.out.println("\nTotal spent as a group on coffee this year: $" + dr.format(cost));
        } else {
            System.out.print("\nTotal spent as a group on coffee this year: $");
            System.out.printf("%.2f%n", cost);
        }
    }

    // properly format our double as ints when theyre whole numbers
    private void outputTotalCost(Map<String, Double> coworkerSpending,String key, DecimalFormat dr) {
        if (coworkerSpending.get(key) == Math.floor(coworkerSpending.get(key))) {
            System.out.println("    " + key + ": $" + dr.format(coworkerSpending.get(key)));
        } else {
            System.out.print("    " + key + ": $");
            System.out.printf("%.2f%n", coworkerSpending.get(key));
        }
    }

    // Record spending for a coworker
    public void recordSpending(String coworker, double spending) {
        coworkerSpending.put(coworker, coworkerSpending.get(coworker) + spending);
        totalSpending += spending;
    }
    // Determine weights for each workers order
    public String calculateWeights() {
        Map<String, Double> weights = new HashMap<>();
        for (String coworker : coworkerSpending.keySet()) {
            weights.put(coworker, coworkerSpending.get(coworker) / totalSpending);
        }
        return selectRandomly(weights);
    }

    public String selectRandomly(Map<String, Double> weights) {
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        double random = Math.random() * totalWeight;
        //we generate a random number within the range of the total weight and then subtract each option's weight from it until we get zero.
        // Options with higher weights will have a greater chance of being selected and we avoid iterating through the entire map each time.
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            random -= entry.getValue();
            if (random <= 0) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Weights not properly set or invalid.");
    }

    public void coffeeAnimation() {
        // some fun ascii
        System.out.println("  _   _   _   _   _   _ ");
        System.out.println(" / \\ / \\ / \\ / \\ / \\ / \\");
        System.out.println("( c | o | f | f | e | e )");
        System.out.println(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");
    }

    private String selectMode(Scanner scanner) {
        // determine if were simulating or collecting user input
        while (true) {
            coffeeAnimation();
            System.out.println("\nSimulate (S) or Manual (M)?:");
            String simOrManual = scanner.nextLine();
            if (simOrManual.equalsIgnoreCase("M")) {
                return "M";
            }
            else if (simOrManual.equalsIgnoreCase("S")) {
                return "S";
            }
            else if (!simOrManual.equalsIgnoreCase("S") || !simOrManual.equalsIgnoreCase("M")) {
                System.out.println("Please Enter S or M;");
            }
        }
    }

    private int getChoice(Scanner scanner) {
        int choice;
        do {
            System.out.println("Enter your choice (1-10):");
            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid choice (1-10):");
                scanner.next(); // Consume invalid input
            }
            choice = scanner.nextInt();
            if (choice < 1 || choice > 10) {
                System.out.println("The choice must be between 1 and 10");
            }
        } while (choice < 1 || choice > 10);
        return choice;
    }

    private int getYear(Scanner scanner) {
        int year;
        do {
            System.out.println("Enter the year (2024-2030):");
            while (!scanner.hasNextInt()) {
                scanner.next(); // Consume invalid input
            }
            year = scanner.nextInt();
        } while (year < 2024 || year > 2030);
        return year;
    }

    // work days per year vary
    private int getWorkDaysInYear(int year) {
        int workDays = 0;
        boolean isLeapYear = (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
        // Assign work days based on the year
        switch (year) {
            case 2024:
            case 2025:
            case 2029:
            case 2030:
                workDays = 252;
                break;
            case 2026:
            case 2027:
                workDays = 251;
                break;
            case 2028:
                workDays = isLeapYear ? 253 : 252;
                break;
            default:
                System.out.println("Year not in range 2024-2030");
        }
        return workDays;
    }

    private double getPrice(String menuItem) {
        int startIndex = menuItem.lastIndexOf("$") + 1;
        int endIndex = menuItem.length();
        String priceString = menuItem.substring(startIndex, endIndex);
        return Double.parseDouble(priceString);
    }
}