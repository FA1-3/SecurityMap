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
    String building;
    String floor;
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
                        building = row[1];
                        break;
                    case -2:
                        floor = row[1];
                        break;
                    default:
                        node.n=index-1;
                        node.building=building;
                        node.floor=floor;
                        switch(row[1]) {
                            case "Exit":
                                node.exit=true;
                                break;
                            case "Stair":
                                node.stair=true;
                                break;
                            case "Elevator":
                                node.elevator=true;
                                break;
                            case "BathM":
                                node.bath='M';
                                break;
                            case "BathF":
                                node.bath='F';
                                break;
                            default:
                                if(row[1]==null)
                                    break;
                                if(row[1].contains("Tunnel")) {
                                    node.tunnel=row[1].substring(6);
                                    break;
                                }
                                break;
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

