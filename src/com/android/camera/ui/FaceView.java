package com.android.camera.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.hardware.Camera.Face;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.CameraScreenNail;
import com.android.camera.Util;

@TargetApi(14)
public class FaceView extends View
  implements FocusIndicator, Rotatable
{
  private final boolean LOGV = false;
  private volatile boolean mBlocked;
  private int mColor;
  private int mDisplayOrientation;
  private Camera.Face[] mFaces;
  private final int mFailColor;
  private final int mFocusedColor;
  private final int mFocusingColor;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 1:
      }
      FaceView.access$002(FaceView.this, false);
      FaceView.access$102(FaceView.this, FaceView.this.mPendingFaces);
      FaceView.this.invalidate();
    }
  };
  private Matrix mMatrix = new Matrix();
  private boolean mMirror;
  private int mOrientation;
  private Paint mPaint;
  private boolean mPause;
  private Camera.Face[] mPendingFaces;
  private RectF mRect = new RectF();
  private boolean mStateSwitchPending = false;

  public FaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    Resources localResources = getResources();
    this.mFocusingColor = localResources.getColor(2131296275);
    this.mFocusedColor = localResources.getColor(2131296276);
    this.mFailColor = localResources.getColor(2131296277);
    this.mColor = this.mFocusingColor;
    this.mPaint = new Paint();
    this.mPaint.setAntiAlias(true);
    this.mPaint.setStyle(Paint.Style.STROKE);
    this.mPaint.setStrokeWidth(localResources.getDimension(2131624007));
  }

  public void clear()
  {
    this.mColor = this.mFocusingColor;
    this.mFaces = null;
    invalidate();
  }

  public boolean faceExists()
  {
    return (this.mFaces != null) && (this.mFaces.length > 0);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if ((!this.mBlocked) && (this.mFaces != null) && (this.mFaces.length > 0))
    {
      CameraScreenNail localCameraScreenNail = ((CameraActivity)getContext()).getCameraScreenNail();
      int i = localCameraScreenNail.getUncroppedRenderWidth();
      int j = localCameraScreenNail.getUncroppedRenderHeight();
      if (((j > i) && (((this.mDisplayOrientation == 0) || (this.mDisplayOrientation == 180)))) || ((i > j) && (((this.mDisplayOrientation == 90) || (this.mDisplayOrientation == 270)))))
      {
        int i2 = i;
        i = j;
        j = i2;
      }
      Util.prepareMatrix(this.mMatrix, this.mMirror, this.mDisplayOrientation, i, j);
      int k = (getWidth() - i) / 2;
      int l = (getHeight() - j) / 2;
      paramCanvas.save();
      this.mMatrix.postRotate(this.mOrientation);
      paramCanvas.rotate(-this.mOrientation);
      int i1 = 0;
      if (i1 < this.mFaces.length)
      {
        label172: if (this.mFaces[i1].score < 50);
        while (true)
        {
          ++i1;
          break label172:
          this.mRect.set(this.mFaces[i1].rect);
          this.mMatrix.mapRect(this.mRect);
          this.mPaint.setColor(this.mColor);
          this.mRect.offset(k, l);
          paramCanvas.drawOval(this.mRect, this.mPaint);
        }
      }
      paramCanvas.restore();
    }
    super.onDraw(paramCanvas);
  }

  public void pause()
  {
    this.mPause = true;
  }

  public void resume()
  {
    this.mPause = false;
  }

  public void setBlockDraw(boolean paramBoolean)
  {
    this.mBlocked = paramBoolean;
  }

  public void setDisplayOrientation(int paramInt)
  {
    this.mDisplayOrientation = paramInt;
  }

  public void setFaces(Camera.Face[] paramArrayOfFace)
  {
    if (this.mPause);
    do
    {
      return;
      if ((this.mFaces == null) || ((((paramArrayOfFace.length <= 0) || (this.mFaces.length != 0))) && (((paramArrayOfFace.length != 0) || (this.mFaces.length <= 0)))))
        break label71;
      this.mPendingFaces = paramArrayOfFace;
    }
    while (this.mStateSwitchPending);
    this.mStateSwitchPending = true;
    this.mHandler.sendEmptyMessageDelayed(1, 70L);
    return;
    if (this.mStateSwitchPending)
    {
      label71: this.mStateSwitchPending = false;
      this.mHandler.removeMessages(1);
    }
    this.mFaces = paramArrayOfFace;
    invalidate();
  }

  public void setMirror(boolean paramBoolean)
  {
    this.mMirror = paramBoolean;
  }

  public void setOrientation(int paramInt, boolean paramBoolean)
  {
    this.mOrientation = paramInt;
    invalidate();
  }

  public void showFail(boolean paramBoolean)
  {
    this.mColor = this.mFailColor;
    invalidate();
  }

  public void showStart()
  {
    this.mColor = this.mFocusingColor;
    invalidate();
  }

  public void showSuccess(boolean paramBoolean)
  {
    this.mColor = this.mFocusedColor;
    invalidate();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.ui.FaceView
 * JD-Core Version:    0.5.4
 */