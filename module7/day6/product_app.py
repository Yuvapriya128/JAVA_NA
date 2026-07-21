import psycopg2

#STEP 1: Connect to the database
conn=psycopg2.connect(
    host="localhost",
    database="northernarc",
    user="postgres",
    password="12345",
    port=5432
)   


#STEP 2: Create a cursor object to interact with the database
curr=conn.cursor()

#STEP 3: Execute SQL queries using the cursor object


curr.execute("CREATE TABLE IF NOT EXISTS employees_py (id SERIAL PRIMARY KEY, name VARCHAR(50), age INT, department VARCHAR(50))") #create a table if it doesn't exist


curr.execute("INSERT INTO employees_py (name, age, department) VALUES (%s, %s, %s)", ("Eve", 28, "HR")) #insert a new record into the employees table
curr.execute("INSERT INTO employees_py (name, age, department) VALUES (%s, %s, %s)", ("Frank", 32, "Finance")) #insert another record into the employees table
curr.execute("INSERT INTO employees_py (name, age, department) VALUES (%s, %s, %s)", ("Grace", 29, "IT")) #insert another record into the employees table
curr.execute("INSERT INTO employees_py (name, age, department) VALUES (%s, %s, %s)", ("Hannah", 35, "Marketing")) #insert another record into the employees table
curr.execute("INSERT INTO employees_py (name, age, department) VALUES (%s, %s, %s)", ("Ian", 31, "Sales")) #insert another record into the employees table
curr.execute("INSERT INTO employees_py (name, age, department) VALUES (%s, %s, %s)", ("Jack", 27, "Operations")) #insert another record into the employees table
conn.commit() #commit the transaction


curr.execute("SELECT * FROM employees_py") #fetch all records from the employees table
rows=curr.fetchall() #fetch all the rows returned by the query
for row in rows:
    print(row)



curr.execute("SELECT * FROM employees_py WHERE department=%s", ("IT",)) #fetch records from the employees table where department is IT
rows=curr.fetchall() #fetch all the rows returned by the query
for row in rows:
    print(row)

    
curr.execute("UPDATE employees_py SET age=%s WHERE name=%s", (30, "Eve")) #update the age of the employee named Eve
conn.commit() #commit the transaction


curr.execute("DELETE FROM employees_py WHERE name=%s", ("Jack",)) #delete the record of the employee named Jack
conn.commit() #commit the transaction

#fetchone - fetch one record at a time
#fetchmany - fetch a specified number of records at a time

#STEP 4: Close the cursor and connection
curr.close()
conn.close()
