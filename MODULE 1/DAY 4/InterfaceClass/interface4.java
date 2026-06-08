package InterfaceClass;

import java.util.Scanner;

interface Msg{
    void send(String msg);
}

public class interface4{
    public void send(String msg){
        System.out.println("Sending message through  message\n"+msg);
    }
    public static void main(String[] args){
        System.out.println("Enter 1,2,3,4:");
        Scanner sc=new Scanner(System.in);

        int n=sc.nextInt();


        System.out.println("Enter message");
        sc.nextLine();
        String mes=sc.nextLine();

        Msg m;

        switch (n) {
            case 1:
                m=new simpleMessage();
                break;
            case 2:
                m=new email();
                break;
            case 3:
                m=new whatsapp();
                break;
            case 4:
                m=new textMessage();
                break;

            default:
                throw new AssertionError();
        }
        m.send(mes);

    }

}
class simpleMessage implements Msg{
    public void send(String msg){
        System.out.println("Sending message through simple message\n"+msg);
    }
}
class email implements  Msg{
    public void send(String msg){
        System.out.println("Sending message through email\n"+msg);
    }
}
class whatsapp implements  Msg{
    public void send(String msg){
        System.out.println("Sending message through whatsapp\n"+msg);
    }
}
class textMessage implements  Msg{
    public void send(String msg){
        System.out.println("Sending message through text message\n"+msg);
    }
}

