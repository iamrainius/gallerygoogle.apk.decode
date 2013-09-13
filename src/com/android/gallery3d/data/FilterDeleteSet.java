package com.android.gallery3d.data;

import java.util.ArrayList;

public class FilterDeleteSet extends MediaSet
  implements ContentListener
{
  private final MediaSet mBaseSet;
  private ArrayList<Deletion> mCurrent = new ArrayList();
  private ArrayList<Request> mRequests = new ArrayList();

  public FilterDeleteSet(Path paramPath, MediaSet paramMediaSet)
  {
    super(paramPath, -1L);
    this.mBaseSet = paramMediaSet;
    this.mBaseSet.addContentListener(this);
  }

  private void sendRequest(int paramInt1, Path paramPath, int paramInt2)
  {
    Request localRequest = new Request(paramInt1, paramPath, paramInt2);
    synchronized (this.mRequests)
    {
      this.mRequests.add(localRequest);
      notifyContentChanged();
      return;
    }
  }

  public void addDeletion(Path paramPath, int paramInt)
  {
    sendRequest(1, paramPath, paramInt);
  }

  public void clearDeletion()
  {
    sendRequest(3, null, 0);
  }

  public ArrayList<MediaItem> getMediaItem(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList;
    if (paramInt2 <= 0)
    {
      localArrayList = new ArrayList();
      return localArrayList;
    }
    int i = -1 + (paramInt1 + paramInt2);
    int j = this.mCurrent.size();
    int k = 0;
    label33: if ((k >= j) || (((Deletion)this.mCurrent.get(k)).index - k > paramInt1));
    for (int l = k; ; ++l)
    {
      if ((l < j) && (((Deletion)this.mCurrent.get(l)).index - l <= i))
        continue;
      localArrayList = this.mBaseSet.getMediaItem(paramInt1 + k, paramInt2 + (l - k));
      for (int i1 = l - 1; ; --i1)
      {
        if (i1 >= k);
        localArrayList.remove(((Deletion)this.mCurrent.get(i1)).index - (paramInt1 + k));
      }
      ++k;
      break label33:
    }
  }

  public int getMediaItemCount()
  {
    return this.mBaseSet.getMediaItemCount() - this.mCurrent.size();
  }

  public String getName()
  {
    return this.mBaseSet.getName();
  }

  public int getNumberOfDeletions()
  {
    return this.mCurrent.size();
  }

  public boolean isCameraRoll()
  {
    return this.mBaseSet.isCameraRoll();
  }

  public void onContentDirty()
  {
    notifyContentChanged();
  }

  public long reload()
  {
    int i;
    label17: ArrayList localArrayList1;
    label49: int j;
    Request localRequest;
    label196: int i7;
    if (this.mBaseSet.reload() > this.mDataVersion)
    {
      i = 1;
      localArrayList1 = this.mRequests;
      monitorenter;
      if (i != 0)
        break label562;
      while (true)
      {
        int i10;
        ArrayList localArrayList4;
        Deletion localDeletion3;
        try
        {
          if (!this.mRequests.isEmpty())
            break label562;
          long l1 = this.mDataVersion;
          return l1;
          if (j >= this.mRequests.size())
            break label267;
          localRequest = (Request)this.mRequests.get(j);
          switch (localRequest.type)
          {
          case 1:
            int i9 = this.mCurrent.size();
            i10 = 0;
            if ((i10 < i9) && (((Deletion)this.mCurrent.get(i10)).path != localRequest.path))
              break label196;
            if (i10 == i9)
            {
              localArrayList4 = this.mCurrent;
              localDeletion3 = new Deletion(localRequest.path, localRequest.indexHint);
            }
          case 2:
          case 3:
          }
        }
        finally
        {
          monitorexit;
        }
        ++i10;
      }
      i7 = this.mCurrent.size();
    }
    for (int i8 = 0; ; ++i8)
    {
      if (i8 < i7)
      {
        if (((Deletion)this.mCurrent.get(i8)).path != localRequest.path)
          continue;
        this.mCurrent.remove(i8);
        break label567:
        this.mCurrent.clear();
        break label567:
        label267: this.mRequests.clear();
        monitorexit;
        if (!this.mCurrent.isEmpty())
        {
          int k = ((Deletion)this.mCurrent.get(0)).index;
          int l = k;
          for (int i1 = 1; i1 < this.mCurrent.size(); ++i1)
          {
            Deletion localDeletion2 = (Deletion)this.mCurrent.get(i1);
            k = Math.min(localDeletion2.index, k);
            l = Math.max(localDeletion2.index, l);
          }
          int i2 = this.mBaseSet.getMediaItemCount();
          int i3 = Math.max(k - 5, 0);
          int i4 = Math.min(l + 5, i2);
          ArrayList localArrayList2 = this.mBaseSet.getMediaItem(i3, i4 - i3);
          ArrayList localArrayList3 = new ArrayList();
          MediaItem localMediaItem;
          for (int i5 = 0; ; ++i5)
          {
            if (i5 >= localArrayList2.size())
              break label539;
            localMediaItem = (MediaItem)localArrayList2.get(i5);
            label450: if (localMediaItem != null)
              break;
          }
          Path localPath = localMediaItem.getPath();
          for (int i6 = 0; ; ++i6)
          {
            if (i6 < this.mCurrent.size());
            Deletion localDeletion1 = (Deletion)this.mCurrent.get(i6);
            if (localDeletion1.path != localPath)
              continue;
            localDeletion1.index = (i3 + i5);
            localArrayList3.add(localDeletion1);
            this.mCurrent.remove(i6);
            break label450:
          }
          label539: this.mCurrent = localArrayList3;
        }
        this.mDataVersion = nextVersionNumber();
        return this.mDataVersion;
        i = 0;
        break label17:
        label562: j = 0;
      }
      label567: ++j;
      break label49:
    }
  }

  public void removeDeletion(Path paramPath)
  {
    sendRequest(2, paramPath, 0);
  }

  private static class Deletion
  {
    int index;
    Path path;

    public Deletion(Path paramPath, int paramInt)
    {
      this.path = paramPath;
      this.index = paramInt;
    }
  }

  private static class Request
  {
    int indexHint;
    Path path;
    int type;

    public Request(int paramInt1, Path paramPath, int paramInt2)
    {
      this.type = paramInt1;
      this.path = paramPath;
      this.indexHint = paramInt2;
    }
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.FilterDeleteSet
 * JD-Core Version:    0.5.4
 */