"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
;
class Person {
    fname;
    lname;
    constructor(fname, lname) {
        this.fname = fname;
        this.lname = lname;
    }
    fullName() {
        return this.fname + " " + this.lname;
    }
    disp() {
        console.log("hi " + this.fullName());
    }
    sayhi() {
        console.log("hi all");
    }
}
let p1 = new Person("Sachin", "Tendulkar");
p1.disp();
p1.sayhi();
//# sourceMappingURL=eighth.js.map