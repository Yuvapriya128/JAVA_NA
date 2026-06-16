package InterfaceClass;
interface Animal{
     void eat();
    void talk();
    void shelter();
}

public class Interface2 {
    public static void main(String[] args) {
        Animal a1=new Lion();
        Animal a2=new Deer();
        Animal a3=new Dog();

        a1.eat();
        a2.eat();
        a3.eat();

        a1.talk();
        a2.talk();
        a3.talk();

        a1.shelter();
        a2.shelter();
        a3.shelter();

    }



}
class Lion implements Animal{
    public void eat(){
        System.out.println("Lion: carnivorous");
    }
    public void talk(){
        System.out.println("Lion: roars");
    }
    public void shelter(){
        System.out.println("Lion: Den");
    }
}
class Deer implements Animal{
    public void eat(){
        System.out.println("Deer: herbivorous");
    }
    public void talk(){
        System.out.println("Deer: roars");
    }
    public void shelter(){
        System.out.println("Deer: Forest");
    }
}
class Dog implements Animal{
    public void eat(){
        System.out.println("Dog: omnivorous");
    }
    public void talk(){
        System.out.println("Dog: barks");
    }
    public void shelter(){
        System.out.println("Dog: kennel");
    }
}
