package com.android.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.gallery3d.common.ApiHelper;
import java.io.FileDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@TargetApi(11)
public class EffectsRecorder
{
  private static Class<?> sClassFilter;
  private static Class<?> sClsGraphEnvironment;
  private static Class<?> sClsGraphRunner;
  private static Class<?> sClsLearningDoneListener;
  private static Class<?> sClsOnRecordingDoneListener;
  private static Class<?> sClsOnRunnerDoneListener;
  private static Class<?> sClsSurfaceTextureSourceListener;
  private static Constructor<?> sCtGraphEnvironment;
  private static Constructor<?> sCtPoint;
  private static Constructor<?> sCtQuad;
  private static EffectsRecorder sEffectsRecorder;
  private static int sEffectsRecorderIndex;
  private static Method sFilterContextGetGLEnvironment;
  private static Method sFilterGraphGetFilter;
  private static Method sFilterGraphTearDown;
  private static Method sFilterIsAvailable;
  private static Method sFilterSetInputValue;
  private static Method sGLEnvironmentActivate;
  private static Method sGLEnvironmentDeactivate;
  private static Method sGLEnvironmentIsActive;
  private static Method sGraphEnvironmentAddReferences;
  private static Method sGraphEnvironmentCreateGLEnvironment;
  private static Method sGraphEnvironmentGetContext;
  private static Method sGraphEnvironmentGetRunner;
  private static Method sGraphEnvironmentLoadGraph;
  private static Method sGraphRunnerGetError;
  private static Method sGraphRunnerGetGraph;
  private static Method sGraphRunnerRun;
  private static Method sGraphRunnerSetDoneCallback;
  private static Method sGraphRunnerStop;
  private static Method sLearningDoneListenerOnLearningDone;
  private static Method sObjectEquals;
  private static Method sObjectToString;
  private static Method sOnRecordingDoneListenerOnRecordingDone;
  private static Method sOnRunnerDoneListenerOnRunnerDone;
  private static boolean sReflectionInited = false;
  private static Method sSurfaceTextureSourceListenerOnSurfaceTextureSourceReady;
  private static Method sSurfaceTextureTargetDisconnect;
  private CameraManager.CameraProxy mCameraDevice;
  private int mCameraDisplayOrientation;
  private int mCameraFacing = 0;
  private double mCaptureRate = 0.0D;
  private Context mContext;
  private int mCurrentEffect = 0;
  private int mEffect = 0;
  private Object mEffectParameter;
  private EffectsListener mEffectsListener;
  private MediaRecorder.OnErrorListener mErrorListener;
  private FileDescriptor mFd;
  private Object mGraphEnv;
  private int mGraphId;
  private Handler mHandler;
  private MediaRecorder.OnInfoListener mInfoListener;
  private Object mLearningDoneListener;
  private boolean mLogVerbose = Log.isLoggable("EffectsRecorder", 2);
  private int mMaxDurationMs = 0;
  private long mMaxFileSize = 0L;
  private Object mOldRunner = null;
  private int mOrientationHint = 0;
  private String mOutputFile;
  private int mPreviewHeight;
  private SurfaceTexture mPreviewSurfaceTexture;
  private int mPreviewWidth;
  private CamcorderProfile mProfile;
  private Object mRecordingDoneListener;
  private Object mRunner = null;
  private Object mRunnerDoneCallback;
  private SoundClips.Player mSoundPlayer;
  private Object mSourceReadyCallback;
  private int mState = 0;
  private SurfaceTexture mTextureSource;

  static
  {
    try
    {
      sClassFilter = Class.forName("android.filterfw.core.Filter");
      sFilterIsAvailable = sClassFilter.getMethod("isAvailable", new Class[] { String.class });
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Log.v("EffectsRecorder", "Can't find the class android.filterfw.core.Filter");
      return;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Log.v("EffectsRecorder", "Can't find the method Filter.isAvailable");
    }
  }

  public EffectsRecorder(Context paramContext)
  {
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "EffectsRecorder created (" + this + ")");
    if (!sReflectionInited);
    try
    {
      sFilterSetInputValue = sClassFilter.getMethod("setInputValue", new Class[] { String.class, Object.class });
      Class localClass1 = Class.forName("android.filterfw.geometry.Point");
      Class[] arrayOfClass5 = new Class[2];
      arrayOfClass5[0] = Float.TYPE;
      arrayOfClass5[1] = Float.TYPE;
      sCtPoint = localClass1.getConstructor(arrayOfClass5);
      sCtQuad = Class.forName("android.filterfw.geometry.Quad").getConstructor(new Class[] { localClass1, localClass1, localClass1, localClass1 });
      Class localClass2 = Class.forName("android.filterpacks.videoproc.BackDropperFilter");
      sClsLearningDoneListener = Class.forName("android.filterpacks.videoproc.BackDropperFilter$LearningDoneListener");
      sLearningDoneListenerOnLearningDone = sClsLearningDoneListener.getMethod("onLearningDone", new Class[] { localClass2 });
      sObjectEquals = Object.class.getMethod("equals", new Class[] { Object.class });
      sObjectToString = Object.class.getMethod("toString", new Class[0]);
      sClsOnRunnerDoneListener = Class.forName("android.filterfw.core.GraphRunner$OnRunnerDoneListener");
      Class localClass3 = sClsOnRunnerDoneListener;
      Class[] arrayOfClass6 = new Class[1];
      arrayOfClass6[0] = Integer.TYPE;
      sOnRunnerDoneListenerOnRunnerDone = localClass3.getMethod("onRunnerDone", arrayOfClass6);
      sClsGraphRunner = Class.forName("android.filterfw.core.GraphRunner");
      sGraphRunnerGetGraph = sClsGraphRunner.getMethod("getGraph", new Class[0]);
      Class localClass4 = sClsGraphRunner;
      Class[] arrayOfClass7 = new Class[1];
      arrayOfClass7[0] = sClsOnRunnerDoneListener;
      sGraphRunnerSetDoneCallback = localClass4.getMethod("setDoneCallback", arrayOfClass7);
      sGraphRunnerRun = sClsGraphRunner.getMethod("run", new Class[0]);
      sGraphRunnerGetError = sClsGraphRunner.getMethod("getError", new Class[0]);
      sGraphRunnerStop = sClsGraphRunner.getMethod("stop", new Class[0]);
      Class localClass5 = Class.forName("android.filterfw.core.FilterContext");
      sFilterContextGetGLEnvironment = localClass5.getMethod("getGLEnvironment", new Class[0]);
      Class localClass6 = Class.forName("android.filterfw.core.FilterGraph");
      sFilterGraphGetFilter = localClass6.getMethod("getFilter", new Class[] { String.class });
      sFilterGraphTearDown = localClass6.getMethod("tearDown", new Class[] { localClass5 });
      sClsGraphEnvironment = Class.forName("android.filterfw.GraphEnvironment");
      sCtGraphEnvironment = sClsGraphEnvironment.getConstructor(new Class[0]);
      sGraphEnvironmentCreateGLEnvironment = sClsGraphEnvironment.getMethod("createGLEnvironment", new Class[0]);
      Class localClass7 = sClsGraphEnvironment;
      Class[] arrayOfClass8 = new Class[2];
      arrayOfClass8[0] = Integer.TYPE;
      arrayOfClass8[1] = Integer.TYPE;
      sGraphEnvironmentGetRunner = localClass7.getMethod("getRunner", arrayOfClass8);
      sGraphEnvironmentAddReferences = sClsGraphEnvironment.getMethod("addReferences", new Class[] { [Ljava.lang.Object.class });
      Class localClass8 = sClsGraphEnvironment;
      Class[] arrayOfClass9 = new Class[2];
      arrayOfClass9[0] = Context.class;
      arrayOfClass9[1] = Integer.TYPE;
      sGraphEnvironmentLoadGraph = localClass8.getMethod("loadGraph", arrayOfClass9);
      sGraphEnvironmentGetContext = sClsGraphEnvironment.getMethod("getContext", new Class[0]);
      Class localClass9 = Class.forName("android.filterfw.core.GLEnvironment");
      sGLEnvironmentIsActive = localClass9.getMethod("isActive", new Class[0]);
      sGLEnvironmentActivate = localClass9.getMethod("activate", new Class[0]);
      sGLEnvironmentDeactivate = localClass9.getMethod("deactivate", new Class[0]);
      sSurfaceTextureTargetDisconnect = Class.forName("android.filterpacks.videosrc.SurfaceTextureTarget").getMethod("disconnect", new Class[] { localClass5 });
      sClsOnRecordingDoneListener = Class.forName("android.filterpacks.videosink.MediaEncoderFilter$OnRecordingDoneListener");
      sOnRecordingDoneListenerOnRecordingDone = sClsOnRecordingDoneListener.getMethod("onRecordingDone", new Class[0]);
      sClsSurfaceTextureSourceListener = Class.forName("android.filterpacks.videosrc.SurfaceTextureSource$SurfaceTextureSourceListener");
      sSurfaceTextureSourceListenerOnSurfaceTextureSourceReady = sClsSurfaceTextureSourceListener.getMethod("onSurfaceTextureSourceReady", new Class[] { SurfaceTexture.class });
      sReflectionInited = true;
      sEffectsRecorderIndex = 1 + sEffectsRecorderIndex;
      Log.v("EffectsRecorder", "Current effects recorder index is " + sEffectsRecorderIndex);
      sEffectsRecorder = this;
      SerializableInvocationHandler localSerializableInvocationHandler = new SerializableInvocationHandler(sEffectsRecorderIndex);
      ClassLoader localClassLoader1 = sClsLearningDoneListener.getClassLoader();
      Class[] arrayOfClass1 = new Class[1];
      arrayOfClass1[0] = sClsLearningDoneListener;
      this.mLearningDoneListener = Proxy.newProxyInstance(localClassLoader1, arrayOfClass1, localSerializableInvocationHandler);
      ClassLoader localClassLoader2 = sClsOnRunnerDoneListener.getClassLoader();
      Class[] arrayOfClass2 = new Class[1];
      arrayOfClass2[0] = sClsOnRunnerDoneListener;
      this.mRunnerDoneCallback = Proxy.newProxyInstance(localClassLoader2, arrayOfClass2, localSerializableInvocationHandler);
      ClassLoader localClassLoader3 = sClsSurfaceTextureSourceListener.getClassLoader();
      Class[] arrayOfClass3 = new Class[1];
      arrayOfClass3[0] = sClsSurfaceTextureSourceListener;
      this.mSourceReadyCallback = Proxy.newProxyInstance(localClassLoader3, arrayOfClass3, localSerializableInvocationHandler);
      ClassLoader localClassLoader4 = sClsOnRecordingDoneListener.getClassLoader();
      Class[] arrayOfClass4 = new Class[1];
      arrayOfClass4[0] = sClsOnRecordingDoneListener;
      this.mRecordingDoneListener = Proxy.newProxyInstance(localClassLoader4, arrayOfClass4, localSerializableInvocationHandler);
      this.mContext = paramContext;
      this.mHandler = new Handler(Looper.getMainLooper());
      this.mSoundPlayer = SoundClips.getPlayer(paramContext);
      return;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private Object getConstant(Class<?> paramClass, String paramString)
  {
    try
    {
      Object localObject = paramClass.getDeclaredField(paramString).get(null);
      return localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private Object getContextGLEnvironment(Object paramObject)
  {
    try
    {
      Object localObject = sFilterContextGetGLEnvironment.invoke(sGraphEnvironmentGetContext.invoke(paramObject, new Object[0]), new Object[0]);
      return localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private Object getGraphFilter(Object paramObject, String paramString)
  {
    try
    {
      Object localObject = sFilterGraphGetFilter.invoke(sGraphRunnerGetGraph.invoke(paramObject, new Object[0]), new Object[] { paramString });
      return localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private void getGraphTearDown(Object paramObject1, Object paramObject2)
  {
    try
    {
      sFilterGraphTearDown.invoke(sGraphRunnerGetGraph.invoke(paramObject1, new Object[0]), new Object[] { paramObject2 });
      return;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private void initializeEffect(boolean paramBoolean)
  {
    monitorenter;
    if (!paramBoolean);
    try
    {
      if ((this.mCurrentEffect == this.mEffect) && (this.mCurrentEffect != 2))
        break label485;
      Object localObject2 = this.mGraphEnv;
      Method localMethod1 = sGraphEnvironmentAddReferences;
      Object[] arrayOfObject1 = new Object[1];
      Object[] arrayOfObject2 = new Object[8];
      arrayOfObject2[0] = "previewSurfaceTexture";
      arrayOfObject2[1] = this.mPreviewSurfaceTexture;
      arrayOfObject2[2] = "previewWidth";
      arrayOfObject2[3] = Integer.valueOf(this.mPreviewWidth);
      arrayOfObject2[4] = "previewHeight";
      arrayOfObject2[5] = Integer.valueOf(this.mPreviewHeight);
      arrayOfObject2[6] = "orientation";
      arrayOfObject2[7] = Integer.valueOf(this.mOrientationHint);
      arrayOfObject1[0] = arrayOfObject2;
      invoke(localObject2, localMethod1, arrayOfObject1);
      if ((this.mState == 3) || (this.mState == 2))
        sendMessage(this.mCurrentEffect, 2);
      throw new RuntimeException("Unknown effect ID" + this.mEffect + "!");
    }
    finally
    {
      monitorexit;
    }
    Object localObject7 = this.mGraphEnv;
    Method localMethod5 = sGraphEnvironmentLoadGraph;
    Object[] arrayOfObject6 = new Object[2];
    arrayOfObject6[0] = this.mContext;
    arrayOfObject6[1] = Integer.valueOf(2131230724);
    this.mGraphId = ((Integer)invoke(localObject7, localMethod5, arrayOfObject6)).intValue();
    label285: this.mCurrentEffect = this.mEffect;
    this.mOldRunner = this.mRunner;
    Object localObject4 = this.mGraphEnv;
    Method localMethod3 = sGraphEnvironmentGetRunner;
    Object[] arrayOfObject4 = new Object[2];
    arrayOfObject4[0] = Integer.valueOf(this.mGraphId);
    arrayOfObject4[1] = getConstant(sClsGraphEnvironment, "MODE_ASYNCHRONOUS");
    this.mRunner = invoke(localObject4, localMethod3, arrayOfObject4);
    Object localObject5 = this.mRunner;
    Method localMethod4 = sGraphRunnerSetDoneCallback;
    Object[] arrayOfObject5 = new Object[1];
    arrayOfObject5[0] = this.mRunnerDoneCallback;
    invoke(localObject5, localMethod4, arrayOfObject5);
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "New runner: " + this.mRunner + ". Old runner: " + this.mOldRunner);
    if ((this.mState == 3) || (this.mState == 2))
    {
      this.mCameraDevice.stopPreview();
      this.mCameraDevice.setPreviewTextureAsync(null);
      invoke(this.mOldRunner, sGraphRunnerStop);
    }
    label485: switch (this.mCurrentEffect)
    {
    default:
    case 1:
    case 2:
    }
    while (true)
    {
      setFaceDetectOrientation();
      setRecordingOrientation();
      monitorexit;
      return;
      sendMessage(2, 0);
      Object localObject3 = this.mGraphEnv;
      Method localMethod2 = sGraphEnvironmentLoadGraph;
      Object[] arrayOfObject3 = new Object[2];
      arrayOfObject3[0] = this.mContext;
      arrayOfObject3[1] = Integer.valueOf(2131230720);
      this.mGraphId = ((Integer)invoke(localObject3, localMethod2, arrayOfObject3)).intValue();
      break label285:
      tryEnableVideoStabilization(true);
      setInputValue(getGraphFilter(this.mRunner, "goofyrenderer"), "currentEffect", Integer.valueOf(((Integer)this.mEffectParameter).intValue()));
      continue;
      tryEnableVideoStabilization(false);
      Object localObject6 = getGraphFilter(this.mRunner, "background");
      if (ApiHelper.HAS_EFFECTS_RECORDING_CONTEXT_INPUT)
        setInputValue(localObject6, "context", this.mContext);
      setInputValue(localObject6, "sourceUrl", this.mEffectParameter);
      if (this.mCameraFacing != 1)
        continue;
      setInputValue(getGraphFilter(this.mRunner, "replacer"), "mirrorBg", Boolean.valueOf(true));
      if (!this.mLogVerbose)
        continue;
      Log.v("EffectsRecorder", "Setting the background to be mirrored");
    }
  }

  private void initializeFilterFramework()
  {
    this.mGraphEnv = newInstance(sCtGraphEnvironment);
    invoke(this.mGraphEnv, sGraphEnvironmentCreateGLEnvironment);
    int i = this.mProfile.videoFrameWidth;
    int j = this.mProfile.videoFrameHeight;
    if ((this.mCameraDisplayOrientation == 90) || (this.mCameraDisplayOrientation == 270))
    {
      int k = i;
      i = j;
      j = k;
    }
    Object localObject = this.mGraphEnv;
    Method localMethod = sGraphEnvironmentAddReferences;
    Object[] arrayOfObject1 = new Object[1];
    Object[] arrayOfObject2 = new Object[12];
    arrayOfObject2[0] = "textureSourceCallback";
    arrayOfObject2[1] = this.mSourceReadyCallback;
    arrayOfObject2[2] = "recordingWidth";
    arrayOfObject2[3] = Integer.valueOf(i);
    arrayOfObject2[4] = "recordingHeight";
    arrayOfObject2[5] = Integer.valueOf(j);
    arrayOfObject2[6] = "recordingProfile";
    arrayOfObject2[7] = this.mProfile;
    arrayOfObject2[8] = "learningDoneListener";
    arrayOfObject2[9] = this.mLearningDoneListener;
    arrayOfObject2[10] = "recordingDoneListener";
    arrayOfObject2[11] = this.mRecordingDoneListener;
    arrayOfObject1[0] = arrayOfObject2;
    invoke(localObject, localMethod, arrayOfObject1);
    this.mRunner = null;
    this.mGraphId = -1;
    this.mCurrentEffect = 0;
  }

  private Object invoke(Object paramObject, Method paramMethod)
  {
    try
    {
      Object localObject = paramMethod.invoke(paramObject, new Object[0]);
      return localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
  {
    try
    {
      Object localObject = paramMethod.invoke(paramObject, paramArrayOfObject);
      return localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private Object invokeObjectEquals(Object paramObject, Object[] paramArrayOfObject)
  {
    Object localObject = paramArrayOfObject[0];
    boolean bool = false;
    if (paramObject == localObject)
      bool = true;
    return Boolean.valueOf(bool);
  }

  private Object invokeObjectToString()
  {
    return "Proxy-" + super.toString();
  }

  private void invokeOnLearningDone()
  {
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "Learning done callback triggered");
    sendMessage(2, 1);
    enable3ALocks(true);
  }

  private void invokeOnRecordingDone()
  {
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "Recording done callback triggered");
    sendMessage(0, 4);
  }

  private void invokeOnRunnerDone(Object[] paramArrayOfObject)
  {
    int i = ((Integer)paramArrayOfObject[0]).intValue();
    monitorenter;
    while (true)
    {
      try
      {
        if (this.mLogVerbose)
          Log.v("EffectsRecorder", "Graph runner done (" + this + ", mRunner " + this.mRunner + ", mOldRunner " + this.mOldRunner + ")");
        if (i == ((Integer)getConstant(sClsGraphRunner, "RESULT_ERROR")).intValue())
        {
          Log.e("EffectsRecorder", "Error running filter graph!");
          if (this.mRunner == null)
            break label364;
          localException = (Exception)invoke(this.mRunner, sGraphRunnerGetError);
          label128: raiseError(localException);
        }
        if (this.mOldRunner != null)
        {
          if (this.mLogVerbose)
            Log.v("EffectsRecorder", "Tearing down old graph.");
          Object localObject2 = getContextGLEnvironment(this.mGraphEnv);
          if ((localObject2 != null) && (!((Boolean)invoke(localObject2, sGLEnvironmentIsActive)).booleanValue()))
            invoke(localObject2, sGLEnvironmentActivate);
          getGraphTearDown(this.mOldRunner, invoke(this.mGraphEnv, sGraphEnvironmentGetContext));
          if ((localObject2 != null) && (((Boolean)invoke(localObject2, sGLEnvironmentIsActive)).booleanValue()))
            invoke(localObject2, sGLEnvironmentDeactivate);
          this.mOldRunner = null;
        }
        if ((this.mState == 3) || (this.mState == 2))
        {
          if (this.mLogVerbose)
            Log.v("EffectsRecorder", "Previous effect halted. Running graph again. state: " + this.mState);
          tryEnable3ALocks(false);
          if ((i == ((Integer)getConstant(sClsGraphRunner, "RESULT_ERROR")).intValue()) && (this.mCurrentEffect == 2))
            sendMessage(2, 0);
          invoke(this.mRunner, sGraphRunnerRun);
        }
        do
        {
          return;
          label364: if (this.mOldRunner == null)
            break label434;
          localException = (Exception)invoke(this.mOldRunner, sGraphRunnerGetError);
          break label128:
        }
        while (this.mState == 5);
        if (this.mLogVerbose)
          Log.v("EffectsRecorder", "Runner halted, restoring direct preview");
        tryEnable3ALocks(false);
      }
      finally
      {
        monitorexit;
      }
      label434: Exception localException = null;
    }
  }

  private void invokeOnSurfaceTextureSourceReady(Object[] paramArrayOfObject)
  {
    SurfaceTexture localSurfaceTexture = (SurfaceTexture)paramArrayOfObject[0];
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "SurfaceTexture ready callback received");
    monitorenter;
    try
    {
      this.mTextureSource = localSurfaceTexture;
      if (this.mState == 0)
      {
        if (this.mLogVerbose)
          Log.v("EffectsRecorder", "Ready callback: Already stopped, skipping.");
        return;
      }
      if (this.mState == 5)
      {
        if (this.mLogVerbose)
          Log.v("EffectsRecorder", "Ready callback: Already released, skipping.");
        return;
      }
    }
    finally
    {
      monitorexit;
    }
    if (localSurfaceTexture == null)
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Ready callback: source null! Looks like graph was closed!");
      if ((this.mState == 3) || (this.mState == 2) || (this.mState == 4))
      {
        if (this.mLogVerbose)
          Log.v("EffectsRecorder", "Ready callback: State: " + this.mState + ". stopCameraPreview");
        stopCameraPreview();
      }
      monitorexit;
      return;
    }
    tryEnable3ALocks(true);
    this.mCameraDevice.stopPreview();
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "Runner active, connecting effects preview");
    this.mCameraDevice.setPreviewTextureAsync(this.mTextureSource);
    this.mCameraDevice.startPreviewAsync();
    tryEnable3ALocks(false);
    this.mState = 3;
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "Start preview/effect switch complete");
    sendMessage(this.mCurrentEffect, 5);
    monitorexit;
  }

  private Object newInstance(Constructor<?> paramConstructor)
  {
    try
    {
      Object localObject = paramConstructor.newInstance(new Object[0]);
      return localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private Object newInstance(Constructor<?> paramConstructor, Object[] paramArrayOfObject)
  {
    try
    {
      Object localObject = paramConstructor.newInstance(paramArrayOfObject);
      return localObject;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private void raiseError(Exception paramException)
  {
    if (this.mEffectsListener == null)
      return;
    this.mHandler.post(new Runnable(paramException)
    {
      public void run()
      {
        if (EffectsRecorder.this.mFd != null)
        {
          EffectsRecorder.this.mEffectsListener.onEffectsError(this.val$exception, null);
          return;
        }
        EffectsRecorder.this.mEffectsListener.onEffectsError(this.val$exception, EffectsRecorder.this.mOutputFile);
      }
    });
  }

  private void sendMessage(int paramInt1, int paramInt2)
  {
    if (this.mEffectsListener == null)
      return;
    this.mHandler.post(new Runnable(paramInt1, paramInt2)
    {
      public void run()
      {
        EffectsRecorder.this.mEffectsListener.onEffectsUpdate(this.val$effect, this.val$msg);
      }
    });
  }

  private void setFaceDetectOrientation()
  {
    if (this.mCurrentEffect != 1)
      return;
    Object localObject1 = getGraphFilter(this.mRunner, "rotate");
    Object localObject2 = getGraphFilter(this.mRunner, "metarotate");
    setInputValue(localObject1, "rotation", Integer.valueOf(this.mOrientationHint));
    setInputValue(localObject2, "rotation", Integer.valueOf((360 - this.mOrientationHint) % 360));
  }

  private void setInputValue(Object paramObject1, String paramString, Object paramObject2)
  {
    try
    {
      sFilterSetInputValue.invoke(paramObject1, new Object[] { paramString, paramObject2 });
      return;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }

  private void setRecordingOrientation()
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if ((this.mState != 4) && (this.mRunner != null))
    {
      Constructor localConstructor1 = sCtPoint;
      Object[] arrayOfObject1 = new Object[2];
      arrayOfObject1[0] = Integer.valueOf(0);
      arrayOfObject1[1] = Integer.valueOf(0);
      localObject1 = newInstance(localConstructor1, arrayOfObject1);
      Constructor localConstructor2 = sCtPoint;
      Object[] arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = Integer.valueOf(1);
      arrayOfObject2[1] = Integer.valueOf(0);
      localObject2 = newInstance(localConstructor2, arrayOfObject2);
      Constructor localConstructor3 = sCtPoint;
      Object[] arrayOfObject3 = new Object[2];
      arrayOfObject3[0] = Integer.valueOf(0);
      arrayOfObject3[1] = Integer.valueOf(1);
      localObject3 = newInstance(localConstructor3, arrayOfObject3);
      Constructor localConstructor4 = sCtPoint;
      Object[] arrayOfObject4 = new Object[2];
      arrayOfObject4[0] = Integer.valueOf(1);
      arrayOfObject4[1] = Integer.valueOf(1);
      localObject4 = newInstance(localConstructor4, arrayOfObject4);
      if (this.mCameraFacing != 0)
        break label216;
    }
    for (Object localObject5 = newInstance(sCtQuad, new Object[] { localObject1, localObject2, localObject3, localObject4 }); ; localObject5 = newInstance(sCtQuad, new Object[] { localObject3, localObject4, localObject1, localObject2 }))
      while (true)
      {
        setInputValue(getGraphFilter(this.mRunner, "recorder"), "inputRegion", localObject5);
        return;
        label216: if ((this.mOrientationHint != 0) && (this.mOrientationHint != 180))
          break;
        localObject5 = newInstance(sCtQuad, new Object[] { localObject2, localObject1, localObject4, localObject3 });
      }
  }

  public void disconnectCamera()
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Disconnecting the effects from Camera");
      stopCameraPreview();
      this.mCameraDevice = null;
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  public void disconnectDisplay()
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Disconnecting the graph from the SurfaceTexture");
      Object localObject2 = getGraphFilter(this.mRunner, "display");
      Method localMethod = sSurfaceTextureTargetDisconnect;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = invoke(this.mGraphEnv, sGraphEnvironmentGetContext);
      invoke(localObject2, localMethod, arrayOfObject);
      return;
    }
    finally
    {
      monitorexit;
    }
  }

  void enable3ALocks(boolean paramBoolean)
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Enable3ALocks");
      if (this.mCameraDevice == null)
      {
        Log.d("EffectsRecorder", "Camera already null. Not enabling 3A locks.");
        return;
      }
      this.mCameraDevice.getParameters();
      throw new RuntimeException("Attempt to lock 3A on camera with no locking support!");
    }
    finally
    {
      monitorexit;
    }
  }

  public void release()
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Releasing (" + this + ")");
      switch (this.mState)
      {
      default:
        if (this.mSoundPlayer != null)
        {
          this.mSoundPlayer.release();
          this.mSoundPlayer = null;
        }
        this.mState = 5;
        sEffectsRecorder = null;
        return;
      case 2:
      case 3:
      case 4:
      }
    }
    finally
    {
      monitorexit;
    }
  }

  public void setCamera(CameraManager.CameraProxy paramCameraProxy)
  {
    monitorenter;
    try
    {
      switch (this.mState)
      {
      default:
        this.mCameraDevice = paramCameraProxy;
        monitorexit;
      case 3:
        throw new RuntimeException("setCamera cannot be called while previewing!");
      case 4:
      case 5:
      }
    }
    finally
    {
      monitorexit;
    }
    throw new RuntimeException("setCamera cannot be called while recording!");
    throw new RuntimeException("setCamera called on an already released recorder!");
  }

  public void setCameraDisplayOrientation(int paramInt)
  {
    if (this.mState != 0)
      throw new RuntimeException("setCameraDisplayOrientation called after configuration!");
    this.mCameraDisplayOrientation = paramInt;
  }

  public void setCameraFacing(int paramInt)
  {
    switch (this.mState)
    {
    default:
      this.mCameraFacing = paramInt;
      setRecordingOrientation();
      return;
    case 5:
    }
    throw new RuntimeException("setCameraFacing called on alrady released recorder!");
  }

  public void setCaptureRate(double paramDouble)
  {
    switch (this.mState)
    {
    default:
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Setting time lapse capture rate to " + paramDouble + " fps");
      this.mCaptureRate = paramDouble;
      return;
    case 4:
      throw new RuntimeException("setCaptureRate cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setCaptureRate called on an already released recorder!");
  }

  public void setEffect(int paramInt, Object paramObject)
  {
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "setEffect: effect ID " + paramInt + ", parameter " + paramObject.toString());
    switch (this.mState)
    {
    default:
      this.mEffect = paramInt;
      this.mEffectParameter = paramObject;
      if ((this.mState == 3) || (this.mState == 2))
        initializeEffect(false);
      return;
    case 4:
      throw new RuntimeException("setEffect cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setEffect called on an already released recorder!");
  }

  public void setEffectsListener(EffectsListener paramEffectsListener)
  {
    this.mEffectsListener = paramEffectsListener;
  }

  public void setMaxDuration(int paramInt)
  {
    monitorenter;
    try
    {
      switch (this.mState)
      {
      default:
        this.mMaxDurationMs = paramInt;
        monitorexit;
      case 4:
        throw new RuntimeException("setMaxDuration cannot be called while recording!");
      case 5:
      }
    }
    finally
    {
      monitorexit;
    }
    throw new RuntimeException("setMaxDuration called on an already released recorder!");
  }

  public void setMaxFileSize(long paramLong)
  {
    monitorenter;
    try
    {
      switch (this.mState)
      {
      default:
        this.mMaxFileSize = paramLong;
        monitorexit;
      case 4:
        throw new RuntimeException("setMaxFileSize cannot be called while recording!");
      case 5:
      }
    }
    finally
    {
      monitorexit;
    }
    throw new RuntimeException("setMaxFileSize called on an already released recorder!");
  }

  public void setOnErrorListener(MediaRecorder.OnErrorListener paramOnErrorListener)
  {
    switch (this.mState)
    {
    default:
      this.mErrorListener = paramOnErrorListener;
      return;
    case 4:
      throw new RuntimeException("setErrorListener cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setErrorListener called on an already released recorder!");
  }

  public void setOnInfoListener(MediaRecorder.OnInfoListener paramOnInfoListener)
  {
    switch (this.mState)
    {
    default:
      this.mInfoListener = paramOnInfoListener;
      return;
    case 4:
      throw new RuntimeException("setInfoListener cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setInfoListener called on an already released recorder!");
  }

  public void setOrientationHint(int paramInt)
  {
    switch (this.mState)
    {
    default:
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Setting orientation hint to: " + paramInt);
      this.mOrientationHint = paramInt;
      setFaceDetectOrientation();
      setRecordingOrientation();
      return;
    case 5:
    }
    throw new RuntimeException("setOrientationHint called on an already released recorder!");
  }

  public void setOutputFile(FileDescriptor paramFileDescriptor)
  {
    switch (this.mState)
    {
    default:
      this.mOutputFile = null;
      this.mFd = paramFileDescriptor;
      return;
    case 4:
      throw new RuntimeException("setOutputFile cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setOutputFile called on an already released recorder!");
  }

  public void setOutputFile(String paramString)
  {
    switch (this.mState)
    {
    default:
      this.mOutputFile = paramString;
      this.mFd = null;
      return;
    case 4:
      throw new RuntimeException("setOutputFile cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setOutputFile called on an already released recorder!");
  }

  public void setPreviewSurfaceTexture(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "setPreviewSurfaceTexture(" + this + ")");
    switch (this.mState)
    {
    default:
      this.mPreviewSurfaceTexture = paramSurfaceTexture;
      this.mPreviewWidth = paramInt1;
      this.mPreviewHeight = paramInt2;
      switch (this.mState)
      {
      default:
        return;
      case 1:
      case 2:
      case 3:
      }
    case 4:
      throw new RuntimeException("setPreviewSurfaceTexture cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setPreviewSurfaceTexture called on an already released recorder!");
    startPreview();
    return;
    initializeEffect(true);
  }

  public void setProfile(CamcorderProfile paramCamcorderProfile)
  {
    switch (this.mState)
    {
    default:
      this.mProfile = paramCamcorderProfile;
      return;
    case 4:
      throw new RuntimeException("setProfile cannot be called while recording!");
    case 5:
    }
    throw new RuntimeException("setProfile called on an already released recorder!");
  }

  public void startPreview()
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Starting preview (" + this + ")");
      switch (this.mState)
      {
      default:
        throw new RuntimeException("No effect selected!");
      case 2:
      case 3:
      case 4:
      case 5:
      }
    }
    finally
    {
      monitorexit;
    }
    Log.w("EffectsRecorder", "startPreview called when already running preview");
    while (true)
    {
      monitorexit;
      return;
      throw new RuntimeException("Cannot start preview when already recording!");
      throw new RuntimeException("setEffect called on an already released recorder!");
      if (this.mEffectParameter == null)
        throw new RuntimeException("No effect parameter provided!");
      if (this.mProfile == null)
        throw new RuntimeException("No recording profile provided!");
      if (this.mPreviewSurfaceTexture == null)
      {
        if (this.mLogVerbose)
          Log.v("EffectsRecorder", "Passed a null surface; waiting for valid one");
        this.mState = 1;
      }
      if (this.mCameraDevice == null)
        throw new RuntimeException("No camera to record from!");
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Initializing filter framework and running the graph.");
      initializeFilterFramework();
      initializeEffect(true);
      this.mState = 2;
      invoke(this.mRunner, sGraphRunnerRun);
    }
  }

  public void startRecording()
  {
    int i = 1;
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Starting recording (" + this + ")");
      switch (this.mState)
      {
      default:
        if (this.mOutputFile != null)
          break label120;
        throw new RuntimeException("No output file name or descriptor provided!");
      case 4:
      case 5:
      }
    }
    finally
    {
      monitorexit;
    }
    throw new RuntimeException("Already recording, cannot begin anew!");
    throw new RuntimeException("startRecording called on an already released recorder!");
    if (this.mState == 0)
      label120: startPreview();
    Object localObject2 = getGraphFilter(this.mRunner, "recorder");
    if (this.mFd != null)
    {
      setInputValue(localObject2, "outputFileDescriptor", this.mFd);
      label162: setInputValue(localObject2, "audioSource", Integer.valueOf(5));
      setInputValue(localObject2, "recordingProfile", this.mProfile);
      setInputValue(localObject2, "orientationHint", Integer.valueOf(this.mOrientationHint));
      if (this.mCaptureRate <= 0.0D)
        break label364;
    }
    while (true)
    {
      if (i != 0)
        setInputValue(localObject2, "timelapseRecordingIntervalUs", Long.valueOf(()(1000000.0D * (1.0D / this.mCaptureRate))));
      while (true)
      {
        if (this.mInfoListener != null)
          setInputValue(localObject2, "infoListener", this.mInfoListener);
        if (this.mErrorListener != null)
          setInputValue(localObject2, "errorListener", this.mErrorListener);
        setInputValue(localObject2, "maxFileSize", Long.valueOf(this.mMaxFileSize));
        setInputValue(localObject2, "maxDurationMs", Integer.valueOf(this.mMaxDurationMs));
        setInputValue(localObject2, "recording", Boolean.valueOf(true));
        this.mSoundPlayer.play(1);
        this.mState = 4;
        monitorexit;
        return;
        setInputValue(localObject2, "outputFile", this.mOutputFile);
        break label162:
        setInputValue(localObject2, "timelapseRecordingIntervalUs", Long.valueOf(0L));
      }
      label364: i = 0;
    }
  }

  public void stopCameraPreview()
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Stopping camera preview.");
      if (this.mCameraDevice == null)
      {
        Log.d("EffectsRecorder", "Camera already null. Nothing to disconnect");
        return;
      }
      this.mCameraDevice.stopPreview();
    }
    finally
    {
      monitorexit;
    }
  }

  public void stopPreview()
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Stopping preview (" + this + ")");
      switch (this.mState)
      {
      default:
        if (this.mState == 4)
          stopRecording();
        this.mCurrentEffect = 0;
        stopCameraPreview();
        this.mState = 0;
        this.mOldRunner = this.mRunner;
        invoke(this.mRunner, sGraphRunnerStop);
        this.mRunner = null;
        return;
      case 0:
      case 5:
      }
    }
    finally
    {
      monitorexit;
    }
    throw new RuntimeException("stopPreview called on released EffectsRecorder!");
  }

  public void stopRecording()
  {
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Stop recording (" + this + ")");
      switch (this.mState)
      {
      case 1:
      case 4:
      default:
        setInputValue(getGraphFilter(this.mRunner, "recorder"), "recording", Boolean.valueOf(false));
        this.mSoundPlayer.play(2);
        this.mState = 3;
        return;
      case 0:
      case 2:
      case 3:
      case 5:
      }
    }
    finally
    {
      monitorexit;
    }
    throw new RuntimeException("stopRecording called on released EffectsRecorder!");
  }

  @TargetApi(14)
  boolean tryEnable3ALocks(boolean paramBoolean)
  {
    int i = 0;
    monitorenter;
    try
    {
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "tryEnable3ALocks");
      if (this.mCameraDevice == null)
        Log.d("EffectsRecorder", "Camera already null. Not tryenabling 3A locks.");
      Camera.Parameters localParameters;
      boolean bool2;
      do
      {
        boolean bool1;
        do
        {
          return i;
          localParameters = this.mCameraDevice.getParameters();
          bool1 = Util.isAutoExposureLockSupported(localParameters);
          i = 0;
        }
        while (!bool1);
        bool2 = Util.isAutoWhiteBalanceLockSupported(localParameters);
        i = 0;
      }
      while (!bool2);
      localParameters.setAutoExposureLock(paramBoolean);
      localParameters.setAutoWhiteBalanceLock(paramBoolean);
      this.mCameraDevice.setParameters(localParameters);
    }
    finally
    {
      monitorexit;
    }
  }

  boolean tryEnableVideoStabilization(boolean paramBoolean)
  {
    if (this.mLogVerbose)
      Log.v("EffectsRecorder", "tryEnableVideoStabilization.");
    if (this.mCameraDevice == null)
      Log.d("EffectsRecorder", "Camera already null. Not enabling video stabilization.");
    do
    {
      return false;
      Camera.Parameters localParameters = this.mCameraDevice.getParameters();
      if (!"true".equals(localParameters.get("video-stabilization-supported")))
        continue;
      if (this.mLogVerbose)
        Log.v("EffectsRecorder", "Setting video stabilization to " + paramBoolean);
      if (paramBoolean);
      for (String str = "true"; ; str = "false")
      {
        localParameters.set("video-stabilization", str);
        this.mCameraDevice.setParameters(localParameters);
        return true;
      }
    }
    while (!this.mLogVerbose);
    Log.v("EffectsRecorder", "Video stabilization not supported");
    return false;
  }

  public static abstract interface EffectsListener
  {
    public abstract void onEffectsError(Exception paramException, String paramString);

    public abstract void onEffectsUpdate(int paramInt1, int paramInt2);
  }

  static class SerializableInvocationHandler
    implements Serializable, InvocationHandler
  {
    private final int mEffectsRecorderIndex;

    public SerializableInvocationHandler(int paramInt)
    {
      this.mEffectsRecorderIndex = paramInt;
    }

    public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
      throws Throwable
    {
      if (EffectsRecorder.sEffectsRecorder == null);
      do
      {
        return null;
        if (this.mEffectsRecorderIndex != EffectsRecorder.sEffectsRecorderIndex)
        {
          Log.v("EffectsRecorder", "Ignore old callback " + this.mEffectsRecorderIndex);
          return null;
        }
        if (paramMethod.equals(EffectsRecorder.sObjectEquals))
          return EffectsRecorder.sEffectsRecorder.invokeObjectEquals(paramObject, paramArrayOfObject);
        if (paramMethod.equals(EffectsRecorder.sObjectToString))
          return EffectsRecorder.sEffectsRecorder.invokeObjectToString();
        if (paramMethod.equals(EffectsRecorder.sLearningDoneListenerOnLearningDone))
        {
          EffectsRecorder.sEffectsRecorder.invokeOnLearningDone();
          return null;
        }
        if (paramMethod.equals(EffectsRecorder.sOnRunnerDoneListenerOnRunnerDone))
        {
          EffectsRecorder.sEffectsRecorder.invokeOnRunnerDone(paramArrayOfObject);
          return null;
        }
        if (!paramMethod.equals(EffectsRecorder.sSurfaceTextureSourceListenerOnSurfaceTextureSourceReady))
          continue;
        EffectsRecorder.sEffectsRecorder.invokeOnSurfaceTextureSourceReady(paramArrayOfObject);
        return null;
      }
      while (!paramMethod.equals(EffectsRecorder.sOnRecordingDoneListenerOnRecordingDone));
      EffectsRecorder.sEffectsRecorder.invokeOnRecordingDone();
      return null;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.EffectsRecorder
 * JD-Core Version:    0.5.4
 */