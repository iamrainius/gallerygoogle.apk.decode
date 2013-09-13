package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectDescriptorFactory
{
  protected static Map<Integer, Map<Integer, Class<? extends BaseDescriptor>>> descriptorRegistry;
  protected static Logger log = Logger.getLogger(ObjectDescriptorFactory.class.getName());

  static
  {
    descriptorRegistry = new HashMap();
    HashSet localHashSet = new HashSet();
    localHashSet.add(DecoderSpecificInfo.class);
    localHashSet.add(SLConfigDescriptor.class);
    localHashSet.add(BaseDescriptor.class);
    localHashSet.add(ExtensionDescriptor.class);
    localHashSet.add(ObjectDescriptorBase.class);
    localHashSet.add(ProfileLevelIndicationDescriptor.class);
    localHashSet.add(AudioSpecificConfig.class);
    localHashSet.add(ExtensionProfileLevelDescriptor.class);
    localHashSet.add(ESDescriptor.class);
    localHashSet.add(DecoderConfigDescriptor.class);
    Iterator localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      Descriptor localDescriptor = (Descriptor)localClass.getAnnotation(Descriptor.class);
      int[] arrayOfInt = localDescriptor.tags();
      int i = localDescriptor.objectTypeIndication();
      Object localObject = (Map)descriptorRegistry.get(Integer.valueOf(i));
      if (localObject == null)
        localObject = new HashMap();
      int j = arrayOfInt.length;
      for (int k = 0; k < j; ++k)
        ((Map)localObject).put(Integer.valueOf(arrayOfInt[k]), localClass);
      descriptorRegistry.put(Integer.valueOf(i), localObject);
    }
  }

  public static BaseDescriptor createFrom(int paramInt, ByteBuffer paramByteBuffer)
    throws IOException
  {
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    Map localMap = (Map)descriptorRegistry.get(Integer.valueOf(paramInt));
    if (localMap == null)
      localMap = (Map)descriptorRegistry.get(Integer.valueOf(-1));
    Class localClass = (Class)localMap.get(Integer.valueOf(i));
    Object localObject;
    if ((localClass == null) || (localClass.isInterface()) || (Modifier.isAbstract(localClass.getModifiers())))
    {
      log.warning("No ObjectDescriptor found for objectTypeIndication " + Integer.toHexString(paramInt) + " and tag " + Integer.toHexString(i) + " found: " + localClass);
      localObject = new UnknownDescriptor();
    }
    while (true)
    {
      ((BaseDescriptor)localObject).parse(i, paramByteBuffer);
      return localObject;
      try
      {
        localObject = (BaseDescriptor)localClass.newInstance();
      }
      catch (Exception localException)
      {
        log.log(Level.SEVERE, "Couldn't instantiate BaseDescriptor class " + localClass + " for objectTypeIndication " + paramInt + " and tag " + i, localException);
        throw new RuntimeException(localException);
      }
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory
 * JD-Core Version:    0.5.4
 */