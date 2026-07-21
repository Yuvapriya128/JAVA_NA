print("Hello World")

# - single line comments
'''
this is a multiline comments
'''

x = 10 #int
print(x)
x = "is" #string
print(x)

# Datatypes
#  Number - int, float, complex
# String -  str
# Boolean - Bool (True, False)

x= 10.34 #float
print(x)
# multi variable assignment
x = y = z = 100
print(x)
print(y)
print(z)

a,b = 10,20
print(a)
print(b)
# check the type of the variable using type function
print(type(x))
f = 10.35
name ="Ram"
isActive = True

print(type(f))
print(type(name))
print(type(isActive))

#operators 
# Exponent operator - a ** b
a = 2
b = 5
print( a ** b)

# membership operators - in and not in -> 
list = ["Apple", "Orange"]
print ("Orange" in list)
print("Banana" not in list)

# identity operators - value plus memory  locations are same - is and is not
a = 10
b = 10
print(a is b)
c = 24
print( a is c)

#swap the variables
i = 10
j = 12
i, j = j , i
print(i,j)


#type conversion using  - int(), float(), str(), bool() functions
x = "25"
print(int(x))
print(float(x))
print(str(43))

#isinstance - check if variable belongs to type
print(isinstance(x, float))

age = 20
if(age > 18):
    print("You are Eligible to vote")
else:
    print("You are not Eligible")

a = 45
b = 34
c = 47
if a>b:
    if a>c:
        print(" a is greater")
    elif b>c:
        print(" b is greater")
    else:
        print(" c is greater")

# for loop - 1 to 5
for i in range(5):
    print(i)


# while loop
print("using while loop")
x = 1
while x < 5:
    print(x)
    x=x+1

#- function definition
def add(a,b):
    return a+b;

print(add(10,20))

def greetUser(name,age):
    return f" Hello, {name} is {age} \n old"

def welcomeUser(name="Guest"):
    return "Welcome "+name

def printDetails(name,age,college="RIT"):
    return f"{name} is {age} years old completed in {college}"


print(greetUser("Ram",20))
print(welcomeUser())
print(welcomeUser("Malar"))

print(printDetails("Ram", 22))
print(printDetails("Gopi", 22, "VIT"))
print(printDetails("Renjitha", 22, "MIT"))

#lambda function-
#lambda syntax
# lambda arguements : expression


square = lambda x : x * x

print(square(5))

