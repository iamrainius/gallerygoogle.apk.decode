package com.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public abstract class CameraPreference
{
  private final Context mContext;
  private SharedPreferences mSharedPreferences;
  private final String mTitle;

  public CameraPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this.mContext = paramContext;
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CameraPreference, 0, 0);
    this.mTitle = localTypedArray.getString(0);
    localTypedArray.recycle();
  }

  public SharedPreferences getSharedPreferences()
  {
    if (this.mSharedPreferences == null)
      this.mSharedPreferences = ComboPreferences.get(this.mContext);
    return this.mSharedPreferences;
  }

  public String getTitle()
  {
    return this.mTitle;
  }

  public abstract void reloadValue();

  public static abstract interface OnPreferenceChangedListener
  {
    public abstract void onCameraPickerClicked(int paramInt);

    public abstract void onSharedPreferenceChanged();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.CameraPreference
 * JD-Core Version:    0.5.4
 */