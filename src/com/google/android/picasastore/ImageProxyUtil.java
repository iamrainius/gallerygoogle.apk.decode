package com.google.android.picasastore;

import android.net.Uri;
import android.net.Uri.Builder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageProxyUtil
{
  private static final Pattern PROXY_HOSTED_IMAGE_URL_RE = Pattern.compile("^(((http(s)?):)?\\/\\/images(\\d)?-.+-opensocial\\.googleusercontent\\.com\\/gadgets\\/proxy\\?)");
  static int sProxyIndex;

  private static String createProxyUrl()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("http://").append("images").append(getNextProxyIndex()).append("-").append("esmobile").append("-opensocial.googleusercontent.com").append("/gadgets/proxy");
    return localStringBuffer.toString();
  }

  private static int getNextProxyIndex()
  {
    monitorenter;
    try
    {
      int i = 1 + sProxyIndex;
      sProxyIndex = i;
      sProxyIndex %= 3;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private static Set<String> getQueryParameterNames(Uri paramUri)
  {
    if (paramUri.isOpaque())
      throw new UnsupportedOperationException("This isn't a hierarchical URI.");
    String str = paramUri.getEncodedQuery();
    if (str == null)
      return Collections.emptySet();
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    int i = 0;
    int j = str.indexOf('&', i);
    if (j == -1);
    for (int k = str.length(); ; k = j)
    {
      int l = str.indexOf('=', i);
      if ((l > k) || (l == -1))
        l = k;
      localLinkedHashSet.add(Uri.decode(str.substring(i, l)));
      i = k + 1;
      if (i >= str.length());
      return Collections.unmodifiableSet(localLinkedHashSet);
    }
  }

  public static boolean isProxyHostedUrl(String paramString)
  {
    if (paramString == null)
      return false;
    return PROXY_HOSTED_IMAGE_URL_RE.matcher(paramString).find();
  }

  public static String setImageUrlSize(int paramInt, String paramString)
  {
    if (paramString == null)
      return paramString;
    String str;
    if (!isProxyHostedUrl(paramString))
      str = createProxyUrl();
    while (true)
    {
      return setImageUrlSizeOptions(paramInt, paramInt, Uri.parse(str), paramString).toString();
      str = paramString;
      paramString = null;
    }
  }

  public static Uri setImageUrlSizeOptions(int paramInt1, int paramInt2, Uri paramUri, String paramString)
  {
    Uri.Builder localBuilder1 = Uri.EMPTY.buildUpon();
    localBuilder1.authority(paramUri.getAuthority());
    localBuilder1.scheme(paramUri.getScheme());
    localBuilder1.path(paramUri.getPath());
    localBuilder1.appendQueryParameter("resize_w", Integer.toString(paramInt1));
    localBuilder1.appendQueryParameter("resize_h", Integer.toString(paramInt2));
    localBuilder1.appendQueryParameter("no_expand", "1");
    Uri localUri = localBuilder1.build();
    Iterator localIterator1 = getQueryParameterNames(paramUri).iterator();
    String str;
    Uri.Builder localBuilder6;
    while (true)
    {
      if (!localIterator1.hasNext())
        break label207;
      str = (String)localIterator1.next();
      if (localUri.getQueryParameter(str) != null)
        continue;
      localBuilder6 = localUri.buildUpon();
      if (!"url".equals(str))
        break;
      localBuilder6.appendQueryParameter("url", paramUri.getQueryParameter("url"));
      localUri = localBuilder6.build();
    }
    Iterator localIterator2 = paramUri.getQueryParameters(str).iterator();
    while (true)
    {
      if (localIterator2.hasNext());
      localBuilder6.appendQueryParameter(str, (String)localIterator2.next());
    }
    if ((paramString != null) && (localUri.getQueryParameter("url") == null))
    {
      label207: Uri.Builder localBuilder5 = localUri.buildUpon();
      localBuilder5.appendQueryParameter("url", paramString);
      localUri = localBuilder5.build();
    }
    if (localUri.getQueryParameter("container") == null)
    {
      Uri.Builder localBuilder4 = localUri.buildUpon();
      localBuilder4.appendQueryParameter("container", "esmobile");
      localUri = localBuilder4.build();
    }
    if (localUri.getQueryParameter("gadget") == null)
    {
      Uri.Builder localBuilder3 = localUri.buildUpon();
      localBuilder3.appendQueryParameter("gadget", "a");
      localUri = localBuilder3.build();
    }
    if (localUri.getQueryParameter("rewriteMime") == null)
    {
      Uri.Builder localBuilder2 = localUri.buildUpon();
      localBuilder2.appendQueryParameter("rewriteMime", "image/*");
      localUri = localBuilder2.build();
    }
    return localUri;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasastore.ImageProxyUtil
 * JD-Core Version:    0.5.4
 */