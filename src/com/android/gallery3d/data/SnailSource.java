package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

public class SnailSource extends MediaSource
{
  private static int sNextId;
  private GalleryApp mApplication;
  private PathMatcher mMatcher;

  public SnailSource(GalleryApp paramGalleryApp)
  {
    super("snail");
    this.mApplication = paramGalleryApp;
    this.mMatcher = new PathMatcher();
    this.mMatcher.add("/snail/set/*", 0);
    this.mMatcher.add("/snail/item/*", 1);
  }

  public static Path getItemPath(int paramInt)
  {
    return Path.fromString("/snail/item").getChild(paramInt);
  }

  public static Path getSetPath(int paramInt)
  {
    return Path.fromString("/snail/set").getChild(paramInt);
  }

  public static int newId()
  {
    monitorenter;
    try
    {
      int i = sNextId;
      sNextId = i + 1;
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

  public MediaObject createMediaObject(Path paramPath)
  {
    DataManager localDataManager = this.mApplication.getDataManager();
    switch (this.mMatcher.match(paramPath))
    {
    default:
      return null;
    case 0:
      return new SnailAlbum(paramPath, (SnailItem)localDataManager.getMediaObject("/snail/item/" + this.mMatcher.getVar(0)));
    case 1:
    }
    this.mMatcher.getIntVar(0);
    return new SnailItem(paramPath);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.SnailSource
 * JD-Core Version:    0.5.4
 */