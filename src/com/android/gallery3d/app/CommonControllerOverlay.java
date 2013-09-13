package com.android.gallery3d.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public abstract class CommonControllerOverlay extends FrameLayout
  implements View.OnClickListener, ControllerOverlay, TimeBar.Listener
{
  protected final View mBackground;
  protected boolean mCanReplay = true;
  protected final TextView mErrorView;
  protected ControllerOverlay.Listener mListener;
  protected final LinearLayout mLoadingView;
  protected View mMainView;
  protected final ImageView mPlayPauseReplayView;
  protected State mState = State.LOADING;
  protected TimeBar mTimeBar;
  private final Rect mWindowInsets = new Rect();

  public CommonControllerOverlay(Context paramContext)
  {
    super(paramContext);
    FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(-2, -2);
    FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(-1, -1);
    this.mBackground = new View(paramContext);
    this.mBackground.setBackgroundColor(paramContext.getResources().getColor(2131296330));
    addView(this.mBackground, localLayoutParams2);
    createTimeBar(paramContext);
    addView(this.mTimeBar, localLayoutParams1);
    this.mLoadingView = new LinearLayout(paramContext);
    this.mLoadingView.setOrientation(1);
    this.mLoadingView.setGravity(1);
    ProgressBar localProgressBar = new ProgressBar(paramContext);
    localProgressBar.setIndeterminate(true);
    this.mLoadingView.addView(localProgressBar, localLayoutParams1);
    TextView localTextView = createOverlayTextView(paramContext);
    localTextView.setText(2131362183);
    this.mLoadingView.addView(localTextView, localLayoutParams1);
    addView(this.mLoadingView, localLayoutParams1);
    this.mPlayPauseReplayView = new ImageView(paramContext);
    this.mPlayPauseReplayView.setImageResource(2130837769);
    this.mPlayPauseReplayView.setBackgroundResource(2130837516);
    this.mPlayPauseReplayView.setScaleType(ImageView.ScaleType.CENTER);
    this.mPlayPauseReplayView.setFocusable(true);
    this.mPlayPauseReplayView.setClickable(true);
    this.mPlayPauseReplayView.setOnClickListener(this);
    addView(this.mPlayPauseReplayView, localLayoutParams1);
    this.mErrorView = createOverlayTextView(paramContext);
    addView(this.mErrorView, localLayoutParams2);
    setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
    hide();
  }

  private TextView createOverlayTextView(Context paramContext)
  {
    TextView localTextView = new TextView(paramContext);
    localTextView.setGravity(17);
    localTextView.setTextColor(-1);
    localTextView.setPadding(0, 15, 0, 15);
    return localTextView;
  }

  private void layoutCenteredView(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramView.getMeasuredWidth();
    int j = paramView.getMeasuredHeight();
    int k = (paramInt3 - paramInt1 - i) / 2;
    int l = (paramInt4 - paramInt2 - j) / 2;
    paramView.layout(k, l, k + i, l + j);
  }

  private void showMainView(View paramView)
  {
    this.mMainView = paramView;
    TextView localTextView = this.mErrorView;
    int i;
    label23: int j;
    label48: ImageView localImageView1;
    int k;
    if (this.mMainView == this.mErrorView)
    {
      i = 0;
      localTextView.setVisibility(i);
      LinearLayout localLinearLayout = this.mLoadingView;
      if (this.mMainView != this.mLoadingView)
        break label100;
      j = 0;
      localLinearLayout.setVisibility(j);
      localImageView1 = this.mPlayPauseReplayView;
      View localView = this.mMainView;
      ImageView localImageView2 = this.mPlayPauseReplayView;
      k = 0;
      if (localView != localImageView2)
        break label106;
    }
    while (true)
    {
      localImageView1.setVisibility(k);
      show();
      return;
      i = 4;
      break label23:
      label100: j = 4;
      break label48:
      label106: k = 4;
    }
  }

  protected abstract void createTimeBar(Context paramContext);

  protected boolean fitSystemWindows(Rect paramRect)
  {
    this.mWindowInsets.set(paramRect);
    return true;
  }

  public View getView()
  {
    return this;
  }

  public void hide()
  {
    this.mPlayPauseReplayView.setVisibility(4);
    this.mLoadingView.setVisibility(4);
    this.mBackground.setVisibility(4);
    this.mTimeBar.setVisibility(4);
    setVisibility(4);
    setFocusable(true);
    requestFocus();
  }

  public void onClick(View paramView)
  {
    if ((this.mListener != null) && (paramView == this.mPlayPauseReplayView))
    {
      if (this.mState != State.ENDED)
        break label42;
      if (this.mCanReplay)
        this.mListener.onReplay();
    }
    label42: 
    do
      return;
    while ((this.mState != State.PAUSED) && (this.mState != State.PLAYING));
    this.mListener.onPlayPause();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rect localRect = this.mWindowInsets;
    int i = localRect.left;
    int j = localRect.right;
    int k = localRect.bottom;
    int l = paramInt4 - paramInt2;
    int i1 = paramInt3 - paramInt1;
    if (this.mErrorView.getVisibility() == 0);
    while (true)
    {
      int i2 = l - k;
      this.mBackground.layout(0, i2 - this.mTimeBar.getBarHeight(), i1, i2);
      this.mTimeBar.layout(i, i2 - this.mTimeBar.getPreferredHeight(), i1 - j, i2);
      this.mTimeBar.requestLayout();
      layoutCenteredView(this.mPlayPauseReplayView, 0, 0, i1, l);
      if (this.mMainView != null)
        layoutCenteredView(this.mMainView, 0, 0, i1, l);
      return;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    measureChildren(paramInt1, paramInt2);
  }

  public void onScrubbingEnd(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mListener.onSeekEnd(paramInt1, paramInt2, paramInt3);
  }

  public void onScrubbingMove(int paramInt)
  {
    this.mListener.onSeekMove(paramInt);
  }

  public void onScrubbingStart()
  {
    this.mListener.onSeekStart();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return super.onTouchEvent(paramMotionEvent);
  }

  public void setCanReplay(boolean paramBoolean)
  {
    this.mCanReplay = paramBoolean;
  }

  public void setListener(ControllerOverlay.Listener paramListener)
  {
    this.mListener = paramListener;
  }

  public void setTimes(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTimeBar.setTime(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void show()
  {
    updateViews();
    setVisibility(0);
    setFocusable(false);
  }

  public void showEnded()
  {
    this.mState = State.ENDED;
    showMainView(this.mPlayPauseReplayView);
  }

  public void showErrorMessage(String paramString)
  {
    this.mState = State.ERROR;
    int i = (int)(0.1666667F * getMeasuredWidth());
    this.mErrorView.setPadding(i, this.mErrorView.getPaddingTop(), i, this.mErrorView.getPaddingBottom());
    this.mErrorView.setText(paramString);
    showMainView(this.mErrorView);
  }

  public void showLoading()
  {
    this.mState = State.LOADING;
    showMainView(this.mLoadingView);
  }

  public void showPaused()
  {
    this.mState = State.PAUSED;
    showMainView(this.mPlayPauseReplayView);
  }

  public void showPlaying()
  {
    this.mState = State.PLAYING;
    showMainView(this.mPlayPauseReplayView);
  }

  protected void updateViews()
  {
    this.mBackground.setVisibility(0);
    this.mTimeBar.setVisibility(0);
    ImageView localImageView1 = this.mPlayPauseReplayView;
    int i;
    label34: ImageView localImageView2;
    if (this.mState == State.PAUSED)
    {
      i = 2130837769;
      localImageView1.setImageResource(i);
      localImageView2 = this.mPlayPauseReplayView;
      if ((this.mState == State.LOADING) || (this.mState == State.ERROR) || ((this.mState == State.ENDED) && (!this.mCanReplay)))
        break label119;
    }
    for (int j = 0; ; j = 8)
    {
      localImageView2.setVisibility(j);
      requestLayout();
      return;
      if (this.mState == State.PLAYING)
        i = 2130837768;
      i = 2130837770;
      label119: break label34:
    }
  }

  protected static enum State
  {
    static
    {
      PAUSED = new State("PAUSED", 1);
      ENDED = new State("ENDED", 2);
      ERROR = new State("ERROR", 3);
      LOADING = new State("LOADING", 4);
      State[] arrayOfState = new State[5];
      arrayOfState[0] = PLAYING;
      arrayOfState[1] = PAUSED;
      arrayOfState[2] = ENDED;
      arrayOfState[3] = ERROR;
      arrayOfState[4] = LOADING;
      $VALUES = arrayOfState;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.CommonControllerOverlay
 * JD-Core Version:    0.5.4
 */