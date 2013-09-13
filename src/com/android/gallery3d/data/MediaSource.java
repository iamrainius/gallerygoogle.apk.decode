package com.android.gallery3d.data;

import android.net.Uri;
import java.util.ArrayList;

public abstract class MediaSource
{
  private String mPrefix;

  protected MediaSource(String paramString)
  {
    this.mPrefix = paramString;
  }

  public abstract MediaObject createMediaObject(Path paramPath);

  public Path findPathByUri(Uri paramUri, String paramString)
  {
    return null;
  }

  public Path getDefaultSetOf(Path paramPath)
  {
    return null;
  }

  public String getPrefix()
  {
    return this.mPrefix;
  }

  public long getTotalTargetCacheSize()
  {
    return 0L;
  }

  public long getTotalUsedCacheSize()
  {
    return 0L;
  }

  public void mapMediaItems(ArrayList<PathId> paramArrayList, MediaSet.ItemConsumer paramItemConsumer)
  {
    int i = paramArrayList.size();
    int j = 0;
    if (j >= i)
      return;
    PathId localPathId = (PathId)paramArrayList.get(j);
    Object localObject3;
    synchronized (DataManager.LOCK)
    {
      MediaObject localMediaObject1 = localPathId.path.getObject();
      localObject3 = localMediaObject1;
      label67: if (localObject3 != null);
    }
  }

  public void pause()
  {
  }

  public void resume()
  {
  }

  public static class PathId
  {
    public int id;
    public Path path;

    public PathId(Path paramPath, int paramInt)
    {
      this.path = paramPath;
      this.id = paramInt;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MediaSource
 * JD-Core Version:    0.5.4
 */