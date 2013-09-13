package com.adobe.xmp.impl;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.PropertyOptions;
import java.util.Calendar;
import java.util.Iterator;

public class XMPMetaImpl
  implements XMPMeta
{
  private String packetHeader = null;
  private XMPNode tree;

  static
  {
    if (!XMPMetaImpl.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public XMPMetaImpl()
  {
    this.tree = new XMPNode(null, null, null);
  }

  public XMPMetaImpl(XMPNode paramXMPNode)
  {
    this.tree = paramXMPNode;
  }

  private Object evaluateNodeValue(int paramInt, XMPNode paramXMPNode)
    throws XMPException
  {
    String str = paramXMPNode.getValue();
    switch (paramInt)
    {
    default:
      if ((str != null) || (paramXMPNode.getOptions().isCompositeProperty()))
        return str;
    case 1:
      return new Boolean(XMPUtils.convertToBoolean(str));
    case 2:
      return new Integer(XMPUtils.convertToInteger(str));
    case 3:
      return new Long(XMPUtils.convertToLong(str));
    case 4:
      return new Double(XMPUtils.convertToDouble(str));
    case 5:
      return XMPUtils.convertToDate(str);
    case 6:
      return XMPUtils.convertToDate(str).getCalendar();
    case 7:
    }
    return XMPUtils.decodeBase64(str);
    return "";
  }

  public Object clone()
  {
    return new XMPMetaImpl((XMPNode)this.tree.clone());
  }

  public boolean doesPropertyExist(String paramString1, String paramString2)
  {
    try
    {
      ParameterAsserts.assertSchemaNS(paramString1);
      ParameterAsserts.assertPropName(paramString2);
      XMPPath localXMPPath = XMPPathParser.expandXPath(paramString1, paramString2);
      XMPNode localXMPNode = XMPNodeUtils.findNode(this.tree, localXMPPath, false, null);
      int i = 0;
      if (localXMPNode != null)
        i = 1;
      return i;
    }
    catch (XMPException localXMPException)
    {
    }
    return false;
  }

  public String getPacketHeader()
  {
    return this.packetHeader;
  }

  public Boolean getPropertyBoolean(String paramString1, String paramString2)
    throws XMPException
  {
    return (Boolean)getPropertyObject(paramString1, paramString2, 1);
  }

  public Calendar getPropertyCalendar(String paramString1, String paramString2)
    throws XMPException
  {
    return (Calendar)getPropertyObject(paramString1, paramString2, 6);
  }

  public Integer getPropertyInteger(String paramString1, String paramString2)
    throws XMPException
  {
    return (Integer)getPropertyObject(paramString1, paramString2, 2);
  }

  protected Object getPropertyObject(String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertPropName(paramString2);
    XMPPath localXMPPath = XMPPathParser.expandXPath(paramString1, paramString2);
    XMPNode localXMPNode = XMPNodeUtils.findNode(this.tree, localXMPPath, false, null);
    Object localObject = null;
    if (localXMPNode != null)
    {
      if ((paramInt != 0) && (localXMPNode.getOptions().isCompositeProperty()))
        throw new XMPException("Property must be simple when a value type is requested", 102);
      localObject = evaluateNodeValue(paramInt, localXMPNode);
    }
    return localObject;
  }

  public String getPropertyString(String paramString1, String paramString2)
    throws XMPException
  {
    return (String)getPropertyObject(paramString1, paramString2, 0);
  }

  public XMPNode getRoot()
  {
    return this.tree;
  }

  public void setLocalizedText(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertArrayName(paramString2);
    ParameterAsserts.assertSpecificLang(paramString4);
    if (paramString3 != null);
    String str2;
    XMPNode localXMPNode1;
    for (String str1 = Utils.normalizeLangValue(paramString3); ; str1 = null)
    {
      str2 = Utils.normalizeLangValue(paramString4);
      XMPPath localXMPPath = XMPPathParser.expandXPath(paramString1, paramString2);
      localXMPNode1 = XMPNodeUtils.findNode(this.tree, localXMPPath, true, new PropertyOptions(7680));
      if (localXMPNode1 != null)
        break;
      throw new XMPException("Failed to find or create array node", 102);
    }
    if (!localXMPNode1.getOptions().isArrayAltText())
    {
      if ((localXMPNode1.hasChildren()) || (!localXMPNode1.getOptions().isArrayAlternate()))
        break label198;
      localXMPNode1.getOptions().setArrayAltText(true);
    }
    Iterator localIterator1 = localXMPNode1.iterateChildren();
    XMPNode localXMPNode4;
    do
    {
      boolean bool1 = localIterator1.hasNext();
      i = 0;
      localObject = null;
      if (!bool1)
        break label234;
      localXMPNode4 = (XMPNode)localIterator1.next();
      if ((localXMPNode4.hasQualifier()) && ("xml:lang".equals(localXMPNode4.getQualifier(1).getName())))
        continue;
      throw new XMPException("Language qualifier must be first", 102);
      label198: throw new XMPException("Specified property is no alt-text array", 102);
    }
    while (!"x-default".equals(localXMPNode4.getQualifier(1).getValue()));
    Object localObject = localXMPNode4;
    int i = 1;
    if ((localObject != null) && (localXMPNode1.getChildrenLength() > 1))
    {
      label234: localXMPNode1.removeChild(localObject);
      localXMPNode1.addChild(1, localObject);
    }
    Object[] arrayOfObject = XMPNodeUtils.chooseLocalizedText(localXMPNode1, str1, str2);
    int j = ((Integer)arrayOfObject[0]).intValue();
    XMPNode localXMPNode2 = (XMPNode)arrayOfObject[1];
    boolean bool2 = "x-default".equals(str2);
    switch (j)
    {
    default:
      throw new XMPException("Unexpected result from ChooseLocalizedText", 9);
    case 0:
      XMPNodeUtils.appendLangItem(localXMPNode1, "x-default", paramString5);
      i = 1;
      if (bool2)
        break label382;
      XMPNodeUtils.appendLangItem(localXMPNode1, str2, paramString5);
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    }
    while (true)
    {
      if ((i == 0) && (localXMPNode1.getChildrenLength() == 1))
        label382: XMPNodeUtils.appendLangItem(localXMPNode1, "x-default", paramString5);
      return;
      if (!bool2)
      {
        if ((i != 0) && (localObject != localXMPNode2) && (localObject != null) && (localObject.getValue().equals(localXMPNode2.getValue())))
          localObject.setValue(paramString5);
        localXMPNode2.setValue(paramString5);
      }
      assert ((i != 0) && (localObject == localXMPNode2));
      Iterator localIterator2 = localXMPNode1.iterateChildren();
      label494: XMPNode localXMPNode3;
      do
      {
        if (!localIterator2.hasNext())
          break label568;
        localXMPNode3 = (XMPNode)localIterator2.next();
      }
      while (localXMPNode3 == localObject);
      String str3 = localXMPNode3.getValue();
      if (localObject != null);
      for (String str4 = localObject.getValue(); ; str4 = null)
      {
        if (str3.equals(str4));
        localXMPNode3.setValue(paramString5);
        break label494:
      }
      label568: if (localObject == null)
        continue;
      localObject.setValue(paramString5);
      continue;
      if ((i != 0) && (localObject != localXMPNode2) && (localObject != null) && (localObject.getValue().equals(localXMPNode2.getValue())))
        localObject.setValue(paramString5);
      localXMPNode2.setValue(paramString5);
      continue;
      XMPNodeUtils.appendLangItem(localXMPNode1, str2, paramString5);
      if (!bool2)
        continue;
      i = 1;
      continue;
      if ((localObject != null) && (localXMPNode1.getChildrenLength() == 1))
        localObject.setValue(paramString5);
      XMPNodeUtils.appendLangItem(localXMPNode1, str2, paramString5);
      continue;
      XMPNodeUtils.appendLangItem(localXMPNode1, str2, paramString5);
      if (!bool2)
        continue;
      i = 1;
    }
  }

  void setNode(XMPNode paramXMPNode, Object paramObject, PropertyOptions paramPropertyOptions, boolean paramBoolean)
    throws XMPException
  {
    if (paramBoolean)
      paramXMPNode.clear();
    paramXMPNode.getOptions().mergeWith(paramPropertyOptions);
    if (!paramXMPNode.getOptions().isCompositeProperty())
    {
      XMPNodeUtils.setNodeValue(paramXMPNode, paramObject);
      return;
    }
    if ((paramObject != null) && (paramObject.toString().length() > 0))
      throw new XMPException("Composite nodes can't have values", 102);
    paramXMPNode.removeChildren();
  }

  public void setPacketHeader(String paramString)
  {
    this.packetHeader = paramString;
  }

  public void setProperty(String paramString1, String paramString2, Object paramObject)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramObject, null);
  }

  public void setProperty(String paramString1, String paramString2, Object paramObject, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    ParameterAsserts.assertSchemaNS(paramString1);
    ParameterAsserts.assertPropName(paramString2);
    PropertyOptions localPropertyOptions = XMPNodeUtils.verifySetOptions(paramPropertyOptions, paramObject);
    XMPPath localXMPPath = XMPPathParser.expandXPath(paramString1, paramString2);
    XMPNode localXMPNode = XMPNodeUtils.findNode(this.tree, localXMPPath, true, localPropertyOptions);
    if (localXMPNode != null)
    {
      setNode(localXMPNode, paramObject, localPropertyOptions, false);
      return;
    }
    throw new XMPException("Specified property does not exist", 102);
  }

  public void setPropertyBoolean(String paramString1, String paramString2, boolean paramBoolean)
    throws XMPException
  {
    if (paramBoolean);
    for (String str = "True"; ; str = "False")
    {
      setProperty(paramString1, paramString2, str, null);
      return;
    }
  }

  public void setPropertyDate(String paramString1, String paramString2, XMPDateTime paramXMPDateTime)
    throws XMPException
  {
    setProperty(paramString1, paramString2, paramXMPDateTime, null);
  }

  public void setPropertyDouble(String paramString1, String paramString2, double paramDouble)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Double(paramDouble), null);
  }

  public void setPropertyInteger(String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    setProperty(paramString1, paramString2, new Integer(paramInt), null);
  }

  public void sort()
  {
    this.tree.sort();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPMetaImpl
 * JD-Core Version:    0.5.4
 */