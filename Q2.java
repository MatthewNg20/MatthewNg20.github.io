import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

class Theatre{
    //Random object to help with randomising functions
    private Random rand = new Random();
    //A variable to hold which seat the user wishes to reserve
    private int seatReserved;
    //A list of integers to store what seat numbers are purchased
    private List<Integer> record = new CopyOnWriteArrayList<>();
    //Manually set the seat count here
    //The seat number is according to the seat count. 100 seats means 0 - 99 number
    private int seatCount;

    //Initialise the theatre's seat count in the main.
    Theatre(int seatCount){
        this.seatCount = seatCount;
    }

    //Adding the seat reserved to the record
    public synchronized void setSeatReserved(int x){
        this.seatReserved = x;
        if(!(record.contains(x))){
            record.add(x);
            System.out.println(Thread.currentThread().getName() + ": Reservation in process...Booking seat: " + (getSeatReserved()));
        }
    }

    //Allows for printing the seat number to the user
    public synchronized int getSeatReserved(){
        return seatReserved;
    }

    //Allows for printing all of the seat numbers reserved to the user for verification purposes
    public synchronized List<Integer> getRecord(){
        return record;
    }

    public synchronized void bookSeat(){
        int temp = rand.nextInt(seatCount)+1;
        //This is to randomise how many seats the users take (between 1 to 3)
        int randomizer = rand.nextInt(3)+1;
        //Check if the seat that is desired to be reserved has already been reserved by someone else
        if(!(record.contains(temp))){
            for(int i = 0; i < randomizer; i++){
                //If the user wishes the purchase a seat at the edge of the theater (for example, seat number 99, 100, and 101 with the seat count of 100)
                //The system will only accept the initial seat, it will not reserve the subsequent seats
                if(!((temp+randomizer) > seatCount)){
                    setSeatReserved(temp+i);
                    //System.out.println(Thread.currentThread().getName() + ": Reservation success!...Booking seat: " + (temp + i));
                }
                else{
                    setSeatReserved(temp);
                    System.out.println(Thread.currentThread().getName() + " bad request. Trying to reserve more than 1 seat that is outside the bounds: " + (temp + i));
                    System.out.println(Thread.currentThread().getName() + ": Reservation only done for 1 seat. Booking seat: " + temp);
                    System.out.println("Note: Seat number is reprinted regardless if the seat is already purchased by you. This is to confirm your reservation number");
                }
                //A system delay to simulate that the user has made a successful purchase
                try{
                    System.out.println(Thread.currentThread().getName() + ": System is processing your reservation, please wait...");
                    wait(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        else{
            System.out.println(Thread.currentThread().getName() + ": Reservation in process...Booking seat: " + (getSeatReserved()));
            System.out.println(Thread.currentThread().getName() + ": Reservation failed. " + (getSeatReserved()) + " is reserved");

        }
        //Another system delay to simulate a successful purchase if the user has completed purchasing all the seats it wish to book
        try{
            System.out.println("System is processing the final results, please wait...");
            wait(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        //
    }
}

//The thread class. It runs the bookSeat() method.
class Customer implements Runnable{
    private Theatre t;

    Customer(Theatre t){
        this.t = t;
    }

    public void run(){
        // A user can randomly choose 1 to 3 seats at a time
        // Error: the user can select the same seats
        t.bookSeat();
            //System.out.println(Thread.currentThread().getName() + ": " + t.getSeatReserved());

        //Note: the seats reserved so far is to show which seats have been booked by all users
        //Note: if the seats reserved so far is equal for 2 users or threads, it means that they both finished purchasing at the same time.
        System.out.println(Thread.currentThread().getName() + " has done purchasing. Seats reserved so far: " + t.getRecord());

    }
}

public class Q2{
    public static void main(String[] args){

        //Manually enter the number of seats here
        Theatre theatre1 = new Theatre(20);

        // Manually set the number of threads or users here
        int numUsers = 5;
        for(int i = 0; i < numUsers; i++){
            new Thread(new Customer(theatre1)).start();
        }
    }
}