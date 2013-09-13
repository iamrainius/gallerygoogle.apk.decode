package com.android.gallery3d.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.app.StitchingChangeListener;
import com.android.gallery3d.util.MediaSetUtils;
import java.util.ArrayList;

public class SecureAlbum extends MediaSet
  implements StitchingChangeListener
{
  private static final String[] PROJECTION = { "_id" };
  private static final Uri[] mWatchUris;
  private ArrayList<Boolean> mAllItemTypes = new ArrayList();
  private ArrayList<Path> mAllItems = new ArrayList();
  private Context mContext;
  private DataManager mDataManager;
  private ArrayList<Path> mExistingItems = new ArrayList();
  private int mMaxImageId = -2147483648;
  private int mMaxVideoId = -2147483648;
  private int mMinImageId = 2147483647;
  private int mMinVideoId = 2147483647;
  private final ChangeNotifier mNotifier;
  private boolean mShowUnlockItem;
  private MediaItem mUnlockItem;

  static
  {
    Uri[] arrayOfUri = new Uri[2];
    arrayOfUri[0] = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    arrayOfUri[1] = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    mWatchUris = arrayOfUri;
  }

  public SecureAlbum(Path paramPath, GalleryApp paramGalleryApp, MediaItem paramMediaItem)
  {
    super(paramPath, nextVersionNumber());
    this.mContext = paramGalleryApp.getAndroidContext();
    this.mDataManager = paramGalleryApp.getDataManager();
    this.mNotifier = new ChangeNotifier(this, mWatchUris, paramGalleryApp);
    this.mUnlockItem = paramMediaItem;
    if ((!isCameraBucketEmpty(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)) || (!isCameraBucketEmpty(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)));
    for (int i = 1; ; i = 0)
    {
      this.mShowUnlockItem = i;
      return;
    }
  }

  private boolean isCameraBucketEmpty(Uri paramUri)
  {
    Uri localUri = paramUri.buildUpon().appendQueryParameter("limit", "1").build();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(MediaSetUtils.CAMERA_BUCKET_ID);
    Cursor localCursor = this.mContext.getContentResolver().query(localUri, PROJECTION, "bucket_id = ?", arrayOfString, null);
    if (localCursor == null)
      return true;
    try
    {
      int i = localCursor.getCount();
      if (i == 0)
      {
        j = 1;
        return j;
      }
      int j = 0;
    }
    finally
    {
      localCursor.close();
    }
  }

  private ArrayList<Integer> queryExistingIds(Uri paramUri, int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramInt1 == 2147483647) || (paramInt2 == -2147483648));
    Cursor localCursor;
    do
    {
      return localArrayList;
      String[] arrayOfString = new String[2];
      arrayOfString[0] = String.valueOf(paramInt1);
      arrayOfString[1] = String.valueOf(paramInt2);
      localCursor = this.mContext.getContentResolver().query(paramUri, PROJECTION, "_id BETWEEN ? AND ?", arrayOfString, null);
    }
    while (localCursor == null);
    while (true)
      try
      {
        if (!localCursor.moveToNext())
          break label102;
        label102: localArrayList.add(Integer.valueOf(localCursor.getInt(0)));
      }
      finally
      {
        localCursor.close();
      }
    return localArrayList;
  }

  private void updateExistingItems()
  {
    if (this.mAllItems.size() == 0);
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    int i;
    do
    {
      return;
      localArrayList1 = queryExistingIds(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this.mMinImageId, this.mMaxImageId);
      localArrayList2 = queryExistingIds(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.mMinVideoId, this.mMaxVideoId);
      this.mExistingItems.clear();
      label60: i = -1 + this.mAllItems.size();
    }
    while (i < 0);
    Path localPath = (Path)this.mAllItems.get(i);
    boolean bool = ((Boolean)this.mAllItemTypes.get(i)).booleanValue();
    int j = Integer.parseInt(localPath.getSuffix());
    if (bool)
      if (localArrayList2.contains(Integer.valueOf(j)))
        this.mExistingItems.add(localPath);
    while (true)
    {
      --i;
      break label60:
      if (!localArrayList1.contains(Integer.valueOf(j)))
        continue;
      this.mExistingItems.add(localPath);
    }
  }

  public void addMediaItem(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
    {
      this.mAllItems.add(Path.fromString("/local/video/item/" + paramInt));
      this.mMinVideoId = Math.min(this.mMinVideoId, paramInt);
      this.mMaxVideoId = Math.max(this.mMaxVideoId, paramInt);
    }
    while (true)
    {
      this.mAllItemTypes.add(Boolean.valueOf(paramBoolean));
      this.mNotifier.fakeChange();
      return;
      this.mAllItems.add(Path.fromString("/local/image/item/" + paramInt));
      this.mMinImageId = Math.min(this.mMinImageId, paramInt);
      this.mMaxImageId = Math.max(this.mMaxImageId, paramInt);
    }
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    int i = this.mExistingItems.size();
    ArrayList localArrayList1;
    if (paramInt1 >= i + 1)
      localArrayList1 = new ArrayList();
    do
    {
      return localArrayList1;
      int j = Math.min(paramInt1 + paramInt2, i);
      ArrayList localArrayList2 = new ArrayList(this.mExistingItems.subList(paramInt1, j));
      MediaItem[] arrayOfMediaItem = new MediaItem[j - paramInt1];
      1 local1 = new MediaSet.ItemConsumer(arrayOfMediaItem)
      {
        public void consume(int paramInt, MediaItem paramMediaItem)
        {
          this.val$buf[paramInt] = paramMediaItem;
        }
      };
      this.mDataManager.mapMediaItems(localArrayList2, local1, 0);
      localArrayList1 = new ArrayList(j - paramInt1);
      for (int k = 0; k < arrayOfMediaItem.length; ++k)
        localArrayList1.add(arrayOfMediaItem[k]);
    }
    while (!this.mShowUnlockItem);
    localArrayList1.add(this.mUnlockItem);
    return localArrayList1;
  }

  public int getMediaItemCount()
  {
    int i = this.mExistingItems.size();
    if (this.mShowUnlockItem);
    for (int j = 1; ; j = 0)
      return j + i;
  }

  public String getName()
  {
    return "secure";
  }

  public boolean isLeafAlbum()
  {
    return true;
  }

  public void onStitchingProgress(Uri paramUri, int paramInt)
  {
  }

  public void onStitchingQueued(Uri paramUri)
  {
    addMediaItem(false, Integer.parseInt(paramUri.getLastPathSegment()));
  }

  public void onStitchingResult(Uri paramUri)
  {
  }

  public long reload()
  {
    if (this.mNotifier.isDirty())
    {
      this.mDataVersion = nextVersionNumber();
      updateExistingItems();
    }
    return this.mDataVersion;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.SecureAlbum
 * JD-Core Version:    0.5.4
 */