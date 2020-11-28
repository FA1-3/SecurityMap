package com.example.securitymap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;


public class CSVFile {
    InputStream inputStream;

    static Hashtable<Integer, Node> nodes = new Hashtable<>();
    static Hashtable<Build, Building> buildings = new Hashtable<>();
    Node node;
    Building building;
    Floor floor;
    public void read(){
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
                        if(!buildings.containsKey(building.name)&&building.name!=Build.OUT) {
                            building.lat1 = Float.parseFloat(row[2]);
                            building.lng1 = Float.parseFloat(row[3]);
                            building.lat2 = Float.parseFloat(row[4]);
                            building.lng2 = Float.parseFloat(row[5]);
                            building.dist = Double.parseDouble(row[6]);
                            building.center = Integer.parseInt(row[7]);
                            building.floors = new ArrayList<Floor>();
                            buildings.put(building.name, building);
                        }
                        break;
                    case -2:
                        floor = new Floor();
                        floor.num = Integer.parseInt(row[1]);
                        floor.name = row[2];
                        floor.width = Double.parseDouble(row[3]);
                        floor.height = Double.parseDouble(row[4]);
                        floor.ox = Double.parseDouble(row[5]);
                        floor.oy = Double.parseDouble(row[6]);
                        buildings.get(building.name).floors.add(floor);
                        break;
                    default:
                        node = new Node();
                        node.neighbour = new ArrayList<>();
                        node.distance = new ArrayList<>();

                        node.n=index;
                        node.building=building.name;
                        if (building.name!=Build.OUT)
                            node.floor=floor.num;
                        else
                            node.floor = -5;
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
                                    case 'G':
                                        node.att[i] = Attribute.BUILDING;
                                }
                            }
                        }
                        int m=4;
                        while(m<row.length){
                            node.neighbour.add(Integer.parseInt(row[m]));
                            node.distance.add(Double.parseDouble(row[m+1]));
                            m+=2;
                        }
                        nodes.put(index, node);
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
    }

    public static Hashtable<Build, Building> getBuildings() {
        return buildings;
    }

    public static Hashtable<Integer, Node> getNodes() {
        return nodes;
    }
}

