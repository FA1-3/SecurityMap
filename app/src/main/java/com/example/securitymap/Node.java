package com.example.securitymap;

import java.util.ArrayList;

public class Node {
    public int n;
    public String floor;
    public String building; //may change to class building
    public ArrayList<Integer> neighbour; // 2, 5, 6
    public ArrayList<Double> distance;//  3.1, 10.34, 12.1
    public char bath;
    public boolean exit;
    public boolean stair;
    public boolean elevator;
    public String tunnel;
    public double d;
    public int last;
    public int rank;

    public String toString(){
       String string = n+", "+floor+", "+building;
       if(exit)
           string=string+", exit";
        if(stair)
            string=string+", stair";
        if(elevator)
            string=string+", elevator";
        if(bath=='M')
            string=string+", bath M";
        if(bath=='F')
            string=string+", bath F";
        string = string+tunnel;
        string = string+"\n";
        for (int neighbour: neighbour) {
            string = string+neighbour+" ";
        }
        string = string+"\n";
        for (double neighbour: distance) {
            string = string+neighbour+" ";
        }
        return string;
    }
}
