package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.googlecode.mp4parser.AbstractFullBox;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ItemLocationBox extends AbstractFullBox
{
  public int baseOffsetSize = 8;
  public int indexSize = 0;
  public List<Item> items = new LinkedList();
  public int lengthSize = 8;
  public int offsetSize = 8;

  public ItemLocationBox()
  {
    super("iloc");
  }

  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.offsetSize = (i >>> 4);
    this.lengthSize = (i & 0xF);
    int j = IsoTypeReader.readUInt8(paramByteBuffer);
    this.baseOffsetSize = (j >>> 4);
    if (getVersion() == 1)
      this.indexSize = (j & 0xF);
    int k = IsoTypeReader.readUInt16(paramByteBuffer);
    for (int l = 0; l < k; ++l)
      this.items.add(new Item(paramByteBuffer));
  }

  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.offsetSize << 4 | this.lengthSize);
    if (getVersion() == 1)
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.baseOffsetSize << 4 | this.indexSize);
    while (true)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.items.size());
      Iterator localIterator = this.items.iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          return;
        ((Item)localIterator.next()).getContent(paramByteBuffer);
      }
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.baseOffsetSize << 4);
    }
  }

  protected long getContentSize()
  {
    long l = 8L;
    Iterator localIterator = this.items.iterator();
    while (localIterator.hasNext())
      l += ((Item)localIterator.next()).getSize();
    return l;
  }

  public class Extent
  {
    public long extentIndex;
    public long extentLength;
    public long extentOffset;

    public Extent(ByteBuffer arg2)
    {
      ByteBuffer localByteBuffer;
      if ((ItemLocationBox.this.getVersion() == 1) && (ItemLocationBox.this.indexSize > 0))
        this.extentIndex = IsoTypeReaderVariable.read(localByteBuffer, ItemLocationBox.this.indexSize);
      this.extentOffset = IsoTypeReaderVariable.read(localByteBuffer, ItemLocationBox.this.offsetSize);
      this.extentLength = IsoTypeReaderVariable.read(localByteBuffer, ItemLocationBox.this.lengthSize);
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      Extent localExtent;
      do
      {
        return true;
        if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
          return false;
        localExtent = (Extent)paramObject;
        if (this.extentIndex != localExtent.extentIndex)
          return false;
        if (this.extentLength != localExtent.extentLength)
          return false;
      }
      while (this.extentOffset == localExtent.extentOffset);
      return false;
    }

    public void getContent(ByteBuffer paramByteBuffer)
    {
      if ((ItemLocationBox.this.getVersion() == 1) && (ItemLocationBox.this.indexSize > 0))
        IsoTypeWriterVariable.write(this.extentIndex, paramByteBuffer, ItemLocationBox.this.indexSize);
      IsoTypeWriterVariable.write(this.extentOffset, paramByteBuffer, ItemLocationBox.this.offsetSize);
      IsoTypeWriterVariable.write(this.extentLength, paramByteBuffer, ItemLocationBox.this.lengthSize);
    }

    public int getSize()
    {
      if (ItemLocationBox.this.indexSize > 0);
      for (int i = ItemLocationBox.this.indexSize; ; i = 0)
        return i + ItemLocationBox.this.offsetSize + ItemLocationBox.this.lengthSize;
    }

    public int hashCode()
    {
      return 31 * (31 * (int)(this.extentOffset ^ this.extentOffset >>> 32) + (int)(this.extentLength ^ this.extentLength >>> 32)) + (int)(this.extentIndex ^ this.extentIndex >>> 32);
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Extent");
      localStringBuilder.append("{extentOffset=").append(this.extentOffset);
      localStringBuilder.append(", extentLength=").append(this.extentLength);
      localStringBuilder.append(", extentIndex=").append(this.extentIndex);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }

  public class Item
  {
    public long baseOffset;
    public int constructionMethod;
    public int dataReferenceIndex;
    public List<ItemLocationBox.Extent> extents = new LinkedList();
    public int itemId;

    public Item(ByteBuffer arg2)
    {
      ByteBuffer localByteBuffer;
      this.itemId = IsoTypeReader.readUInt16(localByteBuffer);
      if (ItemLocationBox.this.getVersion() == 1)
        this.constructionMethod = (0xF & IsoTypeReader.readUInt16(localByteBuffer));
      this.dataReferenceIndex = IsoTypeReader.readUInt16(localByteBuffer);
      if (ItemLocationBox.this.baseOffsetSize > 0);
      for (this.baseOffset = IsoTypeReaderVariable.read(localByteBuffer, ItemLocationBox.this.baseOffsetSize); ; this.baseOffset = 0L)
      {
        int i = IsoTypeReader.readUInt16(localByteBuffer);
        for (int j = 0; ; ++j)
        {
          if (j >= i)
            return;
          this.extents.add(new ItemLocationBox.Extent(ItemLocationBox.this, localByteBuffer));
        }
      }
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      Item localItem;
      do
      {
        return true;
        if ((paramObject == null) || (super.getClass() != paramObject.getClass()))
          return false;
        localItem = (Item)paramObject;
        if (this.baseOffset != localItem.baseOffset)
          return false;
        if (this.constructionMethod != localItem.constructionMethod)
          return false;
        if (this.dataReferenceIndex != localItem.dataReferenceIndex)
          return false;
        if (this.itemId != localItem.itemId)
          return false;
        if (this.extents == null)
          break;
      }
      while (this.extents.equals(localItem.extents));
      while (true)
      {
        return false;
        if (localItem.extents == null);
      }
    }

    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.itemId);
      if (ItemLocationBox.this.getVersion() == 1)
        IsoTypeWriter.writeUInt16(paramByteBuffer, this.constructionMethod);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.dataReferenceIndex);
      if (ItemLocationBox.this.baseOffsetSize > 0)
        IsoTypeWriterVariable.write(this.baseOffset, paramByteBuffer, ItemLocationBox.this.baseOffsetSize);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.extents.size());
      Iterator localIterator = this.extents.iterator();
      while (localIterator.hasNext())
        ((ItemLocationBox.Extent)localIterator.next()).getContent(paramByteBuffer);
    }

    public int getSize()
    {
      int i = 2;
      if (ItemLocationBox.this.getVersion() == 1);
      int j = 2 + ((i += 2) + 2 + ItemLocationBox.this.baseOffsetSize);
      Iterator localIterator = this.extents.iterator();
      while (localIterator.hasNext())
        j += ((ItemLocationBox.Extent)localIterator.next()).getSize();
      return j;
    }

    public int hashCode()
    {
      int i = 31 * (31 * (31 * (31 * this.itemId + this.constructionMethod) + this.dataReferenceIndex) + (int)(this.baseOffset ^ this.baseOffset >>> 32));
      if (this.extents != null);
      for (int j = this.extents.hashCode(); ; j = 0)
        return i + j;
    }

    public String toString()
    {
      return "Item{baseOffset=" + this.baseOffset + ", itemId=" + this.itemId + ", constructionMethod=" + this.constructionMethod + ", dataReferenceIndex=" + this.dataReferenceIndex + ", extents=" + this.extents + '}';
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.coremedia.iso.boxes.ItemLocationBox
 * JD-Core Version:    0.5.4
 */