package com.adobe.xmp;

public class XMPException extends Exception
{
  private int errorCode;

  public XMPException(String paramString, int paramInt)
  {
    super(paramString);
    this.errorCode = paramInt;
  }

  public XMPException(String paramString, int paramInt, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.errorCode = paramInt;
  }

  public int getErrorCode()
  {
    return this.errorCode;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.XMPException
 * JD-Core Version:    0.5.4
 */