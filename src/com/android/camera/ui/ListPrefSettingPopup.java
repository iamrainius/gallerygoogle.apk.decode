package com.android.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.android.camera.IconListPreference;
import com.android.camera.ListPreference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListPrefSettingPopup extends AbstractSettingPopup
  implements AdapterView.OnItemClickListener
{
  private Listener mListener;
  private ListPreference mPreference;

  public ListPrefSettingPopup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public void initialize(ListPreference paramListPreference)
  {
    this.mPreference = paramListPreference;
    Context localContext = getContext();
    CharSequence[] arrayOfCharSequence = this.mPreference.getEntries();
    boolean bool = paramListPreference instanceof IconListPreference;
    int[] arrayOfInt = null;
    if (bool)
    {
      arrayOfInt = ((IconListPreference)this.mPreference).getImageIds();
      if (arrayOfInt == null)
        arrayOfInt = ((IconListPreference)this.mPreference).getLargeIconIds();
    }
    this.mTitle.setText(this.mPreference.getTitle());
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < arrayOfCharSequence.length; ++i)
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("text", arrayOfCharSequence[i].toString());
      if (arrayOfInt != null)
        localHashMap.put("image", Integer.valueOf(arrayOfInt[i]));
      localArrayList.add(localHashMap);
    }
    ListPrefSettingAdapter localListPrefSettingAdapter = new ListPrefSettingAdapter(localContext, localArrayList, 2130968655, new String[] { "text", "image" }, new int[] { 2131558426, 2131558425 });
    ((ListView)this.mSettingList).setAdapter(localListPrefSettingAdapter);
    ((ListView)this.mSettingList).setOnItemClickListener(this);
    reloadPreference();
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    this.mPreference.setValueIndex(paramInt);
    if (this.mListener == null)
      return;
    this.mListener.onListPrefChanged(this.mPreference);
  }

  public void reloadPreference()
  {
    int i = this.mPreference.findIndexOfValue(this.mPreference.getValue());
    if (i != -1)
    {
      ((ListView)this.mSettingList).setItemChecked(i, true);
      return;
    }
    Log.e("ListPrefSettingPopup", "Invalid preference value.");
    this.mPreference.print();
  }

  public void setSettingChangedListener(Listener paramListener)
  {
    this.mListener = paramListener;
  }

  private class ListPrefSettingAdapter extends SimpleAdapter
  {
    ListPrefSettingAdapter(List<? extends Map<String, ?>> paramInt, int paramArrayOfString, String[] paramArrayOfInt, int[] arg5)
    {
      super(paramInt, paramArrayOfString, paramArrayOfInt, arrayOfString, arrayOfInt);
    }

    public void setViewImage(ImageView paramImageView, String paramString)
    {
      if ("".equals(paramString))
      {
        paramImageView.setVisibility(8);
        return;
      }
      super.setViewImage(paramImageView, paramString);
    }
  }

  public static abstract interface Listener
  {
    public abstract void onListPrefChanged(ListPreference paramListPreference);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.ListPrefSettingPopup
 * JD-Core Version:    0.5.4
 */