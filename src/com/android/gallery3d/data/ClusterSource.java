package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

class ClusterSource extends MediaSource
{
  GalleryApp mApplication;
  PathMatcher mMatcher;

  public ClusterSource(GalleryApp paramGalleryApp)
  {
    super("cluster");
    this.mApplication = paramGalleryApp;
    this.mMatcher = new PathMatcher();
    this.mMatcher.add("/cluster/*/time", 0);
    this.mMatcher.add("/cluster/*/location", 1);
    this.mMatcher.add("/cluster/*/tag", 2);
    this.mMatcher.add("/cluster/*/size", 3);
    this.mMatcher.add("/cluster/*/face", 4);
    this.mMatcher.add("/cluster/*/time/*", 256);
    this.mMatcher.add("/cluster/*/location/*", 257);
    this.mMatcher.add("/cluster/*/tag/*", 258);
    this.mMatcher.add("/cluster/*/size/*", 259);
    this.mMatcher.add("/cluster/*/face/*", 260);
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    int i = this.mMatcher.match(paramPath);
    String str = this.mMatcher.getVar(0);
    DataManager localDataManager = this.mApplication.getDataManager();
    MediaSet[] arrayOfMediaSet = localDataManager.getMediaSetsFromString(str);
    switch (i)
    {
    default:
      throw new RuntimeException("bad path: " + paramPath);
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
      return new ClusterAlbumSet(paramPath, this.mApplication, arrayOfMediaSet[0], i);
    case 256:
    case 257:
    case 258:
    case 259:
    case 260:
    }
    return new ClusterAlbum(paramPath, localDataManager, localDataManager.getMediaSet(paramPath.getParent()));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.ClusterSource
 * JD-Core Version:    0.5.4
 */