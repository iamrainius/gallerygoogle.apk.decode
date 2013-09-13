package com.android.camera;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.camera.drawable.TextDrawable;
import com.android.camera.ui.PieItem;
import com.android.camera.ui.PieItem.OnClickListener;
import com.android.camera.ui.PieRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PieController
{
  private static String TAG = "CAM_piecontrol";
  protected CameraActivity mActivity;
  protected CameraPreference.OnPreferenceChangedListener mListener;
  private Map<IconListPreference, String> mOverrides;
  protected PreferenceGroup mPreferenceGroup;
  private Map<IconListPreference, PieItem> mPreferenceMap;
  private List<IconListPreference> mPreferences;
  protected PieRenderer mRenderer;

  public PieController(CameraActivity paramCameraActivity, PieRenderer paramPieRenderer)
  {
    this.mActivity = paramCameraActivity;
    this.mRenderer = paramPieRenderer;
    this.mPreferences = new ArrayList();
    this.mPreferenceMap = new HashMap();
    this.mOverrides = new HashMap();
  }

  private void override(IconListPreference paramIconListPreference, String[] paramArrayOfString)
  {
    this.mOverrides.remove(paramIconListPreference);
    for (int i = 0; ; i += 2)
    {
      PieItem localPieItem;
      if (i < paramArrayOfString.length)
      {
        String str1 = paramArrayOfString[i];
        String str2 = paramArrayOfString[(i + 1)];
        if (!str1.equals(paramIconListPreference.getKey()))
          continue;
        this.mOverrides.put(paramIconListPreference, str2);
        localPieItem = (PieItem)this.mPreferenceMap.get(paramIconListPreference);
        if (str2 != null)
          break label96;
      }
      for (boolean bool = true; ; bool = false)
      {
        localPieItem.setEnabled(bool);
        reloadPreference(paramIconListPreference);
        label96: return;
      }
    }
  }

  private void reloadPreference(IconListPreference paramIconListPreference)
  {
    if (paramIconListPreference.getUseSingleIcon())
      return;
    PieItem localPieItem = (PieItem)this.mPreferenceMap.get(paramIconListPreference);
    String str = (String)this.mOverrides.get(paramIconListPreference);
    int[] arrayOfInt = paramIconListPreference.getLargeIconIds();
    if (arrayOfInt != null)
    {
      int i;
      if (str == null)
        i = paramIconListPreference.findIndexOfValue(paramIconListPreference.getValue());
      do
      {
        localPieItem.setImageResource(this.mActivity, arrayOfInt[i]);
        return;
        i = paramIconListPreference.findIndexOfValue(str);
      }
      while (i != -1);
      Log.e(TAG, "Fail to find override value=" + str);
      paramIconListPreference.print();
      return;
    }
    localPieItem.setImageResource(this.mActivity, paramIconListPreference.getSingleIcon());
  }

  public void addItem(String paramString, float paramFloat1, float paramFloat2)
  {
    IconListPreference localIconListPreference = (IconListPreference)this.mPreferenceGroup.findPreference(paramString);
    if (localIconListPreference == null);
    int[] arrayOfInt;
    int i;
    label54: PieItem localPieItem1;
    int j;
    int k;
    do
    {
      do
      {
        return;
        arrayOfInt = localIconListPreference.getLargeIconIds();
        if ((localIconListPreference.getUseSingleIcon()) || (arrayOfInt == null))
          break label174;
        i = arrayOfInt[localIconListPreference.findIndexOfValue(localIconListPreference.getValue())];
        localPieItem1 = makeItem(i);
        localPieItem1.setFixedSlice(paramFloat1, paramFloat2);
        this.mRenderer.addItem(localPieItem1);
        this.mPreferences.add(localIconListPreference);
        this.mPreferenceMap.put(localIconListPreference, localPieItem1);
        j = localIconListPreference.getEntries().length;
      }
      while (j <= 1);
      label121: k = 0;
    }
    while (k >= j);
    if (arrayOfInt != null);
    for (PieItem localPieItem2 = makeItem(arrayOfInt[k]); ; localPieItem2 = makeItem(localIconListPreference.getEntries()[k]))
    {
      localPieItem1.addItem(localPieItem2);
      localPieItem2.setOnClickListener(new PieItem.OnClickListener(localIconListPreference, k)
      {
        public void onClick(PieItem paramPieItem)
        {
          this.val$pref.setValueIndex(this.val$index);
          PieController.this.reloadPreference(this.val$pref);
          PieController.this.onSettingChanged(this.val$pref);
        }
      });
      ++k;
      break label121:
      label174: i = localIconListPreference.getSingleIcon();
      break label54:
    }
  }

  public void initialize(PreferenceGroup paramPreferenceGroup)
  {
    this.mRenderer.clearItems();
    setPreferenceGroup(paramPreferenceGroup);
  }

  protected PieItem makeItem(int paramInt)
  {
    return new PieItem(this.mActivity.getResources().getDrawable(paramInt).mutate(), 0);
  }

  protected PieItem makeItem(CharSequence paramCharSequence)
  {
    return new PieItem(new TextDrawable(this.mActivity.getResources(), paramCharSequence), 0);
  }

  public void onSettingChanged(ListPreference paramListPreference)
  {
    if (this.mListener == null)
      return;
    this.mListener.onSharedPreferenceChanged();
  }

  public void overrideSettings(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length % 2 != 0)
      throw new IllegalArgumentException();
    Iterator localIterator = this.mPreferenceMap.keySet().iterator();
    while (localIterator.hasNext())
      override((IconListPreference)localIterator.next(), paramArrayOfString);
  }

  public void reloadPreferences()
  {
    this.mPreferenceGroup.reloadValue();
    Iterator localIterator = this.mPreferenceMap.keySet().iterator();
    while (localIterator.hasNext())
      reloadPreference((IconListPreference)localIterator.next());
  }

  protected void setCameraId(int paramInt)
  {
    this.mPreferenceGroup.findPreference("pref_camera_id_key").setValue("" + paramInt);
  }

  public void setListener(CameraPreference.OnPreferenceChangedListener paramOnPreferenceChangedListener)
  {
    this.mListener = paramOnPreferenceChangedListener;
  }

  public void setPreferenceGroup(PreferenceGroup paramPreferenceGroup)
  {
    this.mPreferenceGroup = paramPreferenceGroup;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PieController
 * JD-Core Version:    0.5.4
 */