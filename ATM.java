package ATM;

import java.sql.*;
import java.util.Scanner;

public class ATM {
    public static void main(String[] args) throws SQLException {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bank theBank;
        try {
            // Prepare a statement to retrieve all customers
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers");

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Loop through the result set and add each user to the bank
            theBank = new Bank("Bank of Drausing");
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String password = rs.getString("password");
                /*System.out.println("User ID: " + userId);
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName);
                System.out.println("Password: " + password);*/

                // Add a user, which also creates a checking account
                User aUser = theBank.addUser(firstName, lastName, password);
                Account newAccount = new Account("checking", aUser, theBank);
                aUser.addAccount(newAccount);
                theBank.addAccount(newAccount);
            }

            // Close the result set and statement
            rs.close();
            stmt.close();

            // Prompt for login and show main menu
            User curUser;
            Scanner sc = new Scanner(System.in);

            while(true){

                // stay in the logig prompt until succeseful  login
                curUser = ATM.mainMenuPrompt(theBank, sc);

                // stay in main menu untill user quits
                ATM.printUserMenu(curUser, sc);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the connection
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



        /**
         *
         * @param theBank
         * @param sc
         * @return
         */
        public static User mainMenuPrompt (Bank theBank, Scanner sc){
            // inits
            String userID;
            String pin;
            User authUser;

            // prompt the user for user ID/pin combo until a correct one is reach
            do {

                System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
                System.out.printf("Enter user ID: ");
                userID = sc.nextLine();
                System.out.printf("Enter pin: ");
                pin = sc.nextLine();

                // tryb to get the user object corresponding to the ID and pin combo
                authUser = theBank.userLogin(userID, pin);
                if (authUser == null) {
                    System.out.println("Incorrect user ID or pin. please tru again");
                }
            } while (authUser == null); // continue looping untill successful login

            return authUser;

        }

        public static void printUserMenu (User theUser, Scanner sc){

            // print a summary of the user's account
            theUser.printAccountsSummary();

            //init
            int choice;

            // user menu
            do {
                System.out.printf("\n\nWelcome %s, what do yoy like to do?\n", theUser.getFirstName());
                System.out.println(" 1) Show acount transaction history");
                System.out.println(" 2) withdraw");
                System.out.println(" 3) deposit");
                System.out.println(" 4) transfer");
                System.out.println(" 5) quit");
                System.out.println();
                System.out.printf("Enter your choice: ");
                choice = sc.nextInt();
                System.out.println();

                if (choice < 1 || choice > 5) {
                    System.out.println("Invalid choice. please try again 1-5");
                }
            } while (choice < 1 || choice > 5);

            //procerss the choice
            switch (choice) {
                case 1:
                    ATM.showTransHistory(theUser, sc);
                    break;
                case 2:
                    ATM.withdrawFunds(theUser, sc);
                    break;
                case 3:
                    ATM.depositFunds(theUser, sc);
                    break;
                case 4:
                    ATM.transferFunds(theUser, sc);
                    break;
                case 5:
                    // gobble up rest of previus input
                    sc.nextLine();
                    break;
            }
            // redisplay this menu unless the user wants to q
            if (choice != 5) {
                ATM.printUserMenu(theUser, sc);
            }
        }

        /**
         * show the transaction hisory of the account
         * @param theUser   the loged-in User object
         * @param sc        the
         */
        public static void showTransHistory (User theUser, Scanner sc){

            int theAcct;

            // get account whose transaction history to look at
            do {
                System.out.printf("entrt the number (1-%d) of the account" + " whose transaction you wont to see: ", theUser.numAccounts());
                theAcct = sc.nextInt() - 1;
                if (theAcct < 0 || theAcct >= theUser.numAccounts()) {
                    System.out.println("Invalid account. please try again");
                }
            } while (theAcct < 0 || theAcct >= theUser.numAccounts());

            // print the transaction history
            theUser.printAcctTransHistory(theAcct);

        }
        /**
         *
         * @param theUser
         * @param sc
         */
        public static void transferFunds (User theUser, Scanner sc){

            //inits
            int fromAcct;
            int toAcct;
            double amount;
            double acctBal;

            // get the accountto transfer from
            do {

                System.out.printf("Enter the number (1-%d) of the account\n" + "to transfer from: ", theUser.numAccounts());
                fromAcct = sc.nextInt() - 1;
                if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                    System.out.println("Invalid account. please try again. ");
                }

            } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
            acctBal = theUser.getAcctBlance(fromAcct);

            // get the  account to transfer to
            do {

                System.out.printf("Enter the number (1-%d) of the account\n" + "to transfer to: ", theUser.numAccounts());
                toAcct = sc.nextInt() - 1;
                if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                    System.out.println("Invalid account. please try again. ");
                }
            } while (toAcct < 0 || toAcct >= theUser.numAccounts());

            // get the amount to transfer
            do {
                System.out.printf("Enter the amount to transfer (max Rs.%.02f): Rs.", acctBal);
                amount = sc.nextDouble();
                if (amount < 0) {
                    System.out.println("amount must be greater than zero. ");
                } else if (amount > acctBal) {
                    System.out.printf("amount must NOT be greater than\n" + "blance of Rs.%.02\n", acctBal);
                }

            } while (amount < 0 || amount > acctBal);

            // finally, do the transfer
            theUser.addAcctTransaction(fromAcct, -1 * amount, String.format("Transfer to account %s", theUser.getAcctUUID(toAcct)));
            theUser.addAcctTransaction(toAcct, amount, String.format("Transfer to account %s", theUser.getAcctUUID(fromAcct)));
        }

        public static void withdrawFunds (User theUser, Scanner sc){

            //inits
            int fromAcct;
            double amount;
            double acctBal;
            String memo;

            // get the accountto transfer from
            do {

                System.out.printf("Enter the number (1-%d) of the account\n" + "to withdrow from: ", theUser.numAccounts());
                fromAcct = sc.nextInt() - 1;
                if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                    System.out.println("Invalid account. please try again. ");
                }

            } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
            acctBal = theUser.getAcctBlance(fromAcct);

            // get the amount to transfer
            do {
                System.out.printf("Enter the amount to withdraw (max Rs.%.02f): Rs.", acctBal);
                amount = sc.nextDouble();
                if (amount < 0) {
                    System.out.println("amount must be greater than zero. ");
                } else if (amount > acctBal) {
                    System.out.printf("amount must NOT be greater than\n" + "blance of Rs.%.02f.\n", acctBal);
                }

            } while (amount < 0 || amount > acctBal);

            // gobble up rese of previous input
            sc.nextLine();

            //get a memo
            System.out.print("Enter a amemo: ");
            memo = sc.nextLine();

            // do the withdrowl
            theUser.addAcctTransaction(fromAcct, -1 * amount, memo);
        }

        /**
         *
         * @param theUser
         * @param sc
         */
        public static void depositFunds (User theUser, Scanner sc){

            //inits
            int toAcct;
            double amount;
            double acctBal;
            String memo;

            // get the accountto transfer from
            do {

                System.out.printf("Enter the number (1-%d) of the account\n" + "to deposit in: ", theUser.numAccounts());
                toAcct = sc.nextInt() - 1;
                if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                    System.out.println("Invalid account. please try again. ");
                }

            } while (toAcct < 0 || toAcct >= theUser.numAccounts());
            acctBal = theUser.getAcctBlance(toAcct);

            // get the amount to transfer
            do {
                System.out.printf("Enter the amount to deposit: Rs.");
                amount = sc.nextDouble();
                if (amount < 0) {
                    System.out.println("amount must be greater than zero. ");
                }
            } while (amount < 0);

            // gobble up rese of previous input
            sc.nextLine();

            //get a memo
            System.out.print("Enter amemo: ");
            memo = sc.nextLine();

            // do the withdrowl
            theUser.addAcctTransaction(toAcct, amount, memo);
        }

    }





