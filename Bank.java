package ATM;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class Bank {

    private String name;

    private ArrayList<User> users;

    private ArrayList<Account> accounts;

    /**
     *
     * @param name
     */
    public Bank(String name){

        this.name = name;
        this.users = new ArrayList<User>();
        this.accounts = new ArrayList<Account>();
    }
    /**
     *
     * @return
     */
    public String getNewUserUUID(String name){
        String accNum = null;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT acc_num FROM customers WHERE first_name = ?")) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    accNum = rs.getString("acc_num");
                }
            }

        } catch (SQLException e) {
            // handle the exception appropriately
        }

        return accNum;
    }

    /**
     *
     * @return
     */
    public String getNewAccountUUID(){

        // inits
        String uuid;
        Random rng = new Random();
        int len = 10;
        boolean nonUnique;

        // counting loopn until get a unique ID
        do{

            // generate the number
            uuid = "";
            for(int c = 0; c < len; c++){
                uuid += ((Integer)rng.nextInt(10)).toString();
            }

            // check if it is not unique
            nonUnique = false;
            for(Account a : this.accounts){
                if(uuid.compareTo(a.getUUID()) == 0){
                    nonUnique = true;
                    break;
                }
            }

        }while(nonUnique);

        return uuid;

    }

    /**
     *
     * @param //firstName
     * @param //lastName
     * @param //pin
     * @return
     */

    public void addAccount(Account anAcct){
        this.accounts.add(anAcct);
    }

    public User addUser(String  firstName, String lastName, String pin){

        //creat a new user object and add it to our list
        User newUser = new User(firstName, lastName, pin, this);
        this.users.add(newUser);

        //creat a savings account for the user and add to the User and Bank
        //accounts lists
        Account newAccount = new Account("Savings", newUser, this);
        newUser.addAccount(newAccount);
        this.accounts.add(newAccount);

        return newUser;
    }

    /**
     *
     * @param userID
     * @param pin
     * @return
     */
    public User userLogin(String userID, String pin){

        // search through list of users
        for(User u : this.users){

            // check user ID is correct
            if(u.getUUID().compareTo(userID) == 0 && u.validatePin(pin)){
                return u;
            }
        }

        // if we haven't found the user of have an incorrect pin
        return null;

    }

    public String getName() {
        return this.name;
    }
}


