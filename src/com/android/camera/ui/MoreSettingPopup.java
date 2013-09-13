package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.android.camera.ListPreference;
import com.android.camera.PreferenceGroup;
import java.util.ArrayList;

public class MoreSettingPopup extends AbstractSettingPopup
  implements AdapterView.OnItemClickListener, InLineSettingItem.Listener
{
  private boolean[] mEnabled;
  private ArrayList<ListPreference> mListItem = new ArrayList();
  private Listener mListener;

  public MoreSettingPopup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public void initialize(PreferenceGroup paramPreferenceGroup, String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; ++i)
    {
      ListPreference localListPreference = paramPreferenceGroup.findPreference(paramArrayOfString[i]);
      if (localListPreference == null)
        continue;
      this.mListItem.add(localListPreference);
    }
    MoreSettingAdapter localMoreSettingAdapter = new MoreSettingAdapter();
    ((ListView)this.mSettingList).setAdapter(localMoreSettingAdapter);
    ((ListView)this.mSettingList).setOnItemClickListener(this);
    ((ListView)this.mSettingList).setSelector(17170445);
    this.mEnabled = new boolean[this.mListItem.size()];
    for (int j = 0; j < this.mEnabled.length; ++j)
      this.mEnabled[j] = true;
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if (this.mListener == null)
      return;
    ListPreference localListPreference = (ListPreference)this.mListItem.get(paramInt);
    this.mListener.onPreferenceClicked(localListPreference);
  }

  public void onSettingChanged(ListPreference paramListPreference)
  {
    if (this.mListener == null)
      return;
    this.mListener.onSettingChanged(paramListPreference);
  }

  public void overrideSettings(String[] paramArrayOfString)
  {
    int i;
    if (this.mEnabled == null)
      i = 0;
    for (int j = 0; j < paramArrayOfString.length; j += 2)
    {
      label9: String str1 = paramArrayOfString[j];
      String str2 = paramArrayOfString[(j + 1)];
      int k = 0;
      label32: if (k >= i)
        continue;
      ListPreference localListPreference = (ListPreference)this.mListItem.get(k);
      if ((localListPreference != null) && (str1.equals(localListPreference.getKey())))
      {
        if (str2 != null)
          localListPreference.setValue(str2);
        if (str2 != null)
          break label140;
      }
      for (boolean bool = true; ; bool = false)
      {
        this.mEnabled[k] = bool;
        if (this.mSettingList.getChildCount() > k)
          this.mSettingList.getChildAt(k).setEnabled(bool);
        ++k;
        break label32:
        i = this.mEnabled.length;
        label140: break label9:
      }
    }
    reloadPreference();
  }

  public void reloadPreference()
  {
    int i = this.mSettingList.getChildCount();
    for (int j = 0; j < i; ++j)
    {
      if ((ListPreference)this.mListItem.get(j) == null)
        continue;
      ((InLineSettingItem)this.mSettingList.getChildAt(j)).reloadPreference();
    }
  }

  public void setSettingChangedListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  public static abstract interface Listener
  {
    public abstract void onPreferenceClicked(ListPreference paramListPreference);

    public abstract void onSettingChanged(ListPreference paramListPreference);
  }

  private class MoreSettingAdapter extends ArrayAdapter<ListPreference>
  {
    LayoutInflater mInflater;
    String mOffString;
    String mOnString;

    MoreSettingAdapter()
    {
      super(MoreSettingPopup.this.getContext(), 0, MoreSettingPopup.this.mListItem);
      Context localContext = getContext();
      this.mInflater = LayoutInflater.from(localContext);
      this.mOnString = localContext.getString(2131361908);
      this.mOffString = localContext.getString(2131361907);
    }

    private int getSettingLayoutId(ListPreference paramListPreference)
    {
      if (isOnOffPreference(paramListPreference))
        return 2130968609;
      return 2130968608;
    }

    private boolean isOnOffPreference(ListPreference paramListPreference)
    {
      CharSequence[] arrayOfCharSequence = paramListPreference.getEntries();
      if (arrayOfCharSequence.length != 2);
      String str1;
      String str2;
      do
      {
        return false;
        str1 = arrayOfCharSequence[0].toString();
        str2 = arrayOfCharSequence[1].toString();
      }
      while ((((!str1.equals(this.mOnString)) || (!str2.equals(this.mOffString)))) && (((!str1.equals(this.mOffString)) || (!str2.equals(this.mOnString)))));
      return true;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView != null)
        return paramView;
      ListPreference localListPreference = (ListPreference)MoreSettingPopup.this.mListItem.get(paramInt);
      int i = getSettingLayoutId(localListPreference);
      InLineSettingItem localInLineSettingItem = (InLineSettingItem)this.mInflater.inflate(i, paramViewGroup, false);
      localInLineSettingItem.initialize(localListPreference);
      localInLineSettingItem.setSettingChangedListener(MoreSettingPopup.this);
      if ((paramInt >= 0) && (paramInt < MoreSettingPopup.this.mEnabled.length))
        localInLineSettingItem.setEnabled(MoreSettingPopup.this.mEnabled[paramInt]);
      while (true)
      {
        return localInLineSettingItem;
        Log.w("MoreSettingPopup", "Invalid input: enabled list length, " + MoreSettingPopup.this.mEnabled.length + " position " + paramInt);
      }
    }

    public boolean isEnabled(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < MoreSettingPopup.this.mEnabled.length))
        return MoreSettingPopup.this.mEnabled[paramInt];
      return true;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.MoreSettingPopup
 * JD-Core Version:    0.5.4
 */