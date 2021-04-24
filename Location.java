package Assignment1Update;

import java.util.*;

public class Location {
    private String name;
    private String nameToUpper;
    private double latitude;
    private double longitude;
    private double demandCoefficient;

    private List<Flight> arriveFlights;
    private List<Flight> departFlights;

    public Location(String name,double latitude,double longitude,double demandCoefficient){
        this.name = name;
        this.nameToUpper = name.toUpperCase();
        this.latitude = latitude;
        this.longitude = longitude;
        this.demandCoefficient = demandCoefficient;
        arriveFlights = new ArrayList<>();
        departFlights = new ArrayList<>();
    }

    public List<Flight> getArriveFlights(){

        return arriveFlights;
    }

    public List<Flight> getDepartFlights(){

        return departFlights;
    }
    public String getName(){

        return name;
    }

    public double getLatitude(){

        return latitude;
    }

    public double getLongitude(){

        return longitude;
    }

    public double getDemandCoefficient(){

        return demandCoefficient;
    }

    public String getNameToUpper(){

        return nameToUpper;
    }

    public String toString(){
        return name + "," +  latitude  + "," + longitude + "," + demandCoefficient + "," + "\n";
    }


    public static double formula(double value){

        return Math.pow(Math.sin(value / 2.0),2.0);
    }


    public static double distance(Location startLocation,Location endLocation ){
        double radius = 6371;
        double startLat = startLocation.getLatitude(),startLon = startLocation.getLongitude();
        double endLat = endLocation.getLatitude(),endLon = endLocation.getLongitude();
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLon - startLon);
        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);
        double a = formula(dLat) + Math.cos(startLat) * Math.cos(endLat) * formula(dLon);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return radius * c;
    }


    public void addArrival(Flight f) {

    }

    public void addDeparture(Flight f) {

    }

    /**
     * Check to see if Flight f can depart from this location.
     * If there is a clash, the clashing flight string is returned, otherwise null is returned.
     * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's departure time.
     * @param f The flight to check.
     * @return "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>". Return null if there is no clash.
     */
  /*
    public String hasRunwayDepartureSpace(Flight f) {
        //check departures first


        //check arrivals next


    }
    */
   /*

    /**
     * Check to see if Flight f can arrive at this location.
     * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's arrival time.
     * @param f The flight to check.
     * @return String representing the clashing flight, or null if there is no clash. Eg. "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>"
     */ /*
    public String hasRunwayArrivalSpace(Flight f) {
        //check departures first

        //check arrivals next
    }

    */
}