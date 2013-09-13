package com.android.gallery3d.filtershow.cache;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;

public class DelayedPresetCache extends DirectPresetCache
  implements Handler.Callback
{
  private HandlerThread mHandlerThread = null;
  private Handler mProcessingHandler = null;
  private final Handler mUIHandler = new Handler()
  {
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 0:
      }
      DirectPresetCache.CachedPreset localCachedPreset = (DirectPresetCache.CachedPreset)paramMessage.obj;
      DelayedPresetCache.this.didCompute(localCachedPreset);
    }
  };

  public DelayedPresetCache(ImageLoader paramImageLoader, int paramInt)
  {
    super(paramImageLoader, paramInt);
    this.mHandlerThread.start();
    this.mProcessingHandler = new Handler(this.mHandlerThread.getLooper(), this);
  }

  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default:
      return false;
    case 1:
    }
    DirectPresetCache.CachedPreset localCachedPreset = (DirectPresetCache.CachedPreset)paramMessage.obj;
    compute(localCachedPreset);
    Message localMessage = this.mUIHandler.obtainMessage(0, localCachedPreset);
    this.mUIHandler.sendMessage(localMessage);
    return false;
  }

  protected void willCompute(DirectPresetCache.CachedPreset paramCachedPreset)
  {
    if (paramCachedPreset == null)
      return;
    paramCachedPreset.setBusy(true);
    Message localMessage = this.mProcessingHandler.obtainMessage(1, paramCachedPreset);
    this.mProcessingHandler.sendMessage(localMessage);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.filtershow.cache.DelayedPresetCache
 * JD-Core Version:    0.5.4
 */