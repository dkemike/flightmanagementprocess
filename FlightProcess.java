package Assignment1Update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.*;



public class FlightProcess {
    private static final double speed = 720;
    private static final double radius = 6371;

    public static boolean addTheFlight(String[] args, FlightScheduler f, boolean showAddResult){
        try{
            if(args.length < 7)throw new IllegalArgumentException("Usage: FLIGHT ADD <departure time> <from> <to> <capacity>\nExample: FLIGHT ADD Monday 18:00 Sydney Melbourne 120");
            String departureDayTime = args[2] + " " + args[3];
            try{
                CommonProcess.longDayTimeFormatter.parse(departureDayTime);
            } catch(DateTimeParseException e){
                throw new IllegalArgumentException("Invalid departure time. Use the format <day_of_week> <hour:minute>,with 24h time.");
            }
            String startLocation = args[4];
            if(!f.getLocationMap().containsKey(startLocation.toUpperCase())) throw new IllegalArgumentException("Invalid starting location.");
            String endLocation = args[5];
            if(!f.getLocationMap().containsKey(endLocation.toUpperCase())) throw new IllegalArgumentException("Invalid ending location.");
            if(startLocation.equalsIgnoreCase(endLocation)) throw new IllegalArgumentException("Source and destination cannot be the same place.");
            if((!CommonProcess.isInteger(args[6])|| Integer.parseInt(args[6])<= 0)) throw new IllegalArgumentException("Invalid positive integer capacity.");
            int capacity = Integer.parseInt(args[6]);
            int numOfBook = 0;
            if(args.length == 8){
                if ( (!CommonProcess.isInteger(args[7]) || Integer.parseInt(args[7]) < 0)) throw new IllegalArgumentException("Invalid number of passengers to book.");
                numOfBook = Integer.parseInt(args[7]);
            }
            int distance = calcFlightDistance(startLocation,endLocation, f);
            int duration = calcFlightDuration(distance);
            String arrivalDayTime = calcFlightArrivalDayTime(duration,departureDayTime);
            int id = f.getFlightList().size();
            Flight newFlight = new Flight(id,departureDayTime,startLocation,endLocation,capacity,numOfBook,distance,duration,arrivalDayTime);
            newFlight.setPrice(calcFlightPrice(1,newFlight, f));
            checkFlightScheduleConflict(newFlight, f);
            f.getFlightList().add(newFlight);
            f.getFlightMap().put(id, newFlight);
            if(showAddResult) System.out.println( "Successfully added Flight " + id + "." + "\n");
            f.getLocationMap().get(startLocation.toUpperCase()).getDepartFlights().add(newFlight);
            f.getLocationMap().get(endLocation.toUpperCase()).getArriveFlights().add(newFlight);

        }
        catch (IllegalArgumentException e){
            if(showAddResult) System.out.println(e.getMessage()+"\n");
            return false;
        }
        return true;
    }

    public static void flightImport(String[] args, FlightScheduler f){
        int importCnt = 0,invalidCnt = 0;
        try{
            if(args.length < 3) throw new FileNotFoundException("Error reading file.");
            File csvFile = new File(args[2]);
            Scanner fileReader = new Scanner(csvFile);

            while(fileReader.hasNextLine()){
                String row = fileReader.nextLine();
                String[] dataFields = row.split(",");

                if(dataFields.length < 5){
                    invalidCnt++;
                }else{
                    String[] flightArgs = {"","",dataFields[0].split(" ")[0],dataFields[0].split(" ")[1],dataFields[1],dataFields[2],dataFields[3],dataFields[4]};
                    if(addTheFlight(flightArgs, f,false))importCnt++;
                    else invalidCnt++;
                }
            }

            String importResult = "";
            importResult = importResult + "Imported " + importCnt +(importCnt > 1 ? " flights.":" flight." );
            if(invalidCnt != 0){
                importResult = importResult + "\n" + invalidCnt + (invalidCnt > 1 ? " lines were" : " line was") + " invalid.";
            }
            System.out.println(importResult + "\n");
            fileReader.close();
        }catch(FileNotFoundException e){
            System.out.println("Error reading file."+ "\n");
        }
    }

    public static void flightExport(String[] args, FlightScheduler f){
        try{
            if(args.length < 3) throw new FileNotFoundException("Error writing file.");
            FileWriter fileWriter = new FileWriter(args[2]);

            Collections.sort(f.getFlightList(),(f1, f2) -> f1.getId() - f2.getId());

            for(Flight flight: f.getFlightList()){
                fileWriter.write(flight.toString());
            }
            int numOfFlight = f.getFlightList().size();
            System.out.println("Export " + numOfFlight +" of " + (numOfFlight > 1 ? "flights." : "flight.")+ "\n");
            fileWriter.close();
        }catch (IOException e){
            System.out.println("Error writing file."+ "\n");
        }
    }

    public static void listAllFlights(String[] args, FlightScheduler f){
        try{
            if(args.length > 1) throw new IllegalArgumentException("Invalid command. Type 'help' for a list of commands.");
            System.out.println("Flights");
            System.out.println("-------------------------------------------------------");
            System.out.printf("%-5s%-12s%-12s%s\n","ID","Departure","Arrival","Source --> Destination");
            System.out.println("-------------------------------------------------------");
            if(f.getFlightList().size() == 0){
                System.out.println("(None)"+ "\n");
                return;
            }
            Collections.sort(f.getFlightList(),(f1, f2) -> {
                if(!f1.getDepartureDayTime().equalsIgnoreCase(f2.getDepartureDayTime())){

                    if(f1.getDepartureDay().equals(f2.getDepartureDay())){
                        SimpleDateFormat tempFormatter = new SimpleDateFormat("HH:mm");
                        try{
                            Date d1 = tempFormatter.parse(f1.getDepartureTime());
                            Date d2 = tempFormatter.parse(f2.getDepartureTime());
                            return (int) (d1.getTime() - d2.getTime());
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                    }
                    return CommonProcess.dayToNumber.get(f1.getDepartureDay()) - CommonProcess.dayToNumber.get(f2.getDepartureDay());
                }
                else{
                    return f1.getStartLocation().compareTo(f2.getStartLocation());
                }
            });

            for(Flight flight: f.getFlightList()){
                System.out.printf("%5s%-12s%-12s%s\n",flight.getId() + " ", flight.getDepartureDay() + " " + flight.getDepartureTime(), flight.getArriveDay() + " " + flight.getArriveTime(), flight.getStartLocation() + " --> " + flight.getEndLocation());
            }

        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage() +"\n");
        }
    }

    public static void removeFlight(String[] args, FlightScheduler f){
        try{
            if(args.length != 3)throw new IllegalArgumentException("Invalid command. Type 'help' for a list of commands.");
            int id = Integer.parseInt(args[1]);
            Flight flight = f.getFlightMap().get(id);
            f.getFlightMap().remove(id);
            int searchIdx = findFlight(f.getFlightList(),id);
            f.getFlightList().remove(searchIdx);
            for(Location location: f.getLocationList()){
                searchIdx = findFlight(location.getArriveFlights(),id);
                if(searchIdx != -1) location.getArriveFlights().remove(searchIdx);
                searchIdx = findFlight(location.getDepartFlights(),id);
                if(searchIdx != -1) location.getDepartFlights().remove(searchIdx);
            }
            System.out.println("Removed Flight " + id + ", " + flight.getDepartureDay() + " " + flight.getDepartureTime() + " " + flight.getStartLocation() + " --> " + flight.getEndLocation()+ "\n");
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage() + "\n");
        }
    }

    public static void resetFlight(String[] args, FlightScheduler f){
        try{
            if(args.length != 3) throw new IllegalArgumentException(" Invalid command. Type 'help' for a list of commands.");
            int id = Integer.parseInt(args[1]);
            Flight flight = f.getFlightMap().get(id);
            flight.setNumOfBook(0);
            flight.setPrice(calcFlightPrice(1,flight, f));
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage() +"\n");
        }
    }

    public static void bookFlight(String[] args, FlightScheduler f){
        try{
            if(args.length > 4) throw new IllegalArgumentException("Invalid command. Type 'help' for a list of commands.");
            int newBookNum = 1;
            if (args.length == 4){
                if(!CommonProcess.isInteger(args[3])|| Integer.parseInt(args[3]) <= 0 ) throw new IllegalArgumentException("Invalid number of passengers to books.");
                newBookNum = Integer.parseInt(args[3]);
            }
            int id = Integer.parseInt(args[1]);
            Flight flight = f.getFlightMap().get(id);
            int emptySpace = flight.getCapacity() - flight.getNumOfBook();
            boolean isFull = false;
            double totalPrice = 0;
            if(emptySpace < newBookNum){
                newBookNum = emptySpace;
                isFull = true;
            }
            totalPrice = newBookNum * flight.getPrice();
            flight.setNumOfBook(flight.getNumOfBook() + newBookNum);
            flight.setPrice(changeFlightPrice(flight, f));
            System.out.println("Booked " + newBookNum + " passengers on flight " + id + " for a total cost of $" + totalPrice + "\n");
            if(isFull){
                System.out.println("Flight is now full."+ "\n");
            }
        }
        catch (IllegalArgumentException e){
            System.out.println(e.getMessage()+ "\n");
        }
    }

    public static double changeFlightPrice(Flight flight, FlightScheduler f){
        double y = 0;
        double bookPercentage = (double)flight.getNumOfBook() / flight.getCapacity();
        if(bookPercentage <= 0.5){
            y = -0.4 * bookPercentage + 1;
        }
        else if(bookPercentage <= 0.7){
            y = bookPercentage + 0.3;
        }
        else{
            y = 0.2 / Math.PI * Math.atan( 20.0 * bookPercentage - 14.0) + 1;
        }
        return calcFlightPrice(y,flight, f);
    }

    public static double calcFlightPrice(double y, Flight flight, FlightScheduler f){
        double basePrice = 30;
        double startLocationCoe = f.getLocationMap().get(flight.getStartLocation().toUpperCase()).getDemandCoefficient();
        double endLocationCoe = f.getLocationMap().get(flight.getEndLocation().toUpperCase()).getDemandCoefficient();
        double priceAdjust = 4 * (endLocationCoe - startLocationCoe);
        double distance = flight.getDistance();
        double finalPrice = y * distance / 100 * (basePrice + priceAdjust);
        return finalPrice;
    }

    public static int findFlight(List<Flight> flightList,int id){
        for(int i = 0;i < flightList.size();i++){
            if(flightList.get(i).getId() == id) return i;
        }
        return -1;
    }


    public static int calcFlightDistance(String startName, String endName, FlightScheduler f){
        Location startLocation = f.getLocationMap().get(startName.toUpperCase());
        Location endLocation = f.getLocationMap().get(endName.toUpperCase());
        double startLat = startLocation.getLatitude(),startLon = startLocation.getLongitude();
        double endLat = endLocation.getLatitude(),endLon = endLocation.getLongitude();
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLon - startLon);
        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);
        double a = formula(dLat) + Math.cos(startLat) * Math.cos(endLat) * formula(dLon);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return (int)Math.ceil(radius * c);
    }

    public static double formula(double value){

        return Math.pow(Math.sin(value / 2.0),2.0);
    }

    public static int calcFlightDuration(double distance){

        return (int)(distance / speed * 60);
    }

    public static String calcFlightArrivalDayTime(int duration,String departureTime){
        String arrivalDayTime = "";
        try{
            Date departDate = CommonProcess.simpleLongDayTimeFormatter.parse(departureTime);
            Date arrivalDate = new Date(departDate.getTime() + duration * CommonProcess.minToMill);
            arrivalDayTime = CommonProcess.simpleLongDayTimeFormatter.format(arrivalDate);
        } catch(ParseException e){
            e.printStackTrace();
        }
        return arrivalDayTime;
    }

    public static void checkConflict(String dayTimeString,List<Flight> flightCheckList,boolean isArrive,List<Flight>conflictList){
        SimpleDateFormat tempFormatter= new SimpleDateFormat("WW EEEE HH:mm");
        try{
            Date dayTime1 = tempFormatter.parse("2 " + dayTimeString );
            for(Flight checkFlight : flightCheckList){
                Date dayTime2 = tempFormatter.parse("2 " + (isArrive ? checkFlight.getArriveDayTime(): checkFlight.getDepartureDayTime()));
                if(Math.abs(dayTime2.getTime()- dayTime1.getTime()) <= CommonProcess.hourToMill){
                    checkFlight.setSortDayTime(dayTimeString);
                    conflictList.add(checkFlight);
                }
            }
        } catch(ParseException e){
            e.printStackTrace();
        }
    }

    public static void checkFlightScheduleConflict(Flight flight, FlightScheduler f){
        Location startLocation= f.getLocationMap().get(flight.getStartLocation().toUpperCase());
        Location endLocation = f.getLocationMap().get(flight.getEndLocation().toUpperCase());
        List<Flight> conflictList = new ArrayList<>();
        checkConflict(flight.getDepartureDayTime(),startLocation.getDepartFlights(),false,conflictList);
        checkConflict(flight.getDepartureDayTime(),startLocation.getArriveFlights(),true,conflictList);
        checkConflict(flight.getArriveDayTime(),endLocation.getDepartFlights(),false,conflictList);
        checkConflict(flight.getArriveDayTime(),endLocation.getArriveFlights(),true,conflictList);
        if(conflictList.size() != 0){
            CommonProcess.sortFlightList(conflictList);
            Flight conflictFlight = conflictList.size() == 2 ? conflictList.get(1):conflictList.get(0);
            String conflictMsg = "Scheduling conflict! This flight clashes with Flight " + conflictList.get(0);
            if(findFlight(startLocation.getDepartFlights(),conflictFlight.getId()) != -1 ){
                conflictMsg = conflictMsg + " departing from " + startLocation.getName() + "on " + conflictFlight.getDepartureDayTime();
            }
            else if(findFlight(endLocation.getDepartFlights(),conflictFlight.getId()) != -1 ){
                conflictMsg = conflictMsg + " departing from " + endLocation.getName() + "on " + conflictFlight.getDepartureDayTime();
            }
            else if(findFlight(startLocation.getArriveFlights(),conflictFlight.getId()) != -1 ){
                conflictMsg = conflictMsg + " arriving at " + startLocation.getName() + " on " + conflictFlight.getArriveDayTime();
            }
            else{
                conflictMsg = conflictMsg + " arriving at " + endLocation.getName() + " on " + conflictFlight.getArriveDayTime();
            }
            throw new IllegalArgumentException (conflictMsg);
        }
    }

    public static void viewSingleFlight(int flightId, FlightScheduler f){
        Flight flight = f.getFlightMap().get(flightId);
        DecimalFormat distanceFormatter = new DecimalFormat("#,###");
        System.out.printf("%-14s%s\n","Departure:",flight.getDepartureDay() + " " + flight.getDepartureTime() + " " + flight.getStartLocation());
        System.out.printf("%-14s%s\n","Arrival:",flight.getArriveDay() + " " + flight.getArriveTime() + " " + flight.getEndLocation());
        System.out.printf("%-14s%skm\n","Distance:",distanceFormatter.format(flight.getDistance()));
        int h = flight.getDuration() / 60, m =  flight.getDuration() % 60;
        System.out.printf("%-14s%sh %sm\n","Duration:",h,m);
        System.out.printf("%-14s%s%.2f\n","Ticket Cost:","$",flight.getPrice());
        System.out.printf("%-14s%s\n","Passengers:",flight.getNumOfBook() + "/" + flight.getCapacity());
    }

    public static void processTravel(String[] args, FlightScheduler f){
        try{
            if(args.length < 3) throw new IllegalArgumentException("Usage: TRAVEL <from> <to> [cost/duration/stopovers/layover/flight_time]");
            String startLocation = args[1];
            String endLocation = args[2];
            if(!f.getLocationMap().containsKey(startLocation.toUpperCase())) throw new IllegalArgumentException("Starting location not found.");
            if(!f.getLocationMap().containsKey(endLocation.toUpperCase())) throw new IllegalArgumentException("Ending location not found.");
            String sorting = args.length > 3 ? args[3].toUpperCase() : "DURATION";
            if(!sorting.equals("COST") && !sorting.equals("DURATION") && !sorting.equals("STOPOVERS") &&!sorting.equals("LAYOVER") && !sorting.equals("FLIGHT_TIME")) throw new IllegalArgumentException("Invalid sorting property: must be either cost, duration, stopovers, layover, or flight_time.");
            if(args.length == 5 && !CommonProcess.isInteger(args[4]) || Integer.valueOf(args[4]) < 1) throw new IllegalArgumentException("The input nth is wrong");
            int nth = args.length == 5 ? Integer.valueOf(args[4]) - 1 : 0 ;
            List<Route> routes = searchRoute(startLocation,endLocation, f);
            if(routes.size() == 0) throw new IllegalArgumentException("Sorry, no flights with 3 or less stopovers are available from " + startLocation + " to " + endLocation);
            sortRoutes(routes,sorting);
            if(nth >= routes.size()){
                nth = routes.size() -1;
            }
            Route result =  routes.get(nth);
            printRouteInfo(result);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage() + "\n");
        }
    }

    public static  List<Route> searchRoute(String start, String end, FlightScheduler f){
        List<Route> routes = new ArrayList<>();
        String startLocation = start.toUpperCase();
        String endLocation =  end.toUpperCase();

        Queue<Route> routeQueue = new LinkedList<>();
        for(Flight flight: f.getLocationMap().get(startLocation).getDepartFlights()){
            Route route =  new Route(new ArrayList<>(),0,flight.getPrice(),flight.getDuration(),0,flight.getDuration());
            route.getLeg().add(flight);
            routeQueue.offer(route);
        }

        while(!routeQueue.isEmpty()){
            Route currRoute = routeQueue.poll();
            Flight currFlight = currRoute.getLeg().get(currRoute.getLeg().size() - 1);
            if(currRoute.getLeg().size() > 5){
                continue;
            }
            if(currFlight.getEndLocation().equalsIgnoreCase(endLocation)){
                currRoute.setStopNum(currRoute.getStopNum() - 1);
                routes.add(currRoute);
                continue;
            }
            Location  arrivalLocation  = f.getLocationMap().get(currFlight.getEndLocation().toUpperCase());
            long arrivalFlightDayTime = CommonProcess.dayTimeToMilliSec("2 " + currFlight.getArriveDayTime());

            for(Flight departFlight:arrivalLocation.getDepartFlights()){
                long departFlightDayTime = CommonProcess.dayTimeToMilliSec("2 " + departFlight.getDepartureDayTime());
                if(arrivalFlightDayTime < departFlightDayTime){
                    List<Flight> newLegs = new ArrayList<>(currRoute.getLeg());
                    newLegs.add(departFlight);
                    double newCost = currRoute.getCost() + departFlight.getPrice();
                    int newStopNum = currRoute.getStopNum() + 1;
                    long newLayover = currRoute.getLayover() + departFlightDayTime - arrivalFlightDayTime;
                    int newDuration = currRoute.getDuration() + departFlight.getDuration() + (int)(newLayover / CommonProcess.minToMill);
                    long newFlightTime = newDuration * CommonProcess.minToMill - newLayover;
                    Route tempRoute = new Route(newLegs,newStopNum,newCost,newDuration,newLayover,newFlightTime);
                    routeQueue.offer(tempRoute);
                }
            }
        }

        return routes;
    }

    public static void sortRoutes(List<Route> routes, String sort){
        switch (sort){
            case "COST":
                Collections.sort(routes,(r1,r2)->{
                    if(r1.getCost() != r2.getCost()) return (int)(r1.getCost() - r2.getCost());
                    return r1.getDuration() - r2.getDuration();
                });
                break;
            case "DURATION":
                Collections.sort(routes,(r1,r2)->{
                    if(r1.getDuration() != r2.getDuration()) return r1.getDuration() - r2.getDuration();
                    return (int)(r1.getCost() - r2.getCost());
                });
                break;
            case "STOPOVERS":
                Collections.sort(routes,(r1,r2)-> {
                    if(r1.getStopNum() != r2.getStopNum()) return r1.getStopNum() - r2.getStopNum();
                    if(r1.getDuration() != r2.getDuration()) return r1.getDuration() - r2.getDuration();
                    return (int) (r1.getCost() - r2.getCost());
                });
                break;
            case "LAYOVER":
                Collections.sort(routes,(r1,r2)-> {
                    if(r1.getLayover() != r2.getLayover()) return (int)(r1.getLayover() - r2.getLayover());
                    if(r1.getDuration() != r2.getDuration()) return r1.getDuration() - r2.getDuration();
                    return (int) (r1.getCost() - r2.getCost());
                });
                break;
            case "FLIGHT_TIME":
                Collections.sort(routes,(r1,r2)-> {
                    if(r1.getFlightTime() != r2.getFlightTime()) return (int)(r1.getFlightTime() - r2.getFlightTime());
                    if(r1.getDuration() != r2.getDuration()) return r1.getDuration() - r2.getDuration();
                    return (int) (r1.getCost() - r2.getCost());
                });
                break;
        }
    }

    public static void printRouteInfo(Route route){
        System.out.printf("%-14s%s\n","Legs",route.getLeg().size());
        System.out.printf("%-14s%s\n","Total Duration", CommonProcess.minToHourMin(route.getDuration()));
        System.out.printf("%-14s%s%.2f\n","Total Cost: ","$",route.getCost());
        System.out.println("-------------------------------------------------------------");
        System.out.printf("%-5s%-12s%-12s%-12s%s\n","ID","Cost","Departure","Arrival","Source --> Destination");
        System.out.println("-------------------------------------------------------------");
        for(Flight leg: route.getLeg()){
            System.out.printf("%-5s%-12.2f%-12s%-12s%s\n",leg.getId() + " ", leg.getPrice(),leg.getDepartureDay()+" "+leg.getDepartureTime(),leg.getArriveDay() +
                    " " + leg.getArriveDayTime(),leg.getStartLocation() + " --> " + leg.getEndLocation());
        }
    }
}