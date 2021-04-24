package Assignment1Update;

import java.util.*;

public class FlightScheduler {

    private static FlightScheduler instance;
    private List<Flight> flightList;
    private List<Location> locationList;
    private Map<Integer,Flight> flightMap;
    private Map<String,Location> locationMap;

    public FlightScheduler(String[] args){
        flightList = new ArrayList<>();
        locationList = new ArrayList<>();
        flightMap = new HashMap<>();
        locationMap = new HashMap<>();
    }

    public List<Flight> getFlightList(){

        return flightList;
    }


    public List<Location> getLocationList(){

        return locationList;
    }

    public Map<Integer,Flight> getFlightMap(){

        return flightMap;
    }

    public Map<String,Location> getLocationMap(){

        return locationMap;
    }

    public static void main(String[] args) {
        instance = new FlightScheduler(args);
        instance.run();
    }

    public void run(){
        Scanner in = new Scanner(System.in);
        String[] cmds = null;
        do{
            System.out.print("User:");
            cmds = in.nextLine().split(" ");
        }while(operationProcess(cmds));
    }

    private boolean operationProcess(String[] args){
        switch(args[0].toUpperCase()){
            case "LOCATIONS":
                LocationProcess.listAllLocations(args,this);
                break;
            case "DEPARTURES":
                LocationProcess.listDepartureAllocation(args, this);
                break;
            case "ARRIVALS":
                LocationProcess.listArrivalAllocation(args, this);
                break;
            case "SCHEDULE":
                LocationProcess.listSchedule(args, this);
                break;
            case "LOCATION":
                if(args.length == 1){
                    System.out.println("Usage:\nLOCATION <name>\nLOCATION ADD <name> <latitude> <longitude> <demand_coefficient>\nLOCATION IMPORT/EXPORT <filename>"+"\n");
                    break;
                }
                switch (args[1].toUpperCase()){
                    case "ADD":
                        LocationProcess.locationAdd(args, this, true);
                        break;
                    case "IMPORT":
                        LocationProcess.locationImport(args, this);
                        break;
                    case "EXPORT":
                        LocationProcess.locationExport(args, this);
                        break;
                    default:
                        if(!locationMap.containsKey(args[1].toUpperCase())){
                            System.out.println("Invalid location name"+"\n");
                            break;
                        }
                        if(args.length == 2){
                            LocationProcess.viewLocation(args[1], this);
                        }
                        else{
                            System.out.println("Invalid command. Type 'help' for a list of commands."+"\n");
                        }
                        break;
                }
                break;
            case "TRAVEL":
                FlightProcess.processTravel(args,this);
                break;
            case "FLIGHTS":
                FlightProcess.listAllFlights(args, this);
                break;
            case "FLIGHT":
                if(args.length == 1){
                    System.out.println("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] [num]\nFLIGHT ADD <departure time> <from> <to> <capacity>\nFLIGHT IMPORT/EXPORT <filename>"+"\n");
                    break;
                }
                switch (args[1].toUpperCase()){
                    case "ADD":
                        FlightProcess.addTheFlight(args, this,true);
                        break;
                    case "IMPORT":
                        FlightProcess.flightImport(args, this);
                        break;
                    case "EXPORT":
                        FlightProcess.flightExport(args, this);
                        break;
                    default:
                        if(!CommonProcess.isInteger(args[1])|| ! flightMap.containsKey(Integer.valueOf(args[1]))){
                            System.out.println("Invalid Flight ID."+"\n");
                            break;
                        }
                        int flightId = Integer.parseInt(args[1]);
                        if(args.length == 2){
                            FlightProcess.viewSingleFlight(flightId, this);
                        }
                        else{
                            System.out.println("Invalid command. Type 'help' for a list of commands."+"\n");
                        }
                        if(args.length >= 3){
                            switch(args[2].toUpperCase()){
                                case "REMOVE":
                                    FlightProcess.removeFlight(args, this);
                                    break;
                                case "RESET":
                                    FlightProcess.resetFlight(args,this);
                                    break;
                                case "BOOK":
                                    FlightProcess.bookFlight(args,this);
                                    break;
                            }
                        }
                        break;
                }
                break;
            case "HELP":
                System.out.println(
                        "FLIGHTS - list all available flights ordered by departure time, then departure location name\n" +
                                "FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight\n" +
                                "FLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file\n" +
                                "FLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price,\n" +
                                "capacity, passengers booked)\n" +
                                "FLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price,\n" +
                                "and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1\n" +
                                "passenger. If the given number of bookings is more than the remaining capacity, only accept bookings\n" +
                                "until the capacity is full.\n" +
                                "FLIGHT <id> REMOVE - remove a flight from the schedule\n" +
                                "FLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state.\n\n"+
                                "LOCATIONS - list all available locations in alphabetical order\n" +
                                "LOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location\n" +
                                "LOCATION <name> - view details about a location (it’s name, coordinates, demand coefficient)\n" +
                                "LOCATION IMPORT/EXPORT <filename> - import/export locations to csv file\n" +
                                "SCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart\n" +
                                "DEPARTURES <location_name> - list all departing flights, in order of departure time\n" +
                                "ARRIVALS <location_name> - list all arriving flights, in order of arrival time\n\n" +
                                "TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and\n" +
                                "destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not\n" +
                                "provided, display the first one in the order. If n is larger than the number of flights available, display the\n" +
                                "last one in the ordering.\n\n" +
                                "can have other orderings:\n" +
                                "TRAVEL <from> <to> cost - minimum current cost\n" +
                                "TRAVEL <from> <to> duration - minimum total duration\n" +
                                "TRAVEL <from> <to> stopovers - minimum stopovers\n" +
                                "TRAVEL <from> <to> layover - minimum layover time\n" +
                                "TRAVEL <from> <to> flight_time - minimum flight time\n\n" +
                                "HELP – outputs this help string.\n" +
                                "EXIT – end the program."+"\n"
                );
                break;
            case "EXIT":
                System.out.println("Application closed."+"\n");
                return false;
            default:
                System.out.println("Invalid command. Type 'help' for a list of commands."+"\n");
        }
     return true;
    }

}