package com.example.securitymap;

import java.util.ArrayList;

public class Node {
    public int n;
    public int floor;
    public Build building;
    public ArrayList<Integer> neighbour;
    public ArrayList<Double> distance;

    public double x;
    public double y;
    public Attribute[] att;

    public double d;
    public int last;
    public int rank;
}
