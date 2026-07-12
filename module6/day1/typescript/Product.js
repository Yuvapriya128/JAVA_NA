"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Product {
    name;
    category;
    brand;
    price;
    constructor(name, category, brand, price) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
    }
    show() {
        return (this.name + " " + this.category + " " + this.brand + " " + this.price);
    }
}
let p = new Product("Watch", "Ornaments", "Titan", 100);
console.log(p.show());
//# sourceMappingURL=Product.js.map