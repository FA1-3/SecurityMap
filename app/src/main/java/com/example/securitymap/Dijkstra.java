package com.example.securitymap;

import java.util.ArrayList;
//https://www.baeldung.com/java-dijkstra

public class Dijkstra {
    public int minimum(ArrayList<Node> nodes, ArrayList<Integer> unsettled){
        double minimum = nodes.get(unsettled.get(0)).d;
        int index=0;
        for (int i=1; i<unsettled.size(); i++) {
            if(nodes.get(unsettled.get(i)).d<minimum){
                minimum=nodes.get(unsettled.get(i)).d;
                index=i;
            }
        }
        return index;
    }

    public ArrayList<Integer> calculatePath(ArrayList<Node> nodes, int start, int end){
        ArrayList<Integer> settled = new ArrayList<Integer>();
        ArrayList<Integer> unsettled = new ArrayList<Integer>();
        ArrayList<Integer> shortest = new ArrayList<Integer>();
        for (Node var:nodes) {
            var.d = 100000000;
            var.rank=0;
        }
        nodes.get(start).d=0;
        unsettled.add(start);
        while(!unsettled.isEmpty()){
            int eval = unsettled.get(minimum(nodes, unsettled));
            unsettled.remove(minimum(nodes, unsettled));
            settled.add(eval);
            for (int i=0; i<nodes.get(eval).neighbour.size(); i++) {
                if((!settled.contains(nodes.get(eval).neighbour.get(i))) &&
                        (nodes.get(eval).d + nodes.get(eval).distance.get(i) < nodes.get(nodes.get(eval).neighbour.get(i)).d) &&
                        (nodes.get(eval).d + nodes.get(eval).distance.get(i) < nodes.get(end).d)){
                    nodes.get(nodes.get(eval).neighbour.get(i)).d = nodes.get(eval).d + nodes.get(eval).distance.get(i);
                    nodes.get(nodes.get(eval).neighbour.get(i)).last = eval;
                    nodes.get(nodes.get(eval).neighbour.get(i)).rank = nodes.get(eval).rank + 1;
                    if(!unsettled.contains(nodes.get(eval).neighbour.get(i)))
                        unsettled.add(nodes.get(eval).neighbour.get(i));
                }
            }
        }
        shortest.add(end);
        for(int i=1; i<=nodes.get(end).rank; i++){
            shortest.add(nodes.get(shortest.get(i-1)).last);
        }
        shortest = reverse(shortest);
        return shortest;
    }

    private ArrayList<Integer> reverse(ArrayList<Integer> array1) {
        ArrayList<Integer> array2 = new ArrayList<Integer>();
        for (int i=array1.size()-1; i>=0; i--){
            array2.add(array1.get(i));
        }
        return array2;
    }

}
