package com.android.gallery3d.ui;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.gallery3d.anim.Animation;
import com.android.gallery3d.data.Path;
import java.util.ArrayList;

public class PhotoFallbackEffect extends Animation
  implements AlbumSlotRenderer.SlotFilter
{
  private static final Interpolator ANIM_INTERPOLATE = new DecelerateInterpolator(1.5F);
  private ArrayList<Entry> mList = new ArrayList();
  private PositionProvider mPositionProvider;
  private float mProgress;
  private RectF mSource = new RectF();
  private RectF mTarget = new RectF();

  public PhotoFallbackEffect()
  {
    setDuration(300);
    setInterpolator(ANIM_INTERPOLATE);
  }

  private void drawEntry(GLCanvas paramGLCanvas, Entry paramEntry)
  {
    if (!paramEntry.texture.isLoaded())
      return;
    int i = paramEntry.texture.getWidth();
    int j = paramEntry.texture.getHeight();
    Rect localRect1 = paramEntry.source;
    Rect localRect2 = paramEntry.dest;
    float f1 = this.mProgress;
    float f2 = f1 * (localRect2.height() / Math.min(localRect1.width(), localRect1.height())) + 1.0F * (1.0F - f1);
    float f3 = f1 * localRect2.centerX() + localRect1.centerX() * (1.0F - f1);
    float f4 = f1 * localRect2.centerY() + localRect1.centerY() * (1.0F - f1);
    float f5 = f2 * localRect1.height();
    float f6 = f2 * localRect1.width();
    if (i > j)
    {
      this.mTarget.set(f3 - f5 / 2.0F, f4 - f5 / 2.0F, f3 + f5 / 2.0F, f4 + f5 / 2.0F);
      this.mSource.set((i - j) / 2, 0.0F, (i + j) / 2, j);
      paramGLCanvas.drawTexture(paramEntry.texture, this.mSource, this.mTarget);
      paramGLCanvas.save(1);
      paramGLCanvas.multiplyAlpha(1.0F - f1);
      this.mTarget.set(f3 - f6 / 2.0F, f4 - f5 / 2.0F, f3 - f5 / 2.0F, f4 + f5 / 2.0F);
      this.mSource.set(0.0F, 0.0F, (i - j) / 2, j);
      paramGLCanvas.drawTexture(paramEntry.texture, this.mSource, this.mTarget);
      this.mTarget.set(f3 + f5 / 2.0F, f4 - f5 / 2.0F, f3 + f6 / 2.0F, f4 + f5 / 2.0F);
      this.mSource.set((i + j) / 2, 0.0F, i, j);
      paramGLCanvas.drawTexture(paramEntry.texture, this.mSource, this.mTarget);
      paramGLCanvas.restore();
      return;
    }
    this.mTarget.set(f3 - f6 / 2.0F, f4 - f6 / 2.0F, f3 + f6 / 2.0F, f4 + f6 / 2.0F);
    this.mSource.set(0.0F, (j - i) / 2, i, (j + i) / 2);
    paramGLCanvas.drawTexture(paramEntry.texture, this.mSource, this.mTarget);
    paramGLCanvas.save(1);
    paramGLCanvas.multiplyAlpha(1.0F - f1);
    this.mTarget.set(f3 - f6 / 2.0F, f4 - f5 / 2.0F, f3 + f6 / 2.0F, f4 - f6 / 2.0F);
    this.mSource.set(0.0F, 0.0F, i, (j - i) / 2);
    paramGLCanvas.drawTexture(paramEntry.texture, this.mSource, this.mTarget);
    this.mTarget.set(f3 - f6 / 2.0F, f4 + f6 / 2.0F, f3 + f6 / 2.0F, f4 + f5 / 2.0F);
    this.mSource.set(0.0F, (i + j) / 2, i, j);
    paramGLCanvas.drawTexture(paramEntry.texture, this.mSource, this.mTarget);
    paramGLCanvas.restore();
  }

  public boolean acceptSlot(int paramInt)
  {
    int i = 0;
    int j = this.mList.size();
    while (i < j)
    {
      if (((Entry)this.mList.get(i)).index == paramInt)
        return false;
      ++i;
    }
    return true;
  }

  public boolean draw(GLCanvas paramGLCanvas)
  {
    boolean bool = calculate(AnimationTime.get());
    int i = 0;
    int j = this.mList.size();
    if (i < j)
    {
      label19: Entry localEntry = (Entry)this.mList.get(i);
      if (localEntry.index < 0);
      while (true)
      {
        ++i;
        break label19:
        localEntry.dest = this.mPositionProvider.getPosition(localEntry.index);
        drawEntry(paramGLCanvas, localEntry);
      }
    }
    return bool;
  }

  protected void onCalculate(float paramFloat)
  {
    this.mProgress = paramFloat;
  }

  public void setPositionProvider(PositionProvider paramPositionProvider)
  {
    this.mPositionProvider = paramPositionProvider;
    if (this.mPositionProvider == null)
      return;
    int i = 0;
    int j = this.mList.size();
    while (i < j)
    {
      Entry localEntry = (Entry)this.mList.get(i);
      localEntry.index = this.mPositionProvider.getItemIndex(localEntry.path);
      ++i;
    }
  }

  public static class Entry
  {
    public Rect dest;
    public int index;
    public Path path;
    public Rect source;
    public RawTexture texture;
  }

  public static abstract interface PositionProvider
  {
    public abstract int getItemIndex(Path paramPath);

    public abstract Rect getPosition(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.PhotoFallbackEffect
 * JD-Core Version:    0.5.4
 */