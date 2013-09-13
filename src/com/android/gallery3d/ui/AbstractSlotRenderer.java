package com.android.gallery3d.ui;

import android.content.Context;
import android.graphics.Rect;

public abstract class AbstractSlotRenderer
  implements SlotView.SlotRenderer
{
  private final NinePatchTexture mFramePressed;
  private FadeOutTexture mFramePressedUp;
  private final NinePatchTexture mFrameSelected;
  private final ResourceTexture mPanoramaIcon;
  private final ResourceTexture mVideoOverlay;
  private final ResourceTexture mVideoPlayIcon;

  protected AbstractSlotRenderer(Context paramContext)
  {
    this.mVideoOverlay = new ResourceTexture(paramContext, 2130837780);
    this.mVideoPlayIcon = new ResourceTexture(paramContext, 2130837678);
    this.mPanoramaIcon = new ResourceTexture(paramContext, 2130837651);
    this.mFramePressed = new NinePatchTexture(paramContext, 2130837639);
    this.mFrameSelected = new NinePatchTexture(paramContext, 2130837640);
  }

  protected static void drawFrame(GLCanvas paramGLCanvas, Rect paramRect, Texture paramTexture, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramTexture.draw(paramGLCanvas, paramInt1 - paramRect.left, paramInt2 - paramRect.top, paramInt3 + paramRect.left + paramRect.right, paramInt4 + paramRect.top + paramRect.bottom);
  }

  protected void drawContent(GLCanvas paramGLCanvas, Texture paramTexture, int paramInt1, int paramInt2, int paramInt3)
  {
    paramGLCanvas.save(2);
    int i = Math.min(paramInt1, paramInt2);
    if (paramInt3 != 0)
    {
      paramGLCanvas.translate(i / 2, i / 2);
      paramGLCanvas.rotate(paramInt3, 0.0F, 0.0F, 1.0F);
      paramGLCanvas.translate(-i / 2, -i / 2);
    }
    float f = Math.min(i / paramTexture.getWidth(), i / paramTexture.getHeight());
    paramGLCanvas.scale(f, f, 1.0F);
    paramTexture.draw(paramGLCanvas, 0, 0);
    paramGLCanvas.restore();
  }

  protected void drawPanoramaIcon(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    int i = Math.min(paramInt1, paramInt2) / 6;
    this.mPanoramaIcon.draw(paramGLCanvas, (paramInt1 - i) / 2, (paramInt2 - i) / 2, i, i);
  }

  protected void drawPressedFrame(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    drawFrame(paramGLCanvas, this.mFramePressed.getPaddings(), this.mFramePressed, 0, 0, paramInt1, paramInt2);
  }

  protected void drawPressedUpFrame(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    if (this.mFramePressedUp == null)
      this.mFramePressedUp = new FadeOutTexture(this.mFramePressed);
    drawFrame(paramGLCanvas, this.mFramePressed.getPaddings(), this.mFramePressedUp, 0, 0, paramInt1, paramInt2);
  }

  protected void drawSelectedFrame(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    drawFrame(paramGLCanvas, this.mFrameSelected.getPaddings(), this.mFrameSelected, 0, 0, paramInt1, paramInt2);
  }

  protected void drawVideoOverlay(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    ResourceTexture localResourceTexture = this.mVideoOverlay;
    float f = paramInt2 / localResourceTexture.getHeight();
    localResourceTexture.draw(paramGLCanvas, 0, 0, Math.round(f * localResourceTexture.getWidth()), Math.round(f * localResourceTexture.getHeight()));
    int i = Math.min(paramInt1, paramInt2) / 6;
    this.mVideoPlayIcon.draw(paramGLCanvas, (paramInt1 - i) / 2, (paramInt2 - i) / 2, i, i);
  }

  protected boolean isPressedUpFrameFinished()
  {
    if (this.mFramePressedUp != null)
    {
      if (this.mFramePressedUp.isAnimating())
        return false;
      this.mFramePressedUp = null;
    }
    return true;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.AbstractSlotRenderer
 * JD-Core Version:    0.5.4
 */