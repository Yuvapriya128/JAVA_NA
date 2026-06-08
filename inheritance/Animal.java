package oop.inheritance;

import java.util.Scanner;

public class Animal {
    public void eat(){
        System.out.println("Animal eats");
    }
    public void walk(){
        System.out.println("Animals walk by legs");
    }
    public void talk(){
        System.out.println("Animal talks");
    }
    public static void main(String[] args) {

        System.out.println("Which animal(1,2,3):");
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();

        switch (n) {
            case 1:
                Dog d=new Dog();
                d.eat();
                d.talk();
                d.guard();
                break;
                case 2:
                 Deer de=new Deer();
                de.eat();
                de.talk();
                de.wild();
                break;
                case 3:
                    Lion l=new Lion();
                    l.eat();
                    l.talk();
                    l.ruleForces();
                    break;
                default: 
                System.out.println("Error");
                
        }
        System.out.println("Enter animal(1=Dog,2=Deer,3=Lion)for upcasting:");
        int num=sc.nextInt();
        Animal anup;
        switch (num) {
            case 1:
                anup=new Dog();
                ((Dog)(anup)).guard();
                break;
            case 2:
                anup=new Deer();
                ((Deer)(anup)).wild();
                break;
            case 3:
                anup=new Lion();
                ((Lion)(anup)).ruleForces();
                break;                
            default:
                return;
        }
        anup.talk();
        anup.eat();

        System.out.println("----------upcasting finished------");



        Animal an=new Animal();
        an.eat();
        an.walk();
        an.talk();

        System.out.println("-----");

        Dog d=new Dog();
        d.eat();
        d.talk();
        d.guard();


        Animal and=new Dog();
        and.eat();
        and.talk();
        ((Dog)(and)).guard();

        Animal andeer=new Deer();
        andeer.eat();
        andeer.talk();
        ((Deer)(andeer)).wild();

        Animal anlion=new Lion();
        anlion.eat();
        anlion.talk();
        ((Lion)(anlion)).ruleForces();
    }
    
}
