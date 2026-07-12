class Person {
    //fields not required to be declared if access specifier used in constructor

    constructor(private fname:string,private lname:string) {
//fields not required to be assigned if access specifier provided.
    }
    fullName():string{
        return this.fname+" "+this.lname
    }
    //function
    disp():void {
        console.log("hi "+this.fullName())
    }
}
let p1=new Person("Sachin","Tendulkar")
p1.disp()