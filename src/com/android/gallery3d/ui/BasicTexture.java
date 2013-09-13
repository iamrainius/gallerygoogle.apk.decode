package com.android.gallery3d.ui;

import com.android.gallery3d.common.Utils;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

abstract class BasicTexture
  implements Texture
{
  private static WeakHashMap<BasicTexture, Object> sAllTextures = new WeakHashMap();
  private static ThreadLocal sInFinalizer = new ThreadLocal();
  protected GLCanvas mCanvasRef = null;
  private boolean mHasBorder;
  protected int mHeight = -1;
  protected int mId;
  protected int mState;
  protected int mTextureHeight;
  protected int mTextureWidth;
  protected int mWidth = -1;

  protected BasicTexture()
  {
    this(null, 0, 0);
  }

  protected BasicTexture(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    setAssociatedCanvas(paramGLCanvas);
    this.mId = paramInt1;
    this.mState = paramInt2;
    synchronized (sAllTextures)
    {
      sAllTextures.put(this, null);
      return;
    }
  }

  private void freeResource()
  {
    GLCanvas localGLCanvas = this.mCanvasRef;
    if ((localGLCanvas != null) && (isLoaded()))
      localGLCanvas.unloadTexture(this);
    this.mState = 0;
    setAssociatedCanvas(null);
  }

  public static boolean inFinalizer()
  {
    return sInFinalizer.get() != null;
  }

  public static void invalidateAllTextures()
  {
    synchronized (sAllTextures)
    {
      Iterator localIterator = sAllTextures.keySet().iterator();
      if (localIterator.hasNext())
      {
        BasicTexture localBasicTexture = (BasicTexture)localIterator.next();
        localBasicTexture.mState = 0;
        localBasicTexture.setAssociatedCanvas(null);
      }
    }
    monitorexit;
  }

  public static void yieldAllTextures()
  {
    synchronized (sAllTextures)
    {
      Iterator localIterator = sAllTextures.keySet().iterator();
      if (localIterator.hasNext())
        ((BasicTexture)localIterator.next()).yield();
    }
    monitorexit;
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    paramGLCanvas.drawTexture(this, paramInt1, paramInt2, getWidth(), getHeight());
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGLCanvas.drawTexture(this, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  protected void finalize()
  {
    sInFinalizer.set(BasicTexture.class);
    recycle();
    sInFinalizer.set(null);
  }

  public int getHeight()
  {
    return this.mHeight;
  }

  public int getId()
  {
    return this.mId;
  }

  protected abstract int getTarget();

  public int getTextureHeight()
  {
    return this.mTextureHeight;
  }

  public int getTextureWidth()
  {
    return this.mTextureWidth;
  }

  public int getWidth()
  {
    return this.mWidth;
  }

  public boolean hasBorder()
  {
    return this.mHasBorder;
  }

  public boolean isLoaded()
  {
    return this.mState == 1;
  }

  protected abstract boolean onBind(GLCanvas paramGLCanvas);

  public void recycle()
  {
    freeResource();
  }

  protected void setAssociatedCanvas(GLCanvas paramGLCanvas)
  {
    this.mCanvasRef = paramGLCanvas;
  }

  protected void setBorder(boolean paramBoolean)
  {
    this.mHasBorder = paramBoolean;
  }

  protected void setSize(int paramInt1, int paramInt2)
  {
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    this.mTextureWidth = Utils.nextPowerOf2(paramInt1);
    this.mTextureHeight = Utils.nextPowerOf2(paramInt2);
    if ((this.mTextureWidth <= 4096) && (this.mTextureHeight <= 4096))
      return;
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = Integer.valueOf(this.mTextureWidth);
    arrayOfObject[1] = Integer.valueOf(this.mTextureHeight);
    Log.w("BasicTexture", String.format("texture is too large: %d x %d", arrayOfObject), new Exception());
  }

  public void yield()
  {
    freeResource();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.BasicTexture
 * JD-Core Version:    0.5.4
 */