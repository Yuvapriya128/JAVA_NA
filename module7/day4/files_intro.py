'''
r - open a file for reading. (default) throws error if the file does not exist
w - open a file for writing, creates a new file if it does not exist or truncates the file if it exists
x - open a file for exclusive creation, throws error if the file exists
a - open a file for appending, creates a new file if it does not exist  
b - binary mode for non-text files (e.g. images)
t - text mode (default) for text files
'''


# - older way of opening a file (manual open and close)
f=open("sample.csv","r")
print(f.read()) #reading the entire file
f.close() #closing the file

#with - statement automatically closes the file after the block of code is executed, even if an error occurs
with open("sample.csv","r") as f:
    print("Reading the file using with statement")
    print(f.read()) #reading the entire file

with open("demo.txt","w") as f:
    f.write("Hello World\n") #writing to a file
    f.write("This is a demo file\n")
    f.write("This is the third line\n")

with open("demo.txt","r") as f:
    print(f.read()) #reading the entire file


with open("demo.txt","a") as f:
    f.write("This is the fourth line\n") #appending to a file

with open("demo.txt","r") as f:
    print(f.read()) #reading the entire file after appending

 