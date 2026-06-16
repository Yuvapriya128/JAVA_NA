package Association.pass_in_person;

public class MainPass {
    public static void main(String[] args) {
        Passport pa=new Passport("a213","31-10-2005","23-2-2008","india");
        Person p=new Person("siva","vish",pa);
        System.out.println(p);
    }
}
