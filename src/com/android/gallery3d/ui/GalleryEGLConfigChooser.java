package com.android.gallery3d.ui;

import android.opengl.GLSurfaceView.EGLConfigChooser;
import com.android.gallery3d.common.ApiHelper;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

class GalleryEGLConfigChooser
  implements GLSurfaceView.EGLConfigChooser
{
  private static final int[] ATTR_ID = { 12324, 12323, 12322, 12321, 12325, 12326, 12328, 12327 };
  private static final String[] ATTR_NAME = { "R", "G", "B", "A", "D", "S", "ID", "CAVEAT" };
  private final int[] mConfigSpec565 = { 12324, 5, 12323, 6, 12322, 5, 12321, 0, 12344 };
  private final int[] mConfigSpec888 = { 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12344 };

  private EGLConfig chooseConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig[] paramArrayOfEGLConfig)
  {
    EGLConfig localEGLConfig = null;
    int i = 2147483647;
    int[] arrayOfInt = new int[1];
    int j = 0;
    int k = paramArrayOfEGLConfig.length;
    if (j < k)
    {
      label19: if ((!ApiHelper.USE_888_PIXEL_FORMAT) && (paramEGL10.eglGetConfigAttrib(paramEGLDisplay, paramArrayOfEGLConfig[j], 12324, arrayOfInt)) && (arrayOfInt[0] == 8));
      while (true)
      {
        ++j;
        break label19:
        if (!paramEGL10.eglGetConfigAttrib(paramEGLDisplay, paramArrayOfEGLConfig[j], 12326, arrayOfInt))
          break;
        if ((arrayOfInt[0] == 0) || (arrayOfInt[0] >= i))
          continue;
        i = arrayOfInt[0];
        localEGLConfig = paramArrayOfEGLConfig[j];
      }
      throw new RuntimeException("eglGetConfigAttrib error: " + paramEGL10.eglGetError());
    }
    if (localEGLConfig == null)
      localEGLConfig = paramArrayOfEGLConfig[0];
    paramEGL10.eglGetConfigAttrib(paramEGLDisplay, localEGLConfig, 12326, arrayOfInt);
    logConfig(paramEGL10, paramEGLDisplay, localEGLConfig);
    return localEGLConfig;
  }

  private void logConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig)
  {
    int[] arrayOfInt = new int[1];
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < ATTR_ID.length; ++i)
    {
      paramEGL10.eglGetConfigAttrib(paramEGLDisplay, paramEGLConfig, ATTR_ID[i], arrayOfInt);
      localStringBuilder.append(ATTR_NAME[i] + arrayOfInt[0] + " ");
    }
    Log.i("GalleryEGLConfigChooser", "Config chosen: " + localStringBuilder.toString());
  }

  public EGLConfig chooseConfig(EGL10 paramEGL10, EGLDisplay paramEGLDisplay)
  {
    int[] arrayOfInt1 = new int[1];
    if (ApiHelper.USE_888_PIXEL_FORMAT);
    for (int[] arrayOfInt2 = this.mConfigSpec888; !paramEGL10.eglChooseConfig(paramEGLDisplay, arrayOfInt2, null, 0, arrayOfInt1); arrayOfInt2 = this.mConfigSpec565)
      throw new RuntimeException("eglChooseConfig failed");
    if (arrayOfInt1[0] <= 0)
      throw new RuntimeException("No configs match configSpec");
    EGLConfig[] arrayOfEGLConfig = new EGLConfig[arrayOfInt1[0]];
    if (!paramEGL10.eglChooseConfig(paramEGLDisplay, arrayOfInt2, arrayOfEGLConfig, arrayOfEGLConfig.length, arrayOfInt1))
      throw new RuntimeException();
    return chooseConfig(paramEGL10, paramEGLDisplay, arrayOfEGLConfig);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.ui.GalleryEGLConfigChooser
 * JD-Core Version:    0.5.4
 */