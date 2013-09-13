package com.android.camera;

import android.content.Context;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.Iterator;

public class PreferenceGroup extends CameraPreference
{
  private ArrayList<CameraPreference> list = new ArrayList();

  public PreferenceGroup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public void addChild(CameraPreference paramCameraPreference)
  {
    this.list.add(paramCameraPreference);
  }

  public ListPreference findPreference(String paramString)
  {
    Iterator localIterator = this.list.iterator();
    ListPreference localListPreference1;
    while (localIterator.hasNext())
    {
      CameraPreference localCameraPreference = (CameraPreference)localIterator.next();
      if (localCameraPreference instanceof ListPreference)
      {
        ListPreference localListPreference2 = (ListPreference)localCameraPreference;
        if (localListPreference2.getKey().equals(paramString));
        return localListPreference2;
      }
      if (!localCameraPreference instanceof PreferenceGroup)
        continue;
      localListPreference1 = ((PreferenceGroup)localCameraPreference).findPreference(paramString);
      if (localListPreference1 != null)
        return localListPreference1;
    }
    return null;
  }

  public CameraPreference get(int paramInt)
  {
    return (CameraPreference)this.list.get(paramInt);
  }

  public void reloadValue()
  {
    Iterator localIterator = this.list.iterator();
    while (localIterator.hasNext())
      ((CameraPreference)localIterator.next()).reloadValue();
  }

  public void removePreference(int paramInt)
  {
    this.list.remove(paramInt);
  }

  public int size()
  {
    return this.list.size();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PreferenceGroup
 * JD-Core Version:    0.5.4
 */