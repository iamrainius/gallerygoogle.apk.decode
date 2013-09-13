package com.android.gallery3d.app;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.widget.Toast;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.picasasource.PicasaSource;
import com.android.gallery3d.util.GalleryUtils;

public final class Gallery extends AbstractGalleryActivity
  implements DialogInterface.OnCancelListener
{
  private Dialog mVersionCheckDialog;

  private String getContentType(Intent paramIntent)
  {
    String str1 = paramIntent.getType();
    if (str1 != null)
      return str1;
    Uri localUri = paramIntent.getData();
    try
    {
      String str2 = getContentResolver().getType(localUri);
      return str2;
    }
    catch (Throwable localThrowable)
    {
      Log.w("Gallery", "get type fail", localThrowable);
    }
    return null;
  }

  private void initializeByIntent()
  {
    Intent localIntent = getIntent();
    String str1 = localIntent.getAction();
    if ("android.intent.action.GET_CONTENT".equalsIgnoreCase(str1))
    {
      startGetContent(localIntent);
      return;
    }
    if ("android.intent.action.PICK".equalsIgnoreCase(str1))
    {
      Log.w("Gallery", "action PICK is not supported");
      String str2 = Utils.ensureNotNull(localIntent.getType());
      if (str2.startsWith("vnd.android.cursor.dir/"))
      {
        if (str2.endsWith("/image"))
          localIntent.setType("image/*");
        if (str2.endsWith("/video"))
          localIntent.setType("video/*");
      }
      startGetContent(localIntent);
      return;
    }
    if (("android.intent.action.VIEW".equalsIgnoreCase(str1)) || ("com.android.camera.action.REVIEW".equalsIgnoreCase(str1)))
    {
      startViewAction(localIntent);
      return;
    }
    startDefaultPage();
  }

  private void startGetContent(Intent paramIntent)
  {
    if (paramIntent.getExtras() != null);
    for (Bundle localBundle = new Bundle(paramIntent.getExtras()); ; localBundle = new Bundle())
    {
      localBundle.putBoolean("get-content", true);
      int i = GalleryUtils.determineTypeBits(this, paramIntent);
      localBundle.putInt("type-bits", i);
      localBundle.putString("media-path", getDataManager().getTopSetPath(i));
      getStateManager().startState(AlbumSetPage.class, localBundle);
      return;
    }
  }

  private void startViewAction(Intent paramIntent)
  {
    if (Boolean.valueOf(paramIntent.getBooleanExtra("slideshow", false)).booleanValue())
    {
      getActionBar().hide();
      DataManager localDataManager2 = getDataManager();
      Path localPath4 = localDataManager2.findPathByUri(paramIntent.getData(), paramIntent.getType());
      if ((localPath4 == null) || (localDataManager2.getMediaObject(localPath4) instanceof MediaItem))
        localPath4 = Path.fromString(localDataManager2.getTopSetPath(1));
      Bundle localBundle2 = new Bundle();
      localBundle2.putString("media-set-path", localPath4.toString());
      localBundle2.putBoolean("random-order", true);
      localBundle2.putBoolean("repeat", true);
      if (paramIntent.getBooleanExtra("dream", false))
        localBundle2.putBoolean("dream", true);
      getStateManager().startState(SlideshowPage.class, localBundle2);
      return;
    }
    Bundle localBundle1 = new Bundle();
    DataManager localDataManager1 = getDataManager();
    Uri localUri = paramIntent.getData();
    String str = getContentType(paramIntent);
    if (str == null)
    {
      Toast.makeText(this, 2131362228, 1).show();
      finish();
      return;
    }
    if (localUri == null)
    {
      int k = GalleryUtils.determineTypeBits(this, paramIntent);
      localBundle1.putInt("type-bits", k);
      localBundle1.putString("media-path", getDataManager().getTopSetPath(k));
      getStateManager().startState(AlbumSetPage.class, localBundle1);
      return;
    }
    Path localPath3;
    if (str.startsWith("vnd.android.cursor.dir"))
    {
      int j = paramIntent.getIntExtra("mediaTypes", 0);
      if (j != 0)
        localUri = localUri.buildUpon().appendQueryParameter("mediaTypes", String.valueOf(j)).build();
      localPath3 = localDataManager1.findPathByUri(localUri, null);
      if (localPath3 == null)
        break label491;
    }
    for (MediaSet localMediaSet = (MediaSet)localDataManager1.getMediaObject(localPath3); ; localMediaSet = null)
    {
      if (localMediaSet != null)
      {
        if (localMediaSet.isLeafAlbum())
        {
          localBundle1.putString("media-path", localPath3.toString());
          localBundle1.putString("parent-media-path", localDataManager1.getTopSetPath(3));
          getStateManager().startState(AlbumPage.class, localBundle1);
          return;
        }
        localBundle1.putString("media-path", localPath3.toString());
        getStateManager().startState(AlbumSetPage.class, localBundle1);
        return;
      }
      startDefaultPage();
      return;
      Path localPath1 = localDataManager1.findPathByUri(localUri, paramIntent.getType());
      Path localPath2 = localDataManager1.getDefaultSetOf(localPath1);
      localBundle1.putString("media-item-path", localPath1.toString());
      if ((localPath2 == null) || (paramIntent.getBooleanExtra("SingleItemOnly", false)));
      for (int i = 1; ; i = 0)
      {
        if (i == 0)
        {
          localBundle1.putString("media-set-path", localPath2.toString());
          if ((paramIntent.getBooleanExtra("treat-back-as-up", false)) || ((0x10000000 & paramIntent.getFlags()) != 0))
            localBundle1.putBoolean("treat-back-as-up", true);
        }
        getStateManager().startState(PhotoPage.class, localBundle1);
        label491: return;
      }
    }
  }

  public void onCancel(DialogInterface paramDialogInterface)
  {
    if (paramDialogInterface != this.mVersionCheckDialog)
      return;
    this.mVersionCheckDialog = null;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(8);
    requestWindowFeature(9);
    setContentView(2130968611);
    if (paramBundle != null)
    {
      getStateManager().restoreFromState(paramBundle);
      return;
    }
    initializeByIntent();
  }

  protected void onPause()
  {
    super.onPause();
    if (this.mVersionCheckDialog == null)
      return;
    this.mVersionCheckDialog.dismiss();
  }

  protected void onResume()
  {
    if (getStateManager().getStateCount() > 0);
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      super.onResume();
      if (this.mVersionCheckDialog != null)
        this.mVersionCheckDialog.show();
      return;
    }
  }

  public void startDefaultPage()
  {
    PicasaSource.showSignInReminder(this);
    Bundle localBundle = new Bundle();
    localBundle.putString("media-path", getDataManager().getTopSetPath(3));
    getStateManager().startState(AlbumSetPage.class, localBundle);
    this.mVersionCheckDialog = PicasaSource.getVersionCheckDialog(this);
    if (this.mVersionCheckDialog == null)
      return;
    this.mVersionCheckDialog.setOnCancelListener(this);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.Gallery
 * JD-Core Version:    0.5.4
 */