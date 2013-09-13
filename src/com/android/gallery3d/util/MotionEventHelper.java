package com.android.gallery3d.util;

import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import com.android.gallery3d.common.ApiHelper;

public final class MotionEventHelper
{
  private static MotionEvent.PointerCoords[] getPointerCoords(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getPointerCount();
    MotionEvent.PointerCoords[] arrayOfPointerCoords = new MotionEvent.PointerCoords[i];
    for (int j = 0; j < i; ++j)
    {
      arrayOfPointerCoords[j] = new MotionEvent.PointerCoords();
      paramMotionEvent.getPointerCoords(j, arrayOfPointerCoords[j]);
    }
    return arrayOfPointerCoords;
  }

  private static int[] getPointerIds(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getPointerCount();
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; ++j)
      arrayOfInt[j] = paramMotionEvent.getPointerId(j);
    return arrayOfInt;
  }

  private static float transformAngle(Matrix paramMatrix, float paramFloat)
  {
    float[] arrayOfFloat = new float[2];
    arrayOfFloat[0] = FloatMath.sin(paramFloat);
    arrayOfFloat[1] = (-FloatMath.cos(paramFloat));
    paramMatrix.mapVectors(arrayOfFloat);
    float f = (float)Math.atan2(arrayOfFloat[0], -arrayOfFloat[1]);
    if (f < -1.570796326794897D)
      f = (float)(3.141592653589793D + f);
    do
      return f;
    while (f <= 1.570796326794897D);
    return (float)(f - 3.141592653589793D);
  }

  public static MotionEvent transformEvent(MotionEvent paramMotionEvent, Matrix paramMatrix)
  {
    if (ApiHelper.HAS_MOTION_EVENT_TRANSFORM)
      return transformEventNew(paramMotionEvent, paramMatrix);
    return transformEventOld(paramMotionEvent, paramMatrix);
  }

  @TargetApi(11)
  private static MotionEvent transformEventNew(MotionEvent paramMotionEvent, Matrix paramMatrix)
  {
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    localMotionEvent.transform(paramMatrix);
    return localMotionEvent;
  }

  private static MotionEvent transformEventOld(MotionEvent paramMotionEvent, Matrix paramMatrix)
  {
    long l1 = paramMotionEvent.getDownTime();
    long l2 = paramMotionEvent.getEventTime();
    int i = paramMotionEvent.getAction();
    int j = paramMotionEvent.getPointerCount();
    int[] arrayOfInt = getPointerIds(paramMotionEvent);
    MotionEvent.PointerCoords[] arrayOfPointerCoords = getPointerCoords(paramMotionEvent);
    int k = paramMotionEvent.getMetaState();
    float f1 = paramMotionEvent.getXPrecision();
    float f2 = paramMotionEvent.getYPrecision();
    int l = paramMotionEvent.getDeviceId();
    int i1 = paramMotionEvent.getEdgeFlags();
    int i2 = paramMotionEvent.getSource();
    int i3 = paramMotionEvent.getFlags();
    float[] arrayOfFloat = new float[2 * arrayOfPointerCoords.length];
    for (int i4 = 0; i4 < j; ++i4)
    {
      arrayOfFloat[(i4 * 2)] = arrayOfPointerCoords[i4].x;
      arrayOfFloat[(1 + i4 * 2)] = arrayOfPointerCoords[i4].y;
    }
    paramMatrix.mapPoints(arrayOfFloat);
    for (int i5 = 0; i5 < j; ++i5)
    {
      arrayOfPointerCoords[i5].x = arrayOfFloat[(i5 * 2)];
      arrayOfPointerCoords[i5].y = arrayOfFloat[(1 + i5 * 2)];
      arrayOfPointerCoords[i5].orientation = transformAngle(paramMatrix, arrayOfPointerCoords[i5].orientation);
    }
    return MotionEvent.obtain(l1, l2, i, j, arrayOfInt, arrayOfPointerCoords, k, f1, f2, l, i1, i2, i3);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.MotionEventHelper
 * JD-Core Version:    0.5.4
 */