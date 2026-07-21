package org.northernarc.loanemi.dto;

import java.util.List;

public class DashboardStatisticsDTO {
    private CustomerStatistics customerStatistics;
    private LoanStatistics loanStatistics;
    private PaymentStatistics paymentStatistics;
    private RiskStatistics riskStatistics;

    public DashboardStatisticsDTO() {}

    public DashboardStatisticsDTO(CustomerStatistics customerStatistics, LoanStatistics loanStatistics,
                                  PaymentStatistics paymentStatistics, RiskStatistics riskStatistics) {
        this.customerStatistics = customerStatistics;
        this.loanStatistics = loanStatistics;
        this.paymentStatistics = paymentStatistics;
        this.riskStatistics = riskStatistics;
    }

    public CustomerStatistics getCustomerStatistics() {
        return customerStatistics;
    }

    public void setCustomerStatistics(CustomerStatistics customerStatistics) {
        this.customerStatistics = customerStatistics;
    }

    public LoanStatistics getLoanStatistics() {
        return loanStatistics;
    }

    public void setLoanStatistics(LoanStatistics loanStatistics) {
        this.loanStatistics = loanStatistics;
    }

    public PaymentStatistics getPaymentStatistics() {
        return paymentStatistics;
    }

    public void setPaymentStatistics(PaymentStatistics paymentStatistics) {
        this.paymentStatistics = paymentStatistics;
    }

    public RiskStatistics getRiskStatistics() {
        return riskStatistics;
    }

    public void setRiskStatistics(RiskStatistics riskStatistics) {
        this.riskStatistics = riskStatistics;
    }

    public static class CustomerStatistics {
        private Long total;
        private Long active;
        private Long inactive;
        private Double avgCreditScore;
        private Long withOverdueLoans;

        public CustomerStatistics() {}

        public CustomerStatistics(Long total, Long active, Long inactive, Double avgCreditScore, Long withOverdueLoans) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
            this.avgCreditScore = avgCreditScore;
            this.withOverdueLoans = withOverdueLoans;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Long getActive() {
            return active;
        }

        public void setActive(Long active) {
            this.active = active;
        }

        public Long getInactive() {
            return inactive;
        }

        public void setInactive(Long inactive) {
            this.inactive = inactive;
        }

        public Double getAvgCreditScore() {
            return avgCreditScore;
        }

        public void setAvgCreditScore(Double avgCreditScore) {
            this.avgCreditScore = avgCreditScore;
        }

        public Long getWithOverdueLoans() {
            return withOverdueLoans;
        }

        public void setWithOverdueLoans(Long withOverdueLoans) {
            this.withOverdueLoans = withOverdueLoans;
        }
    }

    public static class LoanStatistics {
        private Long total;
        private Long active;
        private Long closed;
        private Double avgInterestRate;
        private Double totalOutstanding;

        public LoanStatistics() {}

        public LoanStatistics(Long total, Long active, Long closed, Double avgInterestRate, Double totalOutstanding) {
            this.total = total;
            this.active = active;
            this.closed = closed;
            this.avgInterestRate = avgInterestRate;
            this.totalOutstanding = totalOutstanding;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Long getActive() {
            return active;
        }

        public void setActive(Long active) {
            this.active = active;
        }

        public Long getClosed() {
            return closed;
        }

        public void setClosed(Long closed) {
            this.closed = closed;
        }

        public Double getAvgInterestRate() {
            return avgInterestRate;
        }

        public void setAvgInterestRate(Double avgInterestRate) {
            this.avgInterestRate = avgInterestRate;
        }

        public Double getTotalOutstanding() {
            return totalOutstanding;
        }

        public void setTotalOutstanding(Double totalOutstanding) {
            this.totalOutstanding = totalOutstanding;
        }
    }

    public static class PaymentStatistics {
        private Double totalCollected;
        private Double averagePaid;
        private Double todayCollection;
        private Double monthlyCollection;

        public PaymentStatistics() {}

        public PaymentStatistics(Double totalCollected, Double averagePaid, Double todayCollection, Double monthlyCollection) {
            this.totalCollected = totalCollected;
            this.averagePaid = averagePaid;
            this.todayCollection = todayCollection;
            this.monthlyCollection = monthlyCollection;
        }

        public Double getTotalCollected() {
            return totalCollected;
        }

        public void setTotalCollected(Double totalCollected) {
            this.totalCollected = totalCollected;
        }

        public Double getAveragePaid() {
            return averagePaid;
        }

        public void setAveragePaid(Double averagePaid) {
            this.averagePaid = averagePaid;
        }

        public Double getTodayCollection() {
            return todayCollection;
        }

        public void setTodayCollection(Double todayCollection) {
            this.todayCollection = todayCollection;
        }

        public Double getMonthlyCollection() {
            return monthlyCollection;
        }

        public void setMonthlyCollection(Double monthlyCollection) {
            this.monthlyCollection = monthlyCollection;
        }
    }

    public static class RiskStatistics {
        private Long npaCount;
        private List<String> topDefaulters;
        private Double highestOutstandingLoan;
        private Double collectionPercentage;

        public RiskStatistics() {}

        public RiskStatistics(Long npaCount, List<String> topDefaulters, Double highestOutstandingLoan, Double collectionPercentage) {
            this.npaCount = npaCount;
            this.topDefaulters = topDefaulters;
            this.highestOutstandingLoan = highestOutstandingLoan;
            this.collectionPercentage = collectionPercentage;
        }

        public Long getNpaCount() {
            return npaCount;
        }

        public void setNpaCount(Long npaCount) {
            this.npaCount = npaCount;
        }

        public List<String> getTopDefaulters() {
            return topDefaulters;
        }

        public void setTopDefaulters(List<String> topDefaulters) {
            this.topDefaulters = topDefaulters;
        }

        public Double getHighestOutstandingLoan() {
            return highestOutstandingLoan;
        }

        public void setHighestOutstandingLoan(Double highestOutstandingLoan) {
            this.highestOutstandingLoan = highestOutstandingLoan;
        }

        public Double getCollectionPercentage() {
            return collectionPercentage;
        }

        public void setCollectionPercentage(Double collectionPercentage) {
            this.collectionPercentage = collectionPercentage;
        }
    }
}
