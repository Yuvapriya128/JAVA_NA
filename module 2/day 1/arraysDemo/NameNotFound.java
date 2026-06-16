package arraysDemo;

public class NameNotFound extends RuntimeException{
    public NameNotFound(String message){
        super(message);
    }

    public static class cla{
    public static void main(String[] args){
    System.out.println("Enter guest list:");
    for(int i=0;i<args.length;i++){
    System.out.println(args[i]);
    }
    }
    }
}
