package JavaAssociation2.Aggregation;

public class LoanApplications {
    private double amount;
    private int yearsinTenure;
    private double interest;
    private String status;
    private int customerId;

    public LoanApplications(double amount, int yearsinTenure, double interest, String status, int customerId) {
        this.amount = amount;
        this.yearsinTenure = yearsinTenure;
        this.interest = interest;
        this.status = status;
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getYearsinTenure() {
        return yearsinTenure;
    }

    public void setYearsinTenure(int yearsinTenure) {
        this.yearsinTenure = yearsinTenure;
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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    @Override
    public String toString(){
        return (amount+" "+yearsinTenure+" "+interest+" "+status+" "+customerId);
    }
}