package JavaAssociation2.Aggregation;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Team {
    private String name;
    private String location;
    private Set<Player> playerSet;

    public Team(String name, String location) {
        this.name = name;
        this.location = location;
        this.playerSet = new LinkedHashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public void addPlayers(Player p){
        this.playerSet.add(p);
    }
    public void removePlayer(String name){
        Player ptemp=null;
        for(Player eachp:playerSet){
            if(eachp.getFname().equalsIgnoreCase(name)){
                ptemp=eachp;
            }
        }
        if(ptemp!=null){
            playerSet.remove(ptemp);
        }
    }
    public void showPlayer(){
        System.out.println(playerSet);
    }
}
