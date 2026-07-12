"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function sayhi(id, fname, lname) {
    //?makes lname as optional
    console.log("ID:", id);
    console.log("Name:", fname);
    if (lname != undefined)
        console.log("Last name:", lname);
}
sayhi(123, "sachin");
sayhi(111, "saurav", "ganguly");
//# sourceMappingURL=sixth.js.map