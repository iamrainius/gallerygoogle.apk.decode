package com.android.gallery3d.common;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build.VERSION;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import java.lang.reflect.Field;

public class ApiHelper
{
  public static final boolean CAN_START_PREVIEW_IN_JPEG_CALLBACK;
  public static final boolean ENABLE_PHOTO_EDITOR;
  public static final boolean HAS_ACTION_BAR;
  public static final boolean HAS_AUTO_FOCUS_MOVE_CALLBACK;
  public static final boolean HAS_CAMERA_FOCUS_AREA;
  public static final boolean HAS_CAMERA_HDR;
  public static final boolean HAS_CAMERA_METERING_AREA;
  public static final boolean HAS_EFFECTS_RECORDING_CONTEXT_INPUT;
  public static final boolean HAS_FACE_DETECTION;
  public static final boolean HAS_FINE_RESOLUTION_QUALITY_LEVELS;
  public static final boolean HAS_GET_CAMERA_DISABLED;
  public static final boolean HAS_GET_SUPPORTED_VIDEO_SIZE;
  public static final boolean HAS_INTENT_EXTRA_LOCAL_ONLY;
  public static final boolean HAS_MEDIA_ACTION_SOUND;
  public static final boolean HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT;
  public static final boolean HAS_MEDIA_PROVIDER_FILES_TABLE;
  public static final boolean HAS_MOTION_EVENT_TRANSFORM;
  public static final boolean HAS_MTP;
  public static final boolean HAS_OLD_PANORAMA;
  public static final boolean HAS_OPTIONS_IN_MUTABLE;
  public static final boolean HAS_POST_ON_ANIMATION;
  public static final boolean HAS_RELEASE_SURFACE_TEXTURE;
  public static final boolean HAS_REMOTE_VIEWS_SERVICE;
  public static final boolean HAS_REUSING_BITMAP_IN_BITMAP_FACTORY;
  public static final boolean HAS_REUSING_BITMAP_IN_BITMAP_REGION_DECODER;
  public static final boolean HAS_SET_BEAM_PUSH_URIS;
  public static final boolean HAS_SET_DEFALT_BUFFER_SIZE;
  public static final boolean HAS_SET_ICON_ATTRIBUTE;
  public static final boolean HAS_SET_SYSTEM_UI_VISIBILITY;
  public static final boolean HAS_SURFACE_TEXTURE;
  public static final boolean HAS_SURFACE_TEXTURE_RECORDING;
  public static final boolean HAS_TIME_LAPSE_RECORDING;
  public static final boolean HAS_VIEW_PROPERTY_ANIMATOR;
  public static final boolean HAS_VIEW_SYSTEM_UI_FLAG_HIDE_NAVIGATION;
  public static final boolean HAS_VIEW_SYSTEM_UI_FLAG_LAYOUT_STABLE;
  public static final boolean HAS_VIEW_TRANSFORM_PROPERTIES;
  public static final boolean HAS_ZOOM_WHEN_RECORDING;
  public static final boolean USE_888_PIXEL_FORMAT;

  static
  {
    int i = 1;
    int j;
    label12: int k;
    label26: int l;
    label70: int i1;
    label85: int i2;
    label101: int i3;
    label163: int i4;
    label179: int i5;
    label195: int i6;
    label211: int i7;
    if (Build.VERSION.SDK_INT >= 16)
    {
      j = i;
      USE_888_PIXEL_FORMAT = j;
      if (Build.VERSION.SDK_INT < 14)
        break label679;
      k = i;
      ENABLE_PHOTO_EDITOR = k;
      HAS_VIEW_SYSTEM_UI_FLAG_LAYOUT_STABLE = hasField(View.class, "SYSTEM_UI_FLAG_LAYOUT_STABLE");
      HAS_VIEW_SYSTEM_UI_FLAG_HIDE_NAVIGATION = hasField(View.class, "SYSTEM_UI_FLAG_HIDE_NAVIGATION");
      HAS_MEDIA_COLUMNS_WIDTH_AND_HEIGHT = hasField(MediaStore.MediaColumns.class, "WIDTH");
      if (Build.VERSION.SDK_INT < 16)
        break label684;
      l = i;
      HAS_REUSING_BITMAP_IN_BITMAP_REGION_DECODER = l;
      if (Build.VERSION.SDK_INT < 11)
        break label689;
      i1 = i;
      HAS_REUSING_BITMAP_IN_BITMAP_FACTORY = i1;
      if (Build.VERSION.SDK_INT < 16)
        break label695;
      i2 = i;
      HAS_SET_BEAM_PUSH_URIS = i2;
      Class[] arrayOfClass1 = new Class[2];
      arrayOfClass1[0] = Integer.TYPE;
      arrayOfClass1[i] = Integer.TYPE;
      HAS_SET_DEFALT_BUFFER_SIZE = hasMethod("android.graphics.SurfaceTexture", "setDefaultBufferSize", arrayOfClass1);
      HAS_RELEASE_SURFACE_TEXTURE = hasMethod("android.graphics.SurfaceTexture", "release", new Class[0]);
      if (Build.VERSION.SDK_INT < 11)
        break label701;
      i3 = i;
      HAS_SURFACE_TEXTURE = i3;
      if (Build.VERSION.SDK_INT < 12)
        break label707;
      i4 = i;
      HAS_MTP = i4;
      if (Build.VERSION.SDK_INT < 16)
        break label713;
      i5 = i;
      HAS_AUTO_FOCUS_MOVE_CALLBACK = i5;
      if (Build.VERSION.SDK_INT < 11)
        break label719;
      i6 = i;
      HAS_REMOTE_VIEWS_SERVICE = i6;
      if (Build.VERSION.SDK_INT < 11)
        break label725;
      i7 = i;
      HAS_INTENT_EXTRA_LOCAL_ONLY = i7;
      Class[] arrayOfClass2 = new Class[i];
      arrayOfClass2[0] = Integer.TYPE;
      HAS_SET_SYSTEM_UI_VISIBILITY = hasMethod(View.class, "setSystemUiVisibility", arrayOfClass2);
    }
    while (true)
    {
      int i8;
      label328: int i9;
      label368: int i10;
      label384: int i11;
      label400: int i12;
      label416: int i13;
      label432: int i14;
      label448: int i15;
      label464: int i16;
      label480: int i17;
      label496: int i18;
      label512: int i19;
      label528: int i20;
      label544: int i21;
      label560: int i22;
      label576: int i23;
      label592: int i24;
      label608: int i25;
      label624: int i26;
      label640: int i27;
      try
      {
        if ((hasMethod(Camera.class, "setFaceDetectionListener", new Class[] { Class.forName("android.hardware.Camera$FaceDetectionListener") })) && (hasMethod(Camera.class, "startFaceDetection", new Class[0])) && (hasMethod(Camera.class, "stopFaceDetection", new Class[0])))
        {
          boolean bool = hasMethod(Camera.Parameters.class, "getMaxNumDetectedFaces", new Class[0]);
          if (bool)
          {
            i8 = i;
            HAS_FACE_DETECTION = i8;
            Class[] arrayOfClass3 = new Class[i];
            arrayOfClass3[0] = ComponentName.class;
            HAS_GET_CAMERA_DISABLED = hasMethod(DevicePolicyManager.class, "getCameraDisabled", arrayOfClass3);
            if (Build.VERSION.SDK_INT < 16)
              break label745;
            i9 = i;
            HAS_MEDIA_ACTION_SOUND = i9;
            if (Build.VERSION.SDK_INT < 14)
              break label751;
            i10 = i;
            HAS_OLD_PANORAMA = i10;
            if (Build.VERSION.SDK_INT < 11)
              break label757;
            i11 = i;
            HAS_TIME_LAPSE_RECORDING = i11;
            if (Build.VERSION.SDK_INT < 14)
              break label763;
            i12 = i;
            HAS_ZOOM_WHEN_RECORDING = i12;
            if (Build.VERSION.SDK_INT < 14)
              break label769;
            i13 = i;
            HAS_CAMERA_FOCUS_AREA = i13;
            if (Build.VERSION.SDK_INT < 14)
              break label775;
            i14 = i;
            HAS_CAMERA_METERING_AREA = i14;
            if (Build.VERSION.SDK_INT < 11)
              break label781;
            i15 = i;
            HAS_FINE_RESOLUTION_QUALITY_LEVELS = i15;
            if (Build.VERSION.SDK_INT < 11)
              break label787;
            i16 = i;
            HAS_MOTION_EVENT_TRANSFORM = i16;
            if (Build.VERSION.SDK_INT < 17)
              break label793;
            i17 = i;
            HAS_EFFECTS_RECORDING_CONTEXT_INPUT = i17;
            if (Build.VERSION.SDK_INT < 11)
              break label799;
            i18 = i;
            HAS_GET_SUPPORTED_VIDEO_SIZE = i18;
            if (Build.VERSION.SDK_INT < 11)
              break label805;
            i19 = i;
            HAS_SET_ICON_ATTRIBUTE = i19;
            if (Build.VERSION.SDK_INT < 11)
              break label811;
            i20 = i;
            HAS_MEDIA_PROVIDER_FILES_TABLE = i20;
            if (Build.VERSION.SDK_INT < 16)
              break label817;
            i21 = i;
            HAS_SURFACE_TEXTURE_RECORDING = i21;
            if (Build.VERSION.SDK_INT < 11)
              break label823;
            i22 = i;
            HAS_ACTION_BAR = i22;
            if (Build.VERSION.SDK_INT < 11)
              break label829;
            i23 = i;
            HAS_VIEW_TRANSFORM_PROPERTIES = i23;
            if (Build.VERSION.SDK_INT < 17)
              break label835;
            i24 = i;
            HAS_CAMERA_HDR = i24;
            if (Build.VERSION.SDK_INT < 11)
              break label841;
            i25 = i;
            HAS_OPTIONS_IN_MUTABLE = i25;
            if (Build.VERSION.SDK_INT < 14)
              break label847;
            i26 = i;
            CAN_START_PREVIEW_IN_JPEG_CALLBACK = i26;
            if (Build.VERSION.SDK_INT < 12)
              break label853;
            i27 = i;
            label656: HAS_VIEW_PROPERTY_ANIMATOR = i27;
            if (Build.VERSION.SDK_INT < 16)
              break label859;
            label669: HAS_POST_ON_ANIMATION = i;
            return;
            j = 0;
            break label12:
            label679: k = 0;
            break label26:
            label684: l = 0;
            break label70:
            label689: i1 = 0;
            break label85:
            label695: i2 = 0;
            break label101:
            label701: i3 = 0;
            break label163:
            label707: i4 = 0;
            break label179:
            label713: i5 = 0;
            break label195:
            label719: i6 = 0;
            break label211:
            label725: i7 = 0;
          }
        }
        label769: label775: label781: label787: label793: label799: label805: label811: label817: label823: label829: label835: label841: label847: label853: label859: label745: label751: label757: label763: i8 = 0;
      }
      catch (Throwable localThrowable)
      {
        i8 = 0;
        break label328:
        i9 = 0;
        break label368:
        i10 = 0;
        break label384:
        i11 = 0;
        break label400:
        i12 = 0;
        break label416:
        i13 = 0;
        break label432:
        i14 = 0;
        break label448:
        i15 = 0;
        break label464:
        i16 = 0;
        break label480:
        i17 = 0;
        break label496:
        i18 = 0;
        break label512:
        i19 = 0;
        break label528:
        i20 = 0;
        break label544:
        i21 = 0;
        break label560:
        i22 = 0;
        break label576:
        i23 = 0;
        break label592:
        i24 = 0;
        break label608:
        i25 = 0;
        break label624:
        i26 = 0;
        break label640:
        i27 = 0;
        break label656:
        i = 0;
        break label669:
      }
    }
  }

  public static int getIntFieldIfExists(Class<?> paramClass1, String paramString, Class<?> paramClass2, int paramInt)
  {
    try
    {
      int i = paramClass1.getDeclaredField(paramString).getInt(paramClass2);
      return i;
    }
    catch (Exception localException)
    {
    }
    return paramInt;
  }

  private static boolean hasField(Class<?> paramClass, String paramString)
  {
    try
    {
      paramClass.getDeclaredField(paramString);
      return true;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
    }
    return false;
  }

  private static boolean hasMethod(Class<?> paramClass, String paramString, Class<?>[] paramArrayOfClass)
  {
    try
    {
      paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
      return true;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
    }
    return false;
  }

  private static boolean hasMethod(String paramString1, String paramString2, Class<?>[] paramArrayOfClass)
  {
    try
    {
      Class.forName(paramString1).getDeclaredMethod(paramString2, paramArrayOfClass);
      return true;
    }
    catch (Throwable localThrowable)
    {
    }
    return false;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.common.ApiHelper
 * JD-Core Version:    0.5.4
 */