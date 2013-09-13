package com.android.gallery3d.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import com.android.gallery3d.data.BitmapPool;
import com.android.gallery3d.util.ThreadPool.Job;
import com.android.gallery3d.util.ThreadPool.JobContext;

public class AlbumLabelMaker
{
  private BitmapPool mBitmapPool;
  private final LazyLoadedBitmap mCameraIcon;
  private final Context mContext;
  private final TextPaint mCountPaint;
  private int mLabelWidth;
  private final LazyLoadedBitmap mLocalSetIcon;
  private final LazyLoadedBitmap mMtpIcon;
  private final LazyLoadedBitmap mPicasaIcon;
  private final AlbumSetSlotRenderer.LabelSpec mSpec;
  private final TextPaint mTitlePaint;

  public AlbumLabelMaker(Context paramContext, AlbumSetSlotRenderer.LabelSpec paramLabelSpec)
  {
    this.mContext = paramContext;
    this.mSpec = paramLabelSpec;
    this.mTitlePaint = getTextPaint(paramLabelSpec.titleFontSize, paramLabelSpec.titleColor, false);
    this.mCountPaint = getTextPaint(paramLabelSpec.countFontSize, paramLabelSpec.countColor, false);
    this.mLocalSetIcon = new LazyLoadedBitmap(2130837623);
    this.mPicasaIcon = new LazyLoadedBitmap(2130837624);
    this.mCameraIcon = new LazyLoadedBitmap(2130837622);
    this.mMtpIcon = new LazyLoadedBitmap(2130837625);
  }

  static void drawText(Canvas paramCanvas, int paramInt1, int paramInt2, String paramString, int paramInt3, TextPaint paramTextPaint)
  {
    monitorenter;
    float f = paramInt3;
    try
    {
      paramCanvas.drawText(TextUtils.ellipsize(paramString, paramTextPaint, f, TextUtils.TruncateAt.END).toString(), paramInt1, paramInt2 - paramTextPaint.getFontMetricsInt().ascent, paramTextPaint);
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public static int getBorderSize()
  {
    return 0;
  }

  private Bitmap getOverlayAlbumIcon(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return null;
    case 4:
      return this.mCameraIcon.get();
    case 1:
      return this.mLocalSetIcon.get();
    case 3:
      return this.mMtpIcon.get();
    case 2:
    }
    return this.mPicasaIcon.get();
  }

  private static TextPaint getTextPaint(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    TextPaint localTextPaint = new TextPaint();
    localTextPaint.setTextSize(paramInt1);
    localTextPaint.setAntiAlias(true);
    localTextPaint.setColor(paramInt2);
    if (paramBoolean)
      localTextPaint.setTypeface(Typeface.defaultFromStyle(1));
    return localTextPaint;
  }

  public void clearRecycledLabels()
  {
    if (this.mBitmapPool == null)
      return;
    this.mBitmapPool.clear();
  }

  public void recycleLabel(Bitmap paramBitmap)
  {
    this.mBitmapPool.recycle(paramBitmap);
  }

  public ThreadPool.Job<Bitmap> requestLabel(String paramString1, String paramString2, int paramInt)
  {
    return new AlbumLabelJob(paramString1, paramString2, paramInt);
  }

  public void setLabelWidth(int paramInt)
  {
    monitorenter;
    try
    {
      int i = this.mLabelWidth;
      if (i == paramInt)
        return;
      this.mLabelWidth = paramInt;
    }
    finally
    {
      monitorexit;
    }
  }

  private class AlbumLabelJob
    implements ThreadPool.Job<Bitmap>
  {
    private final String mCount;
    private final int mSourceType;
    private final String mTitle;

    public AlbumLabelJob(String paramString1, String paramInt, int arg4)
    {
      this.mTitle = paramString1;
      this.mCount = paramInt;
      int i;
      this.mSourceType = i;
    }

    public Bitmap run(ThreadPool.JobContext paramJobContext)
    {
      AlbumSetSlotRenderer.LabelSpec localLabelSpec = AlbumLabelMaker.this.mSpec;
      String str1 = this.mTitle;
      String str2 = this.mCount;
      Bitmap localBitmap1 = AlbumLabelMaker.this.getOverlayAlbumIcon(this.mSourceType);
      monitorenter;
      Bitmap localBitmap2;
      Canvas localCanvas;
      do
      {
        int i;
        try
        {
          i = AlbumLabelMaker.this.mLabelWidth;
          localBitmap2 = AlbumLabelMaker.this.mBitmapPool.getBitmap();
          monitorexit;
          if (localBitmap2 == null)
            localBitmap2 = Bitmap.createBitmap(i + 0, 0 + localLabelSpec.labelBackgroundHeight, Bitmap.Config.ARGB_8888);
          localCanvas = new Canvas(localBitmap2);
          localCanvas.clipRect(0, 0, 0 + localBitmap2.getWidth(), 0 + localBitmap2.getHeight());
          localCanvas.drawColor(AlbumLabelMaker.this.mSpec.backgroundColor, PorterDuff.Mode.SRC);
          localCanvas.translate(0.0F, 0.0F);
          if (paramJobContext.isCancelled())
            return localBitmap2;
        }
        finally
        {
          monitorexit;
        }
        int j = localLabelSpec.leftMargin + localLabelSpec.iconSize;
        AlbumLabelMaker.drawText(localCanvas, j, (localLabelSpec.labelBackgroundHeight - localLabelSpec.titleFontSize) / 2, str1, i - localLabelSpec.leftMargin - j - localLabelSpec.titleRightMargin, AlbumLabelMaker.this.mTitlePaint);
        if (paramJobContext.isCancelled())
          return null;
        int k = i - localLabelSpec.titleRightMargin;
        AlbumLabelMaker.drawText(localCanvas, k, (localLabelSpec.labelBackgroundHeight - localLabelSpec.countFontSize) / 2, str2, i - k, AlbumLabelMaker.this.mCountPaint);
      }
      while (localBitmap1 == null);
      if (paramJobContext.isCancelled())
        return null;
      float f = localLabelSpec.iconSize / localBitmap1.getWidth();
      localCanvas.translate(localLabelSpec.leftMargin, (localLabelSpec.labelBackgroundHeight - Math.round(f * localBitmap1.getHeight())) / 2.0F);
      localCanvas.scale(f, f);
      localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, null);
      return localBitmap2;
    }
  }

  private class LazyLoadedBitmap
  {
    private Bitmap mBitmap;
    private int mResId;

    public LazyLoadedBitmap(int arg2)
    {
      int i;
      this.mResId = i;
    }

    public Bitmap get()
    {
      monitorenter;
      try
      {
        if (this.mBitmap == null)
        {
          BitmapFactory.Options localOptions = new BitmapFactory.Options();
          localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
          this.mBitmap = BitmapFactory.decodeResource(AlbumLabelMaker.this.mContext.getResources(), this.mResId, localOptions);
        }
        Bitmap localBitmap = this.mBitmap;
        return localBitmap;
      }
      finally
      {
        monitorexit;
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.AlbumLabelMaker
 * JD-Core Version:    0.5.4
 */