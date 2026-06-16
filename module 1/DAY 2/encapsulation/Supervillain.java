// package oop.encapsulation;

class Supervillain {
    private String name;
    private String crime;
    private String power;

    public String getter(){
        return (this.name+" "+this.power+" "+this.crime);
    }
    public Supervillain(String name,String crime,String power){
        this.name=name;
        this.crime=crime;
        this.power=power;
    }
    public void jailname(){
        System.out.println(this.name+" in central jail for "+this.crime );
    }

}
