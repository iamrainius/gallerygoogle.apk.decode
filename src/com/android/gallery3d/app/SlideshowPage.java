package com.android.gallery3d.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.ContentListener;
import com.android.gallery3d.data.DataManager;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaSet;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.GLCanvas;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.GLView;
import com.android.gallery3d.ui.SlideshowView;
import com.android.gallery3d.ui.SynchronizedHandler;
import com.android.gallery3d.util.Future;
import com.android.gallery3d.util.FutureListener;
import java.util.ArrayList;
import java.util.Random;

public class SlideshowPage extends ActivityState
{
  private Handler mHandler;
  private boolean mIsActive = false;
  private Model mModel;
  private Slide mPendingSlide = null;
  private final Intent mResultIntent = new Intent();
  private final GLView mRootPane = new GLView()
  {
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      SlideshowPage.this.mSlideshowView.layout(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
    }

    protected boolean onTouch(MotionEvent paramMotionEvent)
    {
      if (paramMotionEvent.getAction() == 1)
        SlideshowPage.this.onBackPressed();
      return true;
    }

    protected void renderBackground(GLCanvas paramGLCanvas)
    {
      paramGLCanvas.clearBuffer(getBackgroundColor());
    }
  };
  private SlideshowView mSlideshowView;

  private static MediaItem findMediaItem(MediaSet paramMediaSet, int paramInt)
  {
    int i = 0;
    int j = paramMediaSet.getSubMediaSetCount();
    while (i < j)
    {
      MediaSet localMediaSet = paramMediaSet.getSubMediaSet(i);
      int k = localMediaSet.getTotalMediaItemCount();
      if (paramInt < k)
        return findMediaItem(localMediaSet, paramInt);
      paramInt -= k;
      ++i;
    }
    ArrayList localArrayList = paramMediaSet.getMediaItem(paramInt, 1);
    if (localArrayList.isEmpty())
      return null;
    return (MediaItem)localArrayList.get(0);
  }

  private void initializeData(Bundle paramBundle)
  {
    boolean bool1 = paramBundle.getBoolean("random-order", false);
    String str1 = FilterUtils.newFilterPath(paramBundle.getString("media-set-path"), 1);
    MediaSet localMediaSet = this.mActivity.getDataManager().getMediaSet(str1);
    if (bool1)
    {
      boolean bool3 = paramBundle.getBoolean("repeat");
      this.mModel = new SlideshowDataAdapter(this.mActivity, new ShuffleSource(localMediaSet, bool3), 0, null);
      setStateResult(-1, this.mResultIntent.putExtra("photo-index", 0));
      return;
    }
    int i = paramBundle.getInt("photo-index");
    String str2 = paramBundle.getString("media-item-path");
    Path localPath = null;
    if (str2 != null)
      localPath = Path.fromString(str2);
    boolean bool2 = paramBundle.getBoolean("repeat");
    this.mModel = new SlideshowDataAdapter(this.mActivity, new SequentialSource(localMediaSet, bool2), i, localPath);
    setStateResult(-1, this.mResultIntent.putExtra("photo-index", i));
  }

  private void initializeViews()
  {
    this.mSlideshowView = new SlideshowView();
    this.mRootPane.addComponent(this.mSlideshowView);
    setContentPane(this.mRootPane);
  }

  private void loadNextBitmap()
  {
    this.mModel.nextSlide(new FutureListener()
    {
      public void onFutureDone(Future<SlideshowPage.Slide> paramFuture)
      {
        SlideshowPage.access$302(SlideshowPage.this, (SlideshowPage.Slide)paramFuture.get());
        SlideshowPage.this.mHandler.sendEmptyMessage(2);
      }
    });
  }

  private void showPendingBitmap()
  {
    Slide localSlide = this.mPendingSlide;
    if (localSlide == null)
    {
      if (this.mIsActive)
        this.mActivity.getStateManager().finishState(this);
      return;
    }
    this.mSlideshowView.next(localSlide.bitmap, localSlide.item.getRotation());
    setStateResult(-1, this.mResultIntent.putExtra("media-item-path", localSlide.item.getPath().toString()).putExtra("photo-index", localSlide.index));
    this.mHandler.sendEmptyMessageDelayed(1, 3000L);
  }

  protected int getBackgroundColorId()
  {
    return 2131296291;
  }

  public void onCreate(Bundle paramBundle1, Bundle paramBundle2)
  {
    super.onCreate(paramBundle1, paramBundle2);
    this.mFlags = (0x33 | this.mFlags);
    if (paramBundle1.getBoolean("dream"));
    for (this.mFlags = (0x4 | this.mFlags); ; this.mFlags = (0x8 | this.mFlags))
    {
      this.mHandler = new SynchronizedHandler(this.mActivity.getGLRoot())
      {
        public void handleMessage(Message paramMessage)
        {
          switch (paramMessage.what)
          {
          default:
            throw new AssertionError();
          case 2:
            SlideshowPage.this.showPendingBitmap();
            return;
          case 1:
          }
          SlideshowPage.this.loadNextBitmap();
        }
      };
      initializeViews();
      initializeData(paramBundle1);
      return;
    }
  }

  public void onPause()
  {
    super.onPause();
    this.mIsActive = false;
    this.mModel.pause();
    this.mSlideshowView.release();
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
  }

  public void onResume()
  {
    super.onResume();
    this.mIsActive = true;
    this.mModel.resume();
    if (this.mPendingSlide != null)
    {
      showPendingBitmap();
      return;
    }
    loadNextBitmap();
  }

  public static abstract interface Model
  {
    public abstract Future<SlideshowPage.Slide> nextSlide(FutureListener<SlideshowPage.Slide> paramFutureListener);

    public abstract void pause();

    public abstract void resume();
  }

  private static class SequentialSource
    implements SlideshowDataAdapter.SlideshowSource
  {
    private ArrayList<MediaItem> mData = new ArrayList();
    private int mDataStart = 0;
    private long mDataVersion = -1L;
    private final MediaSet mMediaSet;
    private final boolean mRepeat;

    public SequentialSource(MediaSet paramMediaSet, boolean paramBoolean)
    {
      this.mMediaSet = paramMediaSet;
      this.mRepeat = paramBoolean;
    }

    public void addContentListener(ContentListener paramContentListener)
    {
      this.mMediaSet.addContentListener(paramContentListener);
    }

    public int findItemIndex(Path paramPath, int paramInt)
    {
      return this.mMediaSet.getIndexOfItem(paramPath, paramInt);
    }

    public MediaItem getMediaItem(int paramInt)
    {
      int i = this.mDataStart + this.mData.size();
      int j;
      if (this.mRepeat)
      {
        j = this.mMediaSet.getMediaItemCount();
        if (j != 0);
      }
      do
      {
        return null;
        paramInt %= j;
        if ((paramInt >= this.mDataStart) && (paramInt < i))
          continue;
        this.mData = this.mMediaSet.getMediaItem(paramInt, 32);
        this.mDataStart = paramInt;
        i = paramInt + this.mData.size();
      }
      while ((paramInt < this.mDataStart) || (paramInt >= i));
      return (MediaItem)this.mData.get(paramInt - this.mDataStart);
    }

    public long reload()
    {
      long l = this.mMediaSet.reload();
      if (l != this.mDataVersion)
      {
        this.mDataVersion = l;
        this.mData.clear();
      }
      return this.mDataVersion;
    }

    public void removeContentListener(ContentListener paramContentListener)
    {
      this.mMediaSet.removeContentListener(paramContentListener);
    }
  }

  private static class ShuffleSource
    implements SlideshowDataAdapter.SlideshowSource
  {
    private int mLastIndex = -1;
    private final MediaSet mMediaSet;
    private int[] mOrder = new int[0];
    private final Random mRandom = new Random();
    private final boolean mRepeat;
    private long mSourceVersion = -1L;

    public ShuffleSource(MediaSet paramMediaSet, boolean paramBoolean)
    {
      this.mMediaSet = ((MediaSet)Utils.checkNotNull(paramMediaSet));
      this.mRepeat = paramBoolean;
    }

    private void generateOrderArray(int paramInt)
    {
      if (this.mOrder.length != paramInt)
      {
        this.mOrder = new int[paramInt];
        for (int j = 0; j < paramInt; ++j)
          this.mOrder[j] = j;
      }
      for (int i = paramInt - 1; i > 0; --i)
        Utils.swap(this.mOrder, i, this.mRandom.nextInt(i + 1));
      if ((this.mOrder[0] != this.mLastIndex) || (paramInt <= 1))
        return;
      Utils.swap(this.mOrder, 0, 1 + this.mRandom.nextInt(paramInt - 1));
    }

    public void addContentListener(ContentListener paramContentListener)
    {
      this.mMediaSet.addContentListener(paramContentListener);
    }

    public int findItemIndex(Path paramPath, int paramInt)
    {
      return paramInt;
    }

    public MediaItem getMediaItem(int paramInt)
    {
      if (!this.mRepeat)
      {
        int k = this.mOrder.length;
        localMediaItem = null;
        if (paramInt < k);
      }
      int i;
      do
      {
        return localMediaItem;
        i = this.mOrder.length;
        localMediaItem = null;
      }
      while (i == 0);
      this.mLastIndex = this.mOrder[(paramInt % this.mOrder.length)];
      MediaItem localMediaItem = SlideshowPage.access$500(this.mMediaSet, this.mLastIndex);
      for (int j = 0; ; ++j)
      {
        if ((j < 5) && (localMediaItem == null));
        Log.w("SlideshowPage", "fail to find image: " + this.mLastIndex);
        this.mLastIndex = this.mRandom.nextInt(this.mOrder.length);
        localMediaItem = SlideshowPage.access$500(this.mMediaSet, this.mLastIndex);
      }
    }

    public long reload()
    {
      long l = this.mMediaSet.reload();
      if (l != this.mSourceVersion)
      {
        this.mSourceVersion = l;
        int i = this.mMediaSet.getTotalMediaItemCount();
        if (i != this.mOrder.length)
          generateOrderArray(i);
      }
      return l;
    }

    public void removeContentListener(ContentListener paramContentListener)
    {
      this.mMediaSet.removeContentListener(paramContentListener);
    }
  }

  public static class Slide
  {
    public Bitmap bitmap;
    public int index;
    public MediaItem item;

    public Slide(MediaItem paramMediaItem, int paramInt, Bitmap paramBitmap)
    {
      this.bitmap = paramBitmap;
      this.item = paramMediaItem;
      this.index = paramInt;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.SlideshowPage
 * JD-Core Version:    0.5.4
 */