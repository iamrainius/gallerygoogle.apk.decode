package com.android.gallery3d.ui;

import android.content.Context;
import android.view.MotionEvent;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.GalleryUtils;

public class UndoBarView extends GLView
{
  private static long ANIM_TIME = 200L;
  private float mAlpha;
  private long mAnimationStartTime = -1L;
  private final int mBarHeight = GalleryUtils.dpToPixel(48);
  private final int mBarMargin = GalleryUtils.dpToPixel(4);
  private final int mClickRegion;
  private final StringTexture mDeletedText;
  private final int mDeletedTextMargin = GalleryUtils.dpToPixel(16);
  private boolean mDownOnButton;
  private float mFromAlpha;
  private final int mIconMargin = GalleryUtils.dpToPixel(8);
  private final int mIconSize = GalleryUtils.dpToPixel(32);
  private GLView.OnClickListener mOnClickListener;
  private final NinePatchTexture mPanel;
  private final int mSeparatorBottomMargin = GalleryUtils.dpToPixel(10);
  private final int mSeparatorRightMargin = GalleryUtils.dpToPixel(12);
  private final int mSeparatorTopMargin = GalleryUtils.dpToPixel(10);
  private final int mSeparatorWidth = GalleryUtils.dpToPixel(1);
  private float mToAlpha;
  private final ResourceTexture mUndoIcon;
  private final StringTexture mUndoText;
  private final int mUndoTextMargin = GalleryUtils.dpToPixel(16);

  public UndoBarView(Context paramContext)
  {
    this.mPanel = new NinePatchTexture(paramContext, 2130837815);
    this.mUndoText = StringTexture.newInstance(paramContext.getString(2131362217), GalleryUtils.dpToPixel(12), -5592406, 0.0F, true);
    this.mDeletedText = StringTexture.newInstance(paramContext.getString(2131362216), GalleryUtils.dpToPixel(16), -1);
    this.mUndoIcon = new ResourceTexture(paramContext, 2130837704);
    this.mClickRegion = (this.mBarMargin + this.mUndoTextMargin + this.mUndoText.getWidth() + this.mIconMargin + this.mIconSize + this.mSeparatorRightMargin);
  }

  private void advanceAnimation()
  {
    if (this.mAnimationStartTime == -1L)
      return;
    float f1 = (float)(AnimationTime.get() - this.mAnimationStartTime) / (float)ANIM_TIME;
    float f2 = this.mFromAlpha;
    if (this.mToAlpha > this.mFromAlpha);
    while (true)
    {
      this.mAlpha = (f2 + f1);
      this.mAlpha = Utils.clamp(this.mAlpha, 0.0F, 1.0F);
      if (this.mAlpha == this.mToAlpha)
      {
        this.mAnimationStartTime = -1L;
        if (this.mAlpha == 0.0F)
          super.setVisibility(1);
      }
      invalidate();
      return;
      f1 = -f1;
    }
  }

  private static float getTargetAlpha(int paramInt)
  {
    if (paramInt == 0)
      return 1.0F;
    return 0.0F;
  }

  private boolean inUndoButton(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    int i = getWidth();
    int j = getHeight();
    return (f1 >= i - this.mClickRegion) && (f1 < i) && (f2 >= 0.0F) && (f2 < j);
  }

  public void animateVisibility(int paramInt)
  {
    float f = getTargetAlpha(paramInt);
    if ((this.mAnimationStartTime == -1L) && (this.mAlpha == f));
    do
      return;
    while ((this.mAnimationStartTime != -1L) && (this.mToAlpha == f));
    this.mFromAlpha = this.mAlpha;
    this.mToAlpha = f;
    this.mAnimationStartTime = AnimationTime.startTime();
    super.setVisibility(0);
    invalidate();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredSize(0, this.mBarHeight);
  }

  protected boolean onTouch(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    case 2:
    default:
    case 0:
    case 1:
    case 3:
    }
    while (true)
    {
      return true;
      this.mDownOnButton = inUndoButton(paramMotionEvent);
      continue;
      if (!this.mDownOnButton)
        continue;
      if ((this.mOnClickListener != null) && (inUndoButton(paramMotionEvent)))
        this.mOnClickListener.onClick(this);
      this.mDownOnButton = false;
      continue;
      this.mDownOnButton = false;
    }
  }

  protected void render(GLCanvas paramGLCanvas)
  {
    super.render(paramGLCanvas);
    advanceAnimation();
    paramGLCanvas.save(1);
    paramGLCanvas.multiplyAlpha(this.mAlpha);
    int i = getWidth();
    getHeight();
    this.mPanel.draw(paramGLCanvas, this.mBarMargin, 0, i - 2 * this.mBarMargin, this.mBarHeight);
    int j = i - this.mBarMargin - (this.mUndoTextMargin + this.mUndoText.getWidth());
    int k = (this.mBarHeight - this.mUndoText.getHeight()) / 2;
    this.mUndoText.draw(paramGLCanvas, j, k);
    int l = j - (this.mIconMargin + this.mIconSize);
    int i1 = (this.mBarHeight - this.mIconSize) / 2;
    this.mUndoIcon.draw(paramGLCanvas, l, i1, this.mIconSize, this.mIconSize);
    int i2 = l - (this.mSeparatorRightMargin + this.mSeparatorWidth);
    int i3 = this.mSeparatorTopMargin;
    paramGLCanvas.fillRect(i2, i3, this.mSeparatorWidth, this.mBarHeight - this.mSeparatorTopMargin - this.mSeparatorBottomMargin, -5592406);
    int i4 = this.mBarMargin + this.mDeletedTextMargin;
    int i5 = (this.mBarHeight - this.mDeletedText.getHeight()) / 2;
    this.mDeletedText.draw(paramGLCanvas, i4, i5);
    paramGLCanvas.restore();
  }

  public void setOnClickListener(GLView.OnClickListener paramOnClickListener)
  {
    this.mOnClickListener = paramOnClickListener;
  }

  public void setVisibility(int paramInt)
  {
    this.mAlpha = getTargetAlpha(paramInt);
    this.mAnimationStartTime = -1L;
    super.setVisibility(paramInt);
    invalidate();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.UndoBarView
 * JD-Core Version:    0.5.4
 */