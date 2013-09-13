package com.google.android.apps.lightcycle.panorama;

import android.os.Build;
import com.google.android.apps.lightcycle.util.LG;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceManager
{
  private static final Config ACTIVE_CONFIG;
  private static final Map<String, Config> DEVICE_CONFIG;
  private static final boolean IS_DEVICE_SUPPORTED;
  private static final String[] SUPPORTED_DEVICES = { "Galaxy Nexus", "Wingman", "GalaxySZ", "SAMSUNG-SGH-I747", "SGH-I747", "SAMSUNG-SCH-I535", "SCH-I535", "SAMSUNG-SGH-T999", "SGH-T999", "SAMSUNG-SPH-L710", "SPH-L710", "SAMSUNG-SCH-R530", "SCH-R530", "GT-I9300", "occam", "Nexus 4", "manta", "Nexus 10" };

  static
  {
    DEVICE_CONFIG = new HashMap();
    DEVICE_CONFIG.put("Galaxy Nexus", new Config(85.0F, 95.0F, 65.0F, 51.75F));
    DEVICE_CONFIG.put("Wingman", new Config(100.0F, 90.0F, 90.0F, 70.0F));
    DEVICE_CONFIG.put("GalaxySZ", new Config(75.0F, 80.0F, 55.0F, 49.599998F));
    DEVICE_CONFIG.put("SAMSUNG-SGH-I747", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SGH-I747", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SAMSUNG-SCH-I535", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SCH-I535", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SAMSUNG-SGH-T999", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SGH-T999", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SAMSUNG-SPH-L710", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SPH-L710", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SAMSUNG-SCH-R530", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("SCH-R530", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("GT-I9300", new Config(90.0F, 95.0F, 65.0F, 59.599998F));
    DEVICE_CONFIG.put("occam", new Config(90.0F, 95.0F, 65.0F, 57.5F));
    DEVICE_CONFIG.put("Nexus 4", new Config(90.0F, 95.0F, 65.0F, 57.5F));
    DEVICE_CONFIG.put("manta", new Config(90.0F, 95.0F, 65.0F, 54.0F));
    DEVICE_CONFIG.put("Nexus 10", new Config(90.0F, 95.0F, 65.0F, 54.0F));
    DEVICE_CONFIG.put("Default", new Config(75.0F, 80.0F, 55.0F, 0.0F));
    IS_DEVICE_SUPPORTED = isDeviceSupportedInternal();
    ACTIVE_CONFIG = getDeviceConfig();
  }

  public static float getCameraFieldOfViewDegrees(float paramFloat)
  {
    if (ACTIVE_CONFIG.cameraFovDegrees > 0.0F)
      paramFloat = ACTIVE_CONFIG.cameraFovDegrees;
    return paramFloat;
  }

  private static Config getDeviceConfig()
  {
    Iterator localIterator = DEVICE_CONFIG.keySet().iterator();
    String str;
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      if (Build.MODEL.startsWith(str))
        return (Config)DEVICE_CONFIG.get(str);
    }
    return (Config)DEVICE_CONFIG.get("Default");
  }

  public static float getOpenGlDefaultFieldOfViewDegrees()
  {
    return ACTIVE_CONFIG.glFovDegrees;
  }

  public static float getOpenGlMaxFieldOfViewDegrees()
  {
    return ACTIVE_CONFIG.glMaxFovDegrees;
  }

  public static float getOpenGlMinFieldOfViewDegrees()
  {
    return ACTIVE_CONFIG.glMinFovDegrees;
  }

  public static boolean isDeviceSupported()
  {
    return true;
  }

  private static boolean isDeviceSupportedInternal()
  {
    reportBuild();
    for (String str : SUPPORTED_DEVICES)
      if (Build.MODEL.startsWith(str))
        return true;
    return false;
  }

  public static boolean isGalaxySz()
  {
    return Build.MODEL.startsWith("GalaxySZ");
  }

  public static boolean isWingman()
  {
    return Build.MODEL.startsWith("Wingman");
  }

  private static void reportBuild()
  {
    String str1 = "Build : " + Build.MODEL + " ";
    String str2 = str1 + "Hardware : " + Build.HARDWARE + " ";
    String str3 = str2 + "Brand : " + Build.BRAND + " ";
    String str4 = str3 + "Product : " + Build.PRODUCT + " ";
    String str5 = str4 + "Board : " + Build.BOARD + " ";
    LG.d("Build : \n" + str5);
  }

  private static class Config
  {
    public final float cameraFovDegrees;
    public final float glFovDegrees;
    public final float glMaxFovDegrees;
    public final float glMinFovDegrees;

    public Config(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.glFovDegrees = paramFloat1;
      this.glMaxFovDegrees = paramFloat2;
      this.glMinFovDegrees = paramFloat3;
      this.cameraFovDegrees = paramFloat4;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.google.android.apps.lightcycle.panorama.DeviceManager
 * JD-Core Version:    0.5.4
 */