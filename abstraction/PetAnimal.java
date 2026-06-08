public abstract class PetAnimal {
    String owner;
    String name;
    int age;
    public PetAnimal(String name,int age,String owner){
        this.name=name;
        this.age=age;
        this.owner=owner;
    }
    public abstract void isVaccinated();

    
}
