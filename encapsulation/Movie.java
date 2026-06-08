// package oop.encapsulation;
public class Movie {
    private String title;
    private String director;
    private int relyear;

    public String getter(){
        return(this.title+" "+this.director+" "+this.relyear+" year");
    }
    public Movie (String title,String director,int relyear){
        this.title=title;
        this.director=director;
        this.relyear=relyear;
    }

    public void play(){
        if(relyear>1990)
       System.out.println("2.30 hrs");
    else{
        System.out.println("3 hrs");
    }
    }
    public void getRating(){
        
       System.out.println(title+": Good");
    }
}
