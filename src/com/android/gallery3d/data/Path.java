package com.android.gallery3d.data;

import com.android.gallery3d.common.Utils;
import com.android.gallery3d.util.IdentityCache;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Path
{
  private static Path sRoot = new Path(null, "ROOT");
  private IdentityCache<String, Path> mChildren;
  private WeakReference<MediaObject> mObject;
  private final Path mParent;
  private final String mSegment;

  private Path(Path paramPath, String paramString)
  {
    this.mParent = paramPath;
    this.mSegment = paramString;
  }

  public static Path fromString(String paramString)
  {
    monitorenter;
    try
    {
      String[] arrayOfString = split(paramString);
      Path localPath = sRoot;
      for (int i = 0; i < arrayOfString.length; ++i)
        localPath = localPath.getChild(arrayOfString[i]);
      return localPath;
    }
    finally
    {
      monitorexit;
    }
  }

  public static String[] split(String paramString)
  {
    int i = paramString.length();
    if (i == 0)
      return new String[0];
    if (paramString.charAt(0) != '/')
      throw new RuntimeException("malformed path:" + paramString);
    ArrayList localArrayList = new ArrayList();
    int l;
    for (int j = 1; j < i; j = l + 1)
    {
      int k = 0;
      l = j;
      if (l < i)
      {
        label72: int i1 = paramString.charAt(l);
        if (i1 == 123)
          ++k;
        do
          while (true)
          {
            ++l;
            break label72:
            if (i1 != 125)
              break;
            --k;
          }
        while ((k != 0) || (i1 != 47));
      }
      if (k != 0)
        throw new RuntimeException("unbalanced brace in path:" + paramString);
      localArrayList.add(paramString.substring(j, l));
    }
    String[] arrayOfString = new String[localArrayList.size()];
    localArrayList.toArray(arrayOfString);
    return arrayOfString;
  }

  public static String[] splitSequence(String paramString)
  {
    int i = paramString.length();
    if ((paramString.charAt(0) != '{') || (paramString.charAt(i - 1) != '}'))
      throw new RuntimeException("bad sequence: " + paramString);
    ArrayList localArrayList = new ArrayList();
    int l;
    for (int j = 1; j < i - 1; j = l + 1)
    {
      int k = 0;
      l = j;
      if (l < i - 1)
      {
        label77: int i1 = paramString.charAt(l);
        if (i1 == 123)
          ++k;
        do
          while (true)
          {
            ++l;
            break label77:
            if (i1 != 125)
              break;
            --k;
          }
        while ((k != 0) || (i1 != 44));
      }
      if (k != 0)
        throw new RuntimeException("unbalanced brace in path:" + paramString);
      localArrayList.add(paramString.substring(j, l));
    }
    String[] arrayOfString = new String[localArrayList.size()];
    localArrayList.toArray(arrayOfString);
    return arrayOfString;
  }

  public boolean equalsIgnoreCase(String paramString)
  {
    return toString().equalsIgnoreCase(paramString);
  }

  public Path getChild(int paramInt)
  {
    return getChild(String.valueOf(paramInt));
  }

  public Path getChild(long paramLong)
  {
    return getChild(String.valueOf(paramLong));
  }

  public Path getChild(String paramString)
  {
    monitorenter;
    try
    {
      if (this.mChildren == null)
        this.mChildren = new IdentityCache();
      Path localPath1;
      do
      {
        Path localPath2 = new Path(this, paramString);
        this.mChildren.put(paramString, localPath2);
        return localPath2;
        localPath1 = (Path)this.mChildren.get(paramString);
      }
      while (localPath1 == null);
      return localPath1;
    }
    finally
    {
      monitorexit;
    }
  }

  MediaObject getObject()
  {
    monitorenter;
    while (true)
    {
      MediaObject localMediaObject;
      try
      {
        if (this.mObject == null)
        {
          localMediaObject = null;
          return localMediaObject;
        }
      }
      finally
      {
        monitorexit;
      }
    }
  }

  public Path getParent()
  {
    monitorenter;
    try
    {
      Path localPath = this.mParent;
      return localPath;
    }
    finally
    {
      monitorexit;
    }
  }

  public String getPrefix()
  {
    if (this == sRoot)
      return "";
    return getPrefixPath().mSegment;
  }

  public Path getPrefixPath()
  {
    monitorenter;
    try
    {
      throw new IllegalStateException();
    }
    finally
    {
      monitorexit;
    }
    while (this.mParent != sRoot)
      this = this.mParent;
    monitorexit;
    return this;
  }

  public String getSuffix()
  {
    return this.mSegment;
  }

  public void setObject(MediaObject paramMediaObject)
  {
    monitorenter;
    while (true)
    {
      try
      {
        if (this.mObject != null)
        {
          if (this.mObject.get() != null)
            break label54;
          break label49:
          Utils.assertTrue(bool);
          this.mObject = new WeakReference(paramMediaObject);
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      label49: boolean bool = true;
      continue;
      label54: bool = false;
    }
  }

  public String[] split()
  {
    monitorenter;
    int i = 0;
    Path localPath = this;
    try
    {
      while (localPath != sRoot)
      {
        ++i;
        localPath = localPath.mParent;
      }
      String[] arrayOfString = new String[i];
      int k;
      for (int j = i - 1; this != sRoot; j = k)
      {
        k = j - 1;
        arrayOfString[j] = this.mSegment;
        this = this.mParent;
      }
      return arrayOfString;
    }
    finally
    {
      monitorexit;
    }
  }

  public String toString()
  {
    monitorenter;
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      String[] arrayOfString = split();
      for (int i = 0; i < arrayOfString.length; ++i)
      {
        localStringBuilder.append("/");
        localStringBuilder.append(arrayOfString[i]);
      }
      String str = localStringBuilder.toString();
      return str;
    }
    finally
    {
      monitorexit;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.Path
 * JD-Core Version:    0.5.4
 */