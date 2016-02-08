/*
 * Copyright (c) 2016 Pavel Zdeněk
 */

package my.mybrowser.background;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import my.mybrowser.R;

public class ScriptContainer implements JsObjectInterface
{
  @NonNull private final WebView webView;
  @Nullable private final String jsApi;
  @NonNull private Thread jsCallbackThread;
  @Nullable private Thread jsEvalReturnThread;
  @NonNull private Handler handler = new Handler(Looper.getMainLooper());

  private static final String NATIVE_INTERFACE_TOKEN = "__NATIVE_INTERFACE_NAME__";
  private static final String NATIVE_INTERFACE_NAME = "nativeInterface";

  public ScriptContainer(ResourceObjectsProvider resourceProvider)
  {
    webView = resourceProvider.getNewWebView("background");
    webView.loadUrl("about:blank");
    webView.addJavascriptInterface(new ScriptInterface(this), NATIVE_INTERFACE_NAME);
    String maybeJsApi = resourceProvider.getRawResource(R.raw.test_api);
    if (maybeJsApi == null) {
      jsApi = null;
    } else {
      jsApi = maybeJsApi.replace(NATIVE_INTERFACE_TOKEN, NATIVE_INTERFACE_NAME);
      ValueCallback<String> retval = new ValueCallback<String>()
      {
        @Override
        public void onReceiveValue(String s)
        {
          Thread currentThread = Thread.currentThread();
          Log.v("BACKGROUND", "Retval "+s+" thread id "+currentThread.getId()+" name "+currentThread.getName() );
          jsEvalReturnThread = currentThread;
        }
      };
      webView.evaluateJavascript(jsApi, retval);
    }
  }

  // oh the joy of Java "closures"
  private class Finalizable<T>
  {
    public T value;
    public Finalizable(T init) { value = init; }
  }

  public boolean shouldBlock(@NonNull WebResourceRequest request)
  {
    final String url = request.getUrl().toString();
//    final String js = "shouldBlock('"+url+"')'";
    final String js = "false;"; // minimal test, does not work anyway
    final Lock lock = new ReentrantLock();
    final Condition cond = lock.newCondition();
    final Finalizable<Boolean> shouldBlock = new Finalizable(false);

    final ValueCallback<String> shouldBlockResult = new ValueCallback<String>()
    {
      @Override
      public void onReceiveValue(String s)
      {
        Thread t = Thread.currentThread();
        String tag = t.getName() + "/" + url;
        Log.v(tag, "R Will lock");
        lock.lock();
        Log.v(tag, "R Did lock, will set flag");
        shouldBlock.value = new Boolean(s);
        Log.v(tag, "R shouldBlock set to "+shouldBlock.value+", will signal");
        cond.signal();
        Log.v(tag, "R Did signal, will unlock");
        lock.unlock();
        Log.v(tag, "R Did unlock");
      }
    };
    // alternative working shortcut of evaluateJavascript
    final Runnable shouldBlockResultTask = new Runnable()
    {
      @Override
      public void run()
      {
        shouldBlockResult.onReceiveValue("false");
      }
    };

    final Runnable mainThreadTask = new Runnable()
    {
      @Override
      public void run()
      {
        Thread t = Thread.currentThread();
        String tag = t.getName() + "/" + url;
        Log.v(tag, "Will lock");
        lock.lock();
        Log.v(tag, "Did lock, will evaluate");
        webView.evaluateJavascript(js, shouldBlockResult);
//        handler.post(shouldBlockResultTask); // this works
        Log.v(tag, "Did evaluate, will unlock");
        lock.unlock();
        Log.v(tag, "Did unlock, will finish");
      }
    };
    Thread t = Thread.currentThread();
    String tag = t.getName() + "/" + url;
    Log.v(tag, "Will lock");
    lock.lock();
    Log.v(tag, "Did lock, will flag");
    shouldBlock.value = true;
    Log.v(tag, "Did flag, will post");
    handler.post(mainThreadTask);
    Log.v(tag, "Did post, will wait");
    try {
      cond.await();
      Log.v(tag, "Did wait, will unlock");
    } catch (InterruptedException e) {
      Log.v(tag, "Interrupted");
    } finally {
      lock.unlock();
      Log.v(tag, "Did unlock");
    }
    Log.v(tag, "Return " + shouldBlock.value);
    return shouldBlock.value;
  }

  // JsObjectInterface

  public void willBeCalledFromThread(@NonNull Thread t)
  {
    jsCallbackThread = t;
    Log.v("BACK", "Will be called from thread "+t.getId()+" "+t.getName());
  }
}
