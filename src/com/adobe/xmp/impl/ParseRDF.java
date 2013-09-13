package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPAliasInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseRDF
{
  static
  {
    if (!ParseRDF.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  private static XMPNode addChildNode(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, String paramString, boolean paramBoolean)
    throws XMPException
  {
    XMPSchemaRegistry localXMPSchemaRegistry = XMPMetaFactory.getSchemaRegistry();
    String str1 = paramNode.getNamespaceURI();
    String str4;
    label65: boolean bool2;
    boolean bool3;
    XMPNode localXMPNode1;
    if (str1 != null)
    {
      if ("http://purl.org/dc/1.1/".equals(str1))
        str1 = "http://purl.org/dc/elements/1.1/";
      String str2 = localXMPSchemaRegistry.getNamespacePrefix(str1);
      if (str2 == null)
      {
        if (paramNode.getPrefix() == null)
          break label263;
        str4 = paramNode.getPrefix();
        str2 = localXMPSchemaRegistry.registerNamespace(str1, str4);
      }
      String str3 = str2 + paramNode.getLocalName();
      PropertyOptions localPropertyOptions = new PropertyOptions();
      boolean bool1 = false;
      if (paramBoolean)
      {
        XMPNode localXMPNode2 = XMPNodeUtils.findSchemaNode(paramXMPMetaImpl.getRoot(), str1, "_dflt", true);
        localXMPNode2.setImplicit(false);
        paramXMPNode = localXMPNode2;
        XMPAliasInfo localXMPAliasInfo = localXMPSchemaRegistry.findAlias(str3);
        bool1 = false;
        if (localXMPAliasInfo != null)
        {
          bool1 = true;
          paramXMPMetaImpl.getRoot().setHasAliases(true);
          localXMPNode2.setHasAliases(true);
        }
      }
      bool2 = "rdf:li".equals(str3);
      bool3 = "rdf:value".equals(str3);
      localXMPNode1 = new XMPNode(str3, paramString, localPropertyOptions);
      localXMPNode1.setAlias(bool1);
      if (bool3)
        break label283;
      paramXMPNode.addChild(localXMPNode1);
    }
    while (true)
    {
      if (!bool3)
        break label298;
      if ((!paramBoolean) && (paramXMPNode.getOptions().isStruct()))
        break;
      throw new XMPException("Misplaced rdf:value element", 202);
      label263: str4 = "_dflt";
      break label65:
      throw new XMPException("XML namespace required for all elements and attributes", 202);
      label283: paramXMPNode.addChild(1, localXMPNode1);
    }
    paramXMPNode.setHasValueChild(true);
    if (bool2)
    {
      if (!paramXMPNode.getOptions().isArray())
        label298: throw new XMPException("Misplaced rdf:li element", 202);
      localXMPNode1.setName("[]");
    }
    return localXMPNode1;
  }

  private static XMPNode addQualifierNode(XMPNode paramXMPNode, String paramString1, String paramString2)
    throws XMPException
  {
    if ("xml:lang".equals(paramString1))
      paramString2 = Utils.normalizeLangValue(paramString2);
    XMPNode localXMPNode = new XMPNode(paramString1, paramString2, null);
    paramXMPNode.addQualifier(localXMPNode);
    return localXMPNode;
  }

  private static void fixupQualifiedNode(XMPNode paramXMPNode)
    throws XMPException
  {
    assert ((paramXMPNode.getOptions().isStruct()) && (paramXMPNode.hasChildren()));
    XMPNode localXMPNode1 = paramXMPNode.getChild(1);
    assert ("rdf:value".equals(localXMPNode1.getName()));
    if (localXMPNode1.getOptions().getHasLanguage())
    {
      if (paramXMPNode.getOptions().getHasLanguage())
        throw new XMPException("Redundant xml:lang for rdf:value element", 203);
      XMPNode localXMPNode2 = localXMPNode1.getQualifier(1);
      localXMPNode1.removeQualifier(localXMPNode2);
      paramXMPNode.addQualifier(localXMPNode2);
    }
    for (int i = 1; i <= localXMPNode1.getQualifierLength(); ++i)
      paramXMPNode.addQualifier(localXMPNode1.getQualifier(i));
    for (int j = 2; j <= paramXMPNode.getChildrenLength(); ++j)
      paramXMPNode.addQualifier(paramXMPNode.getChild(j));
    assert ((paramXMPNode.getOptions().isStruct()) || (paramXMPNode.getHasValueChild()));
    paramXMPNode.setHasValueChild(false);
    paramXMPNode.getOptions().setStruct(false);
    paramXMPNode.getOptions().mergeWith(localXMPNode1.getOptions());
    paramXMPNode.setValue(localXMPNode1.getValue());
    paramXMPNode.removeChildren();
    Iterator localIterator = localXMPNode1.iterateChildren();
    while (localIterator.hasNext())
      paramXMPNode.addChild((XMPNode)localIterator.next());
  }

  private static int getRDFTermKind(Node paramNode)
  {
    String str1 = paramNode.getLocalName();
    String str2 = paramNode.getNamespaceURI();
    if ((str2 == null) && ((("about".equals(str1)) || ("ID".equals(str1)))) && (paramNode instanceof Attr) && ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(((Attr)paramNode).getOwnerElement().getNamespaceURI())))
      str2 = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2))
    {
      if ("li".equals(str1))
        return 9;
      if ("parseType".equals(str1))
        return 4;
      if ("Description".equals(str1))
        return 8;
      if ("about".equals(str1))
        return 3;
      if ("resource".equals(str1))
        return 5;
      if ("RDF".equals(str1))
        return 1;
      if ("ID".equals(str1))
        return 2;
      if ("nodeID".equals(str1))
        return 6;
      if ("datatype".equals(str1))
        return 7;
      if ("aboutEach".equals(str1))
        return 10;
      if ("aboutEachPrefix".equals(str1))
        return 11;
      if ("bagID".equals(str1))
        return 12;
    }
    return 0;
  }

  private static boolean isCoreSyntaxTerm(int paramInt)
  {
    return (1 <= paramInt) && (paramInt <= 7);
  }

  private static boolean isOldTerm(int paramInt)
  {
    return (10 <= paramInt) && (paramInt <= 12);
  }

  private static boolean isPropertyElementName(int paramInt)
  {
    if ((paramInt == 8) || (isOldTerm(paramInt)));
    do
      return false;
    while (isCoreSyntaxTerm(paramInt));
    return true;
  }

  private static boolean isWhitespaceNode(Node paramNode)
  {
    if (paramNode.getNodeType() != 3)
      return false;
    String str = paramNode.getNodeValue();
    for (int i = 0; i < str.length(); ++i)
      if (!Character.isWhitespace(str.charAt(i)));
    return true;
  }

  static XMPMetaImpl parse(Node paramNode)
    throws XMPException
  {
    XMPMetaImpl localXMPMetaImpl = new XMPMetaImpl();
    rdf_RDF(localXMPMetaImpl, paramNode);
    return localXMPMetaImpl;
  }

  private static void rdf_EmptyPropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int l = 0;
    Object localObject = null;
    if (paramNode.hasChildNodes())
      throw new XMPException("Nested content not allowed with rdf:resource or property attributes", 202);
    int i1 = 0;
    if (i1 < paramNode.getAttributes().getLength())
    {
      label41: Node localNode2 = paramNode.getAttributes().item(i1);
      if (("xmlns".equals(localNode2.getPrefix())) || ((localNode2.getPrefix() == null) && ("xmlns".equals(localNode2.getNodeName()))));
      while (true)
      {
        ++i1;
        break label41:
        switch (getRDFTermKind(localNode2))
        {
        case 2:
        case 1:
        case 3:
        case 4:
        default:
          throw new XMPException("Unrecognized attribute of empty property element", 202);
        case 5:
          if (k != 0)
            throw new XMPException("Empty property element can't have both rdf:resource and rdf:nodeID", 202);
          if (l != 0)
            throw new XMPException("Empty property element can't have both rdf:value and rdf:resource", 203);
          j = 1;
          if (l != 0)
            continue;
          localObject = localNode2;
          break;
        case 6:
          if (j != 0)
            throw new XMPException("Empty property element can't have both rdf:resource and rdf:nodeID", 202);
          k = 1;
          break;
        case 0:
        }
        if (("value".equals(localNode2.getLocalName())) && ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(localNode2.getNamespaceURI())))
        {
          if (j != 0)
            throw new XMPException("Empty property element can't have both rdf:value and rdf:resource", 203);
          l = 1;
          localObject = localNode2;
        }
        if ("xml:lang".equals(localNode2.getNodeName()))
          continue;
        i = 1;
      }
    }
    XMPNode localXMPNode = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, "", paramBoolean);
    String str;
    label377: int i2;
    label402: int i3;
    label405: Node localNode1;
    if ((l != 0) || (j != 0))
      if (localObject != null)
      {
        str = localObject.getNodeValue();
        localXMPNode.setValue(str);
        i2 = 0;
        if (l == 0)
          localXMPNode.getOptions().setURI(true);
        i3 = 0;
        if (i3 >= paramNode.getAttributes().getLength())
          return;
        localNode1 = paramNode.getAttributes().item(i3);
        if ((localNode1 != localObject) && (!"xmlns".equals(localNode1.getPrefix())) && (((localNode1.getPrefix() != null) || (!"xmlns".equals(localNode1.getNodeName())))))
          break label523;
      }
    while (true)
    {
      ++i3;
      break label405:
      str = "";
      break label377:
      i2 = 0;
      if (i != 0);
      localXMPNode.getOptions().setStruct(true);
      i2 = 1;
      break label402:
      switch (getRDFTermKind(localNode1))
      {
      case 2:
      case 6:
      case 1:
      case 3:
      case 4:
      default:
        throw new XMPException("Unrecognized attribute of empty property element", 202);
      case 5:
        label523: addQualifierNode(localXMPNode, "rdf:resource", localNode1.getNodeValue());
        break;
      case 0:
      }
      if (i2 == 0)
        addQualifierNode(localXMPNode, localNode1.getNodeName(), localNode1.getNodeValue());
      if ("xml:lang".equals(localNode1.getNodeName()))
        addQualifierNode(localXMPNode, "xml:lang", localNode1.getNodeValue());
      addChildNode(paramXMPMetaImpl, localXMPNode, localNode1, localNode1.getNodeValue(), false);
    }
  }

  private static void rdf_LiteralPropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    XMPNode localXMPNode = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, null, paramBoolean);
    int i = 0;
    if (i < paramNode.getAttributes().getLength())
    {
      label13: Node localNode2 = paramNode.getAttributes().item(i);
      if (("xmlns".equals(localNode2.getPrefix())) || ((localNode2.getPrefix() == null) && ("xmlns".equals(localNode2.getNodeName()))));
      String str2;
      String str3;
      do
        while (true)
        {
          ++i;
          break label13:
          str2 = localNode2.getNamespaceURI();
          str3 = localNode2.getLocalName();
          if (!"xml:lang".equals(localNode2.getNodeName()))
            break;
          addQualifierNode(localXMPNode, "xml:lang", localNode2.getNodeValue());
        }
      while (("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2)) && ((("ID".equals(str3)) || ("datatype".equals(str3)))));
      throw new XMPException("Invalid attribute for literal property element", 202);
    }
    String str1 = "";
    for (int j = 0; ; ++j)
    {
      if (j >= paramNode.getChildNodes().getLength())
        break label284;
      Node localNode1 = paramNode.getChildNodes().item(j);
      if (localNode1.getNodeType() != 3)
        break;
      str1 = str1 + localNode1.getNodeValue();
    }
    throw new XMPException("Invalid child of literal property element", 202);
    label284: localXMPNode.setValue(str1);
  }

  private static void rdf_NodeElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int i = getRDFTermKind(paramNode);
    if ((i != 8) && (i != 0))
      throw new XMPException("Node element must be rdf:Description or typed node", 202);
    if ((paramBoolean) && (i == 0))
      throw new XMPException("Top level typed node not allowed", 203);
    rdf_NodeElementAttrs(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
    rdf_PropertyElementList(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
  }

  private static void rdf_NodeElementAttrs(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int i = 0;
    int j = 0;
    if (j >= paramNode.getAttributes().getLength())
      label6: return;
    Node localNode = paramNode.getAttributes().item(j);
    if (("xmlns".equals(localNode.getPrefix())) || ((localNode.getPrefix() == null) && ("xmlns".equals(localNode.getNodeName()))));
    while (true)
    {
      ++j;
      break label6:
      int k = getRDFTermKind(localNode);
      switch (k)
      {
      case 1:
      case 4:
      case 5:
      default:
        throw new XMPException("Invalid nodeElement attribute", 202);
      case 2:
      case 3:
      case 6:
        if (i > 0)
          throw new XMPException("Mutally exclusive about, ID, nodeID attributes", 202);
        ++i;
        if ((!paramBoolean) || (k != 3))
          continue;
        if ((paramXMPNode.getName() != null) && (paramXMPNode.getName().length() > 0))
        {
          if (paramXMPNode.getName().equals(localNode.getNodeValue()))
            continue;
          throw new XMPException("Mismatched top level rdf:about values", 203);
        }
        paramXMPNode.setName(localNode.getNodeValue());
        break;
      case 0:
      }
      addChildNode(paramXMPMetaImpl, paramXMPNode, localNode, localNode.getNodeValue(), paramBoolean);
    }
  }

  private static void rdf_NodeElementList(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode)
    throws XMPException
  {
    for (int i = 0; i < paramNode.getChildNodes().getLength(); ++i)
    {
      Node localNode = paramNode.getChildNodes().item(i);
      if (isWhitespaceNode(localNode))
        continue;
      rdf_NodeElement(paramXMPMetaImpl, paramXMPNode, localNode, true);
    }
  }

  private static void rdf_ParseTypeCollectionPropertyElement()
    throws XMPException
  {
    throw new XMPException("ParseTypeCollection property element not allowed", 203);
  }

  private static void rdf_ParseTypeLiteralPropertyElement()
    throws XMPException
  {
    throw new XMPException("ParseTypeLiteral property element not allowed", 203);
  }

  private static void rdf_ParseTypeOtherPropertyElement()
    throws XMPException
  {
    throw new XMPException("ParseTypeOther property element not allowed", 203);
  }

  private static void rdf_ParseTypeResourcePropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    XMPNode localXMPNode = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, "", paramBoolean);
    localXMPNode.getOptions().setStruct(true);
    int i = 0;
    if (i < paramNode.getAttributes().getLength())
    {
      label25: Node localNode = paramNode.getAttributes().item(i);
      if (("xmlns".equals(localNode.getPrefix())) || ((localNode.getPrefix() == null) && ("xmlns".equals(localNode.getNodeName()))));
      String str1;
      String str2;
      do
        while (true)
        {
          ++i;
          break label25:
          str1 = localNode.getLocalName();
          str2 = localNode.getNamespaceURI();
          if (!"xml:lang".equals(localNode.getNodeName()))
            break;
          addQualifierNode(localXMPNode, "xml:lang", localNode.getNodeValue());
        }
      while (("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2)) && ((("ID".equals(str1)) || ("parseType".equals(str1)))));
      throw new XMPException("Invalid attribute for ParseTypeResource property element", 202);
    }
    rdf_PropertyElementList(paramXMPMetaImpl, localXMPNode, paramNode, false);
    if (!localXMPNode.getHasValueChild())
      return;
    fixupQualifiedNode(localXMPNode);
  }

  private static void rdf_PropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    if (!isPropertyElementName(getRDFTermKind(paramNode)))
      throw new XMPException("Invalid property element name", 202);
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    ArrayList localArrayList = null;
    for (int i = 0; i < localNamedNodeMap.getLength(); ++i)
    {
      Node localNode2 = localNamedNodeMap.item(i);
      if ((!"xmlns".equals(localNode2.getPrefix())) && (((localNode2.getPrefix() != null) || (!"xmlns".equals(localNode2.getNodeName())))))
        continue;
      if (localArrayList == null)
        localArrayList = new ArrayList();
      localArrayList.add(localNode2.getNodeName());
    }
    if (localArrayList != null)
    {
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
        localNamedNodeMap.removeNamedItem((String)localIterator.next());
    }
    if (localNamedNodeMap.getLength() > 3)
    {
      rdf_EmptyPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      return;
    }
    for (int j = 0; j < localNamedNodeMap.getLength(); ++j)
    {
      Node localNode1 = localNamedNodeMap.item(j);
      String str1 = localNode1.getLocalName();
      String str2 = localNode1.getNamespaceURI();
      String str3 = localNode1.getNodeValue();
      if (("xml:lang".equals(localNode1.getNodeName())) && (((!"ID".equals(str1)) || (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2)))))
        continue;
      if (("datatype".equals(str1)) && ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2)))
      {
        rdf_LiteralPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
        return;
      }
      if ((!"parseType".equals(str1)) || (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str2)))
      {
        rdf_EmptyPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
        return;
      }
      if ("Literal".equals(str3))
      {
        rdf_ParseTypeLiteralPropertyElement();
        return;
      }
      if ("Resource".equals(str3))
      {
        rdf_ParseTypeResourcePropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
        return;
      }
      if ("Collection".equals(str3))
      {
        rdf_ParseTypeCollectionPropertyElement();
        return;
      }
      rdf_ParseTypeOtherPropertyElement();
      return;
    }
    if (paramNode.hasChildNodes())
    {
      for (int k = 0; k < paramNode.getChildNodes().getLength(); ++k)
      {
        if (paramNode.getChildNodes().item(k).getNodeType() == 3)
          continue;
        rdf_ResourcePropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
        return;
      }
      rdf_LiteralPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
      return;
    }
    rdf_EmptyPropertyElement(paramXMPMetaImpl, paramXMPNode, paramNode, paramBoolean);
  }

  private static void rdf_PropertyElementList(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    int i = 0;
    if (i >= paramNode.getChildNodes().getLength())
      label3: return;
    Node localNode = paramNode.getChildNodes().item(i);
    if (isWhitespaceNode(localNode));
    while (true)
    {
      ++i;
      break label3:
      if (localNode.getNodeType() != 1)
        throw new XMPException("Expected property element node not found", 202);
      rdf_PropertyElement(paramXMPMetaImpl, paramXMPNode, localNode, paramBoolean);
    }
  }

  static void rdf_RDF(XMPMetaImpl paramXMPMetaImpl, Node paramNode)
    throws XMPException
  {
    if (paramNode.hasAttributes())
    {
      rdf_NodeElementList(paramXMPMetaImpl, paramXMPMetaImpl.getRoot(), paramNode);
      return;
    }
    throw new XMPException("Invalid attributes of rdf:RDF element", 202);
  }

  private static void rdf_ResourcePropertyElement(XMPMetaImpl paramXMPMetaImpl, XMPNode paramXMPNode, Node paramNode, boolean paramBoolean)
    throws XMPException
  {
    if ((paramBoolean) && ("iX:changes".equals(paramNode.getNodeName())));
    label35: int j;
    do
    {
      return;
      XMPNode localXMPNode = addChildNode(paramXMPMetaImpl, paramXMPNode, paramNode, "", paramBoolean);
      int i = 0;
      if (i < paramNode.getAttributes().getLength())
      {
        Node localNode2 = paramNode.getAttributes().item(i);
        if (("xmlns".equals(localNode2.getPrefix())) || ((localNode2.getPrefix() == null) && ("xmlns".equals(localNode2.getNodeName()))));
        String str3;
        String str4;
        do
          while (true)
          {
            ++i;
            break label35:
            str3 = localNode2.getLocalName();
            str4 = localNode2.getNamespaceURI();
            if (!"xml:lang".equals(localNode2.getNodeName()))
              break;
            addQualifierNode(localXMPNode, "xml:lang", localNode2.getNodeValue());
          }
        while (("ID".equals(str3)) && ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str4)));
        throw new XMPException("Invalid attribute for resource property element", 202);
      }
      j = 0;
      int k = 0;
      label205: if (k >= paramNode.getChildNodes().getLength())
        continue;
      Node localNode1 = paramNode.getChildNodes().item(k);
      boolean bool;
      String str1;
      if (!isWhitespaceNode(localNode1))
      {
        if ((localNode1.getNodeType() != 1) || (j != 0))
          break label519;
        bool = "http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(localNode1.getNamespaceURI());
        str1 = localNode1.getLocalName();
        if ((!bool) || (!"Bag".equals(str1)))
          break label340;
        localXMPNode.getOptions().setArray(true);
        label309: rdf_NodeElement(paramXMPMetaImpl, localXMPNode, localNode1, false);
        if (!localXMPNode.getHasValueChild())
          break label500;
        fixupQualifiedNode(localXMPNode);
      }
      while (true)
      {
        j = 1;
        ++k;
        break label205:
        if ((bool) && ("Seq".equals(str1)))
          label340: localXMPNode.getOptions().setArray(true).setArrayOrdered(true);
        if ((bool) && ("Alt".equals(str1)))
          localXMPNode.getOptions().setArray(true).setArrayOrdered(true).setArrayAlternate(true);
        localXMPNode.getOptions().setStruct(true);
        if ((!bool) && (!"Description".equals(str1)));
        String str2 = localNode1.getNamespaceURI();
        if (str2 == null)
          throw new XMPException("All XML elements must be in a namespace", 203);
        addQualifierNode(localXMPNode, "rdf:type", str2 + ':' + str1);
        break label309:
        label500: if (!localXMPNode.getOptions().isArrayAlternate())
          continue;
        XMPNodeUtils.detectAltText(localXMPNode);
      }
      if (j != 0)
        label519: throw new XMPException("Invalid child of resource property element", 202);
      throw new XMPException("Children of resource property element must be XML elements", 202);
    }
    while (j != 0);
    throw new XMPException("Missing child of resource property element", 202);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.ParseRDF
 * JD-Core Version:    0.5.4
 */