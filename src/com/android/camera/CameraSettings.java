package com.android.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.util.FloatMath;
import android.util.Log;
import com.android.gallery3d.common.ApiHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CameraSettings
{
  private final int mCameraId;
  private final Camera.CameraInfo[] mCameraInfo;
  private final Context mContext;
  private final Camera.Parameters mParameters;

  public CameraSettings(Activity paramActivity, Camera.Parameters paramParameters, int paramInt, Camera.CameraInfo[] paramArrayOfCameraInfo)
  {
    this.mContext = paramActivity;
    this.mParameters = paramParameters;
    this.mCameraId = paramInt;
    this.mCameraInfo = paramArrayOfCameraInfo;
  }

  private void buildCameraId(PreferenceGroup paramPreferenceGroup, IconListPreference paramIconListPreference)
  {
    int i = this.mCameraInfo.length;
    if (i < 2)
    {
      removePreference(paramPreferenceGroup, paramIconListPreference.getKey());
      return;
    }
    CharSequence[] arrayOfCharSequence = new CharSequence[i];
    for (int j = 0; j < i; ++j)
      arrayOfCharSequence[j] = ("" + j);
    paramIconListPreference.setEntryValues(arrayOfCharSequence);
  }

  private void buildExposureCompensation(PreferenceGroup paramPreferenceGroup, IconListPreference paramIconListPreference)
  {
    int i = this.mParameters.getMaxExposureCompensation();
    int j = this.mParameters.getMinExposureCompensation();
    if ((i == 0) && (j == 0))
    {
      removePreference(paramPreferenceGroup, paramIconListPreference.getKey());
      return;
    }
    float f = this.mParameters.getExposureCompensationStep();
    int k = (int)FloatMath.floor(f * i);
    int l = (int)FloatMath.ceil(f * j);
    CharSequence[] arrayOfCharSequence1 = new CharSequence[1 + (k - l)];
    CharSequence[] arrayOfCharSequence2 = new CharSequence[1 + (k - l)];
    int[] arrayOfInt = new int[1 + (k - l)];
    TypedArray localTypedArray = this.mContext.getResources().obtainTypedArray(2131427367);
    for (int i1 = l; i1 <= k; ++i1)
    {
      arrayOfCharSequence2[(k - i1)] = Integer.toString(Math.round(i1 / f));
      StringBuilder localStringBuilder = new StringBuilder();
      if (i1 > 0)
        localStringBuilder.append('+');
      arrayOfCharSequence1[(k - i1)] = i1;
      arrayOfInt[(k - i1)] = localTypedArray.getResourceId(i1 + 3, 0);
    }
    paramIconListPreference.setUseSingleIcon(true);
    paramIconListPreference.setEntries(arrayOfCharSequence1);
    paramIconListPreference.setEntryValues(arrayOfCharSequence2);
    paramIconListPreference.setLargeIconIds(arrayOfInt);
  }

  private void filterSimilarPictureSize(PreferenceGroup paramPreferenceGroup, ListPreference paramListPreference)
  {
    paramListPreference.filterDuplicated();
    if (paramListPreference.getEntries().length <= 1)
    {
      removePreference(paramPreferenceGroup, paramListPreference.getKey());
      return;
    }
    resetIfInvalid(paramListPreference);
  }

  private void filterUnsupportedOptions(PreferenceGroup paramPreferenceGroup, ListPreference paramListPreference, List<String> paramList)
  {
    if ((paramList == null) || (paramList.size() <= 1))
    {
      removePreference(paramPreferenceGroup, paramListPreference.getKey());
      return;
    }
    paramListPreference.filterUnsupported(paramList);
    if (paramListPreference.getEntries().length <= 1)
    {
      removePreference(paramPreferenceGroup, paramListPreference.getKey());
      return;
    }
    resetIfInvalid(paramListPreference);
  }

  @TargetApi(11)
  public static String getDefaultVideoQuality(int paramInt, String paramString)
  {
    if ((ApiHelper.HAS_FINE_RESOLUTION_QUALITY_LEVELS) && (CamcorderProfile.hasProfile(paramInt, Integer.valueOf(paramString).intValue())))
      return paramString;
    return Integer.toString(1);
  }

  @TargetApi(11)
  private void getFineResolutionQuality(ArrayList<String> paramArrayList)
  {
    if (CamcorderProfile.hasProfile(this.mCameraId, 6))
      paramArrayList.add(Integer.toString(6));
    if (CamcorderProfile.hasProfile(this.mCameraId, 5))
      paramArrayList.add(Integer.toString(5));
    if (!CamcorderProfile.hasProfile(this.mCameraId, 4))
      return;
    paramArrayList.add(Integer.toString(4));
  }

  private ArrayList<String> getSupportedVideoQuality()
  {
    ArrayList localArrayList = new ArrayList();
    if (ApiHelper.HAS_FINE_RESOLUTION_QUALITY_LEVELS)
      getFineResolutionQuality(localArrayList);
    CamcorderProfile localCamcorderProfile1;
    CamcorderProfile localCamcorderProfile2;
    do
    {
      return localArrayList;
      localArrayList.add(Integer.toString(1));
      localCamcorderProfile1 = CamcorderProfile.get(this.mCameraId, 1);
      localCamcorderProfile2 = CamcorderProfile.get(this.mCameraId, 0);
    }
    while (localCamcorderProfile1.videoFrameHeight * localCamcorderProfile1.videoFrameWidth <= localCamcorderProfile2.videoFrameHeight * localCamcorderProfile2.videoFrameWidth);
    localArrayList.add(Integer.toString(0));
    return localArrayList;
  }

  private void initPreference(PreferenceGroup paramPreferenceGroup)
  {
    ListPreference localListPreference1 = paramPreferenceGroup.findPreference("pref_video_quality_key");
    ListPreference localListPreference2 = paramPreferenceGroup.findPreference("pref_video_time_lapse_frame_interval_key");
    ListPreference localListPreference3 = paramPreferenceGroup.findPreference("pref_camera_picturesize_key");
    ListPreference localListPreference4 = paramPreferenceGroup.findPreference("pref_camera_whitebalance_key");
    ListPreference localListPreference5 = paramPreferenceGroup.findPreference("pref_camera_scenemode_key");
    ListPreference localListPreference6 = paramPreferenceGroup.findPreference("pref_camera_flashmode_key");
    ListPreference localListPreference7 = paramPreferenceGroup.findPreference("pref_camera_focusmode_key");
    IconListPreference localIconListPreference1 = (IconListPreference)paramPreferenceGroup.findPreference("pref_camera_exposure_key");
    IconListPreference localIconListPreference2 = (IconListPreference)paramPreferenceGroup.findPreference("pref_camera_id_key");
    ListPreference localListPreference8 = paramPreferenceGroup.findPreference("pref_camera_video_flashmode_key");
    ListPreference localListPreference9 = paramPreferenceGroup.findPreference("pref_video_effect_key");
    ListPreference localListPreference10 = paramPreferenceGroup.findPreference("pref_camera_hdr_key");
    if (localListPreference1 != null)
      filterUnsupportedOptions(paramPreferenceGroup, localListPreference1, getSupportedVideoQuality());
    if (localListPreference3 != null)
    {
      filterUnsupportedOptions(paramPreferenceGroup, localListPreference3, sizeListToStringList(this.mParameters.getSupportedPictureSizes()));
      filterSimilarPictureSize(paramPreferenceGroup, localListPreference3);
    }
    if (localListPreference4 != null)
      filterUnsupportedOptions(paramPreferenceGroup, localListPreference4, this.mParameters.getSupportedWhiteBalance());
    if (localListPreference5 != null)
      filterUnsupportedOptions(paramPreferenceGroup, localListPreference5, this.mParameters.getSupportedSceneModes());
    if (localListPreference6 != null)
      filterUnsupportedOptions(paramPreferenceGroup, localListPreference6, this.mParameters.getSupportedFlashModes());
    if (localListPreference7 != null)
    {
      if (Util.isFocusAreaSupported(this.mParameters))
        break label332;
      filterUnsupportedOptions(paramPreferenceGroup, localListPreference7, this.mParameters.getSupportedFocusModes());
    }
    if (localListPreference8 != null)
      label229: filterUnsupportedOptions(paramPreferenceGroup, localListPreference8, this.mParameters.getSupportedFlashModes());
    if (localIconListPreference1 != null)
      buildExposureCompensation(paramPreferenceGroup, localIconListPreference1);
    if (localIconListPreference2 != null)
      buildCameraId(paramPreferenceGroup, localIconListPreference2);
    if (localListPreference2 != null)
    {
      if (!ApiHelper.HAS_TIME_LAPSE_RECORDING)
        break label345;
      resetIfInvalid(localListPreference2);
    }
    while (true)
    {
      if (localListPreference9 != null)
        filterUnsupportedOptions(paramPreferenceGroup, localListPreference9, null);
      if ((localListPreference10 != null) && (((!ApiHelper.HAS_CAMERA_HDR) || (!Util.isCameraHdrSupported(this.mParameters)))))
        removePreference(paramPreferenceGroup, localListPreference10.getKey());
      return;
      label332: removePreference(paramPreferenceGroup, localListPreference7.getKey());
      break label229:
      label345: removePreference(paramPreferenceGroup, localListPreference2.getKey());
    }
  }

  public static void initialCameraPictureSize(Context paramContext, Camera.Parameters paramParameters)
  {
    List localList = paramParameters.getSupportedPictureSizes();
    if (localList == null)
      return;
    for (String str : paramContext.getResources().getStringArray(2131427337))
    {
      if (!setCameraPictureSize(str, localList, paramParameters))
        continue;
      SharedPreferences.Editor localEditor = ComboPreferences.get(paramContext).edit();
      localEditor.putString("pref_camera_picturesize_key", str);
      localEditor.apply();
      return;
    }
    Log.e("CameraSettings", "No supported picture size found");
  }

  public static Object readEffectParameter(SharedPreferences paramSharedPreferences)
  {
    String str1 = paramSharedPreferences.getString("pref_video_effect_key", "none");
    String str2;
    if (str1.equals("none"))
      str2 = null;
    do
    {
      return str2;
      str2 = str1.substring(1 + str1.indexOf('/'));
      if (!str1.startsWith("goofy_face"))
        continue;
      if (str2.equals("squeeze"))
        return Integer.valueOf(0);
      if (str2.equals("big_eyes"))
        return Integer.valueOf(1);
      if (str2.equals("big_mouth"))
        return Integer.valueOf(2);
      if (str2.equals("small_mouth"))
        return Integer.valueOf(3);
      if (str2.equals("big_nose"))
        return Integer.valueOf(4);
      if (!str2.equals("small_eyes"))
        break;
      return Integer.valueOf(5);
    }
    while (str1.startsWith("backdropper"));
    Log.e("CameraSettings", "Invalid effect selection: " + str1);
    return null;
  }

  public static int readEffectType(SharedPreferences paramSharedPreferences)
  {
    String str = paramSharedPreferences.getString("pref_video_effect_key", "none");
    if (str.equals("none"))
      return 0;
    if (str.startsWith("goofy_face"))
      return 1;
    if (str.startsWith("backdropper"))
      return 2;
    Log.e("CameraSettings", "Invalid effect selection: " + str);
    return 0;
  }

  public static int readExposure(ComboPreferences paramComboPreferences)
  {
    String str = paramComboPreferences.getString("pref_camera_exposure_key", "0");
    try
    {
      int i = Integer.parseInt(str);
      return i;
    }
    catch (Exception localException)
    {
      Log.e("CameraSettings", "Invalid exposure: " + str);
    }
    return 0;
  }

  public static int readPreferredCameraId(SharedPreferences paramSharedPreferences)
  {
    return Integer.parseInt(paramSharedPreferences.getString("pref_camera_id_key", "0"));
  }

  private static boolean removePreference(PreferenceGroup paramPreferenceGroup, String paramString)
  {
    int i = 0;
    int j = paramPreferenceGroup.size();
    while (i < j)
    {
      CameraPreference localCameraPreference = paramPreferenceGroup.get(i);
      if ((localCameraPreference instanceof PreferenceGroup) && (removePreference((PreferenceGroup)localCameraPreference, paramString)))
        return true;
      if ((localCameraPreference instanceof ListPreference) && (((ListPreference)localCameraPreference).getKey().equals(paramString)))
      {
        paramPreferenceGroup.removePreference(i);
        return true;
      }
      ++i;
    }
    return false;
  }

  public static void removePreferenceFromScreen(PreferenceGroup paramPreferenceGroup, String paramString)
  {
    removePreference(paramPreferenceGroup, paramString);
  }

  private void resetIfInvalid(ListPreference paramListPreference)
  {
    if (paramListPreference.findIndexOfValue(paramListPreference.getValue()) != -1)
      return;
    paramListPreference.setValueIndex(0);
  }

  public static boolean setCameraPictureSize(String paramString, List<Camera.Size> paramList, Camera.Parameters paramParameters)
  {
    int i = paramString.indexOf('x');
    if (i == -1)
      return false;
    int j = Integer.parseInt(paramString.substring(0, i));
    int k = Integer.parseInt(paramString.substring(i + 1));
    Iterator localIterator = paramList.iterator();
    Camera.Size localSize;
    do
    {
      if (localIterator.hasNext());
      localSize = (Camera.Size)localIterator.next();
    }
    while ((localSize.width != j) || (localSize.height != k));
    paramParameters.setPictureSize(j, k);
    return true;
  }

  private static List<String> sizeListToStringList(List<Camera.Size> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Camera.Size localSize = (Camera.Size)localIterator.next();
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(localSize.width);
      arrayOfObject[1] = Integer.valueOf(localSize.height);
      localArrayList.add(String.format("%dx%d", arrayOfObject));
    }
    return localArrayList;
  }

  private static void upgradeCameraId(SharedPreferences paramSharedPreferences)
  {
    int i = readPreferredCameraId(paramSharedPreferences);
    if (i == 0);
    int j;
    do
    {
      return;
      j = CameraHolder.instance().getNumberOfCameras();
    }
    while ((i >= 0) && (i < j));
    writePreferredCameraId(paramSharedPreferences, 0);
  }

  public static void upgradeGlobalPreferences(SharedPreferences paramSharedPreferences)
  {
    upgradeOldVersion(paramSharedPreferences);
    upgradeCameraId(paramSharedPreferences);
  }

  public static void upgradeLocalPreferences(SharedPreferences paramSharedPreferences)
  {
    int i;
    try
    {
      int j = paramSharedPreferences.getInt("pref_local_version_key", 0);
      i = j;
      label15: if (i != 2)
        break label27;
      label27: return;
    }
    catch (Exception localException)
    {
      i = 0;
      break label15:
      SharedPreferences.Editor localEditor = paramSharedPreferences.edit();
      if (i == 1)
        localEditor.remove("pref_video_quality_key");
      localEditor.putInt("pref_local_version_key", 2);
      localEditor.apply();
    }
  }

  private static void upgradeOldVersion(SharedPreferences paramSharedPreferences)
  {
    while (true)
    {
      int i;
      label15: label27: String str2;
      try
      {
        int j = paramSharedPreferences.getInt("pref_version_key", 0);
        i = j;
        if (i != 5)
          break label27;
        return;
      }
      catch (Exception localException)
      {
        i = 0;
        break label15:
        SharedPreferences.Editor localEditor = paramSharedPreferences.edit();
        if (i == 0)
          i = 1;
        if (i == 1)
        {
          str2 = paramSharedPreferences.getString("pref_camera_jpegquality_key", "85");
          if (str2.equals("65"))
          {
            str3 = "normal";
            localEditor.putString("pref_camera_jpegquality_key", str3);
            i = 2;
          }
        }
        if (i == 2)
        {
          if (!paramSharedPreferences.getBoolean("pref_camera_recordlocation_key", false))
            break label196;
          str1 = "on";
          localEditor.putString("pref_camera_recordlocation_key", str1);
          i = 3;
        }
        if (i == 3)
        {
          localEditor.remove("pref_camera_videoquality_key");
          localEditor.remove("pref_camera_video_duration_key");
        }
        localEditor.putInt("pref_version_key", 5);
        localEditor.apply();
        return;
      }
      if (str2.equals("75"))
        str3 = "fine";
      String str3 = "superfine";
      continue;
      label196: String str1 = "none";
    }
  }

  public static void writePreferredCameraId(SharedPreferences paramSharedPreferences, int paramInt)
  {
    SharedPreferences.Editor localEditor = paramSharedPreferences.edit();
    localEditor.putString("pref_camera_id_key", Integer.toString(paramInt));
    localEditor.apply();
  }

  public PreferenceGroup getPreferenceGroup(int paramInt)
  {
    PreferenceGroup localPreferenceGroup = (PreferenceGroup)new PreferenceInflater(this.mContext).inflate(paramInt);
    if (this.mParameters != null)
      initPreference(localPreferenceGroup);
    return localPreferenceGroup;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraSettings
 * JD-Core Version:    0.5.4
 */