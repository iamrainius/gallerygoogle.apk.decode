package com.google.android.apps.lightcycle.opengl;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

public class OpenGLException extends Exception
{
  public OpenGLException(String paramString)
  {
    super(paramString);
    Log.e("LightCycle", paramString, this);
  }

  public OpenGLException(String paramString1, String paramString2)
  {
    super(paramString1);
    Log.e("LightCycle", paramString1 + " : " + paramString2, this);
  }

  public static void logError(String paramString)
    throws OpenGLException
  {
    int i = GLES20.glGetError();
    if (i == 0)
      return;
    throw new OpenGLException(paramString + ": glError " + GLU.gluErrorString(i) + " " + i);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.opengl.OpenGLException
 * JD-Core Version:    0.5.4
 */