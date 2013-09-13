package com.adobe.xmp.impl;

public class Utils
{
  private static boolean[] xmlNameChars;
  private static boolean[] xmlNameStartChars;

  static
  {
    initCharTables();
  }

  static boolean checkUUIDFormat(String paramString)
  {
    int i = 1;
    int j = 1;
    int k = 0;
    if (paramString == null)
      return false;
    int l = 0;
    if (l < paramString.length())
    {
      if (paramString.charAt(l) == '-')
      {
        label15: ++k;
        if ((j == 0) || ((l != 8) && (l != 13) && (l != 18) && (l != 23)))
          break label78;
      }
      for (j = i; ; j = 0)
      {
        ++l;
        label78: break label15:
      }
    }
    if ((j != 0) && (4 == k) && (36 == l));
    while (true)
    {
      return i;
      i = 0;
    }
  }

  public static String escapeXML(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    for (int i = 0; ; ++i)
    {
      int j = paramString.length();
      int k = 0;
      if (i < j)
      {
        int i1 = paramString.charAt(i);
        if ((i1 != 60) && (i1 != 62) && (i1 != 38) && (((!paramBoolean2) || ((i1 != 9) && (i1 != 10) && (i1 != 13)))) && (((!paramBoolean1) || (i1 != 34))))
          continue;
        k = 1;
      }
      if (k != 0)
        break;
      return paramString;
    }
    StringBuffer localStringBuffer = new StringBuffer(4 * paramString.length() / 3);
    int l = 0;
    if (l < paramString.length())
    {
      label117: char c = paramString.charAt(l);
      if ((!paramBoolean2) || ((c != '\t') && (c != '\n') && (c != '\r')))
        switch (c)
        {
        default:
          localStringBuffer.append(c);
        case '<':
        case '>':
        case '&':
        case '"':
        }
      while (true)
      {
        label212: ++l;
        break label117:
        localStringBuffer.append("&lt;");
        continue;
        localStringBuffer.append("&gt;");
        continue;
        localStringBuffer.append("&amp;");
        continue;
        if (paramBoolean1);
        for (String str = "&quot;"; ; str = "\"")
        {
          localStringBuffer.append(str);
          break label212:
        }
        localStringBuffer.append("&#x");
        localStringBuffer.append(Integer.toHexString(c).toUpperCase());
        localStringBuffer.append(';');
      }
    }
    return localStringBuffer.toString();
  }

  private static void initCharTables()
  {
    xmlNameChars = new boolean[256];
    xmlNameStartChars = new boolean[256];
    int i = 0;
    if (i >= xmlNameChars.length)
      label18: return;
    boolean[] arrayOfBoolean1 = xmlNameStartChars;
    int j;
    label96: boolean[] arrayOfBoolean2;
    if (((97 <= i) && (i <= 122)) || ((65 <= i) && (i <= 90)) || (i == 58) || (i == 95) || ((192 <= i) && (i <= 214)) || ((216 <= i) && (i <= 246)))
    {
      j = 1;
      arrayOfBoolean1[i] = j;
      arrayOfBoolean2 = xmlNameChars;
      if ((((97 > i) || (i > 122))) && (((65 > i) || (i > 90))) && (((48 > i) || (i > 57))) && (i != 58) && (i != 95) && (i != 45) && (i != 46) && (i != 183) && (((192 > i) || (i > 214))) && (((216 > i) || (i > 246))))
        break label220;
    }
    for (int k = 1; ; k = 0)
    {
      arrayOfBoolean2[i] = k;
      i = (char)(i + 1);
      break label18:
      j = 0;
      label220: break label96:
    }
  }

  static boolean isControlChar(char paramChar)
  {
    return (((paramChar <= '\037') || (paramChar == ''))) && (paramChar != '\t') && (paramChar != '\n') && (paramChar != '\r');
  }

  private static boolean isNameChar(char paramChar)
  {
    return (paramChar > 'ÿ') || (xmlNameChars[paramChar] != 0);
  }

  private static boolean isNameStartChar(char paramChar)
  {
    return (paramChar > 'ÿ') || (xmlNameStartChars[paramChar] != 0);
  }

  public static boolean isXMLName(String paramString)
  {
    if ((paramString.length() > 0) && (!isNameStartChar(paramString.charAt(0))))
      return false;
    for (int i = 1; i < paramString.length(); ++i)
      if (!isNameChar(paramString.charAt(i)));
    return true;
  }

  public static boolean isXMLNameNS(String paramString)
  {
    if ((paramString.length() > 0) && (((!isNameStartChar(paramString.charAt(0))) || (paramString.charAt(0) == ':'))))
      return false;
    for (int i = 1; i < paramString.length(); ++i)
      if ((!isNameChar(paramString.charAt(i))) || (paramString.charAt(i) == ':'));
    return true;
  }

  public static String normalizeLangValue(String paramString)
  {
    if ("x-default".equals(paramString))
      return paramString;
    int i = 1;
    StringBuffer localStringBuffer = new StringBuffer();
    int j = 0;
    if (j < paramString.length())
    {
      switch (paramString.charAt(j))
      {
      default:
        if (i != 2)
          label23: localStringBuffer.append(Character.toLowerCase(paramString.charAt(j)));
      case ' ':
      case '-':
      case '_':
      }
      while (true)
      {
        ++j;
        break label23:
        localStringBuffer.append('-');
        ++i;
        continue;
        localStringBuffer.append(Character.toUpperCase(paramString.charAt(j)));
      }
    }
    return localStringBuffer.toString();
  }

  static String removeControlChars(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString);
    for (int i = 0; i < localStringBuffer.length(); ++i)
    {
      if (!isControlChar(localStringBuffer.charAt(i)))
        continue;
      localStringBuffer.setCharAt(i, ' ');
    }
    return localStringBuffer.toString();
  }

  static String[] splitNameAndValue(String paramString)
  {
    int i = paramString.indexOf('=');
    int j = 1;
    if (paramString.charAt(j) == '?');
    String str = paramString.substring(++j, i);
    int k = i + 1;
    int l = paramString.charAt(k);
    int i1 = k + 1;
    int i2 = -2 + paramString.length();
    StringBuffer localStringBuffer = new StringBuffer(i2 - i);
    while (i1 < i2)
    {
      localStringBuffer.append(paramString.charAt(i1));
      if (paramString.charAt(++i1) != l)
        continue;
      ++i1;
    }
    String[] arrayOfString = new String[2];
    arrayOfString[0] = str;
    arrayOfString[1] = localStringBuffer.toString();
    return arrayOfString;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.Utils
 * JD-Core Version:    0.5.4
 */