package com.android.gallery3d.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.android.gallery3d.common.BlobCache;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReverseGeocoder
{
  private static Address sCurrentAddress;
  private ConnectivityManager mConnectivityManager;
  private Context mContext;
  private BlobCache mGeoCache;
  private Geocoder mGeocoder;

  public ReverseGeocoder(Context paramContext)
  {
    this.mContext = paramContext;
    this.mGeocoder = new Geocoder(this.mContext);
    this.mGeoCache = CacheManager.getCache(paramContext, "rev_geocoding", 1000, 512000, 0);
    this.mConnectivityManager = ((ConnectivityManager)paramContext.getSystemService("connectivity"));
  }

  private String checkNull(String paramString)
  {
    if (paramString == null)
      paramString = "";
    do
      return paramString;
    while (!paramString.equals("null"));
    return "";
  }

  private String getLocalityAdminForAddress(Address paramAddress, boolean paramBoolean)
  {
    String str1;
    if (paramAddress == null)
      str1 = "";
    String str2;
    while (true)
    {
      return str1;
      str1 = paramAddress.getLocality();
      if ((str1 == null) || ("null".equals(str1)))
        break;
      if (paramBoolean);
      str2 = paramAddress.getAdminArea();
      if ((str2 != null) && (str2.length() > 0))
        return str1 + ", " + str2;
    }
    return null;
  }

  public static final String readUTF(DataInputStream paramDataInputStream)
    throws IOException
  {
    String str = paramDataInputStream.readUTF();
    if (str.length() == 0)
      str = null;
    return str;
  }

  private String valueIfEqual(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null) && (paramString1.equalsIgnoreCase(paramString2)))
      return paramString1;
    return null;
  }

  public static final void writeUTF(DataOutputStream paramDataOutputStream, String paramString)
    throws IOException
  {
    if (paramString == null)
    {
      paramDataOutputStream.writeUTF("");
      return;
    }
    paramDataOutputStream.writeUTF(paramString);
  }

  public String computeAddress(SetLatLong paramSetLatLong)
  {
    double d1 = paramSetLatLong.mMinLatLatitude;
    double d2 = paramSetLatLong.mMinLatLongitude;
    double d3 = paramSetLatLong.mMaxLatLatitude;
    double d4 = paramSetLatLong.mMaxLatLongitude;
    if (Math.abs(paramSetLatLong.mMaxLatLatitude - paramSetLatLong.mMinLatLatitude) < Math.abs(paramSetLatLong.mMaxLonLongitude - paramSetLatLong.mMinLonLongitude))
    {
      d1 = paramSetLatLong.mMinLonLatitude;
      d2 = paramSetLatLong.mMinLonLongitude;
      d3 = paramSetLatLong.mMaxLonLatitude;
      d4 = paramSetLatLong.mMaxLonLongitude;
    }
    Object localObject1 = lookupAddress(d1, d2, true);
    Object localObject2 = lookupAddress(d3, d4, true);
    if (localObject1 == null)
      localObject1 = localObject2;
    if (localObject2 == null)
      localObject2 = localObject1;
    String str1;
    if ((localObject1 == null) || (localObject2 == null))
      str1 = null;
    label156: label200: label253: Object localObject7;
    Object localObject8;
    do
    {
      String str4;
      label532: label538: label544: label948: Object localObject12;
      do
      {
        Object localObject5;
        Object localObject6;
        do
        {
          do
          {
            String str3;
            Object localObject3;
            Object localObject4;
            Object localObject13;
            Object localObject14;
            do
            {
              do
              {
                return str1;
                LocationManager localLocationManager = (LocationManager)this.mContext.getSystemService("location");
                Location localLocation = null;
                List localList = localLocationManager.getAllProviders();
                int i = 0;
                int j = localList.size();
                if (i < j)
                {
                  String str5 = (String)localList.get(i);
                  if (str5 == null)
                    break label532;
                  localLocation = localLocationManager.getLastKnownLocation(str5);
                  if (localLocation == null)
                    break label538;
                }
                String str2 = "";
                str3 = "";
                str4 = Locale.getDefault().getCountry();
                Address localAddress;
                if (localLocation != null)
                {
                  localAddress = lookupAddress(localLocation.getLatitude(), localLocation.getLongitude(), true);
                  if (localAddress != null)
                    break label544;
                  localAddress = sCurrentAddress;
                  if ((localAddress != null) && (localAddress.getCountryCode() != null))
                  {
                    str2 = checkNull(localAddress.getLocality());
                    str4 = checkNull(localAddress.getCountryCode());
                    str3 = checkNull(localAddress.getAdminArea());
                  }
                }
                localObject3 = checkNull(((Address)localObject1).getLocality());
                localObject4 = checkNull(((Address)localObject2).getLocality());
                localObject5 = checkNull(((Address)localObject1).getAdminArea());
                localObject6 = checkNull(((Address)localObject2).getAdminArea());
                localObject7 = checkNull(((Address)localObject1).getCountryCode());
                localObject8 = checkNull(((Address)localObject2).getCountryCode());
                if ((!str2.equals(localObject3)) && (!str2.equals(localObject4)))
                  break;
                Object localObject9;
                if (str2.equals(localObject3))
                {
                  localObject9 = localObject4;
                  if (((String)localObject9).length() == 0)
                  {
                    localObject9 = localObject6;
                    if (!str4.equals(localObject8))
                      localObject9 = (String)localObject9 + " " + (String)localObject8;
                  }
                  localObject4 = localObject3;
                  localObject6 = localObject5;
                  localObject8 = localObject7;
                }
                while (true)
                {
                  str1 = valueIfEqual(((Address)localObject1).getAddressLine(0), ((Address)localObject2).getAddressLine(0));
                  if ((str1 == null) || ("null".equals(str1)))
                    break;
                  if (!str2.equals(localObject9));
                  return str1 + " - " + (String)localObject9;
                  localLocation = null;
                  break label200:
                  ++i;
                  break label156:
                  sCurrentAddress = localAddress;
                  break label253:
                  localObject9 = localObject3;
                  if (((String)localObject9).length() == 0)
                  {
                    localObject9 = localObject5;
                    if (!str4.equals(localObject7))
                      localObject9 = (String)localObject9 + " " + (String)localObject7;
                  }
                  localObject3 = localObject4;
                  localObject5 = localObject6;
                  localObject7 = localObject8;
                }
                str1 = valueIfEqual(((Address)localObject1).getThoroughfare(), ((Address)localObject2).getThoroughfare());
              }
              while ((str1 != null) && (!"null".equals(str1)));
              str1 = valueIfEqual((String)localObject3, (String)localObject4);
              if ((str1 == null) || ("".equals(str1)))
                break label769;
              localObject13 = localObject5;
              localObject14 = localObject7;
            }
            while ((localObject13 == null) || (localObject13.length() <= 0));
            if (!localObject14.equals(str4))
              return str1 + ", " + localObject13 + " " + localObject14;
            return str1 + ", " + localObject13;
            if ((str3.equals(localObject5)) && (str3.equals(localObject6)))
            {
              if ("".equals(localObject3))
                label769: localObject3 = localObject4;
              if ("".equals(localObject4))
                localObject4 = localObject3;
              if (!"".equals(localObject3))
              {
                if (((String)localObject3).equals(localObject4))
                  return (String)localObject3 + ", " + str3;
                return (String)localObject3 + " - " + (String)localObject4;
              }
            }
            float[] arrayOfFloat = new float[1];
            Location.distanceBetween(d1, d2, d3, d4, arrayOfFloat);
            if ((int)GalleryUtils.toMile(arrayOfFloat[0]) >= 20)
              break label948;
            str1 = getLocalityAdminForAddress((Address)localObject1, true);
          }
          while (str1 != null);
          str1 = getLocalityAdminForAddress((Address)localObject2, true);
        }
        while (str1 != null);
        str1 = valueIfEqual((String)localObject5, (String)localObject6);
        if ((str1 == null) || ("".equals(str1)))
          break label1026;
        localObject12 = localObject7;
      }
      while ((localObject12.equals(str4)) || (localObject12 == null) || (localObject12.length() <= 0));
      return str1 + " " + localObject12;
      label1026: str1 = valueIfEqual((String)localObject7, (String)localObject8);
    }
    while ((str1 != null) && (!"".equals(str1)));
    Object localObject10 = ((Address)localObject1).getCountryName();
    Object localObject11 = ((Address)localObject2).getCountryName();
    if (localObject10 == null)
      localObject10 = localObject7;
    if (localObject11 == null)
      localObject11 = localObject8;
    if ((localObject10 == null) || (localObject11 == null))
      return null;
    if ((((String)localObject10).length() > 8) || (((String)localObject11).length() > 8))
      return (String)localObject7 + " - " + (String)localObject8;
    return (String)(String)(String)(String)(String)(String)(String)(String)(String)(String)(String)((String)localObject10 + " - " + (String)localObject11);
  }

  public Address lookupAddress(double paramDouble1, double paramDouble2, boolean paramBoolean)
  {
    long l = ()(6378137.0D * (90.0D * (2.0D * (90.0D + paramDouble1)) + (180.0D + paramDouble2)));
    byte[] arrayOfByte = null;
    if (paramBoolean);
    try
    {
      BlobCache localBlobCache = this.mGeoCache;
      arrayOfByte = null;
      if (localBlobCache != null)
        arrayOfByte = this.mGeoCache.lookup(l);
      NetworkInfo localNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
      if ((arrayOfByte == null) || (arrayOfByte.length == 0))
      {
        if (localNetworkInfo != null)
          if (localNetworkInfo.isConnected())
          {
            List localList = this.mGeocoder.getFromLocation(paramDouble1, paramDouble2, 1);
            if (!localList.isEmpty())
            {
              Address localAddress1 = (Address)localList.get(0);
              ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
              DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
              Locale localLocale1 = localAddress1.getLocale();
              writeUTF(localDataOutputStream, localLocale1.getLanguage());
              writeUTF(localDataOutputStream, localLocale1.getCountry());
              writeUTF(localDataOutputStream, localLocale1.getVariant());
              writeUTF(localDataOutputStream, localAddress1.getThoroughfare());
              int i = localAddress1.getMaxAddressLineIndex();
              localDataOutputStream.writeInt(i);
              for (int j = 0; j < i; ++j)
                writeUTF(localDataOutputStream, localAddress1.getAddressLine(j));
              writeUTF(localDataOutputStream, localAddress1.getFeatureName());
              writeUTF(localDataOutputStream, localAddress1.getLocality());
              writeUTF(localDataOutputStream, localAddress1.getAdminArea());
              writeUTF(localDataOutputStream, localAddress1.getSubAdminArea());
              writeUTF(localDataOutputStream, localAddress1.getCountryName());
              writeUTF(localDataOutputStream, localAddress1.getCountryCode());
              writeUTF(localDataOutputStream, localAddress1.getPostalCode());
              writeUTF(localDataOutputStream, localAddress1.getPhone());
              writeUTF(localDataOutputStream, localAddress1.getUrl());
              localDataOutputStream.flush();
              if (this.mGeoCache != null)
                this.mGeoCache.insert(l, localByteArrayOutputStream.toByteArray());
              localDataOutputStream.close();
              return localAddress1;
            }
          }
      }
      else
      {
        DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
        String str1 = readUTF(localDataInputStream);
        String str2 = readUTF(localDataInputStream);
        String str3 = readUTF(localDataInputStream);
        Locale localLocale2 = null;
        if (str1 != null)
          if (str2 != null)
            break label456;
        for (localLocale2 = new Locale(str1); !localLocale2.getLanguage().equals(Locale.getDefault().getLanguage()); localLocale2 = new Locale(str1, str2, str3))
          while (true)
          {
            localDataInputStream.close();
            return lookupAddress(paramDouble1, paramDouble2, false);
            label456: if (str3 != null)
              break;
            localLocale2 = new Locale(str1, str2);
          }
        Address localAddress2 = new Address(localLocale2);
        localAddress2.setThoroughfare(readUTF(localDataInputStream));
        int k = localDataInputStream.readInt();
        for (int i1 = 0; i1 < k; ++i1)
        {
          String str4 = readUTF(localDataInputStream);
          localAddress2.setAddressLine(i1, str4);
        }
        localAddress2.setFeatureName(readUTF(localDataInputStream));
        localAddress2.setLocality(readUTF(localDataInputStream));
        localAddress2.setAdminArea(readUTF(localDataInputStream));
        localAddress2.setSubAdminArea(readUTF(localDataInputStream));
        localAddress2.setCountryName(readUTF(localDataInputStream));
        localAddress2.setCountryCode(readUTF(localDataInputStream));
        localAddress2.setPostalCode(readUTF(localDataInputStream));
        localAddress2.setPhone(readUTF(localDataInputStream));
        localAddress2.setUrl(readUTF(localDataInputStream));
        localDataInputStream.close();
        return localAddress2;
      }
      return null;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public static class SetLatLong
  {
    public double mMaxLatLatitude = -90.0D;
    public double mMaxLatLongitude;
    public double mMaxLonLatitude;
    public double mMaxLonLongitude = -180.0D;
    public double mMinLatLatitude = 90.0D;
    public double mMinLatLongitude;
    public double mMinLonLatitude;
    public double mMinLonLongitude = 180.0D;
  }
}

/* Location:           D:\camera42_patched_v2\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.android.gallery3d.util.ReverseGeocoder
 * JD-Core Version:    0.5.4
 */