package FunctionalInterface;

public class MainGreet {
    public static void main(String[] args) {
  //      This is local inner class
//        class Greet implements Greeting{
//            @Override
//            public void greet(){
//                System.out.println("Good Day!");
//            }
//        }
//        Greet gr=new Greet();
//        gr.greet();

        System.out.println("___________________");
//        anonymous class

//
//        Greeting gm = new Greeting() {
//            @Override
//            public void greet() {
//                System.out.println("Good Day!");
//            }
//        };
//        gm.greet();


// Lambda expression
        System.out.println("___________________");

        Greeting gn=()->{
           System.out.println("Good night!!");

        };
        gn.greet();

        Greeting gf=()->{
            System.out.println("Good Afternoon!!");

        };
        gf.greet();













    }
}
