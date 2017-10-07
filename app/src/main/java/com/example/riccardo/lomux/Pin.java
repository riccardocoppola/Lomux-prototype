package com.example.riccardo.lomux;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Riccardo on 10/08/2017.
 */


public class Pin implements Serializable {

    //mandatory attributes
    protected String id;
    protected PinType pintype;                  //discrimination between three different types of pin


    protected double lat;
    protected double lng;

   // protected LatLng position;                  //latitude and longitude of the pin, to place it on the map
    protected String name;                        //name of the pin (what appears when clicked)
                                                //if the pin is a VENUE pin, the name can be the same of subtitle
    protected String address;                   //address of the pin (name of street, road)


    protected String zipcode;                   //zipcode of the pin
    protected String city;                      //name of the city
                    //possibly define later a CITY as a class, to filter out all pins related to the same city based on it
                    //(and not on string comparisons) - this should also delete the need for a "country" field, since it
                    //would be part of the City class.
    protected String country;                   //name of the country

    //optional attributes for all classes of pin
    protected String info = null;                 //information to provide when the pin is clicked
    protected ArrayList<Itinerary> itineraries = new ArrayList<Itinerary>();    //(null) list of itineraries to which the Pin belongs
    protected Link source = null;   //information about where the pin has been taken
    //protected String source = null;
  //  protected Image image = null;                        //image to show in the box opened when the pin is clicked

    //attributes for different classes of pins
    protected String subtitle = null;           //mandatory for VENUE pins, name of the place, optional for STUDIO/WORK
    protected String artist_name = null;          //mandatory for STUDIO and WORK pins, name of the artist
                    //possibly define later an STUDIO as a class, to filter out all pins related to the same artist
    protected String song_title = null;           //mandatory for WORK pins, name of the song (or album)
    protected String song_lyrics = null;          //optional for WORK pins, lyrics of the song related to the place
    protected ArrayList<Link> mediaList = null;     // list of URIs to open Youtube or Spotify to reproduce media

  //  protected Marker marker = null;             //the pin is connected to the marker that is then shown in the map, when
                                                 //the marker is created from the application

    protected Boolean visible = false;

    private int image_reference = -1;        //only at the beginning in which images are loaded in res folders



    public Pin(String id, PinType pintype, double lat, double lng, String name, String subtitle, String address, String zipcode, String city, String country, String info, String sourceName, String source, String artist_name, String song_title, String song_lyrics) {
        this.id = id;
        this.pintype = pintype;
        this.lat = lat;
        this.lng = lng;
        //this.position = position;
        this.name = name;
        this.subtitle = subtitle;
        this.address = address;
        this.zipcode = zipcode;
        this.city = city;
        this.country = country;
        this.info = info;
        this.source = new Link(sourceName, source);
    //    this.image = image;
        this.subtitle = subtitle;
        this.artist_name = artist_name;
        this.song_title = song_title;
        this.song_lyrics = song_lyrics;
        this.visible = false;
       // marker = null;
    }

    public void setVisible() {
        visible = true;
    }

    public void setNotVisible() {
        visible = false;
    }

    public void addItinerary(Itinerary itinerary) {
        itineraries.add(itinerary);
    }

    public ArrayList<Itinerary> getItineraries() {
        return itineraries;
    }

    public String getId() {
        return id;
    }

  //  public LatLng getPosition() {
  //      return position;
 //   }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public PinType getPintype() {
        return pintype;
    }

    public String getName() {
        return name;
    }

   // public Marker getMarker() {
   //     return marker;
  //  }

    public String getInfo() {
        return info;
    }

    public Link getSource() {
        return source;
    }

  //  public Image getImage() {
 //       return image;
  //  }

    public String getSubtitle() {
        return subtitle;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getSong_title() {
        return song_title;
    }

    public String getSong_lyrics() {
        return song_lyrics;
    }


    public void setId(String id) {
        this.id = id;
    }

  //  public void setPosition(LatLng position) {
  //      this.position = position;
 //   }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPintype(PinType pintype) {
        this.pintype = pintype;
    }

    public void setName(String name) {
        this.name = name;
    }

  //  public void setMarker(Marker marker) {
  //      this.marker = marker;
 //   }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setSource(String sourceName, String href) {
        this.source = new Link(sourceName, href);
    }

  //  public void setImage(Image image) {
   //     this.image = image;
  //  }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public void setSong_lyrics(String song_lyrics) {
        this.song_lyrics = song_lyrics;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getImage_reference() {
        return image_reference;
    }

    public void setImage_reference(int image_reference) {
        this.image_reference = image_reference;
    }

    public void addMedia(String type, String URI) {
        if (mediaList == null)
            mediaList = new ArrayList<>();

        mediaList.add(new Link(type, URI));
    }

    public ArrayList<Link> getMediaList()
    {
        return mediaList;
    }
}
