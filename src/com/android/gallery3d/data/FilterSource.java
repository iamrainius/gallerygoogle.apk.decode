package com.android.gallery3d.data;

import com.android.gallery3d.app.GalleryApp;

public class FilterSource extends MediaSource
{
  private GalleryApp mApplication;
  private MediaItem mCameraShortcutItem;
  private MediaItem mEmptyItem;
  private PathMatcher mMatcher;

  public FilterSource(GalleryApp paramGalleryApp)
  {
    super("filter");
    this.mApplication = paramGalleryApp;
    this.mMatcher = new PathMatcher();
    this.mMatcher.add("/filter/mediatype/*/*", 0);
    this.mMatcher.add("/filter/delete/*", 1);
    this.mMatcher.add("/filter/empty/*", 2);
    this.mMatcher.add("/filter/empty_prompt", 3);
    this.mMatcher.add("/filter/camera_shortcut", 4);
    this.mMatcher.add("/filter/camera_shortcut_item", 5);
    this.mEmptyItem = new EmptyAlbumImage(Path.fromString("/filter/empty_prompt"), this.mApplication);
    this.mCameraShortcutItem = new CameraShortcutImage(Path.fromString("/filter/camera_shortcut_item"), this.mApplication);
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    int i = this.mMatcher.match(paramPath);
    DataManager localDataManager = this.mApplication.getDataManager();
    switch (i)
    {
    default:
      throw new RuntimeException("bad path: " + paramPath);
    case 0:
      int j = this.mMatcher.getIntVar(0);
      return new FilterTypeSet(paramPath, localDataManager, localDataManager.getMediaSetsFromString(this.mMatcher.getVar(1))[0], j);
    case 1:
      return new FilterDeleteSet(paramPath, localDataManager.getMediaSetsFromString(this.mMatcher.getVar(0))[0]);
    case 2:
      return new FilterEmptyPromptSet(paramPath, localDataManager.getMediaSetsFromString(this.mMatcher.getVar(0))[0], this.mEmptyItem);
    case 3:
      return this.mEmptyItem;
    case 4:
      return new SingleItemAlbum(paramPath, this.mCameraShortcutItem);
    case 5:
    }
    return this.mCameraShortcutItem;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.FilterSource
 * JD-Core Version:    0.5.4
 */