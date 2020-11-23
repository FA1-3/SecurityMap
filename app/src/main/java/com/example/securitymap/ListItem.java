package com.example.securitymap;

public class ListItem {

    private String id;
    private String name;
    private String building;
    private int floor;
    private int num;
    private int node;


    public ListItem(String id, String name, String building, int floor, int num, int node) {
        this.id = id; //type, either building, place, or maybe eventually others
        this.name = name; // full name of building or destination
        this.building = building; //three letter indicator
        this.floor = floor; //will be -1 if not-applicable (like for a building)
        this.num = num; // every listItem will be numbered to be easily documented
        this.node = node; //will be -1 if not-applicable (like for a building)
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }



} // end of the class ListItem

