/*

enum gender{
   Male,
    Female
}
*/
enum gender{
    male = "male",
    female = "female",
}

type person={
    name:string,
    age:number,
    gender:gender
}
type employee={
    id:number,
    department:string,
    salary:number
}
type combined= person & employee;

let e1:combined={
    name:"yuva",
    id:32,
    department:"IT",
    salary:234000,
    age:21,
    gender:gender.female
};
console.log(e1);