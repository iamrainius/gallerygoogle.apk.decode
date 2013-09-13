package com.android.gallery3d.data;

import android.content.Context;
import android.content.res.Resources;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class TagClustering extends Clustering
{
  private ArrayList<ArrayList<Path>> mClusters;
  private String[] mNames;
  private String mUntaggedString;

  public TagClustering(Context paramContext)
  {
    this.mUntaggedString = paramContext.getResources().getString(2131362242);
  }

  public ArrayList<Path> getCluster(int paramInt)
  {
    return (ArrayList)this.mClusters.get(paramInt);
  }

  public String getClusterName(int paramInt)
  {
    return this.mNames[paramInt];
  }

  public int getNumberOfClusters()
  {
    return this.mClusters.size();
  }

  public void run(MediaSet paramMediaSet)
  {
    TreeMap localTreeMap = new TreeMap();
    ArrayList localArrayList = new ArrayList();
    paramMediaSet.enumerateTotalMediaItems(new MediaSet.ItemConsumer(localArrayList, localTreeMap)
    {
      public void consume(int paramInt, MediaItem paramMediaItem)
      {
        Path localPath = paramMediaItem.getPath();
        String[] arrayOfString = paramMediaItem.getTags();
        if ((arrayOfString == null) || (arrayOfString.length == 0))
        {
          this.val$untagged.add(localPath);
          return;
        }
        for (int i = 0; ; ++i)
        {
          if (i < arrayOfString.length);
          String str = arrayOfString[i];
          ArrayList localArrayList = (ArrayList)this.val$map.get(str);
          if (localArrayList == null)
          {
            localArrayList = new ArrayList();
            this.val$map.put(str, localArrayList);
          }
          localArrayList.add(localPath);
        }
      }
    });
    int i = localTreeMap.size();
    this.mClusters = new ArrayList();
    if (localArrayList.size() > 0);
    int k;
    for (int j = 1; ; j = 0)
    {
      this.mNames = new String[j + i];
      k = 0;
      Iterator localIterator = localTreeMap.entrySet().iterator();
      while (true)
      {
        if (!localIterator.hasNext())
          break label160;
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String[] arrayOfString2 = this.mNames;
        int l = k + 1;
        arrayOfString2[k] = ((String)localEntry.getKey());
        this.mClusters.add(localEntry.getValue());
        k = l;
      }
    }
    if (localArrayList.size() <= 0)
      label160: return;
    String[] arrayOfString1 = this.mNames;
    (k + 1);
    arrayOfString1[k] = this.mUntaggedString;
    this.mClusters.add(localArrayList);
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.data.TagClustering
 * JD-Core Version:    0.5.4
 */