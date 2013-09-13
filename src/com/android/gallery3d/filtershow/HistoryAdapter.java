package com.android.gallery3d.filtershow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.gallery3d.filtershow.presets.ImagePreset;
import java.util.Vector;

public class HistoryAdapter extends ArrayAdapter<ImagePreset>
{
  private String mBorders = null;
  private String mCrop = null;
  private int mCurrentPresetPosition = 0;
  private String mMirror = null;
  private MenuItem mRedoMenuItem = null;
  private MenuItem mResetMenuItem = null;
  private String mRotate = null;
  private String mStraighten = null;
  private MenuItem mUndoMenuItem = null;

  public HistoryAdapter(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
    ((FilterShowActivity)paramContext);
    this.mBorders = paramContext.getString(2131362091);
    this.mCrop = paramContext.getString(2131362131);
    this.mRotate = paramContext.getString(2131362132);
    this.mStraighten = paramContext.getString(2131362130);
    this.mMirror = paramContext.getString(2131362133);
  }

  public void addHistoryItem(ImagePreset paramImagePreset)
  {
    if (!canAddHistoryItem(paramImagePreset))
      return;
    insert(paramImagePreset, 0);
    updateMenuItems();
  }

  public boolean canAddHistoryItem(ImagePreset paramImagePreset)
  {
    return (getCount() <= 0) || (!getLast().same(paramImagePreset)) || (!getLast().historyName().equalsIgnoreCase(paramImagePreset.historyName()));
  }

  public boolean canRedo()
  {
    return this.mCurrentPresetPosition != 0;
  }

  public boolean canReset()
  {
    int i = 1;
    if (getCount() <= i)
      i = 0;
    return i;
  }

  public boolean canUndo()
  {
    return this.mCurrentPresetPosition != -1 + getCount();
  }

  public ImagePreset getLast()
  {
    if (getCount() == 0)
      return null;
    return (ImagePreset)getItem(0);
  }

  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    View localView = paramView;
    if (localView == null)
      localView = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(2130968598, null);
    ImagePreset localImagePreset = (ImagePreset)getItem(paramInt);
    ImageView localImageView1;
    if (localImagePreset != null)
    {
      TextView localTextView = (TextView)localView.findViewById(2131558489);
      if (localTextView != null)
        localTextView.setText(localImagePreset.historyName());
      localImageView1 = (ImageView)localView.findViewById(2131558488);
      if (paramInt != this.mCurrentPresetPosition)
        break label128;
      localImageView1.setVisibility(0);
    }
    ImageView localImageView2;
    while (true)
    {
      localImageView2 = (ImageView)localView.findViewById(2131558490);
      if (paramInt != -1 + getCount())
        break;
      localImageView2.setImageResource(2130837739);
      return localView;
      label128: localImageView1.setVisibility(4);
    }
    if (localImagePreset.historyName().equalsIgnoreCase(this.mBorders))
    {
      localImageView2.setImageResource(2130837737);
      return localView;
    }
    if (localImagePreset.historyName().equalsIgnoreCase(this.mStraighten))
    {
      localImageView2.setImageResource(2130837740);
      return localView;
    }
    if (localImagePreset.historyName().equalsIgnoreCase(this.mCrop))
    {
      localImageView2.setImageResource(2130837740);
      return localView;
    }
    if (localImagePreset.historyName().equalsIgnoreCase(this.mRotate))
    {
      localImageView2.setImageResource(2130837740);
      return localView;
    }
    if (localImagePreset.historyName().equalsIgnoreCase(this.mMirror))
    {
      localImageView2.setImageResource(2130837740);
      return localView;
    }
    if (localImagePreset.isFx())
    {
      localImageView2.setImageResource(2130837739);
      return localView;
    }
    localImageView2.setImageResource(2130837738);
    return localView;
  }

  public void insert(ImagePreset paramImagePreset, int paramInt)
  {
    if (this.mCurrentPresetPosition != 0)
    {
      Vector localVector = new Vector();
      for (int i = this.mCurrentPresetPosition; i < getCount(); ++i)
        localVector.add(getItem(i));
      clear();
      for (int j = 0; j < localVector.size(); ++j)
        add(localVector.elementAt(j));
      this.mCurrentPresetPosition = paramInt;
      notifyDataSetChanged();
      if (!canAddHistoryItem(paramImagePreset))
        return;
    }
    super.insert(paramImagePreset, paramInt);
    this.mCurrentPresetPosition = paramInt;
    notifyDataSetChanged();
  }

  public int redo()
  {
    this.mCurrentPresetPosition = (-1 + this.mCurrentPresetPosition);
    if (this.mCurrentPresetPosition < 0)
      this.mCurrentPresetPosition = 0;
    notifyDataSetChanged();
    updateMenuItems();
    return this.mCurrentPresetPosition;
  }

  public void reset()
  {
    if (getCount() == 0)
      return;
    ImagePreset localImagePreset = (ImagePreset)getItem(-1 + getCount());
    clear();
    addHistoryItem(localImagePreset);
    updateMenuItems();
  }

  public void setCurrentPreset(int paramInt)
  {
    this.mCurrentPresetPosition = paramInt;
    updateMenuItems();
    notifyDataSetChanged();
  }

  public void setMenuItems(MenuItem paramMenuItem1, MenuItem paramMenuItem2, MenuItem paramMenuItem3)
  {
    this.mUndoMenuItem = paramMenuItem1;
    this.mRedoMenuItem = paramMenuItem2;
    this.mResetMenuItem = paramMenuItem3;
    updateMenuItems();
  }

  public int undo()
  {
    this.mCurrentPresetPosition = (1 + this.mCurrentPresetPosition);
    if (this.mCurrentPresetPosition >= getCount())
      this.mCurrentPresetPosition = (-1 + getCount());
    notifyDataSetChanged();
    updateMenuItems();
    return this.mCurrentPresetPosition;
  }

  public void updateMenuItems()
  {
    if (this.mUndoMenuItem != null)
      this.mUndoMenuItem.setEnabled(canUndo());
    if (this.mRedoMenuItem != null)
      this.mRedoMenuItem.setEnabled(canRedo());
    if (this.mResetMenuItem == null)
      return;
    this.mResetMenuItem.setEnabled(canReset());
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.HistoryAdapter
 * JD-Core Version:    0.5.4
 */