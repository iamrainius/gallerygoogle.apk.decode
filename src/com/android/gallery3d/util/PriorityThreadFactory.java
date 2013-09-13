package com.android.gallery3d.util;

import android.os.Process;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory
  implements ThreadFactory
{
  private final String mName;
  private final AtomicInteger mNumber = new AtomicInteger();
  private final int mPriority;

  public PriorityThreadFactory(String paramString, int paramInt)
  {
    this.mName = paramString;
    this.mPriority = paramInt;
  }

  public Thread newThread(Runnable paramRunnable)
  {
    return new Thread(paramRunnable, this.mName + '-' + this.mNumber.getAndIncrement())
    {
      public void run()
      {
        Process.setThreadPriority(PriorityThreadFactory.this.mPriority);
        super.run();
      }
    };
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.PriorityThreadFactory
 * JD-Core Version:    0.5.4
 */