package com.googlecode.mp4parser.authoring;

public abstract class AbstractTrack
  implements Track
{
  private boolean enabled = true;
  private boolean inMovie = true;
  private boolean inPoster = true;
  private boolean inPreview = true;

  public boolean isEnabled()
  {
    return this.enabled;
  }

  public boolean isInMovie()
  {
    return this.inMovie;
  }

  public boolean isInPoster()
  {
    return this.inPoster;
  }

  public boolean isInPreview()
  {
    return this.inPreview;
  }

  public void setEnabled(boolean paramBoolean)
  {
    this.enabled = paramBoolean;
  }

  public void setInMovie(boolean paramBoolean)
  {
    this.inMovie = paramBoolean;
  }

  public void setInPoster(boolean paramBoolean)
  {
    this.inPoster = paramBoolean;
  }

  public void setInPreview(boolean paramBoolean)
  {
    this.inPreview = paramBoolean;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.authoring.AbstractTrack
 * JD-Core Version:    0.5.4
 */