package com.android.camera.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.android.camera.IconListPreference;
import java.util.ArrayList;
import java.util.HashMap;

public class EffectSettingPopup extends AbstractSettingPopup
  implements View.OnClickListener, AdapterView.OnItemClickListener
{
  private GridView mBackgroundGrid;
  ArrayList<HashMap<String, Object>> mBackgroundItem = new ArrayList();
  private View mClearEffects;
  private Listener mListener;
  private String mNoEffect;
  private IconListPreference mPreference;
  private GridView mSillyFacesGrid;
  ArrayList<HashMap<String, Object>> mSillyFacesItem = new ArrayList();

  public EffectSettingPopup(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mNoEffect = paramContext.getString(2131361976);
  }

  public void onClick(View paramView)
  {
    this.mPreference.setValue(this.mNoEffect);
    reloadPreference();
    if (this.mListener == null)
      return;
    this.mListener.onSettingChanged();
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mClearEffects = findViewById(2131558427);
    this.mClearEffects.setOnClickListener(this);
    this.mSillyFacesGrid = ((GridView)findViewById(2131558430));
    this.mBackgroundGrid = ((GridView)findViewById(2131558434));
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    String str;
    if (paramAdapterView == this.mSillyFacesGrid)
    {
      str = (String)((HashMap)this.mSillyFacesItem.get(paramInt)).get("value");
      label29: if (!str.equals(this.mPreference.getValue()))
        break label108;
      this.mPreference.setValue(this.mNoEffect);
    }
    while (true)
    {
      reloadPreference();
      if (this.mListener != null)
        this.mListener.onSettingChanged();
      do
        return;
      while (paramAdapterView != this.mBackgroundGrid);
      str = (String)((HashMap)this.mBackgroundItem.get(paramInt)).get("value");
      break label29:
      label108: this.mPreference.setValue(str);
    }
  }

  @TargetApi(11)
  public void reloadPreference()
  {
    this.mBackgroundGrid.setItemChecked(this.mBackgroundGrid.getCheckedItemPosition(), false);
    this.mSillyFacesGrid.setItemChecked(this.mSillyFacesGrid.getCheckedItemPosition(), false);
    String str = this.mPreference.getValue();
    if (str.equals(this.mNoEffect))
      return;
    for (int i = 0; i < this.mSillyFacesItem.size(); ++i)
    {
      if (!str.equals(((HashMap)this.mSillyFacesItem.get(i)).get("value")))
        continue;
      this.mSillyFacesGrid.setItemChecked(i, true);
      return;
    }
    for (int j = 0; j < this.mBackgroundItem.size(); ++j)
    {
      if (!str.equals(((HashMap)this.mBackgroundItem.get(j)).get("value")))
        continue;
      this.mBackgroundGrid.setItemChecked(j, true);
      return;
    }
    Log.e("EffectSettingPopup", "Invalid preference value: " + str);
    this.mPreference.print();
  }

  public void setVisibility(int paramInt)
  {
    View localView;
    if (paramInt == 0)
      if (getVisibility() != 0)
      {
        boolean bool = this.mPreference.getValue().equals(this.mNoEffect);
        localView = this.mClearEffects;
        if (!bool)
          break label55;
      }
    for (int i = 8; ; i = 0)
    {
      localView.setVisibility(i);
      reloadPreference();
      super.setVisibility(paramInt);
      label55: return;
    }
  }

  public static abstract interface Listener
  {
    public abstract void onSettingChanged();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.EffectSettingPopup
 * JD-Core Version:    0.5.4
 */