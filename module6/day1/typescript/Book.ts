class Book{
    constructor(private name:string,private price:number){}
    show(){
        console.log(this.name+" "+this.price)
    }
}
let b:Book = new Book("Book Name", 100);
b.show()