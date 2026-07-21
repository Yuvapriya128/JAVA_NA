# install matplotlib or use pandas built in plotting method where we define what type of graph we want to use.
#first we create a dataframe with the sales data. In this case, we will use a line graph to show the trend of sales over time.
# df.plot(
#     x="Month",
#     y="Sales",
#     kind="line"
# )
#call show method to display the graph. This will open a new window with the graph displayed.

import matplotlib.pyplot as plt
plt.plot(df["Month"], df["Sales"])
df.plot(x="Month", y="Sales", kind="line")
plt.show()

#set xlabeland ylabel is used to set the label for the x-axis and y-axis of the graph.

#for bar chart, bar() method is used to create a bar chart. We can specify the x and y values for the bar chart using the x and y parameters.

#plt.pie() used to create a pie chart. We can specify the values for the pie chart using the x parameter. We can also specify the labels for the pie chart using the labels parameter.

#autopct -- used to specify the format of the percentage values displayed on the pie chart. We can use a string format to specify how we want the percentage values to be displayed. For example, we can use "%.1f%%" to display the percentage values with one decimal place.

#%1.1f%% --- used to specify the format of the percentage values displayed on the pie chart. The %1.1f%% format specifies that the percentage values should be displayed with one decimal place and a percent sign. For example, if the percentage value is 25.5, it will be displayed as 25.5%.

marks=[80, 90, 70, 85, 95]
plt.hist(marks, bins=5, edgecolor="black")
plt.title("Distribution of Marks")
plt.xlabel("Marks")
plt.ylabel("Frequency")

plt.show()


#plt.scatter() method is used to create a scatter plot. We can specify the x and y values for the scatter plot using the x and y parameters. We can also specify the color and size of the points in the scatter plot using the c and s parameters.