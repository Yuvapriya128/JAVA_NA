package JavaAssociation2.Aggregation;

public class MainTp {
    public static void main(String[] args) {
        Team t1=new Team("csk","chennai");
        t1.addPlayers(new Player("sachin","tendulkar"));
        t1.addPlayers(new Player("rahul","gandi"));
        t1.showPlayer();
        t1.removePlayer("sachin");
        t1.showPlayer();

        Team t2=new Team("rch","banglore");
        t2.addPlayers(new Player("rohit","karan"));
        t2.addPlayers(new Player("sharma","mukarjee"));
        t2.showPlayer();
        t2.removePlayer("rohit");
        t2.showPlayer();


    }
}
