package com.example.riccardo.lomux;
import android.net.Uri;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Franc on 07/10/2017.
 */

public class Link implements Serializable {
    private String value;
    private String href;

    public Link(String text, String href)
    {
        value = text;
        this.href = href;
    }

    public String getText(){
        return value;
    }

    public String getUri(){
        return href;
    }


}
