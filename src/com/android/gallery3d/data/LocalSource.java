package com.android.gallery3d.data;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import com.android.gallery3d.app.GalleryApp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class LocalSource extends MediaSource
{
  public static final Comparator<MediaSource.PathId> sIdComparator = new IdComparator(null);
  private GalleryApp mApplication;
  private ContentProviderClient mClient;
  private PathMatcher mMatcher;
  private final UriMatcher mUriMatcher = new UriMatcher(-1);

  public LocalSource(GalleryApp paramGalleryApp)
  {
    super("local");
    this.mApplication = paramGalleryApp;
    this.mMatcher = new PathMatcher();
    this.mMatcher.add("/local/image", 0);
    this.mMatcher.add("/local/video", 1);
    this.mMatcher.add("/local/all", 6);
    this.mMatcher.add("/local/image/*", 2);
    this.mMatcher.add("/local/video/*", 3);
    this.mMatcher.add("/local/all/*", 7);
    this.mMatcher.add("/local/image/item/*", 4);
    this.mMatcher.add("/local/video/item/*", 5);
    this.mUriMatcher.addURI("media", "external/images/media/#", 4);
    this.mUriMatcher.addURI("media", "external/video/media/#", 5);
    this.mUriMatcher.addURI("media", "external/images/media", 2);
    this.mUriMatcher.addURI("media", "external/video/media", 3);
    this.mUriMatcher.addURI("media", "external/file", 7);
  }

  private Path getAlbumPath(Uri paramUri, int paramInt)
  {
    int i = getMediaType(paramUri.getQueryParameter("mediaTypes"), paramInt);
    String str = paramUri.getQueryParameter("bucketId");
    int j;
    try
    {
      j = Integer.parseInt(str);
      switch (i)
      {
      case 2:
      case 3:
      default:
        return Path.fromString("/local/all").getChild(j);
      case 1:
      case 4:
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.w("LocalSource", "invalid bucket id: " + str, localNumberFormatException);
      return null;
    }
    return Path.fromString("/local/image").getChild(j);
    return Path.fromString("/local/video").getChild(j);
  }

  private static int getMediaType(String paramString, int paramInt)
  {
    if (paramString == null)
      return paramInt;
    try
    {
      int i = Integer.parseInt(paramString);
      if ((i & 0x5) != 0);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.w("LocalSource", "invalid type: " + paramString, localNumberFormatException);
    }
    return paramInt;
  }

  private void processMapMediaItems(ArrayList<MediaSource.PathId> paramArrayList, MediaSet.ItemConsumer paramItemConsumer, boolean paramBoolean)
  {
    Collections.sort(paramArrayList, sIdComparator);
    int i = paramArrayList.size();
    int l;
    for (int j = 0; j < i; j = l)
    {
      MediaSource.PathId localPathId = (MediaSource.PathId)paramArrayList.get(j);
      ArrayList localArrayList = new ArrayList();
      int k = Integer.parseInt(localPathId.path.getSuffix());
      localArrayList.add(Integer.valueOf(k));
      for (l = j + 1; ; ++l)
      {
        int i2;
        if (l < i)
        {
          i2 = Integer.parseInt(((MediaSource.PathId)paramArrayList.get(l)).path.getSuffix());
          if (i2 - k < 500)
            break label166;
        }
        MediaItem[] arrayOfMediaItem = LocalAlbum.getMediaItemById(this.mApplication, paramBoolean, localArrayList);
        for (int i1 = j; ; ++i1)
        {
          if (i1 >= l)
            break label183;
          paramItemConsumer.consume(((MediaSource.PathId)paramArrayList.get(i1)).id, arrayOfMediaItem[(i1 - j)]);
        }
        label166: label183: localArrayList.add(Integer.valueOf(i2));
      }
    }
  }

  public MediaObject createMediaObject(Path paramPath)
  {
    GalleryApp localGalleryApp = this.mApplication;
    switch (this.mMatcher.match(paramPath))
    {
    default:
      throw new RuntimeException("bad path: " + paramPath);
    case 0:
    case 1:
    case 6:
      return new LocalAlbumSet(paramPath, this.mApplication);
    case 2:
      return new LocalAlbum(paramPath, localGalleryApp, this.mMatcher.getIntVar(0), true);
    case 3:
      return new LocalAlbum(paramPath, localGalleryApp, this.mMatcher.getIntVar(0), false);
    case 7:
      int i = this.mMatcher.getIntVar(0);
      DataManager localDataManager = localGalleryApp.getDataManager();
      MediaSet localMediaSet1 = (MediaSet)localDataManager.getMediaObject(LocalAlbumSet.PATH_IMAGE.getChild(i));
      MediaSet localMediaSet2 = (MediaSet)localDataManager.getMediaObject(LocalAlbumSet.PATH_VIDEO.getChild(i));
      return new LocalMergeAlbum(paramPath, DataManager.sDateTakenComparator, new MediaSet[] { localMediaSet1, localMediaSet2 }, i);
    case 4:
      return new LocalImage(paramPath, this.mApplication, this.mMatcher.getIntVar(0));
    case 5:
    }
    return new LocalVideo(paramPath, this.mApplication, this.mMatcher.getIntVar(0));
  }

  public Path findPathByUri(Uri paramUri, String paramString)
  {
    try
    {
      switch (this.mUriMatcher.match(paramUri))
      {
      case 4:
        long l2 = ContentUris.parseId(paramUri);
        if (l2 >= 0L)
          return LocalImage.ITEM_PATH.getChild(l2);
      case 5:
        long l1 = ContentUris.parseId(paramUri);
        if (l1 >= 0L)
          return LocalVideo.ITEM_PATH.getChild(l1);
      case 2:
        return getAlbumPath(paramUri, 1);
      case 3:
        return getAlbumPath(paramUri, 4);
      case 7:
        Path localPath = getAlbumPath(paramUri, 0);
        return localPath;
      case 6:
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      Log.w("LocalSource", "uri: " + paramUri.toString(), localNumberFormatException);
    }
    return null;
  }

  public Path getDefaultSetOf(Path paramPath)
  {
    MediaObject localMediaObject = this.mApplication.getDataManager().getMediaObject(paramPath);
    if (localMediaObject instanceof LocalMediaItem)
      return Path.fromString("/local/all").getChild(String.valueOf(((LocalMediaItem)localMediaObject).getBucketId()));
    return null;
  }

  public void mapMediaItems(ArrayList<MediaSource.PathId> paramArrayList, MediaSet.ItemConsumer paramItemConsumer)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = paramArrayList.size();
    int j = 0;
    if (j < i)
    {
      label26: MediaSource.PathId localPathId = (MediaSource.PathId)paramArrayList.get(j);
      Path localPath = localPathId.path.getParent();
      if (localPath == LocalImage.ITEM_PATH)
        localArrayList1.add(localPathId);
      while (true)
      {
        ++j;
        break label26:
        if (localPath != LocalVideo.ITEM_PATH)
          continue;
        localArrayList2.add(localPathId);
      }
    }
    processMapMediaItems(localArrayList1, paramItemConsumer, true);
    processMapMediaItems(localArrayList2, paramItemConsumer, false);
  }

  public void pause()
  {
    this.mClient.release();
    this.mClient = null;
  }

  public void resume()
  {
    this.mClient = this.mApplication.getContentResolver().acquireContentProviderClient("media");
  }

  private static class IdComparator
    implements Comparator<MediaSource.PathId>
  {
    public int compare(MediaSource.PathId paramPathId1, MediaSource.PathId paramPathId2)
    {
      String str1 = paramPathId1.path.getSuffix();
      String str2 = paramPathId2.path.getSuffix();
      int i = str1.length();
      int j = str2.length();
      if (i < j)
        return -1;
      if (i > j)
        return 1;
      return str1.compareTo(str2);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocalSource
 * JD-Core Version:    0.5.4
 */