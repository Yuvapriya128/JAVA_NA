
public class Dog extends PetAnimal implements Animal{
    public Dog(String name,int age,String owner){
        super(name,age,owner);
    }
    public void getter(){
        System.out.println (name+" "+age+" \nowner -"+owner);
    }
    
    public void isVaccinated(){
        System.out.println("Dog is vaccinated");
    }
    
    public void eat(){
        System.out.println("Dog is omnivore");
    }

    
    public void talk(){
        System.out.println("Dog barks");
    }
    
   
   public void shelter() {
      System.out.println("Dog in kennel");
   }

    public void guard(){
        System.out.println("Dog guards");
    }
}
