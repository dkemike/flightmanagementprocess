package Assignment1Update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommonProcess {
    public static final long minToMill = 60000;
    public static final long hourToMill = 3600000;
    public static final String daytimePattern = "EEEE HH:mm";
    public static DateTimeFormatter longDayTimeFormatter = DateTimeFormatter.ofPattern(daytimePattern);
    public static SimpleDateFormat simpleLongDayTimeFormatter = new SimpleDateFormat(daytimePattern);
    public static Map<String,Integer> dayToNumber;

    static{
        dayToNumber = new HashMap<>();
        dayToNumber.put("Mon",1);
        dayToNumber.put("Tue",2);
        dayToNumber.put("Wed",3);
        dayToNumber.put("Thu",4);
        dayToNumber.put("Fri",5);
        dayToNumber.put("Sat",6);
        dayToNumber.put("Sun",7);
    }


    public static String minToHourMin(int min){
        int h = min / 60;
        int m = min % 60;
        return h + "h " + m + "m";
    }

    public static boolean isNumeric(String s){

        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static boolean isInteger(String s){

        return s != null && s.matches("[0-9]+");
    }

    public static long dayTimeToMilliSec(String dayTimeStr){
        long millisec = 0;
        SimpleDateFormat tempFormatter = new SimpleDateFormat("WW EEEE HH:mm") ;
        try{
            millisec = tempFormatter.parse(dayTimeStr).getTime();

        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return  millisec;
    }

    public static void sortFlightList(List<Flight> flightList){
        Collections.sort(flightList,(f1,f2) -> {
            String day1 = f1.getSortDayTime().substring(0,3),day2 = f2.getSortDayTime().substring(0,3);
            String time1 = f1.getSortDayTime().split(" ")[1],time2 = f2.getSortDayTime().split(" ")[1];

            if(day1.equals(day2)){
                SimpleDateFormat tempFormatter = new SimpleDateFormat("HH:mm");
                try{
                    Date d1 = tempFormatter.parse(time1);
                    Date d2 = tempFormatter.parse(time2);
                    return (int)(d1.getTime() - d2.getTime());
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }

            return dayToNumber.get(day1) - dayToNumber.get(day2);
        });
    }

}