package ATM;
import java.util.Date;

public class Transaction {

    //the amount  of the transaction
    private double amount;

    //the time and date of the transaction
    private Date timeStamp;

    //teh memo of this transactoin
    private String memo;

    //the account in witch the transaction was performed
    private Account inAccount;

    /**
     * creare a new trasaction
     * @param amount    the amount transaction
     * @param inAccount the account the transaction belongs to
     */
    public Transaction(double amount, Account inAccount){
        this.amount = amount;
        this.inAccount = inAccount;
        this.timeStamp = new Date();
        this.memo = "";
    }

    /**
     * create a new transaction
     * @param amount    the amount transaction
     * @param memo      the memo transaction
     * @param inAccount the account the transaction belongs to
     */
    public Transaction(double amount, String memo, Account inAccount){
        //call the two.arg constructor fi1rst
        this(amount, inAccount);

        //set the memo
        this.memo = memo;
    }

    public double getAmount() {
        return this.amount;
    }

    /**
     *
     * @return
     */
    public String getSummaryLine(){
        if(this.amount >= 0){
            return String.format("%s: $%.02f : %s", this.timeStamp.toString(), this.amount, this.memo);
        }else{
            return String.format("%s: $(%.02f) : %s", this.timeStamp.toString(), -this.amount, this.memo);
        }
    }

}
