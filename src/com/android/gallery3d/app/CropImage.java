package com.android.gallery3d.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.LocalImage;
import com.android.gallery3d.data.LocalMediaItem;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.exif.ExifData;
import com.android.gallery3d.exif.ExifOutputStream;
import com.android.gallery3d.exif.ExifTag;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.ui.BitmapScreenNail;
import com.android.gallery3d.ui.BitmapTileProvider;
import com.android.gallery3d.ui.CropView;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.ui.TileImageViewAdapter;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.InterruptableOutputStream;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CropImage extends AbstractGalleryActivity
{
  public static final File DOWNLOAD_BUCKET = new File(Environment.getExternalStorageDirectory(), "download");
  private Bitmap mBitmap;
  private Bitmap mBitmapInIntent;
  private BitmapScreenNail mBitmapScreenNail;
  private BitmapTileProvider mBitmapTileProvider;
  private CropView mCropView;
  private boolean mDoFaceDetection = true;
  private Future<Bitmap> mLoadBitmapTask;
  private Future<BitmapRegionDecoder> mLoadTask;
  private Handler mMainHandler;
  private MediaItem mMediaItem;
  private ProgressDialog mProgressDialog;
  private BitmapRegionDecoder mRegionDecoder;
  private Future<Intent> mSaveTask;
  private int mState = 0;
  private boolean mUseRegionDecoder = false;

  private void changeExifData(ExifData paramExifData, int paramInt1, int paramInt2)
  {
    paramExifData.addTag(256).setValue(paramInt1);
    paramExifData.addTag(257).setValue(paramInt2);
    paramExifData.addTag(305).setValue("Android Gallery");
    paramExifData.addTag(306).setTimeValue(System.currentTimeMillis());
    paramExifData.removeThumbnailData();
  }

  private Bitmap.CompressFormat convertExtensionToCompressFormat(String paramString)
  {
    if (paramString.equals("png"))
      return Bitmap.CompressFormat.PNG;
    return Bitmap.CompressFormat.JPEG;
  }

  public static String determineCompressFormat(MediaObject paramMediaObject)
  {
    String str1 = "JPEG";
    if (paramMediaObject instanceof MediaItem)
    {
      String str2 = ((MediaItem)paramMediaObject).getMimeType();
      if ((str2.contains("png")) || (str2.contains("gif")))
        str1 = "PNG";
    }
    return str1;
  }

  private void dismissProgressDialogIfShown()
  {
    if (this.mProgressDialog == null)
      return;
    this.mProgressDialog.dismiss();
    this.mProgressDialog = null;
  }

  private void drawInTiles(Canvas paramCanvas, BitmapRegionDecoder paramBitmapRegionDecoder, Rect paramRect1, Rect paramRect2, int paramInt)
  {
    int i = paramInt * 512;
    Rect localRect = new Rect();
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    localOptions.inSampleSize = paramInt;
    paramCanvas.translate(paramRect2.left, paramRect2.top);
    paramCanvas.scale(paramInt * paramRect2.width() / paramRect1.width(), paramInt * paramRect2.height() / paramRect1.height());
    Paint localPaint = new Paint(2);
    int j = paramRect1.left;
    for (int k = 0; j < paramRect1.right; k += 512)
    {
      int l = paramRect1.top;
      int i1 = 0;
      while (l < paramRect1.bottom)
      {
        localRect.set(j, l, j + i, l + i);
        if (localRect.intersect(paramRect1))
          monitorenter;
        try
        {
          Bitmap localBitmap = paramBitmapRegionDecoder.decodeRegion(localRect, localOptions);
          monitorexit;
          paramCanvas.drawBitmap(localBitmap, k, i1, localPaint);
          localBitmap.recycle();
          l += i;
        }
        finally
        {
          monitorexit;
        }
      }
      j += i;
    }
  }

  private Bitmap getCroppedImage(Rect paramRect)
  {
    if ((paramRect.width() > 0) && (paramRect.height() > 0));
    int i;
    int j;
    float f1;
    float f2;
    Rect localRect;
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      Bundle localBundle = getIntent().getExtras();
      i = paramRect.width();
      j = paramRect.height();
      if (localBundle != null)
      {
        i = localBundle.getInt("outputX", i);
        j = localBundle.getInt("outputY", j);
      }
      if (i * j > 5000000)
      {
        float f3 = FloatMath.sqrt(5000000.0F / i / j);
        Log.w("CropImage", "scale down the cropped image: " + f3);
        i = Math.round(f3 * i);
        j = Math.round(f3 * j);
      }
      f1 = 1.0F;
      f2 = 1.0F;
      localRect = new Rect(0, 0, i, j);
      if ((localBundle == null) || (localBundle.getBoolean("scale", true)))
      {
        f1 = i / paramRect.width();
        f2 = j / paramRect.height();
        if ((localBundle == null) || (!localBundle.getBoolean("scaleUpIfNeeded", false)))
        {
          if (f1 > 1.0F)
            f1 = 1.0F;
          if (f2 > 1.0F)
            f2 = 1.0F;
        }
      }
      int k = Math.round(f1 * paramRect.width());
      int l = Math.round(f2 * paramRect.height());
      localRect.set(Math.round((i - k) / 2.0F), Math.round((j - l) / 2.0F), Math.round((i + k) / 2.0F), Math.round((j + l) / 2.0F));
      if (this.mBitmapInIntent == null)
        break;
      Bitmap localBitmap4 = this.mBitmapInIntent;
      Bitmap localBitmap5 = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
      new Canvas(localBitmap5).drawBitmap(localBitmap4, paramRect, localRect, null);
      return localBitmap5;
    }
    if (this.mUseRegionDecoder)
    {
      int i2 = this.mMediaItem.getFullImageRotation();
      rotateRectangle(paramRect, this.mCropView.getImageWidth(), this.mCropView.getImageHeight(), 360 - i2);
      rotateRectangle(localRect, i, j, 360 - i2);
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      int i3 = BitmapUtils.computeSampleSizeLarger(Math.max(f1, f2));
      localOptions.inSampleSize = i3;
      if ((paramRect.width() / i3 == localRect.width()) && (paramRect.height() / i3 == localRect.height()) && (i == localRect.width()) && (j == localRect.height()) && (i2 == 0))
        synchronized (this.mRegionDecoder)
        {
          Bitmap localBitmap3 = this.mRegionDecoder.decodeRegion(paramRect, localOptions);
          return localBitmap3;
        }
      Bitmap localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
      Canvas localCanvas2 = new Canvas(localBitmap2);
      rotateCanvas(localCanvas2, i, j, i2);
      drawInTiles(localCanvas2, this.mRegionDecoder, paramRect, localRect, i3);
      return localBitmap2;
    }
    int i1 = this.mMediaItem.getRotation();
    rotateRectangle(paramRect, this.mCropView.getImageWidth(), this.mCropView.getImageHeight(), 360 - i1);
    rotateRectangle(localRect, i, j, 360 - i1);
    Bitmap localBitmap1 = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
    Canvas localCanvas1 = new Canvas(localBitmap1);
    rotateCanvas(localCanvas1, i, j, i1);
    localCanvas1.drawBitmap(this.mBitmap, paramRect, localRect, new Paint(2));
    return localBitmap1;
  }

  // ERROR //
  private ExifData getExifData(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 395	java/io/FileInputStream
    //   5: dup
    //   6: aload_1
    //   7: invokespecial 397	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   10: astore_3
    //   11: new 399	com/android/gallery3d/exif/ExifReader
    //   14: dup
    //   15: invokespecial 400	com/android/gallery3d/exif/ExifReader:<init>	()V
    //   18: aload_3
    //   19: invokevirtual 404	com/android/gallery3d/exif/ExifReader:read	(Ljava/io/InputStream;)Lcom/android/gallery3d/exif/ExifData;
    //   22: astore 7
    //   24: aload_3
    //   25: invokestatic 410	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   28: aload 7
    //   30: areturn
    //   31: astore 4
    //   33: ldc_w 301
    //   36: ldc_w 412
    //   39: aload 4
    //   41: invokestatic 415	com/android/gallery3d/app/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   44: pop
    //   45: aload_2
    //   46: invokestatic 410	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   49: aconst_null
    //   50: areturn
    //   51: astore 5
    //   53: aload_2
    //   54: invokestatic 410	com/android/camera/Util:closeSilently	(Ljava/io/Closeable;)V
    //   57: aload 5
    //   59: athrow
    //   60: astore 5
    //   62: aload_3
    //   63: astore_2
    //   64: goto -11 -> 53
    //   67: astore 4
    //   69: aload_3
    //   70: astore_2
    //   71: goto -38 -> 33
    //
    // Exception table:
    //   from	to	target	type
    //   2	11	31	java/lang/Throwable
    //   2	11	51	finally
    //   33	45	51	finally
    //   11	24	60	finally
    //   11	24	67	java/lang/Throwable
  }

  private String getFileExtension()
  {
    String str1 = getIntent().getStringExtra("outputFormat");
    if (str1 == null);
    for (String str2 = determineCompressFormat(this.mMediaItem); ; str2 = str1)
    {
      String str3 = str2.toLowerCase();
      if ((!str3.equals("png")) && (!str3.equals("gif")))
        break;
      return "png";
    }
    return "jpg";
  }

  private MediaItem getMediaItemFromIntentData()
  {
    Uri localUri = getIntent().getData();
    DataManager localDataManager = getDataManager();
    Path localPath = localDataManager.findPathByUri(localUri, getIntent().getType());
    if (localPath == null)
    {
      Log.w("CropImage", "cannot get path for: " + localUri + ", or no data given");
      return null;
    }
    return (MediaItem)localDataManager.getMediaObject(localPath);
  }

  private String getOutputMimeType()
  {
    if (getFileExtension().equals("png"))
      return "image/png";
    return "image/jpeg";
  }

  private void initializeData()
  {
    boolean bool1 = true;
    Bundle localBundle = getIntent().getExtras();
    boolean bool2;
    if (localBundle != null)
    {
      if (localBundle.containsKey("noFaceDetection"))
      {
        if (localBundle.getBoolean("noFaceDetection"))
          break label116;
        bool2 = bool1;
        label36: this.mDoFaceDetection = bool2;
      }
      this.mBitmapInIntent = ((Bitmap)localBundle.getParcelable("data"));
      if (this.mBitmapInIntent != null)
      {
        this.mBitmapTileProvider = new BitmapTileProvider(this.mBitmapInIntent, 320);
        this.mCropView.setDataModel(this.mBitmapTileProvider, 0);
        if (this.mDoFaceDetection)
        {
          this.mCropView.detectFaces(this.mBitmapInIntent);
          label110: this.mState = bool1;
        }
      }
    }
    do
    {
      return;
      label116: bool2 = false;
      break label36:
      this.mCropView.initializeHighlightRectangle();
      break label110:
      this.mProgressDialog = ProgressDialog.show(this, null, getString(2131362184), bool1, bool1);
      this.mProgressDialog.setCanceledOnTouchOutside(false);
      this.mProgressDialog.setCancelMessage(this.mMainHandler.obtainMessage(5));
      this.mMediaItem = getMediaItemFromIntentData();
    }
    while (this.mMediaItem == null);
    if ((0x40 & this.mMediaItem.getSupportedOperations()) != 0);
    while (bool1)
    {
      this.mLoadTask = getThreadPool().submit(new LoadDataTask(this.mMediaItem), new FutureListener()
      {
        public void onFutureDone(Future<BitmapRegionDecoder> paramFuture)
        {
          CropImage.access$902(CropImage.this, null);
          BitmapRegionDecoder localBitmapRegionDecoder = (BitmapRegionDecoder)paramFuture.get();
          if (paramFuture.isCancelled())
          {
            if (localBitmapRegionDecoder != null)
              localBitmapRegionDecoder.recycle();
            return;
          }
          CropImage.this.mMainHandler.sendMessage(CropImage.this.mMainHandler.obtainMessage(1, localBitmapRegionDecoder));
        }
      });
      return;
      bool1 = false;
    }
    this.mLoadBitmapTask = getThreadPool().submit(new LoadBitmapDataTask(this.mMediaItem), new FutureListener()
    {
      public void onFutureDone(Future<Bitmap> paramFuture)
      {
        CropImage.access$1002(CropImage.this, null);
        Bitmap localBitmap = (Bitmap)paramFuture.get();
        if (paramFuture.isCancelled())
        {
          if (localBitmap != null)
            localBitmap.recycle();
          return;
        }
        CropImage.this.mMainHandler.sendMessage(CropImage.this.mMainHandler.obtainMessage(2, localBitmap));
      }
    });
  }

  private void onBitmapAvailable(Bitmap paramBitmap)
  {
    if (paramBitmap == null)
    {
      Toast.makeText(this, 2131362191, 0).show();
      finish();
      return;
    }
    this.mUseRegionDecoder = false;
    this.mState = 1;
    this.mBitmap = paramBitmap;
    new BitmapFactory.Options();
    this.mCropView.setDataModel(new BitmapTileProvider(paramBitmap, 512), this.mMediaItem.getRotation());
    if (this.mDoFaceDetection)
    {
      this.mCropView.detectFaces(paramBitmap);
      return;
    }
    this.mCropView.initializeHighlightRectangle();
  }

  private void onBitmapRegionDecoderAvailable(BitmapRegionDecoder paramBitmapRegionDecoder)
  {
    if (paramBitmapRegionDecoder == null)
    {
      Toast.makeText(this, 2131362191, 0).show();
      finish();
      return;
    }
    this.mRegionDecoder = paramBitmapRegionDecoder;
    this.mUseRegionDecoder = true;
    this.mState = 1;
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    int i = paramBitmapRegionDecoder.getWidth();
    int j = paramBitmapRegionDecoder.getHeight();
    localOptions.inSampleSize = BitmapUtils.computeSampleSize(i, j, -1, 480000);
    this.mBitmap = paramBitmapRegionDecoder.decodeRegion(new Rect(0, 0, i, j), localOptions);
    this.mBitmapScreenNail = new BitmapScreenNail(this.mBitmap);
    TileImageViewAdapter localTileImageViewAdapter = new TileImageViewAdapter();
    localTileImageViewAdapter.setScreenNail(this.mBitmapScreenNail, i, j);
    localTileImageViewAdapter.setRegionDecoder(paramBitmapRegionDecoder);
    this.mCropView.setDataModel(localTileImageViewAdapter, this.mMediaItem.getFullImageRotation());
    if (this.mDoFaceDetection)
    {
      this.mCropView.detectFaces(this.mBitmap);
      return;
    }
    this.mCropView.initializeHighlightRectangle();
  }

  private void onSaveClicked()
  {
    Bundle localBundle = getIntent().getExtras();
    RectF localRectF = this.mCropView.getCropRectangle();
    if (localRectF == null)
      return;
    this.mState = 2;
    if ((localBundle != null) && (localBundle.getBoolean("set-as-wallpaper")));
    for (int i = 2131362208; ; i = 2131362196)
    {
      this.mProgressDialog = ProgressDialog.show(this, null, getString(i), true, false);
      this.mSaveTask = getThreadPool().submit(new SaveOutput(localRectF), new FutureListener()
      {
        public void onFutureDone(Future<Intent> paramFuture)
        {
          CropImage.access$702(CropImage.this, null);
          if (paramFuture.isCancelled())
            return;
          Intent localIntent = (Intent)paramFuture.get();
          if (localIntent != null)
          {
            CropImage.this.mMainHandler.sendMessage(CropImage.this.mMainHandler.obtainMessage(3, localIntent));
            return;
          }
          CropImage.this.mMainHandler.sendEmptyMessage(4);
        }
      });
      return;
    }
  }

  private static void rotateCanvas(Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3)
  {
    paramCanvas.translate(paramInt1 / 2, paramInt2 / 2);
    paramCanvas.rotate(paramInt3);
    if ((0x1 & paramInt3 / 90) == 0)
    {
      paramCanvas.translate(-paramInt1 / 2, -paramInt2 / 2);
      return;
    }
    paramCanvas.translate(-paramInt2 / 2, -paramInt1 / 2);
  }

  private static void rotateRectangle(Rect paramRect, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt3 == 0) || (paramInt3 == 360))
      return;
    int i = paramRect.width();
    int j = paramRect.height();
    switch (paramInt3)
    {
    default:
      throw new AssertionError();
    case 90:
      paramRect.top = paramRect.left;
      paramRect.left = (paramInt2 - paramRect.bottom);
      paramRect.right = (j + paramRect.left);
      paramRect.bottom = (i + paramRect.top);
      return;
    case 180:
      paramRect.left = (paramInt1 - paramRect.right);
      paramRect.top = (paramInt2 - paramRect.bottom);
      paramRect.right = (i + paramRect.left);
      paramRect.bottom = (j + paramRect.top);
      return;
    case 270:
    }
    paramRect.left = paramRect.top;
    paramRect.top = (paramInt1 - paramRect.right);
    paramRect.right = (j + paramRect.left);
    paramRect.bottom = (i + paramRect.top);
  }

  private boolean saveBitmapToOutputStream(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap, Bitmap.CompressFormat paramCompressFormat, OutputStream paramOutputStream)
  {
    InterruptableOutputStream localInterruptableOutputStream = new InterruptableOutputStream(paramOutputStream);
    paramJobContext.setCancelListener(new ThreadPool.CancelListener(localInterruptableOutputStream)
    {
      public void onCancel()
      {
        this.val$ios.interrupt();
      }
    });
    try
    {
      paramBitmap.compress(paramCompressFormat, 90, localInterruptableOutputStream);
      boolean bool = paramJobContext.isCancelled();
      if (!bool)
      {
        i = 1;
        return i;
      }
      int i = 0;
    }
    finally
    {
      paramJobContext.setCancelListener(null);
      Utils.closeSilently(localInterruptableOutputStream);
    }
  }

  private boolean saveBitmapToUri(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap, Uri paramUri)
  {
    try
    {
      OutputStream localOutputStream = getContentResolver().openOutputStream(paramUri);
      try
      {
        boolean bool = saveBitmapToOutputStream(paramJobContext, paramBitmap, convertExtensionToCompressFormat(getFileExtension()), localOutputStream);
        return bool;
      }
      finally
      {
        Utils.closeSilently(localOutputStream);
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Log.w("CropImage", "cannot write output", localFileNotFoundException);
    }
    return true;
  }

  private Uri saveGenericImage(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap)
  {
    if ((!DOWNLOAD_BUCKET.isDirectory()) && (!DOWNLOAD_BUCKET.mkdirs()))
      throw new RuntimeException("cannot create download folder");
    long l = System.currentTimeMillis();
    String str = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss").format(new Date(l));
    File localFile = saveMedia(paramJobContext, paramBitmap, DOWNLOAD_BUCKET, str, null);
    if (localFile == null)
      return null;
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("title", str);
    localContentValues.put("_display_name", localFile.getName());
    localContentValues.put("datetaken", Long.valueOf(l));
    localContentValues.put("date_modified", Long.valueOf(l / 1000L));
    localContentValues.put("date_added", Long.valueOf(l / 1000L));
    localContentValues.put("mime_type", getOutputMimeType());
    localContentValues.put("orientation", Integer.valueOf(0));
    localContentValues.put("_data", localFile.getAbsolutePath());
    localContentValues.put("_size", Long.valueOf(localFile.length()));
    setImageSize(localContentValues, paramBitmap.getWidth(), paramBitmap.getHeight());
    return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
  }

  private Uri saveLocalImage(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap)
  {
    LocalImage localLocalImage = (LocalImage)this.mMediaItem;
    File localFile1 = new File(localLocalImage.filePath);
    File localFile2 = new File(localFile1.getParent());
    String str = localFile1.getName();
    int i = str.lastIndexOf('.');
    if (i >= 0)
      str = str.substring(0, i);
    Bitmap.CompressFormat localCompressFormat1 = convertExtensionToCompressFormat(getFileExtension());
    Bitmap.CompressFormat localCompressFormat2 = Bitmap.CompressFormat.JPEG;
    ExifData localExifData = null;
    if (localCompressFormat1 == localCompressFormat2)
    {
      localExifData = getExifData(localFile1.getAbsolutePath());
      if (localExifData != null)
        changeExifData(localExifData, paramBitmap.getWidth(), paramBitmap.getHeight());
    }
    File localFile3 = saveMedia(paramJobContext, paramBitmap, localFile2, str, localExifData);
    if (localFile3 == null)
      return null;
    long l = System.currentTimeMillis() / 1000L;
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("title", localLocalImage.caption);
    localContentValues.put("_display_name", localFile3.getName());
    localContentValues.put("datetaken", Long.valueOf(localLocalImage.dateTakenInMs));
    localContentValues.put("date_modified", Long.valueOf(l));
    localContentValues.put("date_added", Long.valueOf(l));
    localContentValues.put("mime_type", getOutputMimeType());
    localContentValues.put("orientation", Integer.valueOf(0));
    localContentValues.put("_data", localFile3.getAbsolutePath());
    localContentValues.put("_size", Long.valueOf(localFile3.length()));
    setImageSize(localContentValues, paramBitmap.getWidth(), paramBitmap.getHeight());
    if (GalleryUtils.isValidLocation(localLocalImage.latitude, localLocalImage.longitude))
    {
      localContentValues.put("latitude", Double.valueOf(localLocalImage.latitude));
      localContentValues.put("longitude", Double.valueOf(localLocalImage.longitude));
    }
    return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
  }

  private File saveMedia(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap, File paramFile, String paramString, ExifData paramExifData)
  {
    File localFile = null;
    String str = getFileExtension();
    for (int i = 1; ; ++i)
    {
      if (i < 1000)
        localFile = new File(paramFile, paramString + "-" + i + "." + str);
      try
      {
        boolean bool = localFile.createNewFile();
        if (bool)
        {
          if ((localFile.exists()) && (localFile.isFile()))
            break label167;
          throw new RuntimeException("cannot create file: " + paramString);
        }
      }
      catch (IOException localIOException1)
      {
        Log.e("CropImage", "fail to create new file: " + localFile.getAbsolutePath(), localIOException1);
        return null;
      }
    }
    label167: localFile.setReadable(true, false);
    localFile.setWritable(true, false);
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      if (paramExifData != null);
      try
      {
        ExifOutputStream localExifOutputStream = new ExifOutputStream(localFileOutputStream);
        localExifOutputStream.setExifData(paramExifData);
        saveBitmapToOutputStream(paramJobContext, paramBitmap, convertExtensionToCompressFormat(str), localExifOutputStream);
        localFileOutputStream.close();
        if (!paramJobContext.isCancelled())
          break label325;
        return null;
      }
      finally
      {
        localFileOutputStream.close();
      }
    }
    catch (IOException localIOException2)
    {
      Log.e("CropImage", "fail to save image: " + localFile.getAbsolutePath(), localIOException2);
      localFile.delete();
      return null;
    }
    label325: return localFile;
  }

  private Uri savePicasaImage(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap)
  {
    if ((!DOWNLOAD_BUCKET.isDirectory()) && (!DOWNLOAD_BUCKET.mkdirs()))
      throw new RuntimeException("cannot create download folder");
    String str = PicasaSource.getImageTitle(this.mMediaItem);
    int i = str.lastIndexOf('.');
    if (i >= 0)
      str = str.substring(0, i);
    ExifData localExifData = new ExifData(ByteOrder.BIG_ENDIAN);
    PicasaSource.extractExifValues(this.mMediaItem, localExifData);
    changeExifData(localExifData, paramBitmap.getWidth(), paramBitmap.getHeight());
    File localFile = saveMedia(paramJobContext, paramBitmap, DOWNLOAD_BUCKET, str, localExifData);
    if (localFile == null)
      return null;
    long l = System.currentTimeMillis() / 1000L;
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("title", PicasaSource.getImageTitle(this.mMediaItem));
    localContentValues.put("_display_name", localFile.getName());
    localContentValues.put("datetaken", Long.valueOf(PicasaSource.getDateTaken(this.mMediaItem)));
    localContentValues.put("date_modified", Long.valueOf(l));
    localContentValues.put("date_added", Long.valueOf(l));
    localContentValues.put("mime_type", getOutputMimeType());
    localContentValues.put("orientation", Integer.valueOf(0));
    localContentValues.put("_data", localFile.getAbsolutePath());
    localContentValues.put("_size", Long.valueOf(localFile.length()));
    setImageSize(localContentValues, paramBitmap.getWidth(), paramBitmap.getHeight());
    double d1 = PicasaSource.getLatitude(this.mMediaItem);
    double d2 = PicasaSource.getLongitude(this.mMediaItem);
    if (GalleryUtils.isValidLocation(d1, d2))
    {
      localContentValues.put("latitude", Double.valueOf(d1));
      localContentValues.put("longitude", Double.valueOf(d2));
    }
    return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
  }

  private Uri saveToMediaProvider(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap)
  {
    if (PicasaSource.isPicasaImage(this.mMediaItem))
      return savePicasaImage(paramJobContext, paramBitmap);
    if (this.mMediaItem instanceof LocalImage)
      return saveLocalImage(paramJobContext, paramBitmap);
    return saveGenericImage(paramJobContext, paramBitmap);
  }

  private boolean setAsWallpaper(ThreadPool.JobContext paramJobContext, Bitmap paramBitmap)
  {
    try
    {
      WallpaperManager.getInstance(this).setBitmap(paramBitmap);
      return true;
    }
    catch (IOException localIOException)
    {
      Log.w("CropImage", "fail to set wall paper", localIOException);
    }
  }

  private void setCropParameters()
  {
    Bundle localBundle = getIntent().getExtras();
    if (localBundle == null);
    float f1;
    float f2;
    do
    {
      return;
      int i = localBundle.getInt("aspectX", 0);
      int j = localBundle.getInt("aspectY", 0);
      if ((i != 0) && (j != 0))
        this.mCropView.setAspectRatio(i / j);
      f1 = localBundle.getFloat("spotlightX", 0.0F);
      f2 = localBundle.getFloat("spotlightY", 0.0F);
    }
    while ((f1 == 0.0F) || (f2 == 0.0F));
    this.mCropView.setSpotlightRatio(f1, f2);
  }

  @TargetApi(16)
  private static void setImageSize(ContentValues paramContentValues, int paramInt1, int paramInt2)
  {
    if (!ApiHelper.HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT)
      return;
    paramContentValues.put("width", Integer.valueOf(paramInt1));
    paramContentValues.put("height", Integer.valueOf(paramInt2));
  }

  public void onBackPressed()
  {
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(8);
    requestWindowFeature(9);
    setContentView(2130968589);
    this.mCropView = new CropView(this);
    getGLRoot().setContentPane(this.mCropView);
    ActionBar localActionBar = getActionBar();
    localActionBar.setDisplayOptions(12, 12);
    Bundle localBundle = getIntent().getExtras();
    if (localBundle != null)
    {
      if (localBundle.getBoolean("set-as-wallpaper", false))
        localActionBar.setTitle(getString(2131362207));
      if (localBundle.getBoolean("showWhenLocked", false))
        getWindow().addFlags(524288);
    }
    this.mMainHandler = new SynchronizedHandler(getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
          return;
        case 1:
          CropImage.this.dismissProgressDialogIfShown();
          CropImage.this.onBitmapRegionDecoderAvailable((BitmapRegionDecoder)paramMessage.obj);
          return;
        case 2:
          CropImage.this.dismissProgressDialogIfShown();
          CropImage.this.onBitmapAvailable((Bitmap)paramMessage.obj);
          return;
        case 4:
          CropImage.this.dismissProgressDialogIfShown();
          CropImage.this.setResult(0);
          Toast.makeText(CropImage.this, CropImage.this.getString(2131362198), 1).show();
          CropImage.this.finish();
        case 3:
          CropImage.this.dismissProgressDialogIfShown();
          CropImage.this.setResult(-1, (Intent)paramMessage.obj);
          CropImage.this.finish();
          return;
        case 5:
        }
        CropImage.this.setResult(0);
        CropImage.this.finish();
      }
    };
    setCropParameters();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    getMenuInflater().inflate(2131886082, paramMenu);
    return true;
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mBitmapScreenNail == null)
      return;
    this.mBitmapScreenNail.recycle();
    this.mBitmapScreenNail = null;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
    case 16908332:
    case 2131558424:
    case 2131558631:
    }
    while (true)
    {
      return true;
      finish();
      continue;
      setResult(0);
      finish();
      continue;
      onSaveClicked();
    }
  }

  protected void onPause()
  {
    super.onPause();
    dismissProgressDialogIfShown();
    Future localFuture1 = this.mLoadTask;
    if ((localFuture1 != null) && (!localFuture1.isDone()))
    {
      localFuture1.cancel();
      localFuture1.waitDone();
    }
    Future localFuture2 = this.mLoadBitmapTask;
    if ((localFuture2 != null) && (!localFuture2.isDone()))
    {
      localFuture2.cancel();
      localFuture2.waitDone();
    }
    Future localFuture3 = this.mSaveTask;
    if ((localFuture3 != null) && (!localFuture3.isDone()))
    {
      localFuture3.cancel();
      localFuture3.waitDone();
    }
    GLRoot localGLRoot = getGLRoot();
    localGLRoot.lockRenderThread();
    try
    {
      this.mCropView.pause();
      return;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  protected void onResume()
  {
    super.onResume();
    if (this.mState == 0)
      initializeData();
    if (this.mState == 2)
      onSaveClicked();
    GLRoot localGLRoot = getGLRoot();
    localGLRoot.lockRenderThread();
    try
    {
      this.mCropView.resume();
      return;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("state", this.mState);
  }

  private class LoadBitmapDataTask
    implements ThreadPool.Job<Bitmap>
  {
    MediaItem mItem;

    public LoadBitmapDataTask(MediaItem arg2)
    {
      Object localObject;
      this.mItem = localObject;
    }

    public Bitmap run(ThreadPool.JobContext paramJobContext)
    {
      if (this.mItem == null)
        return null;
      return (Bitmap)this.mItem.requestImage(1).run(paramJobContext);
    }
  }

  private class LoadDataTask
    implements ThreadPool.Job<BitmapRegionDecoder>
  {
    MediaItem mItem;

    public LoadDataTask(MediaItem arg2)
    {
      Object localObject;
      this.mItem = localObject;
    }

    public BitmapRegionDecoder run(ThreadPool.JobContext paramJobContext)
    {
      if (this.mItem == null)
        return null;
      return (BitmapRegionDecoder)this.mItem.requestLargeImage().run(paramJobContext);
    }
  }

  private class SaveOutput
    implements ThreadPool.Job<Intent>
  {
    private final RectF mCropRect;

    public SaveOutput(RectF arg2)
    {
      Object localObject;
      this.mCropRect = localObject;
    }

    public Intent run(ThreadPool.JobContext paramJobContext)
    {
      RectF localRectF = this.mCropRect;
      Bundle localBundle = CropImage.this.getIntent().getExtras();
      Rect localRect = new Rect(Math.round(localRectF.left), Math.round(localRectF.top), Math.round(localRectF.right), Math.round(localRectF.bottom));
      Intent localIntent = new Intent();
      localIntent.putExtra("cropped-rect", localRect);
      Bitmap localBitmap = null;
      int i = 0;
      Uri localUri2;
      if (localBundle != null)
      {
        localUri2 = (Uri)localBundle.getParcelable("output");
        localBitmap = null;
        i = 0;
        if (localUri2 != null)
          if (paramJobContext.isCancelled())
            localIntent = null;
      }
      Uri localUri1;
      do
      {
        do
        {
          return localIntent;
          i = 1;
          localBitmap = CropImage.this.getCroppedImage(localRect);
          if (CropImage.this.saveBitmapToUri(paramJobContext, localBitmap, localUri2) == 0)
            return null;
          if (localBundle.getBoolean("return-data", false))
          {
            if (paramJobContext.isCancelled())
              return null;
            i = 1;
            if (localBitmap == null)
              localBitmap = CropImage.this.getCroppedImage(localRect);
            localIntent.putExtra("data", localBitmap);
          }
          if (!localBundle.getBoolean("set-as-wallpaper", false))
            continue;
          if (paramJobContext.isCancelled())
            return null;
          i = 1;
          if (localBitmap == null)
            localBitmap = CropImage.this.getCroppedImage(localRect);
          if (CropImage.this.setAsWallpaper(paramJobContext, localBitmap) == 0)
            return null;
        }
        while (i != 0);
        if (paramJobContext.isCancelled())
          return null;
        if (localBitmap == null)
          localBitmap = CropImage.this.getCroppedImage(localRect);
        localUri1 = CropImage.this.saveToMediaProvider(paramJobContext, localBitmap);
      }
      while (localUri1 == null);
      localIntent.setData(localUri1);
      return localIntent;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.CropImage
 * JD-Core Version:    0.5.4
 */