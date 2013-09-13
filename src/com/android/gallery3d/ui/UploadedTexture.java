package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;
import com.android.gallery3d.common.Utils;
import java.util.HashMap;
import javax.microedition.khronos.opengles.GL11;

abstract class UploadedTexture extends BasicTexture
{
  private static BorderKey sBorderKey;
  private static HashMap<BorderKey, Bitmap> sBorderLines = new HashMap();
  static float[] sCropRect;
  static int[] sTextureId;
  private static int sUploadedCount;
  protected Bitmap mBitmap;
  private int mBorder;
  private boolean mContentValid = true;
  private boolean mIsUploading = false;
  private boolean mOpaque = true;
  private boolean mThrottled = false;

  static
  {
    sBorderKey = new BorderKey(null);
    sTextureId = new int[1];
    sCropRect = new float[4];
  }

  protected UploadedTexture()
  {
    this(false);
  }

  protected UploadedTexture(boolean paramBoolean)
  {
    super(null, 0, 0);
    if (!paramBoolean)
      return;
    setBorder(true);
    this.mBorder = 1;
  }

  private void freeBitmap()
  {
    if (this.mBitmap != null);
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      onFreeBitmap(this.mBitmap);
      this.mBitmap = null;
      return;
    }
  }

  private Bitmap getBitmap()
  {
    if (this.mBitmap == null)
    {
      this.mBitmap = onGetBitmap();
      int i = this.mBitmap.getWidth() + 2 * this.mBorder;
      int j = this.mBitmap.getHeight() + 2 * this.mBorder;
      if (this.mWidth == -1)
        setSize(i, j);
    }
    return this.mBitmap;
  }

  private static Bitmap getBorderLine(boolean paramBoolean, Bitmap.Config paramConfig, int paramInt)
  {
    BorderKey localBorderKey = sBorderKey;
    localBorderKey.vertical = paramBoolean;
    localBorderKey.config = paramConfig;
    localBorderKey.length = paramInt;
    Bitmap localBitmap = (Bitmap)sBorderLines.get(localBorderKey);
    if (localBitmap == null)
      if (!paramBoolean)
        break label64;
    for (localBitmap = Bitmap.createBitmap(1, paramInt, paramConfig); ; localBitmap = Bitmap.createBitmap(paramInt, 1, paramConfig))
    {
      sBorderLines.put(localBorderKey.clone(), localBitmap);
      label64: return localBitmap;
    }
  }

  public static void resetUploadLimit()
  {
    sUploadedCount = 0;
  }

  public static boolean uploadLimitReached()
  {
    return sUploadedCount > 100;
  }

  private void uploadToCanvas(GLCanvas paramGLCanvas)
  {
    GL11 localGL11 = paramGLCanvas.getGLInstance();
    Bitmap localBitmap1 = getBitmap();
    if (localBitmap1 != null)
    {
      int j;
      label77: int i1;
      label273: int i2;
      Bitmap localBitmap2;
      try
      {
        int i = localBitmap1.getWidth();
        j = localBitmap1.getHeight();
        (i + 2 * this.mBorder);
        (j + 2 * this.mBorder);
        int k = getTextureWidth();
        int l = getTextureHeight();
        boolean bool;
        if ((i <= k) && (j <= l))
        {
          bool = true;
          Utils.assertTrue(bool);
          sCropRect[0] = this.mBorder;
          sCropRect[1] = (j + this.mBorder);
          sCropRect[2] = i;
          sCropRect[3] = (-j);
          GLId.glGenTextures(1, sTextureId, 0);
          localGL11.glBindTexture(3553, sTextureId[0]);
          localGL11.glTexParameterfv(3553, 35741, sCropRect, 0);
          localGL11.glTexParameteri(3553, 10242, 33071);
          localGL11.glTexParameteri(3553, 10243, 33071);
          localGL11.glTexParameterf(3553, 10241, 9729.0F);
          localGL11.glTexParameterf(3553, 10240, 9729.0F);
          if ((i != k) || (j != l))
            break label273;
          GLUtils.texImage2D(3553, 0, localBitmap1, 0);
        }
        Bitmap.Config localConfig;
        do
        {
          freeBitmap();
          setAssociatedCanvas(paramGLCanvas);
          this.mId = sTextureId[0];
          this.mState = 1;
          return;
          bool = false;
          break label77:
          i1 = GLUtils.getInternalFormat(localBitmap1);
          i2 = GLUtils.getType(localBitmap1);
          localConfig = localBitmap1.getConfig();
          localGL11.glTexImage2D(3553, 0, i1, k, l, 0, i1, i2, null);
          GLUtils.texSubImage2D(3553, 0, this.mBorder, this.mBorder, localBitmap1, i1, i2);
          if (this.mBorder > 0)
          {
            GLUtils.texSubImage2D(3553, 0, 0, 0, getBorderLine(true, localConfig, l), i1, i2);
            GLUtils.texSubImage2D(3553, 0, 0, 0, getBorderLine(false, localConfig, k), i1, i2);
          }
          if (i + this.mBorder >= k)
            continue;
          Bitmap localBitmap3 = getBorderLine(true, localConfig, l);
          GLUtils.texSubImage2D(3553, 0, i + this.mBorder, 0, localBitmap3, i1, i2);
        }
        while (j + this.mBorder >= l);
        localBitmap2 = getBorderLine(false, localConfig, k);
      }
      finally
      {
        freeBitmap();
      }
    }
    this.mState = -1;
    throw new RuntimeException("Texture load fail, no bitmap");
  }

  public int getHeight()
  {
    if (this.mWidth == -1)
      getBitmap();
    return this.mHeight;
  }

  protected int getTarget()
  {
    return 3553;
  }

  public int getWidth()
  {
    if (this.mWidth == -1)
      getBitmap();
    return this.mWidth;
  }

  protected void invalidateContent()
  {
    if (this.mBitmap != null)
      freeBitmap();
    this.mContentValid = false;
    this.mWidth = -1;
    this.mHeight = -1;
  }

  public boolean isContentValid()
  {
    return (isLoaded()) && (this.mContentValid);
  }

  public boolean isOpaque()
  {
    return this.mOpaque;
  }

  public boolean isUploading()
  {
    return this.mIsUploading;
  }

  protected boolean onBind(GLCanvas paramGLCanvas)
  {
    updateContent(paramGLCanvas);
    return isContentValid();
  }

  protected abstract void onFreeBitmap(Bitmap paramBitmap);

  protected abstract Bitmap onGetBitmap();

  public void recycle()
  {
    super.recycle();
    if (this.mBitmap == null)
      return;
    freeBitmap();
  }

  protected void setIsUploading(boolean paramBoolean)
  {
    this.mIsUploading = paramBoolean;
  }

  public void setOpaque(boolean paramBoolean)
  {
    this.mOpaque = paramBoolean;
  }

  public void updateContent(GLCanvas paramGLCanvas)
  {
    if (!isLoaded())
      if (this.mThrottled)
      {
        int k = 1 + sUploadedCount;
        sUploadedCount = k;
        if (k <= 100);
      }
    do
    {
      return;
      uploadToCanvas(paramGLCanvas);
      return;
    }
    while (this.mContentValid);
    Bitmap localBitmap = getBitmap();
    int i = GLUtils.getInternalFormat(localBitmap);
    int j = GLUtils.getType(localBitmap);
    paramGLCanvas.getGLInstance().glBindTexture(3553, this.mId);
    GLUtils.texSubImage2D(3553, 0, this.mBorder, this.mBorder, localBitmap, i, j);
    freeBitmap();
    this.mContentValid = true;
  }

  private static class BorderKey
    implements Cloneable
  {
    public Bitmap.Config config;
    public int length;
    public boolean vertical;

    public BorderKey clone()
    {
      try
      {
        BorderKey localBorderKey = (BorderKey)super.clone();
        return localBorderKey;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
    }

    public boolean equals(Object paramObject)
    {
      if (!paramObject instanceof BorderKey);
      BorderKey localBorderKey;
      do
      {
        return false;
        localBorderKey = (BorderKey)paramObject;
      }
      while ((this.vertical != localBorderKey.vertical) || (this.config != localBorderKey.config) || (this.length != localBorderKey.length));
      return true;
    }

    public int hashCode()
    {
      int i = this.config.hashCode() ^ this.length;
      if (this.vertical)
        return i;
      return -i;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.UploadedTexture
 * JD-Core Version:    0.5.4
 */