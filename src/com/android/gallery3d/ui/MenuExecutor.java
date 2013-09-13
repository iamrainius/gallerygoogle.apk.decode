package com.android.gallery3d.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.ArrayList;

public class MenuExecutor
{
  private final AbstractGalleryActivity mActivity;
  private ProgressDialog mDialog;
  private final Handler mHandler;
  private final SelectionManager mSelectionManager;
  private Future<?> mTask;
  private boolean mWaitOnStop;

  public MenuExecutor(AbstractGalleryActivity paramAbstractGalleryActivity, SelectionManager paramSelectionManager)
  {
    this.mActivity = ((AbstractGalleryActivity)Utils.checkNotNull(paramAbstractGalleryActivity));
    this.mSelectionManager = ((SelectionManager)Utils.checkNotNull(paramSelectionManager));
    this.mHandler = new SynchronizedHandler(this.mActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default:
        case 3:
        case 1:
        case 2:
          do
          {
            do
              return;
            while (paramMessage.obj == null);
            ((MenuExecutor.ProgressListener)paramMessage.obj).onProgressStart();
            return;
            MenuExecutor.this.stopTaskAndDismissDialog();
            if (paramMessage.obj != null)
              ((MenuExecutor.ProgressListener)paramMessage.obj).onProgressComplete(paramMessage.arg1);
            MenuExecutor.this.mSelectionManager.leaveSelectionMode();
            return;
            if (MenuExecutor.this.mDialog == null)
              continue;
            MenuExecutor.this.mDialog.setProgress(paramMessage.arg1);
          }
          while (paramMessage.obj == null);
          ((MenuExecutor.ProgressListener)paramMessage.obj).onProgressUpdate(paramMessage.arg1);
          return;
        case 4:
        }
        MenuExecutor.this.mActivity.startActivity((Intent)paramMessage.obj);
      }
    };
  }

  private static ProgressDialog createProgressDialog(Context paramContext, int paramInt1, int paramInt2)
  {
    ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
    localProgressDialog.setTitle(paramInt1);
    localProgressDialog.setMax(paramInt2);
    localProgressDialog.setCancelable(false);
    localProgressDialog.setIndeterminate(false);
    if (paramInt2 > 1)
      localProgressDialog.setProgressStyle(1);
    return localProgressDialog;
  }

  private boolean execute(DataManager paramDataManager, ThreadPool.JobContext paramJobContext, int paramInt, Path paramPath)
  {
    boolean bool = true;
    Log.v("MenuExecutor", "Execute cmd: " + paramInt + " for " + paramPath);
    long l = System.currentTimeMillis();
    switch (paramInt)
    {
    default:
      throw new AssertionError();
    case 2131558666:
      paramDataManager.delete(paramPath);
    case 2131558669:
    case 2131558668:
    case 2131558402:
    case 2131558673:
    case 2131558664:
    }
    while (true)
    {
      label118: Log.v("MenuExecutor", "It takes " + (System.currentTimeMillis() - l) + " ms to execute cmd for " + paramPath);
      return bool;
      paramDataManager.rotate(paramPath, 90);
      continue;
      paramDataManager.rotate(paramPath, -90);
      continue;
      MediaObject localMediaObject = paramDataManager.getMediaObject(paramPath);
      if (localMediaObject.getCacheFlag() == 2);
      for (int i = 1; ; i = 2)
      {
        localMediaObject.cache(i);
        break label118:
      }
      MediaItem localMediaItem = (MediaItem)paramDataManager.getMediaObject(paramPath);
      double[] arrayOfDouble = new double[2];
      localMediaItem.getLatLong(arrayOfDouble);
      if (!GalleryUtils.isValidLocation(arrayOfDouble[0], arrayOfDouble[1]))
        continue;
      GalleryUtils.showOnMap(this.mActivity, arrayOfDouble[0], arrayOfDouble[1]);
      continue;
      bool = paramDataManager.getMediaObject(paramPath).Import();
    }
  }

  private Intent getIntentBySingleSelectedPath(String paramString)
  {
    DataManager localDataManager = this.mActivity.getDataManager();
    Path localPath = getSingleSelectedPath();
    String str = getMimeType(localDataManager.getMediaType(localPath));
    return new Intent(paramString).setDataAndType(localDataManager.getContentUri(localPath), str);
  }

  public static String getMimeType(int paramInt)
  {
    switch (paramInt)
    {
    case 3:
    default:
      return "*/*";
    case 2:
      return "image/*";
    case 4:
    }
    return "video/*";
  }

  private Path getSingleSelectedPath()
  {
    int i = 1;
    ArrayList localArrayList = this.mSelectionManager.getSelected(i);
    if (localArrayList.size() == i);
    while (true)
    {
      Utils.assertTrue(i);
      return (Path)localArrayList.get(0);
      int j = 0;
    }
  }

  private void onMenuClicked(int paramInt, ProgressListener paramProgressListener)
  {
    onMenuClicked(paramInt, paramProgressListener, false, true);
  }

  private void onProgressComplete(int paramInt, ProgressListener paramProgressListener)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(1, paramInt, 0, paramProgressListener));
  }

  private void onProgressStart(ProgressListener paramProgressListener)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3, paramProgressListener));
  }

  private void onProgressUpdate(int paramInt, ProgressListener paramProgressListener)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(2, paramInt, 0, paramProgressListener));
  }

  private static void setMenuItemVisible(Menu paramMenu, int paramInt, boolean paramBoolean)
  {
    MenuItem localMenuItem = paramMenu.findItem(paramInt);
    if (localMenuItem == null)
      return;
    localMenuItem.setVisible(paramBoolean);
  }

  private void stopTaskAndDismissDialog()
  {
    if (this.mTask == null)
      return;
    if (!this.mWaitOnStop)
      this.mTask.cancel();
    this.mTask.waitDone();
    this.mDialog.dismiss();
    this.mDialog = null;
    this.mTask = null;
  }

  public static void updateMenuForPanorama(Menu paramMenu, boolean paramBoolean1, boolean paramBoolean2)
  {
    setMenuItemVisible(paramMenu, 2131558665, paramBoolean1);
    if (!paramBoolean2)
      return;
    setMenuItemVisible(paramMenu, 2131558668, false);
    setMenuItemVisible(paramMenu, 2131558669, false);
  }

  public static void updateMenuOperation(Menu paramMenu, int paramInt)
  {
    boolean bool1 = true;
    boolean bool2;
    label10: boolean bool3;
    label19: boolean bool4;
    label29: boolean bool5;
    label40: boolean bool6;
    label49: boolean bool7;
    label59: boolean bool8;
    label69: label79: boolean bool9;
    label90: boolean bool10;
    if ((paramInt & 0x1) != 0)
    {
      bool2 = bool1;
      if ((paramInt & 0x2) == 0)
        break label220;
      bool3 = bool1;
      if ((paramInt & 0x8) == 0)
        break label226;
      bool4 = bool1;
      if ((paramInt & 0x1000) == 0)
        break label232;
      bool5 = bool1;
      if ((paramInt & 0x4) == 0)
        break label238;
      bool6 = bool1;
      if ((paramInt & 0x20) == 0)
        break label244;
      bool7 = bool1;
      if ((paramInt & 0x10) == 0)
        break label250;
      bool8 = bool1;
      if ((paramInt & 0x100) == 0)
        break label256;
      if ((paramInt & 0x200) == 0)
        break label259;
      bool9 = bool1;
      if ((paramInt & 0x400) == 0)
        break label265;
      bool10 = bool1;
      label101: if ((paramInt & 0x800) == 0)
        break label271;
    }
    while (true)
    {
      setMenuItemVisible(paramMenu, 2131558666, bool2);
      setMenuItemVisible(paramMenu, 2131558668, bool3);
      setMenuItemVisible(paramMenu, 2131558669, bool3);
      setMenuItemVisible(paramMenu, 2131558670, bool4);
      setMenuItemVisible(paramMenu, 2131558676, bool5);
      setMenuItemVisible(paramMenu, 2131558665, false);
      setMenuItemVisible(paramMenu, 2131558663, bool6);
      setMenuItemVisible(paramMenu, 2131558671, bool7);
      setMenuItemVisible(paramMenu, 2131558673, bool8);
      setMenuItemVisible(paramMenu, 2131558667, bool9);
      setMenuItemVisible(paramMenu, 2131558672, bool10);
      setMenuItemVisible(paramMenu, 2131558664, bool1);
      return;
      bool2 = false;
      break label10:
      label220: bool3 = false;
      break label19:
      label226: bool4 = false;
      break label29:
      label232: bool5 = false;
      break label40:
      label238: bool6 = false;
      break label49:
      label244: bool7 = false;
      break label59:
      label250: bool8 = false;
      break label69:
      label256: break label79:
      label259: bool9 = false;
      break label90:
      label265: bool10 = false;
      break label101:
      label271: bool1 = false;
    }
  }

  public void onMenuClicked(int paramInt, ProgressListener paramProgressListener, boolean paramBoolean1, boolean paramBoolean2)
  {
    switch (paramInt)
    {
    default:
      return;
    case 2131558403:
      if (this.mSelectionManager.inSelectAllMode())
      {
        this.mSelectionManager.deSelectAll();
        return;
      }
      this.mSelectionManager.selectAll();
      return;
    case 2131558670:
      Intent localIntent3 = getIntentBySingleSelectedPath("com.android.camera.action.CROP");
      this.mActivity.startActivity(localIntent3);
      return;
    case 2131558667:
      Intent localIntent2 = getIntentBySingleSelectedPath("android.intent.action.EDIT").setFlags(1);
      this.mActivity.startActivity(Intent.createChooser(localIntent2, null));
      return;
    case 2131558671:
      Intent localIntent1 = getIntentBySingleSelectedPath("android.intent.action.ATTACH_DATA").addFlags(1);
      localIntent1.putExtra("mimeType", localIntent1.getType());
      AbstractGalleryActivity localAbstractGalleryActivity = this.mActivity;
      localAbstractGalleryActivity.startActivity(Intent.createChooser(localIntent1, localAbstractGalleryActivity.getString(2131362234)));
      return;
    case 2131558666:
    case 2131558669:
    case 2131558668:
    case 2131558673:
    case 2131558664:
    }
    for (int i = 2131362210; ; i = 2131362301)
      while (true)
      {
        startAction(paramInt, i, paramProgressListener, paramBoolean1, paramBoolean2);
        return;
        i = 2131362227;
        continue;
        i = 2131362226;
        continue;
        i = 2131362225;
      }
  }

  public void onMenuClicked(MenuItem paramMenuItem, String paramString, ProgressListener paramProgressListener)
  {
    int i = paramMenuItem.getItemId();
    if (paramString != null)
    {
      if (paramProgressListener != null)
        paramProgressListener.onConfirmDialogShown();
      ConfirmDialogListener localConfirmDialogListener = new ConfirmDialogListener(i, paramProgressListener);
      new AlertDialog.Builder(this.mActivity.getAndroidContext()).setMessage(paramString).setOnCancelListener(localConfirmDialogListener).setPositiveButton(2131361809, localConfirmDialogListener).setNegativeButton(2131362212, localConfirmDialogListener).create().show();
      return;
    }
    onMenuClicked(i, paramProgressListener);
  }

  public void pause()
  {
    stopTaskAndDismissDialog();
  }

  public void startAction(int paramInt1, int paramInt2, ProgressListener paramProgressListener)
  {
    startAction(paramInt1, paramInt2, paramProgressListener, false, true);
  }

  public void startAction(int paramInt1, int paramInt2, ProgressListener paramProgressListener, boolean paramBoolean1, boolean paramBoolean2)
  {
    ArrayList localArrayList = this.mSelectionManager.getSelected(false);
    stopTaskAndDismissDialog();
    this.mDialog = createProgressDialog(this.mActivity, paramInt2, localArrayList.size());
    if (paramBoolean2)
      this.mDialog.show();
    MediaOperation localMediaOperation = new MediaOperation(paramInt1, localArrayList, paramProgressListener);
    this.mTask = this.mActivity.getThreadPool().submit(localMediaOperation, null);
    this.mWaitOnStop = paramBoolean1;
  }

  private class ConfirmDialogListener
    implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener
  {
    private final int mActionId;
    private final MenuExecutor.ProgressListener mListener;

    public ConfirmDialogListener(int paramProgressListener, MenuExecutor.ProgressListener arg3)
    {
      this.mActionId = paramProgressListener;
      Object localObject;
      this.mListener = localObject;
    }

    public void onCancel(DialogInterface paramDialogInterface)
    {
      if (this.mListener == null)
        return;
      this.mListener.onConfirmDialogDismissed(false);
    }

    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1)
      {
        if (this.mListener != null)
          this.mListener.onConfirmDialogDismissed(true);
        MenuExecutor.this.onMenuClicked(this.mActionId, this.mListener);
      }
      do
        return;
      while (this.mListener == null);
      this.mListener.onConfirmDialogDismissed(false);
    }
  }

  private class MediaOperation
    implements ThreadPool.Job<Void>
  {
    private final ArrayList<Path> mItems;
    private final MenuExecutor.ProgressListener mListener;
    private final int mOperation;

    public MediaOperation(ArrayList<Path> paramProgressListener, MenuExecutor.ProgressListener arg3)
    {
      this.mOperation = paramProgressListener;
      Object localObject1;
      this.mItems = localObject1;
      Object localObject2;
      this.mListener = localObject2;
    }

    // ERROR //
    public Void run(ThreadPool.JobContext paramJobContext)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_2
      //   2: aload_0
      //   3: getfield 20	com/android/gallery3d/ui/MenuExecutor$MediaOperation:this$0	Lcom/android/gallery3d/ui/MenuExecutor;
      //   6: invokestatic 42	com/android/gallery3d/ui/MenuExecutor:access$300	(Lcom/android/gallery3d/ui/MenuExecutor;)Lcom/android/gallery3d/app/AbstractGalleryActivity;
      //   9: invokevirtual 48	com/android/gallery3d/app/AbstractGalleryActivity:getDataManager	()Lcom/android/gallery3d/data/DataManager;
      //   12: astore_3
      //   13: iconst_1
      //   14: istore 4
      //   16: aload_0
      //   17: getfield 20	com/android/gallery3d/ui/MenuExecutor$MediaOperation:this$0	Lcom/android/gallery3d/ui/MenuExecutor;
      //   20: aload_0
      //   21: getfield 29	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mListener	Lcom/android/gallery3d/ui/MenuExecutor$ProgressListener;
      //   24: invokestatic 52	com/android/gallery3d/ui/MenuExecutor:access$500	(Lcom/android/gallery3d/ui/MenuExecutor;Lcom/android/gallery3d/ui/MenuExecutor$ProgressListener;)V
      //   27: aload_0
      //   28: getfield 27	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mItems	Ljava/util/ArrayList;
      //   31: invokevirtual 58	java/util/ArrayList:iterator	()Ljava/util/Iterator;
      //   34: astore 8
      //   36: aload 8
      //   38: invokeinterface 64 1 0
      //   43: ifeq +31 -> 74
      //   46: aload 8
      //   48: invokeinterface 68 1 0
      //   53: checkcast 70	com/android/gallery3d/data/Path
      //   56: astore 9
      //   58: aload_1
      //   59: invokeinterface 75 1 0
      //   64: istore 10
      //   66: iload 10
      //   68: ifeq +21 -> 89
      //   71: iconst_3
      //   72: istore 4
      //   74: aload_0
      //   75: getfield 20	com/android/gallery3d/ui/MenuExecutor$MediaOperation:this$0	Lcom/android/gallery3d/ui/MenuExecutor;
      //   78: iload 4
      //   80: aload_0
      //   81: getfield 29	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mListener	Lcom/android/gallery3d/ui/MenuExecutor$ProgressListener;
      //   84: invokestatic 79	com/android/gallery3d/ui/MenuExecutor:access$800	(Lcom/android/gallery3d/ui/MenuExecutor;ILcom/android/gallery3d/ui/MenuExecutor$ProgressListener;)V
      //   87: aconst_null
      //   88: areturn
      //   89: aload_0
      //   90: getfield 20	com/android/gallery3d/ui/MenuExecutor$MediaOperation:this$0	Lcom/android/gallery3d/ui/MenuExecutor;
      //   93: aload_3
      //   94: aload_1
      //   95: aload_0
      //   96: getfield 25	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mOperation	I
      //   99: aload 9
      //   101: invokestatic 83	com/android/gallery3d/ui/MenuExecutor:access$600	(Lcom/android/gallery3d/ui/MenuExecutor;Lcom/android/gallery3d/data/DataManager;Lcom/android/gallery3d/util/ThreadPool$JobContext;ILcom/android/gallery3d/data/Path;)Z
      //   104: ifne +6 -> 110
      //   107: iconst_2
      //   108: istore 4
      //   110: aload_0
      //   111: getfield 20	com/android/gallery3d/ui/MenuExecutor$MediaOperation:this$0	Lcom/android/gallery3d/ui/MenuExecutor;
      //   114: astore 11
      //   116: iinc 2 1
      //   119: aload 11
      //   121: iload_2
      //   122: aload_0
      //   123: getfield 29	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mListener	Lcom/android/gallery3d/ui/MenuExecutor$ProgressListener;
      //   126: invokestatic 86	com/android/gallery3d/ui/MenuExecutor:access$700	(Lcom/android/gallery3d/ui/MenuExecutor;ILcom/android/gallery3d/ui/MenuExecutor$ProgressListener;)V
      //   129: goto -93 -> 36
      //   132: astore 6
      //   134: ldc 88
      //   136: new 90	java/lang/StringBuilder
      //   139: dup
      //   140: invokespecial 91	java/lang/StringBuilder:<init>	()V
      //   143: ldc 93
      //   145: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   148: aload_0
      //   149: getfield 25	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mOperation	I
      //   152: invokevirtual 100	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   155: ldc 102
      //   157: invokevirtual 97	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   160: aload 6
      //   162: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   165: invokevirtual 109	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   168: invokestatic 115	com/android/gallery3d/ui/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   171: pop
      //   172: aload_0
      //   173: getfield 20	com/android/gallery3d/ui/MenuExecutor$MediaOperation:this$0	Lcom/android/gallery3d/ui/MenuExecutor;
      //   176: iload 4
      //   178: aload_0
      //   179: getfield 29	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mListener	Lcom/android/gallery3d/ui/MenuExecutor$ProgressListener;
      //   182: invokestatic 79	com/android/gallery3d/ui/MenuExecutor:access$800	(Lcom/android/gallery3d/ui/MenuExecutor;ILcom/android/gallery3d/ui/MenuExecutor$ProgressListener;)V
      //   185: goto -98 -> 87
      //   188: astore 5
      //   190: aload_0
      //   191: getfield 20	com/android/gallery3d/ui/MenuExecutor$MediaOperation:this$0	Lcom/android/gallery3d/ui/MenuExecutor;
      //   194: iload 4
      //   196: aload_0
      //   197: getfield 29	com/android/gallery3d/ui/MenuExecutor$MediaOperation:mListener	Lcom/android/gallery3d/ui/MenuExecutor$ProgressListener;
      //   200: invokestatic 79	com/android/gallery3d/ui/MenuExecutor:access$800	(Lcom/android/gallery3d/ui/MenuExecutor;ILcom/android/gallery3d/ui/MenuExecutor$ProgressListener;)V
      //   203: aload 5
      //   205: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   16	36	132	java/lang/Throwable
      //   36	66	132	java/lang/Throwable
      //   89	107	132	java/lang/Throwable
      //   110	116	132	java/lang/Throwable
      //   119	129	132	java/lang/Throwable
      //   16	36	188	finally
      //   36	66	188	finally
      //   89	107	188	finally
      //   110	116	188	finally
      //   119	129	188	finally
      //   134	172	188	finally
    }
  }

  public static abstract interface ProgressListener
  {
    public abstract void onConfirmDialogDismissed(boolean paramBoolean);

    public abstract void onConfirmDialogShown();

    public abstract void onProgressComplete(int paramInt);

    public abstract void onProgressStart();

    public abstract void onProgressUpdate(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.MenuExecutor
 * JD-Core Version:    0.5.4
 */