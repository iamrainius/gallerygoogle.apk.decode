package com.android.gallery3d.data;

import android.net.Uri;

public abstract class MediaObject
{
  private static long sVersionSerial = 0L;
  protected long mDataVersion;
  protected final Path mPath;

  public MediaObject(Path paramPath, long paramLong)
  {
    paramPath.setObject(this);
    this.mPath = paramPath;
    this.mDataVersion = paramLong;
  }

  public static int getTypeFromString(String paramString)
  {
    if ("all".equals(paramString))
      return 6;
    if ("image".equals(paramString))
      return 2;
    if ("video".equals(paramString))
      return 4;
    throw new IllegalArgumentException(paramString);
  }

  public static String getTypeString(int paramInt)
  {
    switch (paramInt)
    {
    case 3:
    case 5:
    default:
      throw new IllegalArgumentException();
    case 2:
      return "image";
    case 4:
      return "video";
    case 6:
    }
    return "all";
  }

  public static long nextVersionNumber()
  {
    monitorenter;
    try
    {
      long l = 1L + sVersionSerial;
      sVersionSerial = l;
      monitorexit;
      return l;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public boolean Import()
  {
    throw new UnsupportedOperationException();
  }

  public void cache(int paramInt)
  {
    throw new UnsupportedOperationException();
  }

  public void clearCachedPanoramaSupport()
  {
  }

  public void delete()
  {
    throw new UnsupportedOperationException();
  }

  public int getCacheFlag()
  {
    return 0;
  }

  public long getCacheSize()
  {
    throw new UnsupportedOperationException();
  }

  public int getCacheStatus()
  {
    throw new UnsupportedOperationException();
  }

  public Uri getContentUri()
  {
    String str = super.getClass().getName();
    Log.e("MediaObject", "Class " + str + "should implement getContentUri.");
    Log.e("MediaObject", "The object was created from path: " + getPath());
    throw new UnsupportedOperationException();
  }

  public long getDataVersion()
  {
    return this.mDataVersion;
  }

  public MediaDetails getDetails()
  {
    return new MediaDetails();
  }

  public int getMediaType()
  {
    return 1;
  }

  public void getPanoramaSupport(PanoramaSupportCallback paramPanoramaSupportCallback)
  {
    paramPanoramaSupportCallback.panoramaInfoAvailable(this, false, false);
  }

  public Path getPath()
  {
    return this.mPath;
  }

  public Uri getPlayUri()
  {
    throw new UnsupportedOperationException();
  }

  public int getSupportedOperations()
  {
    return 0;
  }

  public void rotate(int paramInt)
  {
    throw new UnsupportedOperationException();
  }

  public static abstract interface PanoramaSupportCallback
  {
    public abstract void panoramaInfoAvailable(MediaObject paramMediaObject, boolean paramBoolean1, boolean paramBoolean2);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MediaObject
 * JD-Core Version:    0.5.4
 */