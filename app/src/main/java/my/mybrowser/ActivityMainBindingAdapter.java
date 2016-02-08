/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser;

import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.TextView;

import my.mybrowser.databinding.ActivityMainBinding;

public class ActivityMainBindingAdapter
{
  public final ObservableField<String> urlTextValue = new ObservableField<>();
  private final WebView closurableWebView;

  public ActivityMainBindingAdapter(@NonNull ActivityMainBinding binding)
  {
    this.closurableWebView = binding.webView;
    binding.urlTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
      {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
          String addr = textView.getText().toString();
          if (!addr.startsWith("http://")) {
            addr = "http://" + addr;
          }
          closurableWebView.loadUrl(addr);
          return false;
        }
        return true;
      }
    });
  }

  public void setLoadedUrl(@NonNull Uri url)
  {
    urlTextValue.set(url.getHost());
  }
}
