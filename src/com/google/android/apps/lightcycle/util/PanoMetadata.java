package com.google.android.apps.lightcycle.util;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.google.android.apps.lightcycle.xmp.XmpUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class PanoMetadata
{
  private static final String TAG = PanoMetadata.class.getSimpleName();
  public final int croppedAreaHeight;
  public final int croppedAreaLeft;
  public final int croppedAreaTop;
  public final int croppedAreaWidth;
  public final Calendar firstPhotoTime;
  public final int fullPanoHeight;
  public final int fullPanoWidth;
  public final int imageHeight;
  public final int imageWidth;
  public final int largestValidInteriorRectHeight;
  public final int largestValidInteriorRectLeft;
  public final int largestValidInteriorRectTop;
  public final int largestValidInteriorRectWidth;
  public final Calendar lastPhotoTime;
  public final String projectionType;
  public final int sourcePhotosCount;
  public final boolean synthetic;
  public final boolean usePanoViewer;

  private PanoMetadata(int paramInt1, int paramInt2, Calendar paramCalendar1, Calendar paramCalendar2, int paramInt3, String paramString, boolean paramBoolean1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, boolean paramBoolean2)
  {
    this.imageWidth = paramInt1;
    this.imageHeight = paramInt2;
    this.firstPhotoTime = paramCalendar1;
    this.lastPhotoTime = paramCalendar2;
    this.sourcePhotosCount = paramInt3;
    this.projectionType = paramString;
    this.usePanoViewer = paramBoolean1;
    this.croppedAreaWidth = paramInt4;
    this.croppedAreaHeight = paramInt5;
    this.fullPanoWidth = paramInt6;
    this.fullPanoHeight = paramInt7;
    this.croppedAreaLeft = paramInt8;
    this.croppedAreaTop = paramInt9;
    this.largestValidInteriorRectLeft = paramInt10;
    this.largestValidInteriorRectTop = paramInt11;
    this.largestValidInteriorRectWidth = paramInt12;
    this.largestValidInteriorRectHeight = paramInt13;
    this.synthetic = paramBoolean2;
  }

  private static boolean getBoolean(XMPMeta paramXMPMeta, String paramString)
    throws XMPException
  {
    if (paramXMPMeta.doesPropertyExist("http://ns.google.com/photos/1.0/panorama/", paramString))
      return paramXMPMeta.getPropertyBoolean("http://ns.google.com/photos/1.0/panorama/", paramString).booleanValue();
    return false;
  }

  private static Calendar getDate(XMPMeta paramXMPMeta, String paramString)
    throws XMPException
  {
    if (paramXMPMeta.doesPropertyExist("http://ns.google.com/photos/1.0/panorama/", paramString))
      return paramXMPMeta.getPropertyCalendar("http://ns.google.com/photos/1.0/panorama/", paramString);
    return null;
  }

  private static int getInt(XMPMeta paramXMPMeta, String paramString)
    throws XMPException
  {
    if (paramXMPMeta.doesPropertyExist("http://ns.google.com/photos/1.0/panorama/", paramString))
      return paramXMPMeta.getPropertyInteger("http://ns.google.com/photos/1.0/panorama/", paramString).intValue();
    return 0;
  }

  private static String getString(XMPMeta paramXMPMeta, String paramString)
    throws XMPException
  {
    if (paramXMPMeta.doesPropertyExist("http://ns.google.com/photos/1.0/panorama/", paramString))
      return paramXMPMeta.getPropertyString("http://ns.google.com/photos/1.0/panorama/", paramString);
    return null;
  }

  private static boolean isNear(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    return Math.abs(paramDouble1 - paramDouble2) < paramDouble3;
  }

  public static PanoMetadata parse(InputStreamFactory paramInputStreamFactory)
  {
    InputStream localInputStream = paramInputStreamFactory.create();
    if (localInputStream == null)
      return null;
    XMPMeta localXMPMeta = XmpUtil.extractXMPMeta(localInputStream);
    String str = null;
    boolean bool1 = false;
    int i = 0;
    int j = 0;
    int k = 0;
    int l = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    Calendar localCalendar1 = null;
    Calendar localCalendar2 = null;
    int i7 = 0;
    int i8 = 0;
    if (localXMPMeta != null);
    try
    {
      localCalendar1 = getDate(localXMPMeta, "FirstPhotoDate");
      localCalendar2 = getDate(localXMPMeta, "LastPhotoDate");
      i7 = getInt(localXMPMeta, "SourcePhotosCount");
      str = getString(localXMPMeta, "ProjectionType");
      bool1 = getBoolean(localXMPMeta, "UsePanoramaViewer");
      i = getInt(localXMPMeta, "CroppedAreaImageWidthPixels");
      j = getInt(localXMPMeta, "CroppedAreaImageHeightPixels");
      k = getInt(localXMPMeta, "FullPanoWidthPixels");
      l = getInt(localXMPMeta, "FullPanoHeightPixels");
      i1 = getInt(localXMPMeta, "CroppedAreaLeftPixels");
      i2 = getInt(localXMPMeta, "CroppedAreaTopPixels");
      i3 = getInt(localXMPMeta, "LargestValidInteriorRectLeft");
      i4 = getInt(localXMPMeta, "LargestValidInteriorRectTop");
      i5 = getInt(localXMPMeta, "LargestValidInteriorRectWidth");
      int i12 = getInt(localXMPMeta, "LargestValidInteriorRectHeight");
      i6 = i12;
      label215: int i9;
      int i10;
      if ((i > 0) && (j > 0) && (k > 0) && (l > 0))
      {
        i8 = 1;
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(paramInputStreamFactory.create(), null, localOptions);
        i9 = localOptions.outWidth;
        i10 = localOptions.outHeight;
        if (i10 * 2 != i9)
          break label389;
      }
      boolean bool2;
      for (int i11 = 1; ; i11 = 0)
      {
        bool2 = false;
        if (i8 == 0)
        {
          if (i11 == 0)
            break;
          Log.i(TAG, "Could not parse meta data for file. Filling in 360 defaults.");
          localCalendar1 = Calendar.getInstance();
          localCalendar2 = Calendar.getInstance();
          i7 = -1;
          str = "equirectangular";
          bool1 = true;
          i = i9;
          j = i10;
          i1 = 0;
          i2 = 0;
          k = i9;
          l = i10;
          i3 = 0;
          i4 = 0;
          i5 = i9;
          i6 = i10;
          bool2 = true;
        }
        if (isNear(i9 / i10, i / j, 0.001D))
          break label406;
        Log.w(TAG, "Pano metadata does not match file dimensions.");
        return null;
        i8 = 0;
        label389: break label215:
      }
      Log.i(TAG, "Image has no metadata and no 2:1 aspect ratio. Not a pano!");
      return null;
      if (!isNear(k / l, 2.0D, 0.1D))
      {
        label406: Log.w(TAG, "Pano metadata invalid: Full pano dimension not 2:1.");
        return null;
      }
      return new PanoMetadata(i9, i10, localCalendar1, localCalendar2, i7, str, bool1, i, j, k, l, i1, i2, i3, i4, i5, i6, bool2);
    }
    catch (XMPException localXMPException)
    {
      i6 = 0;
      i8 = 0;
    }
  }

  public static PanoMetadata parse(String paramString)
  {
    return parse(new InputStreamFactory(paramString)
    {
      public InputStream create()
      {
        try
        {
          FileInputStream localFileInputStream = new FileInputStream(this.val$filename);
          return localFileInputStream;
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          Log.e(PanoMetadata.TAG, "Could not read file: " + this.val$filename, localFileNotFoundException);
        }
        return null;
      }
    });
  }

  public boolean isScaled()
  {
    return (this.imageWidth != this.croppedAreaWidth) || (this.imageHeight != this.croppedAreaHeight);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.util.PanoMetadata
 * JD-Core Version:    0.5.4
 */