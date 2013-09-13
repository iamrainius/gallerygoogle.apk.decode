package com.android.gallery3d.util;

import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool
{
  public static final JobContext JOB_CONTEXT_STUB = new JobContextStub(null);
  ResourceCounter mCpuCounter = new ResourceCounter(2);
  private final Executor mExecutor;
  ResourceCounter mNetworkCounter = new ResourceCounter(2);

  public ThreadPool()
  {
    this(4, 8);
  }

  public ThreadPool(int paramInt1, int paramInt2)
  {
    this.mExecutor = new ThreadPoolExecutor(paramInt1, paramInt2, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new PriorityThreadFactory("thread-pool", 10));
  }

  public <T> Future<T> submit(Job<T> paramJob)
  {
    return submit(paramJob, null);
  }

  public <T> Future<T> submit(Job<T> paramJob, FutureListener<T> paramFutureListener)
  {
    Worker localWorker = new Worker(paramJob, paramFutureListener);
    this.mExecutor.execute(localWorker);
    return localWorker;
  }

  public static abstract interface CancelListener
  {
    public abstract void onCancel();
  }

  public static abstract interface Job<T>
  {
    public abstract T run(ThreadPool.JobContext paramJobContext);
  }

  public static abstract interface JobContext
  {
    public abstract boolean isCancelled();

    public abstract void setCancelListener(ThreadPool.CancelListener paramCancelListener);

    public abstract boolean setMode(int paramInt);
  }

  private static class JobContextStub
    implements ThreadPool.JobContext
  {
    public boolean isCancelled()
    {
      return false;
    }

    public void setCancelListener(ThreadPool.CancelListener paramCancelListener)
    {
    }

    public boolean setMode(int paramInt)
    {
      return true;
    }
  }

  private static class ResourceCounter
  {
    public int value;

    public ResourceCounter(int paramInt)
    {
      this.value = paramInt;
    }
  }

  private class Worker<T>
    implements Runnable, Future<T>, ThreadPool.JobContext
  {
    private ThreadPool.CancelListener mCancelListener;
    private volatile boolean mIsCancelled;
    private boolean mIsDone;
    private ThreadPool.Job<T> mJob;
    private FutureListener<T> mListener;
    private int mMode;
    private T mResult;
    private ThreadPool.ResourceCounter mWaitOnResource;

    public Worker(FutureListener<T> arg2)
    {
      Object localObject1;
      this.mJob = localObject1;
      Object localObject2;
      this.mListener = localObject2;
    }

    // ERROR //
    private boolean acquireResource(ThreadPool.ResourceCounter paramResourceCounter)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 48	com/android/gallery3d/util/ThreadPool$Worker:mIsCancelled	Z
      //   6: ifeq +12 -> 18
      //   9: aload_0
      //   10: aconst_null
      //   11: putfield 50	com/android/gallery3d/util/ThreadPool$Worker:mWaitOnResource	Lcom/android/gallery3d/util/ThreadPool$ResourceCounter;
      //   14: aload_0
      //   15: monitorexit
      //   16: iconst_0
      //   17: ireturn
      //   18: aload_0
      //   19: aload_1
      //   20: putfield 50	com/android/gallery3d/util/ThreadPool$Worker:mWaitOnResource	Lcom/android/gallery3d/util/ThreadPool$ResourceCounter;
      //   23: aload_0
      //   24: monitorexit
      //   25: aload_1
      //   26: monitorenter
      //   27: aload_1
      //   28: getfield 55	com/android/gallery3d/util/ThreadPool$ResourceCounter:value	I
      //   31: ifle +31 -> 62
      //   34: aload_1
      //   35: iconst_m1
      //   36: aload_1
      //   37: getfield 55	com/android/gallery3d/util/ThreadPool$ResourceCounter:value	I
      //   40: iadd
      //   41: putfield 55	com/android/gallery3d/util/ThreadPool$ResourceCounter:value	I
      //   44: aload_1
      //   45: monitorexit
      //   46: aload_0
      //   47: monitorenter
      //   48: aload_0
      //   49: aconst_null
      //   50: putfield 50	com/android/gallery3d/util/ThreadPool$Worker:mWaitOnResource	Lcom/android/gallery3d/util/ThreadPool$ResourceCounter;
      //   53: aload_0
      //   54: monitorexit
      //   55: iconst_1
      //   56: ireturn
      //   57: astore_2
      //   58: aload_0
      //   59: monitorexit
      //   60: aload_2
      //   61: athrow
      //   62: aload_1
      //   63: invokevirtual 58	java/lang/Object:wait	()V
      //   66: aload_1
      //   67: monitorexit
      //   68: goto -68 -> 0
      //   71: astore_3
      //   72: aload_1
      //   73: monitorexit
      //   74: aload_3
      //   75: athrow
      //   76: astore 5
      //   78: aload_0
      //   79: monitorexit
      //   80: aload 5
      //   82: athrow
      //   83: astore 4
      //   85: goto -19 -> 66
      //
      // Exception table:
      //   from	to	target	type
      //   2	16	57	finally
      //   18	25	57	finally
      //   58	60	57	finally
      //   27	46	71	finally
      //   62	66	71	finally
      //   66	68	71	finally
      //   72	74	71	finally
      //   48	55	76	finally
      //   78	80	76	finally
      //   62	66	83	java/lang/InterruptedException
    }

    private ThreadPool.ResourceCounter modeToCounter(int paramInt)
    {
      if (paramInt == 1)
        return ThreadPool.this.mCpuCounter;
      if (paramInt == 2)
        return ThreadPool.this.mNetworkCounter;
      return null;
    }

    private void releaseResource(ThreadPool.ResourceCounter paramResourceCounter)
    {
      monitorenter;
      try
      {
        paramResourceCounter.value = (1 + paramResourceCounter.value);
        paramResourceCounter.notifyAll();
        return;
      }
      finally
      {
        monitorexit;
      }
    }

    // ERROR //
    public void cancel()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 48	com/android/gallery3d/util/ThreadPool$Worker:mIsCancelled	Z
      //   6: istore_2
      //   7: iload_2
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: aload_0
      //   15: iconst_1
      //   16: putfield 48	com/android/gallery3d/util/ThreadPool$Worker:mIsCancelled	Z
      //   19: aload_0
      //   20: getfield 50	com/android/gallery3d/util/ThreadPool$Worker:mWaitOnResource	Lcom/android/gallery3d/util/ThreadPool$ResourceCounter;
      //   23: ifnull +19 -> 42
      //   26: aload_0
      //   27: getfield 50	com/android/gallery3d/util/ThreadPool$Worker:mWaitOnResource	Lcom/android/gallery3d/util/ThreadPool$ResourceCounter;
      //   30: astore_3
      //   31: aload_3
      //   32: monitorenter
      //   33: aload_0
      //   34: getfield 50	com/android/gallery3d/util/ThreadPool$Worker:mWaitOnResource	Lcom/android/gallery3d/util/ThreadPool$ResourceCounter;
      //   37: invokevirtual 73	java/lang/Object:notifyAll	()V
      //   40: aload_3
      //   41: monitorexit
      //   42: aload_0
      //   43: getfield 76	com/android/gallery3d/util/ThreadPool$Worker:mCancelListener	Lcom/android/gallery3d/util/ThreadPool$CancelListener;
      //   46: ifnull -35 -> 11
      //   49: aload_0
      //   50: getfield 76	com/android/gallery3d/util/ThreadPool$Worker:mCancelListener	Lcom/android/gallery3d/util/ThreadPool$CancelListener;
      //   53: invokeinterface 81 1 0
      //   58: goto -47 -> 11
      //   61: astore_1
      //   62: aload_0
      //   63: monitorexit
      //   64: aload_1
      //   65: athrow
      //   66: astore 4
      //   68: aload_3
      //   69: monitorexit
      //   70: aload 4
      //   72: athrow
      //
      // Exception table:
      //   from	to	target	type
      //   2	7	61	finally
      //   14	33	61	finally
      //   42	58	61	finally
      //   70	73	61	finally
      //   33	42	66	finally
      //   68	70	66	finally
    }

    public T get()
    {
      monitorenter;
      while (true)
        try
        {
          boolean bool = this.mIsDone;
          if (bool)
            break label38;
        }
        finally
        {
          monitorexit;
        }
      label38: Object localObject2 = this.mResult;
      monitorexit;
      return localObject2;
    }

    public boolean isCancelled()
    {
      return this.mIsCancelled;
    }

    public boolean isDone()
    {
      monitorenter;
      try
      {
        boolean bool = this.mIsDone;
        monitorexit;
        return bool;
      }
      finally
      {
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }

    public void run()
    {
      boolean bool = setMode(1);
      Object localObject1 = null;
      if (bool);
      try
      {
        Object localObject3 = this.mJob.run(this);
        localObject1 = localObject3;
        monitorenter;
      }
      catch (Throwable localThrowable)
      {
        try
        {
          setMode(0);
          this.mResult = localObject1;
          this.mIsDone = true;
          super.notifyAll();
          monitorexit;
          if (this.mListener != null);
          return;
          localThrowable = localThrowable;
          Log.w("Worker", "Exception in running a job", localThrowable);
        }
        finally
        {
          monitorexit;
        }
      }
    }

    public void setCancelListener(ThreadPool.CancelListener paramCancelListener)
    {
      monitorenter;
      try
      {
        this.mCancelListener = paramCancelListener;
        if ((this.mIsCancelled) && (this.mCancelListener != null))
          this.mCancelListener.onCancel();
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

    public boolean setMode(int paramInt)
    {
      ThreadPool.ResourceCounter localResourceCounter1 = modeToCounter(this.mMode);
      if (localResourceCounter1 != null)
        releaseResource(localResourceCounter1);
      this.mMode = 0;
      ThreadPool.ResourceCounter localResourceCounter2 = modeToCounter(paramInt);
      if (localResourceCounter2 != null)
      {
        if (!acquireResource(localResourceCounter2))
          return false;
        this.mMode = paramInt;
      }
      return true;
    }

    public void waitDone()
    {
      get();
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.ThreadPool
 * JD-Core Version:    0.5.4
 */