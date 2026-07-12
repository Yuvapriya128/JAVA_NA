type employee = {
    name: string;
    department: string;
};
type player = {
    team: string;
};
const gopi:employee={
    name: "Gopi Sir",
    department:"Full stack"
}
console.log(gopi);


type CombinedType = employee & player;
const sachin: CombinedType = {
    name: "sachin",
    department: "coding",
    team: "angular giants"
};
console.log(sachin);

// different datatypes
// define our tuple
let ourTuple: [number, boolean, string];
//first member should be number ,
//next should be boolean
//third should be string.
// initialize correctly
ourTuple = [5, true, 'Coding is easy'];
console.log(ourTuple)

//Union:
let data: number | string
//now data can be number type or string type
data=10
console.log(data)
data='Hello'
console.log(data)

