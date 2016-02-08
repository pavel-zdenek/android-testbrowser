/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import my.mybrowser.background.ResourceObjectsProvider;
import my.mybrowser.background.ScriptContainer;
import my.mybrowser.databinding.ActivityMainBinding;
import my.mybrowser.webview.MyWebChromeClient;
import my.mybrowser.webview.MyWebViewClient;
import my.mybrowser.webview.WebViewEventsHandler;

public class ActivityMain extends AppCompatActivity implements WebViewEventsHandler, ResourceObjectsProvider
{
  private ScriptContainer background;
  private ActivityMainBindingAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    background = new ScriptContainer(this);
    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    adapter = new ActivityMainBindingAdapter(binding);
    binding.setAdapter(adapter);
    configureWebView(binding.webView, "content", this);
  }

  // WebViewEventsHandler

  public void onUrlStartedLoading(Uri url)
  {
    adapter.setLoadedUrl(url);
  }

  public WebResourceResponse maybeResponseForRequest(WebResourceRequest request)
  {
    if (background.shouldBlock(request)) {
      return new WebResourceResponse(null, null, 403, "Cancelled", null, null);
    }
    return null;
  }

  // ResourceObjectsProvider

  public String getRawResource(int resourceId)
  {
    InputStream is = getResources().openRawResource(resourceId);
    InputStreamReader isr;
    try {
      isr = new InputStreamReader(is, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
    StringBuilder sb = new StringBuilder();
    BufferedReader br = new BufferedReader(isr);
    try {
      for(String line; (line = br.readLine()) != null; )
      {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return sb.toString();
  }

  public @NonNull
  WebView getNewWebView(@NonNull String webViewKind)
  {
    WebView webView = new WebView(getApplicationContext());
    configureWebView(webView, webViewKind, null);
    return webView;
  }

  @NonNull
  private void configureWebView(@NonNull WebView webView, @NonNull String webViewKind, @Nullable WebViewEventsHandler eventsHandler)
  {
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebChromeClient(new MyWebChromeClient(webViewKind));
    if (eventsHandler != null) {
      webView.setWebViewClient(new MyWebViewClient(eventsHandler));
    }
  }

}
