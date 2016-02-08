/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser.webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient
{
  private WebViewEventsHandler eventsHandler;

  public MyWebViewClient(@NonNull WebViewEventsHandler eventsHandler)
  {
    super();
    this.eventsHandler = eventsHandler;
  }
  @Override
  public void onPageStarted(WebView view, String url, Bitmap favicon)
  {
    super.onPageStarted(view, url, favicon);
    Uri urlObj = Uri.parse(url);
    eventsHandler.onUrlStartedLoading(urlObj);
  }

  @Override
  public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
  {
    WebResourceResponse response = eventsHandler.maybeResponseForRequest(request);
    return (response == null) ? super.shouldInterceptRequest(view, request) : response;
  }

  @Override
  public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
  {
    super.onReceivedError(view, request, error);
    Log.e("CONTENT", "Webview error "+request.getUrl()+" error "+error);
  }

  @Override
  public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
  {
    super.onReceivedHttpError(view, request, errorResponse);
    Log.e("CONTENT", "Webview HTTP error " + request.getUrl() + " error " + errorResponse);
  }
}
