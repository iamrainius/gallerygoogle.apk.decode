package com.android.gallery3d.data;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import com.android.gallery3d.app.GalleryApp;
import com.android.gallery3d.app.PanoramaMetadataSupport;
import com.android.gallery3d.app.StitchingProgressManager;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.android.gallery3d.util.UpdateHelper;
import java.io.File;
import java.io.IOException;

public class LocalImage extends LocalMediaItem
{
  static final Path ITEM_PATH = Path.fromString("/local/image/item");
  static final String[] PROJECTION = { "_id", "title", "mime_type", "latitude", "longitude", "datetaken", "date_added", "date_modified", "_data", "orientation", "bucket_id", "_size", "0", "0" };
  private final GalleryApp mApplication;
  private PanoramaMetadataSupport mPanoramaMetadata = new PanoramaMetadataSupport(this);
  public int rotation;

  static
  {
    updateWidthAndHeightProjection();
  }

  public LocalImage(Path paramPath, GalleryApp paramGalleryApp, int paramInt)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    Cursor localCursor = LocalAlbum.getItemCursor(this.mApplication.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION, paramInt);
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

  public LocalImage(Path paramPath, GalleryApp paramGalleryApp, Cursor paramCursor)
  {
    super(paramPath, nextVersionNumber());
    this.mApplication = paramGalleryApp;
    loadFromCursor(paramCursor);
  }

  private static String getExifOrientation(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new AssertionError("invalid: " + paramInt);
    case 0:
      return String.valueOf(1);
    case 90:
      return String.valueOf(6);
    case 180:
      return String.valueOf(3);
    case 270:
    }
    return String.valueOf(8);
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
    this.rotation = paramCursor.getInt(9);
    this.bucketId = paramCursor.getInt(10);
    this.fileSize = paramCursor.getLong(11);
    this.width = paramCursor.getInt(12);
    this.height = paramCursor.getInt(13);
  }

  @TargetApi(16)
  private static void updateWidthAndHeightProjection()
  {
    if (!ApiHelper.HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT)
      return;
    PROJECTION[12] = "width";
    PROJECTION[13] = "height";
  }

  public void clearCachedPanoramaSupport()
  {
    this.mPanoramaMetadata.clearCachedValues();
  }

  public void delete()
  {
    GalleryUtils.assertNotInRenderThread();
    Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    ContentResolver localContentResolver = this.mApplication.getContentResolver();
    String[] arrayOfString = new String[1];
    arrayOfString[0] = String.valueOf(this.id);
    localContentResolver.delete(localUri, "_id=?", arrayOfString);
  }

  public Uri getContentUri()
  {
    return MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(String.valueOf(this.id)).build();
  }

  public MediaDetails getDetails()
  {
    MediaDetails localMediaDetails = super.getDetails();
    localMediaDetails.addDetail(7, Integer.valueOf(this.rotation));
    if ("image/jpeg".equals(this.mimeType))
      MediaDetails.extractExifInfo(localMediaDetails, this.filePath);
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
    return 2;
  }

  public void getPanoramaSupport(MediaObject.PanoramaSupportCallback paramPanoramaSupportCallback)
  {
    this.mPanoramaMetadata.getPanoramaSupport(this.mApplication, paramPanoramaSupportCallback);
  }

  public int getRotation()
  {
    return this.rotation;
  }

  public int getSupportedOperations()
  {
    StitchingProgressManager localStitchingProgressManager = this.mApplication.getStitchingProgressManager();
    int i;
    if ((localStitchingProgressManager != null) && (localStitchingProgressManager.getProgress(getContentUri()) != null))
      i = 0;
    do
    {
      return i;
      i = 1581;
      if (BitmapUtils.isSupportedByRegionDecoder(this.mimeType))
        i |= 64;
      if (!BitmapUtils.isRotationSupported(this.mimeType))
        continue;
      i |= 2;
    }
    while (!GalleryUtils.isValidLocation(this.latitude, this.longitude));
    return i | 0x10;
  }

  public int getWidth()
  {
    return this.width;
  }

  public ThreadPool.Job<Bitmap> requestImage(int paramInt)
  {
    return new LocalImageRequest(this.mApplication, this.mPath, paramInt, this.filePath);
  }

  public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
  {
    return new LocalLargeImageRequest(this.filePath);
  }

  public void rotate(int paramInt)
  {
    GalleryUtils.assertNotInRenderThread();
    Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    ContentValues localContentValues = new ContentValues();
    int i = (paramInt + this.rotation) % 360;
    if (i < 0)
      i += 360;
    if (this.mimeType.equalsIgnoreCase("image/jpeg"));
    try
    {
      ExifInterface localExifInterface = new ExifInterface(this.filePath);
      localExifInterface.setAttribute("Orientation", getExifOrientation(i));
      localExifInterface.saveAttributes();
      this.fileSize = new File(this.filePath).length();
      localContentValues.put("_size", Long.valueOf(this.fileSize));
      localContentValues.put("orientation", Integer.valueOf(i));
      ContentResolver localContentResolver = this.mApplication.getContentResolver();
      String[] arrayOfString = new String[1];
      arrayOfString[0] = String.valueOf(this.id);
      localContentResolver.update(localUri, localContentValues, "_id=?", arrayOfString);
      return;
    }
    catch (IOException localIOException)
    {
      Log.w("LocalImage", "cannot set exif data: " + this.filePath);
    }
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
    this.rotation = localUpdateHelper.update(this.rotation, paramCursor.getInt(9));
    this.bucketId = localUpdateHelper.update(this.bucketId, paramCursor.getInt(10));
    this.fileSize = localUpdateHelper.update(this.fileSize, paramCursor.getLong(11));
    this.width = localUpdateHelper.update(this.width, paramCursor.getInt(12));
    this.height = localUpdateHelper.update(this.height, paramCursor.getInt(13));
    return localUpdateHelper.isUpdated();
  }

  public static class LocalImageRequest extends ImageCacheRequest
  {
    private String mLocalFilePath;

    LocalImageRequest(GalleryApp paramGalleryApp, Path paramPath, int paramInt, String paramString)
    {
      super(paramGalleryApp, paramPath, paramInt, MediaItem.getTargetSize(paramInt));
      this.mLocalFilePath = paramString;
    }

    // ERROR //
    public Bitmap onDecodeOriginal(ThreadPool.JobContext paramJobContext, int paramInt)
    {
      // Byte code:
      //   0: new 25	android/graphics/BitmapFactory$Options
      //   3: dup
      //   4: invokespecial 28	android/graphics/BitmapFactory$Options:<init>	()V
      //   7: astore_3
      //   8: aload_3
      //   9: getstatic 34	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   12: putfield 37	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   15: iload_2
      //   16: invokestatic 14	com/android/gallery3d/data/MediaItem:getTargetSize	(I)I
      //   19: istore 4
      //   21: iload_2
      //   22: iconst_2
      //   23: if_icmpne +77 -> 100
      //   26: new 39	android/media/ExifInterface
      //   29: dup
      //   30: aload_0
      //   31: getfield 19	com/android/gallery3d/data/LocalImage$LocalImageRequest:mLocalFilePath	Ljava/lang/String;
      //   34: invokespecial 42	android/media/ExifInterface:<init>	(Ljava/lang/String;)V
      //   37: astore 5
      //   39: aconst_null
      //   40: astore 6
      //   42: aload 5
      //   44: ifnull +14 -> 58
      //   47: aload 5
      //   49: invokevirtual 46	android/media/ExifInterface:getThumbnail	()[B
      //   52: astore 10
      //   54: aload 10
      //   56: astore 6
      //   58: aload 6
      //   60: ifnull +40 -> 100
      //   63: aload_1
      //   64: aload 6
      //   66: aload_3
      //   67: iload 4
      //   69: invokestatic 52	com/android/gallery3d/data/DecodeUtils:decodeIfBigEnough	(Lcom/android/gallery3d/util/ThreadPool$JobContext;[BLandroid/graphics/BitmapFactory$Options;I)Landroid/graphics/Bitmap;
      //   72: astore 7
      //   74: aload 7
      //   76: ifnull +24 -> 100
      //   79: aload 7
      //   81: areturn
      //   82: astore 8
      //   84: ldc 54
      //   86: ldc 56
      //   88: aload 8
      //   90: invokestatic 62	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   93: pop
      //   94: aconst_null
      //   95: astore 6
      //   97: goto -39 -> 58
      //   100: aload_1
      //   101: aload_0
      //   102: getfield 19	com/android/gallery3d/data/LocalImage$LocalImageRequest:mLocalFilePath	Ljava/lang/String;
      //   105: aload_3
      //   106: iload 4
      //   108: iload_2
      //   109: invokestatic 66	com/android/gallery3d/data/DecodeUtils:decodeThumbnail	(Lcom/android/gallery3d/util/ThreadPool$JobContext;Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;II)Landroid/graphics/Bitmap;
      //   112: areturn
      //   113: astore 8
      //   115: goto -31 -> 84
      //
      // Exception table:
      //   from	to	target	type
      //   26	39	82	java/lang/Throwable
      //   47	54	113	java/lang/Throwable
    }
  }

  public static class LocalLargeImageRequest
    implements ThreadPool.Job<BitmapRegionDecoder>
  {
    String mLocalFilePath;

    public LocalLargeImageRequest(String paramString)
    {
      this.mLocalFilePath = paramString;
    }

    public BitmapRegionDecoder run(ThreadPool.JobContext paramJobContext)
    {
      return DecodeUtils.createBitmapRegionDecoder(paramJobContext, this.mLocalFilePath, false);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.LocalImage
 * JD-Core Version:    0.5.4
 */