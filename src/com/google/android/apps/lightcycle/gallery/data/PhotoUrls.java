package com.google.android.apps.lightcycle.gallery.data;

import android.util.Log;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.LG;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PhotoUrls
{
  private static final String TAG = PhotoUrls.class.getSimpleName();
  public final String baseUrl;
  public final String editUrl;
  public final String photoId;
  private String shortUrl;

  public PhotoUrls(String paramString1, String paramString2, String paramString3)
  {
    this.baseUrl = paramString1;
    this.editUrl = paramString2;
    this.photoId = paramString3;
  }

  private static String convertShortUrlToDogfoodShortUrl(String paramString)
  {
    LG.d("Short URL is " + paramString);
    if (paramString == null)
    {
      Log.e(TAG, "Short URL is null.");
      return null;
    }
    int i = paramString.lastIndexOf('/');
    if (i < 0)
    {
      Log.e(TAG, "goo.gl short URL invalid: " + paramString);
      return null;
    }
    return "https://panoramas.googleplex.com/s/" + paramString.substring(i + 1);
  }

  public static PhotoUrls parseFromXml(String paramString)
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    int i;
    label59: int k;
    try
    {
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      InputSource localInputSource = new InputSource();
      StringReader localStringReader = new StringReader(paramString);
      localInputSource.setCharacterStream(localStringReader);
      Document localDocument = localDocumentBuilder.parse(localInputSource);
      NodeList localNodeList1 = localDocument.getElementsByTagName("content");
      i = 0;
      int j = localNodeList1.getLength();
      String str1 = null;
      if (i < j)
      {
        Node localNode1 = localNodeList1.item(i).getAttributes().getNamedItem("src");
        if (localNode1 == null)
          break label386;
        str1 = localNode1.getTextContent();
      }
      NodeList localNodeList2 = localDocument.getElementsByTagName("link");
      k = 0;
      label129: int l = localNodeList2.getLength();
      String str2 = null;
      if (k < l)
      {
        Node localNode2 = localNodeList2.item(k);
        Node localNode3 = localNode2.getAttributes().getNamedItem("rel");
        if ((localNode3 == null) || (!localNode3.getTextContent().equals("edit")))
          break label392;
        Node localNode4 = localNode2.getAttributes().getNamedItem("href");
        if (localNode4 == null)
          break label392;
        str2 = localNode4.getTextContent();
      }
      String str3 = "";
      NodeList localNodeList3 = localDocument.getElementsByTagName("gphoto:id");
      if (localNodeList3.getLength() == 1)
      {
        str3 = localNodeList3.item(0).getTextContent();
        PhotoUrls localPhotoUrls = new PhotoUrls(str1, str2, str3);
        return localPhotoUrls;
      }
      label386: label392: label340: Log.e(TAG, "We did not find exactly one gphoto:id tag. (Found " + localNodeList3.getLength() + ")");
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      Log.e(TAG, localParserConfigurationException.getMessage(), localParserConfigurationException);
      return new PhotoUrls(null, null, null);
    }
    catch (SAXException localSAXException)
    {
      Log.e(TAG, localSAXException.getMessage(), localSAXException);
      break label340:
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, localIOException.getMessage(), localIOException);
      break label340:
      ++i;
      break label59:
      ++k;
      break label129:
    }
  }

  public void getShortDogfoodUrl(Callback<String> paramCallback)
  {
    monitorenter;
    while (true)
      try
      {
        if (this.shortUrl != null)
        {
          paramCallback.onCallback(this.shortUrl);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.gallery.data.PhotoUrls
 * JD-Core Version:    0.5.4
 */