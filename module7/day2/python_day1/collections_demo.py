names=["Lav","Vishnu","Yuva","Harsh","Renji"]
print(names)
print(names[2]) #accessing third element
names[4]='Renjitha'
print(names)    
print([names[-5]]) #negative indexing - access elements from the end of the list

names.append("Riya") #add an element to the end of the list
print(names)
employees=["Lav",22,60]
for item in employees:
    print(item) #iterating through the list and printing each item

address=["IITM",["Block B","Block C"],"Chennai"]
for item in address:
    for val in item:
        print(val) #nested for loop to iterate through a nested list

#List comprehension - create a new list by applying an expression to each item in an existing list
#older way of creating a list using for loop
numbers=[]
for i in range(1,6):
    numbers.append(i) 
print(numbers) 

#List and Tuple allows duplicate values, but Set does not allow duplicate values

#new way of creating a list using list comprehension
#new_list=[expression for item in iterable if condition]
newNumbers=[i for i in range(1,6)]
print(newNumbers)

#Tuples - immutable data structure, cannot be modified, Temporary data ,()
fruits=("Apple","Banana","Mango","Grapes")
print(fruits)
fruits[1]="Orange" #this will throw an error as tuples are immutable

#SET
stocks={10,10,20,30,40,50,50}
print(stocks)

#Dictionary - key-value pairs, mutable data structure, {}
student={"name":"Vishnu","age":25,"dept":"IT"}
print(student)
print(student["name"]) #accessing value using key
print(student.get("age")) #accessing value using get() method
print(student.keys()) #get all keys
print(student.values()) #get all values
print(student.items()) #get all key-value pairs

for key in student.keys():
    print(f"Key: {key}, Value: {student[key]}") #iterating through the dictionary and printing key-value pairs