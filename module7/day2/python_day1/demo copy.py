#PYTHON PROGRAMMING

print("Hello World!!")

#-single line comment
'''
multi line comment
'''

x=10
print(x)
x="is"
print(x)

# - Datatypes
#Number - int ,float,complex
#String - str
#Boolean - bool
#List - list
#Tuple - tuple
#Dictionary - dict
#Set - set

x=10.34
print(x)
#multiple assignment
x=y=z=100
print(x)
print(y)
print(z)

a,b=10,20
print(a)
print(b)


#Check type of variable using type() function
print(type(x))

f=10.3
name="Ram"
isActive=True

print(type(f))
print(type(name))   
print(type(isActive))

#--Operators
#Exponential - **
x=2**3
print(x)

#membership operator - in, not in
x="Hello"   
print("H" in x)
print("h" in x)
print("H" not in x)

list=["Orange","Banana","Mango"]
print("Banana" in list)
print("Grapes" not in list)

#identity operator - is, is not
# have one object in memory and check if two variables are pointing to the same object or not
x=10    
y=10
print(x is y)
print(x is not y)

a=5
b=10
print(a is b)   
print(a is not b)

#swap two numbers
i=10
j=12
i,j=j,i
print(i)
print(j)
 
#Type Conversion --float(), int(), str(), bool()
x="25"
print(int(x))
print(float(x))
print(bool(x))
print(str(x))
print(str(43))

#isinstance() function - check if a variable is of a particular datatype or not
x=10
print(isinstance(x, int))
print(isinstance(x, float))


age=20
if(age>=18):
    print("Eligible for voting")
else:
    print("Not eligible for voting")


#no do while loop in python
#only while loop and for loop
#if,elif,else
a,b,c=10,20,30
if(a>b and a>c):
    print("A is greater")
    elif(b>c):
        print("B is greater")
else:
    print("C is greater")


#For loop
for i in range(5):
    print(i)    

#while loop
x=1
while(x<=5):
    print(x)
    x+=1

#Function definition
def add(a,b):
    return a+b;
print(add(10,20))

#f means formatted output
def greet(name,age):
    return f"Hello {name}, your age is {age}"
print(greet("Ram",20))

def welcome(name="Guest"):
    return f"Welcome {name}"
print(welcome())
print(welcome("Ram"))

#Lambda function - anonymous function
#lambda arugments:expression
add=lambda a,b:a+b
print(add(10,20))

