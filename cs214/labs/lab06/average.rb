# average.rb averages the values of an array of doubles.
# Precondition: theArray is an array of numbers
# Output: the average of the values in theArray
#
# Begun by: Dr. Adams, for CS 214 at Calvin College.
# Completed by:: Charles Blum
# Date: 30 March 2014
########################################################

###############################################
# sum() sums the values in an array
# Receive: anArray, an array of numbers
# Return: the sum of the values in anArray.
################################################

# Define sum() method here
def sum(someArray)
  total = 0.0
  someArray.each { |a| total += a }
  total
end 

# Document and define average() method here
def average(someArray)
  if someArray.size <= 0
    average = 0.0
  else
    average = sum(someArray) / someArray.size
  end
end

def main
  # Define array0 as an Array containing no values
  array0 = []
  # Define array1 as an Array containing 9.0, 8.0, 7.0, 6.0
  array1 = [9.0, 8.0, 7.0, 6.0]

  puts "sum 0 is: #{ sum(array0) }\n"
  puts "sum 1 is: #{ sum(array1) }\n"

  puts "average 0 is: #{ average(array0) }\n"
  puts "average 1 is: #{ average(array1) }\n"
end

main

