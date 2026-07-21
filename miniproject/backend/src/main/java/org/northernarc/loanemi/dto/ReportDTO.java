package org.northernarc.loanemi.dto;

import java.time.LocalDateTime;

public class ReportDTO {
    private String reportType;
    private LocalDateTime generatedAt;
    private Object reportData;
    private Long totalRecords;

    public ReportDTO() {}

    public ReportDTO(String reportType, LocalDateTime generatedAt, Object reportData, Long totalRecords) {
        this.reportType = reportType;
        this.generatedAt = generatedAt;
        this.reportData = reportData;
        this.totalRecords = totalRecords;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Object getReportData() {
        return reportData;
    }

    public void setReportData(Object reportData) {
        this.reportData = reportData;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
