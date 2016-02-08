/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser.background;

import android.support.annotation.NonNull;
import android.webkit.JavascriptInterface;

public class ScriptInterface
{
  private JsObjectInterface objectInterface;

  public ScriptInterface(@NonNull JsObjectInterface determinationInterface)
  {
    this.objectInterface = determinationInterface;
  }
  @JavascriptInterface
  public void threadDeterminationCallback()
  {
    objectInterface.willBeCalledFromThread(Thread.currentThread());
  }
}
