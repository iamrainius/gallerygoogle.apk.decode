package com.android.gallery3d.util;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.android.gallery3d.app.Gallery;
import com.android.gallery3d.app.PackagesMonitor;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.ui.TiledScreenNail;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GalleryUtils
{
  private static boolean sCameraAvailable;
  private static boolean sCameraAvailableInitialized;
  private static volatile Thread sCurrentThread;
  private static float sPixelDensity = -1.0F;
  private static volatile boolean sWarned;

  static
  {
    sCameraAvailableInitialized = false;
  }

  public static double accurateDistanceMeters(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d1 = Math.sin(0.5D * (paramDouble3 - paramDouble1));
    double d2 = Math.sin(0.5D * (paramDouble4 - paramDouble2));
    double d3 = d1 * d1 + d2 * d2 * Math.cos(paramDouble1) * Math.cos(paramDouble3);
    return 6367000.0D * (2.0D * Math.atan2(Math.sqrt(d3), Math.sqrt(Math.max(0.0D, 1.0D - d3))));
  }

  public static void assertNotInRenderThread()
  {
    if ((sWarned) || (Thread.currentThread() != sCurrentThread))
      return;
    sWarned = true;
    Log.w("GalleryUtils", new Throwable("Should not do this in render thread"));
  }

  @TargetApi(11)
  public static int determineTypeBits(Context paramContext, Intent paramIntent)
  {
    String str = paramIntent.resolveType(paramContext);
    if ("*/*".equals(str));
    for (int i = 3; ; i = 3)
      while (true)
      {
        if ((ApiHelper.HAS_INTENT_EXTRA_LOCAL_ONLY) && (paramIntent.getBooleanExtra("android.intent.extra.LOCAL_ONLY", false)))
          i |= 4;
        return i;
        if (("image/*".equals(str)) || ("vnd.android.cursor.dir/image".equals(str)))
          i = 1;
        if ((!"video/*".equals(str)) && (!"vnd.android.cursor.dir/video".equals(str)))
          break;
        i = 2;
      }
  }

  public static float dpToPixel(float paramFloat)
  {
    return paramFloat * sPixelDensity;
  }

  public static int dpToPixel(int paramInt)
  {
    return Math.round(dpToPixel(paramInt));
  }

  public static double fastDistanceMeters(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((Math.abs(paramDouble1 - paramDouble3) > 0.0174532925199433D) || (Math.abs(paramDouble2 - paramDouble4) > 0.0174532925199433D))
      return accurateDistanceMeters(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    double d1 = paramDouble1 - paramDouble3;
    double d2 = paramDouble2 - paramDouble4;
    double d3 = Math.cos((paramDouble1 + paramDouble3) / 2.0D);
    double d4 = d3 * d3;
    return 6367000.0D * Math.sqrt(d1 * d1 + d2 * (d4 * d2));
  }

  public static String formatDuration(Context paramContext, int paramInt)
  {
    int i = paramInt / 3600;
    int j = (paramInt - i * 3600) / 60;
    int k = paramInt - (i * 3600 + j * 60);
    if (i == 0)
    {
      String str2 = paramContext.getString(2131362180);
      Object[] arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = Integer.valueOf(j);
      arrayOfObject2[1] = Integer.valueOf(k);
      return String.format(str2, arrayOfObject2);
    }
    String str1 = paramContext.getString(2131362181);
    Object[] arrayOfObject1 = new Object[3];
    arrayOfObject1[0] = Integer.valueOf(i);
    arrayOfObject1[1] = Integer.valueOf(j);
    arrayOfObject1[2] = Integer.valueOf(k);
    return String.format(str1, arrayOfObject1);
  }

  public static String formatLatitudeLongitude(String paramString, double paramDouble1, double paramDouble2)
  {
    Locale localLocale = Locale.ENGLISH;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Double.valueOf(paramDouble1);
    arrayOfObject[1] = Double.valueOf(paramDouble2);
    return String.format(localLocale, paramString, arrayOfObject);
  }

  public static int getBucketId(String paramString)
  {
    return paramString.toLowerCase().hashCode();
  }

  public static byte[] getBytes(String paramString)
  {
    byte[] arrayOfByte = new byte[2 * paramString.length()];
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    int j = 0;
    int k = 0;
    while (j < i)
    {
      int l = arrayOfChar[j];
      int i1 = k + 1;
      arrayOfByte[k] = (byte)(l & 0xFF);
      k = i1 + 1;
      arrayOfByte[i1] = (byte)(l >> 8);
      ++j;
    }
    return arrayOfByte;
  }

  public static int getSelectionModePrompt(int paramInt)
  {
    if ((paramInt & 0x2) != 0)
    {
      if ((paramInt & 0x1) == 0)
        return 2131362202;
      return 2131362203;
    }
    return 2131362201;
  }

  public static boolean hasSpaceForSize(long paramLong)
  {
    if (!"mounted".equals(Environment.getExternalStorageState()))
      return false;
    String str = Environment.getExternalStorageDirectory().getPath();
    try
    {
      StatFs localStatFs = new StatFs(str);
      long l = localStatFs.getAvailableBlocks();
      int i = localStatFs.getBlockSize();
      if (l * i > paramLong);
      return true;
    }
    catch (Exception localException)
    {
      Log.i("GalleryUtils", "Fail to access external storage", localException);
    }
    return false;
  }

  public static void initialize(Context paramContext)
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
    sPixelDensity = localDisplayMetrics.density;
    Resources localResources = paramContext.getResources();
    TiledScreenNail.setPlaceholderColor(localResources.getColor(2131296290));
    initializeThumbnailSizes(localDisplayMetrics, localResources);
  }

  private static void initializeThumbnailSizes(DisplayMetrics paramDisplayMetrics, Resources paramResources)
  {
    int i = Math.max(paramDisplayMetrics.heightPixels, paramDisplayMetrics.widthPixels);
    MediaItem.setThumbnailSizes(i / 2, i / 5);
    TiledScreenNail.setMaxSide(i / 2);
  }

  public static float[] intColorToFloatARGBArray(int paramInt)
  {
    float[] arrayOfFloat = new float[4];
    arrayOfFloat[0] = (Color.alpha(paramInt) / 255.0F);
    arrayOfFloat[1] = (Color.red(paramInt) / 255.0F);
    arrayOfFloat[2] = (Color.green(paramInt) / 255.0F);
    arrayOfFloat[3] = (Color.blue(paramInt) / 255.0F);
    return arrayOfFloat;
  }

  public static boolean isCameraAvailable(Context paramContext)
  {
    int i = 1;
    if (sCameraAvailableInitialized)
      return sCameraAvailable;
    int j = paramContext.getPackageManager().getComponentEnabledSetting(new ComponentName(paramContext, "com.android.camera.CameraLauncher"));
    sCameraAvailableInitialized = i;
    if ((j == 0) || (j == i));
    while (true)
    {
      sCameraAvailable = i;
      return sCameraAvailable;
      i = 0;
    }
  }

  public static boolean isEditorAvailable(Context paramContext, String paramString)
  {
    int i = PackagesMonitor.getPackagesVersion(paramContext);
    String str1 = "editor-update-" + paramString;
    String str2 = "has-editor-" + paramString;
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
    if (localSharedPreferences.getInt(str1, 0) != i)
    {
      List localList = paramContext.getPackageManager().queryIntentActivities(new Intent("android.intent.action.EDIT").setType(paramString), 0);
      SharedPreferences.Editor localEditor = localSharedPreferences.edit().putInt(str1, i);
      boolean bool1 = localList.isEmpty();
      boolean bool2 = false;
      if (!bool1)
        bool2 = true;
      localEditor.putBoolean(str2, bool2).commit();
    }
    return localSharedPreferences.getBoolean(str2, true);
  }

  public static boolean isHighResolution(Context paramContext)
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
    return (localDisplayMetrics.heightPixels > 2048) || (localDisplayMetrics.widthPixels > 2048);
  }

  public static boolean isValidLocation(double paramDouble1, double paramDouble2)
  {
    return (paramDouble1 != 0.0D) || (paramDouble2 != 0.0D);
  }

  public static int meterToPixel(float paramFloat)
  {
    return Math.round(dpToPixel(160.0F * (39.369999F * paramFloat)));
  }

  public static void setRenderThread()
  {
    sCurrentThread = Thread.currentThread();
  }

  public static void setViewPointMatrix(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    Arrays.fill(paramArrayOfFloat, 0, 16, 0.0F);
    float f = -paramFloat3;
    paramArrayOfFloat[15] = f;
    paramArrayOfFloat[5] = f;
    paramArrayOfFloat[0] = f;
    paramArrayOfFloat[8] = paramFloat1;
    paramArrayOfFloat[9] = paramFloat2;
    paramArrayOfFloat[11] = 1.0F;
    paramArrayOfFloat[10] = 1.0F;
  }

  public static void showOnMap(Context paramContext, double paramDouble1, double paramDouble2)
  {
    try
    {
      String str = formatLatitudeLongitude("http://maps.google.com/maps?f=q&q=(%f,%f)", paramDouble1, paramDouble2);
      ComponentName localComponentName = new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
      paramContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)).setComponent(localComponentName));
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("GalleryUtils", "GMM activity not found!", localActivityNotFoundException);
      paramContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(formatLatitudeLongitude("geo:%f,%f", paramDouble1, paramDouble2))));
    }
  }

  public static void startCameraActivity(Context paramContext)
  {
    paramContext.startActivity(new Intent("android.media.action.STILL_IMAGE_CAMERA").setFlags(335544320));
  }

  public static void startGalleryActivity(Context paramContext)
  {
    paramContext.startActivity(new Intent(paramContext, Gallery.class));
  }

  public static final double toMile(double paramDouble)
  {
    return paramDouble / 1609.0D;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.GalleryUtils
 * JD-Core Version:    0.5.4
 */