package com.android.gallery3d.app;

import android.net.Uri;

public abstract interface StitchingChangeListener
{
  public abstract void onStitchingProgress(Uri paramUri, int paramInt);

  public abstract void onStitchingQueued(Uri paramUri);

  public abstract void onStitchingResult(Uri paramUri);
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.StitchingChangeListener
 * JD-Core Version:    0.5.4
 */