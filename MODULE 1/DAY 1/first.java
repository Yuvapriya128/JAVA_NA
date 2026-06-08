
import java.util.Scanner;


class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);

        System.out.println("Enter number(a):");
        int a=sc.nextInt();
        System.out.println("Enter number(b):");
        int b=sc.nextInt();
        System.out.println("Enter number(c):");
        int c=sc.nextInt();
        greatest_three(a,b,c);

        System.out.println("Enter number(+ve/-ve):");
        int num=sc.nextInt();
        positive_negative(num);

        System.out.println("Enter number(even/odd):");
        int evenorodd=sc.nextInt();
        even_odd(evenorodd);

        System.out.println("Enter age(voting eligible):");
        int age=sc.nextInt();
        vote_eligible(age);

        System.out.println("Enter year:");
        int year=sc.nextInt();
        leap_year(year);

        System.out.println("Enter marks:");
        int marks=sc.nextInt();
        student_grade(marks);


        
    }
    public static void vote_eligible(int age) {
       
        if(age >= 18) {
            System.out.println("Eligible to vote");
        }else{
            System.out.println("Not eligible to vote");
        }
    }
    public static void even_odd(int num){
        
        if(num%2==0){
            System.out.println(num+" is Even number");
        }else {
            System.out.println(num+ " is Odd number");
        }
    }
    public static void student_grade(int mark){
        
        if(mark<0 && mark>100){
            System.out.println("Enter mark");
        }
        if(mark >=0 && mark <40){
            System.out.println("F");
        }else if(mark>=40 && mark<60){
            System.out.println("C");
        }else if(mark>=60 && mark<80){
            System.out.println("B");
        }else if(mark>=80 && mark<90){
            System.out.println("A");
        }else if(mark >=90 && mark<=100){
            System.out.println("O");
        }

    }
    public static void leap_year(int y){
        
        if(y%400 ==0){
            System.out.println("leap year");
        }else{
            if(y%100 ==0){
                System.out.println("Not a leap year");
            }else{
                if(y%4==0){
                    System.out.println("Leap year");
                }else{
                    System.out.println("Not a leap year");
                }
            }
        }
        
    }
    public static void positive_negative(int num){
        
        if(num > 0){
           System.out.println("Number is positive");
        }else if(num == 0){
           System.out.println("Number is zero");
        }else{
          System.out.println("Number is negative");
        }
        
        
    }
    
    public static void greatest_three(int a,int b,int c){
        
        if((a>b)&&(a>c)){
            System.out.println("a is the greatest");
        }
        else if((b>a) && (b>c)) {
            System.out.println("b is the greatest");
        }else{
            System.out.println(c+" is the greatest");
        }
    }
}
