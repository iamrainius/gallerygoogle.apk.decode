package com.adobe.xmp.impl;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.options.ParseOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMPMetaParser
{
  private static final Object XMP_RDF = new Object();
  private static DocumentBuilderFactory factory = createDocumentBuilderFactory();

  private static DocumentBuilderFactory createDocumentBuilderFactory()
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setNamespaceAware(true);
    localDocumentBuilderFactory.setIgnoringComments(true);
    try
    {
      localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      return localDocumentBuilderFactory;
    }
    catch (Exception localException)
    {
    }
    return localDocumentBuilderFactory;
  }

  private static Object[] findRootNode(Node paramNode, boolean paramBoolean, Object[] paramArrayOfObject)
  {
    NodeList localNodeList = paramNode.getChildNodes();
    int i = 0;
    if (i < localNodeList.getLength())
    {
      label10: Node localNode = localNodeList.item(i);
      if ((7 == localNode.getNodeType()) && (((ProcessingInstruction)localNode).getTarget() == "xpacket"))
        if (paramArrayOfObject != null)
          paramArrayOfObject[2] = ((ProcessingInstruction)localNode).getData();
      label203: Object[] arrayOfObject;
      while (true)
      {
        ++i;
        break label10:
        if ((3 == localNode.getNodeType()) || (7 == localNode.getNodeType()))
          continue;
        String str1 = localNode.getNamespaceURI();
        String str2 = localNode.getLocalName();
        if (((("xmpmeta".equals(str2)) || ("xapmeta".equals(str2)))) && ("adobe:ns:meta/".equals(str1)))
          paramArrayOfObject = findRootNode(localNode, false, paramArrayOfObject);
        do
        {
          return paramArrayOfObject;
          if ((paramBoolean) || (!"RDF".equals(str2)) || (!"http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(str1)))
            break label203;
        }
        while (paramArrayOfObject == null);
        paramArrayOfObject[0] = localNode;
        paramArrayOfObject[1] = XMP_RDF;
        return paramArrayOfObject;
        arrayOfObject = findRootNode(localNode, paramBoolean, paramArrayOfObject);
        if (arrayOfObject != null)
          return arrayOfObject;
      }
    }
    return null;
  }

  public static XMPMeta parse(Object paramObject, ParseOptions paramParseOptions)
    throws XMPException
  {
    ParameterAsserts.assertNotNull(paramObject);
    if (paramParseOptions != null);
    while (true)
    {
      Object[] arrayOfObject = findRootNode(parseXml(paramObject, paramParseOptions), paramParseOptions.getRequireXMPMeta(), new Object[3]);
      if ((arrayOfObject == null) || (arrayOfObject[1] != XMP_RDF))
        break;
      Object localObject = ParseRDF.parse((Node)arrayOfObject[0]);
      ((XMPMetaImpl)localObject).setPacketHeader((String)arrayOfObject[2]);
      if (!paramParseOptions.getOmitNormalization())
        localObject = XMPNormalizer.process((XMPMetaImpl)localObject, paramParseOptions);
      return localObject;
      paramParseOptions = new ParseOptions();
    }
    return (XMPMeta)new XMPMetaImpl();
  }

  private static Document parseInputSource(InputSource paramInputSource)
    throws XMPException
  {
    try
    {
      DocumentBuilder localDocumentBuilder = factory.newDocumentBuilder();
      localDocumentBuilder.setErrorHandler(null);
      Document localDocument = localDocumentBuilder.parse(paramInputSource);
      return localDocument;
    }
    catch (SAXException localSAXException)
    {
      throw new XMPException("XML parsing failure", 201, localSAXException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new XMPException("XML Parser not correctly configured", 0, localParserConfigurationException);
    }
    catch (IOException localIOException)
    {
      throw new XMPException("Error reading the XML-file", 204, localIOException);
    }
  }

  private static Document parseXml(Object paramObject, ParseOptions paramParseOptions)
    throws XMPException
  {
    if (paramObject instanceof InputStream)
      return parseXmlFromInputStream((InputStream)paramObject, paramParseOptions);
    if (paramObject instanceof byte[])
      return parseXmlFromBytebuffer(new ByteBuffer((byte[])(byte[])paramObject), paramParseOptions);
    return parseXmlFromString((String)paramObject, paramParseOptions);
  }

  private static Document parseXmlFromBytebuffer(ByteBuffer paramByteBuffer, ParseOptions paramParseOptions)
    throws XMPException
  {
    InputSource localInputSource = new InputSource(paramByteBuffer.getByteStream());
    try
    {
      Document localDocument2 = parseInputSource(localInputSource);
      return localDocument2;
    }
    catch (XMPException localXMPException)
    {
      if ((localXMPException.getErrorCode() == 201) || (localXMPException.getErrorCode() == 204))
      {
        if (paramParseOptions.getAcceptLatin1())
          paramByteBuffer = Latin1Converter.convert(paramByteBuffer);
        if (paramParseOptions.getFixControlChars())
          try
          {
            String str = paramByteBuffer.getEncoding();
            Document localDocument1 = parseInputSource(new InputSource(new FixASCIIControlsReader(new InputStreamReader(paramByteBuffer.getByteStream(), str))));
            return localDocument1;
          }
          catch (UnsupportedEncodingException localUnsupportedEncodingException)
          {
            throw new XMPException("Unsupported Encoding", 9, localXMPException);
          }
        return parseInputSource(new InputSource(paramByteBuffer.getByteStream()));
      }
    }
    throw localXMPException;
  }

  private static Document parseXmlFromInputStream(InputStream paramInputStream, ParseOptions paramParseOptions)
    throws XMPException
  {
    if ((!paramParseOptions.getAcceptLatin1()) && (!paramParseOptions.getFixControlChars()))
      return parseInputSource(new InputSource(paramInputStream));
    try
    {
      Document localDocument = parseXmlFromBytebuffer(new ByteBuffer(paramInputStream), paramParseOptions);
      return localDocument;
    }
    catch (IOException localIOException)
    {
      throw new XMPException("Error reading the XML-file", 204, localIOException);
    }
  }

  private static Document parseXmlFromString(String paramString, ParseOptions paramParseOptions)
    throws XMPException
  {
    InputSource localInputSource = new InputSource(new StringReader(paramString));
    try
    {
      Document localDocument = parseInputSource(localInputSource);
      return localDocument;
    }
    catch (XMPException localXMPException)
    {
      if ((localXMPException.getErrorCode() == 201) && (paramParseOptions.getFixControlChars()))
        return parseInputSource(new InputSource(new FixASCIIControlsReader(new StringReader(paramString))));
      throw localXMPException;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.adobe.xmp.impl.XMPMetaParser
 * JD-Core Version:    0.5.4
 */