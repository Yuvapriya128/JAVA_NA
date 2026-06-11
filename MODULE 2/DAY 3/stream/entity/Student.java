package stream.entity;

public class Student {
    private String name;
    private double phy;
    private double chem;
    private double math;
    private double hist;
    private double geo;

    public Student(String name, double phy, double chem, double math, double hist, double geo) {
        this.name = name;
        this.phy = phy;
        this.chem = chem;
        this.math = math;
        this.hist = hist;
        this.geo = geo;
    }
    @Override
    public String toString(){
        return (name+" "+phy+" "+chem+" "+math+" "+hist+" "+geo);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPhy() {
        return phy;
    }

    public void setPhy(double phy) {
        this.phy = phy;
    }

    public double getChem() {
        return chem;
    }

    public void setChem(double chem) {
        this.chem = chem;
    }

    public double getMath() {
        return math;
    }

    public void setMath(double math) {
        this.math = math;
    }

    public double getHist() {
        return hist;
    }

    public void setHist(double hist) {
        this.hist = hist;
    }

    public double getGeo() {
        return geo;
    }

    public void setGeo(double geo) {
        this.geo = geo;
    }
}
