package com.android.gallery3d.util;

import com.adobe.xmp.XMPMeta;
import com.google.android.apps.lightcycle.xmp.XmpUtil;
import java.io.InputStream;

public class XmpUtilHelper
{
  public static XMPMeta extractXMPMeta(InputStream paramInputStream)
  {
    return XmpUtil.extractXMPMeta(paramInputStream);
  }

  public static boolean writeXMPMeta(String paramString, Object paramObject)
  {
    return XmpUtil.writeXMPMeta(paramString, (XMPMeta)paramObject);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.XmpUtilHelper
 * JD-Core Version:    0.5.4
 */