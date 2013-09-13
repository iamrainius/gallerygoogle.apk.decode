package com.android.gallery3d.ui;

import java.util.ArrayDeque;

public class TextureUploader
  implements GLRoot.OnGLIdleListener
{
  private final ArrayDeque<UploadedTexture> mBgTextures = new ArrayDeque(64);
  private final ArrayDeque<UploadedTexture> mFgTextures = new ArrayDeque(64);
  private final GLRoot mGLRoot;
  private volatile boolean mIsQueued = false;

  public TextureUploader(GLRoot paramGLRoot)
  {
    this.mGLRoot = paramGLRoot;
  }

  private void queueSelfIfNeed()
  {
    if (this.mIsQueued)
      return;
    this.mIsQueued = true;
    this.mGLRoot.addOnGLIdleListener(this);
  }

  private int upload(GLCanvas paramGLCanvas, ArrayDeque<UploadedTexture> paramArrayDeque, int paramInt, boolean paramBoolean)
  {
    while (paramInt > 0)
    {
      UploadedTexture localUploadedTexture;
      while (true)
      {
        monitorenter;
        try
        {
          if (paramArrayDeque.isEmpty())
            return paramInt;
          localUploadedTexture = (UploadedTexture)paramArrayDeque.removeFirst();
          localUploadedTexture.setIsUploading(false);
          if (!localUploadedTexture.isContentValid())
            break label52;
        }
        finally
        {
          monitorexit;
        }
      }
      label52: localUploadedTexture.updateContent(paramGLCanvas);
      monitorexit;
      if (paramBoolean)
        localUploadedTexture.draw(paramGLCanvas, 0, 0);
      --paramInt;
    }
    return paramInt;
  }

  public void addBgTexture(UploadedTexture paramUploadedTexture)
  {
    monitorenter;
    try
    {
      boolean bool = paramUploadedTexture.isContentValid();
      if (bool)
        return;
      this.mBgTextures.addLast(paramUploadedTexture);
      paramUploadedTexture.setIsUploading(true);
    }
    finally
    {
      monitorexit;
    }
  }

  public void addFgTexture(UploadedTexture paramUploadedTexture)
  {
    monitorenter;
    try
    {
      boolean bool = paramUploadedTexture.isContentValid();
      if (bool)
        return;
      this.mFgTextures.addLast(paramUploadedTexture);
      paramUploadedTexture.setIsUploading(true);
    }
    finally
    {
      monitorexit;
    }
  }

  public void clear()
  {
    monitorenter;
    while (true)
      try
      {
        if (this.mFgTextures.isEmpty())
          break label34;
      }
      finally
      {
        monitorexit;
      }
    while (!this.mBgTextures.isEmpty())
      label34: ((UploadedTexture)this.mBgTextures.pop()).setIsUploading(false);
    monitorexit;
  }

  public boolean onGLIdle(GLCanvas paramGLCanvas, boolean paramBoolean)
  {
    int i = upload(paramGLCanvas, this.mFgTextures, 1, false);
    if (i < 1)
      this.mGLRoot.requestRender();
    upload(paramGLCanvas, this.mBgTextures, i, true);
    monitorenter;
    while (true)
    {
      try
      {
        if (this.mFgTextures.isEmpty())
        {
          boolean bool2 = this.mBgTextures.isEmpty();
          j = 0;
          if (bool2)
          {
            this.mIsQueued = j;
            boolean bool1 = this.mIsQueued;
            return bool1;
          }
        }
      }
      finally
      {
        monitorexit;
      }
      int j = 1;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.TextureUploader
 * JD-Core Version:    0.5.4
 */