package com.adobe.xmp.impl.xpath;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.impl.Utils;
import com.adobe.xmp.options.AliasOptions;
import com.adobe.xmp.properties.XMPAliasInfo;

public final class XMPPathParser
{
  public static XMPPath expandXPath(String paramString1, String paramString2)
    throws XMPException
  {
    if ((paramString1 == null) || (paramString2 == null))
      throw new XMPException("Parameter must not be null", 4);
    XMPPath localXMPPath = new XMPPath();
    PathPosition localPathPosition = new PathPosition();
    localPathPosition.path = paramString2;
    parseRootNode(paramString1, localPathPosition, localXMPPath);
    if (localPathPosition.stepEnd < paramString2.length())
    {
      label46: localPathPosition.stepBegin = localPathPosition.stepEnd;
      skipPathDelimiter(paramString2, localPathPosition);
      localPathPosition.stepEnd = localPathPosition.stepBegin;
      if (paramString2.charAt(localPathPosition.stepBegin) != '[');
      for (XMPPathSegment localXMPPathSegment = parseStructSegment(localPathPosition); ; localXMPPathSegment = parseIndexSegment(localPathPosition))
      {
        if (localXMPPathSegment.getKind() != 1)
          break label243;
        if (localXMPPathSegment.getName().charAt(0) != '@')
          break;
        localXMPPathSegment.setName("?" + localXMPPathSegment.getName().substring(1));
        if ("?xml:lang".equals(localXMPPathSegment.getName()))
          break;
        throw new XMPException("Only xml:lang allowed with '@'", 102);
      }
      if (localXMPPathSegment.getName().charAt(0) == '?')
      {
        localPathPosition.nameStart = (1 + localPathPosition.nameStart);
        localXMPPathSegment.setKind(2);
      }
      verifyQualName(localPathPosition.path.substring(localPathPosition.nameStart, localPathPosition.nameEnd));
      while (true)
      {
        localXMPPath.add(localXMPPathSegment);
        break label46:
        label243: if (localXMPPathSegment.getKind() != 6)
          continue;
        if (localXMPPathSegment.getName().charAt(1) == '@')
        {
          localXMPPathSegment.setName("[?" + localXMPPathSegment.getName().substring(2));
          if (!localXMPPathSegment.getName().startsWith("[?xml:lang="))
            throw new XMPException("Only xml:lang allowed with '@'", 102);
        }
        if (localXMPPathSegment.getName().charAt(1) != '?')
          continue;
        localPathPosition.nameStart = (1 + localPathPosition.nameStart);
        localXMPPathSegment.setKind(5);
        verifyQualName(localPathPosition.path.substring(localPathPosition.nameStart, localPathPosition.nameEnd));
      }
    }
    return localXMPPath;
  }

  private static XMPPathSegment parseIndexSegment(PathPosition paramPathPosition)
    throws XMPException
  {
    paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
    if (('0' <= paramPathPosition.path.charAt(paramPathPosition.stepEnd)) && (paramPathPosition.path.charAt(paramPathPosition.stepEnd) <= '9'))
      while ((paramPathPosition.stepEnd < paramPathPosition.path.length()) && ('0' <= paramPathPosition.path.charAt(paramPathPosition.stepEnd)) && (paramPathPosition.path.charAt(paramPathPosition.stepEnd) <= '9'))
        paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
    for (XMPPathSegment localXMPPathSegment = new XMPPathSegment(null, 3); (paramPathPosition.stepEnd >= paramPathPosition.path.length()) || (paramPathPosition.path.charAt(paramPathPosition.stepEnd) != ']'); localXMPPathSegment = new XMPPathSegment(null, 6))
    {
      while (true)
      {
        throw new XMPException("Missing ']' for array index", 102);
        while ((paramPathPosition.stepEnd < paramPathPosition.path.length()) && (paramPathPosition.path.charAt(paramPathPosition.stepEnd) != ']') && (paramPathPosition.path.charAt(paramPathPosition.stepEnd) != '='))
          paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
        if (paramPathPosition.stepEnd >= paramPathPosition.path.length())
          throw new XMPException("Missing ']' or '=' for array index", 102);
        if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) != ']')
          break;
        if (!"[last()".equals(paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd)))
          throw new XMPException("Invalid non-numeric array index", 102);
        localXMPPathSegment = new XMPPathSegment(null, 4);
      }
      paramPathPosition.nameStart = (1 + paramPathPosition.stepBegin);
      paramPathPosition.nameEnd = paramPathPosition.stepEnd;
      paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
      int i = paramPathPosition.path.charAt(paramPathPosition.stepEnd);
      if ((i != 39) && (i != 34))
        throw new XMPException("Invalid quote in array selector", 102);
      for (paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd); ; paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd))
      {
        if (paramPathPosition.stepEnd < paramPathPosition.path.length())
        {
          if (paramPathPosition.path.charAt(paramPathPosition.stepEnd) != i)
            continue;
          if ((1 + paramPathPosition.stepEnd < paramPathPosition.path.length()) && (paramPathPosition.path.charAt(1 + paramPathPosition.stepEnd) == i))
            break label464;
        }
        if (paramPathPosition.stepEnd < paramPathPosition.path.length())
          break;
        throw new XMPException("No terminating quote for array selector", 102);
        label464: paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
      }
      paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
    }
    paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
    localXMPPathSegment.setName(paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd));
    return localXMPPathSegment;
  }

  private static void parseRootNode(String paramString, PathPosition paramPathPosition, XMPPath paramXMPPath)
    throws XMPException
  {
    while ((paramPathPosition.stepEnd < paramPathPosition.path.length()) && ("/[*".indexOf(paramPathPosition.path.charAt(paramPathPosition.stepEnd)) < 0))
      paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
    if (paramPathPosition.stepEnd == paramPathPosition.stepBegin)
      throw new XMPException("Empty initial XMPPath step", 102);
    String str = verifyXPathRoot(paramString, paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd));
    XMPAliasInfo localXMPAliasInfo = XMPMetaFactory.getSchemaRegistry().findAlias(str);
    if (localXMPAliasInfo == null)
    {
      paramXMPPath.add(new XMPPathSegment(paramString, -2147483648));
      paramXMPPath.add(new XMPPathSegment(str, 1));
    }
    do
    {
      return;
      paramXMPPath.add(new XMPPathSegment(localXMPAliasInfo.getNamespace(), -2147483648));
      XMPPathSegment localXMPPathSegment1 = new XMPPathSegment(verifyXPathRoot(localXMPAliasInfo.getNamespace(), localXMPAliasInfo.getPropName()), 1);
      localXMPPathSegment1.setAlias(true);
      localXMPPathSegment1.setAliasForm(localXMPAliasInfo.getAliasForm().getOptions());
      paramXMPPath.add(localXMPPathSegment1);
      if (!localXMPAliasInfo.getAliasForm().isArrayAltText())
        continue;
      XMPPathSegment localXMPPathSegment2 = new XMPPathSegment("[?xml:lang='x-default']", 5);
      localXMPPathSegment2.setAlias(true);
      localXMPPathSegment2.setAliasForm(localXMPAliasInfo.getAliasForm().getOptions());
      paramXMPPath.add(localXMPPathSegment2);
      return;
    }
    while (!localXMPAliasInfo.getAliasForm().isArray());
    XMPPathSegment localXMPPathSegment3 = new XMPPathSegment("[1]", 3);
    localXMPPathSegment3.setAlias(true);
    localXMPPathSegment3.setAliasForm(localXMPAliasInfo.getAliasForm().getOptions());
    paramXMPPath.add(localXMPPathSegment3);
  }

  private static XMPPathSegment parseStructSegment(PathPosition paramPathPosition)
    throws XMPException
  {
    paramPathPosition.nameStart = paramPathPosition.stepBegin;
    while ((paramPathPosition.stepEnd < paramPathPosition.path.length()) && ("/[*".indexOf(paramPathPosition.path.charAt(paramPathPosition.stepEnd)) < 0))
      paramPathPosition.stepEnd = (1 + paramPathPosition.stepEnd);
    paramPathPosition.nameEnd = paramPathPosition.stepEnd;
    if (paramPathPosition.stepEnd == paramPathPosition.stepBegin)
      throw new XMPException("Empty XMPPath segment", 102);
    return new XMPPathSegment(paramPathPosition.path.substring(paramPathPosition.stepBegin, paramPathPosition.stepEnd), 1);
  }

  private static void skipPathDelimiter(String paramString, PathPosition paramPathPosition)
    throws XMPException
  {
    if (paramString.charAt(paramPathPosition.stepBegin) == '/')
    {
      paramPathPosition.stepBegin = (1 + paramPathPosition.stepBegin);
      if (paramPathPosition.stepBegin >= paramString.length())
        throw new XMPException("Empty XMPPath segment", 102);
    }
    if (paramString.charAt(paramPathPosition.stepBegin) != '*')
      return;
    paramPathPosition.stepBegin = (1 + paramPathPosition.stepBegin);
    if ((paramPathPosition.stepBegin < paramString.length()) && (paramString.charAt(paramPathPosition.stepBegin) == '['))
      return;
    throw new XMPException("Missing '[' after '*'", 102);
  }

  private static void verifyQualName(String paramString)
    throws XMPException
  {
    int i = paramString.indexOf(':');
    if (i > 0)
    {
      String str = paramString.substring(0, i);
      if (Utils.isXMLNameNS(str))
      {
        if (XMPMetaFactory.getSchemaRegistry().getNamespaceURI(str) != null)
          return;
        throw new XMPException("Unknown namespace prefix for qualified name", 102);
      }
    }
    throw new XMPException("Ill-formed qualified name", 102);
  }

  private static void verifySimpleXMLName(String paramString)
    throws XMPException
  {
    if (Utils.isXMLName(paramString))
      return;
    throw new XMPException("Bad XML name", 102);
  }

  private static String verifyXPathRoot(String paramString1, String paramString2)
    throws XMPException
  {
    if ((paramString1 == null) || (paramString1.length() == 0))
      throw new XMPException("Schema namespace URI is required", 101);
    if ((paramString2.charAt(0) == '?') || (paramString2.charAt(0) == '@'))
      throw new XMPException("Top level name must not be a qualifier", 102);
    if ((paramString2.indexOf('/') >= 0) || (paramString2.indexOf('[') >= 0))
      throw new XMPException("Top level name must be simple", 102);
    String str1 = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(paramString1);
    if (str1 == null)
      throw new XMPException("Unregistered schema namespace URI", 101);
    int i = paramString2.indexOf(':');
    if (i < 0)
    {
      verifySimpleXMLName(paramString2);
      paramString2 = str1 + paramString2;
    }
    String str2;
    String str3;
    do
    {
      return paramString2;
      verifySimpleXMLName(paramString2.substring(0, i));
      verifySimpleXMLName(paramString2.substring(i));
      str2 = paramString2.substring(0, i + 1);
      str3 = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(paramString1);
      if (str3 != null)
        continue;
      throw new XMPException("Unknown schema namespace prefix", 101);
    }
    while (str2.equals(str3));
    throw new XMPException("Schema namespace URI and prefix mismatch", 101);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.xpath.XMPPathParser
 * JD-Core Version:    0.5.4
 */