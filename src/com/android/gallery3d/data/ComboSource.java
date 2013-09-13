package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

class ComboSource extends MediaSource
{
  private GalleryApp mApplication;
  private PathMatcher mMatcher;

  public ComboSource(GalleryApp paramGalleryApp)
  {
    super("combo");
    this.mApplication = paramGalleryApp;
    this.mMatcher = new PathMatcher();
    this.mMatcher.add("/combo/*", 0);
    this.mMatcher.add("/combo/*/*", 1);
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    String[] arrayOfString = paramPath.split();
    if (arrayOfString.length < 2)
      throw new RuntimeException("bad path: " + paramPath);
    DataManager localDataManager = this.mApplication.getDataManager();
    switch (this.mMatcher.match(paramPath))
    {
    default:
      return null;
    case 0:
      return new ComboAlbumSet(paramPath, this.mApplication, localDataManager.getMediaSetsFromString(arrayOfString[1]));
    case 1:
    }
    return new ComboAlbum(paramPath, localDataManager.getMediaSetsFromString(arrayOfString[2]), arrayOfString[1]);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ComboSource
 * JD-Core Version:    0.5.4
 */