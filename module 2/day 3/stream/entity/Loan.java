package stream.entity;

public class Loan implements Comparable<Loan> {
    private int loanid;
    private String loanType;
    private int loanAmount;
    private String loanStatus;
    private int interest;
    private int tenure;

    public int getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(int loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Loan(int loanid, String loanType, int loanAmount, String loanStatus, int interest, int tenure) {
        this.loanid = loanid;
        this.loanAmount=loanAmount;
        this.loanType = loanType;
        this.loanStatus = loanStatus;
        this.interest = interest;
        this.tenure = tenure;
    }
    @Override
    public int compareTo(Loan l){
        return this.getLoanid()-l.getLoanid();
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

    public int getInterest() {
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
