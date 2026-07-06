"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Person {
    fname;
    lname;
    //fields not required to be declared if access specifier used in constructor
    constructor(fname, lname) {
        this.fname = fname;
        this.lname = lname;
        //fields not required to be assigned if access specifier provided.
    }
    fullName() {
        return this.fname + " " + this.lname;
    }
    //function
    disp() {
        console.log("hi " + this.fullName());
    }
}
let p1 = new Person("Sachin", "Tendulkar");
p1.disp();
//# sourceMappingURL=seventh.js.map