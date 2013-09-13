package com.android.gallery3d.filtershow.cache;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.HistoryAdapter;
import com.android.gallery3d.filtershow.imageshow.ImageShow;
import com.android.gallery3d.filtershow.presets.ImagePreset;
import com.android.gallery3d.filtershow.tools.SaveCopyTask;
import com.android.gallery3d.filtershow.tools.SaveCopyTask.Callback;
import com.android.gallery3d.util.XmpUtilHelper;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class ImageLoader
{
  private FilterShowActivity mActivity = null;
  private HistoryAdapter mAdapter = null;
  private Bitmap mBackgroundBitmap = null;
  private Cache mCache = null;
  private Context mContext = null;
  private Cache mHiresCache = null;
  private final Vector<ImageShow> mListeners = new Vector();
  private int mOrientation = 0;
  private Bitmap mOriginalBitmapLarge = null;
  private Bitmap mOriginalBitmapSmall = null;
  private Rect mOriginalBounds = null;
  private Uri mUri = null;
  private Runnable mWarnListenersRunnable = new Runnable()
  {
    public void run()
    {
      for (int i = 0; i < ImageLoader.this.mListeners.size(); ++i)
        ((ImageShow)ImageLoader.this.mListeners.elementAt(i)).imageLoaded();
    }
  };
  private final ZoomCache mZoomCache = new ZoomCache();

  public ImageLoader(FilterShowActivity paramFilterShowActivity, Context paramContext)
  {
    this.mActivity = paramFilterShowActivity;
    this.mContext = paramContext;
    this.mCache = new DelayedPresetCache(this, 30);
    this.mHiresCache = new DelayedPresetCache(this, 3);
  }

  private void closeStream(Closeable paramCloseable)
  {
    if (paramCloseable != null);
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  // ERROR //
  public static int getOrientation(Context paramContext, Uri paramUri)
  {
    // Byte code:
    //   0: ldc 99
    //   2: aload_1
    //   3: invokevirtual 105	android/net/Uri:getScheme	()Ljava/lang/String;
    //   6: invokevirtual 111	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   9: ifeq +11 -> 20
    //   12: aload_1
    //   13: invokevirtual 114	android/net/Uri:getPath	()Ljava/lang/String;
    //   16: invokestatic 118	com/android/gallery3d/filtershow/cache/ImageLoader:getOrientationFromPath	(Ljava/lang/String;)I
    //   19: ireturn
    //   20: aconst_null
    //   21: astore_2
    //   22: aload_0
    //   23: invokevirtual 124	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   26: aload_1
    //   27: iconst_1
    //   28: anewarray 107	java/lang/String
    //   31: dup
    //   32: iconst_0
    //   33: ldc 126
    //   35: aastore
    //   36: aconst_null
    //   37: aconst_null
    //   38: aconst_null
    //   39: invokevirtual 132	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   42: astore_2
    //   43: aload_2
    //   44: invokeinterface 138 1 0
    //   49: ifeq +87 -> 136
    //   52: aload_2
    //   53: iconst_0
    //   54: invokeinterface 142 2 0
    //   59: istore 5
    //   61: iload 5
    //   63: lookupswitch	default:+41->104, 0:+47->110, 90:+53->116, 180:+67->130, 270:+60->123
    //   105: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   108: iconst_m1
    //   109: ireturn
    //   110: aload_2
    //   111: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   114: iconst_1
    //   115: ireturn
    //   116: aload_2
    //   117: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   120: bipush 6
    //   122: ireturn
    //   123: aload_2
    //   124: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   127: bipush 8
    //   129: ireturn
    //   130: aload_2
    //   131: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   134: iconst_3
    //   135: ireturn
    //   136: aload_2
    //   137: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   140: iconst_m1
    //   141: ireturn
    //   142: astore 4
    //   144: aload_2
    //   145: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   148: iconst_0
    //   149: ireturn
    //   150: astore_3
    //   151: aload_2
    //   152: invokestatic 148	com/android/gallery3d/common/Utils:closeSilently	(Landroid/database/Cursor;)V
    //   155: aload_3
    //   156: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   22	61	142	android/database/sqlite/SQLiteException
    //   22	61	150	finally
  }

  static int getOrientationFromPath(String paramString)
  {
    try
    {
      int i = new ExifInterface(paramString).getAttributeInt("Orientation", 1);
      return i;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return -1;
  }

  private Bitmap loadRegionBitmap(Uri paramUri, Rect paramRect)
  {
    InputStream localInputStream = null;
    try
    {
      localInputStream = this.mContext.getContentResolver().openInputStream(paramUri);
      Bitmap localBitmap = BitmapRegionDecoder.newInstance(localInputStream, false).decodeRegion(paramRect, null);
      return localBitmap;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Log.e("ImageLoader", "FileNotFoundException: " + paramUri);
      return null;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      return null;
    }
    finally
    {
      closeStream(localInputStream);
    }
  }

  private Bitmap loadScaledBitmap(Uri paramUri, int paramInt)
  {
    InputStream localInputStream = null;
    int k;
    try
    {
      localInputStream = this.mContext.getContentResolver().openInputStream(paramUri);
      Log.v("ImageLoader", "loading uri " + paramUri.getPath() + " input stream: " + localInputStream);
      BitmapFactory.Options localOptions1 = new BitmapFactory.Options();
      localOptions1.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(localInputStream, null, localOptions1);
      int i = localOptions1.outWidth;
      int j = localOptions1.outHeight;
      this.mOriginalBounds = new Rect(0, 0, i, j);
      k = 1;
      if ((i <= 2048) && (j <= 2048) && (((i / 2 < paramInt) || (j / 2 < paramInt))))
      {
        BitmapFactory.Options localOptions2 = new BitmapFactory.Options();
        localOptions2.inSampleSize = k;
        closeStream(localInputStream);
        localInputStream = this.mContext.getContentResolver().openInputStream(paramUri);
        Bitmap localBitmap = BitmapFactory.decodeStream(localInputStream, null, localOptions2);
        return localBitmap;
      }
      i /= 2;
      j /= 2;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Log.e("ImageLoader", "FileNotFoundException: " + paramUri);
      return null;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      return null;
    }
    finally
    {
      closeStream(localInputStream);
    }
  }

  public static Bitmap rotateToPortrait(Bitmap paramBitmap, int paramInt)
  {
    Matrix localMatrix = new Matrix();
    int i = paramBitmap.getWidth();
    int j = paramBitmap.getHeight();
    if ((paramInt == 6) || (paramInt == 8) || (paramInt == 5) || (paramInt == 7))
    {
      int k = i;
      i = j;
      j = k;
    }
    switch (paramInt)
    {
    default:
      return paramBitmap;
    case 6:
      localMatrix.setRotate(90.0F, i / 2.0F, j / 2.0F);
    case 3:
    case 8:
    case 2:
    case 4:
    case 5:
    case 7:
    }
    while (true)
    {
      return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
      localMatrix.setRotate(180.0F, i / 2.0F, j / 2.0F);
      continue;
      localMatrix.setRotate(270.0F, i / 2.0F, j / 2.0F);
      continue;
      localMatrix.preScale(-1.0F, 1.0F);
      continue;
      localMatrix.preScale(1.0F, -1.0F);
      continue;
      localMatrix.setRotate(90.0F, i / 2.0F, j / 2.0F);
      localMatrix.preScale(1.0F, -1.0F);
      continue;
      localMatrix.setRotate(270.0F, i / 2.0F, j / 2.0F);
      localMatrix.preScale(1.0F, -1.0F);
    }
  }

  private void updateBitmaps()
  {
    if (this.mOrientation > 1)
    {
      this.mOriginalBitmapSmall = rotateToPortrait(this.mOriginalBitmapSmall, this.mOrientation);
      this.mOriginalBitmapLarge = rotateToPortrait(this.mOriginalBitmapLarge, this.mOrientation);
    }
    this.mCache.setOriginalBitmap(this.mOriginalBitmapSmall);
    this.mHiresCache.setOriginalBitmap(this.mOriginalBitmapLarge);
    warnListeners();
  }

  private void warnListeners()
  {
    this.mActivity.runOnUiThread(this.mWarnListenersRunnable);
  }

  public void addListener(ImageShow paramImageShow)
  {
    if (!this.mListeners.contains(paramImageShow))
      this.mListeners.add(paramImageShow);
    this.mHiresCache.addObserver(paramImageShow);
  }

  public FilterShowActivity getActivity()
  {
    return this.mActivity;
  }

  public Bitmap getImageForPreset(ImageShow paramImageShow, ImagePreset paramImagePreset, boolean paramBoolean)
  {
    Bitmap localBitmap1 = this.mOriginalBitmapSmall;
    Bitmap localBitmap2 = null;
    if (localBitmap1 == null);
    Bitmap localBitmap3;
    do
    {
      return localBitmap2;
      localBitmap3 = this.mOriginalBitmapLarge;
      localBitmap2 = null;
    }
    while (localBitmap3 == null);
    if (paramBoolean);
    for (localBitmap2 = this.mHiresCache.get(paramImagePreset); ; localBitmap2 = this.mCache.get(paramImagePreset))
    {
      if (localBitmap2 == null);
      if (!paramBoolean)
        break;
      this.mHiresCache.prepare(paramImagePreset);
      this.mHiresCache.addObserver(paramImageShow);
      return localBitmap2;
    }
    this.mCache.prepare(paramImagePreset);
    this.mCache.addObserver(paramImageShow);
    return localBitmap2;
  }

  public Bitmap getOriginalBitmapLarge()
  {
    return this.mOriginalBitmapLarge;
  }

  public Rect getOriginalBounds()
  {
    return this.mOriginalBounds;
  }

  public Bitmap getScaleOneImageForPreset(ImageShow paramImageShow, ImagePreset paramImagePreset, Rect paramRect, boolean paramBoolean)
  {
    Bitmap localBitmap1 = this.mZoomCache.getImage(paramImagePreset, paramRect);
    if ((paramBoolean) || (localBitmap1 == null))
    {
      localBitmap1 = loadRegionBitmap(this.mUri, paramRect);
      if (localBitmap1 != null)
      {
        Bitmap localBitmap2 = localBitmap1.copy(Bitmap.Config.ARGB_8888, true);
        float f = paramImagePreset.getScaleFactor();
        paramImagePreset.setScaleFactor(1.0F);
        Bitmap localBitmap3 = paramImagePreset.apply(localBitmap2);
        paramImagePreset.setScaleFactor(f);
        this.mZoomCache.setImage(paramImagePreset, paramRect, localBitmap3);
        return localBitmap3;
      }
    }
    return localBitmap1;
  }

  public Uri getUri()
  {
    return this.mUri;
  }

  public XMPMeta getXmpObject()
  {
    try
    {
      XMPMeta localXMPMeta = XmpUtilHelper.extractXMPMeta(this.mContext.getContentResolver().openInputStream(getUri()));
      return localXMPMeta;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
    }
    return null;
  }

  public void loadBitmap(Uri paramUri, int paramInt)
  {
    this.mUri = paramUri;
    this.mOrientation = getOrientation(this.mContext, paramUri);
    this.mOriginalBitmapSmall = loadScaledBitmap(paramUri, 160);
    if (this.mOriginalBitmapSmall == null)
      this.mActivity.cannotLoadImage();
    this.mOriginalBitmapLarge = loadScaledBitmap(paramUri, paramInt);
    updateBitmaps();
  }

  public boolean queryLightCycle360()
  {
    try
    {
      XMPMeta localXMPMeta = XmpUtilHelper.extractXMPMeta(this.mContext.getContentResolver().openInputStream(getUri()));
      if (localXMPMeta == null)
        return false;
      localXMPMeta.getPacketHeader();
      try
      {
        if ((!localXMPMeta.doesPropertyExist("http://ns.google.com/photos/1.0/panorama/", "GPano:CroppedAreaImageWidthPixels")) || (!localXMPMeta.doesPropertyExist("http://ns.google.com/photos/1.0/panorama/", "GPano:FullPanoWidthPixels")))
          break label116;
        Integer localInteger1 = localXMPMeta.getPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "GPano:CroppedAreaImageWidthPixels");
        Integer localInteger2 = localXMPMeta.getPropertyInteger("http://ns.google.com/photos/1.0/panorama/", "GPano:FullPanoWidthPixels");
        if ((localInteger1 == null) || (localInteger2 == null))
          break label116;
        boolean bool = localInteger1.equals(localInteger2);
        return bool;
      }
      catch (XMPException localXMPException)
      {
        return false;
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
    }
    label116: return false;
  }

  public void resetImageForPreset(ImagePreset paramImagePreset, ImageShow paramImageShow)
  {
    this.mHiresCache.reset(paramImagePreset);
    this.mCache.reset(paramImagePreset);
    this.mZoomCache.reset(paramImagePreset);
  }

  public void saveImage(ImagePreset paramImagePreset, FilterShowActivity paramFilterShowActivity, File paramFile)
  {
    paramImagePreset.setIsHighQuality(true);
    paramImagePreset.setScaleFactor(1.0F);
    new SaveCopyTask(this.mContext, this.mUri, paramFile, new SaveCopyTask.Callback(paramFilterShowActivity)
    {
      public void onComplete(Uri paramUri)
      {
        this.val$filterShowActivity.completeSaveImage(paramUri);
      }
    }).execute(new ImagePreset[] { paramImagePreset });
  }

  public void setAdapter(HistoryAdapter paramHistoryAdapter)
  {
    this.mAdapter = paramHistoryAdapter;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.cache.ImageLoader
 * JD-Core Version:    0.5.4
 */