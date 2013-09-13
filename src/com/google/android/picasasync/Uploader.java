package com.google.android.picasasync;

public abstract interface Uploader
{
  public static class LocalIoException extends Exception
  {
    private static final long serialVersionUID = -1384577611439153329L;

    public LocalIoException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }

  public static class MediaFileChangedException extends Exception
  {
    private static final long serialVersionUID = -8858438283331535589L;

    public MediaFileChangedException(String paramString)
    {
      super(paramString);
    }
  }

  public static class PicasaQuotaException extends Exception
  {
    private static final long serialVersionUID = -22525693778211948L;

    public PicasaQuotaException(String paramString)
    {
      super(paramString);
    }
  }

  public static class RestartException extends Exception
  {
    private static final long serialVersionUID = -2575549139581664777L;

    public RestartException(String paramString)
    {
      super(paramString);
    }
  }

  public static class UnauthorizedException extends Exception
  {
    private static final long serialVersionUID = 7476449921115679307L;

    public UnauthorizedException(String paramString)
    {
      super(paramString);
    }

    public UnauthorizedException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }

  public static class UploadException extends Exception
  {
    private static final long serialVersionUID = 4567932751848488557L;

    public UploadException(String paramString)
    {
      super(paramString);
    }

    public UploadException(String paramString, Throwable paramThrowable)
    {
      super(paramString, paramThrowable);
    }

    public UploadException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }

  public static abstract interface UploadProgressListener
  {
    public abstract void onProgress(UploadTaskEntry paramUploadTaskEntry);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.picasasync.Uploader
 * JD-Core Version:    0.5.4
 */