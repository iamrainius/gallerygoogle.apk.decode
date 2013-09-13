package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

public class SecureSource extends MediaSource
{
  private static PathMatcher mMatcher = new PathMatcher();
  private GalleryApp mApplication;

  static
  {
    mMatcher.add("/secure/all/*", 0);
    mMatcher.add("/secure/unlock", 1);
  }

  public SecureSource(GalleryApp paramGalleryApp)
  {
    super("secure");
    this.mApplication = paramGalleryApp;
  }

  public static boolean isSecurePath(String paramString)
  {
    return mMatcher.match(Path.fromString(paramString)) == 0;
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    switch (mMatcher.match(paramPath))
    {
    default:
      throw new RuntimeException("bad path: " + paramPath);
    case 0:
      MediaItem localMediaItem = (MediaItem)this.mApplication.getDataManager().getMediaObject("/secure/unlock");
      return new SecureAlbum(paramPath, this.mApplication, localMediaItem);
    case 1:
    }
    return new UnlockImage(paramPath, this.mApplication);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.SecureSource
 * JD-Core Version:    0.5.4
 */