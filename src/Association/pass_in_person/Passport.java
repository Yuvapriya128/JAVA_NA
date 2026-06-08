package Association.pass_in_person;

public class Passport {
    private String pno;
    private String issuedate;
    private String expirydate;
    private String county;

    public Passport(String pno, String issuedate, String expirydate, String county) {
        this.pno = pno;
        this.issuedate = issuedate;
        this.expirydate = expirydate;
        this.county = county;
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getIssuedate() {
        return issuedate;
    }

    public void setIssuedate(String issuedate) {
        this.issuedate = issuedate;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
    public String toString(){
        return (pno+" "+issuedate+" - "+expirydate+" "+county);
    }
}

