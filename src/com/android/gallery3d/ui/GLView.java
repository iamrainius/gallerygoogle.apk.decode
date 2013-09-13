package com.android.gallery3d.ui;

import android.graphics.Rect;
import android.view.MotionEvent;
import com.android.gallery3d.anim.CanvasAnimation;
import com.android.gallery3d.anim.StateTransitionAnimation;
import com.android.gallery3d.common.Utils;
import java.util.ArrayList;

public class GLView
{
  private CanvasAnimation mAnimation;
  private float[] mBackgroundColor;
  protected final Rect mBounds = new Rect();
  private ArrayList<GLView> mComponents;
  private int mLastHeightSpec = -1;
  private int mLastWidthSpec = -1;
  protected int mMeasuredHeight = 0;
  protected int mMeasuredWidth = 0;
  private GLView mMotionTarget;
  protected final Rect mPaddings = new Rect();
  protected GLView mParent;
  private GLRoot mRoot;
  protected int mScrollHeight = 0;
  protected int mScrollWidth = 0;
  protected int mScrollX = 0;
  protected int mScrollY = 0;
  private StateTransitionAnimation mTransition;
  private int mViewFlags = 0;

  private boolean setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 - paramInt1 != this.mBounds.right - this.mBounds.left) || (paramInt4 - paramInt2 != this.mBounds.bottom - this.mBounds.top));
    for (int i = 1; ; i = 0)
    {
      this.mBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
      return i;
    }
  }

  public void addComponent(GLView paramGLView)
  {
    if (paramGLView.mParent != null)
      throw new IllegalStateException();
    if (this.mComponents == null)
      this.mComponents = new ArrayList();
    this.mComponents.add(paramGLView);
    paramGLView.mParent = this;
    if (this.mRoot == null)
      return;
    paramGLView.onAttachToRoot(this.mRoot);
  }

  public void attachToRoot(GLRoot paramGLRoot)
  {
    if ((this.mParent == null) && (this.mRoot == null));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      onAttachToRoot(paramGLRoot);
      return;
    }
  }

  public Rect bounds()
  {
    return this.mBounds;
  }

  public void detachFromRoot()
  {
    if ((this.mParent == null) && (this.mRoot != null));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      onDetachFromRoot();
      return;
    }
  }

  protected boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    int k = paramMotionEvent.getAction();
    if (this.mMotionTarget != null)
    {
      if (k != 0)
        break label101;
      MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
      localMotionEvent.setAction(3);
      dispatchTouchEvent(localMotionEvent, i, j, this.mMotionTarget, false);
      this.mMotionTarget = null;
    }
    if (k == 0)
    {
      int l = -1 + getComponentCount();
      if (l >= 0)
      {
        label74: GLView localGLView = getComponent(l);
        if (localGLView.getVisibility() != 0);
        do
        {
          --l;
          break label74:
          label101: dispatchTouchEvent(paramMotionEvent, i, j, this.mMotionTarget, false);
          if ((k == 3) || (k == 1))
            this.mMotionTarget = null;
          return true;
        }
        while (!dispatchTouchEvent(paramMotionEvent, i, j, localGLView, true));
        this.mMotionTarget = localGLView;
        return true;
      }
    }
    return onTouch(paramMotionEvent);
  }

  protected boolean dispatchTouchEvent(MotionEvent paramMotionEvent, int paramInt1, int paramInt2, GLView paramGLView, boolean paramBoolean)
  {
    Rect localRect = paramGLView.mBounds;
    int i = localRect.left;
    int j = localRect.top;
    if ((!paramBoolean) || (localRect.contains(paramInt1, paramInt2)))
    {
      paramMotionEvent.offsetLocation(-i, -j);
      if (paramGLView.dispatchTouchEvent(paramMotionEvent))
      {
        paramMotionEvent.offsetLocation(i, j);
        return true;
      }
      paramMotionEvent.offsetLocation(i, j);
    }
    return false;
  }

  public float[] getBackgroundColor()
  {
    return this.mBackgroundColor;
  }

  public boolean getBoundsOf(GLView paramGLView, Rect paramRect)
  {
    int i = 0;
    int j = 0;
    for (GLView localGLView = paramGLView; localGLView != this; localGLView = localGLView.mParent)
    {
      if (localGLView == null)
        return false;
      Rect localRect = localGLView.mBounds;
      i += localRect.left;
      j += localRect.top;
    }
    paramRect.set(i, j, i + paramGLView.getWidth(), j + paramGLView.getHeight());
    return true;
  }

  public GLView getComponent(int paramInt)
  {
    if (this.mComponents == null)
      throw new ArrayIndexOutOfBoundsException(paramInt);
    return (GLView)this.mComponents.get(paramInt);
  }

  public int getComponentCount()
  {
    if (this.mComponents == null)
      return 0;
    return this.mComponents.size();
  }

  public GLRoot getGLRoot()
  {
    return this.mRoot;
  }

  public int getHeight()
  {
    return this.mBounds.bottom - this.mBounds.top;
  }

  public int getMeasuredHeight()
  {
    return this.mMeasuredHeight;
  }

  public int getMeasuredWidth()
  {
    return this.mMeasuredWidth;
  }

  public int getVisibility()
  {
    if ((0x1 & this.mViewFlags) == 0)
      return 0;
    return 1;
  }

  public int getWidth()
  {
    return this.mBounds.right - this.mBounds.left;
  }

  public void invalidate()
  {
    GLRoot localGLRoot = getGLRoot();
    if (localGLRoot == null)
      return;
    localGLRoot.requestRender();
  }

  public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mViewFlags = (0xFFFFFFFB & this.mViewFlags);
    onLayout(bool, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void lockRendering()
  {
    if (this.mRoot == null)
      return;
    this.mRoot.lockRenderThread();
  }

  public void measure(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == this.mLastWidthSpec) && (paramInt2 == this.mLastHeightSpec) && ((0x4 & this.mViewFlags) == 0));
    do
    {
      return;
      this.mLastWidthSpec = paramInt1;
      this.mLastHeightSpec = paramInt2;
      this.mViewFlags = (0xFFFFFFFD & this.mViewFlags);
      onMeasure(paramInt1, paramInt2);
    }
    while ((0x2 & this.mViewFlags) != 0);
    throw new IllegalStateException(super.getClass().getName() + " should call setMeasuredSize() in onMeasure()");
  }

  protected void onAttachToRoot(GLRoot paramGLRoot)
  {
    this.mRoot = paramGLRoot;
    int i = 0;
    int j = getComponentCount();
    while (i < j)
    {
      getComponent(i).onAttachToRoot(paramGLRoot);
      ++i;
    }
  }

  protected void onDetachFromRoot()
  {
    int i = 0;
    int j = getComponentCount();
    while (i < j)
    {
      getComponent(i).onDetachFromRoot();
      ++i;
    }
    this.mRoot = null;
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
  }

  protected boolean onTouch(MotionEvent paramMotionEvent)
  {
    return false;
  }

  protected void onVisibilityChanged(int paramInt)
  {
    int i = 0;
    int j = getComponentCount();
    while (i < j)
    {
      GLView localGLView = getComponent(i);
      if (localGLView.getVisibility() == 0)
        localGLView.onVisibilityChanged(paramInt);
      ++i;
    }
  }

  protected void render(GLCanvas paramGLCanvas)
  {
    StateTransitionAnimation localStateTransitionAnimation = this.mTransition;
    boolean bool1 = false;
    if (localStateTransitionAnimation != null)
    {
      boolean bool2 = this.mTransition.calculate(AnimationTime.get());
      bool1 = false;
      if (bool2)
      {
        invalidate();
        bool1 = this.mTransition.isActive();
      }
    }
    renderBackground(paramGLCanvas);
    paramGLCanvas.save();
    if (bool1)
      this.mTransition.applyContentTransform(this, paramGLCanvas);
    int i = 0;
    int j = getComponentCount();
    while (i < j)
    {
      renderChild(paramGLCanvas, getComponent(i));
      ++i;
    }
    paramGLCanvas.restore();
    if (!bool1)
      return;
    this.mTransition.applyOverlay(this, paramGLCanvas);
  }

  protected void renderBackground(GLCanvas paramGLCanvas)
  {
    if (this.mBackgroundColor != null)
      paramGLCanvas.clearBuffer(this.mBackgroundColor);
    if ((this.mTransition == null) || (!this.mTransition.isActive()))
      return;
    this.mTransition.applyBackground(this, paramGLCanvas);
  }

  protected void renderChild(GLCanvas paramGLCanvas, GLView paramGLView)
  {
    if ((paramGLView.getVisibility() != 0) && (paramGLView.mAnimation == null))
      return;
    int i = paramGLView.mBounds.left - this.mScrollX;
    int j = paramGLView.mBounds.top - this.mScrollY;
    paramGLCanvas.translate(i, j);
    CanvasAnimation localCanvasAnimation = paramGLView.mAnimation;
    if (localCanvasAnimation != null)
    {
      paramGLCanvas.save(localCanvasAnimation.getCanvasSaveFlags());
      if (!localCanvasAnimation.calculate(AnimationTime.get()))
        break label126;
      invalidate();
    }
    while (true)
    {
      localCanvasAnimation.apply(paramGLCanvas);
      paramGLView.render(paramGLCanvas);
      if (localCanvasAnimation != null)
        paramGLCanvas.restore();
      paramGLCanvas.translate(-i, -j);
      return;
      label126: paramGLView.mAnimation = null;
    }
  }

  public void requestLayout()
  {
    this.mViewFlags = (0x4 | this.mViewFlags);
    this.mLastHeightSpec = -1;
    this.mLastWidthSpec = -1;
    if (this.mParent != null)
      this.mParent.requestLayout();
    GLRoot localGLRoot;
    do
    {
      return;
      localGLRoot = getGLRoot();
    }
    while (localGLRoot == null);
    localGLRoot.requestLayoutContentPane();
  }

  public void setBackgroundColor(float[] paramArrayOfFloat)
  {
    this.mBackgroundColor = paramArrayOfFloat;
  }

  public void setIntroAnimation(StateTransitionAnimation paramStateTransitionAnimation)
  {
    this.mTransition = paramStateTransitionAnimation;
    if (this.mTransition == null)
      return;
    this.mTransition.start();
  }

  protected void setMeasuredSize(int paramInt1, int paramInt2)
  {
    this.mViewFlags = (0x2 | this.mViewFlags);
    this.mMeasuredWidth = paramInt1;
    this.mMeasuredHeight = paramInt2;
  }

  public void setVisibility(int paramInt)
  {
    if (paramInt == getVisibility())
      return;
    if (paramInt == 0);
    for (this.mViewFlags = (0xFFFFFFFE & this.mViewFlags); ; this.mViewFlags = (0x1 | this.mViewFlags))
    {
      onVisibilityChanged(paramInt);
      invalidate();
      return;
    }
  }

  public void unlockRendering()
  {
    if (this.mRoot == null)
      return;
    this.mRoot.unlockRenderThread();
  }

  public static abstract interface OnClickListener
  {
    public abstract void onClick(GLView paramGLView);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GLView
 * JD-Core Version:    0.5.4
 */