package my.mybrowser.response;

import android.net.Uri;
import android.webkit.WebResourceResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FutureResponse extends WebResourceResponse
{

  private class FutureInputStream extends InputStream {

    private URL url;
    private URLConnection connection;
    public FutureInputStream(URL url)
    {
      this.url = url;
    }

    @Override
    public int read() throws IOException
    {
      if(connection == null)
      {
        connection = url.openConnection();
      }
      return connection.getInputStream().read();
    }

  }

  private class FutureMap<K, V> extends HashMap<K, V>
  {
    @Override
    public Set<Entry<K, V>> entrySet()
    {
      return super.entrySet();
    }

    @Override
    public Set<K> keySet()
    {
      return super.keySet();
    }

    @Override
    public Collection<V> values()
    {
      return super.values();
    }
  }

  private URL requestUrl;
  public FutureResponse(Uri requestUri)
  {
    super(null, null, null);
    try {
      this.requestUrl = new URL(requestUri.toString());
    } catch (MalformedURLException e) {
    }
  }

  @Override
  public String getMimeType()
  {
    return super.getMimeType();
  }

  @Override
  public String getEncoding()
  {
    return super.getEncoding();
  }

  @Override
  public int getStatusCode()
  {
    return super.getStatusCode();
  }

  @Override
  public String getReasonPhrase()
  {
    return super.getReasonPhrase();
  }

  @Override
  public Map<String, String> getResponseHeaders()
  {
    Map<String, String> retval = new FutureMap<>();
    retval.put("User-Agent", "007");
    return retval;
  }

  @Override
  public InputStream getData()
  {
    return new FutureInputStream(requestUrl);
  }
}
