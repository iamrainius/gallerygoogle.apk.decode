package com.android.gallery3d.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;

public class NinePatchTexture extends ResourceTexture
{
  private NinePatchChunk mChunk;
  private SmallCache<NinePatchInstance> mInstanceCache = new SmallCache(null);

  public NinePatchTexture(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }

  private NinePatchInstance findInstance(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    int i = paramInt2 | paramInt1 << 16;
    NinePatchInstance localNinePatchInstance1 = (NinePatchInstance)this.mInstanceCache.get(i);
    if (localNinePatchInstance1 == null)
    {
      localNinePatchInstance1 = new NinePatchInstance(this, paramInt1, paramInt2);
      NinePatchInstance localNinePatchInstance2 = (NinePatchInstance)this.mInstanceCache.put(i, localNinePatchInstance1);
      if (localNinePatchInstance2 != null)
        localNinePatchInstance2.recycle(paramGLCanvas);
    }
    return localNinePatchInstance1;
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!isLoaded())
      this.mInstanceCache.clear();
    if ((paramInt3 == 0) || (paramInt4 == 0))
      return;
    findInstance(paramGLCanvas, paramInt3, paramInt4).draw(paramGLCanvas, this, paramInt1, paramInt2);
  }

  public NinePatchChunk getNinePatchChunk()
  {
    if (this.mChunk == null)
      onGetBitmap();
    return this.mChunk;
  }

  public Rect getPaddings()
  {
    if (this.mChunk == null)
      onGetBitmap();
    return this.mChunk.mPaddings;
  }

  protected Bitmap onGetBitmap()
  {
    if (this.mBitmap != null)
    {
      localBitmap = this.mBitmap;
      return localBitmap;
    }
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    Bitmap localBitmap = BitmapFactory.decodeResource(this.mContext.getResources(), this.mResId, localOptions);
    this.mBitmap = localBitmap;
    setSize(localBitmap.getWidth(), localBitmap.getHeight());
    if (localBitmap.getNinePatchChunk() == null);
    for (NinePatchChunk localNinePatchChunk = null; ; localNinePatchChunk = NinePatchChunk.deserialize(localBitmap.getNinePatchChunk()))
    {
      this.mChunk = localNinePatchChunk;
      if (this.mChunk == null);
      throw new RuntimeException("invalid nine-patch image: " + this.mResId);
    }
  }

  public void recycle()
  {
    super.recycle();
    GLCanvas localGLCanvas = this.mCanvasRef;
    if (localGLCanvas == null)
      return;
    int i = this.mInstanceCache.size();
    for (int j = 0; j < i; ++j)
      ((NinePatchInstance)this.mInstanceCache.valueAt(j)).recycle(localGLCanvas);
    this.mInstanceCache.clear();
  }

  private static class SmallCache<V>
  {
    private int mCount;
    private int[] mKey = new int[16];
    private V[] mValue = (Object[])new Object[16];

    public void clear()
    {
      for (int i = 0; i < this.mCount; ++i)
        this.mValue[i] = null;
      this.mCount = 0;
    }

    public V get(int paramInt)
    {
      for (int i = 0; i < this.mCount; ++i)
      {
        if (this.mKey[i] != paramInt)
          continue;
        if ((this.mCount > 8) && (i > 0))
        {
          int j = this.mKey[i];
          this.mKey[i] = this.mKey[(i - 1)];
          this.mKey[(i - 1)] = j;
          Object localObject = this.mValue[i];
          this.mValue[i] = this.mValue[(i - 1)];
          this.mValue[(i - 1)] = localObject;
        }
        return this.mValue[i];
      }
      return null;
    }

    public V put(int paramInt, V paramV)
    {
      if (this.mCount == 16)
      {
        Object localObject = this.mValue[15];
        this.mKey[15] = paramInt;
        this.mValue[15] = paramV;
        return localObject;
      }
      this.mKey[this.mCount] = paramInt;
      this.mValue[this.mCount] = paramV;
      this.mCount = (1 + this.mCount);
      return null;
    }

    public int size()
    {
      return this.mCount;
    }

    public V valueAt(int paramInt)
    {
      return this.mValue[paramInt];
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.NinePatchTexture
 * JD-Core Version:    0.5.4
 */