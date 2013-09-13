package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.XMPVersionInfo;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.options.SerializeOptions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class XMPSerializerRDF
{
  static final Set RDF_ATTR_QUALIFIER = new HashSet(Arrays.asList(new String[] { "xml:lang", "rdf:resource", "rdf:ID", "rdf:bagID", "rdf:nodeID" }));
  private SerializeOptions options;
  private CountOutputStream outputStream;
  private int padding;
  private int unicodeSize = 1;
  private OutputStreamWriter writer;
  private XMPMetaImpl xmp;

  private void addPadding(int paramInt)
    throws XMPException, IOException
  {
    if (this.options.getExactPacketLength())
    {
      int j = this.outputStream.getBytesWritten() + paramInt * this.unicodeSize;
      if (j > this.padding)
        throw new XMPException("Can't fit into specified packet size", 107);
      this.padding -= j;
    }
    this.padding /= this.unicodeSize;
    int i = this.options.getNewline().length();
    if (this.padding >= i)
    {
      this.padding -= i;
      while (this.padding >= i + 100)
      {
        writeChars(100, ' ');
        writeNewline();
        this.padding -= i + 100;
      }
      writeChars(this.padding, ' ');
      writeNewline();
      return;
    }
    writeChars(this.padding, ' ');
  }

  private void appendNodeValue(String paramString, boolean paramBoolean)
    throws IOException
  {
    write(Utils.escapeXML(paramString, paramBoolean, true));
  }

  private boolean canBeRDFAttrProp(XMPNode paramXMPNode)
  {
    return (!paramXMPNode.hasQualifier()) && (!paramXMPNode.getOptions().isURI()) && (!paramXMPNode.getOptions().isCompositeProperty()) && (!"[]".equals(paramXMPNode.getName()));
  }

  private void declareNamespace(String paramString1, String paramString2, Set paramSet, int paramInt)
    throws IOException
  {
    if (paramString2 == null)
    {
      QName localQName = new QName(paramString1);
      if (!localQName.hasPrefix())
        return;
      paramString1 = localQName.getPrefix();
      paramString2 = XMPMetaFactory.getSchemaRegistry().getNamespaceURI(paramString1 + ":");
      declareNamespace(paramString1, paramString2, paramSet, paramInt);
    }
    if (paramSet.contains(paramString1))
      return;
    writeNewline();
    writeIndent(paramInt);
    write("xmlns:");
    write(paramString1);
    write("=\"");
    write(paramString2);
    write(34);
    paramSet.add(paramString1);
  }

  private void declareUsedNamespaces(XMPNode paramXMPNode, Set paramSet, int paramInt)
    throws IOException
  {
    if (paramXMPNode.getOptions().isSchemaNode())
      declareNamespace(paramXMPNode.getValue().substring(0, -1 + paramXMPNode.getValue().length()), paramXMPNode.getName(), paramSet, paramInt);
    do
    {
      Iterator localIterator2 = paramXMPNode.iterateChildren();
      while (true)
      {
        if (!localIterator2.hasNext())
          break label121;
        declareUsedNamespaces((XMPNode)localIterator2.next(), paramSet, paramInt);
      }
    }
    while (!paramXMPNode.getOptions().isStruct());
    Iterator localIterator1 = paramXMPNode.iterateChildren();
    while (true)
    {
      if (localIterator1.hasNext());
      declareNamespace(((XMPNode)localIterator1.next()).getName(), null, paramSet, paramInt);
    }
    label121: Iterator localIterator3 = paramXMPNode.iterateQualifier();
    while (localIterator3.hasNext())
    {
      XMPNode localXMPNode = (XMPNode)localIterator3.next();
      declareNamespace(localXMPNode.getName(), null, paramSet, paramInt);
      declareUsedNamespaces(localXMPNode, paramSet, paramInt);
    }
  }

  private void emitRDFArrayTag(XMPNode paramXMPNode, boolean paramBoolean, int paramInt)
    throws IOException
  {
    String str;
    if ((paramBoolean) || (paramXMPNode.hasChildren()))
    {
      writeIndent(paramInt);
      if (!paramBoolean)
        break label68;
      str = "<rdf:";
      label24: write(str);
      if (!paramXMPNode.getOptions().isArrayAlternate())
        break label75;
      write("Alt");
      label46: if ((!paramBoolean) || (paramXMPNode.hasChildren()))
        break label103;
      write("/>");
    }
    while (true)
    {
      writeNewline();
      return;
      label68: str = "</rdf:";
      break label24:
      if (paramXMPNode.getOptions().isArrayOrdered())
        label75: write("Seq");
      write("Bag");
      break label46:
      label103: write(">");
    }
  }

  private String serializeAsRDF()
    throws IOException, XMPException
  {
    if (!this.options.getOmitPacketWrapper())
    {
      writeIndent(0);
      write("<?xpacket begin=\"﻿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>");
      writeNewline();
    }
    writeIndent(0);
    write("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"");
    if (!this.options.getOmitVersionAttribute())
      write(XMPMetaFactory.getVersionInfo().getMessage());
    write("\">");
    writeNewline();
    writeIndent(1);
    write("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");
    writeNewline();
    if (this.options.getUseCompactFormat())
      serializeCompactRDFSchemas();
    String str1;
    while (true)
    {
      writeIndent(1);
      write("</rdf:RDF>");
      writeNewline();
      writeIndent(0);
      write("</x:xmpmeta>");
      writeNewline();
      str1 = "";
      if (this.options.getOmitPacketWrapper())
        break label277;
      for (int i = this.options.getBaseIndent(); ; --i)
      {
        if (i <= 0)
          break label195;
        str1 = str1 + this.options.getIndent();
      }
      serializePrettyRDFSchemas();
    }
    label195: String str2 = str1 + "<?xpacket end=\"";
    StringBuilder localStringBuilder = new StringBuilder().append(str2);
    if (this.options.getReadOnlyPacket());
    for (char c = 'r'; ; c = 'w')
    {
      String str3 = c;
      str1 = str3 + "\"?>";
      label277: return str1;
    }
  }

  private void serializeCompactRDFArrayProp(XMPNode paramXMPNode, int paramInt)
    throws IOException, XMPException
  {
    write(62);
    writeNewline();
    emitRDFArrayTag(paramXMPNode, true, paramInt + 1);
    if (paramXMPNode.getOptions().isArrayAltText())
      XMPNodeUtils.normalizeLangArray(paramXMPNode);
    serializeCompactRDFElementProps(paramXMPNode, paramInt + 2);
    emitRDFArrayTag(paramXMPNode, false, paramInt + 1);
  }

  private boolean serializeCompactRDFAttrProps(XMPNode paramXMPNode, int paramInt)
    throws IOException
  {
    int i = 1;
    Iterator localIterator = paramXMPNode.iterateChildren();
    while (localIterator.hasNext())
    {
      XMPNode localXMPNode = (XMPNode)localIterator.next();
      if (canBeRDFAttrProp(localXMPNode))
      {
        writeNewline();
        writeIndent(paramInt);
        write(localXMPNode.getName());
        write("=\"");
        appendNodeValue(localXMPNode.getValue(), true);
        write(34);
      }
      i = 0;
    }
    return i;
  }

  private void serializeCompactRDFElementProps(XMPNode paramXMPNode, int paramInt)
    throws IOException, XMPException
  {
    Iterator localIterator1 = paramXMPNode.iterateChildren();
    label5: XMPNode localXMPNode1;
    do
    {
      if (!localIterator1.hasNext())
        return;
      localXMPNode1 = (XMPNode)localIterator1.next();
    }
    while (canBeRDFAttrProp(localXMPNode1));
    boolean bool1 = true;
    boolean bool2 = true;
    String str = localXMPNode1.getName();
    if ("[]".equals(str))
      str = "rdf:li";
    writeIndent(paramInt);
    write(60);
    write(str);
    int i = 0;
    boolean bool3 = false;
    Iterator localIterator2 = localXMPNode1.iterateQualifier();
    while (localIterator2.hasNext())
    {
      XMPNode localXMPNode2 = (XMPNode)localIterator2.next();
      if (!RDF_ATTR_QUALIFIER.contains(localXMPNode2.getName()))
        i = 1;
      bool3 = "rdf:resource".equals(localXMPNode2.getName());
      write(32);
      write(localXMPNode2.getName());
      write("=\"");
      appendNodeValue(localXMPNode2.getValue(), true);
      write(34);
    }
    if (i != 0)
      serializeCompactRDFGeneralQualifier(paramInt, localXMPNode1);
    while (true)
    {
      if (bool1);
      if (bool2)
        writeIndent(paramInt);
      write("</");
      write(str);
      write(62);
      writeNewline();
      break label5:
      if (!localXMPNode1.getOptions().isCompositeProperty())
      {
        Object[] arrayOfObject = serializeCompactRDFSimpleProp(localXMPNode1);
        bool1 = ((Boolean)arrayOfObject[0]).booleanValue();
        bool2 = ((Boolean)arrayOfObject[1]).booleanValue();
      }
      if (localXMPNode1.getOptions().isArray())
        serializeCompactRDFArrayProp(localXMPNode1, paramInt);
      bool1 = serializeCompactRDFStructProp(localXMPNode1, paramInt, bool3);
    }
  }

  private void serializeCompactRDFGeneralQualifier(int paramInt, XMPNode paramXMPNode)
    throws IOException, XMPException
  {
    write(" rdf:parseType=\"Resource\">");
    writeNewline();
    serializePrettyRDFProperty(paramXMPNode, true, paramInt + 1);
    Iterator localIterator = paramXMPNode.iterateQualifier();
    while (localIterator.hasNext())
      serializePrettyRDFProperty((XMPNode)localIterator.next(), false, paramInt + 1);
  }

  private void serializeCompactRDFSchemas()
    throws IOException, XMPException
  {
    writeIndent(2);
    write("<rdf:Description rdf:about=");
    writeTreeName();
    HashSet localHashSet = new HashSet();
    localHashSet.add("xml");
    localHashSet.add("rdf");
    Iterator localIterator1 = this.xmp.getRoot().iterateChildren();
    while (localIterator1.hasNext())
      declareUsedNamespaces((XMPNode)localIterator1.next(), localHashSet, 4);
    boolean bool = true;
    Iterator localIterator2 = this.xmp.getRoot().iterateChildren();
    while (localIterator2.hasNext())
      bool &= serializeCompactRDFAttrProps((XMPNode)localIterator2.next(), 3);
    if (!bool)
    {
      write(62);
      writeNewline();
      Iterator localIterator3 = this.xmp.getRoot().iterateChildren();
      while (true)
      {
        if (!localIterator3.hasNext())
          break label199;
        serializeCompactRDFElementProps((XMPNode)localIterator3.next(), 3);
      }
    }
    write("/>");
    writeNewline();
    return;
    label199: writeIndent(2);
    write("</rdf:Description>");
    writeNewline();
  }

  private Object[] serializeCompactRDFSimpleProp(XMPNode paramXMPNode)
    throws IOException
  {
    Boolean localBoolean1 = Boolean.TRUE;
    Boolean localBoolean2 = Boolean.TRUE;
    if (paramXMPNode.getOptions().isURI())
    {
      write(" rdf:resource=\"");
      appendNodeValue(paramXMPNode.getValue(), true);
      write("\"/>");
      writeNewline();
      localBoolean1 = Boolean.FALSE;
    }
    while (true)
    {
      return new Object[] { localBoolean1, localBoolean2 };
      if ((paramXMPNode.getValue() == null) || (paramXMPNode.getValue().length() == 0))
      {
        write("/>");
        writeNewline();
        localBoolean1 = Boolean.FALSE;
      }
      write(62);
      appendNodeValue(paramXMPNode.getValue(), false);
      localBoolean2 = Boolean.FALSE;
    }
  }

  private boolean serializeCompactRDFStructProp(XMPNode paramXMPNode, int paramInt, boolean paramBoolean)
    throws XMPException, IOException
  {
    int i = 0;
    int j = 0;
    Iterator localIterator = paramXMPNode.iterateChildren();
    if (localIterator.hasNext())
    {
      if (!canBeRDFAttrProp((XMPNode)localIterator.next()))
        break label75;
      i = 1;
    }
    while (true)
    {
      if ((i != 0) && (j != 0));
      if ((!paramBoolean) || (j == 0))
        break;
      throw new XMPException("Can't mix rdf:resource qualifier and element fields", 202);
      label75: j = 1;
    }
    if (!paramXMPNode.hasChildren())
    {
      write(" rdf:parseType=\"Resource\"/>");
      writeNewline();
      return false;
    }
    if (j == 0)
    {
      serializeCompactRDFAttrProps(paramXMPNode, paramInt + 1);
      write("/>");
      writeNewline();
      return false;
    }
    if (i == 0)
    {
      write(" rdf:parseType=\"Resource\">");
      writeNewline();
      serializeCompactRDFElementProps(paramXMPNode, paramInt + 1);
      return true;
    }
    write(62);
    writeNewline();
    writeIndent(paramInt + 1);
    write("<rdf:Description");
    serializeCompactRDFAttrProps(paramXMPNode, paramInt + 2);
    write(">");
    writeNewline();
    serializeCompactRDFElementProps(paramXMPNode, paramInt + 1);
    writeIndent(paramInt + 1);
    write("</rdf:Description>");
    writeNewline();
    return true;
  }

  private void serializePrettyRDFProperty(XMPNode paramXMPNode, boolean paramBoolean, int paramInt)
    throws IOException, XMPException
  {
    int i = 1;
    int j = 1;
    String str = paramXMPNode.getName();
    int k;
    boolean bool;
    Iterator localIterator1;
    if (paramBoolean)
    {
      str = "rdf:value";
      writeIndent(paramInt);
      write(60);
      write(str);
      k = 0;
      bool = false;
      localIterator1 = paramXMPNode.iterateQualifier();
    }
    while (localIterator1.hasNext())
    {
      XMPNode localXMPNode3 = (XMPNode)localIterator1.next();
      if (!RDF_ATTR_QUALIFIER.contains(localXMPNode3.getName()))
      {
        k = 1;
        continue;
        if ("[]".equals(str));
        str = "rdf:li";
      }
      bool = "rdf:resource".equals(localXMPNode3.getName());
      if (paramBoolean)
        continue;
      write(32);
      write(localXMPNode3.getName());
      write("=\"");
      appendNodeValue(localXMPNode3.getValue(), true);
      write(34);
    }
    if ((k != 0) && (!paramBoolean))
    {
      if (bool)
        throw new XMPException("Can't mix rdf:resource and general qualifiers", 202);
      write(" rdf:parseType=\"Resource\">");
      writeNewline();
      serializePrettyRDFProperty(paramXMPNode, true, paramInt + 1);
      Iterator localIterator5 = paramXMPNode.iterateQualifier();
      while (true)
      {
        if (!localIterator5.hasNext())
          break label323;
        XMPNode localXMPNode2 = (XMPNode)localIterator5.next();
        if (RDF_ATTR_QUALIFIER.contains(localXMPNode2.getName()))
          continue;
        serializePrettyRDFProperty(localXMPNode2, false, paramInt + 1);
      }
    }
    if (!paramXMPNode.getOptions().isCompositeProperty())
      if (paramXMPNode.getOptions().isURI())
      {
        write(" rdf:resource=\"");
        appendNodeValue(paramXMPNode.getValue(), true);
        write("\"/>");
        writeNewline();
      }
    for (i = 0; ; i = 0)
    {
      while (true)
      {
        if (i != 0)
        {
          if (j != 0)
            label323: writeIndent(paramInt);
          write("</");
          write(str);
          write(62);
          writeNewline();
        }
        return;
        if ((paramXMPNode.getValue() == null) || ("".equals(paramXMPNode.getValue())))
        {
          write("/>");
          writeNewline();
          i = 0;
        }
        write(62);
        appendNodeValue(paramXMPNode.getValue(), false);
        j = 0;
        continue;
        if (paramXMPNode.getOptions().isArray())
        {
          write(62);
          writeNewline();
          emitRDFArrayTag(paramXMPNode, true, paramInt + 1);
          if (paramXMPNode.getOptions().isArrayAltText())
            XMPNodeUtils.normalizeLangArray(paramXMPNode);
          Iterator localIterator4 = paramXMPNode.iterateChildren();
          while (localIterator4.hasNext())
            serializePrettyRDFProperty((XMPNode)localIterator4.next(), false, paramInt + 2);
          emitRDFArrayTag(paramXMPNode, false, paramInt + 1);
        }
        if (bool)
          break label588;
        if (paramXMPNode.hasChildren())
          break;
        write(" rdf:parseType=\"Resource\"/>");
        writeNewline();
        i = 0;
      }
      write(" rdf:parseType=\"Resource\">");
      writeNewline();
      Iterator localIterator3 = paramXMPNode.iterateChildren();
      while (true)
      {
        if (localIterator3.hasNext());
        serializePrettyRDFProperty((XMPNode)localIterator3.next(), false, paramInt + 1);
      }
      label588: Iterator localIterator2 = paramXMPNode.iterateChildren();
      while (localIterator2.hasNext())
      {
        XMPNode localXMPNode1 = (XMPNode)localIterator2.next();
        if (!canBeRDFAttrProp(localXMPNode1))
          throw new XMPException("Can't mix rdf:resource and complex fields", 202);
        writeNewline();
        writeIndent(paramInt + 1);
        write(32);
        write(localXMPNode1.getName());
        write("=\"");
        appendNodeValue(localXMPNode1.getValue(), true);
        write(34);
      }
      write("/>");
      writeNewline();
    }
  }

  private void serializePrettyRDFSchema(XMPNode paramXMPNode)
    throws IOException, XMPException
  {
    writeIndent(2);
    write("<rdf:Description rdf:about=");
    writeTreeName();
    HashSet localHashSet = new HashSet();
    localHashSet.add("xml");
    localHashSet.add("rdf");
    declareUsedNamespaces(paramXMPNode, localHashSet, 4);
    write(62);
    writeNewline();
    Iterator localIterator = paramXMPNode.iterateChildren();
    while (localIterator.hasNext())
      serializePrettyRDFProperty((XMPNode)localIterator.next(), false, 3);
    writeIndent(2);
    write("</rdf:Description>");
    writeNewline();
  }

  private void serializePrettyRDFSchemas()
    throws IOException, XMPException
  {
    if (this.xmp.getRoot().getChildrenLength() > 0)
    {
      Iterator localIterator = this.xmp.getRoot().iterateChildren();
      while (true)
      {
        if (!localIterator.hasNext())
          return;
        serializePrettyRDFSchema((XMPNode)localIterator.next());
      }
    }
    writeIndent(2);
    write("<rdf:Description rdf:about=");
    writeTreeName();
    write("/>");
    writeNewline();
  }

  private void write(int paramInt)
    throws IOException
  {
    this.writer.write(paramInt);
  }

  private void write(String paramString)
    throws IOException
  {
    this.writer.write(paramString);
  }

  private void writeChars(int paramInt, char paramChar)
    throws IOException
  {
    while (paramInt > 0)
    {
      this.writer.write(paramChar);
      --paramInt;
    }
  }

  private void writeIndent(int paramInt)
    throws IOException
  {
    for (int i = paramInt + this.options.getBaseIndent(); i > 0; --i)
      this.writer.write(this.options.getIndent());
  }

  private void writeNewline()
    throws IOException
  {
    this.writer.write(this.options.getNewline());
  }

  private void writeTreeName()
    throws IOException
  {
    write(34);
    String str = this.xmp.getRoot().getName();
    if (str != null)
      appendNodeValue(str, true);
    write(34);
  }

  protected void checkOptionsConsistence()
    throws XMPException
  {
    if ((this.options.getEncodeUTF16BE() | this.options.getEncodeUTF16LE()))
      this.unicodeSize = 2;
    if (this.options.getExactPacketLength())
    {
      if ((this.options.getOmitPacketWrapper() | this.options.getIncludeThumbnailPad()))
        throw new XMPException("Inconsistent options for exact size serialize", 103);
      if ((this.options.getPadding() & -1 + this.unicodeSize) != 0)
        throw new XMPException("Exact size must be a multiple of the Unicode element", 103);
    }
    else
    {
      if (!this.options.getReadOnlyPacket())
        break label141;
      if ((this.options.getOmitPacketWrapper() | this.options.getIncludeThumbnailPad()))
        throw new XMPException("Inconsistent options for read-only packet", 103);
      this.padding = 0;
    }
    do
    {
      return;
      if (this.options.getOmitPacketWrapper())
      {
        if (this.options.getIncludeThumbnailPad())
          label141: throw new XMPException("Inconsistent options for non-packet serialize", 103);
        this.padding = 0;
        return;
      }
      if (this.padding != 0)
        continue;
      this.padding = (2048 * this.unicodeSize);
    }
    while ((!this.options.getIncludeThumbnailPad()) || (this.xmp.doesPropertyExist("http://ns.adobe.com/xap/1.0/", "Thumbnails")));
    this.padding += 10000 * this.unicodeSize;
  }

  public void serialize(XMPMeta paramXMPMeta, OutputStream paramOutputStream, SerializeOptions paramSerializeOptions)
    throws XMPException
  {
    try
    {
      this.outputStream = new CountOutputStream(paramOutputStream);
      this.writer = new OutputStreamWriter(this.outputStream, paramSerializeOptions.getEncoding());
      this.xmp = ((XMPMetaImpl)paramXMPMeta);
      this.options = paramSerializeOptions;
      this.padding = paramSerializeOptions.getPadding();
      this.writer = new OutputStreamWriter(this.outputStream, paramSerializeOptions.getEncoding());
      checkOptionsConsistence();
      String str = serializeAsRDF();
      this.writer.flush();
      addPadding(str.length());
      write(str);
      this.writer.flush();
      this.outputStream.close();
      return;
    }
    catch (IOException localIOException)
    {
      throw new XMPException("Error writing to the OutputStream", 0);
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPSerializerRDF
 * JD-Core Version:    0.5.4
 */