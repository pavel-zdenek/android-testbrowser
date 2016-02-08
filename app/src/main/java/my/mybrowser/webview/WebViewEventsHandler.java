/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser.webview;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

public interface WebViewEventsHandler
{
  void onUrlStartedLoading(@NonNull Uri url);
  @Nullable
  WebResourceResponse maybeResponseForRequest(@NonNull WebResourceRequest request);
}
