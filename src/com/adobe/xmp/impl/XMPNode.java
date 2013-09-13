package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.PropertyOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class XMPNode
  implements Comparable
{
  private boolean alias;
  private List children = null;
  private boolean hasAliases;
  private boolean hasValueChild;
  private boolean implicit;
  private String name;
  private PropertyOptions options = null;
  private XMPNode parent;
  private List qualifier = null;
  private String value;

  static
  {
    if (!XMPNode.class.desiredAssertionStatus());
    for (int i = 1; ; i = 0)
    {
      $assertionsDisabled = i;
      return;
    }
  }

  public XMPNode(String paramString, PropertyOptions paramPropertyOptions)
  {
    this(paramString, null, paramPropertyOptions);
  }

  public XMPNode(String paramString1, String paramString2, PropertyOptions paramPropertyOptions)
  {
    this.name = paramString1;
    this.value = paramString2;
    this.options = paramPropertyOptions;
  }

  private void assertChildNotExisting(String paramString)
    throws XMPException
  {
    if (("[]".equals(paramString)) || (findChildByName(paramString) == null))
      return;
    throw new XMPException("Duplicate property or field node '" + paramString + "'", 203);
  }

  private void assertQualifierNotExisting(String paramString)
    throws XMPException
  {
    if (("[]".equals(paramString)) || (findQualifierByName(paramString) == null))
      return;
    throw new XMPException("Duplicate '" + paramString + "' qualifier", 203);
  }

  private XMPNode find(List paramList, String paramString)
  {
    if (paramList != null)
    {
      Iterator localIterator = paramList.iterator();
      XMPNode localXMPNode;
      while (localIterator.hasNext())
      {
        localXMPNode = (XMPNode)localIterator.next();
        if (localXMPNode.getName().equals(paramString))
          return localXMPNode;
      }
    }
    return null;
  }

  private List getChildren()
  {
    if (this.children == null)
      this.children = new ArrayList(0);
    return this.children;
  }

  private List getQualifier()
  {
    if (this.qualifier == null)
      this.qualifier = new ArrayList(0);
    return this.qualifier;
  }

  private boolean isLanguageNode()
  {
    return "xml:lang".equals(this.name);
  }

  private boolean isTypeNode()
  {
    return "rdf:type".equals(this.name);
  }

  public void addChild(int paramInt, XMPNode paramXMPNode)
    throws XMPException
  {
    assertChildNotExisting(paramXMPNode.getName());
    paramXMPNode.setParent(this);
    getChildren().add(paramInt - 1, paramXMPNode);
  }

  public void addChild(XMPNode paramXMPNode)
    throws XMPException
  {
    assertChildNotExisting(paramXMPNode.getName());
    paramXMPNode.setParent(this);
    getChildren().add(paramXMPNode);
  }

  public void addQualifier(XMPNode paramXMPNode)
    throws XMPException
  {
    assertQualifierNotExisting(paramXMPNode.getName());
    paramXMPNode.setParent(this);
    paramXMPNode.getOptions().setQualifier(true);
    getOptions().setHasQualifiers(true);
    if (paramXMPNode.isLanguageNode())
    {
      this.options.setHasLanguage(true);
      getQualifier().add(0, paramXMPNode);
      return;
    }
    if (paramXMPNode.isTypeNode())
    {
      this.options.setHasType(true);
      List localList = getQualifier();
      boolean bool = this.options.getHasLanguage();
      int i = 0;
      if (!bool);
      while (true)
      {
        localList.add(i, paramXMPNode);
        return;
        i = 1;
      }
    }
    getQualifier().add(paramXMPNode);
  }

  protected void cleanupChildren()
  {
    if (!this.children.isEmpty())
      return;
    this.children = null;
  }

  public void clear()
  {
    this.options = null;
    this.name = null;
    this.value = null;
    this.children = null;
    this.qualifier = null;
  }

  public Object clone()
  {
    PropertyOptions localPropertyOptions;
    try
    {
      localPropertyOptions = new PropertyOptions(getOptions().getOptions());
      XMPNode localXMPNode = new XMPNode(this.name, this.value, localPropertyOptions);
      cloneSubtree(localXMPNode);
      return localXMPNode;
    }
    catch (XMPException localXMPException)
    {
      localPropertyOptions = new PropertyOptions();
    }
  }

  public void cloneSubtree(XMPNode paramXMPNode)
  {
    try
    {
      Iterator localIterator1 = iterateChildren();
      if (!localIterator1.hasNext())
        break label51;
      label51: paramXMPNode.addChild((XMPNode)((XMPNode)localIterator1.next()).clone());
    }
    catch (XMPException localXMPException)
    {
      if ($assertionsDisabled)
        return;
      throw new AssertionError();
      Iterator localIterator2 = iterateQualifier();
      while (localIterator2.hasNext())
        paramXMPNode.addQualifier((XMPNode)((XMPNode)localIterator2.next()).clone());
    }
  }

  public int compareTo(Object paramObject)
  {
    if (getOptions().isSchemaNode())
      return this.value.compareTo(((XMPNode)paramObject).getValue());
    return this.name.compareTo(((XMPNode)paramObject).getName());
  }

  public XMPNode findChildByName(String paramString)
  {
    return find(getChildren(), paramString);
  }

  public XMPNode findQualifierByName(String paramString)
  {
    return find(this.qualifier, paramString);
  }

  public XMPNode getChild(int paramInt)
  {
    return (XMPNode)getChildren().get(paramInt - 1);
  }

  public int getChildrenLength()
  {
    if (this.children != null)
      return this.children.size();
    return 0;
  }

  public boolean getHasAliases()
  {
    return this.hasAliases;
  }

  public boolean getHasValueChild()
  {
    return this.hasValueChild;
  }

  public String getName()
  {
    return this.name;
  }

  public PropertyOptions getOptions()
  {
    if (this.options == null)
      this.options = new PropertyOptions();
    return this.options;
  }

  public XMPNode getParent()
  {
    return this.parent;
  }

  public XMPNode getQualifier(int paramInt)
  {
    return (XMPNode)getQualifier().get(paramInt - 1);
  }

  public int getQualifierLength()
  {
    if (this.qualifier != null)
      return this.qualifier.size();
    return 0;
  }

  public List getUnmodifiableChildren()
  {
    return Collections.unmodifiableList(new ArrayList(getChildren()));
  }

  public String getValue()
  {
    return this.value;
  }

  public boolean hasChildren()
  {
    return (this.children != null) && (this.children.size() > 0);
  }

  public boolean hasQualifier()
  {
    return (this.qualifier != null) && (this.qualifier.size() > 0);
  }

  public boolean isAlias()
  {
    return this.alias;
  }

  public boolean isImplicit()
  {
    return this.implicit;
  }

  public Iterator iterateChildren()
  {
    if (this.children != null)
      return getChildren().iterator();
    return Collections.EMPTY_LIST.listIterator();
  }

  public Iterator iterateQualifier()
  {
    if (this.qualifier != null)
      return new Iterator(getQualifier().iterator())
      {
        public boolean hasNext()
        {
          return this.val$it.hasNext();
        }

        public Object next()
        {
          return this.val$it.next();
        }

        public void remove()
        {
          throw new UnsupportedOperationException("remove() is not allowed due to the internal contraints");
        }
      };
    return Collections.EMPTY_LIST.iterator();
  }

  public void removeChild(int paramInt)
  {
    getChildren().remove(paramInt - 1);
    cleanupChildren();
  }

  public void removeChild(XMPNode paramXMPNode)
  {
    getChildren().remove(paramXMPNode);
    cleanupChildren();
  }

  public void removeChildren()
  {
    this.children = null;
  }

  public void removeQualifier(XMPNode paramXMPNode)
  {
    PropertyOptions localPropertyOptions = getOptions();
    if (paramXMPNode.isLanguageNode())
      localPropertyOptions.setHasLanguage(false);
    while (true)
    {
      getQualifier().remove(paramXMPNode);
      if (this.qualifier.isEmpty())
      {
        localPropertyOptions.setHasQualifiers(false);
        this.qualifier = null;
      }
      return;
      if (!paramXMPNode.isTypeNode())
        continue;
      localPropertyOptions.setHasType(false);
    }
  }

  public void removeQualifiers()
  {
    PropertyOptions localPropertyOptions = getOptions();
    localPropertyOptions.setHasQualifiers(false);
    localPropertyOptions.setHasLanguage(false);
    localPropertyOptions.setHasType(false);
    this.qualifier = null;
  }

  public void replaceChild(int paramInt, XMPNode paramXMPNode)
  {
    paramXMPNode.setParent(this);
    getChildren().set(paramInt - 1, paramXMPNode);
  }

  public void setAlias(boolean paramBoolean)
  {
    this.alias = paramBoolean;
  }

  public void setHasAliases(boolean paramBoolean)
  {
    this.hasAliases = paramBoolean;
  }

  public void setHasValueChild(boolean paramBoolean)
  {
    this.hasValueChild = paramBoolean;
  }

  public void setImplicit(boolean paramBoolean)
  {
    this.implicit = paramBoolean;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setOptions(PropertyOptions paramPropertyOptions)
  {
    this.options = paramPropertyOptions;
  }

  protected void setParent(XMPNode paramXMPNode)
  {
    this.parent = paramXMPNode;
  }

  public void setValue(String paramString)
  {
    this.value = paramString;
  }

  public void sort()
  {
    if (hasQualifier())
    {
      XMPNode[] arrayOfXMPNode = (XMPNode[])(XMPNode[])getQualifier().toArray(new XMPNode[getQualifierLength()]);
      for (int i = 0; (arrayOfXMPNode.length > i) && ((("xml:lang".equals(arrayOfXMPNode[i].getName())) || ("rdf:type".equals(arrayOfXMPNode[i].getName())))); ++i)
        arrayOfXMPNode[i].sort();
      Arrays.sort(arrayOfXMPNode, i, arrayOfXMPNode.length);
      ListIterator localListIterator = this.qualifier.listIterator();
      for (int j = 0; j < arrayOfXMPNode.length; ++j)
      {
        localListIterator.next();
        localListIterator.set(arrayOfXMPNode[j]);
        arrayOfXMPNode[j].sort();
      }
    }
    if (!hasChildren())
      return;
    if (!getOptions().isArray())
      Collections.sort(this.children);
    Iterator localIterator = iterateChildren();
    while (localIterator.hasNext())
      ((XMPNode)localIterator.next()).sort();
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPNode
 * JD-Core Version:    0.5.4
 */