package entity;

/*
* Register Customer
      ↓
KYC Verification
      ↓
Credit Score Check
      ↓
Save Customer
      ↓
Send Notification
* */
public class Customer {

    private int id;
    private String customerName;
    private String mobile;
    private String panNumber;
    private String aadhaarNumber;
    private int creditScore;
    // constructors : 3 for jdbc: empty, with id, without id
    // getters/setters
    // toString()

    public Customer(){}

    public Customer(String customerName, String mobile, String panNumber, String aadhaarNumber, int creditScore) {
        this.customerName = customerName;
        this.mobile = mobile;
        this.panNumber = panNumber;
        this.aadhaarNumber = aadhaarNumber;
        this.creditScore = creditScore;
    }

    public Customer(int id, String customerName, String mobile, String panNumber, String aadhaarNumber, int creditScore) {
        this.id = id;
        this.customerName = customerName;
        this.mobile = mobile;
        this.panNumber = panNumber;
        this.aadhaarNumber = aadhaarNumber;
        this.creditScore = creditScore;
    }
//    As Set is used: to prevent Duplicates
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass())
            return false;

        Customer customer = (Customer) o;

        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    @Override
    public String toString(){
        return (id+" "+customerName+" "+mobile+" "+panNumber+" "+aadhaarNumber+" "+creditScore);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

}
