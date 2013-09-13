package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatMath;
import com.android.gallery3d.app.GalleryContext;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.LongSparseArray;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.data.DecodeUtils;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.ThreadPool;
import com.android.gallery3d.util.ThreadPool.CancelListener;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;
import java.util.concurrent.atomic.AtomicBoolean;

public class TileImageView extends GLView
{
  private static int BITMAP_SIZE;
  private static int TILE_SIZE;
  private static BitmapPool sTilePool;
  private final Rect[] mActiveRange;
  private final LongSparseArray<Tile> mActiveTiles = new LongSparseArray();
  private boolean mBackgroundTileUploaded;
  protected int mCenterX;
  protected int mCenterY;
  private final TileQueue mDecodeQueue = new TileQueue(null);
  protected int mImageHeight = -1;
  protected int mImageWidth = -1;
  private boolean mIsTextureFreed;
  private int mLevel = 0;
  protected int mLevelCount;
  private Model mModel;
  private int mOffsetX;
  private int mOffsetY;
  private final TileQueue mRecycledQueue = new TileQueue(null);
  private boolean mRenderComplete;
  protected int mRotation;
  protected float mScale;
  private ScreenNail mScreenNail;
  private final RectF mSourceRect = new RectF();
  private final RectF mTargetRect = new RectF();
  private final ThreadPool mThreadPool;
  private Future<Void> mTileDecoder;
  private final Rect mTileRange = new Rect();
  private final TileUploader mTileUploader;
  private final TileQueue mUploadQueue = new TileQueue(null);
  private int mUploadQuota;

  public TileImageView(GalleryContext paramGalleryContext)
  {
    Rect[] arrayOfRect = new Rect[2];
    arrayOfRect[0] = new Rect();
    arrayOfRect[1] = new Rect();
    this.mActiveRange = arrayOfRect;
    this.mTileUploader = new TileUploader(null);
    this.mThreadPool = paramGalleryContext.getThreadPool();
    this.mTileDecoder = this.mThreadPool.submit(new TileDecoder(null));
    if (TILE_SIZE == 0)
    {
      if (!GalleryUtils.isHighResolution(paramGalleryContext.getAndroidContext()))
        break label239;
      TILE_SIZE = 510;
    }
    while (true)
    {
      BITMAP_SIZE = 2 + TILE_SIZE;
      boolean bool = ApiHelper.HAS_REUSING_BITMAP_IN_BITMAP_REGION_DECODER;
      BitmapPool localBitmapPool = null;
      if (bool)
        localBitmapPool = new BitmapPool(BITMAP_SIZE, BITMAP_SIZE, 128);
      sTilePool = localBitmapPool;
      return;
      label239: TILE_SIZE = 254;
    }
  }

  private void activateTile(int paramInt1, int paramInt2, int paramInt3)
  {
    long l = makeTileKey(paramInt1, paramInt2, paramInt3);
    Tile localTile1 = (Tile)this.mActiveTiles.get(l);
    if (localTile1 != null)
    {
      if (localTile1.mTileState == 2)
        localTile1.mTileState = 1;
      return;
    }
    Tile localTile2 = obtainTile(paramInt1, paramInt2, paramInt3);
    this.mActiveTiles.put(l, localTile2);
  }

  static boolean drawTile(Tile paramTile, GLCanvas paramGLCanvas, RectF paramRectF1, RectF paramRectF2)
  {
    if (paramTile.isContentValid())
    {
      label0: paramRectF1.offset(1.0F, 1.0F);
      paramGLCanvas.drawTexture(paramTile, paramRectF1, paramRectF2);
      return true;
    }
    Tile localTile = paramTile.getParentTile();
    if (localTile == null)
      return false;
    if (paramTile.mX == localTile.mX)
    {
      paramRectF1.left /= 2.0F;
      paramRectF1.right /= 2.0F;
      label69: if (paramTile.mY != localTile.mY)
        break label140;
      paramRectF1.top /= 2.0F;
      paramRectF1.bottom /= 2.0F;
    }
    while (true)
    {
      paramTile = localTile;
      break label0:
      paramRectF1.left = ((TILE_SIZE + paramRectF1.left) / 2.0F);
      paramRectF1.right = ((TILE_SIZE + paramRectF1.right) / 2.0F);
      break label69:
      label140: paramRectF1.top = ((TILE_SIZE + paramRectF1.top) / 2.0F);
      paramRectF1.bottom = ((TILE_SIZE + paramRectF1.bottom) / 2.0F);
    }
  }

  private void getRange(Rect paramRect, int paramInt1, int paramInt2, int paramInt3, float paramFloat, int paramInt4)
  {
    double d1 = Math.toRadians(-paramInt4);
    double d2 = getWidth();
    double d3 = getHeight();
    double d4 = Math.cos(d1);
    double d5 = Math.sin(d1);
    int i = (int)Math.ceil(Math.max(Math.abs(d4 * d2 - d5 * d3), Math.abs(d4 * d2 + d5 * d3)));
    int j = (int)Math.ceil(Math.max(Math.abs(d5 * d2 + d4 * d3), Math.abs(d5 * d2 - d4 * d3)));
    int k = (int)FloatMath.floor(paramInt1 - i / (2.0F * paramFloat));
    int l = (int)FloatMath.floor(paramInt2 - j / (2.0F * paramFloat));
    int i1 = (int)FloatMath.ceil(k + i / paramFloat);
    int i2 = (int)FloatMath.ceil(l + j / paramFloat);
    int i3 = TILE_SIZE << paramInt3;
    paramRect.set(Math.max(0, i3 * (k / i3)), Math.max(0, i3 * (l / i3)), Math.min(this.mImageWidth, i1), Math.min(this.mImageHeight, i2));
  }

  private void getRange(Rect paramRect, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    getRange(paramRect, paramInt1, paramInt2, paramInt3, 1.0F / (1 << paramInt3 + 1), paramInt4);
  }

  private Tile getTile(int paramInt1, int paramInt2, int paramInt3)
  {
    return (Tile)this.mActiveTiles.get(makeTileKey(paramInt1, paramInt2, paramInt3));
  }

  private boolean isScreenNailAnimating()
  {
    return (this.mScreenNail instanceof TiledScreenNail) && (((TiledScreenNail)this.mScreenNail).isAnimating());
  }

  private void layoutTiles(int paramInt1, int paramInt2, float paramFloat, int paramInt3)
  {
    int i = getWidth();
    int j = getHeight();
    this.mLevel = Utils.clamp(Utils.floorLog2(1.0F / paramFloat), 0, this.mLevelCount);
    int k;
    if (this.mLevel != this.mLevelCount)
    {
      Rect localRect2 = this.mTileRange;
      getRange(localRect2, paramInt1, paramInt2, this.mLevel, paramFloat, paramInt3);
      this.mOffsetX = Math.round(i / 2.0F + paramFloat * (localRect2.left - paramInt1));
      this.mOffsetY = Math.round(j / 2.0F + paramFloat * (localRect2.top - paramInt2));
      if (paramFloat * (1 << this.mLevel) > 0.75F)
        k = -1 + this.mLevel;
    }
    int l;
    int i1;
    Rect[] arrayOfRect;
    while (true)
    {
      l = Math.max(0, Math.min(k, -2 + this.mLevelCount));
      i1 = Math.min(l + 2, this.mLevelCount);
      arrayOfRect = this.mActiveRange;
      for (int i2 = l; ; ++i2)
      {
        if (i2 >= i1)
          break label259;
        getRange(arrayOfRect[(i2 - l)], paramInt1, paramInt2, i2, paramInt3);
      }
      k = this.mLevel;
      continue;
      k = -2 + this.mLevel;
      this.mOffsetX = Math.round(i / 2.0F - paramFloat * paramInt1);
      this.mOffsetY = Math.round(j / 2.0F - paramFloat * paramInt2);
    }
    if (paramInt3 % 90 != 0)
      label259: return;
    monitorenter;
    while (true)
    {
      int i4;
      int i6;
      int i7;
      int i8;
      int i10;
      try
      {
        this.mDecodeQueue.clean();
        this.mUploadQueue.clean();
        this.mBackgroundTileUploaded = false;
        int i3 = this.mActiveTiles.size();
        i4 = 0;
        if (i4 < i3)
        {
          Tile localTile = (Tile)this.mActiveTiles.valueAt(i4);
          int i5 = localTile.mTileLevel;
          if ((i5 >= l) && (i5 < i1) && (arrayOfRect[(i5 - l)].contains(localTile.mX, localTile.mY)))
            break label512;
          this.mActiveTiles.removeAt(i4);
          --i4;
          --i3;
          recycleTile(localTile);
          break label512:
        }
        monitorexit;
        i6 = l;
        if (i6 >= i1)
          break label507;
        i7 = TILE_SIZE << i6;
        Rect localRect1 = arrayOfRect[(i6 - l)];
        i8 = localRect1.top;
        int i9 = localRect1.bottom;
        if (i8 >= i9)
          break label501;
        i10 = localRect1.left;
        int i11 = localRect1.right;
        if (i10 >= i11)
          break label491;
        activateTile(i10, i8, i6);
      }
      finally
      {
        monitorexit;
      }
      label491: i8 += i7;
      continue;
      label501: ++i6;
      continue;
      label507: invalidate();
      return;
      label512: ++i4;
    }
  }

  private static long makeTileKey(int paramInt1, int paramInt2, int paramInt3)
  {
    return (paramInt1 << 16 | paramInt2) << 16 | paramInt3;
  }

  private Tile obtainTile(int paramInt1, int paramInt2, int paramInt3)
  {
    monitorenter;
    Tile localTile;
    try
    {
      localTile = this.mRecycledQueue.pop();
      if (localTile != null)
      {
        localTile.mTileState = 1;
        localTile.update(paramInt1, paramInt2, paramInt3);
        return localTile;
      }
    }
    finally
    {
      monitorexit;
    }
  }

  private void uploadBackgroundTiles(GLCanvas paramGLCanvas)
  {
    this.mBackgroundTileUploaded = true;
    int i = this.mActiveTiles.size();
    for (int j = 0; j < i; ++j)
    {
      Tile localTile = (Tile)this.mActiveTiles.valueAt(j);
      if (localTile.isContentValid())
        continue;
      queueForDecode(localTile);
    }
  }

  // ERROR //
  boolean decodeTile(Tile paramTile)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: getfield 177	com/android/gallery3d/ui/TileImageView$Tile:mTileState	I
    //   6: iconst_2
    //   7: if_icmpeq +7 -> 14
    //   10: aload_0
    //   11: monitorexit
    //   12: iconst_0
    //   13: ireturn
    //   14: aload_1
    //   15: iconst_4
    //   16: putfield 177	com/android/gallery3d/ui/TileImageView$Tile:mTileState	I
    //   19: aload_0
    //   20: monitorexit
    //   21: aload_1
    //   22: invokevirtual 364	com/android/gallery3d/ui/TileImageView$Tile:decode	()Z
    //   25: istore_3
    //   26: aload_0
    //   27: monitorenter
    //   28: aload_1
    //   29: getfield 177	com/android/gallery3d/ui/TileImageView$Tile:mTileState	I
    //   32: bipush 32
    //   34: if_icmpne +55 -> 89
    //   37: aload_1
    //   38: bipush 64
    //   40: putfield 177	com/android/gallery3d/ui/TileImageView$Tile:mTileState	I
    //   43: aload_1
    //   44: getfield 368	com/android/gallery3d/ui/TileImageView$Tile:mDecodedTile	Landroid/graphics/Bitmap;
    //   47: ifnull +24 -> 71
    //   50: getstatic 143	com/android/gallery3d/ui/TileImageView:sTilePool	Lcom/android/gallery3d/data/BitmapPool;
    //   53: ifnull +13 -> 66
    //   56: getstatic 143	com/android/gallery3d/ui/TileImageView:sTilePool	Lcom/android/gallery3d/data/BitmapPool;
    //   59: aload_1
    //   60: getfield 368	com/android/gallery3d/ui/TileImageView$Tile:mDecodedTile	Landroid/graphics/Bitmap;
    //   63: invokevirtual 372	com/android/gallery3d/data/BitmapPool:recycle	(Landroid/graphics/Bitmap;)V
    //   66: aload_1
    //   67: aconst_null
    //   68: putfield 368	com/android/gallery3d/ui/TileImageView$Tile:mDecodedTile	Landroid/graphics/Bitmap;
    //   71: aload_0
    //   72: getfield 76	com/android/gallery3d/ui/TileImageView:mRecycledQueue	Lcom/android/gallery3d/ui/TileImageView$TileQueue;
    //   75: aload_1
    //   76: invokevirtual 375	com/android/gallery3d/ui/TileImageView$TileQueue:push	(Lcom/android/gallery3d/ui/TileImageView$Tile;)Z
    //   79: pop
    //   80: aload_0
    //   81: monitorexit
    //   82: iconst_0
    //   83: ireturn
    //   84: astore_2
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_2
    //   88: athrow
    //   89: iload_3
    //   90: ifeq +24 -> 114
    //   93: bipush 8
    //   95: istore 5
    //   97: aload_1
    //   98: iload 5
    //   100: putfield 177	com/android/gallery3d/ui/TileImageView$Tile:mTileState	I
    //   103: aload_0
    //   104: monitorexit
    //   105: iload_3
    //   106: ireturn
    //   107: astore 4
    //   109: aload_0
    //   110: monitorexit
    //   111: aload 4
    //   113: athrow
    //   114: bipush 16
    //   116: istore 5
    //   118: goto -21 -> 97
    //
    // Exception table:
    //   from	to	target	type
    //   2	12	84	finally
    //   14	21	84	finally
    //   85	87	84	finally
    //   28	66	107	finally
    //   66	71	107	finally
    //   71	82	107	finally
    //   97	105	107	finally
    //   109	111	107	finally
  }

  public void drawTile(GLCanvas paramGLCanvas, int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    RectF localRectF1 = this.mSourceRect;
    RectF localRectF2 = this.mTargetRect;
    localRectF2.set(paramFloat1, paramFloat2, paramFloat1 + paramFloat3, paramFloat2 + paramFloat3);
    localRectF1.set(0.0F, 0.0F, TILE_SIZE, TILE_SIZE);
    Tile localTile = getTile(paramInt1, paramInt2, paramInt3);
    if (localTile != null)
    {
      if (!localTile.isContentValid())
      {
        if (localTile.mTileState != 8)
          break label124;
        if (this.mUploadQuota <= 0)
          break label116;
        this.mUploadQuota = (-1 + this.mUploadQuota);
        localTile.updateContent(paramGLCanvas);
      }
      label102: if (!drawTile(localTile, paramGLCanvas, localRectF1, localRectF2));
    }
    do
    {
      return;
      label116: this.mRenderComplete = false;
      break label102:
      label124: if (localTile.mTileState != 16);
      this.mRenderComplete = false;
      queueForDecode(localTile);
      break label102:
    }
    while (this.mScreenNail == null);
    int i = TILE_SIZE << paramInt3;
    float f1 = this.mScreenNail.getWidth() / this.mImageWidth;
    float f2 = this.mScreenNail.getHeight() / this.mImageHeight;
    localRectF1.set(f1 * paramInt1, f2 * paramInt2, f1 * (paramInt1 + i), f2 * (paramInt2 + i));
    this.mScreenNail.draw(paramGLCanvas, localRectF1, localRectF2);
  }

  public void freeTextures()
  {
    this.mIsTextureFreed = true;
    if (this.mTileDecoder != null)
    {
      this.mTileDecoder.cancel();
      this.mTileDecoder.get();
      this.mTileDecoder = null;
    }
    int i = this.mActiveTiles.size();
    for (int j = 0; j < i; ++j)
      ((Tile)this.mActiveTiles.valueAt(j)).recycle();
    this.mActiveTiles.clear();
    this.mTileRange.set(0, 0, 0, 0);
    monitorenter;
    try
    {
      this.mUploadQueue.clean();
      this.mDecodeQueue.clean();
      for (Tile localTile = this.mRecycledQueue.pop(); localTile != null; localTile = this.mRecycledQueue.pop())
        localTile.recycle();
      monitorexit;
      setScreenNail(null);
      if (sTilePool != null);
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  protected void invalidateTiles()
  {
    monitorenter;
    try
    {
      this.mDecodeQueue.clean();
      this.mUploadQueue.clean();
      int i = this.mActiveTiles.size();
      for (int j = 0; j < i; ++j)
        recycleTile((Tile)this.mActiveTiles.valueAt(j));
      this.mActiveTiles.clear();
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void notifyModelInvalidated()
  {
    invalidateTiles();
    if (this.mModel == null)
    {
      this.mScreenNail = null;
      this.mImageWidth = 0;
      this.mImageHeight = 0;
    }
    for (this.mLevelCount = 0; ; this.mLevelCount = this.mModel.getLevelCount())
    {
      layoutTiles(this.mCenterX, this.mCenterY, this.mScale, this.mRotation);
      invalidate();
      return;
      setScreenNail(this.mModel.getScreenNail());
      this.mImageWidth = this.mModel.getImageWidth();
      this.mImageHeight = this.mModel.getImageHeight();
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (!paramBoolean)
      return;
    layoutTiles(this.mCenterX, this.mCenterY, this.mScale, this.mRotation);
  }

  public void prepareTextures()
  {
    if (this.mTileDecoder == null)
      this.mTileDecoder = this.mThreadPool.submit(new TileDecoder(null));
    ScreenNail localScreenNail;
    if (this.mIsTextureFreed)
    {
      layoutTiles(this.mCenterX, this.mCenterY, this.mScale, this.mRotation);
      this.mIsTextureFreed = false;
      Model localModel = this.mModel;
      localScreenNail = null;
      if (localModel != null)
        break label76;
    }
    while (true)
    {
      setScreenNail(localScreenNail);
      return;
      label76: localScreenNail = this.mModel.getScreenNail();
    }
  }

  void queueForDecode(Tile paramTile)
  {
    monitorenter;
    try
    {
      if (paramTile.mTileState == 1)
      {
        paramTile.mTileState = 2;
        if (this.mDecodeQueue.push(paramTile))
          super.notifyAll();
      }
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

  void queueForUpload(Tile paramTile)
  {
    monitorenter;
    try
    {
      this.mUploadQueue.push(paramTile);
      monitorexit;
      if (this.mTileUploader.mActive.compareAndSet(false, true));
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  void recycleTile(Tile paramTile)
  {
    monitorenter;
    while (true)
      try
      {
        if (paramTile.mTileState == 4)
        {
          paramTile.mTileState = 32;
          return;
        }
        paramTile.mTileState = 64;
        if (paramTile.mDecodedTile != null)
        {
          if (sTilePool != null)
            sTilePool.recycle(paramTile.mDecodedTile);
          paramTile.mDecodedTile = null;
        }
      }
      finally
      {
        monitorexit;
      }
  }

  protected void render(GLCanvas paramGLCanvas)
  {
    this.mUploadQuota = 1;
    this.mRenderComplete = true;
    int i = this.mLevel;
    int j = this.mRotation;
    int k = 0;
    if (j != 0)
      k = 0x0 | 0x2;
    if (k != 0)
    {
      paramGLCanvas.save(k);
      if (j != 0)
      {
        int i5 = getWidth() / 2;
        int i6 = getHeight() / 2;
        paramGLCanvas.translate(i5, i6);
        paramGLCanvas.rotate(j, 0.0F, 0.0F, 1.0F);
        paramGLCanvas.translate(-i5, -i6);
      }
    }
    while (true)
    {
      int l;
      int i1;
      int i2;
      try
      {
        if ((i != this.mLevelCount) && (!isScreenNailAnimating()))
        {
          if (this.mScreenNail != null)
            this.mScreenNail.noDraw();
          l = TILE_SIZE << i;
          float f1 = l * this.mScale;
          Rect localRect = this.mTileRange;
          i1 = localRect.top;
          i2 = 0;
          if (i1 >= localRect.bottom)
            break label311;
          float f2 = this.mOffsetY + f1 * i2;
          int i3 = localRect.left;
          for (int i4 = 0; ; ++i4)
          {
            if (i3 >= localRect.right)
              break label363;
            drawTile(paramGLCanvas, i3, i1, i, this.mOffsetX + f1 * i4, f2, f1);
            i3 += l;
          }
        }
        if (this.mScreenNail != null)
        {
          this.mScreenNail.draw(paramGLCanvas, this.mOffsetX, this.mOffsetY, Math.round(this.mImageWidth * this.mScale), Math.round(this.mImageHeight * this.mScale));
          if (isScreenNailAnimating())
            invalidate();
        }
        if (k != 0)
          label311: paramGLCanvas.restore();
        if (this.mRenderComplete)
          return;
      }
      finally
      {
        if (k != 0)
          paramGLCanvas.restore();
      }
      invalidate();
      return;
      label363: i1 += l;
      ++i2;
    }
  }

  public void setModel(Model paramModel)
  {
    this.mModel = paramModel;
    if (paramModel == null)
      return;
    notifyModelInvalidated();
  }

  public boolean setPosition(int paramInt1, int paramInt2, float paramFloat, int paramInt3)
  {
    if ((this.mCenterX == paramInt1) && (this.mCenterY == paramInt2) && (this.mScale == paramFloat) && (this.mRotation == paramInt3))
      return false;
    this.mCenterX = paramInt1;
    this.mCenterY = paramInt2;
    this.mScale = paramFloat;
    this.mRotation = paramInt3;
    layoutTiles(paramInt1, paramInt2, paramFloat, paramInt3);
    invalidate();
    return true;
  }

  public void setScreenNail(ScreenNail paramScreenNail)
  {
    this.mScreenNail = paramScreenNail;
  }

  public static abstract interface Model
  {
    public abstract int getImageHeight();

    public abstract int getImageWidth();

    public abstract int getLevelCount();

    public abstract ScreenNail getScreenNail();

    public abstract Bitmap getTile(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BitmapPool paramBitmapPool);
  }

  private class Tile extends UploadedTexture
  {
    public Bitmap mDecodedTile;
    public Tile mNext;
    public int mTileLevel;
    public volatile int mTileState = 1;
    public int mX;
    public int mY;

    public Tile(int paramInt1, int paramInt2, int arg4)
    {
      this.mX = paramInt1;
      this.mY = paramInt2;
      int i;
      this.mTileLevel = i;
    }

    boolean decode()
    {
      try
      {
        this.mDecodedTile = DecodeUtils.ensureGLCompatibleBitmap(TileImageView.this.mModel.getTile(this.mTileLevel, this.mX, this.mY, TileImageView.TILE_SIZE, 1, TileImageView.sTilePool));
        if (this.mDecodedTile == null)
          break label60;
        return true;
      }
      catch (Throwable localThrowable)
      {
        Log.w("TileImageView", "fail to decode tile", localThrowable);
      }
      label60: return false;
    }

    public Tile getParentTile()
    {
      if (1 + this.mTileLevel == TileImageView.this.mLevelCount)
        return null;
      int i = TileImageView.TILE_SIZE << 1 + this.mTileLevel;
      int j = i * (this.mX / i);
      int k = i * (this.mY / i);
      return TileImageView.this.getTile(j, k, 1 + this.mTileLevel);
    }

    public int getTextureHeight()
    {
      return 2 + TileImageView.TILE_SIZE;
    }

    public int getTextureWidth()
    {
      return 2 + TileImageView.TILE_SIZE;
    }

    protected void onFreeBitmap(Bitmap paramBitmap)
    {
      if (TileImageView.sTilePool == null)
        return;
      TileImageView.sTilePool.recycle(paramBitmap);
    }

    protected Bitmap onGetBitmap()
    {
      if (this.mTileState == 8);
      for (boolean bool = true; ; bool = false)
      {
        Utils.assertTrue(bool);
        int i = 1 + (TileImageView.this.mImageWidth - this.mX >> this.mTileLevel);
        int j = 1 + (TileImageView.this.mImageHeight - this.mY >> this.mTileLevel);
        setSize(Math.min(TileImageView.BITMAP_SIZE, i), Math.min(TileImageView.BITMAP_SIZE, j));
        Bitmap localBitmap = this.mDecodedTile;
        this.mDecodedTile = null;
        this.mTileState = 1;
        return localBitmap;
      }
    }

    public String toString()
    {
      Object[] arrayOfObject = new Object[4];
      arrayOfObject[0] = Integer.valueOf(this.mX / TileImageView.access$500());
      arrayOfObject[1] = Integer.valueOf(this.mY / TileImageView.access$500());
      arrayOfObject[2] = Integer.valueOf(TileImageView.access$900(TileImageView.this));
      arrayOfObject[3] = Integer.valueOf(TileImageView.this.mLevelCount);
      return String.format("tile(%s, %s, %s / %s)", arrayOfObject);
    }

    public void update(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mX = paramInt1;
      this.mY = paramInt2;
      this.mTileLevel = paramInt3;
      invalidateContent();
    }
  }

  private class TileDecoder
    implements ThreadPool.Job<Void>
  {
    private ThreadPool.CancelListener mNotifier = new ThreadPool.CancelListener()
    {
      public void onCancel()
      {
        synchronized (TileImageView.this)
        {
          TileImageView.this.notifyAll();
          return;
        }
      }
    };

    private TileDecoder()
    {
    }

    public Void run(ThreadPool.JobContext paramJobContext)
    {
      paramJobContext.setMode(0);
      paramJobContext.setCancelListener(this.mNotifier);
      if (!paramJobContext.isCancelled())
        synchronized (TileImageView.this)
        {
          TileImageView.Tile localTile = TileImageView.this.mDecodeQueue.pop();
          if ((localTile == null) && (!paramJobContext.isCancelled()))
            Utils.waitWithoutInterrupt(TileImageView.this);
          if ((localTile != null) && (TileImageView.this.decodeTile(localTile)));
          TileImageView.this.queueForUpload(localTile);
        }
      return null;
    }
  }

  private static class TileQueue
  {
    private TileImageView.Tile mHead;

    public void clean()
    {
      this.mHead = null;
    }

    public TileImageView.Tile pop()
    {
      TileImageView.Tile localTile = this.mHead;
      if (localTile != null)
        this.mHead = localTile.mNext;
      return localTile;
    }

    public boolean push(TileImageView.Tile paramTile)
    {
      if (this.mHead == null);
      for (int i = 1; ; i = 0)
      {
        paramTile.mNext = this.mHead;
        this.mHead = paramTile;
        return i;
      }
    }
  }

  private class TileUploader
    implements GLRoot.OnGLIdleListener
  {
    AtomicBoolean mActive = new AtomicBoolean(false);

    private TileUploader()
    {
    }

    public boolean onGLIdle(GLCanvas paramGLCanvas, boolean paramBoolean)
    {
      if (paramBoolean)
        return true;
      int i = 1;
      TileImageView.Tile localTile = null;
      do
      {
        label11: if (i > 0);
        synchronized (TileImageView.this)
        {
          localTile = TileImageView.this.mUploadQueue.pop();
          if (localTile == null)
          {
            if (localTile == null)
              this.mActive.set(false);
            if (localTile == null);
            return false;
          }
        }
      }
      while (localTile.isContentValid());
      boolean bool1 = localTile.isLoaded();
      if (localTile.mTileState == 8);
      for (boolean bool2 = true; ; bool2 = false)
      {
        Utils.assertTrue(bool2);
        localTile.updateContent(paramGLCanvas);
        if (!bool1)
          localTile.draw(paramGLCanvas, 0, 0);
        --i;
        break label11:
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.TileImageView
 * JD-Core Version:    0.5.4
 */