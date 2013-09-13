package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyBoxParserImpl extends AbstractBoxParser
{
  Properties mapping;
  Pattern p;

  // ERROR //
  public PropertyBoxParserImpl(String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 15	com/coremedia/iso/AbstractBoxParser:<init>	()V
    //   4: aload_0
    //   5: ldc 17
    //   7: invokestatic 23	java/util/regex/Pattern:compile	(Ljava/lang/String;)Ljava/util/regex/Pattern;
    //   10: putfield 25	com/coremedia/iso/PropertyBoxParserImpl:p	Ljava/util/regex/Pattern;
    //   13: new 27	java/io/BufferedInputStream
    //   16: dup
    //   17: aload_0
    //   18: invokevirtual 33	java/lang/Object:getClass	()Ljava/lang/Class;
    //   21: ldc 35
    //   23: invokevirtual 41	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   26: invokespecial 44	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   29: astore_2
    //   30: aload_0
    //   31: new 46	java/util/Properties
    //   34: dup
    //   35: invokespecial 47	java/util/Properties:<init>	()V
    //   38: putfield 49	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   41: aload_0
    //   42: getfield 49	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   45: aload_2
    //   46: invokevirtual 52	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   49: invokestatic 58	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   52: invokevirtual 62	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
    //   55: ldc 64
    //   57: invokevirtual 70	java/lang/ClassLoader:getResources	(Ljava/lang/String;)Ljava/util/Enumeration;
    //   60: astore 6
    //   62: aload 6
    //   64: invokeinterface 76 1 0
    //   69: ifeq +71 -> 140
    //   72: new 27	java/io/BufferedInputStream
    //   75: dup
    //   76: aload 6
    //   78: invokeinterface 80 1 0
    //   83: checkcast 82	java/net/URL
    //   86: invokevirtual 86	java/net/URL:openStream	()Ljava/io/InputStream;
    //   89: invokespecial 44	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   92: astore 7
    //   94: aload_0
    //   95: getfield 49	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   98: aload 7
    //   100: invokevirtual 52	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   103: aload 7
    //   105: invokevirtual 89	java/io/BufferedInputStream:close	()V
    //   108: goto -46 -> 62
    //   111: astore 5
    //   113: new 91	java/lang/RuntimeException
    //   116: dup
    //   117: aload 5
    //   119: invokespecial 94	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   122: athrow
    //   123: astore_3
    //   124: aload_2
    //   125: invokevirtual 89	java/io/BufferedInputStream:close	()V
    //   128: aload_3
    //   129: athrow
    //   130: astore 8
    //   132: aload 7
    //   134: invokevirtual 89	java/io/BufferedInputStream:close	()V
    //   137: aload 8
    //   139: athrow
    //   140: aload_1
    //   141: arraylength
    //   142: istore 9
    //   144: iconst_0
    //   145: istore 10
    //   147: iload 10
    //   149: iload 9
    //   151: if_icmpge +38 -> 189
    //   154: aload_1
    //   155: iload 10
    //   157: aaload
    //   158: astore 11
    //   160: aload_0
    //   161: getfield 49	com/coremedia/iso/PropertyBoxParserImpl:mapping	Ljava/util/Properties;
    //   164: new 27	java/io/BufferedInputStream
    //   167: dup
    //   168: aload_0
    //   169: invokevirtual 33	java/lang/Object:getClass	()Ljava/lang/Class;
    //   172: aload 11
    //   174: invokevirtual 41	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   177: invokespecial 44	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   180: invokevirtual 52	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   183: iinc 10 1
    //   186: goto -39 -> 147
    //   189: aload_2
    //   190: invokevirtual 89	java/io/BufferedInputStream:close	()V
    //   193: return
    //   194: astore 12
    //   196: aload 12
    //   198: invokevirtual 97	java/io/IOException:printStackTrace	()V
    //   201: return
    //   202: astore 4
    //   204: aload 4
    //   206: invokevirtual 97	java/io/IOException:printStackTrace	()V
    //   209: goto -81 -> 128
    //
    // Exception table:
    //   from	to	target	type
    //   41	62	111	java/io/IOException
    //   62	94	111	java/io/IOException
    //   103	108	111	java/io/IOException
    //   132	140	111	java/io/IOException
    //   140	144	111	java/io/IOException
    //   154	183	111	java/io/IOException
    //   30	41	123	finally
    //   41	62	123	finally
    //   62	94	123	finally
    //   103	108	123	finally
    //   113	123	123	finally
    //   132	140	123	finally
    //   140	144	123	finally
    //   154	183	123	finally
    //   94	103	130	finally
    //   189	193	194	java/io/IOException
    //   124	128	202	java/io/IOException
  }

  public Box createBox(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    int i = 0;
    FourCcToBox localFourCcToBox = new FourCcToBox(paramString1, paramArrayOfByte, paramString2).invoke();
    String[] arrayOfString = localFourCcToBox.getParam();
    String str = localFourCcToBox.getClazzName();
    while (true)
    {
      Class localClass;
      Class[] arrayOfClass;
      Object[] arrayOfObject;
      try
      {
        if (arrayOfString[0].trim().length() == 0)
          arrayOfString = new String[0];
        localClass = Class.forName(str);
        arrayOfClass = new Class[arrayOfString.length];
        arrayOfObject = new Object[arrayOfString.length];
        if (i >= arrayOfString.length)
          break label213;
        if ("userType".equals(arrayOfString[i]))
        {
          arrayOfObject[i] = paramArrayOfByte;
          arrayOfClass[i] = [B.class;
        }
        else if ("type".equals(arrayOfString[i]))
        {
          arrayOfObject[i] = paramString1;
          arrayOfClass[i] = String.class;
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new RuntimeException(localClassNotFoundException);
      }
      if ("parent".equals(arrayOfString[i]))
      {
        arrayOfObject[i] = paramString2;
        label213: arrayOfClass[i] = String.class;
      }
      else
      {
        throw new InternalError("No such param: " + arrayOfString[i]);
        while (true)
          try
          {
            if (arrayOfString.length > 0)
            {
              localObject = localClass.getConstructor(arrayOfClass);
              return (Box)((Constructor)localObject).newInstance(arrayOfObject);
            }
            Constructor localConstructor = localClass.getConstructor(new Class[0]);
            Object localObject = localConstructor;
          }
          catch (NoSuchMethodException localNoSuchMethodException)
          {
            throw new RuntimeException(localNoSuchMethodException);
          }
          catch (InvocationTargetException localInvocationTargetException)
          {
            throw new RuntimeException(localInvocationTargetException);
          }
          catch (InstantiationException localInstantiationException)
          {
            throw new RuntimeException(localInstantiationException);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new RuntimeException(localIllegalAccessException);
          }
      }
      ++i;
    }
  }

  private class FourCcToBox
  {
    private String clazzName;
    private String[] param;
    private String parent;
    private String type;
    private byte[] userType;

    public FourCcToBox(String paramArrayOfByte, byte[] paramString1, String arg4)
    {
      this.type = paramArrayOfByte;
      Object localObject;
      this.parent = localObject;
      this.userType = paramString1;
    }

    public String getClazzName()
    {
      return this.clazzName;
    }

    public String[] getParam()
    {
      return this.param;
    }

    public FourCcToBox invoke()
    {
      if (this.userType != null)
      {
        if (!"uuid".equals(this.type))
          throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
        str = PropertyBoxParserImpl.this.mapping.getProperty(this.parent + "-uuid[" + Hex.encodeHex(this.userType).toUpperCase() + "]");
        if (str == null)
          str = PropertyBoxParserImpl.this.mapping.getProperty("uuid[" + Hex.encodeHex(this.userType).toUpperCase() + "]");
        if (str != null);
      }
      for (String str = PropertyBoxParserImpl.this.mapping.getProperty("uuid"); ; str = PropertyBoxParserImpl.this.mapping.getProperty(this.type))
        do
        {
          if (str == null)
            str = PropertyBoxParserImpl.this.mapping.getProperty("default");
          if (str != null)
            break label258;
          throw new RuntimeException("No box object found for " + this.type);
          str = PropertyBoxParserImpl.this.mapping.getProperty(this.parent + "-" + this.type);
        }
        while (str != null);
      label258: Matcher localMatcher = PropertyBoxParserImpl.this.p.matcher(str);
      if (!localMatcher.matches())
        throw new RuntimeException("Cannot work with that constructor: " + str);
      this.clazzName = localMatcher.group(1);
      this.param = localMatcher.group(2).split(",");
      return this;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.PropertyBoxParserImpl
 * JD-Core Version:    0.5.4
 */