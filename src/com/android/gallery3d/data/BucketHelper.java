package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.util.Log;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

class BucketHelper
{
  private static final String[] PROJECTION_BUCKET = { "bucket_id", "media_type", "bucket_display_name" };
  private static final String[] PROJECTION_BUCKET_IN_ONE_TABLE = { "bucket_id", "MAX(datetaken)", "bucket_display_name" };

  public static String getBucketName(ContentResolver paramContentResolver, int paramInt)
  {
    if (ApiHelper.HAS_MEDIA_PROVIDER_FILES_TABLE)
    {
      String str3 = getBucketNameInTable(paramContentResolver, getFilesContentUri(), paramInt);
      if (str3 == null)
        return "";
      return str3;
    }
    String str1 = getBucketNameInTable(paramContentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, paramInt);
    if (str1 != null)
      return str1;
    String str2 = getBucketNameInTable(paramContentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, paramInt);
    if (str2 == null)
      return "";
    return str2;
  }

  private static String getBucketNameInTable(ContentResolver paramContentResolver, Uri paramUri, int paramInt)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(paramInt);
    Cursor localCursor = paramContentResolver.query(paramUri.buildUpon().appendQueryParameter("limit", "1").build(), PROJECTION_BUCKET_IN_ONE_TABLE, "bucket_id = ?", arrayOfString, null);
    if (localCursor != null);
    try
    {
      if (localCursor.moveToNext())
      {
        String str = localCursor.getString(2);
        return str;
      }
      return null;
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  @TargetApi(11)
  private static Uri getFilesContentUri()
  {
    return MediaStore.Files.getContentUri("external");
  }

  public static BucketEntry[] loadBucketEntries(ThreadPool.JobContext paramJobContext, ContentResolver paramContentResolver, int paramInt)
  {
    if (ApiHelper.HAS_MEDIA_PROVIDER_FILES_TABLE)
      return loadBucketEntriesFromFilesTable(paramJobContext, paramContentResolver, paramInt);
    return loadBucketEntriesFromImagesAndVideoTable(paramJobContext, paramContentResolver, paramInt);
  }

  private static BucketEntry[] loadBucketEntriesFromFilesTable(ThreadPool.JobContext paramJobContext, ContentResolver paramContentResolver, int paramInt)
  {
    Uri localUri = getFilesContentUri();
    Cursor localCursor = paramContentResolver.query(localUri, PROJECTION_BUCKET, "1) GROUP BY 1,(2", null, "MAX(datetaken) DESC");
    if (localCursor == null)
    {
      Log.w("BucketHelper", "cannot open local database: " + localUri);
      return new BucketEntry[0];
    }
    ArrayList localArrayList = new ArrayList();
    int i = paramInt & 0x2;
    int j = 0;
    if (i != 0)
      j = 0x0 | 0x2;
    if ((paramInt & 0x4) != 0)
      j |= 8;
    try
    {
      boolean bool;
      do
      {
        if (!localCursor.moveToNext())
          break label183;
        if ((j & 1 << localCursor.getInt(1)) != 0)
        {
          BucketEntry localBucketEntry = new BucketEntry(localCursor.getInt(0), localCursor.getString(2));
          if (!localArrayList.contains(localBucketEntry))
            localArrayList.add(localBucketEntry);
        }
        bool = paramJobContext.isCancelled();
      }
      while (!bool);
      return null;
      label183: return (BucketEntry[])localArrayList.toArray(new BucketEntry[localArrayList.size()]);
    }
    finally
    {
      Utils.closeSilently(localCursor);
    }
  }

  private static BucketEntry[] loadBucketEntriesFromImagesAndVideoTable(ThreadPool.JobContext paramJobContext, ContentResolver paramContentResolver, int paramInt)
  {
    HashMap localHashMap = new HashMap(64);
    if ((paramInt & 0x2) != 0)
      updateBucketEntriesFromTable(paramJobContext, paramContentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localHashMap);
    if ((paramInt & 0x4) != 0)
      updateBucketEntriesFromTable(paramJobContext, paramContentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localHashMap);
    BucketEntry[] arrayOfBucketEntry = (BucketEntry[])localHashMap.values().toArray(new BucketEntry[localHashMap.size()]);
    Arrays.sort(arrayOfBucketEntry, new Comparator()
    {
      public int compare(BucketHelper.BucketEntry paramBucketEntry1, BucketHelper.BucketEntry paramBucketEntry2)
      {
        return paramBucketEntry2.dateTaken - paramBucketEntry1.dateTaken;
      }
    });
    return arrayOfBucketEntry;
  }

  private static void updateBucketEntriesFromTable(ThreadPool.JobContext paramJobContext, ContentResolver paramContentResolver, Uri paramUri, HashMap<Integer, BucketEntry> paramHashMap)
  {
    Cursor localCursor = paramContentResolver.query(paramUri, PROJECTION_BUCKET_IN_ONE_TABLE, "1) GROUP BY (1", null, null);
    if (localCursor == null)
    {
      Log.w("BucketHelper", "cannot open media database: " + paramUri);
      return;
    }
    while (true)
    {
      int j;
      BucketEntry localBucketEntry1;
      BucketEntry localBucketEntry2;
      try
      {
        if (!localCursor.moveToNext())
          break label163;
        int i = localCursor.getInt(0);
        j = localCursor.getInt(1);
        localBucketEntry1 = (BucketEntry)paramHashMap.get(Integer.valueOf(i));
        if (localBucketEntry1 != null)
          break label145;
        localBucketEntry2 = new BucketEntry(i, localCursor.getString(2));
        paramHashMap.put(Integer.valueOf(i), localBucketEntry2);
      }
      finally
      {
        Utils.closeSilently(localCursor);
      }
      label145: localBucketEntry1.dateTaken = Math.max(localBucketEntry1.dateTaken, j);
    }
    label163: Utils.closeSilently(localCursor);
  }

  public static class BucketEntry
  {
    public int bucketId;
    public String bucketName;
    public int dateTaken;

    public BucketEntry(int paramInt, String paramString)
    {
      this.bucketId = paramInt;
      this.bucketName = Utils.ensureNotNull(paramString);
    }

    public boolean equals(Object paramObject)
    {
      if (!paramObject instanceof BucketEntry);
      BucketEntry localBucketEntry;
      do
      {
        return false;
        localBucketEntry = (BucketEntry)paramObject;
      }
      while (this.bucketId != localBucketEntry.bucketId);
      return true;
    }

    public int hashCode()
    {
      return this.bucketId;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.BucketHelper
 * JD-Core Version:    0.5.4
 */