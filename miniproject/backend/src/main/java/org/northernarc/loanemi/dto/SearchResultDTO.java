package org.northernarc.loanemi.dto;

import java.util.List;

public class SearchResultDTO {
    private String query;
    private String type;
    private List<?> customers;
    private List<?> loans;
    private List<?> payments;
    private List<?> emis;

    public SearchResultDTO() {}

    public SearchResultDTO(String query, String type, List<?> customers, List<?> loans, List<?> payments, List<?> emis) {
        this.query = query;
        this.type = type;
        this.customers = customers;
        this.loans = loans;
        this.payments = payments;
        this.emis = emis;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<?> getCustomers() {
        return customers;
    }

    public void setCustomers(List<?> customers) {
        this.customers = customers;
    }

    public List<?> getLoans() {
        return loans;
    }

    public void setLoans(List<?> loans) {
        this.loans = loans;
    }

    public List<?> getPayments() {
        return payments;
    }

    public void setPayments(List<?> payments) {
        this.payments = payments;
    }

    public List<?> getEmis() {
        return emis;
    }

    public void setEmis(List<?> emis) {
        this.emis = emis;
    }
}
