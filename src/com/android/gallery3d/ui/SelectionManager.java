package com.android.gallery3d.ui;

import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SelectionManager
{
  private boolean mAutoLeave = true;
  private Set<Path> mClickedSet;
  private DataManager mDataManager;
  private boolean mInSelectionMode;
  private boolean mInverseSelection;
  private boolean mIsAlbumSet;
  private SelectionListener mListener;
  private MediaSet mSourceMediaSet;
  private int mTotal;

  public SelectionManager(AbstractGalleryActivity paramAbstractGalleryActivity, boolean paramBoolean)
  {
    this.mDataManager = paramAbstractGalleryActivity.getDataManager();
    this.mClickedSet = new HashSet();
    this.mIsAlbumSet = paramBoolean;
    this.mTotal = -1;
  }

  private static void expandMediaSet(ArrayList<Path> paramArrayList, MediaSet paramMediaSet)
  {
    int i = paramMediaSet.getSubMediaSetCount();
    for (int j = 0; j < i; ++j)
      expandMediaSet(paramArrayList, paramMediaSet.getSubMediaSet(j));
    int k = paramMediaSet.getMediaItemCount();
    for (int l = 0; l < k; l += 50)
    {
      if (l + 50 < k);
      for (int i1 = 50; ; i1 = k - l)
      {
        Iterator localIterator = paramMediaSet.getMediaItem(l, i1).iterator();
        while (true)
        {
          if (!localIterator.hasNext())
            break label111;
          label111: paramArrayList.add(((MediaItem)localIterator.next()).getPath());
        }
      }
    }
  }

  private int getTotalCount()
  {
    if (this.mSourceMediaSet == null)
      return -1;
    if (this.mTotal < 0)
      if (!this.mIsAlbumSet)
        break label41;
    for (int i = this.mSourceMediaSet.getSubMediaSetCount(); ; i = this.mSourceMediaSet.getMediaItemCount())
    {
      this.mTotal = i;
      label41: return this.mTotal;
    }
  }

  public void deSelectAll()
  {
    leaveSelectionMode();
    this.mInverseSelection = false;
    this.mClickedSet.clear();
  }

  public void enterSelectionMode()
  {
    if (this.mInSelectionMode);
    do
    {
      return;
      this.mInSelectionMode = true;
    }
    while (this.mListener == null);
    this.mListener.onSelectionModeChange(1);
  }

  public ArrayList<Path> getSelected(boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    if (this.mIsAlbumSet)
    {
      if (this.mInverseSelection)
      {
        int l = getTotalCount();
        int i1 = 0;
        label31: if (i1 >= l)
          break label306;
        MediaSet localMediaSet = this.mSourceMediaSet.getSubMediaSet(i1);
        Path localPath3 = localMediaSet.getPath();
        if (!this.mClickedSet.contains(localPath3))
        {
          if (!paramBoolean)
            break label86;
          expandMediaSet(localArrayList, localMediaSet);
        }
        while (true)
        {
          ++i1;
          break label31:
          label86: localArrayList.add(localPath3);
        }
      }
      Iterator localIterator3 = this.mClickedSet.iterator();
      while (true)
      {
        if (!localIterator3.hasNext())
          break label306;
        Path localPath2 = (Path)localIterator3.next();
        if (paramBoolean)
          expandMediaSet(localArrayList, this.mDataManager.getMediaSet(localPath2));
        localArrayList.add(localPath2);
      }
    }
    if (this.mInverseSelection)
    {
      int i = getTotalCount();
      int j = 0;
      while (true)
      {
        if (j >= i)
          break label306;
        int k = Math.min(i - j, 500);
        Iterator localIterator2 = this.mSourceMediaSet.getMediaItem(j, k).iterator();
        while (localIterator2.hasNext())
        {
          Path localPath1 = ((MediaItem)localIterator2.next()).getPath();
          if (this.mClickedSet.contains(localPath1))
            continue;
          localArrayList.add(localPath1);
        }
        j += k;
      }
    }
    Iterator localIterator1 = this.mClickedSet.iterator();
    while (localIterator1.hasNext())
      localArrayList.add((Path)localIterator1.next());
    label306: return localArrayList;
  }

  public int getSelectedCount()
  {
    int i = this.mClickedSet.size();
    if (this.mInverseSelection)
      i = getTotalCount() - i;
    return i;
  }

  public boolean inSelectAllMode()
  {
    return this.mInverseSelection;
  }

  public boolean inSelectionMode()
  {
    return this.mInSelectionMode;
  }

  public boolean isItemSelected(Path paramPath)
  {
    return this.mInverseSelection ^ this.mClickedSet.contains(paramPath);
  }

  public void leaveSelectionMode()
  {
    if (!this.mInSelectionMode);
    do
    {
      return;
      this.mInSelectionMode = false;
      this.mInverseSelection = false;
      this.mClickedSet.clear();
    }
    while (this.mListener == null);
    this.mListener.onSelectionModeChange(2);
  }

  public void selectAll()
  {
    this.mInverseSelection = true;
    this.mClickedSet.clear();
    enterSelectionMode();
    if (this.mListener == null)
      return;
    this.mListener.onSelectionModeChange(3);
  }

  public void setAutoLeaveSelectionMode(boolean paramBoolean)
  {
    this.mAutoLeave = paramBoolean;
  }

  public void setSelectionListener(SelectionListener paramSelectionListener)
  {
    this.mListener = paramSelectionListener;
  }

  public void setSourceMediaSet(MediaSet paramMediaSet)
  {
    this.mSourceMediaSet = paramMediaSet;
    this.mTotal = -1;
  }

  public void toggle(Path paramPath)
  {
    if (this.mClickedSet.contains(paramPath))
      this.mClickedSet.remove(paramPath);
    while (true)
    {
      int i = getSelectedCount();
      if (i == getTotalCount())
        selectAll();
      if (this.mListener != null)
        this.mListener.onSelectionChange(paramPath, isItemSelected(paramPath));
      if ((i == 0) && (this.mAutoLeave))
        leaveSelectionMode();
      return;
      enterSelectionMode();
      this.mClickedSet.add(paramPath);
    }
  }

  public static abstract interface SelectionListener
  {
    public abstract void onSelectionChange(Path paramPath, boolean paramBoolean);

    public abstract void onSelectionModeChange(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.SelectionManager
 * JD-Core Version:    0.5.4
 */