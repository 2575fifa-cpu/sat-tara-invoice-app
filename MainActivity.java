package com.sattara.invoice;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebResourceRequest;
import android.content.Intent;
import android.net.Uri;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.content.Context;

public class MainActivity extends Activity {
  private WebView webView;
  private ValueCallback<Uri[]> filePathCallback;
  private static final int FILE_CHOOSER_REQUEST = 501;
  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    webView = new WebView(this);
    WebSettings s = webView.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setAllowFileAccess(true);
    s.setJavaScriptCanOpenWindowsAutomatically(true);
    webView.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
        if (filePathCallback != null) filePathCallback.onReceiveValue(null);
        filePathCallback = callback;
        Intent intent = params.createIntent();
        try { startActivityForResult(intent, FILE_CHOOSER_REQUEST); }
        catch (Exception e) { filePathCallback = null; return false; }
        return true;
      }
    });
    webView.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("file:///android_asset/") || url.startsWith("about:blank")) return false;
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); } catch (Exception ignored) {}
        return true;
      }
    });
    setContentView(webView);
    webView.loadUrl("file:///android_asset/index.html");
  }
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FILE_CHOOSER_REQUEST && filePathCallback != null) {
      Uri[] results = (resultCode == RESULT_OK && data != null) ? FileChooserParams.parseResult(resultCode, data) : null;
      filePathCallback.onReceiveValue(results);
      filePathCallback = null;
    }
  }
  @Override public void onBackPressed() {
    if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
  }
}
