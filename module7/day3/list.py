list=["yuva","siva","gokul"]
print(list)
print(list[0])

list[2]="amudha"
print(list)

print(list[-1])
print(list[-2])

# multi data types
empdata=["yuva",21,1999]
for item in empdata:
    print(item)

# nested list
print("Nested list")
address=[["24","chennai"],["tamilnadu","india"]]
for item in address:
    for i in item:
        print(i)

# list comprehension
print("List comprehension-old")
num=[]
for x in range(4):
    num.append(x)
print(num)

print("List comprehension-new")
newnumbers=[x for x in range(4)]
print(newnumbers)