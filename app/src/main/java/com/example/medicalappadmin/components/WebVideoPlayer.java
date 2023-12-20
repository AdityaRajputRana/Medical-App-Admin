package com.example.medicalappadmin.components;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.example.medicalappadmin.R;

public class WebVideoPlayer {

    private Dialog webViewPopupDialog;
    private WebView webView;

    public WebVideoPlayer(Context context) {
        webViewPopupDialog = new Dialog(context);
        webViewPopupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        webViewPopupDialog.setContentView(R.layout.web_popup_layout);
        webViewPopupDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );

        webView = webViewPopupDialog.findViewById(R.id.webView);
        ImageButton btnClose = webViewPopupDialog.findViewById(R.id.btnClose);

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopup();
            }
        });
    }

    public void playVideo(String videoUrl) {
        webView.loadUrl(videoUrl);
        webViewPopupDialog.show();
    }

    public void dismissPopup() {
        if (webViewPopupDialog != null && webViewPopupDialog.isShowing()) {
            webView.loadUrl("about:blank");
            webViewPopupDialog.dismiss();
        }
    }
}
