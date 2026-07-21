class calculator:


#self -> refers to the current object
#all the variables inside the class, you have to access with self
#constructor - parameterised constructor
    def __init__(self, x, y):
        self.x = x
        self.y = y
    
    def add(self):
        return self.x+self.y
    
c = calculator(10,20)
result = c.add()
print(result)

c1 = calculator(24,56)
print(c1.add())

class Person:
    def __init__(self, name, age):
      self.name = name
      self.age = age
    
    def showDetails(self):
        return f" Name : {self.name} & Age: {self.age}"
    
# inheritance -> create a base class first, pass the base class as function parameter for sub class definition
class Employee(Person):
    def __init__(self, name, age, department):
        super().__init__(name,age);
        self.department = department
    
    def showCompleteDetails(self):
        return f" Name : {self.name} & Age: {self.age} & working in {self.department}"
    
e = Employee("Renjitha", 20, "IT")
print(e.showCompleteDetails())