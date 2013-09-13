package com.adobe.xmp.impl;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.AliasOptions;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPAliasInfo;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XMPNormalizer
{
  private static Map dcArrayForms;

  static
  {
    initDCArrays();
  }

  private static void compareAliasedSubtrees(XMPNode paramXMPNode1, XMPNode paramXMPNode2, boolean paramBoolean)
    throws XMPException
  {
    if ((!paramXMPNode1.getValue().equals(paramXMPNode2.getValue())) || (paramXMPNode1.getChildrenLength() != paramXMPNode2.getChildrenLength()))
      throw new XMPException("Mismatch between alias and base nodes", 203);
    if ((!paramBoolean) && (((!paramXMPNode1.getName().equals(paramXMPNode2.getName())) || (!paramXMPNode1.getOptions().equals(paramXMPNode2.getOptions())) || (paramXMPNode1.getQualifierLength() != paramXMPNode2.getQualifierLength()))))
      throw new XMPException("Mismatch between alias and base nodes", 203);
    Iterator localIterator1 = paramXMPNode1.iterateChildren();
    Iterator localIterator2 = paramXMPNode2.iterateChildren();
    while ((localIterator1.hasNext()) && (localIterator2.hasNext()))
      compareAliasedSubtrees((XMPNode)localIterator1.next(), (XMPNode)localIterator2.next(), false);
    Iterator localIterator3 = paramXMPNode1.iterateQualifier();
    Iterator localIterator4 = paramXMPNode2.iterateQualifier();
    while ((localIterator3.hasNext()) && (localIterator4.hasNext()))
      compareAliasedSubtrees((XMPNode)localIterator3.next(), (XMPNode)localIterator4.next(), false);
  }

  private static void deleteEmptySchemas(XMPNode paramXMPNode)
  {
    Iterator localIterator = paramXMPNode.iterateChildren();
    while (localIterator.hasNext())
    {
      if (((XMPNode)localIterator.next()).hasChildren())
        continue;
      localIterator.remove();
    }
  }

  private static void fixGPSTimeStamp(XMPNode paramXMPNode)
    throws XMPException
  {
    XMPNode localXMPNode1 = XMPNodeUtils.findChildNode(paramXMPNode, "exif:GPSTimeStamp", false);
    if (localXMPNode1 == null)
      return;
    try
    {
      XMPDateTime localXMPDateTime1 = XMPUtils.convertToDate(localXMPNode1.getValue());
      if ((localXMPDateTime1.getYear() == 0) && (localXMPDateTime1.getMonth() == 0) && (localXMPDateTime1.getDay() == 0));
      XMPNode localXMPNode2 = XMPNodeUtils.findChildNode(paramXMPNode, "exif:DateTimeOriginal", false);
      if (localXMPNode2 == null)
        localXMPNode2 = XMPNodeUtils.findChildNode(paramXMPNode, "exif:DateTimeDigitized", false);
      XMPDateTime localXMPDateTime2 = XMPUtils.convertToDate(localXMPNode2.getValue());
      Calendar localCalendar = localXMPDateTime1.getCalendar();
      localCalendar.set(1, localXMPDateTime2.getYear());
      localCalendar.set(2, localXMPDateTime2.getMonth());
      localCalendar.set(5, localXMPDateTime2.getDay());
      localXMPNode1.setValue(XMPUtils.convertFromDate(new XMPDateTimeImpl(localCalendar)));
      return;
    }
    catch (XMPException localXMPException)
    {
    }
  }

  private static void initDCArrays()
  {
    dcArrayForms = new HashMap();
    PropertyOptions localPropertyOptions1 = new PropertyOptions();
    localPropertyOptions1.setArray(true);
    dcArrayForms.put("dc:contributor", localPropertyOptions1);
    dcArrayForms.put("dc:language", localPropertyOptions1);
    dcArrayForms.put("dc:publisher", localPropertyOptions1);
    dcArrayForms.put("dc:relation", localPropertyOptions1);
    dcArrayForms.put("dc:subject", localPropertyOptions1);
    dcArrayForms.put("dc:type", localPropertyOptions1);
    PropertyOptions localPropertyOptions2 = new PropertyOptions();
    localPropertyOptions2.setArray(true);
    localPropertyOptions2.setArrayOrdered(true);
    dcArrayForms.put("dc:creator", localPropertyOptions2);
    dcArrayForms.put("dc:date", localPropertyOptions2);
    PropertyOptions localPropertyOptions3 = new PropertyOptions();
    localPropertyOptions3.setArray(true);
    localPropertyOptions3.setArrayOrdered(true);
    localPropertyOptions3.setArrayAlternate(true);
    localPropertyOptions3.setArrayAltText(true);
    dcArrayForms.put("dc:description", localPropertyOptions3);
    dcArrayForms.put("dc:rights", localPropertyOptions3);
    dcArrayForms.put("dc:title", localPropertyOptions3);
  }

  private static void migrateAudioCopyright(XMPMeta paramXMPMeta, XMPNode paramXMPNode)
  {
    try
    {
      XMPNode localXMPNode1 = XMPNodeUtils.findSchemaNode(((XMPMetaImpl)paramXMPMeta).getRoot(), "http://purl.org/dc/elements/1.1/", true);
      String str1 = paramXMPNode.getValue();
      XMPNode localXMPNode2 = XMPNodeUtils.findChildNode(localXMPNode1, "dc:rights", false);
      if ((localXMPNode2 == null) || (!localXMPNode2.hasChildren()))
        paramXMPMeta.setLocalizedText("http://purl.org/dc/elements/1.1/", "rights", "", "x-default", "\n\n" + str1, null);
      XMPNode localXMPNode3;
      String str2;
      int j;
      do
        while (true)
        {
          paramXMPNode.getParent().removeChild(paramXMPNode);
          return;
          int i = XMPNodeUtils.lookupLanguageItem(localXMPNode2, "x-default");
          if (i < 0)
          {
            paramXMPMeta.setLocalizedText("http://purl.org/dc/elements/1.1/", "rights", "", "x-default", localXMPNode2.getChild(1).getValue(), null);
            i = XMPNodeUtils.lookupLanguageItem(localXMPNode2, "x-default");
          }
          localXMPNode3 = localXMPNode2.getChild(i);
          str2 = localXMPNode3.getValue();
          j = str2.indexOf("\n\n");
          if (j >= 0)
            break;
          if (str1.equals(str2))
            continue;
          localXMPNode3.setValue(str2 + "\n\n" + str1);
        }
      while (str2.substring(j + 2).equals(str1));
      localXMPNode3.setValue(str2.substring(0, j + 2) + str1);
    }
    catch (XMPException localXMPException)
    {
    }
  }

  private static void moveExplicitAliases(XMPNode paramXMPNode, ParseOptions paramParseOptions)
    throws XMPException
  {
    if (!paramXMPNode.getHasAliases())
      return;
    paramXMPNode.setHasAliases(false);
    boolean bool1 = paramParseOptions.getStrictAliasing();
    Iterator localIterator1 = paramXMPNode.getUnmodifiableChildren().iterator();
    while (true)
    {
      if (localIterator1.hasNext());
      XMPNode localXMPNode1 = (XMPNode)localIterator1.next();
      if (!localXMPNode1.getHasAliases())
        continue;
      Iterator localIterator2 = localXMPNode1.iterateChildren();
      while (localIterator2.hasNext())
      {
        label63: XMPNode localXMPNode2 = (XMPNode)localIterator2.next();
        if (!localXMPNode2.isAlias())
          continue;
        localXMPNode2.setAlias(false);
        XMPAliasInfo localXMPAliasInfo = XMPMetaFactory.getSchemaRegistry().findAlias(localXMPNode2.getName());
        if (localXMPAliasInfo == null)
          continue;
        XMPNode localXMPNode3 = XMPNodeUtils.findSchemaNode(paramXMPNode, localXMPAliasInfo.getNamespace(), null, true);
        localXMPNode3.setImplicit(false);
        XMPNode localXMPNode4 = XMPNodeUtils.findChildNode(localXMPNode3, localXMPAliasInfo.getPrefix() + localXMPAliasInfo.getPropName(), false);
        if (localXMPNode4 == null)
        {
          if (localXMPAliasInfo.getAliasForm().isSimple())
          {
            localXMPNode2.setName(localXMPAliasInfo.getPrefix() + localXMPAliasInfo.getPropName());
            localXMPNode3.addChild(localXMPNode2);
            localIterator2.remove();
          }
          XMPNode localXMPNode6 = new XMPNode(localXMPAliasInfo.getPrefix() + localXMPAliasInfo.getPropName(), localXMPAliasInfo.getAliasForm().toPropertyOptions());
          localXMPNode3.addChild(localXMPNode6);
          transplantArrayItemAlias(localIterator2, localXMPNode2, localXMPNode6);
        }
        if (localXMPAliasInfo.getAliasForm().isSimple())
        {
          if (bool1)
            compareAliasedSubtrees(localXMPNode2, localXMPNode4, true);
          localIterator2.remove();
        }
        int i;
        if (localXMPAliasInfo.getAliasForm().isArrayAltText())
        {
          i = XMPNodeUtils.lookupLanguageItem(localXMPNode4, "x-default");
          localXMPNode5 = null;
          if (i == -1);
        }
        for (XMPNode localXMPNode5 = localXMPNode4.getChild(i); localXMPNode5 == null; localXMPNode5 = localXMPNode4.getChild(1))
        {
          boolean bool2;
          do
          {
            transplantArrayItemAlias(localIterator2, localXMPNode2, localXMPNode4);
            break label63:
            bool2 = localXMPNode4.hasChildren();
            localXMPNode5 = null;
          }
          while (!bool2);
        }
        if (bool1)
          compareAliasedSubtrees(localXMPNode2, localXMPNode5, true);
        localIterator2.remove();
      }
      localXMPNode1.setHasAliases(false);
    }
  }

  private static void normalizeDCArrays(XMPNode paramXMPNode)
    throws XMPException
  {
    int i = 1;
    if (i > paramXMPNode.getChildrenLength())
      label2: return;
    XMPNode localXMPNode1 = paramXMPNode.getChild(i);
    PropertyOptions localPropertyOptions = (PropertyOptions)dcArrayForms.get(localXMPNode1.getName());
    if (localPropertyOptions == null);
    while (true)
    {
      ++i;
      break label2:
      if (localXMPNode1.getOptions().isSimple())
      {
        XMPNode localXMPNode2 = new XMPNode(localXMPNode1.getName(), localPropertyOptions);
        localXMPNode1.setName("[]");
        localXMPNode2.addChild(localXMPNode1);
        paramXMPNode.replaceChild(i, localXMPNode2);
        if ((!localPropertyOptions.isArrayAltText()) || (localXMPNode1.getOptions().getHasLanguage()))
          continue;
        localXMPNode1.addQualifier(new XMPNode("xml:lang", "x-default", null));
      }
      localXMPNode1.getOptions().setOption(7680, false);
      localXMPNode1.getOptions().mergeWith(localPropertyOptions);
      if (!localPropertyOptions.isArrayAltText())
        continue;
      repairAltText(localXMPNode1);
    }
  }

  static XMPMeta process(XMPMetaImpl paramXMPMetaImpl, ParseOptions paramParseOptions)
    throws XMPException
  {
    XMPNode localXMPNode = paramXMPMetaImpl.getRoot();
    touchUpDataModel(paramXMPMetaImpl);
    moveExplicitAliases(localXMPNode, paramParseOptions);
    tweakOldXMP(localXMPNode);
    deleteEmptySchemas(localXMPNode);
    return paramXMPMetaImpl;
  }

  private static void repairAltText(XMPNode paramXMPNode)
    throws XMPException
  {
    if ((paramXMPNode == null) || (!paramXMPNode.getOptions().isArray()))
      return;
    paramXMPNode.getOptions().setArrayOrdered(true).setArrayAlternate(true).setArrayAltText(true);
    Iterator localIterator = paramXMPNode.iterateChildren();
    while (true)
    {
      if (localIterator.hasNext());
      XMPNode localXMPNode = (XMPNode)localIterator.next();
      if (localXMPNode.getOptions().isCompositeProperty())
        localIterator.remove();
      if (localXMPNode.getOptions().getHasLanguage())
        continue;
      String str = localXMPNode.getValue();
      if ((str == null) || (str.length() == 0))
        localIterator.remove();
      localXMPNode.addQualifier(new XMPNode("xml:lang", "x-repair", null));
    }
  }

  private static void touchUpDataModel(XMPMetaImpl paramXMPMetaImpl)
    throws XMPException
  {
    XMPNodeUtils.findSchemaNode(paramXMPMetaImpl.getRoot(), "http://purl.org/dc/elements/1.1/", true);
    Iterator localIterator = paramXMPMetaImpl.getRoot().iterateChildren();
    while (localIterator.hasNext())
    {
      XMPNode localXMPNode1 = (XMPNode)localIterator.next();
      if ("http://purl.org/dc/elements/1.1/".equals(localXMPNode1.getName()))
        normalizeDCArrays(localXMPNode1);
      if ("http://ns.adobe.com/exif/1.0/".equals(localXMPNode1.getName()))
      {
        fixGPSTimeStamp(localXMPNode1);
        XMPNode localXMPNode4 = XMPNodeUtils.findChildNode(localXMPNode1, "exif:UserComment", false);
        if (localXMPNode4 == null)
          continue;
        repairAltText(localXMPNode4);
      }
      if ("http://ns.adobe.com/xmp/1.0/DynamicMedia/".equals(localXMPNode1.getName()))
      {
        XMPNode localXMPNode3 = XMPNodeUtils.findChildNode(localXMPNode1, "xmpDM:copyright", false);
        if (localXMPNode3 == null)
          continue;
        migrateAudioCopyright(paramXMPMetaImpl, localXMPNode3);
      }
      if (!"http://ns.adobe.com/xap/1.0/rights/".equals(localXMPNode1.getName()))
        continue;
      XMPNode localXMPNode2 = XMPNodeUtils.findChildNode(localXMPNode1, "xmpRights:UsageTerms", false);
      if (localXMPNode2 == null)
        continue;
      repairAltText(localXMPNode2);
    }
  }

  private static void transplantArrayItemAlias(Iterator paramIterator, XMPNode paramXMPNode1, XMPNode paramXMPNode2)
    throws XMPException
  {
    if (paramXMPNode2.getOptions().isArrayAltText())
    {
      if (paramXMPNode1.getOptions().getHasLanguage())
        throw new XMPException("Alias to x-default already has a language qualifier", 203);
      paramXMPNode1.addQualifier(new XMPNode("xml:lang", "x-default", null));
    }
    paramIterator.remove();
    paramXMPNode1.setName("[]");
    paramXMPNode2.addChild(paramXMPNode1);
  }

  private static void tweakOldXMP(XMPNode paramXMPNode)
    throws XMPException
  {
    if ((paramXMPNode.getName() != null) && (paramXMPNode.getName().length() >= 36))
    {
      String str = paramXMPNode.getName().toLowerCase();
      if (str.startsWith("uuid:"))
        str = str.substring(5);
      if (Utils.checkUUIDFormat(str))
      {
        XMPNode localXMPNode = XMPNodeUtils.findNode(paramXMPNode, XMPPathParser.expandXPath("http://ns.adobe.com/xap/1.0/mm/", "InstanceID"), true, null);
        if (localXMPNode == null)
          break label113;
        localXMPNode.setOptions(null);
        localXMPNode.setValue("uuid:" + str);
        localXMPNode.removeChildren();
        localXMPNode.removeQualifiers();
        paramXMPNode.setName(null);
      }
    }
    return;
    label113: throw new XMPException("Failure creating xmpMM:InstanceID", 9);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPNormalizer
 * JD-Core Version:    0.5.4
 */