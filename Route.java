package Assignment1Update;

import java.util.*;


public class Route {

    private int stopNum;
    private List<Flight> leg;
    private int duration;
    private double cost;
    private long flightTime;
    private long layover;

    public Route(List<Flight>leg, int stopNum, double cost, int duration, long layover, long flightTime){
        this.leg = leg;
        this.stopNum = stopNum;
        this.cost = cost;
        this.duration = duration;
        this.layover = layover;
        this.flightTime = flightTime;
    }

    public Route(){
        leg = new ArrayList<>();
        stopNum = 0;
        cost = 0;
        duration = 0;
        layover = 0;
        flightTime = 0;
    }

    public int getStopNum() {

        return stopNum;
    }

    public void setStopNum(int stopNum) {

        this.stopNum = stopNum;
    }

    public List<Flight> getLeg(){

        return leg;
    }

    public int getDuration() {

        return duration;
    }

    public double getCost() {

        return cost;
    }

    public long getFlightTime() {

        return flightTime;
    }

    public long getLayover() {

        return layover;
    }

    public String toString(){
        return "FlightRoute{" + "stopNum=" + stopNum + ",cost=" + cost +",duration=" + duration + ",layover=" + layover + ",flightTime=" + flightTime +
                ",flightTime=" + flightTime + "}";
    }
}
