package nbfcSIOC.entity;


import java.time.LocalDate;
import java.util.Date;
/*LocalDate : cleaner API
* so use that*/

public class Borrow {
    private int id;
    private String lenderName;
    private String lenderType;
    private double amount;
    private double interest;
    private String status;
    private int tenureMonths;
    private LocalDate borrowedDate;
    private LocalDate maturityDate;

    public Borrow(){}


    public Borrow(String lenderName, String lenderType, double amount, double interest, String status, int tenureMonths, LocalDate borrowedDate, LocalDate maturityDate) {
        this.lenderName = lenderName;
        this.lenderType = lenderType;
        this.amount = amount;
        this.interest = interest;
        this.status = status;
        this.tenureMonths = tenureMonths;
        this.borrowedDate = borrowedDate;
        this.maturityDate = maturityDate;
    }

    public Borrow(int id, String lenderName, String lenderType, double amount, double interest, String status, int tenureMonths, LocalDate borrowedDate, LocalDate maturityDate) {
        this.id = id;
        this.lenderName = lenderName;
        this.lenderType = lenderType;
        this.amount = amount;
        this.interest = interest;
        this.status = status;
        this.tenureMonths = tenureMonths;
        this.borrowedDate = borrowedDate;
        this.maturityDate = maturityDate;
    }

    @Override
    public String toString(){
        return (id+" "+lenderName+" "+lenderType+" "+amount+" "+interest+" "+status+" "+tenureMonths+" "+borrowedDate+" "+maturityDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public String getLenderType() {
        return lenderType;
    }

    public void setLenderType(String lenderType) {
        this.lenderType = lenderType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(int tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }
}
