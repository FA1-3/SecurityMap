package com.example.securitymap;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
//https://www.baeldung.com/java-dijkstra

public class Dijkstra {
    static ArrayList<Integer> path;
    static ArrayList<Build> pathBuildings;
    static ArrayList<Integer> pathFloors;
    static int pathProgress;

    static int minimum(Hashtable<Integer, Node> nodes, ArrayList<Integer> unsettled){
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

    static ArrayList<Integer> reverse(ArrayList<Integer> array1) {
        ArrayList<Integer> array2 = new ArrayList<>();
        for (int i=array1.size()-1; i>=0; i--){
            array2.add(array1.get(i));
        }
        return array2;
    }

    static void calculatePath(Hashtable<Integer, Node> nodes, int start, int end, ArrayList<Attribute> att){ //nodes contient tous les points enregistres
        ArrayList<Integer> settled = new ArrayList<>();
        ArrayList<Integer> unsettled = new ArrayList<>();
        path = new ArrayList<>();
        Enumeration<Integer> keys = nodes.keys();
        int k;
        while(keys.hasMoreElements()) {
            k=keys.nextElement();
            Node node = nodes.get(k);
            node.d = Integer.MAX_VALUE; //Distance de chaque point à partir du point de départ est "infinie"
        }

        nodes.get(start).d=0;
        unsettled.add(start);   //seul point à considérer au début est le point de départ
        while(!unsettled.isEmpty()){
            int eval = unsettled.get(minimum(nodes, unsettled));    //point à considérer est le point le plus près du départ (avec le plus petit d)
            unsettled.remove(minimum(nodes, unsettled));    //lorsqu'un point a été considéré, on ne veut plus le revisiter donc on l'enlève de la liste unsettled
            settled.add(eval);                              //puis on le met dans settled
            Node evaluation = nodes.get(eval);
            for (int i=0; i<evaluation.neighbour.size(); i++) {  //on passe au travers de tous les voisins du point considéré
                Node neighbour = nodes.get(evaluation.neighbour.get(i));
                if((!att.contains(evaluation.att[0])&&!att.contains(evaluation.att[1]))||(!att.contains(neighbour.att[0])&&!att.contains(neighbour.att[1])))
                if (neighbour.att[0] != Attribute.BUILDING && neighbour.att[1] != Attribute.BUILDING || neighbour.n==end) {
                    if(MapsActivity.warmest){
                        if (!settled.contains(neighbour.n)) {
                            if (evaluation.building == Build.OUT || neighbour.building == Build.OUT && (evaluation.d + evaluation.distance.get(i) < neighbour.d) &&(evaluation.d + evaluation.distance.get(i) < nodes.get(end).d)){
                                if ((att.contains(neighbour.att[0]) || att.contains(neighbour.att[1])) && (att.contains(evaluation.att[0]) || att.contains(evaluation.att[1]))) {
                                    neighbour.d = Integer.MAX_VALUE;
                                } else {
                                    neighbour.d = evaluation.d + evaluation.distance.get(i);//changer la distance du voisin pour la nouvelle dist
                                    neighbour.last = eval; //le point précédent au voisin dans le trajet le plus court est le point considéré
                                    if (!unsettled.contains(neighbour.n)) //si le voisin n'est pas déjà dans unsettled, ajoute le
                                        unsettled.add(neighbour.n);
                                }
                            } else if (evaluation.building != Build.OUT && neighbour.building != Build.OUT && (evaluation.d < neighbour.d) && (evaluation.d < nodes.get(end).d)){
                                if ((att.contains(neighbour.att[0]) || att.contains(neighbour.att[1])) && (att.contains(evaluation.att[0]) || att.contains(evaluation.att[1]))) {
                                    neighbour.d = Integer.MAX_VALUE;
                                } else {
                                    neighbour.d = evaluation.d;//changer la distance du voisin pour la nouvelle dist
                                    neighbour.last = eval; //le point précédent au voisin dans le trajet le plus court est le point considéré
                                    if (!unsettled.contains(neighbour.n)) //si le voisin n'est pas déjà dans unsettled, ajoute le
                                        unsettled.add(neighbour.n);
                                }
                            }
                        }
                    } else {
                        if ((!settled.contains(neighbour.n)) &&  //si point n'a pas été déjà visité
                                (evaluation.d + evaluation.distance.get(i) < neighbour.d) && //si distance (d) du voisin est plus grande
                                (evaluation.d + evaluation.distance.get(i) < nodes.get(end).d)) { //et si cette dernière distance est plus petite que la plus petite
                            //distance calculée à date au point d'arrivé
                            if ((att.contains(neighbour.att[0]) || att.contains(neighbour.att[1])) && (att.contains(evaluation.att[0]) || att.contains(evaluation.att[1]))) {
                                neighbour.d = Integer.MAX_VALUE;
                            } else {
                                neighbour.d = evaluation.d + evaluation.distance.get(i);//changer la distance du voisin pour la nouvelle dist
                                neighbour.last = eval; //le point précédent au voisin dans le trajet le plus court est le point considéré
                                if (!unsettled.contains(neighbour.n)) //si le voisin n'est pas déjà dans unsettled, ajoute le
                                    unsettled.add(neighbour.n);
                            }
                        }
                    }
                }
            }
        }
        path.add(end);
        int i=0;
        while (nodes.get(path.get(i)).last!=start){ //pour déterminer le trajet, en commenceant avec le point d'arrivé, prendre le dernier point qui donne le trajet le plus court
                                                    //au point
            path.add(nodes.get(path.get(i)).last);
            i++;
        }
        path.add(start);
        path = reverse(path);

        pathBuildings = new ArrayList<>();
        pathFloors = new ArrayList<>();
        Node yeet;
        for (int j=0; j<path.size(); j++) {
            yeet = nodes.get(path.get(j));
            if(pathBuildings.isEmpty()||pathBuildings.get(pathBuildings.size()-1)!=yeet.building||pathFloors.get(pathFloors.size()-1)!=yeet.floor) {
                pathBuildings.add(yeet.building);
                pathFloors.add(yeet.floor);
            }
        }
    }
}
