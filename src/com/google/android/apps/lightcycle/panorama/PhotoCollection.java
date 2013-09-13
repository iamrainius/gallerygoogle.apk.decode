package com.google.android.apps.lightcycle.panorama;

import F;
import android.opengl.Matrix;
import com.google.android.apps.lightcycle.math.Quaternion;
import com.google.android.apps.lightcycle.opengl.DrawableGL;
import com.google.android.apps.lightcycle.opengl.GLTexture;
import com.google.android.apps.lightcycle.opengl.GLTexture.TextureType;
import com.google.android.apps.lightcycle.opengl.OpenGLException;
import com.google.android.apps.lightcycle.opengl.Shader;
import com.google.android.apps.lightcycle.shaders.TransparencyShader;
import com.google.android.apps.lightcycle.util.LG;
import java.util.Vector;

public class PhotoCollection extends DrawableGL
{
  private float[] intrinsicsToCamera = new float[16];
  private Vector<PhotoFrame> mFrames = new Vector();
  private float[] mvp = new float[16];
  private PanoramaFrameOverlay panoramaFrameOverlay;
  private double slerpAlpha = -1.0D;
  private Quaternion tempQuaternion = new Quaternion();
  private float[] tempRotation = new float[16];
  private float[] tempTransform = new float[16];
  private TransparencyShader textureShader;

  public PhotoCollection(PanoramaFrameOverlay paramPanoramaFrameOverlay)
  {
    this.panoramaFrameOverlay = paramPanoramaFrameOverlay;
    try
    {
      this.textureShader = new TransparencyShader();
      OpenGLException.logError("photo collection");
      Matrix.setIdentityM(this.intrinsicsToCamera, 0);
      Matrix.rotateM(this.intrinsicsToCamera, 0, 180.0F, 1.0F, 0.0F, 0.0F);
      return;
    }
    catch (OpenGLException localOpenGLException)
    {
      localOpenGLException.printStackTrace();
    }
  }

  private static void setRotation(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    paramArrayOfFloat2[0] = paramArrayOfFloat1[0];
    paramArrayOfFloat2[1] = paramArrayOfFloat1[1];
    paramArrayOfFloat2[2] = paramArrayOfFloat1[2];
    paramArrayOfFloat2[3] = 0.0F;
    paramArrayOfFloat2[4] = paramArrayOfFloat1[3];
    paramArrayOfFloat2[5] = paramArrayOfFloat1[4];
    paramArrayOfFloat2[6] = paramArrayOfFloat1[5];
    paramArrayOfFloat2[7] = 0.0F;
    paramArrayOfFloat2[8] = paramArrayOfFloat1[6];
    paramArrayOfFloat2[9] = paramArrayOfFloat1[7];
    paramArrayOfFloat2[10] = paramArrayOfFloat1[8];
    paramArrayOfFloat2[14] = 0.0F;
    paramArrayOfFloat2[13] = 0.0F;
    paramArrayOfFloat2[12] = 0.0F;
    paramArrayOfFloat2[11] = 0.0F;
    paramArrayOfFloat2[15] = 1.0F;
  }

  public int addNewPhoto(float[] paramArrayOfFloat)
  {
    PhotoFrame localPhotoFrame = new PhotoFrame(null);
    localPhotoFrame.cameraToWorld = ((float[])paramArrayOfFloat.clone());
    computeWorldToGL(localPhotoFrame.cameraToWorld, localPhotoFrame.cameraToGL);
    localPhotoFrame.previewFadeInAlpha = 0.1F;
    localPhotoFrame.previewTexture = new GLTexture(GLTexture.TextureType.Standard);
    LightCycleNative.CreateFrameTexture(localPhotoFrame.previewTexture.getIndex());
    localPhotoFrame.thumbnailTexture = new GLTexture(GLTexture.TextureType.Standard);
    this.mFrames.add(localPhotoFrame);
    return localPhotoFrame.thumbnailTexture.getIndex();
  }

  public void computeWorldToGL(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    setRotation(paramArrayOfFloat1, this.tempRotation);
    Matrix.multiplyMM(this.tempTransform, 0, this.intrinsicsToCamera, 0, this.tempRotation, 0);
    Matrix.transposeM(paramArrayOfFloat2, 0, this.tempTransform, 0);
  }

  public void drawObject(float[] paramArrayOfFloat)
    throws OpenGLException
  {
    Shader localShader = this.panoramaFrameOverlay.getShader();
    boolean bool1 = this.panoramaFrameOverlay.getDrawOutlineOnly();
    this.panoramaFrameOverlay.getTextureId();
    this.panoramaFrameOverlay.setDrawOutlineOnly(false);
    this.panoramaFrameOverlay.setShader(this.textureShader);
    boolean bool2 = this.slerpAlpha < 0.0D;
    int i = 0;
    if (!bool2)
    {
      this.slerpAlpha += 0.05D * (1.0D - this.slerpAlpha);
      boolean bool3 = this.slerpAlpha < 0.95D;
      i = 0;
      if (!bool3)
      {
        this.slerpAlpha = -1.0D;
        i = 1;
      }
    }
    Vector localVector = this.mFrames;
    monitorenter;
    for (int j = 0; ; ++j)
    {
      while (true)
      {
        PhotoFrame localPhotoFrame;
        float f1;
        int l;
        int i1;
        try
        {
          if (j >= this.mFrames.size())
            break label526;
          localPhotoFrame = (PhotoFrame)this.mFrames.get(j);
          f1 = localPhotoFrame.previewFadeInAlpha;
          int k = localPhotoFrame.count;
          if ((localPhotoFrame.previewTexture.getIndex() != -1) && (i != 0) && (localPhotoFrame.thumbnailLoaded))
            localPhotoFrame.previewTexture.recycle();
          float[] arrayOfFloat = localPhotoFrame.cameraToGL;
          if (this.slerpAlpha >= 0.0D)
          {
            Quaternion.slerp(localPhotoFrame.startQuaternion, localPhotoFrame.endQuaternion, this.slerpAlpha, this.tempQuaternion);
            this.tempQuaternion.toRotationMatrix(this.tempRotation);
            arrayOfFloat = this.tempRotation;
          }
          Matrix.multiplyMM(this.mvp, 0, paramArrayOfFloat, 0, arrayOfFloat, 0);
          this.textureShader.bind();
          l = localPhotoFrame.previewTexture.getIndex();
          i1 = localPhotoFrame.thumbnailTexture.getIndex();
          if ((l != -1) && (((!localPhotoFrame.thumbnailLoaded) || (this.slerpAlpha == -1.0D))))
          {
            this.panoramaFrameOverlay.setTextureId(l);
            this.textureShader.setAlpha(f1);
            this.panoramaFrameOverlay.draw(this.mvp);
            if (f1 < 1.0F)
            {
              if (f1 <= 0.99F)
                break label505;
              localPhotoFrame.previewFadeInAlpha = 1.0F;
            }
            if (k >= 500)
              break label546;
            localPhotoFrame.count = (1 + localPhotoFrame.count);
            break label546:
          }
          if (l != -1)
            break label428;
          this.panoramaFrameOverlay.setTextureId(i1);
          this.textureShader.setAlpha(1.0F);
        }
        finally
        {
          monitorexit;
        }
        label428: float f2 = (float)this.slerpAlpha;
        this.panoramaFrameOverlay.setTextureId(l);
        this.textureShader.setAlpha(1.0F - f2);
        this.panoramaFrameOverlay.draw(this.mvp);
        this.textureShader.bind();
        this.panoramaFrameOverlay.setTextureId(i1);
        this.textureShader.setAlpha(f2);
        this.panoramaFrameOverlay.draw(this.mvp);
        continue;
        label505: localPhotoFrame.previewFadeInAlpha += 0.05F * (1.0F - f1);
      }
      label526: monitorexit;
      this.panoramaFrameOverlay.setDrawOutlineOnly(bool1);
      this.panoramaFrameOverlay.setShader(localShader);
      label546: return;
    }
  }

  public float[] getCameraToWorld(int paramInt)
  {
    return ((PhotoFrame)this.mFrames.get(paramInt)).cameraToWorld;
  }

  public int getNumFrames()
  {
    return this.mFrames.size();
  }

  public void thumbnailLoaded(int paramInt)
  {
    ((PhotoFrame)this.mFrames.get(paramInt)).thumbnailLoaded = true;
  }

  public void undoAddPhoto()
  {
    synchronized (this.mFrames)
    {
      if (this.mFrames.size() > 0)
        this.mFrames.removeElementAt(-1 + this.mFrames.size());
      return;
    }
  }

  public void updateTransforms(float[] paramArrayOfFloat)
  {
    synchronized (this.mFrames)
    {
      LG.d("PhotoCollection::updateTransforms length = " + paramArrayOfFloat.length);
      int i = paramArrayOfFloat.length / 9;
      for (int j = 0; j < i; ++j)
      {
        PhotoFrame localPhotoFrame = (PhotoFrame)this.mFrames.get(j);
        int k = j * 9;
        for (int l = 0; l < 9; ++l)
          localPhotoFrame.cameraToWorld[l] = paramArrayOfFloat[(k + l)];
        this.slerpAlpha = 0.0D;
        localPhotoFrame.startQuaternion.fromRotationMatrix(localPhotoFrame.cameraToGL);
        computeWorldToGL(localPhotoFrame.cameraToWorld, localPhotoFrame.cameraToGL);
        localPhotoFrame.endQuaternion.fromRotationMatrix(localPhotoFrame.cameraToGL);
      }
      return;
    }
  }

  private class PhotoFrame
  {
    public float[] cameraToGL = new float[16];
    public float[] cameraToWorld = null;
    public int count = 0;
    public Quaternion endQuaternion = new Quaternion();
    public float previewFadeInAlpha = 0.0F;
    public GLTexture previewTexture;
    public Quaternion startQuaternion = new Quaternion();
    public boolean thumbnailLoaded = false;
    public GLTexture thumbnailTexture;

    private PhotoFrame()
    {
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.PhotoCollection
 * JD-Core Version:    0.5.4
 */