package com.hrxiang.android.base.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by hairui.xiang on 2017/8/29.
 */

public class NetworkUtils {
    public static final String NETWORK_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private NetworkUtils() {
    }

    public static IntentFilter getNetworkChangeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_CHANGE_ACTION);
        return filter;
    }

    /**
     * Get the Sim card Operator Name, MCC and MNC
     *
     * @param context context
     * @return int
     */
    public static String getOperatorName(Context context) {
        /*
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         */
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            // getSimOperatorName就可以直接获取到运营商的名字
            return telephonyManager.getSimOperatorName()
                    + "(" + telephonyManager.getSimOperator() + ")";
        }
        return "Unknown";
    }

    /**
     * Check if the connection is fast
     */
    private static boolean isConnectionFast(int type, int subType) {
        switch (type) {
            case ConnectivityManager.TYPE_WIFI:
                return true;
            case ConnectivityManager.TYPE_MOBILE:
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return false; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return true; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return true; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return false; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return true; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return true; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return true; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return true; // ~ 400-7000 kbps
                    /*
                     * Above API level 7, make sure to set android:targetSdkVersion
                     * to appropriate level to use these
                     */
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                        return true; // ~ 1-2 Mbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                        return true; // ~ 5 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                        return true; // ~ 10-20 Mbps
                    case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                        return false; // ~25 kbps
                    case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                        return true; // ~ 10+ Mbps
                    // Unknown
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    /**
     * Get the network info
     */
    public static NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) ContextProvider.getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo();
        return (null != info && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo();
        return (null != info && info.isConnected()
                && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected()
                && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected() && isConnectionFast(info.getType(),
                info.getSubtype()));
    }

    /**
     * 网络监测，判断当前网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo();
        return null != info && info.isAvailable();
    }

    /**
     * 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     */
    public static boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                buffer.append(content);
            }
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
        }
        return false;
    }

    /**
     * Unknown network class
     */
    public static final String NETWORK_CLASS_UNKNOWN = "Unknown";

    /**
     * wifi net work
     */
    public static final String NETWORK_WIFI = "WIFI";

    /**
     * "2G" networks
     */
    public static final String NETWORK_CLASS_2_G = "2G";

    /**
     * "3G" networks
     */
    public static final String NETWORK_CLASS_3_G = "3G";

    /**
     * "4G" networks
     */
    public static final String NETWORK_CLASS_4_G = "4G";


    private static String getNetWorkClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;

            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public static String getNetWorkType(Context context) {
        if (isConnectedWifi(context)) {
            return NETWORK_WIFI;
        } else if (isConnectedMobile(context)) {
            return getNetWorkClass(context);
        }
        return NETWORK_CLASS_UNKNOWN;
    }

    public static String getNetworkInfo(Context context) {
        return getOperatorName(context) + "_" + getNetWorkType(context);
    }
}
