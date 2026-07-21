import numpy as np
import pandas as pd
a=np.array([1,2,3,4,5])

print(a.sum()) #sum of all elements in the array
print(a.max()) #maximum element in the array
print(a.min()) #minimum element in the array
print(a.mean()) #mean of all elements in the array
print(a[0]) #accessing first element of the array
print(a[-1]) #accessing last element of the array
print(a[1:4]) #accessing elements from index 1 to 3

print('-' * 50)
# --multidimensional array
b=np.array([[1,2,3],[4,5,6],[7,8,9]])
print(b.sum()) #sum of all elements in the array
print(b.max()) #maximum element in the array
print(b.min()) #minimum element in the array
print(b.mean()) #mean of all elements in the array
print(b.ndim) #number of dimensions
print(b.shape) #shape of the array
print(b.dtype) #data type of the array elements

print('-' * 50)
#OTP GENERATION
otp=np.random.randint(1000,9999) #generate a random integer between 1000 and 9999
print(f"Your OTP is: {otp}")

random=np.random.rand(5) #generate 5 random numbers between 0 and 1
print(random)

#  none values remove 
data={
    'Name':['John','Alice','Bob','Eve',None],
    'Age':[None,30,35,40,None],
    'City':['New York','Los Angeles',None,'Houston',None]
}
df=pd.DataFrame(data)
print(df.isnull().sum()) #check for None values in the dataframe
# df.dropna(inplace=True) #remove rows with any None values
print("After replacing None values")
print(df.fillna({'Name':'Unknown','Age':0,'City':'Unknown'},inplace=True)) #replace None values with specified values
print(df) #printing the dataframe after removing None values

print(df.head()) #printing the first 5 rows of the dataframe
print(df.tail()) #printing the last 5 rows of the dataframe

# limit 10 rows in the dataframe
print(df.head(10)) #printing the first 10 rows of the dataframe
print(df.shape) #printing the shape of the dataframe
# rowsxcolumns

print("describe")
print(df.describe()) #printing the statistical summary of the dataframe
print("info")
print(df.info()) #printing the information about the dataframe
