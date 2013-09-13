package com.android.gallery3d.util;

import com.android.gallery3d.common.Utils;
import java.util.LinkedList;

public class JobLimiter
  implements FutureListener
{
  private final LinkedList<JobWrapper<?>> mJobs = new LinkedList();
  private int mLimit;
  private final ThreadPool mPool;

  public JobLimiter(ThreadPool paramThreadPool, int paramInt)
  {
    this.mPool = ((ThreadPool)Utils.checkNotNull(paramThreadPool));
    this.mLimit = paramInt;
  }

  private void submitTasksIfAllowed()
  {
    while ((this.mLimit > 0) && (!this.mJobs.isEmpty()))
    {
      JobWrapper localJobWrapper;
      do
        localJobWrapper = (JobWrapper)this.mJobs.removeFirst();
      while (localJobWrapper.isCancelled());
      this.mLimit = (-1 + this.mLimit);
      localJobWrapper.setFuture(this.mPool.submit(localJobWrapper, this));
    }
  }

  public void onFutureDone(Future paramFuture)
  {
    monitorenter;
    try
    {
      this.mLimit = (1 + this.mLimit);
      submitTasksIfAllowed();
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

  public <T> Future<T> submit(ThreadPool.Job<T> paramJob, FutureListener<T> paramFutureListener)
  {
    monitorenter;
    try
    {
      JobWrapper localJobWrapper = new JobWrapper((ThreadPool.Job)Utils.checkNotNull(paramJob), paramFutureListener);
      this.mJobs.addLast(localJobWrapper);
      submitTasksIfAllowed();
      monitorexit;
      return localJobWrapper;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  private static class JobWrapper<T>
    implements Future<T>, ThreadPool.Job<T>
  {
    private Future<T> mDelegate;
    private ThreadPool.Job<T> mJob;
    private FutureListener<T> mListener;
    private T mResult;
    private int mState = 0;

    public JobWrapper(ThreadPool.Job<T> paramJob, FutureListener<T> paramFutureListener)
    {
      this.mJob = paramJob;
      this.mListener = paramFutureListener;
    }

    public void cancel()
    {
      monitorenter;
      try
      {
        int i = this.mState;
        FutureListener localFutureListener = null;
        if (i != 1)
        {
          localFutureListener = this.mListener;
          this.mJob = null;
          this.mListener = null;
          if (this.mDelegate != null)
          {
            this.mDelegate.cancel();
            this.mDelegate = null;
          }
        }
        this.mState = 2;
        this.mResult = null;
        super.notifyAll();
        monitorexit;
        if (localFutureListener != null);
        return;
      }
      finally
      {
        monitorexit;
      }
    }

    public T get()
    {
      monitorenter;
      while (true)
        try
        {
          if (this.mState != 0)
            break label21;
        }
        finally
        {
          monitorexit;
        }
      label21: Object localObject2 = this.mResult;
      monitorexit;
      return localObject2;
    }

    public boolean isCancelled()
    {
      monitorenter;
      try
      {
        int i = this.mState;
        if (i == 2)
        {
          j = 1;
          return j;
        }
        int j = 0;
      }
      finally
      {
        monitorexit;
      }
    }

    public boolean isDone()
    {
      return this.mState != 0;
    }

    // ERROR //
    public T run(ThreadPool.JobContext paramJobContext)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 30	com/android/gallery3d/util/JobLimiter$JobWrapper:mState	I
      //   6: iconst_2
      //   7: if_icmpne +7 -> 14
      //   10: aload_0
      //   11: monitorexit
      //   12: aconst_null
      //   13: areturn
      //   14: aload_0
      //   15: getfield 32	com/android/gallery3d/util/JobLimiter$JobWrapper:mJob	Lcom/android/gallery3d/util/ThreadPool$Job;
      //   18: astore_3
      //   19: aload_0
      //   20: monitorexit
      //   21: aload_3
      //   22: aload_1
      //   23: invokeinterface 67 2 0
      //   28: astore 9
      //   30: aload 9
      //   32: astore 6
      //   34: aload_0
      //   35: monitorenter
      //   36: aload_0
      //   37: getfield 30	com/android/gallery3d/util/JobLimiter$JobWrapper:mState	I
      //   40: iconst_2
      //   41: if_icmpne +47 -> 88
      //   44: aload_0
      //   45: monitorexit
      //   46: aconst_null
      //   47: areturn
      //   48: astore_2
      //   49: aload_0
      //   50: monitorexit
      //   51: aload_2
      //   52: athrow
      //   53: astore 4
      //   55: ldc 69
      //   57: new 71	java/lang/StringBuilder
      //   60: dup
      //   61: invokespecial 72	java/lang/StringBuilder:<init>	()V
      //   64: ldc 74
      //   66: invokevirtual 78	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   69: aload_3
      //   70: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   73: invokevirtual 85	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   76: aload 4
      //   78: invokestatic 91	com/android/gallery3d/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   81: pop
      //   82: aconst_null
      //   83: astore 6
      //   85: goto -51 -> 34
      //   88: aload_0
      //   89: iconst_1
      //   90: putfield 30	com/android/gallery3d/util/JobLimiter$JobWrapper:mState	I
      //   93: aload_0
      //   94: getfield 34	com/android/gallery3d/util/JobLimiter$JobWrapper:mListener	Lcom/android/gallery3d/util/FutureListener;
      //   97: astore 8
      //   99: aload_0
      //   100: aconst_null
      //   101: putfield 34	com/android/gallery3d/util/JobLimiter$JobWrapper:mListener	Lcom/android/gallery3d/util/FutureListener;
      //   104: aload_0
      //   105: aconst_null
      //   106: putfield 32	com/android/gallery3d/util/JobLimiter$JobWrapper:mJob	Lcom/android/gallery3d/util/ThreadPool$Job;
      //   109: aload_0
      //   110: aload 6
      //   112: putfield 41	com/android/gallery3d/util/JobLimiter$JobWrapper:mResult	Ljava/lang/Object;
      //   115: aload_0
      //   116: invokevirtual 44	java/lang/Object:notifyAll	()V
      //   119: aload_0
      //   120: monitorexit
      //   121: aload 8
      //   123: ifnull +21 -> 144
      //   126: aload 8
      //   128: aload_0
      //   129: invokeinterface 50 2 0
      //   134: aload 6
      //   136: areturn
      //   137: astore 7
      //   139: aload_0
      //   140: monitorexit
      //   141: aload 7
      //   143: athrow
      //   144: aload 6
      //   146: areturn
      //
      // Exception table:
      //   from	to	target	type
      //   2	12	48	finally
      //   14	21	48	finally
      //   49	51	48	finally
      //   21	30	53	java/lang/Throwable
      //   36	46	137	finally
      //   88	121	137	finally
      //   139	141	137	finally
    }

    public void setFuture(Future<T> paramFuture)
    {
      monitorenter;
      try
      {
        int i = this.mState;
        if (i != 0)
          return;
      }
      finally
      {
        monitorexit;
      }
    }

    public void waitDone()
    {
      get();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.JobLimiter
 * JD-Core Version:    0.5.4
 */