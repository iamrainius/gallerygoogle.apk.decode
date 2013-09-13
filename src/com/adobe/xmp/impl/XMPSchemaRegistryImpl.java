package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.options.AliasOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPAliasInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XMPSchemaRegistryImpl
  implements XMPSchemaRegistry
{
  private Map aliasMap = new HashMap();
  private Map namespaceToPrefixMap = new HashMap();
  private Pattern p = Pattern.compile("[/*?\\[\\]]");
  private Map prefixToNamespaceMap = new HashMap();

  public XMPSchemaRegistryImpl()
  {
    try
    {
      registerStandardNamespaces();
      registerStandardAliases();
      return;
    }
    catch (XMPException localXMPException)
    {
      throw new RuntimeException("The XMPSchemaRegistry cannot be initialized!");
    }
  }

  private void registerStandardAliases()
    throws XMPException
  {
    AliasOptions localAliasOptions1 = new AliasOptions().setArrayOrdered(true);
    AliasOptions localAliasOptions2 = new AliasOptions().setArrayAltText(true);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Authors", "http://purl.org/dc/elements/1.1/", "creator", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Description", "http://purl.org/dc/elements/1.1/", "description", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Format", "http://purl.org/dc/elements/1.1/", "format", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Keywords", "http://purl.org/dc/elements/1.1/", "subject", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Locale", "http://purl.org/dc/elements/1.1/", "language", null);
    registerAlias("http://ns.adobe.com/xap/1.0/", "Title", "http://purl.org/dc/elements/1.1/", "title", null);
    registerAlias("http://ns.adobe.com/xap/1.0/rights/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "BaseURL", "http://ns.adobe.com/xap/1.0/", "BaseURL", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "CreationDate", "http://ns.adobe.com/xap/1.0/", "CreateDate", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Creator", "http://ns.adobe.com/xap/1.0/", "CreatorTool", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "ModDate", "http://ns.adobe.com/xap/1.0/", "ModifyDate", null);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Subject", "http://purl.org/dc/elements/1.1/", "description", localAliasOptions2);
    registerAlias("http://ns.adobe.com/pdf/1.3/", "Title", "http://purl.org/dc/elements/1.1/", "title", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Caption", "http://purl.org/dc/elements/1.1/", "description", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Keywords", "http://purl.org/dc/elements/1.1/", "subject", null);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Marked", "http://ns.adobe.com/xap/1.0/rights/", "Marked", null);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "Title", "http://purl.org/dc/elements/1.1/", "title", localAliasOptions2);
    registerAlias("http://ns.adobe.com/photoshop/1.0/", "WebStatement", "http://ns.adobe.com/xap/1.0/rights/", "WebStatement", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "Artist", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "DateTime", "http://ns.adobe.com/xap/1.0/", "ModifyDate", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "ImageDescription", "http://purl.org/dc/elements/1.1/", "description", null);
    registerAlias("http://ns.adobe.com/tiff/1.0/", "Software", "http://ns.adobe.com/xap/1.0/", "CreatorTool", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Author", "http://purl.org/dc/elements/1.1/", "creator", localAliasOptions1);
    registerAlias("http://ns.adobe.com/png/1.0/", "Copyright", "http://purl.org/dc/elements/1.1/", "rights", localAliasOptions2);
    registerAlias("http://ns.adobe.com/png/1.0/", "CreationTime", "http://ns.adobe.com/xap/1.0/", "CreateDate", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Description", "http://purl.org/dc/elements/1.1/", "description", localAliasOptions2);
    registerAlias("http://ns.adobe.com/png/1.0/", "ModificationTime", "http://ns.adobe.com/xap/1.0/", "ModifyDate", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Software", "http://ns.adobe.com/xap/1.0/", "CreatorTool", null);
    registerAlias("http://ns.adobe.com/png/1.0/", "Title", "http://purl.org/dc/elements/1.1/", "title", localAliasOptions2);
  }

  private void registerStandardNamespaces()
    throws XMPException
  {
    registerNamespace("http://www.w3.org/XML/1998/namespace", "xml");
    registerNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
    registerNamespace("http://purl.org/dc/elements/1.1/", "dc");
    registerNamespace("http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/", "Iptc4xmpCore");
    registerNamespace("adobe:ns:meta/", "x");
    registerNamespace("http://ns.adobe.com/iX/1.0/", "iX");
    registerNamespace("http://ns.adobe.com/xap/1.0/", "xmp");
    registerNamespace("http://ns.adobe.com/xap/1.0/rights/", "xmpRights");
    registerNamespace("http://ns.adobe.com/xap/1.0/mm/", "xmpMM");
    registerNamespace("http://ns.adobe.com/xap/1.0/bj/", "xmpBJ");
    registerNamespace("http://ns.adobe.com/xmp/note/", "xmpNote");
    registerNamespace("http://ns.adobe.com/pdf/1.3/", "pdf");
    registerNamespace("http://ns.adobe.com/pdfx/1.3/", "pdfx");
    registerNamespace("http://www.npes.org/pdfx/ns/id/", "pdfxid");
    registerNamespace("http://www.aiim.org/pdfa/ns/schema#", "pdfaSchema");
    registerNamespace("http://www.aiim.org/pdfa/ns/property#", "pdfaProperty");
    registerNamespace("http://www.aiim.org/pdfa/ns/type#", "pdfaType");
    registerNamespace("http://www.aiim.org/pdfa/ns/field#", "pdfaField");
    registerNamespace("http://www.aiim.org/pdfa/ns/id/", "pdfaid");
    registerNamespace("http://www.aiim.org/pdfa/ns/extension/", "pdfaExtension");
    registerNamespace("http://ns.adobe.com/photoshop/1.0/", "photoshop");
    registerNamespace("http://ns.adobe.com/album/1.0/", "album");
    registerNamespace("http://ns.adobe.com/exif/1.0/", "exif");
    registerNamespace("http://ns.adobe.com/exif/1.0/aux/", "aux");
    registerNamespace("http://ns.adobe.com/tiff/1.0/", "tiff");
    registerNamespace("http://ns.adobe.com/png/1.0/", "png");
    registerNamespace("http://ns.adobe.com/jpeg/1.0/", "jpeg");
    registerNamespace("http://ns.adobe.com/jp2k/1.0/", "jp2k");
    registerNamespace("http://ns.adobe.com/camera-raw-settings/1.0/", "crs");
    registerNamespace("http://ns.adobe.com/StockPhoto/1.0/", "bmsp");
    registerNamespace("http://ns.adobe.com/creatorAtom/1.0/", "creatorAtom");
    registerNamespace("http://ns.adobe.com/asf/1.0/", "asf");
    registerNamespace("http://ns.adobe.com/xmp/wav/1.0/", "wav");
    registerNamespace("http://ns.adobe.com/xmp/1.0/DynamicMedia/", "xmpDM");
    registerNamespace("http://ns.adobe.com/xmp/transient/1.0/", "xmpx");
    registerNamespace("http://ns.adobe.com/xap/1.0/t/", "xmpT");
    registerNamespace("http://ns.adobe.com/xap/1.0/t/pg/", "xmpTPg");
    registerNamespace("http://ns.adobe.com/xap/1.0/g/", "xmpG");
    registerNamespace("http://ns.adobe.com/xap/1.0/g/img/", "xmpGImg");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Font#", "stFNT");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Dimensions#", "stDim");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/ResourceEvent#", "stEvt");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/ResourceRef#", "stRef");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Version#", "stVer");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/Job#", "stJob");
    registerNamespace("http://ns.adobe.com/xap/1.0/sType/ManifestItem#", "stMfs");
    registerNamespace("http://ns.adobe.com/xmp/Identifier/qual/1.0/", "xmpidq");
  }

  public XMPAliasInfo findAlias(String paramString)
  {
    monitorenter;
    try
    {
      XMPAliasInfo localXMPAliasInfo = (XMPAliasInfo)this.aliasMap.get(paramString);
      monitorexit;
      return localXMPAliasInfo;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public String getNamespacePrefix(String paramString)
  {
    monitorenter;
    try
    {
      String str = (String)this.namespaceToPrefixMap.get(paramString);
      monitorexit;
      return str;
    }
    finally
    {
      localObject = finally;
      monitorexit;
      throw localObject;
    }
  }

  public String getNamespaceURI(String paramString)
  {
    monitorenter;
    if (paramString != null);
    try
    {
      if (!paramString.endsWith(":"))
        paramString = paramString + ":";
      String str = (String)this.prefixToNamespaceMap.get(paramString);
      return str;
    }
    finally
    {
      monitorexit;
    }
  }

  void registerAlias(String paramString1, String paramString2, String paramString3, String paramString4, AliasOptions paramAliasOptions)
    throws XMPException
  {
    monitorenter;
    AliasOptions localAliasOptions;
    while (true)
    {
      try
      {
        ParameterAsserts.assertSchemaNS(paramString1);
        ParameterAsserts.assertPropName(paramString2);
        ParameterAsserts.assertSchemaNS(paramString3);
        ParameterAsserts.assertPropName(paramString4);
        if (paramAliasOptions != null)
        {
          localAliasOptions = new AliasOptions(XMPNodeUtils.verifySetOptions(paramAliasOptions.toPropertyOptions(), null).getOptions());
          if (!this.p.matcher(paramString2).find());
          throw new XMPException("Alias and actual property names must be simple", 102);
        }
      }
      finally
      {
        monitorexit;
      }
      localAliasOptions = new AliasOptions();
    }
    String str1 = getNamespacePrefix(paramString1);
    String str2 = getNamespacePrefix(paramString3);
    if (str1 == null)
      throw new XMPException("Alias namespace is not registered", 101);
    if (str2 == null)
      throw new XMPException("Actual namespace is not registered", 101);
    String str3 = str1 + paramString2;
    if (this.aliasMap.containsKey(str3))
      throw new XMPException("Alias is already existing", 4);
    if (this.aliasMap.containsKey(str2 + paramString4))
      throw new XMPException("Actual property is already an alias, use the base property", 4);
    1 local1 = new XMPAliasInfo(paramString3, str2, paramString4, localAliasOptions)
    {
      public AliasOptions getAliasForm()
      {
        return this.val$aliasOpts;
      }

      public String getNamespace()
      {
        return this.val$actualNS;
      }

      public String getPrefix()
      {
        return this.val$actualPrefix;
      }

      public String getPropName()
      {
        return this.val$actualProp;
      }

      public String toString()
      {
        return this.val$actualPrefix + this.val$actualProp + " NS(" + this.val$actualNS + "), FORM (" + getAliasForm() + ")";
      }
    };
    this.aliasMap.put(str3, local1);
    monitorexit;
  }

  public String registerNamespace(String paramString1, String paramString2)
    throws XMPException
  {
    monitorenter;
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertPrefix(paramString2);
      if (paramString2.charAt(-1 + paramString2.length()) != ':')
        paramString2 = paramString2 + ':';
      throw new XMPException("The prefix is a bad XML name", 201);
    }
    finally
    {
      monitorexit;
    }
    String str1 = (String)this.namespaceToPrefixMap.get(paramString1);
    String str2 = (String)this.prefixToNamespaceMap.get(paramString2);
    if (str1 != null)
    {
      label116: monitorexit;
      return str1;
    }
    String str3;
    if (str2 != null)
    {
      str3 = paramString2;
      for (int i = 1; this.prefixToNamespaceMap.containsKey(str3); ++i)
        str3 = paramString2.substring(0, -1 + paramString2.length()) + "_" + i + "_:";
    }
    while (true)
    {
      this.prefixToNamespaceMap.put(paramString2, paramString1);
      this.namespaceToPrefixMap.put(paramString1, paramString2);
      str1 = paramString2;
      break label116:
      paramString2 = str3;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPSchemaRegistryImpl
 * JD-Core Version:    0.5.4
 */