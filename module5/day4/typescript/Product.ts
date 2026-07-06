class Product {
    constructor(public name: string,private category:string,private brand:string, public price: number) {}

    show(){
        return (this.name+" "+this.category+" "+this.brand+" "+this.price);
    }
}
let p: Product = new Product("Watch", "Ornaments", "Titan", 100);
console.log(p.show());