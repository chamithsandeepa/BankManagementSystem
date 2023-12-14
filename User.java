package ATM;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    //first anme of the user
    private String firstName;

    //last name of the user
    private String lastName;

    //id number of the user
    private String uuid;

    //the MD5 hash of the user's pin numer
    private byte pinHash[];

    //the list of accounts of the user
    private ArrayList<Account> accounts;


    /**
     *
     * @param firstName
     * @param lastName
     * @param pin
     * @param theBank
     */
    public User(String firstName, String lastName, String pin, Bank theBank){

        //set user's name
        this.firstName = firstName;
        this.lastName = lastName;

        //store the pin's MD5 hash, rather than the original
        //value for the security reasone
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, cought NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        //get a new, unique universal ID for the user
        this.uuid = theBank.getNewUserUUID(firstName);

        //create empty list of accounts
        this.accounts = new ArrayList<Account>();

        //print log messeage
       // System.out.printf("New user %s, %s with ID %s created.\n", lastName, firstName, this.uuid);
    }

    /**
     *
     * @param anAcct
     */
    public void addAccount(Account anAcct){
        this.accounts.add(anAcct);
    }

    /**
     *
     * @return
     */
    public String getUUID(){
        return this.uuid;
    }

    /**
     *
     * @param aPin
     * @return
     */
    public boolean validatePin(String aPin){

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(md.digest(aPin.getBytes()), this.pinHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, cought NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public void printAccountsSummary(){

        System.out.printf("\n\n%s's accounts summary\n", this.firstName);
        for (int a = 0; a < this.accounts.size(); a++) {
            System.out.printf("  %d) %s\n", (a + 1),this.accounts.get(a).getSummaryLine());
        }
    }

    /**
     *
     * @return
     */
    public int numAccounts(){
        return this.accounts.size();
    }

    /**
     *
     * @param acctIdx
     */
    public void  printAcctTransHistory(int acctIdx){
        this.accounts.get(acctIdx).printTransHistory();
    }

    /**
     *
     * @param acctInx
     * @return
     */
    public double getAcctBlance(int acctInx){
        return this.accounts.get(acctInx).getBlance();
    }

    /**
     *
     * @param acctidx
     * @return
     */
    public String getAcctUUID(int acctidx){
        return this.accounts.get(acctidx).getUUID();
    }

    /**
     *
     * @param acctInx
     * @param amount
     * @param memo
     */
    public void addAcctTransaction(int acctInx, double amount, String memo){
        this.accounts.get(acctInx).addTransaction(amount, memo);
    }
}

