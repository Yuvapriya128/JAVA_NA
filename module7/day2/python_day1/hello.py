print("Hello World");

age = 20
#uses indentation instead of braces, if no indentation will throw indentationerror
if age >= 18:
    print("Eligible");

x=10
print(type(x))

x = "Python"
print(type(x))

#multiple 

for x in range(0,11,2):
    print(x)
else:
    print("Loop ended")




person = {
    "firsname": "Peter",
    "lastname": "Parker",
    "email": "Peter@gmail.com",
    "age": 25,
    "address": {
        "House No": 23,
        "Street No": 5,
        "City": "Mumbai",
        "Locality": "Bandra"
    }
}

print(person["address"]["Locality"]);

#for i in range(5):
 #   pass


#   Membership Operators - whether a value exists in a collection. in and not in
# Identity Operators  -Used to compare whether two variables refer to the same object. is and is not

def add(x,y):
    return x+y

print(add(10,20))

students = [
    ("John",80),
    ("Alice",90),
    ("Bob",70)
]

students.sort(key=lambda x:x[0])

print(students)