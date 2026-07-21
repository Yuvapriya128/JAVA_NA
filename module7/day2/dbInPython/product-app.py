import psycopg2

conn = psycopg2.connect(
    host="localhost",
    database="northernarc",
    user="postgres",
    password="12345",
    port=5432
)

curr=conn.cursor()

curr.execute("CREATE TABLE IF NOT EXISTS products_py (id Serial PRIMARY KEY, name VARCHAR(255), price DECIMAL(10, 2))")
print("Table created successfully")

curr.execute("INSERT INTO products_py (name, price) VALUES (%s, %s)", ("Product A", 10.99))
curr.execute("INSERT INTO products_py (name, price) VALUES (%s, %s)", ("Product B", 19.99))
curr.execute("INSERT INTO products_py (name, price) VALUES (%s, %s)", ("Product C", 29.99))

conn.commit()
print("Data inserted successfully")

curr.execute("SELECT * FROM products_py")
products = curr.fetchall()

print("Products:")
for(id, name, price) in products:
    print(f"ID: {id}, Name: {name}, Price: {price}")
# print(products)

print("Fetching product with ID 1:")
curr.execute("select * from products_py where id=%s", (1,))
product = curr.fetchone()
print(f"ID: {product[0]}, Name: {product[1]}, Price: {product[2]}")

print("Fetching products with price greater than 15:")
curr.execute("SELECT * FROM products_py WHERE price > %s", (15,))
# fetches only n records from the result set. If n is not specified, all remaining rows will be returned.
products = curr.fetchmany(500)
for(id, name, price) in products:
    print(f"ID: {id}, Name: {name}, Price: {price}")


curr.execute("UPDATE products_py SET price = %s WHERE id = %s", (12.99, 1))
conn.commit()
print("Product with ID 1 updated successfully")

curr.close()
conn.close()

