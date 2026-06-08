
import java.util.Scanner;

public interface  Notification{
     void send(String msg);
    public static void main(String[] args){
        System.out.println("Enter 1,2,3,4:");
        Scanner sc=new Scanner(System.in);

        int n=sc.nextInt();
        
        
        System.out.println("Enter message");
        sc.nextLine();
        String mes=sc.nextLine();

        Notification m;

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
class simpleMessage implements  Notification{
    public void send(String msg){
        System.out.println("Sending message through simple message\n"+msg);
    }
}
class email implements  Notification{
    public void send(String msg){
        System.out.println("Sending message through email\n"+msg);
    }
}
class whatsapp implements  Notification{
    public void send(String msg){
        System.out.println("Sending message through whatsapp\n"+msg);
    }
}
class textMessage implements  Notification{
    public void send(String msg){
        System.out.println("Sending message through text message\n"+msg);
    }
}

