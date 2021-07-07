package com.example.webmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WebActivity extends AppCompatActivity {

    WebView web;
    ProgressBar progressBar;

    RelativeLayout relativeLayout;
    Button noInternetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        web = findViewById(R.id.webView);
        noInternetButton = (Button) findViewById(R.id.retry);
        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        checkConnection();

        noInternetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);


        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {


            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }



    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.confirm)).setNegativeButton(getResources().getString(R.string.no), null).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        }

    }

    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.back), Toast.LENGTH_LONG).show();


            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_previous:
                onBackPressed();
                break;
            case R.id.nav_next:
                if (web.canGoForward()) {
                    web.goForward();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void checkConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService((Context.CONNECTIVITY_SERVICE));
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.WEB_URL);
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        if (wifi.isConnected()) {
            web.setVisibility((View.VISIBLE));
            relativeLayout.setVisibility(View.GONE);
            web.loadUrl(url);


        } else if (mobileNetwork.isConnected()) {
            web.setVisibility((View.VISIBLE));
            relativeLayout.setVisibility(View.GONE);
            web.loadUrl(url);


        } else {
            web.setVisibility((View.GONE));
            relativeLayout.setVisibility(View.VISIBLE);


        }

    }
}