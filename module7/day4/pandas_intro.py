# data analytics
import pandas as pd

a=[1,3,5,7,9]
s=pd.Series(a)
print(s)
print(s.sum()) #sum of all elements in the series
print(s.max()) #maximum element in the series

#Dataframe
data={'Name':['John','Alice','Bob','Eve'],
        'Age':[25,30,35,40],
        'City':['New York','Los Angeles','Chicago','Houston']}

df=pd.DataFrame(data)
print(df)

# write into a csv file
df.to_csv('data.csv',index=False)
 #writing the dataframe to a csv file without index

# read from a csv file
df=pd.read_csv('data.csv')  
print(df) #reading the dataframe from a csv file

Age=df['Age'] #accessing a column in the dataframe
print(Age) #printing the Age column

AgeAndName=df[['Age','Name']] #accessing multiple columns in the dataframe
print(AgeAndName) #printing the Age and Name columns

# filter data
filtered_df=df[df['Age']>30] #filtering rows where Age is greater than 30
print(filtered_df) #printing the filtered dataframe

# filter data using multiple conditions
filtered_df=df[(df['Age']>30) & (df['City']=='Chicago')]
print(filtered_df) #printing the filtered dataframe with multiple conditions