"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Book {
    name;
    price;
    constructor(name, price) {
        this.name = name;
        this.price = price;
    }
    show() {
        console.log(this.name + " " + this.price);
    }
}
let b = new Book("Book Name", 100);
b.show();
//# sourceMappingURL=Book.js.map