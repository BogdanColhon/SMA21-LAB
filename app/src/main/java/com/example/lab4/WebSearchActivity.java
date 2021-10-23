package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.net.URL;

public class WebSearchActivity extends AppCompatActivity implements View.OnClickListener {
    public String link;
    private Button bBackground;
    private Button bForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_search);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        bBackground = findViewById(R.id.bLoadBackground);
        bForeground = findViewById(R.id.bLoadForeground);


        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new MyCustomWebViewClient());
        String url = "https://www.google.ro/imghp?hl=ro&authuser=0&ogbl";
        myWebView.loadUrl(url);
    }

    private class MyCustomWebViewClient extends WebViewClient {
    @Override

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(Uri.parse(url).toString().contains("tbm=isch")) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        return true;
    }
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData a = clipboard.getPrimaryClip();
        ClipData.Item item = a.getItemAt(0);
        String url = item.getText().toString();
        if (!url.contains("https://images.app.goo.gl"))
            Toast.makeText(this, "URL not valid.Try another.", Toast.LENGTH_SHORT).show();
        else {
            switch (v.getId()) {
                case R.id.bLoadBackground:
                    Intent intent = new Intent(this, ImageIntentService.class);
                    intent.putExtra("EXTRA_URL", url);
                    System.out.println("ai");
                    if (intent.resolveActivity(getPackageManager()) != null)
                        startService(intent);
                    break;
                case R.id.bLoadForeground:
                    Intent intent2 = new Intent(this, ForegroundImageService.class);
                    intent2.putExtra("EXTRA_URL", url);
                    intent2.setAction(ForegroundImageService.STARTFOREGROUND_ACTION);
                    System.out.println("bi");
                    if (intent2.resolveActivity(getPackageManager()) != null)
                        startService(intent2);
                    break;
            }
        }
    }

}

