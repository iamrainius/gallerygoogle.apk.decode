package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class TiledTexture
  implements Texture
{
  private static Canvas sCanvas;
  private static Tile sFreeTileHead = null;
  private static final Object sFreeTileLock = new Object();
  private static Paint sPaint;
  private static Bitmap sUploadBitmap;
  private final RectF mDestRect = new RectF();
  private final int mHeight;
  private final RectF mSrcRect = new RectF();
  private final Tile[] mTiles;
  private int mUploadIndex = 0;
  private final int mWidth;

  public TiledTexture(Bitmap paramBitmap)
  {
    this.mWidth = paramBitmap.getWidth();
    this.mHeight = paramBitmap.getHeight();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j = this.mWidth;
    while (i < j)
    {
      int k = 0;
      int l = this.mHeight;
      while (k < l)
      {
        Tile localTile = obtainTile();
        localTile.offsetX = i;
        localTile.offsetY = k;
        localTile.bitmap = paramBitmap;
        localTile.setSize(Math.min(254, this.mWidth - i), Math.min(254, this.mHeight - k));
        localArrayList.add(localTile);
        k += 254;
      }
      i += 254;
    }
    this.mTiles = ((Tile[])localArrayList.toArray(new Tile[localArrayList.size()]));
  }

  public static void freeResources()
  {
    sUploadBitmap = null;
    sCanvas = null;
    sPaint = null;
  }

  private static void freeTile(Tile paramTile)
  {
    paramTile.invalidateContent();
    paramTile.bitmap = null;
    synchronized (sFreeTileLock)
    {
      paramTile.nextFreeTile = sFreeTileHead;
      sFreeTileHead = paramTile;
      return;
    }
  }

  private static void mapRect(RectF paramRectF1, RectF paramRectF2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    paramRectF1.set(paramFloat3 + paramFloat5 * (paramRectF2.left - paramFloat1), paramFloat4 + paramFloat6 * (paramRectF2.top - paramFloat2), paramFloat3 + paramFloat5 * (paramRectF2.right - paramFloat1), paramFloat4 + paramFloat6 * (paramRectF2.bottom - paramFloat2));
  }

  private static Tile obtainTile()
  {
    synchronized (sFreeTileLock)
    {
      Tile localTile1 = sFreeTileHead;
      if (localTile1 == null)
      {
        Tile localTile2 = new Tile(null);
        return localTile2;
      }
      sFreeTileHead = localTile1.nextFreeTile;
      localTile1.nextFreeTile = null;
      return localTile1;
    }
  }

  public static void prepareResources()
  {
    sUploadBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
    sCanvas = new Canvas(sUploadBitmap);
    sPaint = new Paint(2);
    sPaint.setColor(0);
    sPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
  }

  private boolean uploadNextTile(GLCanvas paramGLCanvas)
  {
    if (this.mUploadIndex == this.mTiles.length);
    do
    {
      return true;
      Tile[] arrayOfTile = this.mTiles;
      int i = this.mUploadIndex;
      this.mUploadIndex = (i + 1);
      Tile localTile = arrayOfTile[i];
      if (localTile.bitmap == null)
        continue;
      boolean bool = localTile.isLoaded();
      localTile.updateContent(paramGLCanvas);
      if (bool)
        continue;
      localTile.draw(paramGLCanvas, 0, 0);
    }
    while (this.mUploadIndex == this.mTiles.length);
    return false;
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2)
  {
    draw(paramGLCanvas, paramInt1, paramInt2, this.mWidth, this.mHeight);
  }

  public void draw(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    RectF localRectF1 = this.mSrcRect;
    RectF localRectF2 = this.mDestRect;
    float f1 = paramInt3 / this.mWidth;
    float f2 = paramInt4 / this.mHeight;
    int i = 0;
    int j = this.mTiles.length;
    while (i < j)
    {
      Tile localTile = this.mTiles[i];
      localRectF1.set(0.0F, 0.0F, localTile.contentWidth, localTile.contentHeight);
      localRectF1.offset(localTile.offsetX, localTile.offsetY);
      mapRect(localRectF2, localRectF1, 0.0F, 0.0F, paramInt1, paramInt2, f1, f2);
      localRectF1.offset(1 - localTile.offsetX, 1 - localTile.offsetY);
      paramGLCanvas.drawTexture(localTile, this.mSrcRect, this.mDestRect);
      ++i;
    }
  }

  public void draw(GLCanvas paramGLCanvas, RectF paramRectF1, RectF paramRectF2)
  {
    RectF localRectF1 = this.mSrcRect;
    RectF localRectF2 = this.mDestRect;
    float f1 = paramRectF1.left;
    float f2 = paramRectF1.top;
    float f3 = paramRectF2.left;
    float f4 = paramRectF2.top;
    float f5 = paramRectF2.width() / paramRectF1.width();
    float f6 = paramRectF2.height() / paramRectF1.height();
    int i = 0;
    int j = this.mTiles.length;
    if (i >= j)
      label68: return;
    Tile localTile = this.mTiles[i];
    localRectF1.set(0.0F, 0.0F, localTile.contentWidth, localTile.contentHeight);
    localRectF1.offset(localTile.offsetX, localTile.offsetY);
    if (!localRectF1.intersect(paramRectF1));
    while (true)
    {
      ++i;
      break label68:
      mapRect(localRectF2, localRectF1, f1, f2, f3, f4, f5, f6);
      localRectF1.offset(1 - localTile.offsetX, 1 - localTile.offsetY);
      paramGLCanvas.drawTexture(localTile, localRectF1, localRectF2);
    }
  }

  public void drawMixed(GLCanvas paramGLCanvas, int paramInt1, float paramFloat, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    RectF localRectF1 = this.mSrcRect;
    RectF localRectF2 = this.mDestRect;
    float f1 = paramInt4 / this.mWidth;
    float f2 = paramInt5 / this.mHeight;
    int i = 0;
    int j = this.mTiles.length;
    while (i < j)
    {
      Tile localTile = this.mTiles[i];
      localRectF1.set(0.0F, 0.0F, localTile.contentWidth, localTile.contentHeight);
      localRectF1.offset(localTile.offsetX, localTile.offsetY);
      mapRect(localRectF2, localRectF1, 0.0F, 0.0F, paramInt2, paramInt3, f1, f2);
      localRectF1.offset(1 - localTile.offsetX, 1 - localTile.offsetY);
      paramGLCanvas.drawMixed(localTile, paramInt1, paramFloat, this.mSrcRect, this.mDestRect);
      ++i;
    }
  }

  public int getHeight()
  {
    return this.mHeight;
  }

  public int getWidth()
  {
    return this.mWidth;
  }

  public boolean isOpaque()
  {
    return false;
  }

  public boolean isReady()
  {
    return this.mUploadIndex == this.mTiles.length;
  }

  public void recycle()
  {
    int i = 0;
    int j = this.mTiles.length;
    while (i < j)
    {
      freeTile(this.mTiles[i]);
      ++i;
    }
  }

  private static class Tile extends UploadedTexture
  {
    public Bitmap bitmap;
    public int contentHeight;
    public int contentWidth;
    public Tile nextFreeTile;
    public int offsetX;
    public int offsetY;

    protected void onFreeBitmap(Bitmap paramBitmap)
    {
    }

    protected Bitmap onGetBitmap()
    {
      int i = 1 - this.offsetX;
      int j = 1 - this.offsetY;
      int k = this.bitmap.getWidth() - i;
      int l = this.bitmap.getHeight() - j;
      TiledTexture.sCanvas.drawBitmap(this.bitmap, i, j, null);
      this.bitmap = null;
      if (i > 0)
        TiledTexture.sCanvas.drawLine(i - 1, 0.0F, i - 1, 256.0F, TiledTexture.sPaint);
      if (j > 0)
        TiledTexture.sCanvas.drawLine(0.0F, j - 1, 256.0F, j - 1, TiledTexture.sPaint);
      if (k < 254)
        TiledTexture.sCanvas.drawLine(k, 0.0F, k, 256.0F, TiledTexture.sPaint);
      if (l < 254)
        TiledTexture.sCanvas.drawLine(0.0F, l, 256.0F, l, TiledTexture.sPaint);
      return TiledTexture.sUploadBitmap;
    }

    public void setSize(int paramInt1, int paramInt2)
    {
      this.contentWidth = paramInt1;
      this.contentHeight = paramInt2;
      this.mWidth = (paramInt1 + 2);
      this.mHeight = (paramInt2 + 2);
      this.mTextureWidth = 256;
      this.mTextureHeight = 256;
    }
  }

  public static class Uploader
    implements GLRoot.OnGLIdleListener
  {
    private final GLRoot mGlRoot;
    private boolean mIsQueued = false;
    private final ArrayDeque<TiledTexture> mTextures = new ArrayDeque(8);

    public Uploader(GLRoot paramGLRoot)
    {
      this.mGlRoot = paramGLRoot;
    }

    public void addTexture(TiledTexture paramTiledTexture)
    {
      monitorenter;
      try
      {
        boolean bool = paramTiledTexture.isReady();
        if (bool);
        do
        {
          return;
          this.mTextures.addLast(paramTiledTexture);
        }
        while (this.mIsQueued);
        this.mIsQueued = true;
      }
      finally
      {
        monitorexit;
      }
    }

    public void clear()
    {
      monitorenter;
      try
      {
        this.mTextures.clear();
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public boolean onGLIdle(GLCanvas paramGLCanvas, boolean paramBoolean)
    {
      ArrayDeque localArrayDeque = this.mTextures;
      monitorenter;
      while (true)
      {
        try
        {
          if ((!localArrayDeque.isEmpty()) && (((TiledTexture)localArrayDeque.peekFirst()).uploadNextTile(paramGLCanvas) != 0))
          {
            localArrayDeque.removeFirst();
            this.mGlRoot.requestRender();
          }
          if (!this.mTextures.isEmpty())
          {
            i = 1;
            this.mIsQueued = i;
            boolean bool = this.mIsQueued;
            return bool;
          }
        }
        finally
        {
          monitorexit;
        }
        int i = 0;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.TiledTexture
 * JD-Core Version:    0.5.4
 */