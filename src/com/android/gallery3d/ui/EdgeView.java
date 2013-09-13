package com.android.gallery3d.ui;

import android.content.Context;
import android.opengl.Matrix;

public class EdgeView extends GLView
{
  private EdgeEffect[] mEffect = new EdgeEffect[4];
  private float[] mMatrix = new float[64];

  public EdgeView(Context paramContext)
  {
    for (int i = 0; i < 4; ++i)
      this.mEffect[i] = new EdgeEffect(paramContext);
  }

  public void onAbsorb(int paramInt1, int paramInt2)
  {
    this.mEffect[paramInt2].onAbsorb(paramInt1);
    if (this.mEffect[paramInt2].isFinished())
      return;
    invalidate();
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!paramBoolean)
      return;
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    int k = 0;
    if (k < 4)
    {
      if ((k & 0x1) == 0)
        label20: this.mEffect[k].setSize(i, j);
      while (true)
      {
        ++k;
        break label20:
        this.mEffect[k].setSize(j, i);
      }
    }
    Matrix.setIdentityM(this.mMatrix, 0);
    Matrix.setIdentityM(this.mMatrix, 16);
    Matrix.setIdentityM(this.mMatrix, 32);
    Matrix.setIdentityM(this.mMatrix, 48);
    Matrix.rotateM(this.mMatrix, 16, 90.0F, 0.0F, 0.0F, 1.0F);
    Matrix.scaleM(this.mMatrix, 16, 1.0F, -1.0F, 1.0F);
    Matrix.translateM(this.mMatrix, 32, 0.0F, j, 0.0F);
    Matrix.scaleM(this.mMatrix, 32, 1.0F, -1.0F, 1.0F);
    Matrix.translateM(this.mMatrix, 48, i, 0.0F, 0.0F);
    Matrix.rotateM(this.mMatrix, 48, 90.0F, 0.0F, 0.0F, 1.0F);
  }

  public void onPull(int paramInt1, int paramInt2)
  {
    if ((paramInt2 & 0x1) == 0);
    for (int i = getWidth(); ; i = getHeight())
    {
      this.mEffect[paramInt2].onPull(paramInt1 / i);
      if (!this.mEffect[paramInt2].isFinished())
        invalidate();
      return;
    }
  }

  public void onRelease()
  {
    int i = 0;
    int j = 0;
    if (j < 4)
    {
      label4: this.mEffect[j].onRelease();
      if (!this.mEffect[j].isFinished());
      for (int k = 1; ; k = 0)
      {
        i |= k;
        ++j;
        break label4:
      }
    }
    if (i == 0)
      return;
    invalidate();
  }

  protected void render(GLCanvas paramGLCanvas)
  {
    super.render(paramGLCanvas);
    boolean bool = false;
    for (int i = 0; i < 4; ++i)
    {
      paramGLCanvas.save(2);
      paramGLCanvas.multiplyMatrix(this.mMatrix, i * 16);
      bool |= this.mEffect[i].draw(paramGLCanvas);
      paramGLCanvas.restore();
    }
    if (!bool)
      return;
    invalidate();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.EdgeView
 * JD-Core Version:    0.5.4
 */