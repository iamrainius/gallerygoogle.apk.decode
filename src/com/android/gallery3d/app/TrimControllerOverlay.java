package com.android.gallery3d.app;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TrimControllerOverlay extends CommonControllerOverlay
{
  public TrimControllerOverlay(Context paramContext)
  {
    super(paramContext);
  }

  private void hidePlayButtonIfPlaying()
  {
    if (this.mState == CommonControllerOverlay.State.PLAYING)
      this.mPlayPauseReplayView.setVisibility(4);
    this.mPlayPauseReplayView.setAlpha(1.0F);
  }

  protected void createTimeBar(Context paramContext)
  {
    this.mTimeBar = new TrimTimeBar(paramContext, this);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (super.onTouchEvent(paramMotionEvent));
    do
    {
      return true;
      switch (paramMotionEvent.getAction())
      {
      default:
        return true;
      case 0:
      }
      if ((this.mState != CommonControllerOverlay.State.PLAYING) && (this.mState != CommonControllerOverlay.State.PAUSED))
        continue;
      this.mListener.onPlayPause();
      return true;
    }
    while ((this.mState != CommonControllerOverlay.State.ENDED) || (!this.mCanReplay));
    this.mListener.onReplay();
    return true;
  }

  public void setTimes(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTimeBar.setTime(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void showPlaying()
  {
    super.showPlaying();
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this.mPlayPauseReplayView, "alpha", new float[] { 1.0F, 0.0F });
    localObjectAnimator.setDuration(200L);
    localObjectAnimator.start();
    localObjectAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnimator)
      {
        TrimControllerOverlay.this.hidePlayButtonIfPlaying();
      }

      public void onAnimationEnd(Animator paramAnimator)
      {
        TrimControllerOverlay.this.hidePlayButtonIfPlaying();
      }

      public void onAnimationRepeat(Animator paramAnimator)
      {
      }

      public void onAnimationStart(Animator paramAnimator)
      {
      }
    });
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.TrimControllerOverlay
 * JD-Core Version:    0.5.4
 */