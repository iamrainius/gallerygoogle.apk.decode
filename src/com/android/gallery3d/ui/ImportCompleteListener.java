package com.android.gallery3d.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.AlbumPage;
import com.android.gallery3d.app.StateManager;
import com.android.gallery3d.util.MediaSetUtils;

public class ImportCompleteListener
  implements MenuExecutor.ProgressListener
{
  private AbstractGalleryActivity mActivity;
  private PowerManager.WakeLock mWakeLock;

  public ImportCompleteListener(AbstractGalleryActivity paramAbstractGalleryActivity)
  {
    this.mActivity = paramAbstractGalleryActivity;
    this.mWakeLock = ((PowerManager)this.mActivity.getSystemService("power")).newWakeLock(6, "Gallery Album Import");
  }

  private void goToImportedAlbum()
  {
    String str = "/local/all/" + MediaSetUtils.IMPORTED_BUCKET_ID;
    Bundle localBundle = new Bundle();
    localBundle.putString("media-path", str);
    this.mActivity.getStateManager().startState(AlbumPage.class, localBundle);
  }

  public void onConfirmDialogDismissed(boolean paramBoolean)
  {
  }

  public void onConfirmDialogShown()
  {
  }

  public void onProgressComplete(int paramInt)
  {
    int i;
    if (paramInt == 1)
    {
      i = 2131362302;
      goToImportedAlbum();
    }
    while (true)
    {
      Toast.makeText(this.mActivity.getAndroidContext(), i, 1).show();
      this.mWakeLock.release();
      return;
      i = 2131362303;
    }
  }

  public void onProgressStart()
  {
    this.mWakeLock.acquire();
  }

  public void onProgressUpdate(int paramInt)
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.ImportCompleteListener
 * JD-Core Version:    0.5.4
 */