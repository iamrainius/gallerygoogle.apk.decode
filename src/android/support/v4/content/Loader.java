package android.support.v4.content;

import android.support.v4.util.DebugUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class Loader<D>
{
  boolean mAbandoned;
  boolean mContentChanged;
  int mId;
  OnLoadCompleteListener<D> mListener;
  boolean mReset;
  boolean mStarted;

  public void abandon()
  {
    this.mAbandoned = true;
    onAbandon();
  }

  public String dataToString(D paramD)
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    DebugUtils.buildShortClassTag(paramD, localStringBuilder);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }

  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mId=");
    paramPrintWriter.print(this.mId);
    paramPrintWriter.print(" mListener=");
    paramPrintWriter.println(this.mListener);
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mStarted=");
    paramPrintWriter.print(this.mStarted);
    paramPrintWriter.print(" mContentChanged=");
    paramPrintWriter.print(this.mContentChanged);
    paramPrintWriter.print(" mAbandoned=");
    paramPrintWriter.print(this.mAbandoned);
    paramPrintWriter.print(" mReset=");
    paramPrintWriter.println(this.mReset);
  }

  protected void onAbandon()
  {
  }

  protected void onReset()
  {
  }

  protected void onStartLoading()
  {
  }

  protected void onStopLoading()
  {
  }

  public void registerListener(int paramInt, OnLoadCompleteListener<D> paramOnLoadCompleteListener)
  {
    if (this.mListener != null)
      throw new IllegalStateException("There is already a listener registered");
    this.mListener = paramOnLoadCompleteListener;
    this.mId = paramInt;
  }

  public void reset()
  {
    onReset();
    this.mReset = true;
    this.mStarted = false;
    this.mAbandoned = false;
    this.mContentChanged = false;
  }

  public final void startLoading()
  {
    this.mStarted = true;
    this.mReset = false;
    this.mAbandoned = false;
    onStartLoading();
  }

  public void stopLoading()
  {
    this.mStarted = false;
    onStopLoading();
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    DebugUtils.buildShortClassTag(this, localStringBuilder);
    localStringBuilder.append(" id=");
    localStringBuilder.append(this.mId);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }

  public void unregisterListener(OnLoadCompleteListener<D> paramOnLoadCompleteListener)
  {
    if (this.mListener == null)
      throw new IllegalStateException("No listener register");
    if (this.mListener != paramOnLoadCompleteListener)
      throw new IllegalArgumentException("Attempting to unregister the wrong listener");
    this.mListener = null;
  }

  public static abstract interface OnLoadCompleteListener<D>
  {
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     android.support.v4.content.Loader
 * JD-Core Version:    0.5.4
 */