package location.garbage.management.model;


public class Transaction {

    public String uid;
    public String name;
    public String district;
    public String houseNO;
    public String packages;
    public String garbage;
    public String phone;
    public String method;
    public double price;
    public double amount;
    public long time = System.currentTimeMillis();
    public boolean isPayed = true;

    public Transaction() {

    }

}