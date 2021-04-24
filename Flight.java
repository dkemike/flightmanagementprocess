package Assignment1Update;

public class Flight {
    private int id;
    private String departureDayTime;
    private String departureDay;
    private String departureTime;

    private String arriveDayTime;
    private String arriveDay;
    private String arriveTime;

    private String startLocation;
    private String endLocation;

    private double distance;
    private int duration;
    private int capacity;
    private double price;
    private int numOfBook;
    private String sortDayTime;

    public Flight(int id,String departureDayTime,String startLocation,String endLocation,int capacity,int numOfBook,int distance,int duration,String arriveDayTime){
        this.id = id;
        this.departureDayTime = departureDayTime;
        this.departureDay = departureDayTime.split(" ")[0].substring(0,3);
        this.departureTime = departureDayTime.split(" ")[1];
        this.arriveDayTime = arriveDayTime;
        this.arriveDay = arriveDayTime.split(" ")[0].substring(0,3);
        this.arriveTime = arriveDayTime.split(" ")[1];
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.capacity = capacity;
        this.price = 0;
        this.numOfBook = numOfBook;
        this.distance = distance;
        this.duration = duration;
        this.sortDayTime = " ";
    }


    public String getSortDayTime(){

        return sortDayTime;
    }

    public void setSortDayTime(String sortDayTime){

        this.sortDayTime = sortDayTime;
    }
    public int getId(){

        return id;
    }

    public String getDepartureDay(){

        return departureDay;
    }

    public String getDepartureDayTime(){

        return departureDayTime;
    }

    public int getCapacity(){

        return capacity;
    }

    public String getDepartureTime(){

        return departureTime;
    }

    public String getArriveDayTime(){

        return arriveDayTime;
    }

    public String getStartLocation(){

        return startLocation;
    }

    public String getEndLocation(){

        return endLocation;
    }

    public double getPrice(){

        return price;
    }

    public void setPrice(double price){

        this.price = price;
    }
    public int getNumOfBook(){

        return numOfBook;
    }

    public void setNumOfBook(int numOfBook){

        this.numOfBook = numOfBook;
    }

    public String getArriveDay(){

        return arriveDay;
    }


    public String getArriveTime(){

        return arriveTime;
    }

    public double getDistance(){

        return distance;
    }

    public int getDuration(){

        return duration;
    }

    public String toString(){
        return departureDayTime + "," +  startLocation + "," + endLocation + "," + capacity + "," + numOfBook + "\n";
    }
}