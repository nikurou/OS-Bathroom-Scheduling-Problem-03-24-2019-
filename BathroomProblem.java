
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.ArrayList; // import the ArrayList class
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
/***************************************************************
* file: BathroomProblem.java
* author: Dylan Chung
* class: CS 4310.02 Operating Systems
*
* assignment: program 2
* date last modified: 3/24/2019
*
* purpose: An implementation of the unisex bathroom problem in which only 
* up to three males may be in the bathroom at a time or up to three females at a time
* but never at the same time. Solved through the use of semaphores and mutexes to schedule the 
* threads. 
* 

* *
****************************************************************/
class BathroomProblem {

    static Semaphore maleLock = new Semaphore(3);
    static Semaphore femaleLock = new Semaphore(3);
    static Semaphore bathroom = new Semaphore(1); //BATHROOM MUTEX
    static Semaphore maleMutex = new Semaphore(1);
    static Semaphore femaleMutex = new Semaphore(1);
    static Semaphore turnstile = new Semaphore(1); 
    static int maleCount = 0;
    static int femaleCount = 0;
    static List<int[]> people_list = new ArrayList<int[]>(); //The list of people in line.
            
     //Class: Male
    //Purpose: The Male class which handles the threads for male restroom goers.  
   static class Male implements Runnable{
        
        @Override
        public void run() {
            try {
                //Arrive
                Arrive();
                //Using the bathroom section
                UseFacilities();
                //Depart 
                Depart();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        //Method: Arrive() 
        //Purpose: Acquires a mutex lock called Turnstile (to prevent starvation) and maleMutex so that when we increment maleCount, 
        //         we don't mess up the increment and corrupt the value, and if it's the first person in, it'll lock the bathroom
        //         so that only males may enter. Mutexes are released afterwards, but acquires the lock for MaleLock 
        //         as it will be calling UseFacilities and Depart() afterwards.
        public void Arrive() throws InterruptedException{
                turnstile.acquire();
                maleMutex.acquire();
                maleCount = maleCount + 1;
                
                if(maleCount == 1)
                    bathroom.acquire();
                
                maleMutex.release();
                turnstile.release();
                maleLock.acquire();
        }
        
        //Method: Depart()
        //Purpose: The person is now done using the restroom, so we release the lock on maleLock, and acquire the mutex so that we can 
        //         alter maleCount. Release the bathroom so that others can use it if no more males are in it.
        //         Release the mutex after we're done altering the counter variable. 
        public void Depart() throws InterruptedException{
             maleLock.release();
               maleMutex.acquire();
               maleCount = maleCount -1;
               if(maleCount == 0)
                   bathroom.release();
                maleMutex.release();
        }
    }
    //Class: Female
    //Purpose: The female class which handles the threads for the female bathroom goers.
    static class Female implements Runnable {

        @Override
        public void run() {
              try {
                //Arrive
                Arrive(); 
                    
                //Use Bathroom
                UseFacilities();

                //Depart
                 Depart();
  
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        
         //Method: Arrive() 
        //Purpose: Acquires a mutex lock called Turnstile (to prevent starvation) and maleMutex so that when we increment maleCount, 
        //         we don't mess up the increment and corrupt the value, and if it's the first person in, it'll lock the bathroom
        //         so that only males may enter. Mutexes are released afterwards, but acquires the lock for MaleLock 
        //         as it will be calling UseFacilities and Depart() afterwards.
      public void Arrive() throws InterruptedException {
                turnstile.acquire();
                femaleMutex.acquire();
                femaleCount = femaleCount + 1;
                
                if(femaleCount == 1)
                    bathroom.acquire();
                
                femaleMutex.release();
                turnstile.release();
                femaleLock.acquire();
      }
        //Method: Depart()
        //Purpose: The person is now done using the restroom, so we release the lock on the Lock, and acquire the mutex so that we can 
        //         alter Count. Release the bathroom so that others can use it if no more pople are in it.
        //         Release the mutex after we're done altering the counter variable. 
      public void Depart() throws InterruptedException {
               femaleLock.release();
               femaleMutex.acquire();
               femaleCount = femaleCount -1;
               if(femaleCount == 0)
                   bathroom.release();
                femaleMutex.release();
          
      }
    }
    //Method: main()
    //Purpose: The main method. Reads the arraylist of integers that represents genders
    //         and based on that list, creates the appropriate threads and runs them.
    public static void main(String[] args) throws Exception {
        
        int numPeople;
        
        
        Scanner kb = new Scanner(System.in);
        System.out.println("There are three cases to run, listed below...");
        System.out.println("1.) 5 : DELAY(10) : 5 : DELAY(10) : 5 : DELAY(10) : 5");
        System.out.println("2.)10 : DELAY(10) : 10");
        System.out.println("3.)20");
        
        System.out.println("Please enter 1, 2, or 3 to run the desired case...");
        
        int choice = kb.nextInt();
      
        if(choice == 1){ //5 : DELAY(10) : 5 : DELAY(10) : 5 : DELAY(10) : 5
            System.out.println("Executing Case One");
            
            for(int j = 0; j <= 3; j++){
                numPeople = 5;
                weightedRandomGender(numPeople); 
                for(int i = 0; i< numPeople; i++){
                    if(people_list.get(i)[1] == 1){
                        Female f = new Female();
                        Thread t = new Thread(f);
                        t.setName("Female");
                        t.start();
                    }
                    else if(people_list.get(i)[1] == 0){
                        Male m = new Male();
                        Thread t = new Thread(m);
                        t.setName("Male");
                        t.start();
                    }
                }
                System.out.println("Forcing 10 second delay...");
                TimeUnit.SECONDS.sleep(10);
             }
       
        }
        if(choice == 2){ //10 : DELAY(10) : 10
             System.out.println("Executing Case Two");
            
            for(int j = 0; j <= 1; j++){
                numPeople = 10;
                weightedRandomGender(numPeople); 
                for(int i = 0; i< numPeople; i++){
                    if(people_list.get(i)[1] == 1){
                        Female f = new Female();
                        Thread t = new Thread(f);
                        t.setName("Female");
                        t.start();
                    }
                    else if(people_list.get(i)[1] == 0){
                        Male m = new Male();
                        Thread t = new Thread(m);
                        t.setName("Male");
                        t.start();
                    }
                }
                System.out.println("Forcing 10 second delay...");
                TimeUnit.SECONDS.sleep(10);
             }    
        }
        if(choice == 3) { //20 
            System.out.println("Executing Case Three");
            numPeople = 20;
            weightedRandomGender(numPeople); 
            for(int i = 0; i< numPeople; i++){
                    if(people_list.get(i)[1] == 1){
                        Female f = new Female();
                        Thread t = new Thread(f);
                        t.setName("Female");
                        t.start();
                    }
                    else if(people_list.get(i)[1] == 0){
                        Male m = new Male();
                        Thread t = new Thread(m);
                        t.setName("Male");
                        t.start();
                    }
                }
        }

    }
    //Method: UseFacilities()
    //Purpose: Prints out a debug message simulating the thread's use of a bathroom by 
    //         Having a fixed delay of 5 seconds as stated in the prompt so I didn't bother 
    //         passing time as a parameter.
   
    private static void UseFacilities() throws InterruptedException{
          System.out.println( Thread.currentThread().getName() + " is USING BATHROOM");
          //System.out.println("Available Permits Leftl " + maleLock.availablePermits());
          Thread.sleep(5000); //5 SECONDS TO DUMP HUMAN WASTE
          System.out.println(Thread.currentThread().getName() + " has FINISHED THEIR BUSINESS");
    }
    
    //Method: weightedRandomGender(int numPeople)
    //Purpose: Populates an arraylist with a random integer between 0 and 1. 
    //         However it's weighted so that it's a 60% chance that it will return 1.
    //         Where 0 represents male, and 1 represents female.
    private static void weightedRandomGender(int numPeople){
        Random rand = new Random();
        rand.setSeed(9);
        people_list = new ArrayList<int[]>();
        
        for(int i = 0; i<numPeople; i++){
            int n = rand.nextInt(10) + 1;
            //System.out.println("This is n: " + n);
            int data[] = new int[2];
            data[0] = i; //ID 
            if(n<=6){
              System.out.println("Female Generated");
                data[1] = 1;
                people_list.add(data);
            }
            else{
              System.out.println("Male Generated");
                data[1] = 0;
                people_list.add(data); 
            }
        }
    }
}
