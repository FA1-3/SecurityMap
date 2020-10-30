package com.example.securitymap;

import java.util.ArrayList;

public class Node {
    public int n;
    public Floor floor;
    public Building building;
    public ArrayList<Integer> neighbour;
    public ArrayList<Double> distance;

    public Attribute[] att;

    public double d;
    public int last;
    public int rank;
}
