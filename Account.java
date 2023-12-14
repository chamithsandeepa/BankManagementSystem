package ATM;
import java.util.ArrayList;

public class Account {

    //the  name of the account
    private String name;

    //the account ID number
    private String uuid;

    ///user that owns of theis account
    private User holder;

    //the list of thransaction of this account
    private ArrayList<Transaction> transactions;

    /**
     *
     * @param name
     * @param holder
     * @param theBank
     */
    public Account(String name, User holder, Bank theBank){

        // set the account name and holder
        this.name = name;
        this.holder = holder;

        //get new account UUID
        this.uuid = theBank.getNewAccountUUID();

        //init transactions
        this.transactions = new ArrayList<Transaction>();
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
     * @return
     */
    public String getSummaryLine(){

        // get the accounts balence
        double balence = this.getBlance();

        //format the summary line, depending on the whether the balence is
        //negative
        if(balence >= 0){
            return String.format("%s: Rs.%.02f : %s", this.uuid, balence, this.name);
        }else{
            return String.format("%s: Rs.(%.02f) : %s", this.uuid, balence, this.name);
        }
    }

    /**
     *
     * @return   the blance value
     */
    public double getBlance(){

        double balence = 0;
        for(Transaction t :this.transactions){
            balence += t.getAmount();
        }
        return balence;
    }

    /**
     * print the transaction history of the account
     */
    public void printTransHistory(){

        System.out.printf("\nTransaction history for account %s\n", this.uuid);
        for(int t = this.transactions.size() - 1; t >= 0; t--){
            System.out.println(this.transactions.get(t).getSummaryLine());
        }
        System.out.println();
    }

    /**
     *
     * @param amount
     * @param memo
     */
    public void addTransaction(double amount, String memo){

        // creat new transaction object and add it to our list
        Transaction newTrans = new Transaction(amount, memo, this);
        this.transactions.add(newTrans);
    }
}
