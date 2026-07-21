package org.northernarc.loanemi.dto;

/**
 * DTO for EMI insights dashboard widget.
 */
public class EmiInsightsDTO {

    private Double highestEmi;
    private Double lowestEmi;
    private Double averageEmi;
    private Double totalMonthlyEmiCollection;
    private Double upcomingEmiAmount;

    public EmiInsightsDTO() {
    }

    public EmiInsightsDTO(Double highestEmi, Double lowestEmi, Double averageEmi, 
                          Double totalMonthlyEmiCollection, Double upcomingEmiAmount) {
        this.highestEmi = highestEmi;
        this.lowestEmi = lowestEmi;
        this.averageEmi = averageEmi;
        this.totalMonthlyEmiCollection = totalMonthlyEmiCollection;
        this.upcomingEmiAmount = upcomingEmiAmount;
    }

    public Double getHighestEmi() {
        return highestEmi;
    }

    public void setHighestEmi(Double highestEmi) {
        this.highestEmi = highestEmi;
    }

    public Double getLowestEmi() {
        return lowestEmi;
    }

    public void setLowestEmi(Double lowestEmi) {
        this.lowestEmi = lowestEmi;
    }

    public Double getAverageEmi() {
        return averageEmi;
    }

    public void setAverageEmi(Double averageEmi) {
        this.averageEmi = averageEmi;
    }

    public Double getTotalMonthlyEmiCollection() {
        return totalMonthlyEmiCollection;
    }

    public void setTotalMonthlyEmiCollection(Double totalMonthlyEmiCollection) {
        this.totalMonthlyEmiCollection = totalMonthlyEmiCollection;
    }

    public Double getUpcomingEmiAmount() {
        return upcomingEmiAmount;
    }

    public void setUpcomingEmiAmount(Double upcomingEmiAmount) {
        this.upcomingEmiAmount = upcomingEmiAmount;
    }
}
