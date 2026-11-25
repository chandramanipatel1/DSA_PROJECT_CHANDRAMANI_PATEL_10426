
import java.util.*;
import java.io.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Account> accounts = new ArrayList<>();
    static Queue<String> customers = new LinkedList<>();
    static Stack<String> transactions = new Stack<>();

    static Account currentAccount = null;

    // Account class
    static class Account {
        String accNo;
        String name;
        String pin;
        int balance;
        boolean blocked;

        Account(String accNo, String name, String pin,
                int balance, boolean blocked) {
            this.accNo = accNo;
            this.name = name;
            this.pin = pin;
            this.balance = balance;
            this.blocked = blocked;
        }
    }

    // Load accounts from file
    static void loadAccounts() {
        try {
            File file = new File("accounts.txt");

            if (!file.exists()) {
                accounts.add(new Account(
                    "ACC101", "Riya", "1234", 5000, false
                ));
                saveAccounts();
                return;
            }

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                String[] data = line.split(",");

                if (data.length == 5) {
                    Account a = new Account(
                        data[0],
                        data[1],
                        data[2],
                        Integer.parseInt(data[3]),
                        Boolean.parseBoolean(data[4])
                    );

                    accounts.add(a);
                }
            }

            fileScanner.close();

        } catch (Exception e) {
            System.out.println("Error loading accounts.");
        }

        // Add customers to queue
        for (Account a : accounts) {
            customers.add(a.accNo);
        }
    }

    // Save accounts to file
    static void saveAccounts() {
        try {
            PrintWriter pw = new PrintWriter("accounts.txt");

            for (Account a : accounts) {
                pw.println(
                    a.accNo + "," +
                    a.name + "," +
                    a.pin + "," +
                    a.balance + "," +
                    a.blocked
                );
            }

            pw.close();

        } catch (Exception e) {
            System.out.println("Error saving accounts.");
        }
    }

    // Search account
    static Account searchAccount(String accNo) {
        for (Account a : accounts) {
            if (a.accNo.equals(accNo)) {
                return a;
            }
        }
        return null;
    }

    // Login
    static void serveCustomer() {

        if (currentAccount != null) {
            System.out.println("A customer is already logged in.");
            return;
        }

        if (customers.isEmpty()) {
            System.out.println("No customers waiting.");
            return;
        }

        String accNo = customers.poll();

        Account a = searchAccount(accNo);

        if (a == null || a.blocked) {
            System.out.println("Account unavailable or card blocked.");
            return;
        }

        System.out.println("Enter PIN:");

        int attempts = 3;
        boolean success = false;

        while (attempts > 0) {

            String enteredPin = sc.nextLine();

            if (enteredPin.equals(a.pin)) {
                success = true;
                break;
            }

            attempts--;

            System.out.println("Incorrect PIN. Attempts left: " + attempts);
        }

        if (!success) {
            a.blocked = true;
            System.out.println("Card blocked after 3 wrong attempts.");
            saveAccounts();
            return;
        }

        currentAccount = a;

        System.out.println("Login successful.");
        System.out.println("Welcome, " + a.name);
        System.out.println("Balance: " + a.balance);
    }

    // Check balance
    static void checkBalance() {

        if (currentAccount == null) {
            System.out.println("Please login first.");
            return;
        }

        System.out.println("Balance: " + currentAccount.balance);
    }

    // Deposit money
    static void deposit() {

        if (currentAccount == null) {
            System.out.println("Please login first.");
            return;
        }

        System.out.println("Enter deposit amount:");

        int amount = sc.nextInt();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        currentAccount.balance += amount;

        transactions.push(
            "Deposit " + amount +
            " | Balance " + currentAccount.balance
        );

        System.out.println("Deposit successful.");
        System.out.println("Balance: " + currentAccount.balance);

        saveAccounts();
    }

    // Withdraw money using Greedy Algorithm
    static void withdraw() {

        if (currentAccount == null) {
            System.out.println("Please login first.");
            return;
        }

        System.out.println("Enter withdrawal amount:");

        int amount = sc.nextInt();
        sc.nextLine();

        if (amount <= 0 || amount % 100 != 0) {
            System.out.println("Invalid amount. Enter multiple of 100.");
            return;
        }

        if (amount > currentAccount.balance) {
            System.out.println("Insufficient balance.");
            return;
        }

        int[] notes = {500, 200, 100};

        int remaining = amount;

        System.out.println("Dispensed:");

        for (int note : notes) {

            int count = remaining / note;

            if (count > 0) {
                System.out.println(note + " x " + count);
                remaining = remaining % note;
            }
        }

        currentAccount.balance -= amount;

        transactions.push(
            "Withdraw " + amount +
            " | Balance " + currentAccount.balance
        );

        System.out.println("Balance: " + currentAccount.balance);

        saveAccounts();
    }

    // Mini statement
    static void miniStatement() {

        if (currentAccount == null) {
            System.out.println("Please login first.");
            return;
        }

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        System.out.println("Mini Statement:");

        int count = 1;

        for (int i = transactions.size() - 1; i >= 0; i--) {

            System.out.println(count + ". " + transactions.get(i));
            count++;
        }
    }

    // Main method
    public static void main(String[] args) {

        loadAccounts();

        int choice;

        do {

            System.out.println("\n===== ATM SIMULATOR =====");
            System.out.println("1. Serve Next Customer");
            System.out.println("2. Check Balance");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Mini Statement");
            System.out.println("6. Logout");
            System.out.println("7. Exit");

            System.out.println("Enter your choice:");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    serveCustomer();
                    break;

                case 2:
                    checkBalance();
                    break;

                case 3:
                    deposit();
                    break;

                case 4:
                    withdraw();
                    break;

                case 5:
                    miniStatement();
                    break;

                case 6:
                    if (currentAccount != null) {
                        System.out.println("Logout successful.");
                        currentAccount = null;
                    } else {
                        System.out.println("No customer logged in.");
                    }
                    break;

                case 7:
                    saveAccounts();
                    System.out.println("Thank you for using ATM.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 7);

        sc.close();
    }
}

