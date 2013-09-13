package com.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ComboPreferences
  implements SharedPreferences, SharedPreferences.OnSharedPreferenceChangeListener
{
  private static WeakHashMap<Context, ComboPreferences> sMap = new WeakHashMap();
  private CopyOnWriteArrayList<SharedPreferences.OnSharedPreferenceChangeListener> mListeners;
  private SharedPreferences mPrefGlobal;
  private SharedPreferences mPrefLocal;

  public ComboPreferences(Context paramContext)
  {
    this.mPrefGlobal = PreferenceManager.getDefaultSharedPreferences(paramContext);
    this.mPrefGlobal.registerOnSharedPreferenceChangeListener(this);
    synchronized (sMap)
    {
      sMap.put(paramContext, this);
      this.mListeners = new CopyOnWriteArrayList();
      return;
    }
  }

  public static ComboPreferences get(Context paramContext)
  {
    synchronized (sMap)
    {
      ComboPreferences localComboPreferences = (ComboPreferences)sMap.get(paramContext);
      return localComboPreferences;
    }
  }

  private static boolean isGlobal(String paramString)
  {
    return (paramString.equals("pref_video_time_lapse_frame_interval_key")) || (paramString.equals("pref_camera_id_key")) || (paramString.equals("pref_camera_recordlocation_key")) || (paramString.equals("pref_camera_first_use_hint_shown_key")) || (paramString.equals("pref_video_first_use_hint_shown_key")) || (paramString.equals("pref_video_effect_key"));
  }

  public boolean contains(String paramString)
  {
    if (this.mPrefLocal.contains(paramString));
    do
      return true;
    while (this.mPrefGlobal.contains(paramString));
    return false;
  }

  public SharedPreferences.Editor edit()
  {
    return new MyEditor();
  }

  public Map<String, ?> getAll()
  {
    throw new UnsupportedOperationException();
  }

  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    if ((isGlobal(paramString)) || (!this.mPrefLocal.contains(paramString)))
      return this.mPrefGlobal.getBoolean(paramString, paramBoolean);
    return this.mPrefLocal.getBoolean(paramString, paramBoolean);
  }

  public float getFloat(String paramString, float paramFloat)
  {
    if ((isGlobal(paramString)) || (!this.mPrefLocal.contains(paramString)))
      return this.mPrefGlobal.getFloat(paramString, paramFloat);
    return this.mPrefLocal.getFloat(paramString, paramFloat);
  }

  public SharedPreferences getGlobal()
  {
    return this.mPrefGlobal;
  }

  public int getInt(String paramString, int paramInt)
  {
    if ((isGlobal(paramString)) || (!this.mPrefLocal.contains(paramString)))
      return this.mPrefGlobal.getInt(paramString, paramInt);
    return this.mPrefLocal.getInt(paramString, paramInt);
  }

  public SharedPreferences getLocal()
  {
    return this.mPrefLocal;
  }

  public long getLong(String paramString, long paramLong)
  {
    if ((isGlobal(paramString)) || (!this.mPrefLocal.contains(paramString)))
      return this.mPrefGlobal.getLong(paramString, paramLong);
    return this.mPrefLocal.getLong(paramString, paramLong);
  }

  public String getString(String paramString1, String paramString2)
  {
    if ((isGlobal(paramString1)) || (!this.mPrefLocal.contains(paramString1)))
      return this.mPrefGlobal.getString(paramString1, paramString2);
    return this.mPrefLocal.getString(paramString1, paramString2);
  }

  public Set<String> getStringSet(String paramString, Set<String> paramSet)
  {
    throw new UnsupportedOperationException();
  }

  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext())
      ((SharedPreferences.OnSharedPreferenceChangeListener)localIterator.next()).onSharedPreferenceChanged(this, paramString);
  }

  public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener)
  {
    this.mListeners.add(paramOnSharedPreferenceChangeListener);
  }

  public void setLocalId(Context paramContext, int paramInt)
  {
    String str = paramContext.getPackageName() + "_preferences_" + paramInt;
    if (this.mPrefLocal != null)
      this.mPrefLocal.unregisterOnSharedPreferenceChangeListener(this);
    this.mPrefLocal = paramContext.getSharedPreferences(str, 0);
    this.mPrefLocal.registerOnSharedPreferenceChangeListener(this);
  }

  public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener paramOnSharedPreferenceChangeListener)
  {
    this.mListeners.remove(paramOnSharedPreferenceChangeListener);
  }

  private class MyEditor
    implements SharedPreferences.Editor
  {
    private SharedPreferences.Editor mEditorGlobal = ComboPreferences.this.mPrefGlobal.edit();
    private SharedPreferences.Editor mEditorLocal = ComboPreferences.this.mPrefLocal.edit();

    MyEditor()
    {
    }

    public void apply()
    {
      this.mEditorGlobal.apply();
      this.mEditorLocal.apply();
    }

    public SharedPreferences.Editor clear()
    {
      this.mEditorGlobal.clear();
      this.mEditorLocal.clear();
      return this;
    }

    public boolean commit()
    {
      boolean bool1 = this.mEditorGlobal.commit();
      boolean bool2 = this.mEditorLocal.commit();
      return (bool1) && (bool2);
    }

    public SharedPreferences.Editor putBoolean(String paramString, boolean paramBoolean)
    {
      if (ComboPreferences.access$200(paramString))
      {
        this.mEditorGlobal.putBoolean(paramString, paramBoolean);
        return this;
      }
      this.mEditorLocal.putBoolean(paramString, paramBoolean);
      return this;
    }

    public SharedPreferences.Editor putFloat(String paramString, float paramFloat)
    {
      if (ComboPreferences.access$200(paramString))
      {
        this.mEditorGlobal.putFloat(paramString, paramFloat);
        return this;
      }
      this.mEditorLocal.putFloat(paramString, paramFloat);
      return this;
    }

    public SharedPreferences.Editor putInt(String paramString, int paramInt)
    {
      if (ComboPreferences.access$200(paramString))
      {
        this.mEditorGlobal.putInt(paramString, paramInt);
        return this;
      }
      this.mEditorLocal.putInt(paramString, paramInt);
      return this;
    }

    public SharedPreferences.Editor putLong(String paramString, long paramLong)
    {
      if (ComboPreferences.access$200(paramString))
      {
        this.mEditorGlobal.putLong(paramString, paramLong);
        return this;
      }
      this.mEditorLocal.putLong(paramString, paramLong);
      return this;
    }

    public SharedPreferences.Editor putString(String paramString1, String paramString2)
    {
      if (ComboPreferences.access$200(paramString1))
      {
        this.mEditorGlobal.putString(paramString1, paramString2);
        return this;
      }
      this.mEditorLocal.putString(paramString1, paramString2);
      return this;
    }

    public SharedPreferences.Editor putStringSet(String paramString, Set<String> paramSet)
    {
      throw new UnsupportedOperationException();
    }

    public SharedPreferences.Editor remove(String paramString)
    {
      this.mEditorGlobal.remove(paramString);
      this.mEditorLocal.remove(paramString);
      return this;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ComboPreferences
 * JD-Core Version:    0.5.4
 */