package org.example.jpademo.model;

import jakarta.persistence.*;

@Entity
@Table(name="leasejpa")
public class Lease {
    @Id
    @GeneratedValue
    @Column(name="id")
    private int leaseId;

    private String customerName;
    private String assetType;
    private Double leaseAmt;
    private int tenureMonths;
    private Double emi;
    private String status;

    public Lease() {
    }

    public Lease(String customerName, String assetType, Double leaseAmt, int tenureMonths, Double emi, String status) {
        this.customerName = customerName;
        this.assetType = assetType;
        this.leaseAmt = leaseAmt;
        this.tenureMonths = tenureMonths;
        this.emi = emi;
        this.status = status;
    }

    public Lease(int leaseId, String customerName, String assetType, Double leaseAmt, int tenureMonths, Double emi, String status) {
        this.leaseId = leaseId;
        this.customerName = customerName;
        this.assetType = assetType;
        this.leaseAmt = leaseAmt;
        this.tenureMonths = tenureMonths;
        this.emi = emi;
        this.status = status;
    }

    public long getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(int leaseId) {
        this.leaseId = leaseId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public Double getLeaseAmt() {
        return leaseAmt;
    }

    public void setLeaseAmt(Double leaseAmt) {
        this.leaseAmt = leaseAmt;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(int tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public Double getEmi() {
        return emi;
    }

    public void setEmi(Double emi) {
        this.emi = emi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
