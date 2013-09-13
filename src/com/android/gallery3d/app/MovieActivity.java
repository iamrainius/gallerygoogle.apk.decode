package com.android.gallery3d.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ShareActionProvider;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.Utils;

public class MovieActivity extends Activity
{
  private boolean mFinishOnCompletion;
  private MoviePlayer mPlayer;
  private boolean mTreatUpAsBack;
  private Uri mUri;

  private Intent createShareIntent()
  {
    Intent localIntent = new Intent("android.intent.action.SEND");
    localIntent.setType("video/*");
    localIntent.putExtra("android.intent.extra.STREAM", this.mUri);
    return localIntent;
  }

  private void initializeActionBar(Intent paramIntent)
  {
    this.mUri = paramIntent.getData();
    ActionBar localActionBar = getActionBar();
    setActionBarLogoFromIntent(paramIntent);
    localActionBar.setDisplayOptions(4, 4);
    String str = paramIntent.getStringExtra("android.intent.extra.TITLE");
    if (str != null)
    {
      localActionBar.setTitle(str);
      return;
    }
    new AsyncQueryHandler(getContentResolver(), localActionBar)
    {
      protected void onQueryComplete(int paramInt, Object paramObject, Cursor paramCursor)
      {
        if (paramCursor != null);
        try
        {
          if (paramCursor.moveToFirst())
          {
            String str = paramCursor.getString(0);
            ActionBar localActionBar = this.val$actionBar;
            if (str == null)
              str = "";
            localActionBar.setTitle(str);
          }
          return;
        }
        finally
        {
          Utils.closeSilently(paramCursor);
        }
      }
    }
    .startQuery(0, null, this.mUri, new String[] { "_display_name" }, null, null, null);
  }

  private void setActionBarLogoFromIntent(Intent paramIntent)
  {
    Bitmap localBitmap = (Bitmap)paramIntent.getParcelableExtra("logo-bitmap");
    if (localBitmap == null)
      return;
    getActionBar().setLogo(new BitmapDrawable(getResources(), localBitmap));
  }

  @TargetApi(16)
  private void setSystemUiVisibility(View paramView)
  {
    if (!ApiHelper.HAS_VIEW_SYSTEM_UI_FLAG_LAYOUT_STABLE)
      return;
    paramView.setSystemUiVisibility(1792);
  }

  public void onCreate(Bundle paramBundle)
  {
    boolean bool = true;
    super.onCreate(paramBundle);
    requestWindowFeature(8);
    requestWindowFeature(9);
    setContentView(2130968618);
    View localView = findViewById(2131558529);
    setSystemUiVisibility(localView);
    Intent localIntent = getIntent();
    initializeActionBar(localIntent);
    this.mFinishOnCompletion = localIntent.getBooleanExtra("android.intent.extra.finishOnCompletion", bool);
    this.mTreatUpAsBack = localIntent.getBooleanExtra("treat-up-as-back", false);
    Uri localUri = localIntent.getData();
    if (!this.mFinishOnCompletion);
    while (true)
    {
      this.mPlayer = new MoviePlayer(localView, this, localUri, paramBundle, bool)
      {
        public void onCompletion()
        {
          if (!MovieActivity.this.mFinishOnCompletion)
            return;
          MovieActivity.this.finish();
        }
      };
      if (localIntent.hasExtra("android.intent.extra.screenOrientation"))
      {
        int i = localIntent.getIntExtra("android.intent.extra.screenOrientation", -1);
        if (i != getRequestedOrientation())
          setRequestedOrientation(i);
      }
      Window localWindow = getWindow();
      WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
      localLayoutParams.buttonBrightness = 0.0F;
      localLayoutParams.flags = (0x400 | localLayoutParams.flags);
      localWindow.setAttributes(localLayoutParams);
      localWindow.setBackgroundDrawable(null);
      return;
      bool = false;
    }
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    getMenuInflater().inflate(2131886089, paramMenu);
    MenuItem localMenuItem = paramMenu.findItem(2131558663);
    if ("content".equals(this.mUri.getScheme()))
    {
      localMenuItem.setVisible(true);
      ((ShareActionProvider)localMenuItem.getActionProvider()).setShareIntent(createShareIntent());
      return true;
    }
    localMenuItem.setVisible(false);
    return true;
  }

  public void onDestroy()
  {
    this.mPlayer.onDestroy();
    super.onDestroy();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return (this.mPlayer.onKeyDown(paramInt, paramKeyEvent)) || (super.onKeyDown(paramInt, paramKeyEvent));
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return (this.mPlayer.onKeyUp(paramInt, paramKeyEvent)) || (super.onKeyUp(paramInt, paramKeyEvent));
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    int i = paramMenuItem.getItemId();
    if (i == 16908332)
    {
      if (this.mTreatUpAsBack)
      {
        finish();
        return true;
      }
      startActivity(new Intent(this, Gallery.class));
      finish();
      return true;
    }
    if (i == 2131558663)
    {
      startActivity(Intent.createChooser(createShareIntent(), getString(2131362213)));
      return true;
    }
    return false;
  }

  public void onPause()
  {
    this.mPlayer.onPause();
    super.onPause();
  }

  public void onResume()
  {
    this.mPlayer.onResume();
    super.onResume();
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    this.mPlayer.onSaveInstanceState(paramBundle);
  }

  public void onStart()
  {
    ((AudioManager)getSystemService("audio")).requestAudioFocus(null, 3, 2);
    super.onStart();
  }

  protected void onStop()
  {
    ((AudioManager)getSystemService("audio")).abandonAudioFocus(null);
    super.onStop();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.MovieActivity
 * JD-Core Version:    0.5.4
 */