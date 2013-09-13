package com.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import java.util.ArrayList;
import java.util.List;

public class ListPreference extends CameraPreference
{
  private final CharSequence[] mDefaultValues;
  private CharSequence[] mEntries;
  private CharSequence[] mEntryValues;
  private final String mKey;
  private boolean mLoaded = false;
  private String mValue;

  public ListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPreference, 0, 0);
    this.mKey = ((String)Util.checkNotNull(localTypedArray.getString(0)));
    TypedValue localTypedValue = localTypedArray.peekValue(1);
    if ((localTypedValue != null) && (localTypedValue.type == 1))
      this.mDefaultValues = localTypedArray.getTextArray(1);
    while (true)
    {
      setEntries(localTypedArray.getTextArray(3));
      setEntryValues(localTypedArray.getTextArray(2));
      localTypedArray.recycle();
      return;
      this.mDefaultValues = new CharSequence[1];
      this.mDefaultValues[0] = localTypedArray.getString(1);
    }
  }

  private String findSupportedDefaultValue()
  {
    for (int i = 0; i < this.mDefaultValues.length; ++i)
      for (int j = 0; j < this.mEntryValues.length; ++j)
        if (this.mEntryValues[j].equals(this.mDefaultValues[i]))
          return this.mDefaultValues[i].toString();
    return null;
  }

  public void filterDuplicated()
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    int j = this.mEntryValues.length;
    while (i < j)
    {
      if (!localArrayList1.contains(this.mEntries[i]))
      {
        localArrayList1.add(this.mEntries[i]);
        localArrayList2.add(this.mEntryValues[i]);
      }
      ++i;
    }
    int k = localArrayList1.size();
    this.mEntries = ((CharSequence[])localArrayList1.toArray(new CharSequence[k]));
    this.mEntryValues = ((CharSequence[])localArrayList2.toArray(new CharSequence[k]));
  }

  public void filterUnsupported(List<String> paramList)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    int j = this.mEntryValues.length;
    while (i < j)
    {
      if (paramList.indexOf(this.mEntryValues[i].toString()) >= 0)
      {
        localArrayList1.add(this.mEntries[i]);
        localArrayList2.add(this.mEntryValues[i]);
      }
      ++i;
    }
    int k = localArrayList1.size();
    this.mEntries = ((CharSequence[])localArrayList1.toArray(new CharSequence[k]));
    this.mEntryValues = ((CharSequence[])localArrayList2.toArray(new CharSequence[k]));
  }

  public int findIndexOfValue(String paramString)
  {
    int i = 0;
    int j = this.mEntryValues.length;
    while (i < j)
    {
      if (Util.equals(this.mEntryValues[i], paramString))
        return i;
      ++i;
    }
    return -1;
  }

  public CharSequence[] getEntries()
  {
    return this.mEntries;
  }

  public String getEntry()
  {
    return this.mEntries[findIndexOfValue(getValue())].toString();
  }

  public CharSequence[] getEntryValues()
  {
    return this.mEntryValues;
  }

  public String getKey()
  {
    return this.mKey;
  }

  public String getValue()
  {
    if (!this.mLoaded)
    {
      this.mValue = getSharedPreferences().getString(this.mKey, findSupportedDefaultValue());
      this.mLoaded = true;
    }
    return this.mValue;
  }

  protected void persistStringValue(String paramString)
  {
    SharedPreferences.Editor localEditor = getSharedPreferences().edit();
    localEditor.putString(this.mKey, paramString);
    localEditor.apply();
  }

  public void print()
  {
    Log.v("ListPreference", "Preference key=" + getKey() + ". value=" + getValue());
    for (int i = 0; i < this.mEntryValues.length; ++i)
      Log.v("ListPreference", "entryValues[" + i + "]=" + this.mEntryValues[i]);
  }

  public void reloadValue()
  {
    this.mLoaded = false;
  }

  public void setEntries(CharSequence[] paramArrayOfCharSequence)
  {
    if (paramArrayOfCharSequence == null)
      paramArrayOfCharSequence = new CharSequence[0];
    this.mEntries = paramArrayOfCharSequence;
  }

  public void setEntryValues(CharSequence[] paramArrayOfCharSequence)
  {
    if (paramArrayOfCharSequence == null)
      paramArrayOfCharSequence = new CharSequence[0];
    this.mEntryValues = paramArrayOfCharSequence;
  }

  public void setValue(String paramString)
  {
    if (findIndexOfValue(paramString) < 0)
      throw new IllegalArgumentException();
    this.mValue = paramString;
    persistStringValue(paramString);
  }

  public void setValueIndex(int paramInt)
  {
    setValue(this.mEntryValues[paramInt].toString());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ListPreference
 * JD-Core Version:    0.5.4
 */