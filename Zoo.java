import java.io.InputStream;
import java.util.Scanner;
import java.util.Observer;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class Zoo {
    public static int days;
    public static Animal[] zooAnimals;
    public ZooKeeper z;
    public ZooClock clock = new ZooClock();

    public Zoo(int days, Animal[] zooAnimals) {
        this.days = days;
        this.zooAnimals = zooAnimals;
    }

    public void runDaysAtZoo() throws InterruptedException {
        int i = 0;
        ZooKeeper z = new ZooKeeper(zooAnimals);
        ZooFoodServer fs = new ZooFoodServer();
        ZooAnnouncer a = new ZooAnnouncer();
        //all employees observe clock (observer pattern)
        this.clock.addObserver(z);
        this.clock.addObserver(fs);
        this.clock.addObserver(a);
        //zooannouncer observes zookeeper and foodserver (observer pattern)
        z.addObserver(a);
        fs.addObserver(a);
        while (i < days) {
            System.out.println("Day " + (i + 1));
            z.goToWork(i);
            fs.goToWork((i));
            a.goToWork(i);
            while (this.clock.getTime() < 20) {
                this.clock.runClock();
                TimeUnit.MILLISECONDS.sleep(100);
            }
            System.out.println("It's the end of the day!");
            TimeUnit.MILLISECONDS.sleep(200);
            this.clock.setTime(7);
            i++;
        }
    }


    public static void main(String[] args) throws InterruptedException {
        //need to instansiate the animals here first
        Lion Leo = new Lion("Leo the Lion");
        Lion Larry = new Lion("Larry the Lion");
        Cat Charlie = new Cat("Charlie the Cat");
        Cat Camy = new Cat("Camy the Cat");
        Hippo Henry = new Hippo("Henry the Hippo");
        Hippo Harley = new Hippo("Harley the Hippo");
        Rhino Rich = new Rhino("Rich the Rhino");
        Rhino Raven = new Rhino("Raven the Rhion");
        Dog Doug = new Dog("Doug the Dog");
        Dog Daniel = new Dog("Daniel the Dog");
        Wolf Walter = new Wolf("Walter the Wolf");
        Wolf Waren = new Wolf("Warren the Wolf");
        Frog Fred = new Frog("Fred the Frog");
        Frog Francis = new Frog("Francis the Frog");
        Toad Tim = new Toad("Tim the Toad");
        Toad Toadete = new Toad("Toadete the Toad");
        Doug.setFetchBehavior(new lazyFetch());//Strategy design implementation
        Walter.setFetchBehavior(new fastFetch());//Delegation, and changing behavior of Doug on runtime

        Animal[] zooAnimals = {Leo, Larry, Charlie, Camy, Henry, Harley, Rich, Raven, Doug, Daniel, Walter, Waren, Fred, Francis, Tim, Toadete}; // All animals put into an Array

        Scanner in = new Scanner(System.in); //I have never used java so Scanner was found from ::https://stackoverflow.com/questions/4238384/java-equivalent-of-cin-c
        //I then reused logic of this for the rest of the assignment
        System.out.println("How many days would you like to run the zoo?");
        int days = 0;
        days = in.nextInt();
        Zoo zoo = new Zoo(days, zooAnimals);
        zoo.runDaysAtZoo();

    }
}

class ZooClock extends Observable {
    private int hour = 7;
    public void setTime(int hour) {
        this.hour = hour;
    }
    public int getTime() {
        return hour;
    }
    public void runClock() throws InterruptedException {
        hour+=1;
        if(hour > 12) {
          System.out.println(hour%12 + " PM");
        } else {
          System.out.println(hour + " AM");
        }
        //clock notifies observers when the clock changes
        setChanged();
        notifyObservers();
        clearChanged();
    }
}


abstract class ZooEmployee extends Observable {
    protected boolean atWork = false;
    public void goToWork(int day) { //zoo should tell employees to go to work
        if(!atWork) { //just fun use of class attributes
            System.out.println("Employee arrives at zoo on Day " + (day+1) + " at the zoo!");
            atWork = true;
        }
    }
    private void leaveWork() {
        System.out.println("leaving work");
        atWork=true;
    }
    abstract public void doJob(int hour); //an example of ABSTRACTION:
    //since each employee has a different job to do, there is no default uniform job
    //so it is up to the type of employee (subclass) to define the job they have to do

}


class ZooKeeper extends ZooEmployee implements Observer{
    public int tasks = 0;
    public Animal[] zooAnimals;
    public ZooKeeper(Animal[] zooAnimals) { //construct list of which animals a zookeeper is looking after
        this.zooAnimals = zooAnimals;
    }
    public void goToWork(int day) { //zoo should tell employees to go to work
        if(!atWork) { //just fun use of class attributes
            System.out.println("Zookeeper arrives at zoo on Day " + (day+1) + " at the zoo!");
            atWork = true;
        }
    }
    private void leaveWork() {
        System.out.println("Zookeeper leaves the zoo for the night\n");
        atWork = false;
        tasks = 0;
    }
    public void doJob(int hour) { //implements doJob for ZooKeeper class
        int animalNum = zooAnimals.length;
        if (hour == 9) {
            tasks+=1;
            //keeper notifies observers when task occurs
            setChanged();
            notifyObservers();
            clearChanged();
            for (int i = 0; i < animalNum; i++) {
                wakeAnimals(zooAnimals[i]);
            }
        }
        if (hour == 10) {
            tasks+=1;
            //keeper notifies observers when task occurs
            setChanged();
            notifyObservers();
            clearChanged();
            System.out.println("ROLL CALL!!!\n"); //roll call outside loop
            for (int i = 0; i < animalNum; i++) {
                rollCallAnimals(zooAnimals[i]);
            }
        }
        if (hour == 12) {
            tasks+=1;
            //keeper notifies observers when task occurs
            setChanged();
            notifyObservers();
            clearChanged();
            for (int i = 0; i < animalNum; i++) {
                feedAnimals(zooAnimals[i]);
            }
        }
        if (hour == 14) {
            tasks+=1;
            //keeper notifies observers when task occurs
            setChanged();
            notifyObservers();
            clearChanged();
            for (int i = 0; i < animalNum; i++) {
                exerciseAnimals(zooAnimals[i]);
            }
        }
        if (hour == 19) {
            tasks+=1;
            //keeper notifies observers when task occurs
            setChanged();
            notifyObservers();
            clearChanged();
            for (int i = 0; i < animalNum; i++) {
                sleep(zooAnimals[i]);
            }
        }
        if (hour >= 20 && atWork) {
            leaveWork();
        }
    }
    public void wakeAnimals(Animal A) {
        System.out.println("Zookeeper wakes " + A.getName());
        A.wakeUp();
    }
    public void rollCallAnimals(Animal A){
        A.makeNoise();
    }
    public void feedAnimals(Animal A){
        System.out.println("Zookeeper feeds " + A.getName());
        A.Eat();
    }
    public void exerciseAnimals(Animal A) {
        System.out.println("Zookeeper tells " + A.getName() +" Exercises");
        A.Roam();
    }
    public void sleep(Animal A) {
        System.out.println("Zookeeper tells " + A.getName() + " goes to Sleep");
        A.Sleep();
    }

    @Override //every update function is part of the observer pattern
    public void update(Observable o, Object arg) {
        ZooClock c = (ZooClock) o;
        int hour = c.getTime();
        doJob(hour);
    }
}

class ZooFoodServer extends ZooEmployee implements Observer {
    public int servedFood = 0;
    public void goToWork(int day) {
        if(!atWork) {
          System.out.println("Foodserver arrives at zoo on Day " + (day+1) + " at the zoo!");
          atWork = true;
        }
    }
    private void leaveWork() {
        System.out.println("Foodserver leaves the zoo for the night\n");
        atWork = false;
        servedFood = 0;
    }

    public void doJob(int hour) {
        if(hour == 11 || hour == 16) {
          makeFood();
        }
        if(hour == 12 || hour == 17) {
          serveFood();
        }
        if(hour == 13 || hour == 18) {
          cleanArea();
        }
        if(hour >= 19 && atWork) {
          leaveWork();
        }
    }
    public void makeFood() {
        System.out.println("Food has been made.");
    }
    public void serveFood() {
        //server notifies announcer when the food is served
        servedFood+=1;
        setChanged();
        notifyObservers();
        clearChanged();
        System.out.println("Food has been served.");
    }
    public void cleanArea() {
        System.out.println("Foodserver cleaning area");
    }

    @Override
    public void update(Observable o, Object arg) {
        ZooClock c = (ZooClock) o;
        int hour = c.getTime();
        doJob(hour);
    }
}

class ZooAnnouncer extends ZooEmployee implements Observer {
    public void goToWork(int day) {
        if(!atWork) {
          System.out.println("ZooAnnouncer arrives at zoo on Day " + (day+1) + " at the zoo!");
          atWork = true;
        }
    }
    private void leaveWork() {
        System.out.println("ZooAnnouncer leaves the zoo for the night\n");
        atWork = false;
    }

    public void doJob(int hour) {
      if(hour >= 20 && atWork) {
        leaveWork();
      }
    }

    @Override
    public void update(Observable o, Object arg) {
      //instanceof to do this trick learned from https://stackoverflow.com/questions/4584541/check-if-a-class-object-is-subclass-of-another-class-object-in-java
      if (o instanceof ZooKeeper) {
        ZooKeeper z1 = (ZooKeeper) o;
        switch(z1.tasks) {
          case 1:
            System.out.println("This is Zoo Announcer. The ZooKeeper is waking the animals");
            break;
          case 2:
            System.out.println("This is Zoo Announcer. The ZooKeeper is rollcalling the animals");
            break;
          case 3:
            System.out.println("This is Zoo Announcer. The ZooKeeper is feeding the animals");
            break;
          case 4:
            System.out.println("This is Zoo Announcer. The ZooKeeper is exercising the animals");
            break;
          case 5:
            System.out.println("This is Zoo Announcer. The ZooKeeper is putting the animals to sleep");
            break;
        }
      }
      if (o instanceof ZooFoodServer) {
        ZooFoodServer fs1 = (ZooFoodServer) o;
        switch(fs1.servedFood) {
          case 1:
            System.out.println("This is Zoo Announcer. Lunch is being served");
            break;
          case 2:
            System.out.println("this is Zoo Announcer. Dinner is being served");
        }
      }
      if (o instanceof ZooClock) {
        ZooClock c1 = (ZooClock) o;
        int hour = c1.getTime();
        doJob(hour);
      }
    }
}

interface fetchBehavior{// This is where fetch behavior/strategy pattern is implemented, it is implemented for the canine subclass
    public void preformFetch();
    public void setFetchBehavior(fetchBehavior fb);
}

class lazyFetch implements fetchBehavior{//This is not overwriting the dog/wolf class but being changed at runtime, dog is lazy

    @Override
    public void preformFetch() {
        System.out.println(" lazily fetches");
    }

    @Override
    public void setFetchBehavior(fetchBehavior fb) {
        return;//We wont be using this
    }
}

class fastFetch implements fetchBehavior{//wolf gets the fast fetch, but it can be changed if the program were to be user dependant

    @Override
    public void preformFetch() {
        System.out.println(" fetches with haste");
    }

    @Override
    public void setFetchBehavior(fetchBehavior fb) {
        return;//We wont be using this
    }
}


abstract class Animal { //an example of an ABSTRACT CLASS:
    //instances of the Animal class cannot exist because it is an abstract class
    //it's main purpose is to define the commonality of the subclasses
    //a list of animals can exist despite them not needing the be the same type
    //and a zookeeper object can call methods define here and have it work for all the zooAnimals
    //regardless of subclass
    protected String name; //this and the two methods below are a good example of ENCAPSULATION.
    //the name of the animal isn't accessible to other classes (that aren't subclasses), so objects that don't inherent from the Animal class
    //can only access/change this variable through our get/set methods.
    //variable is protected so subclasses can access the variable

    //name is also a good example of IDENTITY:
    //the name differentiates between which object is which
    //names can technically be the same. a better Identity would be a UID (unique Identifying number)
    //although there is no reason to implement a UID in this project
    public void setName(String s) { //changes protected name variable
        name = s;
    }
    public String getName() { //returns protected name variable
        return name;
    }
    public void wakeUp() { //outputs animal wakeup text
        System.out.println(name + " wakes up\n");
    }
    public void makeNoise() { //outputs animal rollcall text
        System.out.println(name + " makes noise\n");
    }
    public void Eat() { //outputs animal eating text
        System.out.println(name + " eats\n");
    }
    public void Roam() { //outputs animal roam text
        System.out.println(name + " roams\n");
    }
    public void Sleep() { //outputs animal sleep text
        System.out.println(name + " goes to sleep\n");
    }
    //All animals need to have the sleep, roam , eat functions.
    //This will correspond to the text that gets output.
    //All animals need to inherit these ::wake up, make noise, eat, roam, and sleep.

    public static int RandNumberFunction() {
        return (int) (Math.random()*100);// Gives a random number from 1-100
        //This Randnumber function was taken from(below)
        //https://www.java67.com/2015/01/how-to-get-random-number-between-0-and-1-java.html
    }
}
//Start all Pachyderms
abstract class Pachyderm extends Animal {
    @Override
    public void Roam() { //an example of POLYMORPHISM:
        //this method overrides and implements the roam function differently
        //than how it was implemented by the superclass (Animal)
        //which is what polymorphism is and it's very useful.
        int randNum = RandNumberFunction();
        if (randNum <= 25) {
            System.out.println(name + " Charges!!\n");
        } else {
            System.out.println(name + " roams\n");
        }
    }
}

class Rhino extends Pachyderm {
    Rhino(String s){ //Constuctor to make naming easier
        this.name = s;
    }
    public void makeNoise() {
        System.out.println(name + " Snorts\n");
    }
}

class Hippo extends Pachyderm{
    Hippo(String s){//Constuctor to make naming easier
        this.name = s;
    }
    public void makeNoise() {
        System.out.println(name + " Hums\n");
    }
}
//End all Pachyderms

//Start all felines
abstract class Feline extends Animal{
    public void makeNoise(){
        System.out.println(name + " purrrr\n");
    }
    @Override
    public void Sleep(){
        int num = RandNumberFunction();
        if (num <= 30){//30% chance
            Roam();
        }
        else if (num > 30 && num <= 70) { //40% Chance
            Sleep();
        } else {
            makeNoise(); //30% Chance
        }

    }
}

class Lion extends Feline {
    Lion(String s) { //Constuctor to make naming easier
        this.name = s;
    }
    @Override
    public void makeNoise(){
        System.out.println(name + " Roars!\n");
    }
}

class Cat extends Feline{

    Cat(String s) { //Constuctor to make naming easier
        this.name = s;
    }
    @Override
    public void makeNoise() {
        System.out.println(name + " Purrs...\n");
    }

}
//End all Felines

//Start all Canines
 abstract class Canine extends Animal implements fetchBehavior {
    fetchBehavior canFetch;//creating the interface for the strategy pattern

    public void setFetchBehavior(fetchBehavior fb) {
        canFetch = fb ;//set the fetch behavior for the dog/wolf
    }
    public void preformFetch(){
        canFetch.preformFetch();
    }

}

class Dog extends Canine{
    Dog(String s) { //Constuctor to make naming easier
        this.name = s;
    }
    @Override
    public void Roam() {
        int num = RandNumberFunction();
        if(num <= 25) { // 25% chance
            System.out.println(name + " decided to Dig instead of Roam\n");
        } else {
            System.out.println(name + " roams\n");
        }
    }
    public void makeNoise() {
        System.out.println(name + " Barks\n");
    }

}

class Wolf extends Canine {
    Wolf(String s) { //Constuctor to make naming easier
        this.name = s;
    }
    public void makeNoise() {
        System.out.println(name + " Howls\n");
    }
}

//End all Canines

//Start all Amphibians
abstract class Amphibian extends Animal {

}

class Frog extends Amphibian{
    Frog(String s){ //Constuctor to make naming easier
        this.name = s;
    }
    public void makeNoise() {
        System.out.println(name + " Ribbits\n");
    }
}

class Toad extends Amphibian{
    Toad(String s){//Constuctor to make naming easier
        this.name = s;
    }
    public void makeNoise() {
        System.out.println(name + " Croaks\n");
    }
}
//End all amphibians
