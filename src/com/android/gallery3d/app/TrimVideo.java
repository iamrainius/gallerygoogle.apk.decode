package com.android.gallery3d.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class TrimVideo extends Activity
  implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, ControllerOverlay.Listener
{
  private Context mContext;
  private TrimControllerOverlay mController;
  private File mDstFile = null;
  private final Handler mHandler = new Handler();
  private boolean mHasPaused = false;
  public ProgressDialog mProgress;
  private final Runnable mProgressChecker = new Runnable()
  {
    public void run()
    {
      int i = TrimVideo.this.setProgress();
      TrimVideo.this.mHandler.postDelayed(TrimVideo.this.mProgressChecker, 200 - i % 200);
    }
  };
  private File mSaveDirectory = null;
  private String mSaveFileName = null;
  private File mSrcFile = null;
  private String mSrcVideoPath = null;
  private int mTrimEndTime = 0;
  private int mTrimStartTime = 0;
  private Uri mUri;
  private int mVideoPosition = 0;
  private VideoView mVideoView;
  private String saveFolderName = null;

  private File getSaveDirectory()
  {
    File[] arrayOfFile = new File[1];
    querySource(new String[] { "_data" }, new ContentResolverQueryCallback(arrayOfFile)
    {
      public void onCursorResult(Cursor paramCursor)
      {
        this.val$dir[0] = new File(paramCursor.getString(0)).getParentFile();
      }
    });
    return arrayOfFile[0];
  }

  private Uri insertContent(File paramFile)
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1 / 1000L;
    ContentValues localContentValues = new ContentValues(12);
    localContentValues.put("title", this.mSaveFileName);
    localContentValues.put("_display_name", paramFile.getName());
    localContentValues.put("mime_type", "video/mp4");
    localContentValues.put("datetaken", Long.valueOf(l1));
    localContentValues.put("date_modified", Long.valueOf(l2));
    localContentValues.put("date_added", Long.valueOf(l2));
    localContentValues.put("_data", paramFile.getAbsolutePath());
    localContentValues.put("_size", Long.valueOf(paramFile.length()));
    querySource(new String[] { "datetaken", "latitude", "longitude", "resolution" }, new ContentResolverQueryCallback(localContentValues)
    {
      public void onCursorResult(Cursor paramCursor)
      {
        long l = paramCursor.getLong(0);
        if (l > 0L)
          this.val$values.put("datetaken", Long.valueOf(l));
        double d1 = paramCursor.getDouble(1);
        double d2 = paramCursor.getDouble(2);
        if ((d1 != 0.0D) || (d2 != 0.0D))
        {
          this.val$values.put("latitude", Double.valueOf(d1));
          this.val$values.put("longitude", Double.valueOf(d2));
        }
        this.val$values.put("resolution", paramCursor.getString(3));
      }
    });
    return getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
  }

  private void pauseVideo()
  {
    this.mVideoView.pause();
    this.mController.showPaused();
  }

  private void playVideo()
  {
    this.mVideoView.start();
    this.mController.showPlaying();
    setProgress();
  }

  private void querySource(String[] paramArrayOfString, ContentResolverQueryCallback paramContentResolverQueryCallback)
  {
    ContentResolver localContentResolver = getContentResolver();
    Cursor localCursor = null;
    try
    {
      localCursor = localContentResolver.query(this.mUri, paramArrayOfString, null, null, null);
      if ((localCursor != null) && (localCursor.moveToNext()))
        paramContentResolverQueryCallback.onCursorResult(localCursor);
      return;
    }
    catch (Exception localException)
    {
      return;
    }
    finally
    {
      if (localCursor != null)
        localCursor.close();
    }
  }

  private int setProgress()
  {
    this.mVideoPosition = this.mVideoView.getCurrentPosition();
    if (this.mVideoPosition < this.mTrimStartTime)
    {
      this.mVideoView.seekTo(this.mTrimStartTime);
      this.mVideoPosition = this.mTrimStartTime;
    }
    if ((this.mVideoPosition >= this.mTrimEndTime) && (this.mTrimEndTime > 0))
    {
      if (this.mVideoPosition > this.mTrimEndTime)
      {
        this.mVideoView.seekTo(this.mTrimEndTime);
        this.mVideoPosition = this.mTrimEndTime;
      }
      this.mController.showEnded();
      this.mVideoView.pause();
    }
    int i = this.mVideoView.getDuration();
    if ((i > 0) && (this.mTrimEndTime == 0))
      this.mTrimEndTime = i;
    this.mController.setTimes(this.mVideoPosition, i, this.mTrimStartTime, this.mTrimEndTime);
    return this.mVideoPosition;
  }

  private void showProgressDialog()
  {
    this.mProgress = new ProgressDialog(this);
    this.mProgress.setTitle(getString(2131362329));
    this.mProgress.setMessage(getString(2131362330));
    this.mProgress.setCancelable(false);
    this.mProgress.setCanceledOnTouchOutside(false);
    this.mProgress.show();
  }

  private void trimVideo()
  {
    int i = this.mTrimEndTime - this.mTrimStartTime;
    if (i < 100)
    {
      Toast.makeText(getApplicationContext(), getString(2131362332), 0).show();
      return;
    }
    if (Math.abs(this.mVideoView.getDuration() - i) < 100)
    {
      onBackPressed();
      return;
    }
    this.mSaveDirectory = getSaveDirectory();
    if ((this.mSaveDirectory == null) || (!this.mSaveDirectory.canWrite()))
      this.mSaveDirectory = new File(Environment.getExternalStorageDirectory(), "download");
    for (this.saveFolderName = getString(2131362319); ; this.saveFolderName = this.mSaveDirectory.getName())
    {
      this.mSaveFileName = new SimpleDateFormat("'TRIM'_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
      this.mDstFile = new File(this.mSaveDirectory, this.mSaveFileName + ".mp4");
      this.mSrcFile = new File(this.mSrcVideoPath);
      showProgressDialog();
      new Thread(new Runnable()
      {
        public void run()
        {
          try
          {
            TrimVideoUtils.startTrim(TrimVideo.this.mSrcFile, TrimVideo.this.mDstFile, TrimVideo.this.mTrimStartTime, TrimVideo.this.mTrimEndTime);
            TrimVideo.this.insertContent(TrimVideo.this.mDstFile);
            TrimVideo.this.mHandler.post(new Runnable()
            {
              public void run()
              {
                Toast.makeText(TrimVideo.this.getApplicationContext(), TrimVideo.this.getString(2131362331) + " " + TrimVideo.this.saveFolderName, 0).show();
                if (TrimVideo.this.mProgress == null)
                  return;
                TrimVideo.this.mProgress.dismiss();
                TrimVideo.this.mProgress = null;
                Intent localIntent = new Intent("android.intent.action.VIEW");
                localIntent.setDataAndTypeAndNormalize(Uri.fromFile(TrimVideo.this.mDstFile), "video/*");
                localIntent.putExtra("android.intent.extra.finishOnCompletion", false);
                TrimVideo.this.startActivity(localIntent);
                TrimVideo.this.finish();
              }
            });
            return;
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
          }
        }
      }).start();
      return;
    }
  }

  public void onCompletion(MediaPlayer paramMediaPlayer)
  {
    this.mController.showEnded();
  }

  public void onCreate(Bundle paramBundle)
  {
    this.mContext = getApplicationContext();
    super.onCreate(paramBundle);
    requestWindowFeature(8);
    requestWindowFeature(9);
    ActionBar localActionBar = getActionBar();
    localActionBar.setDisplayOptions(0, 2);
    localActionBar.setDisplayOptions(16, 16);
    localActionBar.setCustomView(2130968660);
    ((TextView)findViewById(2131558619)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        TrimVideo.this.trimVideo();
      }
    });
    Intent localIntent = getIntent();
    this.mUri = localIntent.getData();
    this.mSrcVideoPath = localIntent.getStringExtra("media-item-path");
    setContentView(2130968661);
    View localView = findViewById(2131558620);
    this.mVideoView = ((VideoView)localView.findViewById(2131558530));
    this.mController = new TrimControllerOverlay(this.mContext);
    ((ViewGroup)localView).addView(this.mController.getView());
    this.mController.setListener(this);
    this.mController.setCanReplay(true);
    this.mVideoView.setOnErrorListener(this);
    this.mVideoView.setOnCompletionListener(this);
    this.mVideoView.setVideoURI(this.mUri);
    playVideo();
  }

  public void onDestroy()
  {
    this.mVideoView.stopPlayback();
    super.onDestroy();
  }

  public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    return false;
  }

  public void onHidden()
  {
  }

  public void onPause()
  {
    this.mHasPaused = true;
    this.mHandler.removeCallbacksAndMessages(null);
    this.mVideoPosition = this.mVideoView.getCurrentPosition();
    this.mVideoView.suspend();
    super.onPause();
  }

  public void onPlayPause()
  {
    if (this.mVideoView.isPlaying())
    {
      pauseVideo();
      return;
    }
    playVideo();
  }

  public void onReplay()
  {
    this.mVideoView.seekTo(this.mTrimStartTime);
    playVideo();
  }

  public void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    this.mTrimStartTime = paramBundle.getInt("trim_start", 0);
    this.mTrimEndTime = paramBundle.getInt("trim_end", 0);
    this.mVideoPosition = paramBundle.getInt("video_pos", 0);
  }

  public void onResume()
  {
    super.onResume();
    if (this.mHasPaused)
    {
      this.mVideoView.seekTo(this.mVideoPosition);
      this.mVideoView.resume();
      this.mHasPaused = false;
    }
    this.mHandler.post(this.mProgressChecker);
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("trim_start", this.mTrimStartTime);
    paramBundle.putInt("trim_end", this.mTrimEndTime);
    paramBundle.putInt("video_pos", this.mVideoPosition);
    super.onSaveInstanceState(paramBundle);
  }

  public void onSeekEnd(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mVideoView.seekTo(paramInt1);
    this.mTrimStartTime = paramInt2;
    this.mTrimEndTime = paramInt3;
    setProgress();
  }

  public void onSeekMove(int paramInt)
  {
    this.mVideoView.seekTo(paramInt);
  }

  public void onSeekStart()
  {
    pauseVideo();
  }

  public void onShown()
  {
  }

  public void onStop()
  {
    if (this.mProgress != null)
    {
      this.mProgress.dismiss();
      this.mProgress = null;
    }
    super.onStop();
  }

  private static abstract interface ContentResolverQueryCallback
  {
    public abstract void onCursorResult(Cursor paramCursor);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.TrimVideo
 * JD-Core Version:    0.5.4
 */