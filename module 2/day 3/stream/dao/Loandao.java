package stream.dao;

import stream.entity.Loan;

public interface Loandao {
    public void save(Loan l);
    public void remove(int loanid);
    public Iterable<Loan> findAll();
    public Iterable<Loan> findByStatus(String status);
    public Iterable<Loan> findByType(String type);
    public void updateInterest(int rate);
    public Iterable<Loan> updateLoanInterest(String type,int rate);
    public Iterable<Loan> sortLoanByAmount();
    public Iterable<Loan> sortLoanByAmtInt();
    public void details();
}
