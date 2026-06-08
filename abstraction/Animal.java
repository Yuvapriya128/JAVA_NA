// package oop.abstraction;
import  java.util.*;
import javax.sound.sampled.SourceDataLine;

public interface  Animal {
     void eat();   
    void talk();
     void shelter();

     public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        
        System.out.println("Enter animal(1=Dog,2=Deer,3=Lion):");
        int n=sc.nextInt();
        
       switch(n) {
            case 1:
                Dog d=new Dog("johnny",3,"john");
                d.getter();
                d.isVaccinated();
                d.eat();
                d.talk();
                d.shelter();
                d.guard();
                break;
                case 2:
                 Deer de=new Deer();
                de.eat();
                de.talk();
                de.shelter();
                de.wild();
                break;
                case 3:
                    Lion l=new Lion();
                    l.eat();
                    l.talk();
                    l.shelter();
                    l.ruleForces();
                    break;
                default: 
                System.out.println("Error");
                
        }
    }


}
