package com.android.gallery3d.app;

import android.app.ActionBar;
import android.app.ActionBar.OnMenuVisibilityListener;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.TwoLineListItem;
import com.android.gallery3d.ui.GLRoot;
import java.util.ArrayList;

public class GalleryActionBar
  implements ActionBar.OnNavigationListener
{
  private static final ActionItem[] sClusterItems;
  private ActionBar mActionBar;
  private Menu mActionBarMenu;
  private ArrayList<Integer> mActions;
  private AbstractGalleryActivity mActivity;
  private ClusterAdapter mAdapter = new ClusterAdapter(null);
  private AlbumModeAdapter mAlbumModeAdapter;
  private OnAlbumModeSelectedListener mAlbumModeListener;
  private CharSequence[] mAlbumModes;
  private ClusterRunner mClusterRunner;
  private Context mContext;
  private int mCurrentIndex;
  private LayoutInflater mInflater;
  private int mLastAlbumModeSelected;
  private ShareActionProvider mShareActionProvider;
  private Intent mShareIntent;
  private ShareActionProvider mSharePanoramaActionProvider;
  private Intent mSharePanoramaIntent;
  private CharSequence[] mTitles;

  static
  {
    ActionItem[] arrayOfActionItem = new ActionItem[5];
    arrayOfActionItem[0] = new ActionItem(1, true, false, 2131362312, 2131362240);
    arrayOfActionItem[1] = new ActionItem(4, true, false, 2131362314, 2131362261, 2131362236);
    arrayOfActionItem[2] = new ActionItem(2, true, false, 2131362313, 2131362260, 2131362237);
    arrayOfActionItem[3] = new ActionItem(32, true, false, 2131362315, 2131362239);
    arrayOfActionItem[4] = new ActionItem(8, true, false, 2131362316, 2131362238);
    sClusterItems = arrayOfActionItem;
  }

  public GalleryActionBar(AbstractGalleryActivity paramAbstractGalleryActivity)
  {
    this.mActionBar = paramAbstractGalleryActivity.getActionBar();
    this.mContext = paramAbstractGalleryActivity.getAndroidContext();
    this.mActivity = paramAbstractGalleryActivity;
    this.mInflater = this.mActivity.getLayoutInflater();
    this.mCurrentIndex = 0;
  }

  private void createDialogData()
  {
    ArrayList localArrayList = new ArrayList();
    this.mActions = new ArrayList();
    for (ActionItem localActionItem : sClusterItems)
    {
      if ((!localActionItem.enabled) || (!localActionItem.visible))
        continue;
      localArrayList.add(this.mContext.getString(localActionItem.dialogTitle));
      this.mActions.add(Integer.valueOf(localActionItem.action));
    }
    this.mTitles = new CharSequence[localArrayList.size()];
    localArrayList.toArray(this.mTitles);
  }

  public static String getClusterByTypeString(Context paramContext, int paramInt)
  {
    for (ActionItem localActionItem : sClusterItems)
      if (localActionItem.action == paramInt)
        return paramContext.getString(localActionItem.clusterBy);
    return null;
  }

  public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    if (this.mActionBar == null)
      return;
    this.mActionBar.addOnMenuVisibilityListener(paramOnMenuVisibilityListener);
  }

  public void createActionBarMenu(int paramInt, Menu paramMenu)
  {
    this.mActivity.getMenuInflater().inflate(paramInt, paramMenu);
    this.mActionBarMenu = paramMenu;
    MenuItem localMenuItem1 = paramMenu.findItem(2131558665);
    if (localMenuItem1 != null)
    {
      this.mSharePanoramaActionProvider = ((ShareActionProvider)localMenuItem1.getActionProvider());
      this.mSharePanoramaActionProvider.setShareHistoryFileName("panorama_share_history.xml");
      this.mSharePanoramaActionProvider.setShareIntent(this.mSharePanoramaIntent);
    }
    MenuItem localMenuItem2 = paramMenu.findItem(2131558663);
    if (localMenuItem2 == null)
      return;
    this.mShareActionProvider = ((ShareActionProvider)localMenuItem2.getActionProvider());
    this.mShareActionProvider.setShareHistoryFileName("share_history.xml");
    this.mShareActionProvider.setShareIntent(this.mShareIntent);
  }

  public void disableAlbumModeMenu(boolean paramBoolean)
  {
    if (this.mActionBar == null)
      return;
    this.mAlbumModeListener = null;
    if (!paramBoolean)
      return;
    this.mActionBar.setNavigationMode(0);
  }

  public void disableClusterMenu(boolean paramBoolean)
  {
    if (this.mActionBar == null)
      return;
    this.mClusterRunner = null;
    if (!paramBoolean)
      return;
    this.mActionBar.setNavigationMode(0);
  }

  public void enableAlbumModeMenu(int paramInt, OnAlbumModeSelectedListener paramOnAlbumModeSelectedListener)
  {
    if (this.mActionBar == null)
      return;
    if (this.mAlbumModeAdapter == null)
    {
      Resources localResources = this.mActivity.getResources();
      CharSequence[] arrayOfCharSequence = new CharSequence[2];
      arrayOfCharSequence[0] = localResources.getString(2131362327);
      arrayOfCharSequence[1] = localResources.getString(2131362328);
      this.mAlbumModes = arrayOfCharSequence;
      this.mAlbumModeAdapter = new AlbumModeAdapter(null);
    }
    this.mAlbumModeListener = null;
    this.mLastAlbumModeSelected = paramInt;
    this.mActionBar.setListNavigationCallbacks(this.mAlbumModeAdapter, this);
    this.mActionBar.setNavigationMode(1);
    this.mActionBar.setSelectedNavigationItem(paramInt);
    this.mAlbumModeListener = paramOnAlbumModeSelectedListener;
  }

  public void enableClusterMenu(int paramInt, ClusterRunner paramClusterRunner)
  {
    if (this.mActionBar == null)
      return;
    this.mClusterRunner = null;
    this.mActionBar.setListNavigationCallbacks(this.mAdapter, this);
    this.mActionBar.setNavigationMode(1);
    setSelectedAction(paramInt);
    this.mClusterRunner = paramClusterRunner;
  }

  public int getClusterTypeAction()
  {
    return sClusterItems[this.mCurrentIndex].action;
  }

  public int getHeight()
  {
    if (this.mActionBar != null)
      return this.mActionBar.getHeight();
    return 0;
  }

  public Menu getMenu()
  {
    return this.mActionBarMenu;
  }

  public void hide()
  {
    if (this.mActionBar == null)
      return;
    this.mActionBar.hide();
  }

  public void onConfigurationChanged()
  {
    if ((this.mActionBar == null) || (this.mAlbumModeListener == null))
      return;
    OnAlbumModeSelectedListener localOnAlbumModeSelectedListener = this.mAlbumModeListener;
    enableAlbumModeMenu(this.mLastAlbumModeSelected, localOnAlbumModeSelectedListener);
  }

  public boolean onNavigationItemSelected(int paramInt, long paramLong)
  {
    if (((paramInt != this.mCurrentIndex) && (this.mClusterRunner != null)) || (this.mAlbumModeListener != null))
      this.mActivity.getGLRoot().lockRenderThread();
    while (true)
      try
      {
        if (this.mAlbumModeListener != null)
        {
          this.mAlbumModeListener.onAlbumModeSelected(paramInt);
          return false;
        }
      }
      finally
      {
        this.mActivity.getGLRoot().unlockRenderThread();
      }
  }

  public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener)
  {
    if (this.mActionBar == null)
      return;
    this.mActionBar.removeOnMenuVisibilityListener(paramOnMenuVisibilityListener);
  }

  public void setClusterItemEnabled(int paramInt, boolean paramBoolean)
  {
    ActionItem[] arrayOfActionItem = sClusterItems;
    int i = arrayOfActionItem.length;
    for (int j = 0; ; ++j)
    {
      if (j < i)
      {
        ActionItem localActionItem = arrayOfActionItem[j];
        if (localActionItem.action != paramInt)
          continue;
        localActionItem.enabled = paramBoolean;
      }
      return;
    }
  }

  public void setClusterItemVisibility(int paramInt, boolean paramBoolean)
  {
    ActionItem[] arrayOfActionItem = sClusterItems;
    int i = arrayOfActionItem.length;
    for (int j = 0; ; ++j)
    {
      if (j < i)
      {
        ActionItem localActionItem = arrayOfActionItem[j];
        if (localActionItem.action != paramInt)
          continue;
        localActionItem.visible = paramBoolean;
      }
      return;
    }
  }

  public void setDisplayOptions(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mActionBar == null)
      return;
    int i = 0;
    if (paramBoolean1)
      i = 0x0 | 0x4;
    if (paramBoolean2)
      i |= 8;
    this.mActionBar.setDisplayOptions(i, 12);
    this.mActionBar.setHomeButtonEnabled(paramBoolean1);
  }

  public boolean setSelectedAction(int paramInt)
  {
    if (this.mActionBar == null)
      return false;
    int i = 0;
    int j = sClusterItems.length;
    while (true)
    {
      if (i < j);
      if (sClusterItems[i].action == paramInt)
      {
        this.mActionBar.setSelectedNavigationItem(i);
        this.mCurrentIndex = i;
        return true;
      }
      ++i;
    }
  }

  public void setShareIntents(Intent paramIntent1, Intent paramIntent2)
  {
    this.mSharePanoramaIntent = paramIntent1;
    if (this.mSharePanoramaActionProvider != null)
      this.mSharePanoramaActionProvider.setShareIntent(paramIntent1);
    this.mShareIntent = paramIntent2;
    if (this.mShareActionProvider == null)
      return;
    this.mShareActionProvider.setShareIntent(paramIntent2);
  }

  public void setSubtitle(String paramString)
  {
    if (this.mActionBar == null)
      return;
    this.mActionBar.setSubtitle(paramString);
  }

  public void setTitle(int paramInt)
  {
    if (this.mActionBar == null)
      return;
    this.mActionBar.setTitle(this.mContext.getString(paramInt));
  }

  public void setTitle(String paramString)
  {
    if (this.mActionBar == null)
      return;
    this.mActionBar.setTitle(paramString);
  }

  public void show()
  {
    if (this.mActionBar == null)
      return;
    this.mActionBar.show();
  }

  public void showClusterDialog(ClusterRunner paramClusterRunner)
  {
    createDialogData();
    ArrayList localArrayList = this.mActions;
    new AlertDialog.Builder(this.mContext).setTitle(2131362317).setItems(this.mTitles, new DialogInterface.OnClickListener(paramClusterRunner, localArrayList)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        GalleryActionBar.this.mActivity.getGLRoot().lockRenderThread();
        try
        {
          this.val$clusterRunner.doCluster(((Integer)this.val$actions.get(paramInt)).intValue());
          return;
        }
        finally
        {
          GalleryActionBar.this.mActivity.getGLRoot().unlockRenderThread();
        }
      }
    }).create().show();
  }

  private static class ActionItem
  {
    public int action;
    public int clusterBy;
    public int dialogTitle;
    public boolean enabled;
    public int spinnerTitle;
    public boolean visible;

    public ActionItem(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramBoolean1, paramBoolean2, paramInt2, paramInt2, paramInt3);
    }

    public ActionItem(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3, int paramInt4)
    {
      this.action = paramInt1;
      this.enabled = paramBoolean2;
      this.spinnerTitle = paramInt2;
      this.dialogTitle = paramInt3;
      this.clusterBy = paramInt4;
      this.visible = true;
    }
  }

  private class AlbumModeAdapter extends BaseAdapter
  {
    private AlbumModeAdapter()
    {
    }

    public int getCount()
    {
      return GalleryActionBar.this.mAlbumModes.length;
    }

    public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        paramView = GalleryActionBar.this.mInflater.inflate(2130968578, paramViewGroup, false);
      ((TextView)paramView).setText((CharSequence)getItem(paramInt));
      return paramView;
    }

    public Object getItem(int paramInt)
    {
      return GalleryActionBar.this.mAlbumModes[paramInt];
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        paramView = GalleryActionBar.this.mInflater.inflate(2130968579, paramViewGroup, false);
      TwoLineListItem localTwoLineListItem = (TwoLineListItem)paramView;
      localTwoLineListItem.getText1().setText(GalleryActionBar.this.mActionBar.getTitle());
      localTwoLineListItem.getText2().setText((CharSequence)getItem(paramInt));
      return paramView;
    }
  }

  private class ClusterAdapter extends BaseAdapter
  {
    private ClusterAdapter()
    {
    }

    public int getCount()
    {
      return GalleryActionBar.sClusterItems.length;
    }

    public Object getItem(int paramInt)
    {
      return GalleryActionBar.sClusterItems[paramInt];
    }

    public long getItemId(int paramInt)
    {
      return GalleryActionBar.sClusterItems[paramInt].action;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
        paramView = GalleryActionBar.this.mInflater.inflate(2130968578, paramViewGroup, false);
      ((TextView)paramView).setText(GalleryActionBar.sClusterItems[paramInt].spinnerTitle);
      return paramView;
    }
  }

  public static abstract interface ClusterRunner
  {
    public abstract void doCluster(int paramInt);
  }

  public static abstract interface OnAlbumModeSelectedListener
  {
    public abstract void onAlbumModeSelected(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.GalleryActionBar
 * JD-Core Version:    0.5.4
 */