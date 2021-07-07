package com.example.webmap;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    public final View myContentsView;
    private String name, vicinity;

    public MyInfoWindowAdapter() {

        myContentsView = LayoutInflater.from(MyApplication.getAppContext()).inflate(
                R.layout.mywindow, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = ((TextView) view
                .findViewById(R.id.infoTextView));
        if (!tvTitle.equals((""))) {
            tvTitle.setText(title);
        }

    }


    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, myContentsView);

        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, myContentsView);
        return myContentsView;
    }

}
