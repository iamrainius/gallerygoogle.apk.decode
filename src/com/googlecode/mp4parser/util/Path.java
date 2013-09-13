package com.googlecode.mp4parser.util;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path
{
  static Pattern component;

  static
  {
    if (!Path.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      component = Pattern.compile("(....|\\.\\.)(\\[(.*)\\])?");
      return;
    }
  }

  public static Box getPath(Box paramBox, String paramString)
  {
    List localList = getPaths(paramBox, paramString);
    if (localList.isEmpty())
      return null;
    return (Box)localList.get(0);
  }

  public static List<Box> getPaths(Box paramBox, String paramString)
  {
    if (paramString.startsWith("/"))
    {
      for (Object localObject2 = paramBox; ((Box)localObject2).getParent() != null; localObject2 = ((Box)localObject2).getParent());
      assert (localObject2 instanceof IsoFile) : (((Box)localObject2).getType() + " has no parent");
      localObject1 = getPaths((Box)localObject2, paramString.substring(1));
      return localObject1;
    }
    if (paramString.isEmpty())
      return Collections.singletonList(paramBox);
    String str2;
    String str1;
    if (paramString.contains("/"))
    {
      str2 = paramString.substring(1 + paramString.indexOf('/'));
      str1 = paramString.substring(0, paramString.indexOf('/'));
    }
    Matcher localMatcher;
    String str3;
    while (true)
    {
      localMatcher = component.matcher(str1);
      if (!localMatcher.matches())
        break label317;
      str3 = localMatcher.group(1);
      if (!"..".equals(str3))
        break;
      return getPaths(paramBox.getParent(), str2);
      str1 = paramString;
      str2 = "";
    }
    int i = -1;
    if (localMatcher.group(2) != null)
      i = Integer.parseInt(localMatcher.group(3));
    Object localObject1 = new LinkedList();
    int j = 0;
    Iterator localIterator = ((ContainerBox)paramBox).getBoxes().iterator();
    while (true)
    {
      if (localIterator.hasNext());
      Box localBox = (Box)localIterator.next();
      if (!localBox.getType().matches(str3))
        continue;
      if ((i == -1) || (i == j))
        ((List)localObject1).addAll(getPaths(localBox, str2));
      ++j;
    }
    label317: throw new RuntimeException(str1 + " is invalid path.");
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.util.Path
 * JD-Core Version:    0.5.4
 */