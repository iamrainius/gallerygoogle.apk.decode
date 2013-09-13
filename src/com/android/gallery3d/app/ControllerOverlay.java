package com.android.gallery3d.app;

public abstract interface ControllerOverlay
{
  public static abstract interface Listener
  {
    public abstract void onHidden();

    public abstract void onPlayPause();

    public abstract void onReplay();

    public abstract void onSeekEnd(int paramInt1, int paramInt2, int paramInt3);

    public abstract void onSeekMove(int paramInt);

    public abstract void onSeekStart();

    public abstract void onShown();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.ControllerOverlay
 * JD-Core Version:    0.5.4
 */