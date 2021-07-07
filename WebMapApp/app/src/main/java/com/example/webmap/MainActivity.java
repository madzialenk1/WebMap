package com.example.webmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button butMap, butWeb, butSubmit;
    EditText webEdit, geocoderEdit;
    TextView infoText;
    public static final String LAT_VALUE = "lat";
    public static final String LONG_VALUE = "long";
    public static final String WEB_URL = "web";

    boolean isWebChosen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butMap = findViewById(R.id.mapbtn);
        butWeb = findViewById(R.id.webbtn);
        webEdit = findViewById(R.id.webEdit);
        geocoderEdit = findViewById(R.id.geocoderEdit);
        butSubmit = findViewById(R.id.submitBtn);
        infoText = findViewById(R.id.infoText);
        webEdit.setVisibility(View.GONE);
        geocoderEdit.setVisibility(View.GONE);
        infoText.setText(getResources().getString(R.string.empty_string));
        butSubmit.setVisibility(View.GONE);


        butMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webEdit.setVisibility(View.VISIBLE);
                geocoderEdit.setVisibility(View.VISIBLE);
                webEdit.setHint(getResources().getString(R.string.latitude));
                infoText.setText(getResources().getString(R.string.coordinates_alert));
                isWebChosen = false;
                webEdit.setText(getResources().getString(R.string.empty_string));
                geocoderEdit.setText(getResources().getString(R.string.empty_string));
                butSubmit.setVisibility(View.VISIBLE);
                webEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                geocoderEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);


            }
        });

        butWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webEdit.setHint(getResources().getString(R.string.website));
                webEdit.setVisibility(View.VISIBLE);
                geocoderEdit.setVisibility(View.GONE);
                infoText.setText(getResources().getString(R.string.page_name));
                isWebChosen = true;
                butSubmit.setVisibility(View.VISIBLE);
                webEdit.setInputType(InputType.TYPE_CLASS_TEXT);

            }
        });

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String editText1 = webEdit.getText().toString();
                String editText2 = geocoderEdit.getText().toString();
                System.out.println(isWebChosen);
                System.out.println(editText1);
                System.out.println(editText2);
                if (isWebChosen == false && !editText1.equals(getResources().getString(R.string.empty_string)) && !editText2.equals(getResources().getString(R.string.empty_string))) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    double lat = Double.parseDouble(webEdit.getText().toString());
                    double log = Double.parseDouble(geocoderEdit.getText().toString());
                    webEdit.setText(getResources().getString(R.string.empty_string));
                    geocoderEdit.setText(getResources().getString(R.string.empty_string));

                    intent.putExtra(LAT_VALUE, lat);
                    intent.putExtra(LONG_VALUE, log);
                    startActivity(intent);
                } else if (isWebChosen == false && (editText1.equals(getResources().getString(R.string.empty_string)) || editText2.equals(getResources().getString(R.string.empty_string)))) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.coordinate_spec), Toast.LENGTH_LONG).show();
                } else if (isWebChosen == true && editText1.equals(getResources().getString(R.string.empty_string))) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.url_spec), Toast.LENGTH_LONG).show();
                } else if ((isWebChosen == true && !editText1.equals(getResources().getString(R.string.empty_string)))) {

                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    String url = webEdit.getText().toString();
                    webEdit.setText(getResources().getString(R.string.empty_string));
                    intent.putExtra(WEB_URL, url);

                    startActivity(intent);

                }
            }
        });


    }
}