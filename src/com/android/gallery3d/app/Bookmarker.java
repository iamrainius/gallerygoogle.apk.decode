package com.android.gallery3d.app;

import android.content.Context;
import android.net.Uri;
import com.android.gallery3d.common.BlobCache;
import com.android.gallery3d.util.CacheManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

class Bookmarker
{
  private final Context mContext;

  public Bookmarker(Context paramContext)
  {
    this.mContext = paramContext;
  }

  public Integer getBookmark(Uri paramUri)
  {
    try
    {
      byte[] arrayOfByte = CacheManager.getCache(this.mContext, "bookmark", 100, 10240, 1).lookup(paramUri.hashCode());
      if (arrayOfByte == null)
        return null;
      DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
      String str = DataInputStream.readUTF(localDataInputStream);
      int i = localDataInputStream.readInt();
      int j = localDataInputStream.readInt();
      if ((str.equals(paramUri.toString())) && (i >= 30000) && (j >= 120000) && (i <= j - 30000))
      {
        Integer localInteger = Integer.valueOf(i);
        return localInteger;
      }
    }
    catch (Throwable localThrowable)
    {
      Log.w("Bookmarker", "getBookmark failed", localThrowable);
    }
    return null;
  }

  public void setBookmark(Uri paramUri, int paramInt1, int paramInt2)
  {
    try
    {
      BlobCache localBlobCache = CacheManager.getCache(this.mContext, "bookmark", 100, 10240, 1);
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
      localDataOutputStream.writeUTF(paramUri.toString());
      localDataOutputStream.writeInt(paramInt1);
      localDataOutputStream.writeInt(paramInt2);
      localDataOutputStream.flush();
      localBlobCache.insert(paramUri.hashCode(), localByteArrayOutputStream.toByteArray());
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("Bookmarker", "setBookmark failed", localThrowable);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.Bookmarker
 * JD-Core Version:    0.5.4
 */