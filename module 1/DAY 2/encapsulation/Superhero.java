
// package oop.encapsulation;

 class Superhero{
    private String name;
    private String superpower;

    public String getter(){
        return (this.name +" uses "+this.superpower);
    }

    public void setter(String name,String superpower){
        this.name=name;
        this.superpower=superpower;
    }

    public void useSuperpower(){
       System.out.println(this.name + " uses "+this.superpower);
    }
    public void saveWorld(){
        System.out.println(this.name+" saves world");
    }

}