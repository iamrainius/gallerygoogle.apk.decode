package com.android.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import com.android.gallery3d.common.ApiHelper;
import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Util
{
  private static ImageFileNamer sImageFileNamer;
  private static int[] sLocation;
  private static float sPixelDensity = 1.0F;

  static
  {
    sLocation = new int[2];
  }

  public static void Assert(boolean paramBoolean)
  {
    if (paramBoolean)
      return;
    throw new AssertionError();
  }

  public static void broadcastNewPicture(Context paramContext, Uri paramUri)
  {
    paramContext.sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", paramUri));
    paramContext.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", paramUri));
  }

  public static <T> T checkNotNull(T paramT)
  {
    if (paramT == null)
      throw new NullPointerException();
    return paramT;
  }

  public static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 > paramInt3)
      return paramInt3;
    if (paramInt1 < paramInt2)
      return paramInt2;
    return paramInt1;
  }

  public static void closeSilently(Closeable paramCloseable)
  {
    if (paramCloseable == null)
      return;
    try
    {
      paramCloseable.close();
      return;
    }
    catch (Throwable localThrowable)
    {
    }
  }

  private static int computeInitialSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    double d1 = paramOptions.outWidth;
    double d2 = paramOptions.outHeight;
    int i;
    label20: int j;
    if (paramInt2 < 0)
    {
      i = 1;
      if (paramInt1 >= 0)
        break label58;
      j = 128;
      label29: if (j >= i)
        break label82;
    }
    do
    {
      return i;
      i = (int)Math.ceil(Math.sqrt(d1 * d2 / paramInt2));
      break label20:
      label58: j = (int)Math.min(Math.floor(d1 / paramInt1), Math.floor(d2 / paramInt1));
      break label29:
      if ((paramInt2 < 0) && (paramInt1 < 0))
        label82: return 1;
    }
    while (paramInt1 < 0);
    return j;
  }

  public static int computeSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
  {
    int i = computeInitialSampleSize(paramOptions, paramInt1, paramInt2);
    if (i <= 8)
    {
      j = 1;
      while (true)
      {
        if (j >= i)
          break label43;
        j <<= 1;
      }
    }
    int j = 8 * ((i + 7) / 8);
    label43: return j;
  }

  public static String createJpegName(long paramLong)
  {
    synchronized (sImageFileNamer)
    {
      String str = sImageFileNamer.generateName(paramLong);
      return str;
    }
  }

  public static boolean equals(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != paramObject2)
    {
      if (paramObject1 == null);
      do
        return false;
      while (!paramObject1.equals(paramObject2));
    }
    return true;
  }

  public static void fadeIn(View paramView)
  {
    fadeIn(paramView, 0.0F, 1.0F, 400L);
    paramView.setEnabled(true);
  }

  public static void fadeIn(View paramView, float paramFloat1, float paramFloat2, long paramLong)
  {
    if (paramView.getVisibility() == 0)
      return;
    paramView.setVisibility(0);
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
    localAlphaAnimation.setDuration(paramLong);
    paramView.startAnimation(localAlphaAnimation);
  }

  public static void fadeOut(View paramView)
  {
    if (paramView.getVisibility() != 0)
      return;
    paramView.setEnabled(false);
    AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
    localAlphaAnimation.setDuration(400L);
    paramView.startAnimation(localAlphaAnimation);
    paramView.setVisibility(8);
  }

  public static int getCameraFacingIntentExtras(Activity paramActivity)
  {
    int i = -1;
    int j = paramActivity.getIntent().getIntExtra("android.intent.extras.CAMERA_FACING", -1);
    if (isFrontCameraIntent(j))
    {
      int l = CameraHolder.instance().getFrontCameraId();
      if (l != -1)
        i = l;
    }
    int k;
    do
    {
      do
        return i;
      while (!isBackCameraIntent(j));
      k = CameraHolder.instance().getBackCameraId();
    }
    while (k == -1);
    return k;
  }

  public static int getCameraOrientation(int paramInt)
  {
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    Camera.getCameraInfo(paramInt, localCameraInfo);
    return localCameraInfo.orientation;
  }

  @TargetApi(13)
  private static Point getDefaultDisplaySize(Activity paramActivity, Point paramPoint)
  {
    Display localDisplay = paramActivity.getWindowManager().getDefaultDisplay();
    if (Build.VERSION.SDK_INT >= 13)
    {
      localDisplay.getSize(paramPoint);
      return paramPoint;
    }
    paramPoint.set(localDisplay.getWidth(), localDisplay.getHeight());
    return paramPoint;
  }

  public static int getDisplayOrientation(int paramInt1, int paramInt2)
  {
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    Camera.getCameraInfo(paramInt2, localCameraInfo);
    if (localCameraInfo.facing == 1)
      return (360 - (paramInt1 + localCameraInfo.orientation) % 360) % 360;
    return (360 + (localCameraInfo.orientation - paramInt1)) % 360;
  }

  public static int getDisplayRotation(Activity paramActivity)
  {
    switch (paramActivity.getWindowManager().getDefaultDisplay().getRotation())
    {
    case 0:
    default:
      return 0;
    case 1:
      return 90;
    case 2:
      return 180;
    case 3:
    }
    return 270;
  }

  public static int getJpegRotation(int paramInt1, int paramInt2)
  {
    int i = 0;
    Camera.CameraInfo localCameraInfo;
    if (paramInt2 != -1)
    {
      localCameraInfo = CameraHolder.instance().getCameraInfo()[paramInt1];
      if (localCameraInfo.facing != 1)
        break label41;
      i = (360 + (localCameraInfo.orientation - paramInt2)) % 360;
    }
    return i;
    label41: return (paramInt2 + localCameraInfo.orientation) % 360;
  }

  public static Camera.Size getOptimalPreviewSize(Activity paramActivity, List<Camera.Size> paramList, double paramDouble)
  {
    Object localObject;
    if (paramList == null)
      localObject = null;
    int i;
    do
    {
      return localObject;
      localObject = null;
      double d1 = 1.7976931348623157E+308D;
      Point localPoint = getDefaultDisplaySize(paramActivity, new Point());
      i = Math.min(localPoint.x, localPoint.y);
      Iterator localIterator1 = paramList.iterator();
      while (localIterator1.hasNext())
      {
        Camera.Size localSize2 = (Camera.Size)localIterator1.next();
        if ((Math.abs(localSize2.width / localSize2.height - paramDouble) > 0.001D) || (Math.abs(localSize2.height - i) >= d1))
          continue;
        localObject = localSize2;
        d1 = Math.abs(localSize2.height - i);
      }
    }
    while (localObject != null);
    Log.w("Util", "No preview size match the aspect ratio");
    double d2 = 1.7976931348623157E+308D;
    Iterator localIterator2 = paramList.iterator();
    while (true)
    {
      if (localIterator2.hasNext());
      Camera.Size localSize1 = (Camera.Size)localIterator2.next();
      if (Math.abs(localSize1.height - i) >= d2)
        continue;
      localObject = localSize1;
      d2 = Math.abs(localSize1.height - i);
    }
  }

  public static Camera.Size getOptimalVideoSnapshotPictureSize(List<Camera.Size> paramList, double paramDouble)
  {
    Object localObject;
    if (paramList == null)
      localObject = null;
    do
    {
      return localObject;
      localObject = null;
      Iterator localIterator1 = paramList.iterator();
      while (localIterator1.hasNext())
      {
        Camera.Size localSize2 = (Camera.Size)localIterator1.next();
        if ((Math.abs(localSize2.width / localSize2.height - paramDouble) > 0.001D) || ((localObject != null) && (localSize2.width <= localObject.width)))
          continue;
        localObject = localSize2;
      }
    }
    while (localObject != null);
    Log.w("Util", "No picture size match the aspect ratio");
    Iterator localIterator2 = paramList.iterator();
    while (true)
    {
      if (localIterator2.hasNext());
      Camera.Size localSize1 = (Camera.Size)localIterator2.next();
      if ((localObject != null) && (localSize1.width <= localObject.width))
        continue;
      localObject = localSize1;
    }
  }

  public static int[] getRelativeLocation(View paramView1, View paramView2)
  {
    paramView1.getLocationInWindow(sLocation);
    int i = sLocation[0];
    int j = sLocation[1];
    paramView2.getLocationInWindow(sLocation);
    int[] arrayOfInt1 = sLocation;
    arrayOfInt1[0] -= i;
    int[] arrayOfInt2 = sLocation;
    arrayOfInt2[1] -= j;
    return sLocation;
  }

  public static void initialize(Context paramContext)
  {
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
    sPixelDensity = localDisplayMetrics.density;
    sImageFileNamer = new ImageFileNamer(paramContext.getString(2131361963));
  }

  public static boolean isAutoExposureLockSupported(Camera.Parameters paramParameters)
  {
    return "true".equals(paramParameters.get("auto-exposure-lock-supported"));
  }

  public static boolean isAutoWhiteBalanceLockSupported(Camera.Parameters paramParameters)
  {
    return "true".equals(paramParameters.get("auto-whitebalance-lock-supported"));
  }

  private static boolean isBackCameraIntent(int paramInt)
  {
    return paramInt == 0;
  }

  public static boolean isCameraHdrSupported(Camera.Parameters paramParameters)
  {
    List localList = paramParameters.getSupportedSceneModes();
    return (localList != null) && (localList.contains("hdr"));
  }

  @TargetApi(14)
  public static boolean isFocusAreaSupported(Camera.Parameters paramParameters)
  {
    boolean bool1 = ApiHelper.HAS_CAMERA_FOCUS_AREA;
    int i = 0;
    if (bool1)
    {
      int j = paramParameters.getMaxNumFocusAreas();
      i = 0;
      if (j > 0)
      {
        boolean bool2 = isSupported("auto", paramParameters.getSupportedFocusModes());
        i = 0;
        if (bool2)
          i = 1;
      }
    }
    return i;
  }

  private static boolean isFrontCameraIntent(int paramInt)
  {
    return paramInt == 1;
  }

  @TargetApi(14)
  public static boolean isMeteringAreaSupported(Camera.Parameters paramParameters)
  {
    boolean bool = ApiHelper.HAS_CAMERA_METERING_AREA;
    int i = 0;
    if (bool)
    {
      int j = paramParameters.getMaxNumMeteringAreas();
      i = 0;
      if (j > 0)
        i = 1;
    }
    return i;
  }

  public static boolean isSupported(String paramString, List<String> paramList)
  {
    if (paramList == null);
    do
      return false;
    while (paramList.indexOf(paramString) < 0);
    return true;
  }

  public static boolean isVideoSnapshotSupported(Camera.Parameters paramParameters)
  {
    return "true".equals(paramParameters.get("video-snapshot-supported"));
  }

  public static Bitmap makeBitmap(byte[] paramArrayOfByte, int paramInt)
  {
    try
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, localOptions);
      if ((!localOptions.mCancel) && (localOptions.outWidth != -1))
      {
        if (localOptions.outHeight == -1)
          return null;
        localOptions.inSampleSize = computeSampleSize(localOptions, -1, paramInt);
        localOptions.inJustDecodeBounds = false;
        localOptions.inDither = false;
        localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap localBitmap = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, localOptions);
        return localBitmap;
      }
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      Log.e("Util", "Got oom exception ", localOutOfMemoryError);
    }
    return null;
  }

  public static CameraManager.CameraProxy openCamera(Activity paramActivity, int paramInt)
    throws CameraHardwareException, CameraDisabledException
  {
    throwIfCameraDisabled(paramActivity);
    try
    {
      CameraManager.CameraProxy localCameraProxy = CameraHolder.instance().open(paramInt);
      return localCameraProxy;
    }
    catch (CameraHardwareException localCameraHardwareException)
    {
      if ("eng".equals(Build.TYPE))
        throw new RuntimeException("openCamera failed", localCameraHardwareException);
      throw localCameraHardwareException;
    }
  }

  public static void prepareMatrix(Matrix paramMatrix, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    float f;
    if (paramBoolean)
      f = -1.0F;
    while (true)
    {
      paramMatrix.setScale(f, 1.0F);
      paramMatrix.postRotate(paramInt1);
      paramMatrix.postScale(paramInt2 / 2000.0F, paramInt3 / 2000.0F);
      paramMatrix.postTranslate(paramInt2 / 2.0F, paramInt3 / 2.0F);
      return;
      f = 1.0F;
    }
  }

  public static void rectFToRect(RectF paramRectF, Rect paramRect)
  {
    paramRect.left = Math.round(paramRectF.left);
    paramRect.top = Math.round(paramRectF.top);
    paramRect.right = Math.round(paramRectF.right);
    paramRect.bottom = Math.round(paramRectF.bottom);
  }

  public static Bitmap rotate(Bitmap paramBitmap, int paramInt)
  {
    return rotateAndMirror(paramBitmap, paramInt, false);
  }

  public static Bitmap rotateAndMirror(Bitmap paramBitmap, int paramInt, boolean paramBoolean)
  {
    Matrix localMatrix;
    if ((((paramInt != 0) || (paramBoolean))) && (paramBitmap != null))
    {
      localMatrix = new Matrix();
      if (paramBoolean)
      {
        localMatrix.postScale(-1.0F, 1.0F);
        paramInt = (paramInt + 360) % 360;
        if ((paramInt != 0) && (paramInt != 180))
          break label131;
        localMatrix.postTranslate(paramBitmap.getWidth(), 0.0F);
      }
      if (paramInt != 0)
        localMatrix.postRotate(paramInt, paramBitmap.getWidth() / 2.0F, paramBitmap.getHeight() / 2.0F);
    }
    try
    {
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
      if (paramBitmap != localBitmap)
      {
        paramBitmap.recycle();
        paramBitmap = localBitmap;
      }
      return paramBitmap;
      if ((paramInt == 90) || (paramInt == 270))
        label131: localMatrix.postTranslate(paramBitmap.getHeight(), 0.0F);
      throw new IllegalArgumentException("Invalid degrees=" + paramInt);
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
    }
    return paramBitmap;
  }

  public static int roundOrientation(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1)
    {
      j = 1;
      if (j != 0)
        label7: paramInt2 = 90 * ((paramInt1 + 45) / 90) % 360;
      return paramInt2;
    }
    int i = Math.abs(paramInt1 - paramInt2);
    if (Math.min(i, 360 - i) >= 50);
    for (int j = 1; ; j = 0)
      break label7:
  }

  public static void setGpsParameters(Camera.Parameters paramParameters, Location paramLocation)
  {
    paramParameters.removeGpsData();
    paramParameters.setGpsTimestamp(System.currentTimeMillis() / 1000L);
    int i;
    if (paramLocation != null)
    {
      double d1 = paramLocation.getLatitude();
      double d2 = paramLocation.getLongitude();
      if ((d1 == 0.0D) && (d2 == 0.0D))
        break label120;
      i = 1;
      if (i == 0)
        label46: return;
      Log.d("Util", "Set gps location");
      paramParameters.setGpsLatitude(d1);
      paramParameters.setGpsLongitude(d2);
      paramParameters.setGpsProcessingMethod(paramLocation.getProvider().toUpperCase());
      if (!paramLocation.hasAltitude())
        break label126;
      paramParameters.setGpsAltitude(paramLocation.getAltitude());
    }
    while (true)
    {
      if (paramLocation.getTime() != 0L)
        paramParameters.setGpsTimestamp(paramLocation.getTime() / 1000L);
      return;
      label120: i = 0;
      break label46:
      label126: paramParameters.setGpsAltitude(0.0D);
    }
  }

  public static void showErrorAndFinish(Activity paramActivity, int paramInt)
  {
    1 local1 = new DialogInterface.OnClickListener(paramActivity)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        this.val$activity.finish();
      }
    };
    TypedValue localTypedValue = new TypedValue();
    paramActivity.getTheme().resolveAttribute(16843605, localTypedValue, true);
    new AlertDialog.Builder(paramActivity).setCancelable(false).setTitle(2131361889).setMessage(paramInt).setNeutralButton(2131361961, local1).setIcon(localTypedValue.resourceId).show();
  }

  @TargetApi(14)
  private static void throwIfCameraDisabled(Activity paramActivity)
    throws CameraDisabledException
  {
    if ((!ApiHelper.HAS_GET_CAMERA_DISABLED) || (!((DevicePolicyManager)paramActivity.getSystemService("device_policy")).getCameraDisabled(null)))
      return;
    throw new CameraDisabledException();
  }

  private static class ImageFileNamer
  {
    private SimpleDateFormat mFormat;
    private long mLastDate;
    private int mSameSecondCount;

    public ImageFileNamer(String paramString)
    {
      this.mFormat = new SimpleDateFormat(paramString);
    }

    public String generateName(long paramLong)
    {
      Date localDate = new Date(paramLong);
      String str = this.mFormat.format(localDate);
      if (paramLong / 1000L == this.mLastDate / 1000L)
      {
        this.mSameSecondCount = (1 + this.mSameSecondCount);
        return str + "_" + this.mSameSecondCount;
      }
      this.mLastDate = paramLong;
      this.mSameSecondCount = 0;
      return str;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.Util
 * JD-Core Version:    0.5.4
 */