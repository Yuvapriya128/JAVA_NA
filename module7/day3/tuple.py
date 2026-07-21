print("Tuple")
tuple=("Tuple:","apple", "banana","banana", "cherry")
# tuple[0]="kiwi"  # Runtime type error
print(tuple)

print("set")
set={"apple", "banana","banana", "cherry"}
print(set)


# dictionary
print("Dictionary")
empdata={"name":"yuva","age":21,"dob":1999}
print(empdata)
print(empdata.keys())
print(empdata.values())
print(empdata["age"])
print(empdata.get("name"))

for key in empdata.keys():
    # print(key,empdata[key])
    print(f"{key}->{empdata[key]}")

# array of dictionaries
print("Array of dictionaries")
array_of_dicts=[{"name":"yuva","age":21},{"name":"siva","age":22,"dob":1998}]
for item in array_of_dicts:
    print(item)

print(array_of_dicts[0].keys())
print(array_of_dicts[0].values())