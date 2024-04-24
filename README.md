In this program we determine who will be paying for today's coffee. By generating a random number within the range of the total weight
(the sum of each individuals proportional weight based on their coffee spending) and then subtracting each option's weight from it 
until we get zero, we are moving closer to zero within the range of weights. Options with higher weights 
will have a greater chance of being selected and we avoid iterating through the entire map each time. We ensure that coworkers with 
higher weights (those who spend more on their coffee) have a larger portion of the range assigned to them, therefore having a 
higher probability of being selected since their range is larger within the total weight range. 

To compile: 
  cd to --> beverageTime/src/main/java/org/example 
  run command --> javac CoffeePayment.java
  *** Program Compiles *** (See created .class file in beverageTime/src/main/java/org/example)

To run:
  cd to --> /beverageTime/src/main/java 
  run command --> java org.example.CoffeePayment
  *** Program begins *** (Select (S) or (M))
~ Enjoy ~
