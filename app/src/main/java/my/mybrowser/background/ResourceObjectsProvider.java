/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser.background;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebView;

public interface ResourceObjectsProvider
{
  @Nullable
  String getRawResource(int resourceId);
  @NonNull
  WebView getNewWebView(@NonNull String webViewKind);
}
