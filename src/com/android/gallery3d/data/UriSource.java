package com.android.gallery3d.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.util.MediaSetUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

class UriSource extends MediaSource
{
  private static final String[] ALLOWED_URIS = { "content://downloads/all_downloads/" };
  private GalleryApp mApplication;

  public UriSource(GalleryApp paramGalleryApp)
  {
    super("uri");
    this.mApplication = paramGalleryApp;
  }

  private static boolean checkIfUriStringAllowed(String paramString)
  {
    String[] arrayOfString = ALLOWED_URIS;
    int i = arrayOfString.length;
    for (int j = 0; ; ++j)
    {
      int k = 0;
      if (j < i)
      {
        if (!paramString.startsWith(arrayOfString[j]))
          continue;
        k = 1;
      }
      return k;
    }
  }

  private String getMimeType(Uri paramUri)
  {
    String str1;
    if ("file".equals(paramUri.getScheme()))
    {
      String str2 = MimeTypeMap.getFileExtensionFromUrl(paramUri.toString());
      str1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str2.toLowerCase());
      if (str1 == null);
    }
    do
    {
      return str1;
      str1 = this.mApplication.getContentResolver().getType(paramUri);
    }
    while (str1 != null);
    return "image/*";
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    String[] arrayOfString = paramPath.split();
    if (arrayOfString.length != 3)
      throw new RuntimeException("bad path: " + paramPath);
    try
    {
      String str1 = URLDecoder.decode(arrayOfString[1], "utf-8");
      String str2 = URLDecoder.decode(arrayOfString[2], "utf-8");
      UriImage localUriImage = new UriImage(this.mApplication, paramPath, Uri.parse(str1), str2);
      return localUriImage;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new AssertionError(localUnsupportedEncodingException);
    }
  }

  public Path findPathByUri(Uri paramUri, String paramString)
  {
    String str = getMimeType(paramUri);
    if ((paramString == null) || (("image/*".equals(paramString)) && (str.startsWith("image/"))))
      paramString = str;
    if (paramString.startsWith("image/"))
      try
      {
        Path localPath = Path.fromString("/uri/" + URLEncoder.encode(paramUri.toString(), "utf-8") + "/" + URLEncoder.encode(paramString, "utf-8"));
        return localPath;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new AssertionError(localUnsupportedEncodingException);
      }
    return null;
  }

  public Path getDefaultSetOf(Path paramPath)
  {
    MediaObject localMediaObject = this.mApplication.getDataManager().getMediaObject(paramPath);
    if ((!localMediaObject instanceof UriImage) || (!checkIfUriStringAllowed(((UriImage)localMediaObject).getContentUri().toString())))
      return null;
    return Path.fromString("/local/all").getChild(MediaSetUtils.DOWNLOAD_BUCKET_ID);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.UriSource
 * JD-Core Version:    0.5.4
 */