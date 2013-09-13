package com.android.gallery3d.app;

import com.android.gallery3d.data.Path;

public class FilterUtils
{
  private static void getAppliedFilters(Path paramPath, int[] paramArrayOfInt)
  {
    getAppliedFilters(paramPath, paramArrayOfInt, false);
  }

  private static void getAppliedFilters(Path paramPath, int[] paramArrayOfInt, boolean paramBoolean)
  {
    String[] arrayOfString1 = paramPath.split();
    for (int i = 0; i < arrayOfString1.length; ++i)
    {
      if (!arrayOfString1[i].startsWith("{"))
        continue;
      String[] arrayOfString2 = Path.splitSequence(arrayOfString1[i]);
      for (int k = 0; k < arrayOfString2.length; ++k)
        getAppliedFilters(Path.fromString(arrayOfString2[k]), paramArrayOfInt, paramBoolean);
    }
    if (!arrayOfString1[0].equals("cluster"))
      return;
    if (arrayOfString1.length == 4)
      paramBoolean = true;
    int j = toClusterType(arrayOfString1[2]);
    paramArrayOfInt[0] = (j | paramArrayOfInt[0]);
    paramArrayOfInt[4] = j;
    if (!paramBoolean)
      return;
    paramArrayOfInt[2] = (j | paramArrayOfInt[2]);
  }

  public static String newClusterPath(String paramString, int paramInt)
  {
    switch (paramInt)
    {
    default:
      return paramString;
    case 2:
    case 4:
    case 8:
    case 16:
    case 32:
    }
    for (String str = "time"; ; str = "face")
      while (true)
      {
        return "/cluster/{" + paramString + "}/" + str;
        str = "location";
        continue;
        str = "tag";
        continue;
        str = "size";
      }
  }

  public static String newFilterPath(String paramString, int paramInt)
  {
    switch (paramInt)
    {
    default:
      return paramString;
    case 1:
    case 2:
    }
    for (int i = 2; ; i = 4)
      return "/filter/mediatype/" + i + "/{" + paramString + "}";
  }

  private static String removeOneClusterFromPath(String paramString)
  {
    return removeOneClusterFromPath(paramString, new boolean[1]);
  }

  private static String removeOneClusterFromPath(String paramString, boolean[] paramArrayOfBoolean)
  {
    if (paramArrayOfBoolean[0] != 0)
      return paramString;
    String[] arrayOfString1 = Path.split(paramString);
    if (arrayOfString1[0].equals("cluster"))
    {
      paramArrayOfBoolean[0] = true;
      return Path.splitSequence(arrayOfString1[1])[0];
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if (i < arrayOfString1.length)
    {
      label48: localStringBuilder.append("/");
      if (arrayOfString1[i].startsWith("{"))
      {
        localStringBuilder.append("{");
        String[] arrayOfString2 = Path.splitSequence(arrayOfString1[i]);
        for (int j = 0; j < arrayOfString2.length; ++j)
        {
          if (j > 0)
            localStringBuilder.append(",");
          localStringBuilder.append(removeOneClusterFromPath(arrayOfString2[j], paramArrayOfBoolean));
        }
        localStringBuilder.append("}");
      }
      while (true)
      {
        ++i;
        break label48:
        localStringBuilder.append(arrayOfString1[i]);
      }
    }
    return localStringBuilder.toString();
  }

  private static void setMenuItemApplied(GalleryActionBar paramGalleryActionBar, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean1);
    for (boolean bool = true; ; bool = false)
    {
      paramGalleryActionBar.setClusterItemEnabled(paramInt, bool);
      return;
    }
  }

  private static void setMenuItemAppliedEnabled(GalleryActionBar paramGalleryActionBar, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    paramGalleryActionBar.setClusterItemEnabled(paramInt, paramBoolean2);
  }

  public static void setupMenuItems(GalleryActionBar paramGalleryActionBar, Path paramPath, boolean paramBoolean)
  {
    int i = 1;
    int[] arrayOfInt = new int[6];
    getAppliedFilters(paramPath, arrayOfInt);
    int k = arrayOfInt[0];
    int l = arrayOfInt[i];
    int i1 = arrayOfInt[3];
    int i2 = arrayOfInt[4];
    int i3 = arrayOfInt[5];
    label54: label64: label83: label93: label113: label124: int i38;
    label145: label156: label178: label193: label201: label221: label236: label246: int i40;
    if ((k & 0x2) != 0)
    {
      int i4 = i;
      if ((i2 & 0x2) == 0)
        break label349;
      int i6 = i;
      setMenuItemApplied(paramGalleryActionBar, 2, i4, i6);
      if ((k & 0x4) == 0)
        break label355;
      int i8 = i;
      if ((i2 & 0x4) == 0)
        break label361;
      int i10 = i;
      setMenuItemApplied(paramGalleryActionBar, 4, i8, i10);
      if ((k & 0x8) == 0)
        break label367;
      int i12 = i;
      if ((i2 & 0x8) == 0)
        break label373;
      int i14 = i;
      setMenuItemApplied(paramGalleryActionBar, 8, i12, i14);
      if ((k & 0x20) == 0)
        break label379;
      int i16 = i;
      if ((i2 & 0x20) == 0)
        break label385;
      int i18 = i;
      setMenuItemApplied(paramGalleryActionBar, 32, i16, i18);
      if ((paramBoolean) && (k != 0))
        break label391;
      int i20 = i;
      paramGalleryActionBar.setClusterItemVisibility(i, i20);
      if (k != 0)
        break label397;
      int i22 = i;
      if (i2 != 0)
        break label403;
      int i24 = i;
      setMenuItemApplied(paramGalleryActionBar, 2131558657, i22, i24);
      if ((l & 0x1) == 0)
        break label409;
      int i26 = i;
      if (((l & 0x1) != 0) || (i1 != 0))
        break label415;
      int i28 = i;
      if ((i3 & 0x1) == 0)
        break label421;
      int i30 = i;
      setMenuItemAppliedEnabled(paramGalleryActionBar, 2131362246, i26, i28, i30);
      if ((l & 0x2) == 0)
        break label427;
      int i32 = i;
      label268: if (((l & 0x2) != 0) || (i1 != 0))
        break label433;
      int i34 = i;
      label283: if ((i3 & 0x2) == 0)
        break label439;
      int i36 = i;
      label293: setMenuItemAppliedEnabled(paramGalleryActionBar, 2131362247, i32, i34, i36);
      if (l != 0)
        break label445;
      i38 = i;
      label313: if ((l == 0) || (i1 != 0))
        break label451;
      i40 = i;
      label326: if (i3 != 0)
        break label457;
    }
    while (true)
    {
      setMenuItemAppliedEnabled(paramGalleryActionBar, 2131362248, i38, i40, i);
      return;
      int i5 = 0;
      break label54:
      label349: int i7 = 0;
      break label64:
      label355: int i9 = 0;
      break label83:
      label361: int i11 = 0;
      break label93:
      label367: int i13 = 0;
      break label113:
      label373: int i15 = 0;
      break label124:
      label379: int i17 = 0;
      break label145:
      label385: int i19 = 0;
      break label156:
      label391: int i21 = 0;
      break label178:
      label397: int i23 = 0;
      break label193:
      label403: int i25 = 0;
      break label201:
      label409: int i27 = 0;
      break label221:
      label415: int i29 = 0;
      break label236:
      label421: int i31 = 0;
      break label246:
      label427: int i33 = 0;
      break label268:
      label433: int i35 = 0;
      break label283:
      label439: int i37 = 0;
      break label293:
      label445: int i39 = 0;
      break label313:
      label451: int i41 = 0;
      break label326:
      label457: int j = 0;
    }
  }

  public static String switchClusterPath(String paramString, int paramInt)
  {
    return newClusterPath(removeOneClusterFromPath(paramString), paramInt);
  }

  private static int toClusterType(String paramString)
  {
    if (paramString.equals("time"))
      return 2;
    if (paramString.equals("location"))
      return 4;
    if (paramString.equals("tag"))
      return 8;
    if (paramString.equals("size"))
      return 16;
    if (paramString.equals("face"))
      return 32;
    return 0;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.app.FilterUtils
 * JD-Core Version:    0.5.4
 */