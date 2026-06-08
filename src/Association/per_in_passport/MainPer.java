package Association.per_in_passport;



public class MainPer {
    public static void main(String[] args) {


        Person person = new Person("siva", "vish");
        Passport pa = new Passport("a213", "31-10-2005", "23-2-2008", "india", person);

        System.out.println(pa);
    }
}
