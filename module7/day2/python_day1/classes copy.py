class calculator:

    #constructor function
    #self- instance of the class
    #parameterized constructor
    def __init__(self,a,b):
        self.a=a
        self.b=b

    def add(self):
        return self.a+self.b 
        
c=calculator(10,20)
result=c.add()
print(result)


class Person:
    def __init__(self,name,age):
        self.name=name
        self.age=age

    def showDetails(self):
        return f"Name: {self.name}, Age: {self.age}"

#Inheritance -create a base class first, then pass it as a parameter to the derived class
class Employee(Person):
    def __init__(self,name,age,dept):
        super().__init__(name,age)
        self.dept=dept

    def showDetails(self):
        return f"Name: {self.name}, Age: {self.age}, Department: {self.dept}"

e=Employee("Vishnu",25,"IT")
print(e.showDetails())


#For multiple inheritance, pass multiple base classes as parameters to the derived class
#overriding also possible in multiple inheritance