package com.android.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import com.android.gallery3d.common.ApiHelper;

public class SoundClips
{
  public static Player getPlayer(Context paramContext)
  {
    if (ApiHelper.HAS_MEDIA_ACTION_SOUND)
      return new MediaActionSoundPlayer();
    return new SoundPoolPlayer(paramContext);
  }

  @TargetApi(16)
  private static class MediaActionSoundPlayer
    implements SoundClips.Player
  {
    private MediaActionSound mSound = new MediaActionSound();

    public MediaActionSoundPlayer()
    {
      this.mSound.load(2);
      this.mSound.load(3);
      this.mSound.load(1);
    }

    public void play(int paramInt)
    {
      monitorenter;
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      case 2:
      }
      while (true)
      {
        try
        {
          Log.w("MediaActionSoundPlayer", "Unrecognized action:" + paramInt);
          return;
        }
        finally
        {
          monitorexit;
        }
        this.mSound.play(2);
        continue;
        this.mSound.play(3);
      }
    }

    public void release()
    {
      if (this.mSound == null)
        return;
      this.mSound.release();
      this.mSound = null;
    }
  }

  public static abstract interface Player
  {
    public abstract void play(int paramInt);

    public abstract void release();
  }

  private static class SoundPoolPlayer
    implements SoundPool.OnLoadCompleteListener, SoundClips.Player
  {
    private static final int[] SOUND_RES = { 2131230723, 2131230727 };
    private Context mContext;
    private final boolean[] mSoundIDReady;
    private int mSoundIDToPlay;
    private final int[] mSoundIDs;
    private SoundPool mSoundPool;
    private final int[] mSoundRes = { 0, 1, 1 };

    public SoundPoolPlayer(Context paramContext)
    {
      this.mContext = paramContext;
      int i = ApiHelper.getIntFieldIfExists(AudioManager.class, "STREAM_SYSTEM_ENFORCED", null, 2);
      this.mSoundIDToPlay = 0;
      this.mSoundPool = new SoundPool(1, i, 0);
      this.mSoundPool.setOnLoadCompleteListener(this);
      this.mSoundIDs = new int[SOUND_RES.length];
      this.mSoundIDReady = new boolean[SOUND_RES.length];
      for (int j = 0; j < SOUND_RES.length; ++j)
      {
        this.mSoundIDs[j] = this.mSoundPool.load(this.mContext, SOUND_RES[j], 1);
        this.mSoundIDReady[j] = false;
      }
    }

    public void onLoadComplete(SoundPool paramSoundPool, int paramInt1, int paramInt2)
    {
      if (paramInt2 != 0)
      {
        Log.e("SoundPoolPlayer", "loading sound tracks failed (status=" + paramInt2 + ")");
        for (int j = 0; ; ++j)
        {
          if (j < this.mSoundIDs.length)
          {
            if (this.mSoundIDs[j] != paramInt1)
              continue;
            this.mSoundIDs[j] = 0;
          }
          return;
        }
      }
      for (int i = 0; ; ++i)
      {
        if (i < this.mSoundIDs.length)
        {
          if (this.mSoundIDs[i] != paramInt1)
            continue;
          this.mSoundIDReady[i] = true;
        }
        if (paramInt1 == this.mSoundIDToPlay);
        this.mSoundIDToPlay = 0;
        this.mSoundPool.play(paramInt1, 1.0F, 1.0F, 0, 0, 1.0F);
        return;
      }
    }

    public void play(int paramInt)
    {
      monitorenter;
      if (paramInt >= 0);
      while (true)
      {
        int i;
        try
        {
          if (paramInt >= this.mSoundRes.length)
          {
            Log.e("SoundPoolPlayer", "Resource ID not found for action:" + paramInt + " in play().");
            return;
          }
          i = this.mSoundRes[paramInt];
          if (this.mSoundIDs[i] != 0)
            break label110;
          this.mSoundIDs[i] = this.mSoundPool.load(this.mContext, SOUND_RES[i], 1);
        }
        finally
        {
          monitorexit;
        }
        if (this.mSoundIDReady[i] == 0)
          label110: this.mSoundIDToPlay = this.mSoundIDs[i];
        this.mSoundPool.play(this.mSoundIDs[i], 1.0F, 1.0F, 0, 0, 1.0F);
      }
    }

    public void release()
    {
      monitorenter;
      try
      {
        if (this.mSoundPool != null)
        {
          this.mSoundPool.release();
          this.mSoundPool = null;
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
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.SoundClips
 * JD-Core Version:    0.5.4
 */