package com.adobe.xmp.impl;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPDateTimeFactory;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathSegment;
import com.adobe.xmp.options.PropertyOptions;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class XMPNodeUtils
{
  static
  {
    if (!XMPNodeUtils.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  static void appendLangItem(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    XMPNode localXMPNode1 = new XMPNode("[]", paramString2, null);
    XMPNode localXMPNode2 = new XMPNode("xml:lang", paramString1, null);
    localXMPNode1.addQualifier(localXMPNode2);
    if (!"x-default".equals(localXMPNode2.getValue()))
    {
      paramXMPNode.addChild(localXMPNode1);
      return;
    }
    paramXMPNode.addChild(1, localXMPNode1);
  }

  static Object[] chooseLocalizedText(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    if (!paramXMPNode.getOptions().isArrayAltText())
      throw new XMPException("Localized text array is not alt-text", 102);
    if (!paramXMPNode.hasChildren())
    {
      Object[] arrayOfObject6 = new Object[2];
      arrayOfObject6[0] = new Integer(0);
      arrayOfObject6[1] = null;
      return arrayOfObject6;
    }
    int i = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    Iterator localIterator = paramXMPNode.iterateChildren();
    while (localIterator.hasNext())
    {
      XMPNode localXMPNode = (XMPNode)localIterator.next();
      if (localXMPNode.getOptions().isCompositeProperty())
        throw new XMPException("Alt-text array item is not simple", 102);
      if ((!localXMPNode.hasQualifier()) || (!"xml:lang".equals(localXMPNode.getQualifier(1).getName())))
        throw new XMPException("Alt-text array item has no language qualifier", 102);
      String str = localXMPNode.getQualifier(1).getValue();
      if (paramString2.equals(str))
      {
        Object[] arrayOfObject5 = new Object[2];
        arrayOfObject5[0] = new Integer(1);
        arrayOfObject5[1] = localXMPNode;
        return arrayOfObject5;
      }
      if ((paramString1 != null) && (str.startsWith(paramString1)))
      {
        if (localObject1 == null)
          localObject1 = localXMPNode;
        ++i;
      }
      if (!"x-default".equals(str))
        continue;
      localObject2 = localXMPNode;
    }
    if (i == 1)
    {
      Object[] arrayOfObject4 = new Object[2];
      arrayOfObject4[0] = new Integer(2);
      arrayOfObject4[1] = localObject1;
      return arrayOfObject4;
    }
    if (i > 1)
    {
      Object[] arrayOfObject3 = new Object[2];
      arrayOfObject3[0] = new Integer(3);
      arrayOfObject3[1] = localObject1;
      return arrayOfObject3;
    }
    if (localObject2 != null)
    {
      Object[] arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = new Integer(4);
      arrayOfObject2[1] = localObject2;
      return arrayOfObject2;
    }
    Object[] arrayOfObject1 = new Object[2];
    arrayOfObject1[0] = new Integer(5);
    arrayOfObject1[1] = paramXMPNode.getChild(1);
    return arrayOfObject1;
  }

  static void deleteNode(XMPNode paramXMPNode)
  {
    XMPNode localXMPNode = paramXMPNode.getParent();
    if (paramXMPNode.getOptions().isQualifier())
      localXMPNode.removeQualifier(paramXMPNode);
    while (true)
    {
      if ((!localXMPNode.hasChildren()) && (localXMPNode.getOptions().isSchemaNode()))
        localXMPNode.getParent().removeChild(localXMPNode);
      return;
      localXMPNode.removeChild(paramXMPNode);
    }
  }

  static void detectAltText(XMPNode paramXMPNode)
  {
    if ((!paramXMPNode.getOptions().isArrayAlternate()) || (!paramXMPNode.hasChildren()))
      return;
    Iterator localIterator = paramXMPNode.iterateChildren();
    do
    {
      boolean bool = localIterator.hasNext();
      i = 0;
      if (!bool)
        break label55;
    }
    while (!((XMPNode)localIterator.next()).getOptions().getHasLanguage());
    int i = 1;
    if (i == 0)
      label55: return;
    paramXMPNode.getOptions().setArrayAltText(true);
    normalizeLangArray(paramXMPNode);
  }

  static XMPNode findChildNode(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    if ((!paramXMPNode.getOptions().isSchemaNode()) && (!paramXMPNode.getOptions().isStruct()))
    {
      if (!paramXMPNode.isImplicit())
        throw new XMPException("Named children only allowed for schemas and structs", 102);
      if (paramXMPNode.getOptions().isArray())
        throw new XMPException("Named children not allowed for arrays", 102);
      if (paramBoolean)
        paramXMPNode.getOptions().setStruct(true);
    }
    XMPNode localXMPNode = paramXMPNode.findChildByName(paramString);
    if ((localXMPNode == null) && (paramBoolean))
    {
      localXMPNode = new XMPNode(paramString, new PropertyOptions());
      localXMPNode.setImplicit(true);
      paramXMPNode.addChild(localXMPNode);
    }
    assert ((localXMPNode != null) || (!paramBoolean));
    return localXMPNode;
  }

  private static int findIndexedItem(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    int i;
    try
    {
      i = Integer.parseInt(paramString.substring(1, -1 + paramString.length()));
      if (i < 1)
        throw new XMPException("Array index must be larger than zero", 102);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new XMPException("Array index not digits.", 102);
    }
    if ((paramBoolean) && (i == 1 + paramXMPNode.getChildrenLength()))
    {
      XMPNode localXMPNode = new XMPNode("[]", null);
      localXMPNode.setImplicit(true);
      paramXMPNode.addChild(localXMPNode);
    }
    return i;
  }

  static XMPNode findNode(XMPNode paramXMPNode, XMPPath paramXMPPath, boolean paramBoolean, PropertyOptions paramPropertyOptions)
    throws XMPException
  {
    if ((paramXMPPath == null) || (paramXMPPath.size() == 0))
      throw new XMPException("Empty XMPPath", 102);
    XMPNode localXMPNode1 = findSchemaNode(paramXMPNode, paramXMPPath.getSegment(0).getName(), paramBoolean);
    if (localXMPNode1 == null)
      return null;
    boolean bool = localXMPNode1.isImplicit();
    XMPNode localXMPNode2 = null;
    if (bool)
    {
      localXMPNode1.setImplicit(false);
      localXMPNode2 = localXMPNode1;
    }
    for (int i = 1; ; ++i)
    {
      try
      {
        if (i >= paramXMPPath.size())
          break label240;
        localXMPNode1 = followXPathStep(localXMPNode1, paramXMPPath.getSegment(i), paramBoolean);
        if (localXMPNode1 == null)
        {
          if (paramBoolean);
          deleteNode(localXMPNode2);
          return null;
        }
      }
      catch (XMPException localXMPException)
      {
        if (localXMPNode2 != null)
          deleteNode(localXMPNode2);
        throw localXMPException;
      }
      if (!localXMPNode1.isImplicit())
        continue;
      localXMPNode1.setImplicit(false);
      if ((i == 1) && (paramXMPPath.getSegment(i).isAlias()) && (paramXMPPath.getSegment(i).getAliasForm() != 0))
      {
        localXMPNode1.getOptions().setOption(paramXMPPath.getSegment(i).getAliasForm(), true);
      }
      else if ((i < -1 + paramXMPPath.size()) && (paramXMPPath.getSegment(i).getKind() == 1) && (!localXMPNode1.getOptions().isCompositeProperty()))
      {
        localXMPNode1.getOptions().setStruct(true);
        break label267:
        if (localXMPNode2 != null)
        {
          label240: localXMPNode1.getOptions().mergeWith(paramPropertyOptions);
          localXMPNode1.setOptions(localXMPNode1.getOptions());
        }
        return localXMPNode1;
      }
      label267: if (localXMPNode2 != null)
        continue;
      localXMPNode2 = localXMPNode1;
    }
  }

  private static XMPNode findQualifierNode(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    assert (!paramString.startsWith("?"));
    XMPNode localXMPNode = paramXMPNode.findQualifierByName(paramString);
    if ((localXMPNode == null) && (paramBoolean))
    {
      localXMPNode = new XMPNode(paramString, null);
      localXMPNode.setImplicit(true);
      paramXMPNode.addQualifier(localXMPNode);
    }
    return localXMPNode;
  }

  static XMPNode findSchemaNode(XMPNode paramXMPNode, String paramString1, String paramString2, boolean paramBoolean)
    throws XMPException
  {
    assert (paramXMPNode.getParent() == null);
    XMPNode localXMPNode = paramXMPNode.findChildByName(paramString1);
    if ((localXMPNode == null) && (paramBoolean))
    {
      localXMPNode = new XMPNode(paramString1, new PropertyOptions().setSchemaNode(true));
      localXMPNode.setImplicit(true);
      String str = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(paramString1);
      if (str == null)
      {
        if ((paramString2 == null) || (paramString2.length() == 0))
          break label119;
        str = XMPMetaFactory.getSchemaRegistry().registerNamespace(paramString1, paramString2);
      }
      localXMPNode.setValue(str);
      paramXMPNode.addChild(localXMPNode);
    }
    return localXMPNode;
    label119: throw new XMPException("Unregistered schema namespace URI", 101);
  }

  static XMPNode findSchemaNode(XMPNode paramXMPNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    return findSchemaNode(paramXMPNode, paramString, null, paramBoolean);
  }

  private static XMPNode followXPathStep(XMPNode paramXMPNode, XMPPathSegment paramXMPPathSegment, boolean paramBoolean)
    throws XMPException
  {
    int i = paramXMPPathSegment.getKind();
    XMPNode localXMPNode;
    if (i == 1)
    {
      localXMPNode = findChildNode(paramXMPNode, paramXMPPathSegment.getName(), paramBoolean);
      return localXMPNode;
    }
    if (i == 2)
      return findQualifierNode(paramXMPNode, paramXMPPathSegment.getName().substring(1), paramBoolean);
    if (!paramXMPNode.getOptions().isArray())
      throw new XMPException("Indexing applied to non-array", 102);
    if (i == 3);
    String[] arrayOfString1;
    for (int j = findIndexedItem(paramXMPNode, paramXMPPathSegment.getName(), paramBoolean); ; j = lookupQualSelector(paramXMPNode, arrayOfString1[0], arrayOfString1[1], paramXMPPathSegment.getAliasForm()))
    {
      while (true)
      {
        localXMPNode = null;
        if (1 <= j);
        int k = paramXMPNode.getChildrenLength();
        localXMPNode = null;
        if (j <= k);
        return paramXMPNode.getChild(j);
        if (i == 4)
          j = paramXMPNode.getChildrenLength();
        if (i != 6)
          break;
        String[] arrayOfString2 = Utils.splitNameAndValue(paramXMPPathSegment.getName());
        j = lookupFieldSelector(paramXMPNode, arrayOfString2[0], arrayOfString2[1]);
      }
      if (i != 5)
        break;
      arrayOfString1 = Utils.splitNameAndValue(paramXMPPathSegment.getName());
    }
    throw new XMPException("Unknown array indexing step in FollowXPathStep", 9);
  }

  private static int lookupFieldSelector(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    int i = -1;
    for (int j = 1; (j <= paramXMPNode.getChildrenLength()) && (i < 0); ++j)
    {
      XMPNode localXMPNode1 = paramXMPNode.getChild(j);
      if (!localXMPNode1.getOptions().isStruct())
        throw new XMPException("Field selector must be used on array of struct", 102);
      int k = 1;
      label53: if (k > localXMPNode1.getChildrenLength())
        continue;
      XMPNode localXMPNode2 = localXMPNode1.getChild(k);
      if (!paramString1.equals(localXMPNode2.getName()));
      do
      {
        ++k;
        break label53:
      }
      while (!paramString2.equals(localXMPNode2.getValue()));
      i = j;
    }
    return i;
  }

  static int lookupLanguageItem(XMPNode paramXMPNode, String paramString)
    throws XMPException
  {
    if (!paramXMPNode.getOptions().isArray())
      throw new XMPException("Language item must be used on array", 102);
    int i = 1;
    if (i <= paramXMPNode.getChildrenLength())
    {
      label25: XMPNode localXMPNode = paramXMPNode.getChild(i);
      if ((!localXMPNode.hasQualifier()) || (!"xml:lang".equals(localXMPNode.getQualifier(1).getName())));
      while (true)
      {
        ++i;
        break label25:
        if (paramString.equals(localXMPNode.getQualifier(1).getValue()))
          return i;
      }
    }
    return -1;
  }

  private static int lookupQualSelector(XMPNode paramXMPNode, String paramString1, String paramString2, int paramInt)
    throws XMPException
  {
    if ("xml:lang".equals(paramString1))
    {
      int j = lookupLanguageItem(paramXMPNode, Utils.normalizeLangValue(paramString2));
      if ((j < 0) && ((paramInt & 0x1000) > 0))
      {
        XMPNode localXMPNode2 = new XMPNode("[]", null);
        localXMPNode2.addQualifier(new XMPNode("xml:lang", "x-default", null));
        paramXMPNode.addChild(1, localXMPNode2);
        return 1;
      }
      return j;
    }
    for (int i = 1; i < paramXMPNode.getChildrenLength(); ++i)
    {
      Iterator localIterator = paramXMPNode.getChild(i).iterateQualifier();
      XMPNode localXMPNode1;
      do
      {
        if (!localIterator.hasNext())
          break label145;
        localXMPNode1 = (XMPNode)localIterator.next();
      }
      while ((!paramString1.equals(localXMPNode1.getName())) || (!paramString2.equals(localXMPNode1.getValue())));
      label145: return i;
    }
    return -1;
  }

  static void normalizeLangArray(XMPNode paramXMPNode)
  {
    if (!paramXMPNode.getOptions().isArrayAltText())
      return;
    for (int i = 2; ; ++i)
    {
      if (i <= paramXMPNode.getChildrenLength());
      XMPNode localXMPNode = paramXMPNode.getChild(i);
      if ((!localXMPNode.hasQualifier()) || (!"x-default".equals(localXMPNode.getQualifier(1).getValue())))
        continue;
      try
      {
        paramXMPNode.removeChild(i);
        paramXMPNode.addChild(1, localXMPNode);
        if (i == 2);
        paramXMPNode.getChild(2).setValue(localXMPNode.getValue());
        return;
      }
      catch (XMPException localXMPException)
      {
        if (!$assertionsDisabled);
        throw new AssertionError();
      }
    }
  }

  static String serializeNodeValue(Object paramObject)
  {
    if (paramObject == null);
    for (String str = null; str != null; str = paramObject.toString())
      while (true)
      {
        return Utils.removeControlChars(str);
        if (paramObject instanceof Boolean)
          str = XMPUtils.convertFromBoolean(((Boolean)paramObject).booleanValue());
        if (paramObject instanceof Integer)
          str = XMPUtils.convertFromInteger(((Integer)paramObject).intValue());
        if (paramObject instanceof Long)
          str = XMPUtils.convertFromLong(((Long)paramObject).longValue());
        if (paramObject instanceof Double)
          str = XMPUtils.convertFromDouble(((Double)paramObject).doubleValue());
        if (paramObject instanceof XMPDateTime)
          str = XMPUtils.convertFromDate((XMPDateTime)paramObject);
        if (paramObject instanceof GregorianCalendar)
          str = XMPUtils.convertFromDate(XMPDateTimeFactory.createFromCalendar((GregorianCalendar)paramObject));
        if (!paramObject instanceof byte[])
          break;
        str = XMPUtils.encodeBase64((byte[])(byte[])paramObject);
      }
    return null;
  }

  static void setNodeValue(XMPNode paramXMPNode, Object paramObject)
  {
    String str = serializeNodeValue(paramObject);
    if ((!paramXMPNode.getOptions().isQualifier()) || (!"xml:lang".equals(paramXMPNode.getName())))
    {
      paramXMPNode.setValue(str);
      return;
    }
    paramXMPNode.setValue(Utils.normalizeLangValue(str));
  }

  static PropertyOptions verifySetOptions(PropertyOptions paramPropertyOptions, Object paramObject)
    throws XMPException
  {
    if (paramPropertyOptions == null)
      paramPropertyOptions = new PropertyOptions();
    if (paramPropertyOptions.isArrayAltText())
      paramPropertyOptions.setArrayAlternate(true);
    if (paramPropertyOptions.isArrayAlternate())
      paramPropertyOptions.setArrayOrdered(true);
    if (paramPropertyOptions.isArrayOrdered())
      paramPropertyOptions.setArray(true);
    if ((paramPropertyOptions.isCompositeProperty()) && (paramObject != null) && (paramObject.toString().length() > 0))
      throw new XMPException("Structs and arrays can't have values", 103);
    paramPropertyOptions.assertConsistency(paramPropertyOptions.getOptions());
    return paramPropertyOptions;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPNodeUtils
 * JD-Core Version:    0.5.4
 */