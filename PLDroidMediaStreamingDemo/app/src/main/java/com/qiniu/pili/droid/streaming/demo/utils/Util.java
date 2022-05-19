package com.qiniu.pili.droid.streaming.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.Record;
import com.qiniu.android.dns.dns.DnsUdpResolver;
import com.qiniu.android.dns.dns.DohResolver;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static com.qiniu.android.dns.IResolver.DNS_DEFAULT_TIMEOUT;

public class Util {

    public static boolean isSupportScreenCapture() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isSupportHWEncode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static void showToast(Activity activity, String msg) {
        showToast(activity, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(final Activity activity, final String msg, final int duration) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, duration).show();
            }
        });
    }

    public static String syncRequest(String appServerUrl) {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(appServerUrl).openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(5000);
            httpConn.setReadTimeout(10000);
            int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int length = httpConn.getContentLength();
            if (length <= 0) {
                length = 16 * 1024;
            }
            InputStream is = httpConn.getInputStream();
            byte[] data = new byte[length];
            int read = is.read(data);
            is.close();
            if (read <= 0) {
                return null;
            }
            return new String(data, 0, read);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 配置自定义 DNS 服务器，非必须
     *
     * - 可通过创建 {@link DnsUdpResolver} 对象配置自定义的 DNS 服务器地址
     * - 可通过创建 {@link DohResolver} 对象配置支持 Doh(Dns over http) 协议的 url
     * 其中，UDP 的方式解析速度快，但是安全性无法得到保证，HTTPDNS 的方式解析速度慢，但是安全性有保证，您可根据您的
     * 使用场景自行选择合适的解析方式
     */
    public static DnsManager getMyDnsManager() {
        IResolver[] resolvers = new IResolver[2];
        // 配置自定义 DNS 服务器地址
        String[] udpDnsServers = new String[]{"223.5.5.5", "114.114.114.114", "1.1.1.1", "208.67.222.222"};
        resolvers[0] = new DnsUdpResolver(udpDnsServers, Record.TYPE_A, DNS_DEFAULT_TIMEOUT);
        // 配置 HTTPDNS 地址
        String[] httpDnsServers = new String[]{"https://223.6.6.6/dns-query", "https://8.8.8.8/dns-query"};
        resolvers[1] = new DohResolver(httpDnsServers, Record.TYPE_A, DNS_DEFAULT_TIMEOUT);
        return new DnsManager(NetworkInfo.normal, resolvers);
    }

    public static String getVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
        String userId = preferences.getString(Config.KEY_USER_ID, "");
        if ("".equals(userId)) {
            userId = userId();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Config.KEY_USER_ID, userId);
            editor.apply();
        }
        return userId;
    }

    private static String userId() {
        Random r = new Random();
        return System.currentTimeMillis() + "" + r.nextInt(999);
    }
}
