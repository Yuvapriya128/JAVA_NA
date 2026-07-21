from sqlalchemy import create_engine, Column, Integer, String, Float, Date, ForeignKey
from sqlalchemy.orm import declarative_base
from sqlalchemy.orm import relationship, sessionmaker

engine= create_engine('postgresql://postgres:12345@localhost:5432/northernarc')

Base = declarative_base()

class Employee(Base):
    __tablename__ = 'py_employees'

    id = Column(Integer, primary_key=True)
    name = Column(String(255))
    salary = Column(Float)

    def __init__(self, name, salary):
        self.name = name
        self.salary = salary

    def __repr__(self):
        return f"Employee(id={self.id}, name='{self.name}', salary={self.salary})\n"
    
Session = sessionmaker(bind=engine)
session = Session()

Base.metadata.create_all(engine)
print("Table created successfully")

Employee1 = Employee(name='John Doe', salary=50000)
Employee2 = Employee(name='Jane Smith', salary=60000)
Employee3 = Employee(name='Mike Johnson', salary=55000)
session.add(Employee1)
session.add_all([
    Employee2,
    Employee3
])
session.commit()
print("Data inserted successfully")

print("Fetching all employees with salary greater than 55000:")
high_salary_employees = session.query(Employee).filter(Employee.salary > 55000).all()
for e in high_salary_employees:
    print(e)

print("Exp: All high salary employees:")
print(high_salary_employees)

print("update")
employee_to_update = session.query(Employee).filter(Employee.name == 'John Doe').update({'salary': 52000})
session.commit()
print("Updated employee:")
print(employee_to_update)
print(
    session.query(Employee).filter(Employee.name == 'John Doe',Employee.salary==52000).first()
)

print("delete")
employee_to_delete = session.query(Employee).filter(Employee.name == 'Mike Johnson').delete()
session.commit()
print("Deleted employee:")
print(employee_to_delete)

    

