package com.android.gallery3d.data;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.MediaSetUtils;
import java.util.ArrayList;

public class LocalAlbum extends MediaSet
{
  private static final String[] COUNT_PROJECTION = { "count(*)" };
  private final GalleryApp mApplication;
  private final Uri mBaseUri;
  private final int mBucketId;
  private int mCachedCount = -1;
  private final boolean mIsImage;
  private final Path mItemPath;
  private final String mName;
  private final ChangeNotifier mNotifier;
  private final String mOrderClause;
  private final String[] mProjection;
  private final ContentResolver mResolver;
  private final String mWhereClause;

  public LocalAlbum(Path paramPath, GalleryApp paramGalleryApp, int paramInt, boolean paramBoolean)
  {
    this(paramPath, paramGalleryApp, paramInt, paramBoolean, BucketHelper.getBucketName(paramGalleryApp.getContentResolver(), paramInt));
  }

  public LocalAlbum(Path paramPath, GalleryApp paramGalleryApp, int paramInt, boolean paramBoolean, String paramString)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    this.mResolver = paramGalleryApp.getContentResolver();
    this.mBucketId = paramInt;
    this.mName = getLocalizedName(paramGalleryApp.getResources(), paramInt, paramString);
    this.mIsImage = paramBoolean;
    if (paramBoolean)
    {
      this.mWhereClause = "bucket_id = ?";
      this.mOrderClause = "datetaken DESC, _id DESC";
      this.mBaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      this.mProjection = LocalImage.PROJECTION;
    }
    for (this.mItemPath = LocalImage.ITEM_PATH; ; this.mItemPath = LocalVideo.ITEM_PATH)
    {
      this.mNotifier = new ChangeNotifier(this, this.mBaseUri, paramGalleryApp);
      return;
      this.mWhereClause = "bucket_id = ?";
      this.mOrderClause = "datetaken DESC, _id DESC";
      this.mBaseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
      this.mProjection = LocalVideo.PROJECTION;
    }
  }

  public static Cursor getItemCursor(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString, int paramInt)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramInt);
    return paramContentResolver.query(paramUri, paramArrayOfString, "_id=?", arrayOfString, null);
  }

  public static String getLocalizedName(Resources paramResources, int paramInt, String paramString)
  {
    if (paramInt == MediaSetUtils.CAMERA_BUCKET_ID)
      paramString = paramResources.getString(2131362318);
    do
    {
      return paramString;
      if (paramInt == MediaSetUtils.DOWNLOAD_BUCKET_ID)
        return paramResources.getString(2131362319);
      if (paramInt == MediaSetUtils.IMPORTED_BUCKET_ID)
        return paramResources.getString(2131362321);
      if (paramInt == MediaSetUtils.SNAPSHOT_BUCKET_ID)
        return paramResources.getString(2131362322);
    }
    while (paramInt != MediaSetUtils.EDITED_ONLINE_PHOTOS_BUCKET_ID);
    return paramResources.getString(2131362320);
  }

  public static MediaItem[] getMediaItemById(GalleryApp paramGalleryApp, boolean paramBoolean, ArrayList<Integer> paramArrayList)
  {
    MediaItem[] arrayOfMediaItem = new MediaItem[paramArrayList.size()];
    if (paramArrayList.isEmpty())
      return arrayOfMediaItem;
    int i = ((Integer)paramArrayList.get(0)).intValue();
    int j = ((Integer)paramArrayList.get(-1 + paramArrayList.size())).intValue();
    Uri localUri;
    String[] arrayOfString1;
    if (paramBoolean)
    {
      localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      arrayOfString1 = LocalImage.PROJECTION;
    }
    DataManager localDataManager;
    Cursor localCursor;
    for (Path localPath = LocalImage.ITEM_PATH; ; localPath = LocalVideo.ITEM_PATH)
    {
      ContentResolver localContentResolver = paramGalleryApp.getContentResolver();
      localDataManager = paramGalleryApp.getDataManager();
      String[] arrayOfString2 = new String[2];
      arrayOfString2[0] = String.valueOf(i);
      arrayOfString2[1] = String.valueOf(j);
      localCursor = localContentResolver.query(localUri, arrayOfString1, "_id BETWEEN ? AND ?", arrayOfString2, "_id");
      if (localCursor != null)
        break;
      Log.w("LocalAlbum", "query fail" + localUri);
      return arrayOfMediaItem;
      localUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
      arrayOfString1 = LocalVideo.PROJECTION;
    }
    try
    {
      int k = paramArrayList.size();
      for (int l = 0; (l < k) && (localCursor.moveToNext()); ++l)
      {
        int i1;
        do
          i1 = localCursor.getInt(0);
        while (((Integer)paramArrayList.get(l)).intValue() > i1);
        while (true)
        {
          int i2 = ((Integer)paramArrayList.get(l)).intValue();
          if (i2 >= i1)
            break;
          if (++l >= k)
            return arrayOfMediaItem;
        }
        arrayOfMediaItem[l] = loadOrUpdateItem(localPath.getChild(i1), localCursor, localDataManager, paramGalleryApp, paramBoolean);
      }
      return arrayOfMediaItem;
    }
    finally
    {
      localCursor.close();
    }
  }

  private static MediaItem loadOrUpdateItem(Path paramPath, Cursor paramCursor, DataManager paramDataManager, GalleryApp paramGalleryApp, boolean paramBoolean)
  {
    synchronized (DataManager.LOCK)
    {
      Object localObject3 = (LocalMediaItem)paramDataManager.peekMediaObject(paramPath);
      if (localObject3 == null)
      {
        if (paramBoolean);
        for (localObject3 = new LocalImage(paramPath, paramGalleryApp, paramCursor); ; localObject3 = new LocalVideo(paramPath, paramGalleryApp, paramCursor))
          return localObject3;
      }
      ((LocalMediaItem)localObject3).updateContent(paramCursor);
    }
  }

  public void delete()
  {
    GalleryUtils.assertNotInRenderThread();
    ContentResolver localContentResolver = this.mResolver;
    Uri localUri = this.mBaseUri;
    String str = this.mWhereClause;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(this.mBucketId);
    localContentResolver.delete(localUri, str, arrayOfString);
  }

  public Uri getContentUri()
  {
    if (this.mIsImage)
      return MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("bucketId", String.valueOf(this.mBucketId)).build();
    return MediaStore.Video.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("bucketId", String.valueOf(this.mBucketId)).build();
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    DataManager localDataManager = this.mApplication.getDataManager();
    Uri localUri = this.mBaseUri.buildUpon().appendQueryParameter("limit", paramInt1 + "," + paramInt2).build();
    ArrayList localArrayList = new ArrayList();
    GalleryUtils.assertNotInRenderThread();
    ContentResolver localContentResolver = this.mResolver;
    String[] arrayOfString1 = this.mProjection;
    String str = this.mWhereClause;
    String[] arrayOfString2 = new String[1];
    arrayOfString2[0] = String.valueOf(this.mBucketId);
    Cursor localCursor = localContentResolver.query(localUri, arrayOfString1, str, arrayOfString2, this.mOrderClause);
    if (localCursor == null)
    {
      Log.w("LocalAlbum", "query fail: " + localUri);
      return localArrayList;
    }
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label205;
        int i = localCursor.getInt(0);
        label205: localArrayList.add(loadOrUpdateItem(this.mItemPath.getChild(i), localCursor, localDataManager, this.mApplication, this.mIsImage));
      }
      finally
      {
        localCursor.close();
      }
    return localArrayList;
  }

  public int getMediaItemCount()
  {
    Cursor localCursor;
    if (this.mCachedCount == -1)
    {
      ContentResolver localContentResolver = this.mResolver;
      Uri localUri = this.mBaseUri;
      String[] arrayOfString1 = COUNT_PROJECTION;
      String str = this.mWhereClause;
      String[] arrayOfString2 = new String[1];
      arrayOfString2[0] = String.valueOf(this.mBucketId);
      localCursor = localContentResolver.query(localUri, arrayOfString1, str, arrayOfString2, null);
      if (localCursor == null)
      {
        Log.w("LocalAlbum", "query fail");
        return 0;
      }
    }
    try
    {
      Utils.assertTrue(localCursor.moveToNext());
      this.mCachedCount = localCursor.getInt(0);
      return this.mCachedCount;
    }
    finally
    {
      localCursor.close();
    }
  }

  public String getName()
  {
    return this.mName;
  }

  public int getSupportedOperations()
  {
    return 1029;
  }

  public boolean isCameraRoll()
  {
    return this.mBucketId == MediaSetUtils.CAMERA_BUCKET_ID;
  }

  public boolean isLeafAlbum()
  {
    return true;
  }

  public long reload()
  {
    if (this.mNotifier.isDirty())
    {
      this.mDataVersion = nextVersionNumber();
      this.mCachedCount = -1;
    }
    return this.mDataVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocalAlbum
 * JD-Core Version:    0.5.4
 */