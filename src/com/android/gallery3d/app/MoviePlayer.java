package com.android.gallery3d.app;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.VideoView;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.util.GalleryUtils;

public class MoviePlayer
  implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, ControllerOverlay.Listener
{
  private final AudioBecomingNoisyReceiver mAudioBecomingNoisyReceiver;
  private final Bookmarker mBookmarker;
  private Context mContext;
  private final MovieControllerOverlay mController;
  private boolean mDragging;
  private final Handler mHandler = new Handler();
  private boolean mHasPaused = false;
  private int mLastSystemUiVis = 0;
  private final Runnable mPlayingChecker = new Runnable()
  {
    public void run()
    {
      if (MoviePlayer.this.mVideoView.isPlaying())
      {
        MoviePlayer.this.mController.showPlaying();
        return;
      }
      MoviePlayer.this.mHandler.postDelayed(MoviePlayer.this.mPlayingChecker, 250L);
    }
  };
  private final Runnable mProgressChecker = new Runnable()
  {
    public void run()
    {
      int i = MoviePlayer.this.setProgress();
      MoviePlayer.this.mHandler.postDelayed(MoviePlayer.this.mProgressChecker, 1000 - i % 1000);
    }
  };
  private long mResumeableTime = 9223372036854775807L;
  private final View mRootView;
  private boolean mShowing;
  private final Uri mUri;
  private int mVideoPosition = 0;
  private final VideoView mVideoView;

  public MoviePlayer(View paramView, MovieActivity paramMovieActivity, Uri paramUri, Bundle paramBundle, boolean paramBoolean)
  {
    this.mContext = paramMovieActivity.getApplicationContext();
    this.mRootView = paramView;
    this.mVideoView = ((VideoView)paramView.findViewById(2131558530));
    this.mBookmarker = new Bookmarker(paramMovieActivity);
    this.mUri = paramUri;
    this.mController = new MovieControllerOverlay(this.mContext);
    ((ViewGroup)paramView).addView(this.mController.getView());
    this.mController.setListener(this);
    this.mController.setCanReplay(paramBoolean);
    this.mVideoView.setOnErrorListener(this);
    this.mVideoView.setOnCompletionListener(this);
    this.mVideoView.setVideoURI(this.mUri);
    this.mVideoView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        MoviePlayer.this.mController.show();
        return true;
      }
    });
    this.mVideoView.postDelayed(new Runnable()
    {
      public void run()
      {
        MoviePlayer.this.mVideoView.setVisibility(0);
      }
    }
    , 500L);
    setOnSystemUiVisibilityChangeListener();
    showSystemUi(false);
    this.mAudioBecomingNoisyReceiver = new AudioBecomingNoisyReceiver(null);
    this.mAudioBecomingNoisyReceiver.register();
    Intent localIntent = new Intent("com.android.music.musicservicecommand");
    localIntent.putExtra("command", "pause");
    paramMovieActivity.sendBroadcast(localIntent);
    if (paramBundle != null)
    {
      this.mVideoPosition = paramBundle.getInt("video-position", 0);
      this.mResumeableTime = paramBundle.getLong("resumeable-timeout", 9223372036854775807L);
      this.mVideoView.start();
      this.mVideoView.suspend();
      this.mHasPaused = true;
      return;
    }
    Integer localInteger = this.mBookmarker.getBookmark(this.mUri);
    if (localInteger != null)
    {
      showResumeDialog(paramMovieActivity, localInteger.intValue());
      return;
    }
    startVideo();
  }

  private static boolean isMediaKey(int paramInt)
  {
    return (paramInt == 79) || (paramInt == 88) || (paramInt == 87) || (paramInt == 85) || (paramInt == 126) || (paramInt == 127);
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

  @TargetApi(16)
  private void setOnSystemUiVisibilityChangeListener()
  {
    if (!ApiHelper.HAS_VIEW_SYSTEM_UI_FLAG_HIDE_NAVIGATION)
      return;
    this.mVideoView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
    {
      public void onSystemUiVisibilityChange(int paramInt)
      {
        int i = paramInt ^ MoviePlayer.this.mLastSystemUiVis;
        MoviePlayer.access$702(MoviePlayer.this, paramInt);
        if (((i & 0x2) == 0) || ((paramInt & 0x2) != 0))
          return;
        MoviePlayer.this.mController.show();
        MoviePlayer.this.mRootView.setBackgroundColor(-16777216);
      }
    });
  }

  private int setProgress()
  {
    if ((this.mDragging) || (!this.mShowing))
      return 0;
    int i = this.mVideoView.getCurrentPosition();
    int j = this.mVideoView.getDuration();
    this.mController.setTimes(i, j, 0, 0);
    return i;
  }

  private void showResumeDialog(Context paramContext, int paramInt)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle(2131362186);
    String str = paramContext.getString(2131362187);
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = GalleryUtils.formatDuration(paramContext, paramInt / 1000);
    localBuilder.setMessage(String.format(str, arrayOfObject));
    localBuilder.setOnCancelListener(new DialogInterface.OnCancelListener()
    {
      public void onCancel(DialogInterface paramDialogInterface)
      {
        MoviePlayer.this.onCompletion();
      }
    });
    localBuilder.setPositiveButton(2131362188, new DialogInterface.OnClickListener(paramInt)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        MoviePlayer.this.mVideoView.seekTo(this.val$bookmark);
        MoviePlayer.this.startVideo();
      }
    });
    localBuilder.setNegativeButton(2131362193, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        MoviePlayer.this.startVideo();
      }
    });
    localBuilder.show();
  }

  @TargetApi(16)
  private void showSystemUi(boolean paramBoolean)
  {
    if (!ApiHelper.HAS_VIEW_SYSTEM_UI_FLAG_LAYOUT_STABLE)
      return;
    int i = 1792;
    if (!paramBoolean)
      i |= 7;
    this.mVideoView.setSystemUiVisibility(i);
  }

  private void startVideo()
  {
    String str = this.mUri.getScheme();
    if (("http".equalsIgnoreCase(str)) || ("rtsp".equalsIgnoreCase(str)))
    {
      this.mController.showLoading();
      this.mHandler.removeCallbacks(this.mPlayingChecker);
      this.mHandler.postDelayed(this.mPlayingChecker, 250L);
    }
    while (true)
    {
      this.mVideoView.start();
      setProgress();
      return;
      this.mController.showPlaying();
      this.mController.hide();
    }
  }

  public void onCompletion()
  {
  }

  public void onCompletion(MediaPlayer paramMediaPlayer)
  {
    this.mController.showEnded();
    onCompletion();
  }

  public void onDestroy()
  {
    this.mVideoView.stopPlayback();
    this.mAudioBecomingNoisyReceiver.unregister();
  }

  public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    this.mHandler.removeCallbacksAndMessages(null);
    this.mController.showErrorMessage("");
    return false;
  }

  public void onHidden()
  {
    this.mShowing = false;
    showSystemUi(false);
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool = true;
    if (paramKeyEvent.getRepeatCount() > 0)
      bool = isMediaKey(paramInt);
    do
    {
      do
      {
        return bool;
        switch (paramInt)
        {
        case 87:
        case 88:
        default:
          return false;
        case 79:
        case 85:
          if (this.mVideoView.isPlaying())
          {
            pauseVideo();
            return bool;
          }
          playVideo();
          return bool;
        case 127:
        case 126:
        }
      }
      while (!this.mVideoView.isPlaying());
      pauseVideo();
      return bool;
    }
    while (this.mVideoView.isPlaying());
    playVideo();
    return bool;
  }

  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    return isMediaKey(paramInt);
  }

  public void onPause()
  {
    this.mHasPaused = true;
    this.mHandler.removeCallbacksAndMessages(null);
    this.mVideoPosition = this.mVideoView.getCurrentPosition();
    this.mBookmarker.setBookmark(this.mUri, this.mVideoPosition, this.mVideoView.getDuration());
    this.mVideoView.suspend();
    this.mResumeableTime = (180000L + System.currentTimeMillis());
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
    startVideo();
  }

  public void onResume()
  {
    if (this.mHasPaused)
    {
      this.mVideoView.seekTo(this.mVideoPosition);
      this.mVideoView.resume();
      if (System.currentTimeMillis() > this.mResumeableTime)
        pauseVideo();
    }
    this.mHandler.post(this.mProgressChecker);
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putInt("video-position", this.mVideoPosition);
    paramBundle.putLong("resumeable-timeout", this.mResumeableTime);
  }

  public void onSeekEnd(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mDragging = false;
    this.mVideoView.seekTo(paramInt1);
    setProgress();
  }

  public void onSeekMove(int paramInt)
  {
    this.mVideoView.seekTo(paramInt);
  }

  public void onSeekStart()
  {
    this.mDragging = true;
  }

  public void onShown()
  {
    this.mShowing = true;
    setProgress();
    showSystemUi(true);
  }

  private class AudioBecomingNoisyReceiver extends BroadcastReceiver
  {
    private AudioBecomingNoisyReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (!MoviePlayer.this.mVideoView.isPlaying())
        return;
      MoviePlayer.this.pauseVideo();
    }

    public void register()
    {
      MoviePlayer.this.mContext.registerReceiver(this, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
    }

    public void unregister()
    {
      MoviePlayer.this.mContext.unregisterReceiver(this);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.MoviePlayer
 * JD-Core Version:    0.5.4
 */