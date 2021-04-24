package Assignment1Update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LocationProcess {

    public static void listSchedule(String[]args, FlightScheduler f){
        try{
            if(args.length != 2) throw new IllegalArgumentException("Invalid command. Type 'help' for a list of commands.");
            String locationName = args[1];

            if(!f.getLocationMap().containsKey(locationName.toUpperCase())) throw new IllegalArgumentException("This location does not exist in the system.");
            Location location = f.getLocationMap().get(locationName.toUpperCase());
            List<Flight> flightList = new ArrayList<>();
            Set <Integer> arrivalSet = new HashSet<>();

            for(Flight flight:location.getArriveFlights()){
                flight.setSortDayTime(flight.getArriveDayTime());
                arrivalSet.add(flight.getId());
            }

            for(Flight flight:location.getDepartFlights()){
                flight.setSortDayTime(flight.getDepartureDayTime());
            }

            flightList.addAll(location.getArriveFlights());
            flightList.addAll(location.getDepartFlights());
            CommonProcess.sortFlightList(flightList);

            System.out.println(locationName);
            System.out.println("-------------------------------------------------------");
            System.out.printf("%-5s%-12s%s\n","ID","Time","Departure/Arrival to/from Location");
            System.out.println("-------------------------------------------------------");

            for(Flight flight:flightList){
                String departArrivalStr = "";
                if(arrivalSet.contains(flight.getId())){
                    departArrivalStr = "Arrival from " + flight.getStartLocation();
                }else{
                    departArrivalStr = "Departure to " + flight.getEndLocation();
                }

                String day = flight.getSortDayTime().substring(0,3),time = flight.getSortDayTime().split(" ")[1];
                System.out.printf("%-5s%-12s%s\n",flight.getId() + " ",day + " " + time,departArrivalStr);
            }
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage()+"\n");
        }
    }

    public static void listDepartureAllocation(String[] args, FlightScheduler f){
        try{
            if(args.length != 2) throw new IllegalArgumentException("Invalid command. Type 'help' for a list of commands.");

            String locationName = args[1];
            if(!f.getLocationMap().containsKey(locationName.toUpperCase())) throw new IllegalArgumentException("This location does not exist in the system.");

            Location location = f.getLocationMap().get(locationName.toUpperCase());

            for(Flight flight:location.getDepartFlights()){
                flight.setSortDayTime(flight.getDepartureDayTime());
            }
            CommonProcess.sortFlightList(location.getDepartFlights());

            System.out.println(locationName);
            System.out.println("-------------------------------------------------------");
            System.out.printf("%-5s%-12s%s\n","ID","Time","Departure/Arrival to/from Location");
            System.out.println("-------------------------------------------------------");

            for(Flight flight:location.getDepartFlights()){
                System.out.printf("%-5s%-12s%s\n",flight.getId() + " ",flight.getDepartureDay() + " " + flight.getDepartureTime(),"Departure to" + flight.getEndLocation());
            }
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage()+"\n");
        }
    }

    public static void listArrivalAllocation(String[] args, FlightScheduler f){
        try{
            if(args.length != 2) throw new IllegalArgumentException("Invalid command. Type 'help' for a list of commands.");

            String locationName = args[1];
            if(!f.getLocationMap().containsKey(locationName.toUpperCase()))throw new IllegalArgumentException("This location does not exist in the system.");

            Location location = f.getLocationMap().get(locationName.toUpperCase());

            for(Flight flight:location.getArriveFlights()){
                flight.setSortDayTime(flight.getArriveDayTime());
            }
            CommonProcess.sortFlightList(location.getArriveFlights());

            System.out.println(locationName);
            System.out.println("-------------------------------------------------------");
            System.out.printf("%-5s%-12s%s\n", "ID","Time","Departure/Arrival to/from Location");
            System.out.println("-------------------------------------------------------");

            for(Flight flight:location.getArriveFlights()){
                System.out.printf("%-5s%-12s%s\n", flight.getId() + " ",flight.getArriveDay() + " " +  flight.getArriveTime(),"Arrival from" + flight.getStartLocation());
            }
        } catch(IllegalArgumentException e){
            System.out.println(e.getMessage()+"\n");
        }
    }

    public static boolean locationAdd(String[] args, FlightScheduler f, boolean addResult){
        try{
            if(args.length < 6) throw new IllegalArgumentException("Usage:LOCATION ADD <name> <lat> <long> <demand_coefficient>\nExample: LOCATION ADD Sydney -33.847927 150.651786 0.2");
            String name = args[2];
            if(f.getLocationMap().containsKey(name.toUpperCase())) throw new IllegalArgumentException("This location already exists.");
            if(!CommonProcess.isNumeric(args[3])|| Double.parseDouble(args[3]) < - 85 || Double.parseDouble(args[3]) > 85) throw new IllegalArgumentException("Invalid latitude. It must be a number of degrees between -85 and +85.");
            double latitude = Double.parseDouble(args[3]);
            if(!CommonProcess.isNumeric(args[4])|| Double.parseDouble(args[4]) < - 180 || Double.parseDouble(args[4]) > 180) throw new IllegalArgumentException("Invalid longitude. It must be a number of degrees between -180 and +180. ");
            double longitude = Double.parseDouble(args[4]);
            if(!CommonProcess.isNumeric(args[5])|| Double.parseDouble(args[5]) < -1 || Double.parseDouble(args[5]) > 1) throw new IllegalArgumentException("Invalid demand coefficient. It must be a number between -1 and +1.");
            double demandCoefficient = Double.parseDouble(args[5]);
            Location newLocation = new Location(name,latitude,longitude,demandCoefficient);
            f.getLocationList().add(newLocation);
            f.getLocationMap().put(newLocation.getNameToUpper(),newLocation);
            if(addResult) System.out.println("Successfully added location " + name + "."+ "\n");
        }
        catch (IllegalArgumentException e){
            if(addResult) System.out.println(e.getMessage()+"\n");
            return false;
        }
        return true;
    }

    public static void locationImport(String[]args, FlightScheduler f){
        int invalidCount = 0;
        int importCount = 0;

        try{
            if(args.length < 3) throw new FileNotFoundException("Error reading file.");
            File csvFile = new File(args[2]);
            Scanner fileReader = new Scanner(csvFile);

            while(fileReader.hasNextLine()){
                String row = fileReader.nextLine();
                String[] dataFields = row.split(",");

                if(dataFields.length < 4){
                    invalidCount++;
                }
                else{
                    String[] locationArgs = {"","",dataFields[0],dataFields[1],dataFields[2],dataFields[3]};
                    if(locationAdd(locationArgs, f,false)){
                        importCount++;
                    }
                    else{
                        invalidCount++;
                    }
                }
            }
            String importResult = "";
            importResult = "Imported " + importCount + (importCount > 1 ? " locations." : " location.");
            if(invalidCount != 0){
                importResult = importResult + "\n" + invalidCount + (invalidCount > 1 ? " lines were" : " line was") + " invalid.";
            }
            System.out.println(importResult + "\n");
            fileReader.close();
        }catch(FileNotFoundException e){
            System.out.println("Error reading file."+ "\n");
        }
    }

    public static void locationExport(String[] args, FlightScheduler f){
        try{
            if(args.length < 3) throw new FileNotFoundException("Error writing file.");
            FileWriter fileWriter = new FileWriter(args[2]);
            Collections.sort(f.getLocationList(),(l1, l2) -> l1.getName().compareTo(l2.getName()));
            for(Location location: f.getLocationList()){
                fileWriter.write(location.toString());
            }
            int numofLocation = f.getLocationList().size();
            System.out.println("Export " + numofLocation + " of" + (numofLocation > 1 ? " locations." : " location.")+ "\n");
            fileWriter.close();
        } catch(IOException e){
            System.out.println("Error writing file." + "\n");
        }
    }

    public static void listAllLocations(String[] args, FlightScheduler f){
        try{
            if(args.length > 1) throw new IllegalArgumentException("Invalid command. Type 'help' for a list of commands.");
            if(f.getLocationList().size() == 0){
                System.out.println("(None)"+ "\n");
                return;
            }
            Collections.sort(f.getLocationList(),(l1, l2) -> l1.getName().compareTo(l2.getName()));
            int idx = 0;
            int size = f.getLocationList().size();
            System.out.println("Locations (" + size + "):");
            for(Location location: f.getLocationList()){
                System.out.print(location.getName());
                System.out.print((++idx != size)? ",": "\n");
            }
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage()+"\n");
        }
    }

    public static void viewLocation(String name, FlightScheduler f){
        Location location = f.getLocationMap().get(name.toUpperCase());
        System.out.printf("%-14s%s\n", "Location:",location.getName());
        System.out.printf("%-14s%s\n", "Latitude:",location.getLatitude());
        System.out.printf("%-14s%s\n","Longitude:",location.getLongitude());
        if(location.getDemandCoefficient() >= 0){
            System.out.printf("%-14s%s\n","Demand:","+" + location.getDemandCoefficient());
        }
        else{
            System.out.printf("%-14s%s\n","Demand:",location.getDemandCoefficient());
        }
    }
}