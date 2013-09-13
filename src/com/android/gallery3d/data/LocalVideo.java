package com.android.gallery3d.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.MediaStore.Video.Media;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.android.gallery3d.util.UpdateHelper;

public class LocalVideo extends LocalMediaItem
{
  static final Path ITEM_PATH = Path.fromString("/local/video/item");
  static final String[] PROJECTION = { "_id", "title", "mime_type", "latitude", "longitude", "datetaken", "date_added", "date_modified", "_data", "duration", "bucket_id", "_size", "resolution" };
  public int durationInSec;
  private final GalleryApp mApplication;

  public LocalVideo(Path paramPath, GalleryApp paramGalleryApp, int paramInt)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    Cursor localCursor = LocalAlbum.getItemCursor(this.mApplication.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, PROJECTION, paramInt);
    if (localCursor == null)
      throw new RuntimeException("cannot get cursor for: " + paramPath);
    try
    {
      if (localCursor.moveToNext())
      {
        loadFromCursor(localCursor);
        localCursor.close();
      }
      throw new RuntimeException("cannot find data for: " + paramPath);
    }
    finally
    {
      localCursor.close();
    }
  }

  public LocalVideo(Path paramPath, GalleryApp paramGalleryApp, Cursor paramCursor)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    loadFromCursor(paramCursor);
  }

  private void loadFromCursor(Cursor paramCursor)
  {
    this.id = paramCursor.getInt(0);
    this.caption = paramCursor.getString(1);
    this.mimeType = paramCursor.getString(2);
    this.latitude = paramCursor.getDouble(3);
    this.longitude = paramCursor.getDouble(4);
    this.dateTakenInMs = paramCursor.getLong(5);
    this.dateAddedInSec = paramCursor.getLong(6);
    this.dateModifiedInSec = paramCursor.getLong(7);
    this.filePath = paramCursor.getString(8);
    this.durationInSec = (paramCursor.getInt(9) / 1000);
    this.bucketId = paramCursor.getInt(10);
    this.fileSize = paramCursor.getLong(11);
    parseResolution(paramCursor.getString(12));
  }

  private void parseResolution(String paramString)
  {
    if (paramString == null);
    int i;
    do
    {
      return;
      i = paramString.indexOf('x');
    }
    while (i == -1);
    try
    {
      int j = Integer.parseInt(paramString.substring(0, i));
      int k = Integer.parseInt(paramString.substring(i + 1));
      this.width = j;
      this.height = k;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("LocalVideo", localThrowable);
    }
  }

  public void delete()
  {
    GalleryUtils.assertNotInRenderThread();
    Uri localUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    ContentResolver localContentResolver = this.mApplication.getContentResolver();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(this.id);
    localContentResolver.delete(localUri, "_id=?", arrayOfString);
  }

  public Uri getContentUri()
  {
    return MediaStore.Video.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(String.valueOf(this.id)).build();
  }

  public MediaDetails getDetails()
  {
    MediaDetails localMediaDetails = super.getDetails();
    if (this.durationInSec > 0)
      localMediaDetails.addDetail(8, GalleryUtils.formatDuration(this.mApplication.getAndroidContext(), this.durationInSec));
    return localMediaDetails;
  }

  public String getFilePath()
  {
    return this.filePath;
  }

  public int getHeight()
  {
    return this.height;
  }

  public int getMediaType()
  {
    return 4;
  }

  public Uri getPlayUri()
  {
    return getContentUri();
  }

  public int getSupportedOperations()
  {
    return 5253;
  }

  public int getWidth()
  {
    return this.width;
  }

  public ThreadPool.Job<Bitmap> requestImage(int paramInt)
  {
    return new LocalVideoRequest(this.mApplication, getPath(), paramInt, this.filePath);
  }

  public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
  {
    throw new UnsupportedOperationException("Cannot regquest a large image to a local video!");
  }

  public void rotate(int paramInt)
  {
  }

  protected boolean updateFromCursor(Cursor paramCursor)
  {
    UpdateHelper localUpdateHelper = new UpdateHelper();
    this.id = localUpdateHelper.update(this.id, paramCursor.getInt(0));
    this.caption = ((String)localUpdateHelper.update(this.caption, paramCursor.getString(1)));
    this.mimeType = ((String)localUpdateHelper.update(this.mimeType, paramCursor.getString(2)));
    this.latitude = localUpdateHelper.update(this.latitude, paramCursor.getDouble(3));
    this.longitude = localUpdateHelper.update(this.longitude, paramCursor.getDouble(4));
    this.dateTakenInMs = localUpdateHelper.update(this.dateTakenInMs, paramCursor.getLong(5));
    this.dateAddedInSec = localUpdateHelper.update(this.dateAddedInSec, paramCursor.getLong(6));
    this.dateModifiedInSec = localUpdateHelper.update(this.dateModifiedInSec, paramCursor.getLong(7));
    this.filePath = ((String)localUpdateHelper.update(this.filePath, paramCursor.getString(8)));
    this.durationInSec = localUpdateHelper.update(this.durationInSec, paramCursor.getInt(9) / 1000);
    this.bucketId = localUpdateHelper.update(this.bucketId, paramCursor.getInt(10));
    this.fileSize = localUpdateHelper.update(this.fileSize, paramCursor.getLong(11));
    return localUpdateHelper.isUpdated();
  }

  public static class LocalVideoRequest extends ImageCacheRequest
  {
    private String mLocalFilePath;

    LocalVideoRequest(GalleryApp paramGalleryApp, Path paramPath, int paramInt, String paramString)
    {
      super(paramGalleryApp, paramPath, paramInt, MediaItem.getTargetSize(paramInt));
      this.mLocalFilePath = paramString;
    }

    public Bitmap onDecodeOriginal(ThreadPool.JobContext paramJobContext, int paramInt)
    {
      Bitmap localBitmap = BitmapUtils.createVideoThumbnail(this.mLocalFilePath);
      if ((localBitmap == null) || (paramJobContext.isCancelled()))
        localBitmap = null;
      return localBitmap;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocalVideo
 * JD-Core Version:    0.5.4
 */