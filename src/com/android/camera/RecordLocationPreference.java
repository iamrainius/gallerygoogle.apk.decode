package com.android.camera;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

public class RecordLocationPreference extends IconListPreference
{
  private final ContentResolver mResolver;

  public RecordLocationPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mResolver = paramContext.getContentResolver();
  }

  public static boolean get(SharedPreferences paramSharedPreferences, ContentResolver paramContentResolver)
  {
    return "on".equals(paramSharedPreferences.getString("pref_camera_recordlocation_key", "none"));
  }

  public static boolean isSet(SharedPreferences paramSharedPreferences)
  {
    return !"none".equals(paramSharedPreferences.getString("pref_camera_recordlocation_key", "none"));
  }

  public String getValue()
  {
    if (get(getSharedPreferences(), this.mResolver))
      return "on";
    return "off";
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.RecordLocationPreference
 * JD-Core Version:    0.5.4
 */