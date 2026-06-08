package Association_2;

public class Pancard {
    protected String pancardno;
    protected String dob;

    public String getPancardno() {
        return pancardno;
    }

    public void setPancardno(String pancardno) {
        this.pancardno = pancardno;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Pancard(String pancardno, String dob) {
        this.pancardno = pancardno;
        this.dob = dob;
    }

    @Override
    public String toString() {
        return (pancardno+" "+dob);
    }
}
