package location.garbage.management.model;


public class Garbage {

    public String uid;
    public String name;
    public String district;
    public String houseNO;
    public String packages;
    public String garbage;
    public String phone;
    public long time = System.currentTimeMillis();
    public boolean isPicked;
    public double price;
    public double amount;


    public Garbage() {

    }

}