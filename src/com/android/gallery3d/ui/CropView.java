package com.android.gallery3d.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;
import com.android.gallery3d.anim.Animation;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.Utils;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL11;

public class CropView extends GLView
{
  private AbstractGalleryActivity mActivity;
  private AnimationController mAnimation = new AnimationController();
  private float mAspectRatio = -1.0F;
  private FaceHighlightView mFaceDetectionView;
  private GLPaint mFacePaint = new GLPaint();
  private HighlightRectangle mHighlightRectangle;
  private int mImageHeight = -1;
  private int mImageRotation;
  private TileImageView mImageView;
  private int mImageWidth = -1;
  private Handler mMainHandler;
  private GLPaint mPaint = new GLPaint();
  private float mSpotlightRatioX = 0.0F;
  private float mSpotlightRatioY = 0.0F;

  public CropView(AbstractGalleryActivity paramAbstractGalleryActivity)
  {
    this.mActivity = paramAbstractGalleryActivity;
    this.mImageView = new TileImageView(paramAbstractGalleryActivity);
    this.mFaceDetectionView = new FaceHighlightView(null);
    this.mHighlightRectangle = new HighlightRectangle();
    addComponent(this.mImageView);
    addComponent(this.mFaceDetectionView);
    addComponent(this.mHighlightRectangle);
    this.mHighlightRectangle.setVisibility(1);
    this.mPaint.setColor(-16741633);
    this.mPaint.setLineWidth(3.0F);
    this.mFacePaint.setColor(-16777216);
    this.mFacePaint.setLineWidth(3.0F);
    this.mMainHandler = new SynchronizedHandler(paramAbstractGalleryActivity.getGLRoot())
    {
      public void handleMessage(Message paramMessage)
      {
        int i = 1;
        if (paramMessage.what == i);
        while (true)
        {
          Utils.assertTrue(i);
          ((CropView.DetectFaceTask)paramMessage.obj).updateFaces();
          return;
          int j = 0;
        }
      }
    };
  }

  private boolean setImageViewPosition(int paramInt1, int paramInt2, float paramFloat)
  {
    int i = this.mImageWidth - paramInt1;
    int j = this.mImageHeight - paramInt2;
    TileImageView localTileImageView = this.mImageView;
    int k = this.mImageRotation;
    switch (k)
    {
    default:
      throw new IllegalArgumentException(String.valueOf(k));
    case 0:
      return localTileImageView.setPosition(paramInt1, paramInt2, paramFloat, 0);
    case 90:
      return localTileImageView.setPosition(paramInt2, i, paramFloat, 90);
    case 180:
      return localTileImageView.setPosition(i, j, paramFloat, 180);
    case 270:
    }
    return localTileImageView.setPosition(j, paramInt1, paramFloat, 270);
  }

  public void detectFaces(Bitmap paramBitmap)
  {
    int i = this.mImageRotation;
    int j = paramBitmap.getWidth();
    int k = paramBitmap.getHeight();
    float f = FloatMath.sqrt(120000.0F / (j * k));
    Bitmap localBitmap;
    if ((0x1 & i / 90) == 0)
    {
      int i2 = 0xFFFFFFFE & Math.round(f * j);
      int i3 = Math.round(f * k);
      localBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.RGB_565);
      Canvas localCanvas2 = new Canvas(localBitmap);
      localCanvas2.rotate(i, i2 / 2, i3 / 2);
      localCanvas2.scale(i2 / j, i3 / k);
      localCanvas2.drawBitmap(paramBitmap, 0.0F, 0.0F, new Paint(2));
    }
    while (true)
    {
      new DetectFaceTask(localBitmap).start();
      return;
      int l = 0xFFFFFFFE & Math.round(f * k);
      int i1 = Math.round(f * j);
      localBitmap = Bitmap.createBitmap(l, i1, Bitmap.Config.RGB_565);
      Canvas localCanvas1 = new Canvas(localBitmap);
      localCanvas1.translate(l / 2, i1 / 2);
      localCanvas1.rotate(i);
      localCanvas1.translate(-i1 / 2, -l / 2);
      localCanvas1.scale(l / k, i1 / j);
      localCanvas1.drawBitmap(paramBitmap, 0.0F, 0.0F, new Paint(2));
    }
  }

  public RectF getCropRectangle()
  {
    if (this.mHighlightRectangle.getVisibility() == 1)
      return null;
    RectF localRectF = this.mHighlightRectangle.mHighlightRect;
    return new RectF(localRectF.left * this.mImageWidth, localRectF.top * this.mImageHeight, localRectF.right * this.mImageWidth, localRectF.bottom * this.mImageHeight);
  }

  public int getImageHeight()
  {
    return this.mImageHeight;
  }

  public int getImageWidth()
  {
    return this.mImageWidth;
  }

  public void initializeHighlightRectangle()
  {
    this.mHighlightRectangle.setInitRectangle();
    this.mHighlightRectangle.setVisibility(0);
  }

  public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    this.mFaceDetectionView.layout(0, 0, i, j);
    this.mHighlightRectangle.layout(0, 0, i, j);
    this.mImageView.layout(0, 0, i, j);
    if (this.mImageHeight == -1)
      return;
    this.mAnimation.initialize();
    if (this.mHighlightRectangle.getVisibility() != 0)
      return;
    this.mAnimation.parkNow(this.mHighlightRectangle.mHighlightRect);
  }

  public void pause()
  {
    this.mImageView.freeTextures();
  }

  public void render(GLCanvas paramGLCanvas)
  {
    AnimationController localAnimationController = this.mAnimation;
    if (localAnimationController.calculate(AnimationTime.get()))
      invalidate();
    setImageViewPosition(localAnimationController.getCenterX(), localAnimationController.getCenterY(), localAnimationController.getScale());
    super.render(paramGLCanvas);
  }

  public void renderBackground(GLCanvas paramGLCanvas)
  {
    paramGLCanvas.clearBuffer();
  }

  public void resume()
  {
    this.mImageView.prepareTextures();
  }

  public void setAspectRatio(float paramFloat)
  {
    this.mAspectRatio = paramFloat;
  }

  public void setDataModel(TileImageView.Model paramModel, int paramInt)
  {
    if ((0x1 & paramInt / 90) != 0)
      this.mImageWidth = paramModel.getImageHeight();
    for (this.mImageHeight = paramModel.getImageWidth(); ; this.mImageHeight = paramModel.getImageHeight())
    {
      this.mImageRotation = paramInt;
      this.mImageView.setModel(paramModel);
      this.mAnimation.initialize();
      return;
      this.mImageWidth = paramModel.getImageWidth();
    }
  }

  public void setSpotlightRatio(float paramFloat1, float paramFloat2)
  {
    this.mSpotlightRatioX = paramFloat1;
    this.mSpotlightRatioY = paramFloat2;
  }

  private class AnimationController extends Animation
  {
    private float mCurrentScale;
    private int mCurrentX;
    private int mCurrentY;
    private float mStartScale;
    private int mStartX;
    private int mStartY;
    private float mTargetScale;
    private int mTargetX;
    private int mTargetY;

    public AnimationController()
    {
      setDuration(1250);
      setInterpolator(new DecelerateInterpolator(4.0F));
    }

    private void calculateTarget(RectF paramRectF)
    {
      float f1 = CropView.this.getWidth();
      float f2 = CropView.this.getHeight();
      float f4;
      int i;
      label220: int k;
      if (CropView.this.mImageWidth != -1)
      {
        float f3 = Math.min(f1 / CropView.this.mImageWidth, f2 / CropView.this.mImageHeight);
        f4 = Utils.clamp(0.6F * Math.min(f1 / (paramRectF.width() * CropView.this.mImageWidth), f2 / (paramRectF.height() * CropView.this.mImageHeight)), f3, 2.0F);
        Math.round(0.5F * (CropView.this.mImageWidth * (paramRectF.left + paramRectF.right)));
        Math.round(0.5F * (CropView.this.mImageHeight * (paramRectF.top + paramRectF.bottom)));
        if (Math.round(f4 * CropView.this.mImageWidth) <= f1)
          break label311;
        int l = Math.round(f1 * 0.5F / f4);
        i = Utils.clamp(Math.round((paramRectF.left + paramRectF.right) * CropView.this.mImageWidth / 2.0F), l, CropView.this.mImageWidth - l);
        if (Math.round(f4 * CropView.this.mImageHeight) <= f2)
          break label325;
        k = Math.round(f2 * 0.5F / f4);
      }
      for (int j = Utils.clamp(Math.round((paramRectF.top + paramRectF.bottom) * CropView.this.mImageHeight / 2.0F), k, CropView.this.mImageHeight - k); ; j = CropView.this.mImageHeight / 2)
      {
        this.mTargetX = i;
        this.mTargetY = j;
        this.mTargetScale = f4;
        return;
        label311: i = CropView.this.mImageWidth / 2;
        label325: break label220:
      }
    }

    public int getCenterX()
    {
      return this.mCurrentX;
    }

    public int getCenterY()
    {
      return this.mCurrentY;
    }

    public float getScale()
    {
      return this.mCurrentScale;
    }

    public void initialize()
    {
      this.mCurrentX = (CropView.this.mImageWidth / 2);
      this.mCurrentY = (CropView.this.mImageHeight / 2);
      this.mCurrentScale = Math.min(2.0F, Math.min(CropView.this.getWidth() / CropView.this.mImageWidth, CropView.this.getHeight() / CropView.this.mImageHeight));
    }

    public void inverseMapPoint(PointF paramPointF)
    {
      float f = this.mCurrentScale;
      paramPointF.x = Utils.clamp(((paramPointF.x - 0.5F * CropView.this.getWidth()) / f + this.mCurrentX) / CropView.this.mImageWidth, 0.0F, 1.0F);
      paramPointF.y = Utils.clamp(((paramPointF.y - 0.5F * CropView.this.getHeight()) / f + this.mCurrentY) / CropView.this.mImageHeight, 0.0F, 1.0F);
    }

    public RectF mapRect(RectF paramRectF1, RectF paramRectF2)
    {
      float f1 = 0.5F * CropView.this.getWidth();
      float f2 = 0.5F * CropView.this.getHeight();
      int i = this.mCurrentX;
      int j = this.mCurrentY;
      float f3 = this.mCurrentScale;
      paramRectF2.set(f1 + f3 * (paramRectF1.left * CropView.this.mImageWidth - i), f2 + f3 * (paramRectF1.top * CropView.this.mImageHeight - j), f1 + f3 * (paramRectF1.right * CropView.this.mImageWidth - i), f2 + f3 * (paramRectF1.bottom * CropView.this.mImageHeight - j));
      return paramRectF2;
    }

    protected void onCalculate(float paramFloat)
    {
      this.mCurrentX = Math.round(this.mStartX + paramFloat * (this.mTargetX - this.mStartX));
      this.mCurrentY = Math.round(this.mStartY + paramFloat * (this.mTargetY - this.mStartY));
      this.mCurrentScale = (this.mStartScale + paramFloat * (this.mTargetScale - this.mStartScale));
      if ((this.mCurrentX != this.mTargetX) || (this.mCurrentY != this.mTargetY) || (this.mCurrentScale != this.mTargetScale))
        return;
      forceStop();
    }

    public void parkNow(RectF paramRectF)
    {
      calculateTarget(paramRectF);
      forceStop();
      int i = this.mTargetX;
      this.mCurrentX = i;
      this.mStartX = i;
      int j = this.mTargetY;
      this.mCurrentY = j;
      this.mStartY = j;
      float f = this.mTargetScale;
      this.mCurrentScale = f;
      this.mStartScale = f;
    }

    public void startParkingAnimation(RectF paramRectF)
    {
      RectF localRectF = CropView.this.mAnimation.mapRect(paramRectF, new RectF());
      int i = CropView.this.getWidth();
      int j = CropView.this.getHeight();
      float f1 = localRectF.width() / i;
      float f2 = localRectF.height() / j;
      if ((f1 >= 0.4F) && (f1 < 0.8F) && (f2 >= 0.4F) && (f2 < 0.8F) && (localRectF.left >= 64.0F) && (localRectF.right < i - 64) && (localRectF.top >= 64.0F) && (localRectF.bottom < j - 64))
        return;
      this.mStartX = this.mCurrentX;
      this.mStartY = this.mCurrentY;
      this.mStartScale = this.mCurrentScale;
      calculateTarget(paramRectF);
      start();
    }
  }

  private class DetectFaceTask extends Thread
  {
    private final Bitmap mFaceBitmap;
    private int mFaceCount;
    private final FaceDetector.Face[] mFaces = new FaceDetector.Face[3];

    public DetectFaceTask(Bitmap arg2)
    {
      Object localObject;
      this.mFaceBitmap = localObject;
      setName("face-detect");
    }

    private RectF getFaceRect(FaceDetector.Face paramFace)
    {
      PointF localPointF = new PointF();
      paramFace.getMidPoint(localPointF);
      int i = this.mFaceBitmap.getWidth();
      int j = this.mFaceBitmap.getHeight();
      float f1 = 2.0F * paramFace.eyesDistance();
      float f2 = f1;
      float f3 = CropView.this.mAspectRatio;
      if (f3 != -1.0F)
      {
        if (f3 <= 1.0F)
          break label250;
        f1 = f2 * f3;
      }
      label73: RectF localRectF = new RectF(localPointF.x - f1, localPointF.y - f2, f1 + localPointF.x, f2 + localPointF.y);
      localRectF.intersect(0.0F, 0.0F, i, j);
      if (f3 != -1.0F)
      {
        if (localRectF.width() / localRectF.height() <= f3)
          break label260;
        float f5 = f3 * localRectF.height();
        localRectF.left = (0.5F * (localRectF.left + localRectF.right - f5));
        localRectF.right = (f5 + localRectF.left);
      }
      while (true)
      {
        localRectF.left /= i;
        localRectF.right /= i;
        localRectF.top /= j;
        localRectF.bottom /= j;
        return localRectF;
        label250: f2 = f1 / f3;
        break label73:
        label260: float f4 = localRectF.width() / f3;
        localRectF.top = (0.5F * (localRectF.top + localRectF.bottom - f4));
        localRectF.bottom = (f4 + localRectF.top);
      }
    }

    public void run()
    {
      Bitmap localBitmap = this.mFaceBitmap;
      this.mFaceCount = new FaceDetector(localBitmap.getWidth(), localBitmap.getHeight(), 3).findFaces(localBitmap, this.mFaces);
      CropView.this.mMainHandler.sendMessage(CropView.this.mMainHandler.obtainMessage(1, this));
    }

    public void updateFaces()
    {
      if (this.mFaceCount > 1)
      {
        int i = 0;
        int j = this.mFaceCount;
        while (i < j)
        {
          CropView.this.mFaceDetectionView.addFace(getFaceRect(this.mFaces[i]));
          ++i;
        }
        CropView.this.mFaceDetectionView.setVisibility(0);
        Toast.makeText(CropView.this.mActivity.getAndroidContext(), 2131362195, 0).show();
        return;
      }
      if (this.mFaceCount == 1)
      {
        CropView.this.mFaceDetectionView.setVisibility(1);
        CropView.this.mHighlightRectangle.setRectangle(getFaceRect(this.mFaces[0]));
        CropView.this.mHighlightRectangle.setVisibility(0);
        return;
      }
      CropView.this.mHighlightRectangle.setInitRectangle();
      CropView.this.mHighlightRectangle.setVisibility(0);
    }
  }

  private class FaceHighlightView extends GLView
  {
    private ArrayList<RectF> mFaces = new ArrayList();
    private int mPressedFaceIndex = -1;
    private RectF mRect = new RectF();

    private FaceHighlightView()
    {
    }

    private int getFaceIndexByPosition(float paramFloat1, float paramFloat2)
    {
      ArrayList localArrayList = this.mFaces;
      int i = 0;
      int j = localArrayList.size();
      while (i < j)
      {
        if (CropView.this.mAnimation.mapRect((RectF)localArrayList.get(i), this.mRect).contains(paramFloat1, paramFloat2))
          return i;
        ++i;
      }
      return -1;
    }

    private void renderFace(GLCanvas paramGLCanvas, RectF paramRectF, boolean paramBoolean)
    {
      GL11 localGL11 = paramGLCanvas.getGLInstance();
      if (paramBoolean)
      {
        localGL11.glEnable(2960);
        localGL11.glClear(1024);
        localGL11.glStencilOp(7680, 7680, 7681);
        localGL11.glStencilFunc(519, 1, 1);
      }
      RectF localRectF = CropView.this.mAnimation.mapRect(paramRectF, this.mRect);
      paramGLCanvas.fillRect(localRectF.left, localRectF.top, localRectF.width(), localRectF.height(), 0);
      paramGLCanvas.drawRect(localRectF.left, localRectF.top, localRectF.width(), localRectF.height(), CropView.this.mFacePaint);
      if (!paramBoolean)
        return;
      localGL11.glStencilOp(7680, 7680, 7680);
    }

    private void setPressedFace(int paramInt)
    {
      if (this.mPressedFaceIndex == paramInt)
        return;
      this.mPressedFaceIndex = paramInt;
      invalidate();
    }

    public void addFace(RectF paramRectF)
    {
      this.mFaces.add(paramRectF);
      invalidate();
    }

    protected boolean onTouch(MotionEvent paramMotionEvent)
    {
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      switch (paramMotionEvent.getAction())
      {
      default:
      case 0:
      case 2:
      case 1:
      case 3:
      }
      int i;
      do
      {
        return true;
        setPressedFace(getFaceIndexByPosition(f1, f2));
        return true;
        i = this.mPressedFaceIndex;
        setPressedFace(-1);
      }
      while (i == -1);
      CropView.this.mHighlightRectangle.setRectangle((RectF)this.mFaces.get(i));
      CropView.this.mHighlightRectangle.setVisibility(0);
      setVisibility(1);
      return true;
    }

    protected void renderBackground(GLCanvas paramGLCanvas)
    {
      ArrayList localArrayList = this.mFaces;
      int i = 0;
      int j = localArrayList.size();
      if (i < j)
      {
        label13: RectF localRectF = (RectF)localArrayList.get(i);
        if (i == this.mPressedFaceIndex);
        for (boolean bool = true; ; bool = false)
        {
          renderFace(paramGLCanvas, localRectF, bool);
          ++i;
          break label13:
        }
      }
      GL11 localGL11 = paramGLCanvas.getGLInstance();
      if (this.mPressedFaceIndex == -1)
        return;
      localGL11.glStencilFunc(517, 1, 1);
      paramGLCanvas.fillRect(0.0F, 0.0F, getWidth(), getHeight(), 1711276032);
      localGL11.glDisable(2960);
    }
  }

  private class HighlightRectangle extends GLView
  {
    private ResourceTexture mArrow = new ResourceTexture(CropView.this.mActivity.getAndroidContext(), 2130837568);
    private RectF mHighlightRect = new RectF(0.25F, 0.25F, 0.75F, 0.75F);
    private int mMovingEdges = 0;
    private float mReferenceX;
    private float mReferenceY;
    private PointF mTempPoint = new PointF();
    private RectF mTempRect = new RectF();

    public HighlightRectangle()
    {
    }

    private void drawHighlightRectangle(GLCanvas paramGLCanvas, RectF paramRectF)
    {
      GL11 localGL11 = paramGLCanvas.getGLInstance();
      localGL11.glLineWidth(3.0F);
      localGL11.glEnable(2848);
      localGL11.glEnable(2960);
      localGL11.glClear(1024);
      localGL11.glStencilOp(7680, 7680, 7681);
      localGL11.glStencilFunc(519, 1, 1);
      if ((CropView.this.mSpotlightRatioX == 0.0F) || (CropView.this.mSpotlightRatioY == 0.0F))
      {
        paramGLCanvas.fillRect(paramRectF.left, paramRectF.top, paramRectF.width(), paramRectF.height(), 0);
        paramGLCanvas.drawRect(paramRectF.left, paramRectF.top, paramRectF.width(), paramRectF.height(), CropView.this.mPaint);
      }
      while (true)
      {
        localGL11.glStencilFunc(517, 1, 1);
        localGL11.glStencilOp(7680, 7680, 7680);
        paramGLCanvas.fillRect(0.0F, 0.0F, getWidth(), getHeight(), -1610612736);
        localGL11.glDisable(2960);
        return;
        float f1 = paramRectF.width() * CropView.this.mSpotlightRatioX;
        float f2 = paramRectF.height() * CropView.this.mSpotlightRatioY;
        float f3 = paramRectF.centerX();
        float f4 = paramRectF.centerY();
        paramGLCanvas.fillRect(f3 - f1 / 2.0F, f4 - f2 / 2.0F, f1, f2, 0);
        paramGLCanvas.drawRect(f3 - f1 / 2.0F, f4 - f2 / 2.0F, f1, f2, CropView.this.mPaint);
        paramGLCanvas.drawRect(paramRectF.left, paramRectF.top, paramRectF.width(), paramRectF.height(), CropView.this.mPaint);
        localGL11.glStencilFunc(517, 1, 1);
        localGL11.glStencilOp(7680, 7680, 7681);
        paramGLCanvas.drawRect(f3 - f2 / 2.0F, f4 - f1 / 2.0F, f2, f1, CropView.this.mPaint);
        paramGLCanvas.fillRect(f3 - f2 / 2.0F, f4 - f1 / 2.0F, f2, f1, 0);
        paramGLCanvas.fillRect(paramRectF.left, paramRectF.top, paramRectF.width(), paramRectF.height(), -2147483648);
      }
    }

    private void moveEdges(MotionEvent paramMotionEvent)
    {
      float f1 = CropView.this.mAnimation.getScale();
      float f2 = (paramMotionEvent.getX() - this.mReferenceX) / f1 / CropView.this.mImageWidth;
      float f3 = (paramMotionEvent.getY() - this.mReferenceY) / f1 / CropView.this.mImageHeight;
      this.mReferenceX = paramMotionEvent.getX();
      this.mReferenceY = paramMotionEvent.getY();
      RectF localRectF = this.mHighlightRect;
      if ((0x10 & this.mMovingEdges) != 0)
      {
        float f13 = Utils.clamp(f2, -localRectF.left, 1.0F - localRectF.right);
        float f14 = Utils.clamp(f3, -localRectF.top, 1.0F - localRectF.bottom);
        localRectF.top = (f14 + localRectF.top);
        localRectF.bottom = (f14 + localRectF.bottom);
        localRectF.left = (f13 + localRectF.left);
        localRectF.right = (f13 + localRectF.right);
      }
      while (true)
      {
        label177: invalidate();
        return;
        PointF localPointF = this.mTempPoint;
        localPointF.set(this.mReferenceX, this.mReferenceY);
        CropView.this.mAnimation.inverseMapPoint(localPointF);
        float f4 = localRectF.left + 16.0F / CropView.this.mImageWidth;
        float f5 = localRectF.right - 16.0F / CropView.this.mImageWidth;
        float f6 = localRectF.top + 16.0F / CropView.this.mImageHeight;
        float f7 = localRectF.bottom - 16.0F / CropView.this.mImageHeight;
        if ((0x4 & this.mMovingEdges) != 0)
          localRectF.right = Utils.clamp(localPointF.x, f4, 1.0F);
        if ((0x1 & this.mMovingEdges) != 0)
          localRectF.left = Utils.clamp(localPointF.x, 0.0F, f5);
        if ((0x2 & this.mMovingEdges) != 0)
          localRectF.top = Utils.clamp(localPointF.y, 0.0F, f7);
        if ((0x8 & this.mMovingEdges) != 0)
          localRectF.bottom = Utils.clamp(localPointF.y, f6, 1.0F);
        if (CropView.this.mAspectRatio == -1.0F)
          continue;
        float f8 = CropView.this.mAspectRatio * CropView.this.mImageHeight / CropView.this.mImageWidth;
        float f12;
        if (localRectF.width() / localRectF.height() > f8)
        {
          f12 = localRectF.width() / f8;
          if ((0x8 & this.mMovingEdges) != 0)
            localRectF.bottom = Utils.clamp(f12 + localRectF.top, f6, 1.0F);
        }
        float f11;
        while (true)
        {
          if (localRectF.width() / localRectF.height() <= f8)
            break label651;
          f11 = f8 * localRectF.height();
          if ((0x1 & this.mMovingEdges) == 0)
            break;
          localRectF.left = Utils.clamp(localRectF.right - f11, 0.0F, f5);
          break label177:
          localRectF.top = Utils.clamp(localRectF.bottom - f12, 0.0F, f7);
          continue;
          float f9 = f8 * localRectF.height();
          if ((0x1 & this.mMovingEdges) != 0)
            localRectF.left = Utils.clamp(localRectF.right - f9, 0.0F, f5);
          localRectF.right = Utils.clamp(f9 + localRectF.left, f4, 1.0F);
        }
        localRectF.right = Utils.clamp(f11 + localRectF.left, f4, 1.0F);
        continue;
        label651: float f10 = localRectF.width() / f8;
        if ((0x8 & this.mMovingEdges) != 0)
          localRectF.bottom = Utils.clamp(f10 + localRectF.top, f6, 1.0F);
        localRectF.top = Utils.clamp(localRectF.bottom - f10, 0.0F, f7);
      }
    }

    private void setMovingEdges(MotionEvent paramMotionEvent)
    {
      RectF localRectF = CropView.this.mAnimation.mapRect(this.mHighlightRect, this.mTempRect);
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      if ((f1 > 30.0F + localRectF.left) && (f1 < localRectF.right - 30.0F) && (f2 > 30.0F + localRectF.top) && (f2 < localRectF.bottom - 30.0F))
        this.mMovingEdges = 16;
      int i;
      label116: int j;
      label143: int i3;
      label166: int i4;
      label184: label219: label227: int i6;
      label302: int k;
      label335: int l;
      do
      {
        do
        {
          return;
          if ((localRectF.top - 30.0F > f2) || (f2 > 30.0F + localRectF.bottom))
            break label483;
          i = 1;
          if ((localRectF.left - 30.0F > f1) || (f1 > 30.0F + localRectF.right))
            break label489;
          j = 1;
          if (i == 0)
            continue;
          if (Math.abs(f1 - localRectF.left) > 30.0F)
            break label495;
          i3 = 1;
          if (Math.abs(f1 - localRectF.right) > 30.0F)
            break label501;
          i4 = 1;
          if ((i3 != 0) && (i4 != 0))
          {
            if (Math.abs(f1 - localRectF.left) >= Math.abs(f1 - localRectF.right))
              break label507;
            i3 = 1;
            if (i3 != 0)
              break label513;
            i4 = 1;
          }
          if (i3 != 0)
            this.mMovingEdges = (0x1 | this.mMovingEdges);
          if (i4 != 0)
            this.mMovingEdges = (0x4 | this.mMovingEdges);
          if ((CropView.this.mAspectRatio == -1.0F) || (j == 0))
            continue;
          int i5 = this.mMovingEdges;
          if (f2 <= (localRectF.top + localRectF.bottom) / 2.0F)
            break label519;
          i6 = 8;
          this.mMovingEdges = (i6 | i5);
        }
        while (j == 0);
        if (Math.abs(f2 - localRectF.top) > 30.0F)
          break label525;
        k = 1;
        if (Math.abs(f2 - localRectF.bottom) > 30.0F)
          break label531;
        l = 1;
        if ((k != 0) && (l != 0))
        {
          label354: if (Math.abs(f2 - localRectF.top) >= Math.abs(f2 - localRectF.bottom))
            break label537;
          k = 1;
          label391: if (k != 0)
            break label543;
          l = 1;
        }
        if (k != 0)
          label399: this.mMovingEdges = (0x2 | this.mMovingEdges);
        if (l == 0)
          continue;
        this.mMovingEdges = (0x8 | this.mMovingEdges);
      }
      while ((CropView.this.mAspectRatio == -1.0F) || (i == 0));
      int i1 = this.mMovingEdges;
      if (f1 > (localRectF.left + localRectF.right) / 2.0F);
      for (int i2 = 4; ; i2 = 1)
      {
        this.mMovingEdges = (i2 | i1);
        return;
        label483: i = 0;
        break label116:
        label489: j = 0;
        break label143:
        label495: i3 = 0;
        break label166:
        label501: i4 = 0;
        break label184:
        label507: i3 = 0;
        break label219:
        label513: i4 = 0;
        break label227:
        label519: i6 = 2;
        break label302:
        label525: k = 0;
        break label335:
        label531: l = 0;
        break label354:
        label537: k = 0;
        break label391:
        label543: l = 0;
        break label399:
      }
    }

    protected boolean onTouch(MotionEvent paramMotionEvent)
    {
      switch (paramMotionEvent.getAction())
      {
      default:
        return true;
      case 0:
        this.mReferenceX = paramMotionEvent.getX();
        this.mReferenceY = paramMotionEvent.getY();
        setMovingEdges(paramMotionEvent);
        invalidate();
        return true;
      case 2:
        moveEdges(paramMotionEvent);
        return true;
      case 1:
      case 3:
      }
      this.mMovingEdges = 0;
      CropView.this.mAnimation.startParkingAnimation(this.mHighlightRect);
      invalidate();
      return true;
    }

    protected void renderBackground(GLCanvas paramGLCanvas)
    {
      RectF localRectF = CropView.this.mAnimation.mapRect(this.mHighlightRect, this.mTempRect);
      drawHighlightRectangle(paramGLCanvas, localRectF);
      float f1 = (localRectF.top + localRectF.bottom) / 2.0F;
      float f2 = (localRectF.left + localRectF.right) / 2.0F;
      if (this.mMovingEdges == 0);
      for (int i = 1; ; i = 0)
      {
        if (((0x4 & this.mMovingEdges) != 0) || (i != 0))
          this.mArrow.draw(paramGLCanvas, Math.round(localRectF.right - this.mArrow.getWidth() / 2), Math.round(f1 - this.mArrow.getHeight() / 2));
        if (((0x1 & this.mMovingEdges) != 0) || (i != 0))
          this.mArrow.draw(paramGLCanvas, Math.round(localRectF.left - this.mArrow.getWidth() / 2), Math.round(f1 - this.mArrow.getHeight() / 2));
        if (((0x2 & this.mMovingEdges) != 0) || (i != 0))
          this.mArrow.draw(paramGLCanvas, Math.round(f2 - this.mArrow.getWidth() / 2), Math.round(localRectF.top - this.mArrow.getHeight() / 2));
        if (((0x8 & this.mMovingEdges) != 0) || (i != 0))
          this.mArrow.draw(paramGLCanvas, Math.round(f2 - this.mArrow.getWidth() / 2), Math.round(localRectF.bottom - this.mArrow.getHeight() / 2));
        return;
      }
    }

    public void setInitRectangle()
    {
      float f1;
      label15: float f2;
      float f3;
      if (CropView.this.mAspectRatio == -1.0F)
      {
        f1 = 1.0F;
        f2 = 0.3F;
        f3 = 0.3F;
        if (f1 <= 1.0F)
          break label84;
        f3 = f2 / f1;
      }
      while (true)
      {
        this.mHighlightRect.set(0.5F - f2, 0.5F - f3, 0.5F + f2, 0.5F + f3);
        return;
        f1 = CropView.this.mAspectRatio * CropView.this.mImageHeight / CropView.this.mImageWidth;
        break label15:
        label84: f2 = f3 * f1;
      }
    }

    public void setRectangle(RectF paramRectF)
    {
      this.mHighlightRect.set(paramRectF);
      CropView.this.mAnimation.startParkingAnimation(paramRectF);
      invalidate();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.CropView
 * JD-Core Version:    0.5.4
 */