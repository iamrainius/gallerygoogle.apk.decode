package com.android.gallery3d.ui;

import android.graphics.RectF;
import android.opengl.GLU;
import android.opengl.Matrix;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.IntArray;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

public class GLCanvasImpl
  implements GLCanvas
{
  private static final float[] BOX_COORDINATES = { 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F };
  private float mAlpha;
  private boolean mBlendEnabled = true;
  private int mBoxCoords;
  int mCountDrawLine;
  int mCountDrawMesh;
  int mCountFillRect;
  int mCountTextureOES;
  int mCountTextureRect;
  private final IntArray mDeleteBuffers = new IntArray();
  private final RectF mDrawTextureSourceRect = new RectF();
  private final RectF mDrawTextureTargetRect = new RectF();
  private int[] mFrameBuffer = new int[1];
  private final GL11 mGL;
  private final GLState mGLState;
  private final float[] mMapPointsBuffer = new float[4];
  private final float[] mMatrixValues = new float[16];
  private ConfigState mRecycledRestoreAction;
  private final ArrayList<ConfigState> mRestoreStack = new ArrayList();
  private int mScreenHeight;
  private int mScreenWidth;
  private final ArrayList<RawTexture> mTargetStack = new ArrayList();
  private RawTexture mTargetTexture;
  private final float[] mTempMatrix = new float[32];
  private final float[] mTextureColor = new float[4];
  private final float[] mTextureMatrixValues = new float[16];
  private final IntArray mUnboundTextures = new IntArray();

  GLCanvasImpl(GL11 paramGL11)
  {
    this.mGL = paramGL11;
    this.mGLState = new GLState(paramGL11);
    initialize();
  }

  private static ByteBuffer allocateDirectNativeOrderBuffer(int paramInt)
  {
    return ByteBuffer.allocateDirect(paramInt).order(ByteOrder.nativeOrder());
  }

  private boolean bindTexture(BasicTexture paramBasicTexture)
  {
    if (!paramBasicTexture.onBind(this))
      return false;
    int i = paramBasicTexture.getTarget();
    this.mGLState.setTextureTarget(i);
    this.mGL.glBindTexture(i, paramBasicTexture.getId());
    return true;
  }

  private static void checkFramebufferStatus(GL11ExtensionPack paramGL11ExtensionPack)
  {
    int i = paramGL11ExtensionPack.glCheckFramebufferStatusOES(36160);
    if (i == 36053)
      return;
    String str = "";
    switch (i)
    {
    case 36056:
    default:
    case 36058:
    case 36054:
    case 36055:
    case 36059:
    case 36060:
    case 36061:
    case 36057:
    }
    while (true)
    {
      throw new RuntimeException(str + ":" + Integer.toHexString(i));
      str = "FRAMEBUFFER_FORMATS";
      continue;
      str = "FRAMEBUFFER_ATTACHMENT";
      continue;
      str = "FRAMEBUFFER_MISSING_ATTACHMENT";
      continue;
      str = "FRAMEBUFFER_DRAW_BUFFER";
      continue;
      str = "FRAMEBUFFER_READ_BUFFER";
      continue;
      str = "FRAMEBUFFER_UNSUPPORTED";
      continue;
      str = "FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
    }
  }

  private static void convertCoordinate(RectF paramRectF1, RectF paramRectF2, BasicTexture paramBasicTexture)
  {
    int i = paramBasicTexture.getWidth();
    int j = paramBasicTexture.getHeight();
    int k = paramBasicTexture.getTextureWidth();
    int l = paramBasicTexture.getTextureHeight();
    paramRectF1.left /= k;
    paramRectF1.right /= k;
    paramRectF1.top /= l;
    paramRectF1.bottom /= l;
    float f1 = i / k;
    if (paramRectF1.right > f1)
    {
      paramRectF2.right = (paramRectF2.left + paramRectF2.width() * (f1 - paramRectF1.left) / paramRectF1.width());
      paramRectF1.right = f1;
    }
    float f2 = j / l;
    if (paramRectF1.bottom <= f2)
      return;
    paramRectF2.bottom = (paramRectF2.top + paramRectF2.height() * (f2 - paramRectF1.top) / paramRectF1.height());
    paramRectF1.bottom = f2;
  }

  private void drawBoundTexture(BasicTexture paramBasicTexture, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isMatrixRotatedOrFlipped(this.mMatrixValues))
      if (paramBasicTexture.hasBorder())
      {
        setTextureCoords(1.0F / paramBasicTexture.getTextureWidth(), 1.0F / paramBasicTexture.getTextureHeight(), (paramBasicTexture.getWidth() - 1.0F) / paramBasicTexture.getTextureWidth(), (paramBasicTexture.getHeight() - 1.0F) / paramBasicTexture.getTextureHeight());
        label61: textureRect(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    int i;
    int j;
    int k;
    int l;
    do
    {
      return;
      setTextureCoords(0.0F, 0.0F, paramBasicTexture.getWidth() / paramBasicTexture.getTextureWidth(), paramBasicTexture.getHeight() / paramBasicTexture.getTextureHeight());
      break label61:
      float[] arrayOfFloat = mapPoints(this.mMatrixValues, paramInt1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2);
      i = (int)(0.5F + arrayOfFloat[0]);
      j = (int)(0.5F + arrayOfFloat[1]);
      k = (int)(0.5F + arrayOfFloat[2]) - i;
      l = (int)(0.5F + arrayOfFloat[3]) - j;
    }
    while ((k <= 0) || (l <= 0));
    ((GL11Ext)this.mGL).glDrawTexiOES(i, j, 0, k, l);
    this.mCountTextureOES = (1 + this.mCountTextureOES);
  }

  private void drawTexture(BasicTexture paramBasicTexture, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
  {
    if ((paramInt3 <= 0) || (paramInt4 <= 0))
      return;
    GLState localGLState = this.mGLState;
    if ((this.mBlendEnabled) && (((!paramBasicTexture.isOpaque()) || (paramFloat < 0.95F))));
    for (boolean bool = true; ; bool = false)
    {
      localGLState.setBlendEnabled(bool);
      if (bindTexture(paramBasicTexture));
      this.mGLState.setTextureAlpha(paramFloat);
      drawBoundTexture(paramBasicTexture, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
  }

  private void freeRestoreConfig(ConfigState paramConfigState)
  {
    paramConfigState.mNextFree = this.mRecycledRestoreAction;
    this.mRecycledRestoreAction = paramConfigState;
  }

  private void initialize()
  {
    GL11 localGL11 = this.mGL;
    FloatBuffer localFloatBuffer = allocateDirectNativeOrderBuffer(32 * BOX_COORDINATES.length / 8).asFloatBuffer();
    localFloatBuffer.put(BOX_COORDINATES, 0, BOX_COORDINATES.length).position(0);
    int[] arrayOfInt = new int[1];
    GLId.glGenBuffers(1, arrayOfInt, 0);
    this.mBoxCoords = arrayOfInt[0];
    localGL11.glBindBuffer(34962, this.mBoxCoords);
    localGL11.glBufferData(34962, 4 * localFloatBuffer.capacity(), localFloatBuffer, 35044);
    localGL11.glVertexPointer(2, 5126, 0, 0);
    localGL11.glTexCoordPointer(2, 5126, 0, 0);
    localGL11.glClientActiveTexture(33985);
    localGL11.glTexCoordPointer(2, 5126, 0, 0);
    localGL11.glClientActiveTexture(33984);
    localGL11.glEnableClientState(32888);
  }

  private static boolean isMatrixRotatedOrFlipped(float[] paramArrayOfFloat)
  {
    if ((Math.abs(paramArrayOfFloat[4]) <= 1.0E-005F) && (Math.abs(paramArrayOfFloat[1]) <= 1.0E-005F) && (paramArrayOfFloat[0] >= -1.0E-005F))
    {
      boolean bool = paramArrayOfFloat[5] < 1.0E-005F;
      i = 0;
      if (!bool)
        break label52;
    }
    int i = 1;
    label52: return i;
  }

  private float[] mapPoints(float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float[] arrayOfFloat = this.mMapPointsBuffer;
    float f1 = paramArrayOfFloat[0] * paramInt1 + paramArrayOfFloat[4] * paramInt2 + paramArrayOfFloat[12];
    float f2 = paramArrayOfFloat[1] * paramInt1 + paramArrayOfFloat[5] * paramInt2 + paramArrayOfFloat[13];
    float f3 = paramArrayOfFloat[3] * paramInt1 + paramArrayOfFloat[7] * paramInt2 + paramArrayOfFloat[15];
    arrayOfFloat[0] = (f1 / f3);
    arrayOfFloat[1] = (f2 / f3);
    float f4 = paramArrayOfFloat[0] * paramInt3 + paramArrayOfFloat[4] * paramInt4 + paramArrayOfFloat[12];
    float f5 = paramArrayOfFloat[1] * paramInt3 + paramArrayOfFloat[5] * paramInt4 + paramArrayOfFloat[13];
    float f6 = paramArrayOfFloat[3] * paramInt3 + paramArrayOfFloat[7] * paramInt4 + paramArrayOfFloat[15];
    arrayOfFloat[2] = (f4 / f6);
    arrayOfFloat[3] = (f5 / f6);
    return arrayOfFloat;
  }

  private ConfigState obtainRestoreConfig()
  {
    if (this.mRecycledRestoreAction != null)
    {
      ConfigState localConfigState = this.mRecycledRestoreAction;
      this.mRecycledRestoreAction = localConfigState.mNextFree;
      return localConfigState;
    }
    return new ConfigState(null);
  }

  private void restoreTransform()
  {
    System.arraycopy(this.mTempMatrix, 0, this.mMatrixValues, 0, 16);
  }

  private void saveTransform()
  {
    System.arraycopy(this.mMatrixValues, 0, this.mTempMatrix, 0, 16);
  }

  private void setMixedColor(int paramInt, float paramFloat1, float paramFloat2)
  {
    float f1 = paramFloat2 * (1.0F - paramFloat1);
    float f2 = paramFloat2 * paramFloat1 / (1.0F - f1) * (paramInt >>> 24) / 65025.0F;
    setTextureColor(f2 * (0xFF & paramInt >>> 16), f2 * (0xFF & paramInt >>> 8), f2 * (paramInt & 0xFF), f1);
    GL11 localGL11 = this.mGL;
    localGL11.glTexEnvfv(8960, 8705, this.mTextureColor, 0);
    localGL11.glTexEnvf(8960, 34161, 34165.0F);
    localGL11.glTexEnvf(8960, 34162, 34165.0F);
    localGL11.glTexEnvf(8960, 34177, 34166.0F);
    localGL11.glTexEnvf(8960, 34193, 768.0F);
    localGL11.glTexEnvf(8960, 34185, 34166.0F);
    localGL11.glTexEnvf(8960, 34201, 770.0F);
    localGL11.glTexEnvf(8960, 34178, 34166.0F);
    localGL11.glTexEnvf(8960, 34194, 770.0F);
    localGL11.glTexEnvf(8960, 34186, 34166.0F);
    localGL11.glTexEnvf(8960, 34202, 770.0F);
  }

  private void setRenderTarget(RawTexture paramRawTexture)
  {
    GL11ExtensionPack localGL11ExtensionPack = (GL11ExtensionPack)this.mGL;
    if ((this.mTargetTexture == null) && (paramRawTexture != null))
    {
      GLId.glGenBuffers(1, this.mFrameBuffer, 0);
      localGL11ExtensionPack.glBindFramebufferOES(36160, this.mFrameBuffer[0]);
    }
    if ((this.mTargetTexture != null) && (paramRawTexture == null))
    {
      localGL11ExtensionPack.glBindFramebufferOES(36160, 0);
      localGL11ExtensionPack.glDeleteFramebuffersOES(1, this.mFrameBuffer, 0);
    }
    this.mTargetTexture = paramRawTexture;
    if (paramRawTexture == null)
    {
      setSize(this.mScreenWidth, this.mScreenHeight);
      return;
    }
    setSize(paramRawTexture.getWidth(), paramRawTexture.getHeight());
    if (!paramRawTexture.isLoaded())
      paramRawTexture.prepare(this);
    localGL11ExtensionPack.glFramebufferTexture2DOES(36160, 36064, 3553, paramRawTexture.getId(), 0);
    checkFramebufferStatus(localGL11ExtensionPack);
  }

  private void setTextureColor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float[] arrayOfFloat = this.mTextureColor;
    arrayOfFloat[0] = paramFloat1;
    arrayOfFloat[1] = paramFloat2;
    arrayOfFloat[2] = paramFloat3;
    arrayOfFloat[3] = paramFloat4;
  }

  private void setTextureCoords(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.mGL.glMatrixMode(5890);
    this.mTextureMatrixValues[0] = (paramFloat3 - paramFloat1);
    this.mTextureMatrixValues[5] = (paramFloat4 - paramFloat2);
    this.mTextureMatrixValues[10] = 1.0F;
    this.mTextureMatrixValues[12] = paramFloat1;
    this.mTextureMatrixValues[13] = paramFloat2;
    this.mTextureMatrixValues[15] = 1.0F;
    this.mGL.glLoadMatrixf(this.mTextureMatrixValues, 0);
    this.mGL.glMatrixMode(5888);
  }

  private void setTextureCoords(RectF paramRectF)
  {
    setTextureCoords(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom);
  }

  private void setTextureCoords(float[] paramArrayOfFloat)
  {
    this.mGL.glMatrixMode(5890);
    this.mGL.glLoadMatrixf(paramArrayOfFloat, 0);
    this.mGL.glMatrixMode(5888);
  }

  private void textureRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    GL11 localGL11 = this.mGL;
    saveTransform();
    translate(paramFloat1, paramFloat2);
    scale(paramFloat3, paramFloat4, 1.0F);
    localGL11.glLoadMatrixf(this.mMatrixValues, 0);
    localGL11.glDrawArrays(5, 0, 4);
    restoreTransform();
    this.mCountTextureRect = (1 + this.mCountTextureRect);
  }

  public void beginRenderTarget(RawTexture paramRawTexture)
  {
    save();
    this.mTargetStack.add(this.mTargetTexture);
    setRenderTarget(paramRawTexture);
  }

  public void clearBuffer()
  {
    clearBuffer(null);
  }

  public void clearBuffer(float[] paramArrayOfFloat)
  {
    if ((paramArrayOfFloat != null) && (paramArrayOfFloat.length == 4))
      this.mGL.glClearColor(paramArrayOfFloat[1], paramArrayOfFloat[2], paramArrayOfFloat[3], paramArrayOfFloat[0]);
    while (true)
    {
      this.mGL.glClear(16384);
      return;
      this.mGL.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
    }
  }

  public void deleteBuffer(int paramInt)
  {
    synchronized (this.mUnboundTextures)
    {
      this.mDeleteBuffers.add(paramInt);
      return;
    }
  }

  public void deleteRecycledResources()
  {
    synchronized (this.mUnboundTextures)
    {
      IntArray localIntArray2 = this.mUnboundTextures;
      if (localIntArray2.size() > 0)
      {
        GLId.glDeleteTextures(this.mGL, localIntArray2.size(), localIntArray2.getInternalArray(), 0);
        localIntArray2.clear();
      }
      IntArray localIntArray3 = this.mDeleteBuffers;
      if (localIntArray3.size() > 0)
      {
        GLId.glDeleteBuffers(this.mGL, localIntArray3.size(), localIntArray3.getInternalArray(), 0);
        localIntArray3.clear();
      }
      return;
    }
  }

  public void drawMesh(BasicTexture paramBasicTexture, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    float f = this.mAlpha;
    if (!bindTexture(paramBasicTexture))
      return;
    GLState localGLState = this.mGLState;
    if ((this.mBlendEnabled) && (((!paramBasicTexture.isOpaque()) || (f < 0.95F))));
    for (boolean bool = true; ; bool = false)
    {
      localGLState.setBlendEnabled(bool);
      this.mGLState.setTextureAlpha(f);
      setTextureCoords(0.0F, 0.0F, 1.0F, 1.0F);
      saveTransform();
      translate(paramInt1, paramInt2);
      this.mGL.glLoadMatrixf(this.mMatrixValues, 0);
      this.mGL.glBindBuffer(34962, paramInt3);
      this.mGL.glVertexPointer(2, 5126, 0, 0);
      this.mGL.glBindBuffer(34962, paramInt4);
      this.mGL.glTexCoordPointer(2, 5126, 0, 0);
      this.mGL.glBindBuffer(34963, paramInt5);
      this.mGL.glDrawElements(5, paramInt6, 5121, 0);
      this.mGL.glBindBuffer(34962, this.mBoxCoords);
      this.mGL.glVertexPointer(2, 5126, 0, 0);
      this.mGL.glTexCoordPointer(2, 5126, 0, 0);
      restoreTransform();
      this.mCountDrawMesh = (1 + this.mCountDrawMesh);
      return;
    }
  }

  public void drawMixed(BasicTexture paramBasicTexture, int paramInt, float paramFloat, RectF paramRectF1, RectF paramRectF2)
  {
    if ((paramRectF2.width() <= 0.0F) || (paramRectF2.height() <= 0.0F))
      return;
    if (paramFloat <= 0.01F)
    {
      drawTexture(paramBasicTexture, paramRectF1, paramRectF2);
      return;
    }
    if (paramFloat >= 1.0F)
    {
      fillRect(paramRectF2.left, paramRectF2.top, paramRectF2.width(), paramRectF2.height(), paramInt);
      return;
    }
    float f = this.mAlpha;
    this.mDrawTextureSourceRect.set(paramRectF1);
    this.mDrawTextureTargetRect.set(paramRectF2);
    RectF localRectF1 = this.mDrawTextureSourceRect;
    RectF localRectF2 = this.mDrawTextureTargetRect;
    GLState localGLState = this.mGLState;
    if ((this.mBlendEnabled) && (((!paramBasicTexture.isOpaque()) || (!Utils.isOpaque(paramInt)) || (f < 0.95F))));
    for (boolean bool = true; ; bool = false)
    {
      localGLState.setBlendEnabled(bool);
      if (bindTexture(paramBasicTexture));
      this.mGLState.setTexEnvMode(34160);
      setMixedColor(paramInt, paramFloat, f);
      convertCoordinate(localRectF1, localRectF2, paramBasicTexture);
      setTextureCoords(localRectF1);
      textureRect(localRectF2.left, localRectF2.top, localRectF2.width(), localRectF2.height());
      this.mGLState.setTexEnvMode(7681);
      return;
    }
  }

  public void drawRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, GLPaint paramGLPaint)
  {
    GL11 localGL11 = this.mGL;
    this.mGLState.setColorMode(paramGLPaint.getColor(), this.mAlpha);
    this.mGLState.setLineWidth(paramGLPaint.getLineWidth());
    saveTransform();
    translate(paramFloat1, paramFloat2);
    scale(paramFloat3, paramFloat4, 1.0F);
    localGL11.glLoadMatrixf(this.mMatrixValues, 0);
    localGL11.glDrawArrays(2, 6, 4);
    restoreTransform();
    this.mCountDrawLine = (1 + this.mCountDrawLine);
  }

  public void drawTexture(BasicTexture paramBasicTexture, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    drawTexture(paramBasicTexture, paramInt1, paramInt2, paramInt3, paramInt4, this.mAlpha);
  }

  public void drawTexture(BasicTexture paramBasicTexture, RectF paramRectF1, RectF paramRectF2)
  {
    if ((paramRectF2.width() <= 0.0F) || (paramRectF2.height() <= 0.0F))
      return;
    this.mDrawTextureSourceRect.set(paramRectF1);
    this.mDrawTextureTargetRect.set(paramRectF2);
    RectF localRectF1 = this.mDrawTextureSourceRect;
    RectF localRectF2 = this.mDrawTextureTargetRect;
    GLState localGLState = this.mGLState;
    if ((this.mBlendEnabled) && (((!paramBasicTexture.isOpaque()) || (this.mAlpha < 0.95F))));
    for (boolean bool = true; ; bool = false)
    {
      localGLState.setBlendEnabled(bool);
      if (bindTexture(paramBasicTexture));
      convertCoordinate(localRectF1, localRectF2, paramBasicTexture);
      setTextureCoords(localRectF1);
      this.mGLState.setTextureAlpha(this.mAlpha);
      textureRect(localRectF2.left, localRectF2.top, localRectF2.width(), localRectF2.height());
      return;
    }
  }

  public void drawTexture(BasicTexture paramBasicTexture, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    GLState localGLState = this.mGLState;
    if ((this.mBlendEnabled) && (((!paramBasicTexture.isOpaque()) || (this.mAlpha < 0.95F))));
    for (boolean bool = true; ; bool = false)
    {
      localGLState.setBlendEnabled(bool);
      if (bindTexture(paramBasicTexture))
        break;
      return;
    }
    setTextureCoords(paramArrayOfFloat);
    this.mGLState.setTextureAlpha(this.mAlpha);
    textureRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void endRenderTarget()
  {
    setRenderTarget((RawTexture)this.mTargetStack.remove(-1 + this.mTargetStack.size()));
    restore();
  }

  public void fillRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt)
  {
    this.mGLState.setColorMode(paramInt, this.mAlpha);
    GL11 localGL11 = this.mGL;
    saveTransform();
    translate(paramFloat1, paramFloat2);
    scale(paramFloat3, paramFloat4, 1.0F);
    localGL11.glLoadMatrixf(this.mMatrixValues, 0);
    localGL11.glDrawArrays(5, 0, 4);
    restoreTransform();
    this.mCountFillRect = (1 + this.mCountFillRect);
  }

  public float getAlpha()
  {
    return this.mAlpha;
  }

  public GL11 getGLInstance()
  {
    return this.mGL;
  }

  public void multiplyAlpha(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      this.mAlpha = (paramFloat * this.mAlpha);
      return;
    }
  }

  public void multiplyMatrix(float[] paramArrayOfFloat, int paramInt)
  {
    float[] arrayOfFloat = this.mTempMatrix;
    Matrix.multiplyMM(arrayOfFloat, 0, this.mMatrixValues, 0, paramArrayOfFloat, paramInt);
    System.arraycopy(arrayOfFloat, 0, this.mMatrixValues, 0, 16);
  }

  public void restore()
  {
    if (this.mRestoreStack.isEmpty())
      throw new IllegalStateException();
    ConfigState localConfigState = (ConfigState)this.mRestoreStack.remove(-1 + this.mRestoreStack.size());
    localConfigState.restore(this);
    freeRestoreConfig(localConfigState);
  }

  public void rotate(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (paramFloat1 == 0.0F)
      return;
    float[] arrayOfFloat = this.mTempMatrix;
    Matrix.setRotateM(arrayOfFloat, 0, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    Matrix.multiplyMM(arrayOfFloat, 16, this.mMatrixValues, 0, arrayOfFloat, 0);
    System.arraycopy(arrayOfFloat, 16, this.mMatrixValues, 0, 16);
  }

  public void save()
  {
    save(-1);
  }

  public void save(int paramInt)
  {
    ConfigState localConfigState = obtainRestoreConfig();
    if ((paramInt & 0x1) != 0)
    {
      localConfigState.mAlpha = this.mAlpha;
      label19: if ((paramInt & 0x2) == 0)
        break label60;
      System.arraycopy(this.mMatrixValues, 0, localConfigState.mMatrix, 0, 16);
    }
    while (true)
    {
      this.mRestoreStack.add(localConfigState);
      return;
      localConfigState.mAlpha = -1.0F;
      break label19:
      label60: localConfigState.mMatrix[0] = (1.0F / -1.0F);
    }
  }

  public void scale(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    Matrix.scaleM(this.mMatrixValues, 0, paramFloat1, paramFloat2, paramFloat3);
  }

  public void setAlpha(float paramFloat)
  {
    if ((paramFloat >= 0.0F) && (paramFloat <= 1.0F));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      this.mAlpha = paramFloat;
      return;
    }
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    if ((paramInt1 >= 0) && (paramInt2 >= 0));
    for (boolean bool = true; ; bool = false)
    {
      Utils.assertTrue(bool);
      if (this.mTargetTexture == null)
      {
        this.mScreenWidth = paramInt1;
        this.mScreenHeight = paramInt2;
      }
      this.mAlpha = 1.0F;
      GL11 localGL11 = this.mGL;
      localGL11.glViewport(0, 0, paramInt1, paramInt2);
      localGL11.glMatrixMode(5889);
      localGL11.glLoadIdentity();
      GLU.gluOrtho2D(localGL11, 0.0F, paramInt1, 0.0F, paramInt2);
      localGL11.glMatrixMode(5888);
      localGL11.glLoadIdentity();
      float[] arrayOfFloat = this.mMatrixValues;
      Matrix.setIdentityM(arrayOfFloat, 0);
      if (this.mTargetTexture == null)
      {
        Matrix.translateM(arrayOfFloat, 0, 0.0F, paramInt2, 0.0F);
        Matrix.scaleM(arrayOfFloat, 0, 1.0F, -1.0F, 1.0F);
      }
      return;
    }
  }

  public void translate(float paramFloat1, float paramFloat2)
  {
    float[] arrayOfFloat = this.mMatrixValues;
    arrayOfFloat[12] += paramFloat1 * arrayOfFloat[0] + paramFloat2 * arrayOfFloat[4];
    arrayOfFloat[13] += paramFloat1 * arrayOfFloat[1] + paramFloat2 * arrayOfFloat[5];
    arrayOfFloat[14] += paramFloat1 * arrayOfFloat[2] + paramFloat2 * arrayOfFloat[6];
    arrayOfFloat[15] += paramFloat1 * arrayOfFloat[3] + paramFloat2 * arrayOfFloat[7];
  }

  public void translate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    Matrix.translateM(this.mMatrixValues, 0, paramFloat1, paramFloat2, paramFloat3);
  }

  public boolean unloadTexture(BasicTexture paramBasicTexture)
  {
    synchronized (this.mUnboundTextures)
    {
      if (!paramBasicTexture.isLoaded())
        return false;
      this.mUnboundTextures.add(paramBasicTexture.mId);
      return true;
    }
  }

  private static class ConfigState
  {
    float mAlpha;
    float[] mMatrix = new float[16];
    ConfigState mNextFree;

    public void restore(GLCanvasImpl paramGLCanvasImpl)
    {
      if (this.mAlpha >= 0.0F)
        paramGLCanvasImpl.setAlpha(this.mAlpha);
      if (this.mMatrix[0] == (1.0F / -1.0F))
        return;
      System.arraycopy(this.mMatrix, 0, paramGLCanvasImpl.mMatrixValues, 0, 16);
    }
  }

  private static class GLState
  {
    private boolean mBlendEnabled = true;
    private final GL11 mGL;
    private boolean mLineSmooth = false;
    private float mLineWidth = 1.0F;
    private int mTexEnvMode = 7681;
    private float mTextureAlpha = 1.0F;
    private int mTextureTarget = 3553;

    public GLState(GL11 paramGL11)
    {
      this.mGL = paramGL11;
      paramGL11.glDisable(2896);
      paramGL11.glEnable(3024);
      paramGL11.glEnableClientState(32884);
      paramGL11.glEnableClientState(32888);
      paramGL11.glEnable(3553);
      paramGL11.glTexEnvf(8960, 8704, 7681.0F);
      paramGL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      paramGL11.glClearStencil(0);
      paramGL11.glEnable(3042);
      paramGL11.glBlendFunc(1, 771);
      paramGL11.glPixelStorei(3317, 2);
    }

    public void setBlendEnabled(boolean paramBoolean)
    {
      if (this.mBlendEnabled == paramBoolean)
        return;
      this.mBlendEnabled = paramBoolean;
      if (paramBoolean)
      {
        this.mGL.glEnable(3042);
        return;
      }
      this.mGL.glDisable(3042);
    }

    public void setColorMode(int paramInt, float paramFloat)
    {
      if ((!Utils.isOpaque(paramInt)) || (paramFloat < 0.95F));
      for (boolean bool = true; ; bool = false)
      {
        setBlendEnabled(bool);
        this.mTextureAlpha = -1.0F;
        setTextureTarget(0);
        float f = 65535.0F * (paramFloat * (paramInt >>> 24)) / 255.0F / 255.0F;
        this.mGL.glColor4x(Math.round(f * (0xFF & paramInt >> 16)), Math.round(f * (0xFF & paramInt >> 8)), Math.round(f * (paramInt & 0xFF)), Math.round(255.0F * f));
        return;
      }
    }

    public void setLineWidth(float paramFloat)
    {
      if (this.mLineWidth == paramFloat)
        return;
      this.mLineWidth = paramFloat;
      this.mGL.glLineWidth(paramFloat);
    }

    public void setTexEnvMode(int paramInt)
    {
      if (this.mTexEnvMode == paramInt)
        return;
      this.mTexEnvMode = paramInt;
      this.mGL.glTexEnvf(8960, 8704, paramInt);
    }

    public void setTextureAlpha(float paramFloat)
    {
      if (this.mTextureAlpha == paramFloat)
        return;
      this.mTextureAlpha = paramFloat;
      if (paramFloat >= 0.95F)
      {
        this.mGL.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        setTexEnvMode(7681);
        return;
      }
      this.mGL.glColor4f(paramFloat, paramFloat, paramFloat, paramFloat);
      setTexEnvMode(8448);
    }

    public void setTextureTarget(int paramInt)
    {
      if (this.mTextureTarget == paramInt);
      do
      {
        return;
        if (this.mTextureTarget != 0)
          this.mGL.glDisable(this.mTextureTarget);
        this.mTextureTarget = paramInt;
      }
      while (this.mTextureTarget == 0);
      this.mGL.glEnable(this.mTextureTarget);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GLCanvasImpl
 * JD-Core Version:    0.5.4
 */