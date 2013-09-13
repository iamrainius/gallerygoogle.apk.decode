package com.android.gallery3d.picasasource;

import android.content.ContentProviderClient;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.data.BytesBufferPool;
import com.android.gallery3d.data.BytesBufferPool.BytesBuffer;
import com.android.gallery3d.data.DecodeUtils;
import com.android.gallery3d.data.Face;
import com.android.gallery3d.data.Log;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.data.MediaDetails.FlashState;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.provider.GalleryProvider;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import com.google.android.picasastore.PicasaStoreFacade;
import com.google.android.picasasync.PhotoEntry;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

class PicasaImage extends MediaItem
{
  public static final File DOWNLOAD_DIR = new File(Environment.getExternalStorageDirectory(), "download");
  public static final Path ITEM_PATH = Path.fromString("/picasa/item");
  private PhotoEntry mData;
  private boolean mIsVideo;
  private final PicasaSource mSource;

  public PicasaImage(Path paramPath, PicasaSource paramPicasaSource, PhotoEntry paramPhotoEntry)
  {
    super(paramPath, nextVersionNumber());
    this.mSource = paramPicasaSource;
    this.mData = paramPhotoEntry;
    this.mIsVideo = this.mData.contentType.startsWith("video/");
  }

  public static PicasaImage find(Path paramPath, PicasaSource paramPicasaSource, long paramLong)
  {
    PhotoEntry localPhotoEntry = PicasaAlbum.getPhotoEntry(paramPicasaSource, paramLong);
    if (localPhotoEntry == null)
      return null;
    return new PicasaImage(paramPath, paramPicasaSource, localPhotoEntry);
  }

  public long getAlbumId()
  {
    return this.mData.albumId;
  }

  public Uri getContentUri()
  {
    return GalleryProvider.getUriFor((Context)this.mSource.getApplication(), this.mPath);
  }

  public long getDateInMs()
  {
    return this.mData.dateTaken;
  }

  public MediaDetails getDetails()
  {
    int i = 1;
    MediaDetails localMediaDetails = super.getDetails();
    localMediaDetails.addDetail(i, this.mData.title);
    localMediaDetails.addDetail(3, DateFormat.getDateTimeInstance().format(new Date(this.mData.dateUpdated)));
    localMediaDetails.addDetail(5, Integer.valueOf(this.mData.width));
    localMediaDetails.addDetail(6, Integer.valueOf(this.mData.height));
    localMediaDetails.addDetail(7, Integer.valueOf(this.mData.rotation));
    localMediaDetails.addDetail(10, Long.valueOf(this.mData.size));
    if ((this.mData.summary != null) && (this.mData.summary.length() != 0))
      localMediaDetails.addDetail(2, this.mData.summary);
    if (GalleryUtils.isValidLocation(this.mData.latitude, this.mData.longitude))
    {
      double[] arrayOfDouble = new double[2];
      arrayOfDouble[0] = this.mData.latitude;
      arrayOfDouble[i] = this.mData.longitude;
      localMediaDetails.addDetail(4, arrayOfDouble);
    }
    if (!Utils.isNullOrEmpty(this.mData.exifMake))
      localMediaDetails.addDetail(100, this.mData.exifMake);
    if (!Utils.isNullOrEmpty(this.mData.exifModel))
      localMediaDetails.addDetail(101, this.mData.exifModel);
    if (this.mData.exifFlash != 0)
      if (this.mData.exifFlash != i)
        break label400;
    while (true)
    {
      localMediaDetails.addDetail(102, new MediaDetails.FlashState(i));
      if (this.mData.exifFocalLength > 0.0F)
      {
        localMediaDetails.addDetail(103, String.valueOf(this.mData.exifFocalLength));
        localMediaDetails.setUnit(103, 2131362277);
      }
      if (this.mData.exifFstop > 0.0F)
        localMediaDetails.addDetail(105, String.valueOf(this.mData.exifFstop));
      if (this.mData.exifExposure > 0.0F)
        localMediaDetails.addDetail(107, String.valueOf(this.mData.exifExposure));
      if (this.mData.exifIso > 0)
        localMediaDetails.addDetail(108, String.valueOf(this.mData.exifIso));
      return localMediaDetails;
      label400: i = 0;
    }
  }

  public Face[] getFaces()
  {
    String str1 = this.mData.faceNames;
    String str2 = this.mData.faceIds;
    String str3 = this.mData.faceRects;
    if ((str1 == null) || (str2 == null) || (str3 == null))
      return null;
    ArrayList localArrayList;
    try
    {
      localArrayList = new ArrayList();
      StringTokenizer localStringTokenizer1 = new StringTokenizer(str1, ",");
      StringTokenizer localStringTokenizer2 = new StringTokenizer(str2, ",");
      StringTokenizer localStringTokenizer3 = new StringTokenizer(str3, ",");
      String str4;
      String str5;
      String str6;
      do
      {
        if ((!localStringTokenizer1.hasMoreTokens()) || (!localStringTokenizer2.hasMoreElements()) || (!localStringTokenizer3.hasMoreElements()))
          break label187;
        str4 = localStringTokenizer1.nextToken().trim();
        str5 = localStringTokenizer2.nextToken().trim();
        str6 = localStringTokenizer3.nextToken().trim();
      }
      while ((str4 == null) || (str5 == null) || (str6 == null));
      localArrayList.add(new Face(str4, str5, str6));
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.w("PicasaImage", localRuntimeException);
      return null;
    }
    label187: Face[] arrayOfFace = (Face[])localArrayList.toArray(new Face[localArrayList.size()]);
    return arrayOfFace;
  }

  public int getFullImageRotation()
  {
    return this.mData.rotation;
  }

  public int getHeight()
  {
    return this.mData.height;
  }

  public void getLatLong(double[] paramArrayOfDouble)
  {
    paramArrayOfDouble[0] = this.mData.latitude;
    paramArrayOfDouble[1] = this.mData.longitude;
  }

  public int getMediaType()
  {
    if (this.mIsVideo)
      return 4;
    return 2;
  }

  public String getMimeType()
  {
    return this.mData.contentType;
  }

  public String getName()
  {
    return this.mData.title;
  }

  public PhotoEntry getPhotoEntry()
  {
    return this.mData;
  }

  public Uri getPlayUri()
  {
    File localFile = PicasaStoreFacade.getCacheFile(this.mData.id, ".full");
    if (localFile != null)
      return Uri.fromFile(localFile);
    return Uri.parse(this.mData.contentUrl);
  }

  public long getSize()
  {
    return this.mData.size;
  }

  public int getSupportedOperations()
  {
    if (this.mIsVideo);
    for (int i = 128; ; i = 616)
    {
      int j = i | 0x404;
      if (!BitmapUtils.isSupportedByRegionDecoder(this.mData.contentType))
        j &= -65;
      if (GalleryUtils.isValidLocation(this.mData.latitude, this.mData.longitude))
        j |= 16;
      return j;
    }
  }

  public String[] getTags()
  {
    String str = this.mData.keywords;
    if (str == null)
      return null;
    ArrayList localArrayList = new ArrayList();
    StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
    while (localStringTokenizer.hasMoreTokens())
      localArrayList.add(localStringTokenizer.nextToken().trim());
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }

  public int getWidth()
  {
    return this.mData.width;
  }

  public ThreadPool.Job<Bitmap> requestImage(int paramInt)
  {
    if (paramInt == 2);
    for (String str = "thumbnail"; ; str = "screennail")
      return new PicasaBitmapJob(this.mSource.getPicasaStoreFacade().getPhotoUri(this.mData.id, str, this.mData.screennailUrl), paramInt);
  }

  public ThreadPool.Job<BitmapRegionDecoder> requestLargeImage()
  {
    return new PicasaLargeImageRequest(this.mSource.getPicasaStoreFacade().getPhotoUri(this.mData.id, "full", this.mData.contentUrl));
  }

  protected void updateContent(PhotoEntry paramPhotoEntry)
  {
    if (this.mData.equals(Utils.checkNotNull(paramPhotoEntry)))
      return;
    this.mData = paramPhotoEntry;
    this.mDataVersion = nextVersionNumber();
  }

  private class PicasaBitmapJob
    implements ThreadPool.Job<Bitmap>
  {
    private Uri mPhotoUri;
    private int mType;

    public PicasaBitmapJob(Uri paramInt, int arg3)
    {
      this.mPhotoUri = paramInt;
      int i;
      this.mType = i;
    }

    public Bitmap run(ThreadPool.JobContext paramJobContext)
    {
      ParcelFileDescriptor localParcelFileDescriptor = null;
      BytesBufferPool.BytesBuffer localBytesBuffer = MediaItem.getBytesBufferPool().get();
      Object localObject2;
      try
      {
        boolean bool = paramJobContext.isCancelled();
        if (bool)
          return null;
        localParcelFileDescriptor = PicasaImage.this.mSource.getStoreProvider().openFile(this.mPhotoUri, "r");
        localBytesBuffer.readFrom(paramJobContext, localParcelFileDescriptor.getFileDescriptor());
        if (this.mType == 2)
        {
          localObject2 = MediaItem.getMicroThumbPool();
          Bitmap localBitmap = DecodeUtils.decode(paramJobContext, localBytesBuffer.data, localBytesBuffer.offset, localBytesBuffer.length, null, (BitmapPool)localObject2);
          return localBitmap;
        }
      }
      catch (Throwable localThrowable)
      {
        BitmapPool localBitmapPool;
        Log.w("PicasaImage", "fail to decode bitmap", localThrowable);
        return null;
      }
      finally
      {
        Utils.closeSilently(localParcelFileDescriptor);
        MediaItem.getBytesBufferPool().recycle(localBytesBuffer);
      }
    }
  }

  private class PicasaLargeImageRequest
    implements ThreadPool.Job<BitmapRegionDecoder>
  {
    private Uri mPhotoUri;

    PicasaLargeImageRequest(Uri arg2)
    {
      Object localObject;
      this.mPhotoUri = localObject;
    }

    // ERROR //
    public BitmapRegionDecoder run(ThreadPool.JobContext paramJobContext)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokeinterface 30 1 0
      //   6: ifeq +5 -> 11
      //   9: aconst_null
      //   10: areturn
      //   11: aconst_null
      //   12: astore_2
      //   13: new 32	com/android/gallery3d/picasasource/CancelableInputStream
      //   16: dup
      //   17: aload_0
      //   18: getfield 15	com/android/gallery3d/picasasource/PicasaImage$PicasaLargeImageRequest:this$0	Lcom/android/gallery3d/picasasource/PicasaImage;
      //   21: invokestatic 38	com/android/gallery3d/picasasource/PicasaImage:access$000	(Lcom/android/gallery3d/picasasource/PicasaImage;)Lcom/android/gallery3d/picasasource/PicasaSource;
      //   24: invokevirtual 44	com/android/gallery3d/picasasource/PicasaSource:getStoreProvider	()Landroid/content/ContentProviderClient;
      //   27: aload_0
      //   28: getfield 20	com/android/gallery3d/picasasource/PicasaImage$PicasaLargeImageRequest:mPhotoUri	Landroid/net/Uri;
      //   31: invokespecial 47	com/android/gallery3d/picasasource/CancelableInputStream:<init>	(Landroid/content/ContentProviderClient;Landroid/net/Uri;)V
      //   34: astore_3
      //   35: aload_1
      //   36: new 49	com/android/gallery3d/picasasource/PicasaImage$PicasaLargeImageRequest$1
      //   39: dup
      //   40: aload_0
      //   41: aload_3
      //   42: invokespecial 52	com/android/gallery3d/picasasource/PicasaImage$PicasaLargeImageRequest$1:<init>	(Lcom/android/gallery3d/picasasource/PicasaImage$PicasaLargeImageRequest;Lcom/android/gallery3d/picasasource/CancelableInputStream;)V
      //   45: invokeinterface 56 2 0
      //   50: aload_1
      //   51: aload_3
      //   52: iconst_1
      //   53: invokestatic 62	com/android/gallery3d/data/DecodeUtils:createBitmapRegionDecoder	(Lcom/android/gallery3d/util/ThreadPool$JobContext;Ljava/io/InputStream;Z)Landroid/graphics/BitmapRegionDecoder;
      //   56: astore 7
      //   58: aload_1
      //   59: invokeinterface 30 1 0
      //   64: istore 8
      //   66: iload 8
      //   68: ifeq +6 -> 74
      //   71: aconst_null
      //   72: astore 7
      //   74: aload_1
      //   75: aconst_null
      //   76: invokeinterface 56 2 0
      //   81: aload_3
      //   82: invokestatic 68	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   85: aload 7
      //   87: areturn
      //   88: astore 4
      //   90: ldc 70
      //   92: ldc 72
      //   94: aload 4
      //   96: invokestatic 78	com/android/gallery3d/data/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   99: pop
      //   100: aload_1
      //   101: aconst_null
      //   102: invokeinterface 56 2 0
      //   107: aload_2
      //   108: invokestatic 68	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   111: aconst_null
      //   112: areturn
      //   113: astore 5
      //   115: aload_1
      //   116: aconst_null
      //   117: invokeinterface 56 2 0
      //   122: aload_2
      //   123: invokestatic 68	com/android/gallery3d/common/Utils:closeSilently	(Ljava/io/Closeable;)V
      //   126: aload 5
      //   128: athrow
      //   129: astore 5
      //   131: aload_3
      //   132: astore_2
      //   133: goto -18 -> 115
      //   136: astore 4
      //   138: aload_3
      //   139: astore_2
      //   140: goto -50 -> 90
      //
      // Exception table:
      //   from	to	target	type
      //   13	35	88	java/lang/Throwable
      //   13	35	113	finally
      //   90	100	113	finally
      //   35	66	129	finally
      //   35	66	136	java/lang/Throwable
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.picasasource.PicasaImage
 * JD-Core Version:    0.5.4
 */