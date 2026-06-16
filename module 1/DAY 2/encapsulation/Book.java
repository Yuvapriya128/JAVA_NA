// package oop.encapsulation;

class Book {
    private String title;
    private String author;
    private int pages;

    public String getter(){
        return (this.title+" "+this.author+" "+this.pages+" "+"pages");
    }

    public Book (String title,String author,int pages){
        this.title=title;
        this.author=author;
        this.pages=pages;
    }

    public void read(){
        System.out.println(this.pages+" read");
    }
    public void getSummary(){
        System.out.println("summary of "+this.title);
    }
    
}
