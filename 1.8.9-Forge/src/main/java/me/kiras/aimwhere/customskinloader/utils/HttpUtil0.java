package me.kiras.aimwhere.customskinloader.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.lang3.StringUtils;

public class HttpUtil0 {

   public static boolean isLocal(String url) {
      return url == null?false:!url.startsWith("http");
   }

   public static void ignoreHttpsCertificate() {
      HostnameVerifier doNotVerify = new HostnameVerifier() {
         public boolean verify(String hostname, SSLSession session) {
            return true;
         }
      };
      TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
         public void checkClientTrusted(X509Certificate[] chain, String authType) {}
         public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
         }
         public void checkServerTrusted(X509Certificate[] chain, String authType) {}
      }};

      try {
         SSLContext e = SSLContext.getInstance("TLS");
         e.init((KeyManager[])null, trustAllCerts, new SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(e.getSocketFactory());
         HttpsURLConnection.setDefaultHostnameVerifier(doNotVerify);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static String parseAddress(String address) {
      if(StringUtils.isEmpty(address)) {
         return null;
      } else {
         String[] addresses = address.split(":");

         InetAddress add;
         try {
            add = InetAddress.getByName(addresses[0]);
         } catch (UnknownHostException var4) {
            var4.printStackTrace();
            return null;
         }

         return add.getHostAddress() + (addresses.length == 2?addresses[1]:"25565");
      }
   }

   public static boolean isLanServer(String standardAddress) {
      if(StringUtils.isEmpty(standardAddress)) {
         return true;
      } else {
         String[] addresses = standardAddress.split(":");
         int numIp = getNumIp(addresses[0]);
         return numIp == 0 || numIp == getNumIp("127.0.0.1") || numIp >= getNumIp("192.168.0.0") && numIp <= getNumIp("192.168.255.255") || numIp >= getNumIp("10.0.0.0") && numIp <= getNumIp("10.255.255.255") || numIp >= getNumIp("172.16.0.0") && numIp <= getNumIp("172.31.255.255");
      }
   }

   public static int getNumIp(String ip) {
      int num = 0;
      String[] ips = ip.split("\\.");
      if(ips.length != 4) {
         return 0;
      } else {
         for(int i = 0; i < 4; ++i) {
            num += Integer.parseInt(ips[i]) * (256 ^ 3 - i);
         }

         return num;
      }
   }
}
