package com.android.camera;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PreferenceInflater
{
  private static final Class<?>[] CTOR_SIGNATURE;
  private static final String PACKAGE_NAME = PreferenceInflater.class.getPackage().getName();
  private static final HashMap<String, Constructor<?>> sConstructorMap;
  private Context mContext;

  static
  {
    CTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class };
    sConstructorMap = new HashMap();
  }

  public PreferenceInflater(Context paramContext)
  {
    this.mContext = paramContext;
  }

  private CameraPreference inflate(XmlPullParser paramXmlPullParser)
  {
    AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
    ArrayList localArrayList = new ArrayList();
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = this.mContext;
    arrayOfObject[1] = localAttributeSet;
    while (true)
    {
      int i;
      CameraPreference localCameraPreference2;
      int j;
      try
      {
        i = paramXmlPullParser.next();
        break label195:
        do
        {
          i = paramXmlPullParser.next();
          break label195:
          localCameraPreference2 = newPreference(paramXmlPullParser.getName(), arrayOfObject);
          j = paramXmlPullParser.getDepth();
          if (j <= localArrayList.size())
            break label129;
          label92: localArrayList.add(localCameraPreference2);
        }
        while (j <= 1);
        label129: ((PreferenceGroup)localArrayList.get(j - 2)).addChild(localCameraPreference2);
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        throw new InflateException(localXmlPullParserException);
        int k = j - 1;
        localArrayList.set(k, localCameraPreference2);
        break label92:
      }
      catch (IOException localIOException)
      {
        throw new InflateException(paramXmlPullParser.getPositionDescription(), localIOException);
      }
      do
      {
        if (localArrayList.size() == 0)
          throw new InflateException("No root element found");
        CameraPreference localCameraPreference1 = (CameraPreference)localArrayList.get(0);
        label195: return localCameraPreference1;
      }
      while (i == 1);
      if (i == 2)
        continue;
    }
  }

  private CameraPreference newPreference(String paramString, Object[] paramArrayOfObject)
  {
    String str = PACKAGE_NAME + "." + paramString;
    Constructor localConstructor = (Constructor)sConstructorMap.get(str);
    if (localConstructor == null);
    try
    {
      localConstructor = this.mContext.getClassLoader().loadClass(str).getConstructor(CTOR_SIGNATURE);
      sConstructorMap.put(str, localConstructor);
      CameraPreference localCameraPreference = (CameraPreference)localConstructor.newInstance(paramArrayOfObject);
      return localCameraPreference;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new InflateException("Error inflating class " + str, localNoSuchMethodException);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InflateException("No such class: " + str, localClassNotFoundException);
    }
    catch (Exception localException)
    {
      throw new InflateException("While create instance of" + str, localException);
    }
  }

  public CameraPreference inflate(int paramInt)
  {
    return inflate(this.mContext.getResources().getXml(paramInt));
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.camera.PreferenceInflater
 * JD-Core Version:    0.5.4
 */