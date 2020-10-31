package com.example.securitymap;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class CSVFile {
    InputStream inputStream;

    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Build> build_names = new ArrayList<Build>();
    ArrayList<Building> buildings = new ArrayList<Building>();
    Node node;
    Building building;
    int floor;
    public ArrayList<Node> read(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                int index = Integer.parseInt(row[0]);
                switch(index){
                    case -1:
                        building = new Building();
                        building.name = Build.valueOf(row[1]);
                        if(!build_names.contains(building.name)) {
                            building.lat1 = Float.parseFloat(row[2]);
                            building.lng1 = Float.parseFloat(row[3]);
                            building.lat2 = Float.parseFloat(row[4]);
                            building.lng2 = Float.parseFloat(row[5]);
                            building.dist = Double.parseDouble(row[6]);
                            build_names.add(building.name);
                            buildings.add(building);
                        }
                        break;
                    case -2:
                        floor = Integer.parseInt(row[1]);
                        break;
                    default:
                        node = new Node();
                        node.neighbour = new ArrayList<Integer>();
                        node.distance = new ArrayList<Double>();

                        node.n=index;
                        node.building=building.name;
                        node.floor=floor;
                        node.x=Double.parseDouble(row[1]);
                        node.y=Double.parseDouble(row[2]);

                        node.att = new Attribute[2];
                        node.att[0] = Attribute.NULL;
                        node.att[1] = Attribute.NULL;
                        if (row[3] != null) {
                            for(int i=0; i<row[3].length();i++) {
                                switch (row[3].charAt(i)){
                                    case 'B':
                                        node.att[i] = Attribute.BATHROOM;
                                        break;
                                    case 'L':
                                        node.att[i] = Attribute.ELEVATOR;
                                        break;
                                    case 'S':
                                        node.att[i] = Attribute.STAIR;
                                        break;
                                    case 'X':
                                        node.att[i] = Attribute.EXIT;
                                        break;
                                }
                            }
                        }
                        int m=4;
                        while(m<row.length){
                            node.neighbour.add(Integer.parseInt(row[m]));
                            node.distance.add(Double.parseDouble(row[m+1]));
                            m+=2;
                        }
                        nodes.add(node);
                        break;
                    }
                }

            }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return nodes;
    }
}

