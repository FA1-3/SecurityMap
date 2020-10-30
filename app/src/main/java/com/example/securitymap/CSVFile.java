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
    Node node;
    Building building;
    Floor floor;
    public ArrayList<Node> read(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                node = new Node();
                node.neighbour = new ArrayList<Integer>();
                node.distance = new ArrayList<Double>();
                String[] row = csvLine.split(",");
                int index = Integer.parseInt(row[0]);
                switch(index){
                    case -1:
                        building = Building.valueOf(row[1]);
                        break;
                    case -2:
                        floor = Floor.valueOf(row[1]);
                        break;
                    default:
                        node.n=index;
                        node.building=building;
                        node.floor=floor;
                        node.att = new Attribute[2];
                        node.att[0] = Attribute.NULL;
                        node.att[1] = Attribute.NULL;
                        if (row[1] != null) {
                            for(int i=0; i<row[1].length();i++) {
                                switch (row[1].charAt(i)){
                                    case 'B':
                                        node.att[0] = Attribute.BATHROOM;
                                        break;
                                    case 'L':
                                        node.att[0] = Attribute.ELEVATOR;
                                        break;
                                    case 'S':
                                        node.att[0] = Attribute.STAIR;
                                        break;
                                    case 'X':
                                        node.att[0] = Attribute.EXIT;
                                        break;
                                }

                            }
                        }

                        int m=2;
                        while(m<row.length){
                            node.neighbour.add(Integer.parseInt(row[m])-1);
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
        for (Node node: nodes) {
           Log.v("tag", node.toString());
        }
        return nodes;
    }
}

