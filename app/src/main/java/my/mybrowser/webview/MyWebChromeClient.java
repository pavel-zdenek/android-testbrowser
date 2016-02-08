/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser.webview;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

public class MyWebChromeClient extends WebChromeClient
{
  private String webViewKind;

  public MyWebChromeClient(String webViewKind)
  {
    super();
    this.webViewKind = webViewKind;
  }

  @Override
  public boolean onConsoleMessage(ConsoleMessage consoleMessage)
  {
    Log.d(webViewKind, "JS console "+consoleMessage.message());
    return true;
  }
}
