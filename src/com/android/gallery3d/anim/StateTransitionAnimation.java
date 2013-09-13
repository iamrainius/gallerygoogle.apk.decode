package com.android.gallery3d.anim;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.RawTexture;
import com.android.gallery3d.ui.TiledScreenNail;

public class StateTransitionAnimation extends Animation
{
  private float mCurrentBackgroundAlpha;
  private float mCurrentBackgroundScale;
  private float mCurrentContentAlpha;
  private float mCurrentContentScale;
  private float mCurrentOverlayAlpha;
  private float mCurrentOverlayScale;
  private RawTexture mOldScreenTexture;
  private final Spec mTransitionSpec;

  public StateTransitionAnimation(Spec paramSpec, RawTexture paramRawTexture)
  {
    if (paramSpec != null);
    while (true)
    {
      this.mTransitionSpec = paramSpec;
      setDuration(this.mTransitionSpec.duration);
      setInterpolator(this.mTransitionSpec.interpolator);
      this.mOldScreenTexture = paramRawTexture;
      TiledScreenNail.disableDrawPlaceholder();
      return;
      paramSpec = Spec.OUTGOING;
    }
  }

  public StateTransitionAnimation(Transition paramTransition, RawTexture paramRawTexture)
  {
    this(Spec.access$000(paramTransition), paramRawTexture);
  }

  private void applyOldTexture(GLView paramGLView, GLCanvas paramGLCanvas, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if (this.mOldScreenTexture == null)
      return;
    if (paramBoolean)
      paramGLCanvas.clearBuffer(paramGLView.getBackgroundColor());
    paramGLCanvas.save();
    paramGLCanvas.setAlpha(paramFloat1);
    int i = paramGLView.getWidth() / 2;
    int j = paramGLView.getHeight() / 2;
    paramGLCanvas.translate(i, j);
    paramGLCanvas.scale(paramFloat2, paramFloat2, 1.0F);
    this.mOldScreenTexture.draw(paramGLCanvas, -i, -j);
    paramGLCanvas.restore();
  }

  public void applyBackground(GLView paramGLView, GLCanvas paramGLCanvas)
  {
    if (this.mCurrentBackgroundAlpha <= 0.0F)
      return;
    applyOldTexture(paramGLView, paramGLCanvas, this.mCurrentBackgroundAlpha, this.mCurrentBackgroundScale, true);
  }

  public void applyContentTransform(GLView paramGLView, GLCanvas paramGLCanvas)
  {
    int i = paramGLView.getWidth() / 2;
    int j = paramGLView.getHeight() / 2;
    paramGLCanvas.translate(i, j);
    paramGLCanvas.scale(this.mCurrentContentScale, this.mCurrentContentScale, 1.0F);
    paramGLCanvas.translate(-i, -j);
    paramGLCanvas.setAlpha(this.mCurrentContentAlpha);
  }

  public void applyOverlay(GLView paramGLView, GLCanvas paramGLCanvas)
  {
    if (this.mCurrentOverlayAlpha <= 0.0F)
      return;
    applyOldTexture(paramGLView, paramGLCanvas, this.mCurrentOverlayAlpha, this.mCurrentOverlayScale, false);
  }

  public boolean calculate(long paramLong)
  {
    boolean bool = super.calculate(paramLong);
    if (!isActive())
    {
      if (this.mOldScreenTexture != null)
      {
        this.mOldScreenTexture.recycle();
        this.mOldScreenTexture = null;
      }
      TiledScreenNail.enableDrawPlaceholder();
    }
    return bool;
  }

  protected void onCalculate(float paramFloat)
  {
    this.mCurrentContentScale = (this.mTransitionSpec.contentScaleFrom + paramFloat * (this.mTransitionSpec.contentScaleTo - this.mTransitionSpec.contentScaleFrom));
    this.mCurrentContentAlpha = (this.mTransitionSpec.contentAlphaFrom + paramFloat * (this.mTransitionSpec.contentAlphaTo - this.mTransitionSpec.contentAlphaFrom));
    this.mCurrentBackgroundAlpha = (this.mTransitionSpec.backgroundAlphaFrom + paramFloat * (this.mTransitionSpec.backgroundAlphaTo - this.mTransitionSpec.backgroundAlphaFrom));
    this.mCurrentBackgroundScale = (this.mTransitionSpec.backgroundScaleFrom + paramFloat * (this.mTransitionSpec.backgroundScaleTo - this.mTransitionSpec.backgroundScaleFrom));
    this.mCurrentOverlayScale = (this.mTransitionSpec.overlayScaleFrom + paramFloat * (this.mTransitionSpec.overlayScaleTo - this.mTransitionSpec.overlayScaleFrom));
    this.mCurrentOverlayAlpha = (this.mTransitionSpec.overlayAlphaFrom + paramFloat * (this.mTransitionSpec.overlayAlphaTo - this.mTransitionSpec.overlayAlphaFrom));
  }

  public static class Spec
  {
    private static final Interpolator DEFAULT_INTERPOLATOR = new DecelerateInterpolator();
    public static final Spec INCOMING;
    public static final Spec OUTGOING = new Spec();
    public static final Spec PHOTO_INCOMING;
    public float backgroundAlphaFrom = 0.0F;
    public float backgroundAlphaTo = 0.0F;
    public float backgroundScaleFrom = 0.0F;
    public float backgroundScaleTo = 0.0F;
    public float contentAlphaFrom = 1.0F;
    public float contentAlphaTo = 1.0F;
    public float contentScaleFrom = 1.0F;
    public float contentScaleTo = 1.0F;
    public int duration = 330;
    public Interpolator interpolator = DEFAULT_INTERPOLATOR;
    public float overlayAlphaFrom = 0.0F;
    public float overlayAlphaTo = 0.0F;
    public float overlayScaleFrom = 0.0F;
    public float overlayScaleTo = 0.0F;

    static
    {
      OUTGOING.backgroundAlphaFrom = 0.5F;
      OUTGOING.backgroundAlphaTo = 0.0F;
      OUTGOING.backgroundScaleFrom = 1.0F;
      OUTGOING.backgroundScaleTo = 0.0F;
      OUTGOING.contentAlphaFrom = 0.5F;
      OUTGOING.contentAlphaTo = 1.0F;
      OUTGOING.contentScaleFrom = 3.0F;
      OUTGOING.contentScaleTo = 1.0F;
      INCOMING = new Spec();
      INCOMING.overlayAlphaFrom = 1.0F;
      INCOMING.overlayAlphaTo = 0.0F;
      INCOMING.overlayScaleFrom = 1.0F;
      INCOMING.overlayScaleTo = 3.0F;
      INCOMING.contentAlphaFrom = 0.0F;
      INCOMING.contentAlphaTo = 1.0F;
      INCOMING.contentScaleFrom = 0.25F;
      INCOMING.contentScaleTo = 1.0F;
      PHOTO_INCOMING = INCOMING;
    }

    private static Spec specForTransition(StateTransitionAnimation.Transition paramTransition)
    {
      switch (StateTransitionAnimation.1.$SwitchMap$com$android$gallery3d$anim$StateTransitionAnimation$Transition[paramTransition.ordinal()])
      {
      default:
        return null;
      case 1:
        return OUTGOING;
      case 2:
        return INCOMING;
      case 3:
      }
      return PHOTO_INCOMING;
    }
  }

  public static enum Transition
  {
    static
    {
      Incoming = new Transition("Incoming", 2);
      PhotoIncoming = new Transition("PhotoIncoming", 3);
      Transition[] arrayOfTransition = new Transition[4];
      arrayOfTransition[0] = None;
      arrayOfTransition[1] = Outgoing;
      arrayOfTransition[2] = Incoming;
      arrayOfTransition[3] = PhotoIncoming;
      $VALUES = arrayOfTransition;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.anim.StateTransitionAnimation
 * JD-Core Version:    0.5.4
 */