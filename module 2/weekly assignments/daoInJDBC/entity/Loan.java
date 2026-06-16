package daoInJDBC.entity;

public class Loan {
    private int loanid;
    private String loanType;
    private int loanAmount;
    private String loanStatus;
    private double interest;
    private int tenure;

    public int getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(int loanAmount) {
        this.loanAmount = loanAmount;
    }
    public Loan(){}

    public Loan(String loanType, int loanAmount, String loanStatus, double interest, int tenure) {
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.loanStatus = loanStatus;
        this.interest = interest;
        this.tenure = tenure;
    }

    public Loan(int loanid, String loanType, int loanAmount, String loanStatus, double interest, int tenure) {
        this.loanid = loanid;
        this.loanAmount=loanAmount;
        this.loanType = loanType;
        this.loanStatus = loanStatus;
        this.interest = interest;
        this.tenure = tenure;
    }

    @Override
    public String toString(){
        return (loanid+" "+loanType+" "+loanAmount+" "+loanStatus+" "+interest+" "+tenure);
    }

    public int getLoanid() {
        return loanid;
    }

    public void setLoanid(int loanid) {
        this.loanid = loanid;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public int getTenure() {
        return tenure;
    }

    public void setTenure(int tenure) {
        this.tenure = tenure;
    }
}
