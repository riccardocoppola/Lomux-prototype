package com.example.riccardo.lomux;
import android.net.Uri;

/**
 * Created by Franc on 07/10/2017.
 */

public class Link {
    private String value;
    private Uri href;

    public Link(String text, String href)
    {
        value = text;
        this.href = Uri.parse(href);
    }

    public String getText(){
        return value;
    }

    public Uri getUri(){
        return href;
    }


}
