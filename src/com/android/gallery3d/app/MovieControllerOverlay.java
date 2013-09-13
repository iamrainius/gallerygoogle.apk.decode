package com.android.gallery3d.app;

import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MovieControllerOverlay extends CommonControllerOverlay
  implements Animation.AnimationListener
{
  private final Handler handler = new Handler();
  private boolean hidden;
  private final Animation hideAnimation;
  private final Runnable startHidingRunnable = new Runnable()
  {
    public void run()
    {
      MovieControllerOverlay.this.startHiding();
    }
  };

  public MovieControllerOverlay(Context paramContext)
  {
    super(paramContext);
    this.hideAnimation = AnimationUtils.loadAnimation(paramContext, 2131034116);
    this.hideAnimation.setAnimationListener(this);
    hide();
  }

  private void cancelHiding()
  {
    this.handler.removeCallbacks(this.startHidingRunnable);
    this.mBackground.setAnimation(null);
    this.mTimeBar.setAnimation(null);
    this.mPlayPauseReplayView.setAnimation(null);
  }

  private void maybeStartHiding()
  {
    cancelHiding();
    if (this.mState != CommonControllerOverlay.State.PLAYING)
      return;
    this.handler.postDelayed(this.startHidingRunnable, 2500L);
  }

  private void startHideAnimation(View paramView)
  {
    if (paramView.getVisibility() != 0)
      return;
    paramView.startAnimation(this.hideAnimation);
  }

  private void startHiding()
  {
    startHideAnimation(this.mBackground);
    startHideAnimation(this.mTimeBar);
    startHideAnimation(this.mPlayPauseReplayView);
  }

  protected void createTimeBar(Context paramContext)
  {
    this.mTimeBar = new TimeBar(paramContext, this);
  }

  public void hide()
  {
    boolean bool = this.hidden;
    this.hidden = true;
    super.hide();
    if ((this.mListener == null) || (bool == this.hidden))
      return;
    this.mListener.onHidden();
  }

  public void onAnimationEnd(Animation paramAnimation)
  {
    hide();
  }

  public void onAnimationRepeat(Animation paramAnimation)
  {
  }

  public void onAnimationStart(Animation paramAnimation)
  {
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.hidden)
      show();
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public void onScrubbingEnd(int paramInt1, int paramInt2, int paramInt3)
  {
    maybeStartHiding();
    super.onScrubbingEnd(paramInt1, paramInt2, paramInt3);
  }

  public void onScrubbingMove(int paramInt)
  {
    cancelHiding();
    super.onScrubbingMove(paramInt);
  }

  public void onScrubbingStart()
  {
    cancelHiding();
    super.onScrubbingStart();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (super.onTouchEvent(paramMotionEvent));
    do
    {
      return true;
      if (this.hidden)
      {
        show();
        return true;
      }
      switch (paramMotionEvent.getAction())
      {
      default:
        return true;
      case 0:
        cancelHiding();
      case 1:
      }
    }
    while ((this.mState != CommonControllerOverlay.State.PLAYING) && (this.mState != CommonControllerOverlay.State.PAUSED));
    this.mListener.onPlayPause();
    return true;
    maybeStartHiding();
    return true;
  }

  public void show()
  {
    boolean bool = this.hidden;
    this.hidden = false;
    super.show();
    if ((this.mListener != null) && (bool != this.hidden))
      this.mListener.onShown();
    maybeStartHiding();
  }

  protected void updateViews()
  {
    if (this.hidden)
      return;
    super.updateViews();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.MovieControllerOverlay
 * JD-Core Version:    0.5.4
 */