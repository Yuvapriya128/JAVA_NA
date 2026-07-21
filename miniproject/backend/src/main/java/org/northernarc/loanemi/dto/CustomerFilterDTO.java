package org.northernarc.loanemi.dto;

public class CustomerFilterDTO {
    private String name;
    private String email;
    private String phone;
    private String city;
    private String role;
    private Boolean activeStatus;
    private Integer creditScoreMin;
    private Integer creditScoreMax;

    public CustomerFilterDTO() {}

    public CustomerFilterDTO(String name, String email, String phone, String city, String role,
                           Boolean activeStatus, Integer creditScoreMin, Integer creditScoreMax) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.role = role;
        this.activeStatus = activeStatus;
        this.creditScoreMin = creditScoreMin;
        this.creditScoreMax = creditScoreMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Integer getCreditScoreMin() {
        return creditScoreMin;
    }

    public void setCreditScoreMin(Integer creditScoreMin) {
        this.creditScoreMin = creditScoreMin;
    }

    public Integer getCreditScoreMax() {
        return creditScoreMax;
    }

    public void setCreditScoreMax(Integer creditScoreMax) {
        this.creditScoreMax = creditScoreMax;
    }
}
