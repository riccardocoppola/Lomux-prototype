package com.example.riccardo.lomux;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Riccardo on 25/08/2017.
 */

public class Itinerary implements Serializable {


    private String ID;
    private String name;
    private String info;
    private Image image;
    private Image imagecircle;

    //TODO capire se aggiungere city o meno all'interno dell'itinerario, e gestione con getter setter e adeguamento csv

    private int image_reference;        //only at the beginning in which images are loaded in res folders
    private int image_circle_reference;    //only at the beginning in which images are loaded in res folders


    private LinkedHashMap<String, Pin> pins = new LinkedHashMap<String, Pin>();

    public Itinerary(String id, String name, String info, Image image) {
        this.ID = id;
        this.name = name;
        this.info = info;
        this.image = null;
        this.imagecircle = null;
        this.image_reference = -1;
        this.image_circle_reference = -1;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPin(Pin pin) {
        pins.put(pin.getId(), pin);
    }

    public Pin getPin(int id) {
        return pins.get(id);
    }

    public LinkedHashMap<String, Pin> getPins() {
        return pins;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImagecircle() {
        return imagecircle;
    }

    public void setImagecircle(Image imagecircle) {
        this.imagecircle = imagecircle;
    }

    public int getImage_reference() {
        return image_reference;
    }

    public void setImage_reference(int image_reference) {
        this.image_reference = image_reference;
    }

    public int getImage_circle_reference() {
        return image_circle_reference;
    }

    public void setImage_circle_reference(int image_circle_reference) {
        this.image_circle_reference = image_circle_reference;
    }
}
