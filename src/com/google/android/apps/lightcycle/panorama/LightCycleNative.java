package com.google.android.apps.lightcycle.panorama;

import com.google.android.apps.lightcycle.util.PanoMetadata;
import java.util.HashMap;
import java.util.Map;

public class LightCycleNative
{
  private static NativeUpdatePhotoRenderingCallback nativeTransformsCallback;
  private static NativeProgressCallback progressCallback;
  private static Map<Integer, LightCycleView.ProgressCallback> progressCallbacks;

  static
  {
    System.loadLibrary("lightcycle");
    progressCallbacks = new HashMap();
    progressCallback = new NativeProgressCallback(null);
    nativeTransformsCallback = new NativeUpdatePhotoRenderingCallback(null);
  }

  public static native void AddImage(String paramString, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, boolean paramBoolean1, boolean paramBoolean2);

  public static native void AllowFastMotion(boolean paramBoolean);

  public static native int CleanUp();

  public static native void ComputeAlignment();

  public static native int CreateFrameTexture(int paramInt);

  public static native int CreateNewStitchingSession();

  public static native boolean CreateThumbnailImage(String paramString1, String paramString2, int paramInt, float paramFloat);

  public static native float[] EndGyroCalibration(float[] paramArrayOfFloat, int paramInt, long paramLong);

  public static native int[] GetDeletedTargets();

  public static native float[] GetFrameGeometry(int paramInt1, int paramInt2);

  public static native float[] GetFramePanoOutline(int paramInt1, int paramInt2);

  public static native float GetHeadingRadians();

  public static native NewTarget[] GetNewTargets();

  public static native int GetTargetInRange();

  private static native int Init(int paramInt1, int paramInt2, float paramFloat, boolean paramBoolean, NativeProgressCallback paramNativeProgressCallback, NativeUpdatePhotoRenderingCallback paramNativeUpdatePhotoRenderingCallback);

  public static native int InitFrameTexture(int paramInt1, int paramInt2, int paramInt3);

  public static void InitNative(int paramInt1, int paramInt2, float paramFloat, boolean paramBoolean)
  {
    Init(paramInt1, paramInt2, paramFloat, paramBoolean, progressCallback, nativeTransformsCallback);
  }

  public static native int InitTexture(int paramInt);

  public static native boolean IsImageInLargestComponent(int paramInt);

  public static native boolean MovingTooFast();

  public static native boolean PhotoSkippedTooFast();

  public static native int PreviewStitch(String paramString);

  public static native float[] ProcessFrame(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean);

  public static native void ResetForCapture();

  public static native void SetAppVersion(String paramString);

  public static native void SetCurrentOrientation(float paramFloat);

  public static native void SetFilteredRotation(float[] paramArrayOfFloat);

  public static native void SetGravityVector(float paramFloat1, float paramFloat2, float paramFloat3);

  public static native void SetSensorMovementTooFast(boolean paramBoolean);

  public static native void SetTargetHitAngleRadians(float paramFloat);

  public static native void StartGyroCalibration(float paramFloat);

  public static native void StereographicProject(float paramFloat, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);

  public static boolean StereographicProject(float paramFloat, String paramString1, String paramString2, int paramInt)
  {
    PanoMetadata localPanoMetadata = PanoMetadata.parse(paramString1);
    if (localPanoMetadata == null);
    do
      return false;
    while (localPanoMetadata.isScaled());
    StereographicProject(paramFloat, paramString1, paramString2, paramInt, localPanoMetadata.fullPanoWidth, localPanoMetadata.fullPanoHeight, localPanoMetadata.croppedAreaLeft, localPanoMetadata.croppedAreaTop);
    return true;
  }

  public static native int StitchPanorama(String paramString1, String paramString2, boolean paramBoolean1, String paramString3, int paramInt1, float paramFloat, int paramInt2, boolean paramBoolean2);

  public static native boolean TakeNewPhoto();

  public static native boolean TargetHit();

  public static native void UndoAddImage(boolean paramBoolean);

  public static native int UpdateFrameTexture(int paramInt);

  public static native void UpdateNewTextures();

  public static native int UpdateTexture(int paramInt);

  public static native int ValidInPlaneAngle();

  public static void setProgressCallback(int paramInt, LightCycleView.ProgressCallback paramProgressCallback)
  {
    progressCallbacks.put(Integer.valueOf(paramInt), paramProgressCallback);
  }

  public static void setUpdatePhotoRenderingCallback(LightCycleRenderer.UpdatePhotoRendering paramUpdatePhotoRendering)
  {
    NativeUpdatePhotoRenderingCallback.updatePhotoRenderingCallback = paramUpdatePhotoRendering;
  }

  private static class NativeProgressCallback
  {
    public static void onProgress(int paramInt1, int paramInt2)
    {
      if (!LightCycleNative.progressCallbacks.containsKey(Integer.valueOf(paramInt1)))
        return;
      ((LightCycleView.ProgressCallback)LightCycleNative.progressCallbacks.get(Integer.valueOf(paramInt1))).progress(paramInt2);
    }
  }

  private static class NativeUpdatePhotoRenderingCallback
  {
    public static LightCycleRenderer.UpdatePhotoRendering updatePhotoRenderingCallback = null;

    public static void thumbnailLoaded(int paramInt)
    {
      if (updatePhotoRenderingCallback == null)
        return;
      updatePhotoRenderingCallback.thumbnailLoaded(paramInt);
    }

    public static void updateTransforms(float[] paramArrayOfFloat)
    {
      if (updatePhotoRenderingCallback == null)
        return;
      updatePhotoRenderingCallback.updateTransforms(paramArrayOfFloat);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.LightCycleNative
 * JD-Core Version:    0.5.4
 */