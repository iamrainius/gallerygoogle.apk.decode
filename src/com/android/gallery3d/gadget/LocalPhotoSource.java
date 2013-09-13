package com.android.gallery3d.gadget;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.GalleryUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class LocalPhotoSource
  implements WidgetSource
{
  private static final Uri CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
  private static final String[] COUNT_PROJECTION;
  private static final Path LOCAL_IMAGE_ROOT;
  private static final String ORDER;
  private static final String[] PROJECTION = { "_id" };
  private static final String SELECTION;
  private boolean mContentDirty = true;
  private ContentListener mContentListener;
  private ContentObserver mContentObserver;
  private Context mContext;
  private DataManager mDataManager;
  private ArrayList<Long> mPhotos = new ArrayList();

  static
  {
    COUNT_PROJECTION = new String[] { "count(*)" };
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = "bucket_id";
    arrayOfObject[1] = Integer.valueOf(getDownloadBucketId());
    SELECTION = String.format("%s != %s", arrayOfObject);
    ORDER = String.format("%s DESC", new Object[] { "datetaken" });
    LOCAL_IMAGE_ROOT = Path.fromString("/local/image/item");
  }

  public LocalPhotoSource(Context paramContext)
  {
    this.mContext = paramContext;
    this.mDataManager = ((GalleryApp)paramContext.getApplicationContext()).getDataManager();
    this.mContentObserver = new ContentObserver(new Handler())
    {
      public void onChange(boolean paramBoolean)
      {
        LocalPhotoSource.access$002(LocalPhotoSource.this, true);
        if (LocalPhotoSource.this.mContentListener == null)
          return;
        LocalPhotoSource.this.mContentListener.onContentDirty();
      }
    };
    this.mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, this.mContentObserver);
  }

  private static int getDownloadBucketId()
  {
    return GalleryUtils.getBucketId(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
  }

  private int[] getExponentialIndice(int paramInt1, int paramInt2)
  {
    Random localRandom = new Random();
    if (paramInt2 > paramInt1)
      paramInt2 = paramInt1;
    HashSet localHashSet = new HashSet(paramInt2);
    while (localHashSet.size() < paramInt2)
    {
      int l = (int)(-Math.log(localRandom.nextDouble()) * paramInt1 / 2.0D);
      if (l >= paramInt1)
        continue;
      localHashSet.add(Integer.valueOf(l));
    }
    int[] arrayOfInt = new int[paramInt2];
    int i = 0;
    Iterator localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      int j = ((Integer)localIterator.next()).intValue();
      int k = i + 1;
      arrayOfInt[i] = j;
      i = k;
    }
    return arrayOfInt;
  }

  private int getPhotoCount(ContentResolver paramContentResolver)
  {
    Cursor localCursor = paramContentResolver.query(CONTENT_URI, COUNT_PROJECTION, SELECTION, null, null);
    if (localCursor == null)
      return 0;
    try
    {
      Utils.assertTrue(localCursor.moveToNext());
      int i = localCursor.getInt(0);
      return i;
    }
    finally
    {
      localCursor.close();
    }
  }

  private boolean isContentSound(int paramInt)
  {
    if (this.mPhotos.size() < Math.min(paramInt, 128));
    Cursor localCursor;
    do
    {
      return false;
      if (this.mPhotos.size() == 0)
        return true;
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator = this.mPhotos.iterator();
      while (localIterator.hasNext())
      {
        Long localLong = (Long)localIterator.next();
        if (localStringBuilder.length() > 0)
          localStringBuilder.append(",");
        localStringBuilder.append(localLong);
      }
      ContentResolver localContentResolver = this.mContext.getContentResolver();
      Uri localUri = CONTENT_URI;
      String[] arrayOfString = COUNT_PROJECTION;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = "_id";
      arrayOfObject[1] = localStringBuilder.toString();
      localCursor = localContentResolver.query(localUri, arrayOfString, String.format("%s in (%s)", arrayOfObject), null, null);
    }
    while (localCursor == null);
    try
    {
      Utils.assertTrue(localCursor.moveToNext());
      int i = localCursor.getInt(0);
      int j = this.mPhotos.size();
      if (i == j)
      {
        k = 1;
        return k;
      }
      int k = 0;
    }
    finally
    {
      localCursor.close();
    }
  }

  public void close()
  {
    this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
  }

  public Uri getContentUri(int paramInt)
  {
    if (paramInt < this.mPhotos.size())
      return CONTENT_URI.buildUpon().appendPath(String.valueOf(this.mPhotos.get(paramInt))).build();
    return null;
  }

  public Bitmap getImage(int paramInt)
  {
    if (paramInt >= this.mPhotos.size())
      return null;
    long l = ((Long)this.mPhotos.get(paramInt)).longValue();
    MediaItem localMediaItem = (MediaItem)this.mDataManager.getMediaObject(LOCAL_IMAGE_ROOT.getChild(l));
    if (localMediaItem == null)
      return null;
    return WidgetUtils.createWidgetBitmap(localMediaItem);
  }

  public void reload()
  {
    if (!this.mContentDirty);
    int[] arrayOfInt;
    Cursor localCursor;
    do
    {
      int i;
      do
      {
        return;
        this.mContentDirty = false;
        i = getPhotoCount(this.mContext.getContentResolver());
      }
      while (isContentSound(i));
      arrayOfInt = getExponentialIndice(i, 128);
      Arrays.sort(arrayOfInt);
      this.mPhotos.clear();
      localCursor = this.mContext.getContentResolver().query(CONTENT_URI, PROJECTION, SELECTION, null, ORDER);
    }
    while (localCursor == null);
    try
    {
      int j = arrayOfInt.length;
      for (int k = 0; k < j; ++k)
      {
        if (!localCursor.moveToPosition(arrayOfInt[k]))
          continue;
        this.mPhotos.add(Long.valueOf(localCursor.getLong(0)));
      }
      return;
    }
    finally
    {
      localCursor.close();
    }
  }

  public void setContentListener(ContentListener paramContentListener)
  {
    this.mContentListener = paramContentListener;
  }

  public int size()
  {
    reload();
    return this.mPhotos.size();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.gadget.LocalPhotoSource
 * JD-Core Version:    0.5.4
 */