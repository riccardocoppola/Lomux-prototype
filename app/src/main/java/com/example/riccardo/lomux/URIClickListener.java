package com.example.riccardo.lomux;

import android.view.View;
import android.content.Intent;
import android.net.Uri;


/**
 * Created by Riccardo on 07/10/2017.
 */

public class URIClickListener implements View.OnClickListener {


    String uri;

    URIClickListener(String uri) {

        this.uri = uri;

    }

    @Override
    public void onClick(View view) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(uri));
        view.getContext().startActivity(i);


    }
}
