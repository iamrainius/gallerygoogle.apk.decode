package com.google.android.apps.lightcycle.panorama;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLWindowSurfaceFactory;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.apps.lightcycle.camera.CameraApiProxy.CameraProxy;
import com.google.android.apps.lightcycle.camera.CameraPreview;
import com.google.android.apps.lightcycle.sensor.SensorReader;
import com.google.android.apps.lightcycle.storage.LocalSessionStorage;
import com.google.android.apps.lightcycle.storage.PhotoMetadata;
import com.google.android.apps.lightcycle.util.Callback;
import com.google.android.apps.lightcycle.util.LG;
import com.google.android.apps.lightcycle.util.LocationProvider;
import com.google.android.apps.lightcycle.util.MetadataUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class LightCycleView extends GLSurfaceView
  implements View.OnClickListener
{
  private static final String TAG = LightCycleView.class.getSimpleName();
  private final MovingSpeedCalibrator calibrator = new MovingSpeedCalibrator();
  private Handler imageFileWriteHandler = new Handler();
  private IncrementalAligner mAligner;
  private CameraPreview mCameraPreview;
  private boolean mCameraStopped = true;
  private Context mContext;
  private int mCurrentPhoto = 0;
  private boolean mEnableTouchEvents = true;
  private boolean mFirstFrame = false;
  private Handler mHandler;
  private boolean mLastZoom;
  private LocalSessionStorage mLocalStorage;
  private LocationProvider mLocationProvider = null;
  private MessageSender mMessageSender = new MessageSender();
  private FileWriter mOrientationWriter = null;
  Camera.PictureCallback mPictureCallback = new Camera.PictureCallback()
  {
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
      LightCycleView.this.mCameraPreview.initCamera(LightCycleView.this.mPreviewCallback, 320, 240, true);
      LightCycleView.this.writePictureToFileAsync(paramArrayOfByte);
      LightCycleView.access$302(LightCycleView.this, false);
      LightCycleView.this.mSensorReader.getAccelInPlaneRotationRadians();
      LightCycleView.access$902(LightCycleView.this, true);
      LightCycleView.this.mCameraPreview.startPreview();
    }
  };
  private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback()
  {
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
      if (LightCycleView.this.mTakingPhoto);
      do
      {
        return;
        if (LightCycleView.this.mTakeNewPhoto)
        {
          LightCycleView.this.mRenderer.setAddNextFrame();
          LightCycleView.access$602(LightCycleView.this, false);
        }
        LightCycleView.this.mRenderer.setPhotoFinished();
        if (!LightCycleView.this.mCameraStopped)
          LightCycleView.this.mRenderer.setImageData(paramArrayOfByte);
        LightCycleView.this.mCameraPreview.returnCallbackBuffer(paramArrayOfByte);
      }
      while (!LightCycleView.this.mFirstFrame);
      LightCycleView.access$902(LightCycleView.this, false);
    }
  };
  private LightCycleRenderer mRenderer;
  private SensorReader mSensorReader;
  Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
  {
    public void onShutter()
    {
    }
  };
  private boolean mTakeNewPhoto = false;
  private boolean mTakingPhoto = false;
  Camera.PictureCallback mTestCallback = new Camera.PictureCallback()
  {
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
    }
  };
  private Vector<Integer> mThumbnailTextureIds = new Vector(100);
  private float mZoomCurrentDistance;
  private float mZoomStartingDistance;
  private boolean mZooming;
  private Callback<Void> onPhotoTakenCallback;
  private final List<PhotoMetadata> photosTaken = new ArrayList();
  private final List<float[]> rotationQueue = new ArrayList();

  public LightCycleView(Activity paramActivity, CameraPreview paramCameraPreview, SensorReader paramSensorReader, LocalSessionStorage paramLocalSessionStorage, IncrementalAligner paramIncrementalAligner, LightCycleRenderer paramLightCycleRenderer)
  {
    this(paramActivity, paramCameraPreview, paramSensorReader, paramLocalSessionStorage, paramIncrementalAligner, paramLightCycleRenderer, null);
  }

  @SuppressLint({"NewApi"})
  public LightCycleView(Activity paramActivity, CameraPreview paramCameraPreview, SensorReader paramSensorReader, LocalSessionStorage paramLocalSessionStorage, IncrementalAligner paramIncrementalAligner, LightCycleRenderer paramLightCycleRenderer, SurfaceTexture paramSurfaceTexture)
  {
    super(paramActivity);
    this.mContext = paramActivity;
    this.mSensorReader = paramSensorReader;
    this.mLocalStorage = paramLocalSessionStorage;
    this.mAligner = paramIncrementalAligner;
    initPhotoStorage(paramActivity);
    this.mCameraPreview = paramCameraPreview;
    this.mCameraPreview.setMainView(this);
    if (this.mCameraPreview == null)
    {
      Log.v(TAG, "Error creating CameraPreview.");
      return;
    }
    this.mRenderer = paramLightCycleRenderer;
    this.mRenderer.setView(this);
    Display localDisplay = paramActivity.getWindow().getWindowManager().getDefaultDisplay();
    this.mRenderer.setSensorReader(localDisplay, paramSensorReader);
    this.mRenderer.getRenderedGui().subscribe(new MessageSender.MessageSubscriber()
    {
      public void message(int paramInt, float paramFloat, String paramString)
      {
        if (paramInt != 1)
          return;
        LightCycleView.this.mMessageSender.notifyAll(1, 0.0F, "");
      }
    });
    setEGLContextClientVersion(2);
    if (paramSurfaceTexture != null)
      setEGLWindowSurfaceFactory(new GLSurfaceView.EGLWindowSurfaceFactory(paramSurfaceTexture)
      {
        public EGLSurface createWindowSurface(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig, Object paramObject)
        {
          try
          {
            EGLSurface localEGLSurface = paramEGL10.eglCreateWindowSurface(paramEGLDisplay, paramEGLConfig, this.val$surfaceTexture, null);
            return localEGLSurface;
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
          }
          return null;
        }

        public void destroySurface(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLSurface paramEGLSurface)
        {
          paramEGL10.eglDestroySurface(paramEGLDisplay, paramEGLSurface);
        }
      });
    setRenderer(this.mRenderer);
    setRenderMode(0);
    setClickable(true);
    setOnClickListener(this);
    this.mHandler = new MainHandler(null);
    this.mLocationProvider = new LocationProvider((LocationManager)paramActivity.getSystemService("location"));
    this.mSensorReader.setSensorVelocityCallback(new Callback()
    {
      public void onCallback(Float paramFloat)
      {
        LightCycleView.this.calibrator.onSensorVelocityUpdate(paramFloat.floatValue());
      }
    });
  }

  private float getPinchDistance(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX(0) - paramMotionEvent.getX(1);
    float f2 = paramMotionEvent.getY(0) - paramMotionEvent.getY(1);
    return FloatMath.sqrt(f1 * f1 + f2 * f2);
  }

  private void initPhotoStorage(Context paramContext)
  {
    try
    {
      this.mOrientationWriter = new FileWriter(this.mLocalStorage.orientationFilePath);
      return;
    }
    catch (IOException localIOException)
    {
      Log.e(TAG, "Could not create file writer for : " + this.mLocalStorage.orientationFilePath);
    }
  }

  private static double readExposureFromFile(File paramFile)
  {
    String str1 = paramFile.getAbsolutePath();
    try
    {
      String str2 = new ExifInterface(str1).getAttribute("ExposureTime");
      double d1 = -1.0D;
      if (str2 != null);
      try
      {
        double d2 = Double.parseDouble(str2);
        d1 = d2;
        return d1;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return -2.0D;
      }
    }
    catch (IOException localIOException)
    {
    }
    return -3.0D;
  }

  private void takePhoto()
  {
    monitorenter;
    try
    {
      CameraApiProxy.CameraProxy localCameraProxy = this.mCameraPreview.getCamera();
      if (localCameraProxy == null)
        LG.d("Unable to take a photo : camera is null");
      do
      {
        return;
        localCameraProxy.setPreviewCallbackWithBuffer(null);
        localCameraProxy.setPreviewCallback(null);
        localCameraProxy.takePicture(this.mShutterCallback, this.mTestCallback, this.mPictureCallback);
        this.photosTaken.add(new PhotoMetadata(System.currentTimeMillis(), null, this.mLocationProvider.getCurrentLocation(), this.mSensorReader.getAzimuthInDeg()));
      }
      while (this.onPhotoTakenCallback == null);
    }
    finally
    {
      monitorexit;
    }
  }

  private void writeOrientationString(float[] paramArrayOfFloat)
  {
    String str1 = new String();
    float f = 0.0F;
    for (int i = 0; i < 9; ++i)
    {
      str1 = str1 + paramArrayOfFloat[i] + " ";
      f += paramArrayOfFloat[i];
    }
    String str2 = str1 + f + "\n";
    try
    {
      this.mOrientationWriter.write(str2);
      this.mOrientationWriter.flush();
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  private void writePictureToFileAsync(byte[] paramArrayOfByte)
  {
    this.imageFileWriteHandler.post(new Runnable(paramArrayOfByte)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: iconst_1
        //   1: anewarray 4	java/lang/Object
        //   4: astore 4
        //   6: aload 4
        //   8: iconst_0
        //   9: aload_0
        //   10: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   13: invokestatic 35	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1300	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)I
        //   16: invokestatic 41	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   19: aastore
        //   20: ldc 43
        //   22: aload 4
        //   24: invokestatic 49	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   27: astore 5
        //   29: new 51	java/io/File
        //   32: dup
        //   33: aload_0
        //   34: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   37: invokestatic 55	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1400	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)Lcom/google/android/apps/lightcycle/storage/LocalSessionStorage;
        //   40: getfield 61	com/google/android/apps/lightcycle/storage/LocalSessionStorage:sessionDir	Ljava/lang/String;
        //   43: aload 5
        //   45: invokespecial 64	java/io/File:<init>	(Ljava/lang/String;Ljava/lang/String;)V
        //   48: astore 6
        //   50: new 66	java/io/FileOutputStream
        //   53: dup
        //   54: aload 6
        //   56: invokespecial 69	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
        //   59: astore 7
        //   61: invokestatic 75	com/google/android/apps/lightcycle/panorama/DeviceManager:isGalaxySz	()Z
        //   64: ifeq +231 -> 295
        //   67: aload_0
        //   68: getfield 21	com/google/android/apps/lightcycle/panorama/LightCycleView$8:val$imageData	[B
        //   71: iconst_0
        //   72: aload_0
        //   73: getfield 21	com/google/android/apps/lightcycle/panorama/LightCycleView$8:val$imageData	[B
        //   76: arraylength
        //   77: invokestatic 81	android/graphics/BitmapFactory:decodeByteArray	([BII)Landroid/graphics/Bitmap;
        //   80: astore 11
        //   82: aload 11
        //   84: getstatic 87	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
        //   87: bipush 100
        //   89: aload 7
        //   91: invokevirtual 93	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
        //   94: pop
        //   95: aload 11
        //   97: invokevirtual 96	android/graphics/Bitmap:recycle	()V
        //   100: aload 7
        //   102: invokevirtual 99	java/io/FileOutputStream:close	()V
        //   105: aload_0
        //   106: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   109: invokestatic 35	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1300	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)I
        //   112: aload_0
        //   113: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   116: invokestatic 103	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1500	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)Ljava/util/List;
        //   119: invokeinterface 109 1 0
        //   124: if_icmpge +33 -> 157
        //   127: aload_0
        //   128: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   131: invokestatic 103	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1500	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)Ljava/util/List;
        //   134: aload_0
        //   135: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   138: invokestatic 35	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1300	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)I
        //   141: invokeinterface 113 2 0
        //   146: checkcast 115	com/google/android/apps/lightcycle/storage/PhotoMetadata
        //   149: aload 6
        //   151: invokevirtual 119	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   154: putfield 122	com/google/android/apps/lightcycle/storage/PhotoMetadata:filePath	Ljava/lang/String;
        //   157: new 124	java/lang/StringBuilder
        //   160: dup
        //   161: invokespecial 125	java/lang/StringBuilder:<init>	()V
        //   164: ldc 127
        //   166: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   169: aload_0
        //   170: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   173: invokestatic 35	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1300	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)I
        //   176: invokevirtual 134	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   179: ldc 136
        //   181: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   184: aload_0
        //   185: getfield 21	com/google/android/apps/lightcycle/panorama/LightCycleView$8:val$imageData	[B
        //   188: arraylength
        //   189: invokevirtual 134	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   192: ldc 138
        //   194: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   197: invokevirtual 141	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   200: invokestatic 147	com/google/android/apps/lightcycle/util/LG:d	(Ljava/lang/String;)V
        //   203: aload_0
        //   204: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   207: invokestatic 151	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1600	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)Ljava/util/Vector;
        //   210: aload_0
        //   211: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   214: invokestatic 35	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1300	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)I
        //   217: invokevirtual 154	java/util/Vector:get	(I)Ljava/lang/Object;
        //   220: checkcast 37	java/lang/Integer
        //   223: invokevirtual 157	java/lang/Integer:intValue	()I
        //   226: istore 8
        //   228: aload_0
        //   229: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   232: invokestatic 160	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1700	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)Ljava/util/List;
        //   235: iconst_0
        //   236: invokeinterface 163 2 0
        //   241: checkcast 165	[F
        //   244: invokevirtual 169	[F:clone	()Ljava/lang/Object;
        //   247: checkcast 165	[F
        //   250: astore 9
        //   252: aload_0
        //   253: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   256: invokestatic 173	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1800	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)Lcom/google/android/apps/lightcycle/panorama/IncrementalAligner;
        //   259: aload 6
        //   261: invokevirtual 119	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   264: aload 9
        //   266: iload 8
        //   268: invokevirtual 179	com/google/android/apps/lightcycle/panorama/IncrementalAligner:addImage	(Ljava/lang/String;[FI)V
        //   271: aload_0
        //   272: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   275: invokestatic 182	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1304	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)I
        //   278: pop
        //   279: aload_0
        //   280: getfield 19	com/google/android/apps/lightcycle/panorama/LightCycleView$8:this$0	Lcom/google/android/apps/lightcycle/panorama/LightCycleView;
        //   283: invokestatic 186	com/google/android/apps/lightcycle/panorama/LightCycleView:access$200	(Lcom/google/android/apps/lightcycle/panorama/LightCycleView;)Lcom/google/android/apps/lightcycle/panorama/MovingSpeedCalibrator;
        //   286: aload 6
        //   288: invokestatic 190	com/google/android/apps/lightcycle/panorama/LightCycleView:access$1900	(Ljava/io/File;)D
        //   291: invokevirtual 196	com/google/android/apps/lightcycle/panorama/MovingSpeedCalibrator:onExposureUpdate	(D)V
        //   294: return
        //   295: aload 7
        //   297: aload_0
        //   298: getfield 21	com/google/android/apps/lightcycle/panorama/LightCycleView$8:val$imageData	[B
        //   301: invokevirtual 199	java/io/FileOutputStream:write	([B)V
        //   304: goto -204 -> 100
        //   307: astore_1
        //   308: aload_1
        //   309: invokevirtual 202	java/io/FileNotFoundException:printStackTrace	()V
        //   312: return
        //   313: astore_3
        //   314: aload_3
        //   315: invokevirtual 203	java/io/IOException:printStackTrace	()V
        //   318: return
        //   319: astore_2
        //   320: aload_2
        //   321: invokevirtual 204	java/lang/IndexOutOfBoundsException:printStackTrace	()V
        //   324: return
        //   325: astore_2
        //   326: goto -6 -> 320
        //   329: astore_3
        //   330: goto -16 -> 314
        //   333: astore_1
        //   334: goto -26 -> 308
        //
        // Exception table:
        //   from	to	target	type
        //   61	100	307	java/io/FileNotFoundException
        //   100	157	307	java/io/FileNotFoundException
        //   157	294	307	java/io/FileNotFoundException
        //   295	304	307	java/io/FileNotFoundException
        //   0	61	313	java/io/IOException
        //   0	61	319	java/lang/IndexOutOfBoundsException
        //   61	100	325	java/lang/IndexOutOfBoundsException
        //   100	157	325	java/lang/IndexOutOfBoundsException
        //   157	294	325	java/lang/IndexOutOfBoundsException
        //   295	304	325	java/lang/IndexOutOfBoundsException
        //   61	100	329	java/io/IOException
        //   100	157	329	java/io/IOException
        //   157	294	329	java/io/IOException
        //   295	304	329	java/io/IOException
        //   0	61	333	java/io/FileNotFoundException
      }
    });
  }

  public void clearRendering()
  {
    this.mRenderer.setRenderBlankScreen(true);
    requestRender();
  }

  public CameraPreview getCameraPreview()
  {
    return this.mCameraPreview;
  }

  public Camera.PreviewCallback getPreviewCallback()
  {
    return this.mPreviewCallback;
  }

  public int getTotalPhotos()
  {
    return this.photosTaken.size();
  }

  public boolean isProcessingAlignment()
  {
    return this.mAligner.isProcessingImages();
  }

  public void onClick(View paramView)
  {
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if (!this.mEnableTouchEvents)
      bool = false;
    do
    {
      do
      {
        return bool;
        bool = this.mRenderer.getRenderedGui().handleEvent(paramMotionEvent);
      }
      while (bool);
      bool = true;
      switch (0xFF & paramMotionEvent.getAction())
      {
      case 0:
      case 3:
      case 4:
      default:
        return false;
      case 5:
        this.mZoomStartingDistance = getPinchDistance(paramMotionEvent);
        this.mZooming = true;
        return bool;
      case 2:
      case 6:
      case 1:
      }
    }
    while (!this.mZooming);
    this.mZoomCurrentDistance = getPinchDistance(paramMotionEvent);
    float f4 = this.mZoomCurrentDistance / this.mZoomStartingDistance;
    this.mRenderer.pinchZoom(f4);
    return bool;
    this.mZooming = false;
    float f3 = this.mZoomCurrentDistance / this.mZoomStartingDistance;
    this.mRenderer.endPinchZoom(f3);
    this.mLastZoom = true;
    return bool;
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if ((this.mRenderer == null) || (this.mRenderer.getPanoPreview2d() == null) || (this.mRenderer.getPanoPreview2d().pointInside(f1, f2)));
    while (true)
    {
      this.mLastZoom = false;
      return bool;
      if (this.mLastZoom)
        continue;
    }
  }

  public void registerMessageSink(MessageSender.MessageSubscriber paramMessageSubscriber)
  {
    this.mMessageSender.subscribe(paramMessageSubscriber);
  }

  public void requestPhoto(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (this.mTakingPhoto)
      return;
    this.rotationQueue.add(paramArrayOfFloat);
    this.mHandler.sendEmptyMessage(4);
    this.mThumbnailTextureIds.setSize(Math.max(paramInt1 + 1, this.mThumbnailTextureIds.size()));
    this.mThumbnailTextureIds.set(paramInt1, Integer.valueOf(paramInt2));
    this.mTakingPhoto = true;
    writeOrientationString(paramArrayOfFloat);
  }

  public void resetVelocityLimit()
  {
    this.calibrator.reset();
  }

  public void setAppVersion()
  {
    PackageInfo localPackageInfo1;
    try
    {
      PackageInfo localPackageInfo2 = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
      localPackageInfo1 = localPackageInfo2;
      if (localPackageInfo1 != null)
      {
        String str = localPackageInfo1.versionName;
        LG.d("Setting version to " + str);
        LightCycleNative.SetAppVersion(str);
      }
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      LG.d("Unable to find the app package.");
      localPackageInfo1 = null;
    }
  }

  public void setEnableTouchEvents(boolean paramBoolean)
  {
    this.mEnableTouchEvents = paramBoolean;
  }

  public void setFrameDimensions(int paramInt1, int paramInt2)
  {
    this.mRenderer.setFrameDimensions(paramInt1, paramInt2);
  }

  public void setLiveImageDisplay(boolean paramBoolean)
  {
    this.mRenderer.setLiveImageDisplay(paramBoolean);
  }

  public void setLocationProviderEnabled(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mLocationProvider.startProvider();
      return;
    }
    this.mLocationProvider.stopProvider();
  }

  public void setOnPhotoTakenCallback(Callback<Void> paramCallback)
  {
    this.onPhotoTakenCallback = paramCallback;
  }

  public void startCamera()
  {
    LightCycleNative.ResetForCapture();
    LG.d("Reset native code for capture.");
    setAppVersion();
    float[] arrayOfFloat = LightCycleNative.GetFrameGeometry(2, 2);
    this.mRenderer.createFrameDisplay(arrayOfFloat, 2, 2);
    if (this.mCameraPreview == null)
      return;
    this.mCameraStopped = false;
    this.mHandler.sendEmptyMessage(3);
  }

  public void stopCamera()
  {
    this.mRenderer.setRenderingStopped(true);
    this.mCameraStopped = true;
    if (this.mCameraPreview == null)
      return;
    this.mCameraPreview.releaseCamera();
    this.mCameraPreview = null;
    this.mLocationProvider.stopProvider();
    MetadataUtils.writeMetadataFile(this.mLocalStorage.metadataFilePath, this.photosTaken);
    try
    {
      this.mOrientationWriter.close();
      return;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  public int undoAddImage()
  {
    monitorenter;
    int j;
    try
    {
      int i = this.mCurrentPhoto;
      if (i == 0)
      {
        j = 0;
        return j;
      }
      label175: this.photosTaken.remove(-1 + this.photosTaken.size());
    }
    finally
    {
      try
      {
        this.mOrientationWriter.close();
        BufferedReader localBufferedReader = new BufferedReader(new FileReader(this.mLocalStorage.orientationFilePath));
        StringBuilder localStringBuilder = new StringBuilder();
        for (int k = 0; k < this.mCurrentPhoto; ++k)
        {
          localStringBuilder.append(localBufferedReader.readLine());
          localStringBuilder.append("\n");
        }
        localBufferedReader.close();
        this.mOrientationWriter = new FileWriter(this.mLocalStorage.orientationFilePath);
        this.mOrientationWriter.write(localStringBuilder.toString());
        this.mOrientationWriter.flush();
        LG.d("undoAddImage: finished writing mOrientationWriter");
        j = this.mCurrentPhoto;
      }
      catch (IOException localIOException)
      {
        Log.e(TAG, "undo image exception:", localIOException);
        break label175:
        localObject = finally;
        monitorexit;
        throw localObject;
      }
    }
  }

  public void undoLastCapturedPhoto()
  {
    this.mRenderer.getRenderedGui().notifyUndo();
  }

  private class MainHandler extends Handler
  {
    private MainHandler()
    {
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
      case 3:
        do
        {
          return;
          LightCycleView.access$302(LightCycleView.this, false);
        }
        while (LightCycleView.this.mCameraPreview == null);
        LightCycleView.this.mCameraPreview.startPreview();
        return;
      case 4:
      }
      LightCycleView.this.takePhoto();
    }
  }

  public static abstract interface ProgressCallback
  {
    public abstract void progress(int paramInt);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.LightCycleView
 * JD-Core Version:    0.5.4
 */