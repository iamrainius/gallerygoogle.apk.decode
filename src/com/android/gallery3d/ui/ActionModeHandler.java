package com.android.gallery3d.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Parcelable;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.MediaObject.PanoramaSupportCallback;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.ArrayList;
import java.util.Iterator;

public class ActionModeHandler
  implements ActionMode.Callback, PopupList.OnPopupItemClickListener
{
  private ActionMode mActionMode;
  private final AbstractGalleryActivity mActivity;
  private ActionModeListener mListener;
  private final Handler mMainHandler;
  private Menu mMenu;
  private final MenuExecutor mMenuExecutor;
  private Future<?> mMenuTask;
  private final NfcAdapter mNfcAdapter;
  private final SelectionManager mSelectionManager;
  private SelectionMenu mSelectionMenu;
  private ShareActionProvider mShareActionProvider;
  private MenuItem mShareMenuItem;
  private ShareActionProvider mSharePanoramaActionProvider;
  private MenuItem mSharePanoramaMenuItem;
  private final ShareActionProvider.OnShareTargetSelectedListener mShareTargetSelectedListener = new ShareActionProvider.OnShareTargetSelectedListener()
  {
    public boolean onShareTargetSelected(ShareActionProvider paramShareActionProvider, Intent paramIntent)
    {
      ActionModeHandler.this.mSelectionManager.leaveSelectionMode();
      return false;
    }
  };

  public ActionModeHandler(AbstractGalleryActivity paramAbstractGalleryActivity, SelectionManager paramSelectionManager)
  {
    this.mActivity = ((AbstractGalleryActivity)Utils.checkNotNull(paramAbstractGalleryActivity));
    this.mSelectionManager = ((SelectionManager)Utils.checkNotNull(paramSelectionManager));
    this.mMenuExecutor = new MenuExecutor(paramAbstractGalleryActivity, paramSelectionManager);
    this.mMainHandler = new Handler(paramAbstractGalleryActivity.getMainLooper());
    this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this.mActivity.getAndroidContext());
  }

  private int computeMenuOptions(ArrayList<MediaObject> paramArrayList)
  {
    int i = -1;
    int j = 0;
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      MediaObject localMediaObject = (MediaObject)localIterator.next();
      int k = localMediaObject.getSupportedOperations();
      j |= localMediaObject.getMediaType();
      i &= k;
    }
    switch (paramArrayList.size())
    {
    default:
      i &= 2311;
    case 1:
    }
    String str;
    do
    {
      return i;
      str = MenuExecutor.getMimeType(j);
    }
    while (GalleryUtils.isEditorAvailable(this.mActivity, str));
    return i & 0xFFFFFDFF;
  }

  private Intent computePanoramaSharingIntent(ThreadPool.JobContext paramJobContext)
  {
    ArrayList localArrayList1 = this.mSelectionManager.getSelected(true);
    Intent localIntent;
    if (localArrayList1.size() == 0)
      localIntent = null;
    ArrayList localArrayList2;
    int i;
    do
    {
      return localIntent;
      localArrayList2 = new ArrayList();
      DataManager localDataManager = this.mActivity.getDataManager();
      localIntent = new Intent();
      Iterator localIterator = localArrayList1.iterator();
      while (localIterator.hasNext())
      {
        Path localPath = (Path)localIterator.next();
        if (paramJobContext.isCancelled())
          return null;
        localArrayList2.add(localDataManager.getContentUri(localPath));
      }
      i = localArrayList2.size();
    }
    while (i <= 0);
    if (i > 1)
    {
      localIntent.setAction("android.intent.action.SEND_MULTIPLE");
      localIntent.setType("application/vnd.google.panorama360+jpg");
      localIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList2);
    }
    while (true)
    {
      localIntent.addFlags(1);
      return localIntent;
      localIntent.setAction("android.intent.action.SEND");
      localIntent.setType("application/vnd.google.panorama360+jpg");
      localIntent.putExtra("android.intent.extra.STREAM", (Parcelable)localArrayList2.get(0));
    }
  }

  private Intent computeSharingIntent(ThreadPool.JobContext paramJobContext)
  {
    ArrayList localArrayList1 = this.mSelectionManager.getSelected(true);
    if (localArrayList1.size() == 0)
    {
      setNfcBeamPushUris(null);
      return null;
    }
    ArrayList localArrayList2 = new ArrayList();
    DataManager localDataManager = this.mActivity.getDataManager();
    int i = 0;
    Intent localIntent = new Intent();
    Iterator localIterator = localArrayList1.iterator();
    while (localIterator.hasNext())
    {
      Path localPath = (Path)localIterator.next();
      if (paramJobContext.isCancelled())
        return null;
      int k = localDataManager.getSupportedOperations(localPath);
      i |= localDataManager.getMediaType(localPath);
      if ((k & 0x4) == 0)
        continue;
      localArrayList2.add(localDataManager.getContentUri(localPath));
    }
    int j = localArrayList2.size();
    if (j > 0)
    {
      String str = MenuExecutor.getMimeType(i);
      if (j > 1)
      {
        localIntent.setAction("android.intent.action.SEND_MULTIPLE").setType(str);
        localIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList2);
      }
      while (true)
      {
        localIntent.addFlags(1);
        setNfcBeamPushUris((Uri[])localArrayList2.toArray(new Uri[localArrayList2.size()]));
        return localIntent;
        localIntent.setAction("android.intent.action.SEND").setType(str);
        localIntent.putExtra("android.intent.extra.STREAM", (Parcelable)localArrayList2.get(0));
      }
    }
    setNfcBeamPushUris(null);
    return localIntent;
  }

  private ArrayList<MediaObject> getSelectedMediaObjects(ThreadPool.JobContext paramJobContext)
  {
    ArrayList localArrayList1 = this.mSelectionManager.getSelected(false);
    if (localArrayList1.isEmpty())
    {
      localArrayList2 = null;
      return localArrayList2;
    }
    ArrayList localArrayList2 = new ArrayList();
    DataManager localDataManager = this.mActivity.getDataManager();
    Iterator localIterator = localArrayList1.iterator();
    while (true)
    {
      if (localIterator.hasNext());
      Path localPath = (Path)localIterator.next();
      if (paramJobContext.isCancelled())
        return null;
      localArrayList2.add(localDataManager.getMediaObject(localPath));
    }
  }

  @TargetApi(16)
  private void setNfcBeamPushUris(Uri[] paramArrayOfUri)
  {
    if ((this.mNfcAdapter == null) || (!ApiHelper.HAS_SET_BEAM_PUSH_URIS))
      return;
    this.mNfcAdapter.setBeamPushUrisCallback(null, this.mActivity);
    this.mNfcAdapter.setBeamPushUris(paramArrayOfUri, this.mActivity);
  }

  private void updateSelectionMenu()
  {
    int i = this.mSelectionManager.getSelectedCount();
    String str = this.mActivity.getResources().getQuantityString(2131820545, i);
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Integer.valueOf(i);
    setTitle(String.format(str, arrayOfObject));
    this.mSelectionMenu.updateSelectAllMode(this.mSelectionManager.inSelectAllMode());
  }

  public void finishActionMode()
  {
    this.mActionMode.finish();
  }

  public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
  {
    GLRoot localGLRoot = this.mActivity.getGLRoot();
    localGLRoot.lockRenderThread();
    ImportCompleteListener localImportCompleteListener;
    try
    {
      if (this.mListener != null)
      {
        boolean bool = this.mListener.onActionItemClicked(paramMenuItem);
        if (bool)
        {
          this.mSelectionManager.leaveSelectionMode();
          return bool;
        }
      }
      Object localObject2 = null;
      if (paramMenuItem.getItemId() == 2131558664)
        localImportCompleteListener = new ImportCompleteListener(this.mActivity);
      int i;
      do
      {
        this.mMenuExecutor.onMenuClicked(paramMenuItem, (String)localObject2, localImportCompleteListener);
        return true;
        i = paramMenuItem.getItemId();
        localObject2 = null;
        localImportCompleteListener = null;
      }
      while (i != 2131558666);
      String str = this.mActivity.getResources().getQuantityString(2131820544, this.mSelectionManager.getSelectedCount());
      localObject2 = str;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
  {
    paramActionMode.getMenuInflater().inflate(2131886090, paramMenu);
    this.mMenu = paramMenu;
    this.mSharePanoramaMenuItem = paramMenu.findItem(2131558665);
    if (this.mSharePanoramaMenuItem != null)
    {
      this.mSharePanoramaActionProvider = ((ShareActionProvider)this.mSharePanoramaMenuItem.getActionProvider());
      this.mSharePanoramaActionProvider.setOnShareTargetSelectedListener(this.mShareTargetSelectedListener);
      this.mSharePanoramaActionProvider.setShareHistoryFileName("panorama_share_history.xml");
    }
    this.mShareMenuItem = paramMenu.findItem(2131558663);
    if (this.mShareMenuItem != null)
    {
      this.mShareActionProvider = ((ShareActionProvider)this.mShareMenuItem.getActionProvider());
      this.mShareActionProvider.setOnShareTargetSelectedListener(this.mShareTargetSelectedListener);
      this.mShareActionProvider.setShareHistoryFileName("share_history.xml");
    }
    return true;
  }

  public void onDestroyActionMode(ActionMode paramActionMode)
  {
    this.mSelectionManager.leaveSelectionMode();
  }

  public boolean onPopupItemClick(int paramInt)
  {
    GLRoot localGLRoot = this.mActivity.getGLRoot();
    localGLRoot.lockRenderThread();
    if (paramInt == 2131558403);
    try
    {
      updateSupportedOperation();
      this.mMenuExecutor.onMenuClicked(paramInt, null, false, true);
      return true;
    }
    finally
    {
      localGLRoot.unlockRenderThread();
    }
  }

  public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
  {
    return false;
  }

  public void pause()
  {
    if (this.mMenuTask != null)
    {
      this.mMenuTask.cancel();
      this.mMenuTask = null;
    }
    this.mMenuExecutor.pause();
  }

  public void resume()
  {
    if (!this.mSelectionManager.inSelectionMode())
      return;
    updateSupportedOperation();
  }

  public void setActionModeListener(ActionModeListener paramActionModeListener)
  {
    this.mListener = paramActionModeListener;
  }

  public void setTitle(String paramString)
  {
    this.mSelectionMenu.setTitle(paramString);
  }

  public void startActionMode()
  {
    AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
    this.mActionMode = localAbstractGalleryActivity.startActionMode(this);
    View localView = LayoutInflater.from(localAbstractGalleryActivity).inflate(2130968580, null);
    this.mActionMode.setCustomView(localView);
    this.mSelectionMenu = new SelectionMenu(localAbstractGalleryActivity, (Button)localView.findViewById(2131558407), this);
    updateSelectionMenu();
  }

  public void updateSupportedOperation()
  {
    if (this.mMenuTask != null)
      this.mMenuTask.cancel();
    updateSelectionMenu();
    if (this.mSharePanoramaMenuItem != null)
      this.mSharePanoramaMenuItem.setEnabled(false);
    if (this.mShareMenuItem != null)
      this.mShareMenuItem.setEnabled(false);
    this.mMenuTask = this.mActivity.getThreadPool().submit(new ThreadPool.Job()
    {
      public Void run(ThreadPool.JobContext paramJobContext)
      {
        ArrayList localArrayList = ActionModeHandler.this.getSelectedMediaObjects(paramJobContext);
        if (localArrayList == null);
        int i;
        ActionModeHandler.GetAllPanoramaSupports localGetAllPanoramaSupports;
        Intent localIntent1;
        Intent localIntent2;
        do
        {
          do
          {
            return null;
            i = ActionModeHandler.this.computeMenuOptions(localArrayList);
          }
          while (paramJobContext.isCancelled());
          localGetAllPanoramaSupports = new ActionModeHandler.GetAllPanoramaSupports(localArrayList, paramJobContext);
          localIntent1 = ActionModeHandler.this.computePanoramaSharingIntent(paramJobContext);
          localIntent2 = ActionModeHandler.this.computeSharingIntent(paramJobContext);
          localGetAllPanoramaSupports.waitForPanoramaSupport();
        }
        while (paramJobContext.isCancelled());
        ActionModeHandler.this.mMainHandler.post(new Runnable(paramJobContext, i, localGetAllPanoramaSupports, localIntent1, localIntent2)
        {
          public void run()
          {
            ActionModeHandler.access$502(ActionModeHandler.this, null);
            if (this.val$jc.isCancelled())
              return;
            MenuExecutor.updateMenuOperation(ActionModeHandler.this.mMenu, this.val$operation);
            MenuExecutor.updateMenuForPanorama(ActionModeHandler.this.mMenu, this.val$supportCallback.mAllPanorama360, this.val$supportCallback.mHasPanorama360);
            if (ActionModeHandler.this.mSharePanoramaMenuItem != null)
            {
              ActionModeHandler.this.mSharePanoramaMenuItem.setEnabled(true);
              if (!this.val$supportCallback.mAllPanorama360)
                break label224;
              ActionModeHandler.this.mShareMenuItem.setShowAsAction(0);
              ActionModeHandler.this.mShareMenuItem.setTitle(ActionModeHandler.this.mActivity.getResources().getString(2131362215));
            }
            while (true)
            {
              ActionModeHandler.this.mSharePanoramaActionProvider.setShareIntent(this.val$share_panorama_intent);
              if (ActionModeHandler.this.mShareMenuItem != null);
              ActionModeHandler.this.mShareMenuItem.setEnabled(true);
              ActionModeHandler.this.mShareActionProvider.setShareIntent(this.val$share_intent);
              return;
              label224: ActionModeHandler.this.mSharePanoramaMenuItem.setVisible(false);
              ActionModeHandler.this.mShareMenuItem.setShowAsAction(1);
              ActionModeHandler.this.mShareMenuItem.setTitle(ActionModeHandler.this.mActivity.getResources().getString(2131362213));
            }
          }
        });
        return null;
      }
    });
  }

  public void updateSupportedOperation(Path paramPath, boolean paramBoolean)
  {
    updateSupportedOperation();
  }

  public static abstract interface ActionModeListener
  {
    public abstract boolean onActionItemClicked(MenuItem paramMenuItem);
  }

  private static class GetAllPanoramaSupports
    implements MediaObject.PanoramaSupportCallback
  {
    public boolean mAllPanorama360 = true;
    public boolean mAllPanoramas = true;
    public boolean mHasPanorama360 = false;
    private ThreadPool.JobContext mJobContext;
    private Object mLock = new Object();
    private int mNumInfoRequired;

    public GetAllPanoramaSupports(ArrayList<MediaObject> paramArrayList, ThreadPool.JobContext paramJobContext)
    {
      this.mJobContext = paramJobContext;
      this.mNumInfoRequired = paramArrayList.size();
      Iterator localIterator = paramArrayList.iterator();
      while (localIterator.hasNext())
        ((MediaObject)localIterator.next()).getPanoramaSupport(this);
    }

    public void panoramaInfoAvailable(MediaObject paramMediaObject, boolean paramBoolean1, boolean paramBoolean2)
    {
      while (true)
      {
        synchronized (this.mLock)
        {
          this.mNumInfoRequired = (-1 + this.mNumInfoRequired);
          if ((!paramBoolean1) || (!this.mAllPanoramas))
            break label126;
          i = 1;
          this.mAllPanoramas = i;
          if ((!paramBoolean2) || (!this.mAllPanorama360))
            break label132;
          j = 1;
          this.mAllPanorama360 = j;
          if (!this.mHasPanorama360)
          {
            k = 0;
            if (!paramBoolean2)
            {
              this.mHasPanorama360 = k;
              if ((this.mNumInfoRequired == 0) || (this.mJobContext.isCancelled()))
                this.mLock.notifyAll();
              return;
            }
          }
        }
        int k = 1;
        continue;
        label126: int i = 0;
        continue;
        label132: int j = 0;
      }
    }

    public void waitForPanoramaSupport()
    {
      synchronized (this.mLock)
      {
        while (this.mNumInfoRequired != 0)
        {
          boolean bool = this.mJobContext.isCancelled();
          if (bool)
            break;
          try
          {
            this.mLock.wait();
          }
          catch (InterruptedException localInterruptedException)
          {
          }
        }
        return;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.ActionModeHandler
 * JD-Core Version:    0.5.4
 */