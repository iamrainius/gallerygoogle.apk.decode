package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

public class MtpSource extends MediaSource
{
  GalleryApp mApplication;
  PathMatcher mMatcher;
  MtpContext mMtpContext;

  public MtpSource(GalleryApp paramGalleryApp)
  {
    super("mtp");
    this.mApplication = paramGalleryApp;
    this.mMatcher = new PathMatcher();
    this.mMatcher.add("/mtp", 0);
    this.mMatcher.add("/mtp/*", 1);
    this.mMatcher.add("/mtp/item/*/*", 2);
    this.mMtpContext = new MtpContext(this.mApplication.getAndroidContext());
  }

  public static boolean isMtpPath(String paramString)
  {
    return (paramString != null) && (Path.fromString(paramString).getPrefix().equals("mtp"));
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    switch (this.mMatcher.match(paramPath))
    {
    default:
      throw new RuntimeException("bad path: " + paramPath);
    case 0:
      return new MtpDeviceSet(paramPath, this.mApplication, this.mMtpContext);
    case 1:
      int k = this.mMatcher.getIntVar(0);
      return new MtpDevice(paramPath, this.mApplication, k, this.mMtpContext);
    case 2:
    }
    int i = this.mMatcher.getIntVar(0);
    int j = this.mMatcher.getIntVar(1);
    return new MtpImage(paramPath, this.mApplication, i, j, this.mMtpContext);
  }

  public void pause()
  {
    this.mMtpContext.pause();
  }

  public void resume()
  {
    this.mMtpContext.resume();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.MtpSource
 * JD-Core Version:    0.5.4
 */