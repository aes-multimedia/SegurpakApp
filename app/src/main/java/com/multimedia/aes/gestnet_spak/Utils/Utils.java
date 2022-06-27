package com.multimedia.aes.gestnet_spak.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Utils {



    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String escaparString(String s) {
        return s.replace("'", "\'").replace("'", "\"");
    }
    public static String getConnectionType(Context context) {
        String typeConnection;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != activeNetwork && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                typeConnection = "TYPE_WIFI";

            }else{
                typeConnection = "TYPE_MOBILE";
            }
        }else{
            typeConnection = "NONE";
        }
        return typeConnection;
    }
    public static boolean compruebaConexion(Context context) {
        boolean connected;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != activeNetwork && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int numberOfLevels = 100;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int levelWifi = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                if (levelWifi < 10) {
                    connected = false;
                } else {
                    connected = true;
                }
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                int tipoNetWork = activeNetwork.getSubtype();


                switch (tipoNetWork) {
                    case TelephonyManager.NETWORK_TYPE_HSDPA:// HSDPA 3g+
                    case TelephonyManager.NETWORK_TYPE_UMTS:// HSDPA 3gg
                    case TelephonyManager.NETWORK_TYPE_HSPA://3G
                    case TelephonyManager.NETWORK_TYPE_EVDO_0://3G
                    case TelephonyManager.NETWORK_TYPE_EVDO_A://3G
                    case TelephonyManager.NETWORK_TYPE_HSUPA:// HSUPA 3.5G Plus
                    case TelephonyManager.NETWORK_TYPE_EHRPD:// EHRPD 3g+
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //3g+ 4.9 Mbit/s
                    case TelephonyManager.NETWORK_TYPE_HSPAP: //3G
                    case TelephonyManager.NETWORK_TYPE_LTE:// LTE >4g
                    case TelephonyManager.NETWORK_TYPE_EDGE:// EDGE 2.5g
                    case TelephonyManager.NETWORK_TYPE_NR:// EDGE 5g

                        //connected = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            SignalStrength signalStrength = telephonyManager.getSignalStrength();
                            int levellStrength = telephonyManager.getSignalStrength().getLevel();
                           if(signalStrength != null){
                               if (levellStrength > 0) {
                                   connected = true;
                               } else {
                                   connected = false;
                               }
                           }else{
                               connected = false;
                           }

                        }else{
                            connected = true;
                        }

                        break;
                    default:
                        connected = false;
                        break;
                }
            }else{
                connected = false;
            }
        }else{
            connected = false;
        }
        return connected;
    }
    public static List<String> getDuration(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        List<String> duracion = new ArrayList<>();

       /* long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);*/
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        duracion.add(String.valueOf(hours));
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        duracion.add(String.valueOf(minutes));
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        duracion.add(String.valueOf(seconds));

        StringBuilder sb = new StringBuilder(64);
        /*sb.append(days);
        sb.append(" Days ");*/
        sb.append(hours);
        sb.append(" horas ");
        sb.append(minutes);
        sb.append(" minutos");
        /*sb.append(seconds);
        sb.append("seg");*/
        duracion.add(sb.toString());
        return duracion;
    }
    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getDateTimeEsp() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static boolean isSpfT(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    public static boolean isEnfT(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    public static String formatDateTimeEn(String fecha) throws ParseException {
        DateFormat dateFormatEsp = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat dateFormatEn = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaDate = dateFormatEsp.parse(fecha);
        return dateFormatEn.format(fechaDate);
    }

    public static String formatDateTimeEsp(String fecha) throws ParseException {
        DateFormat dateFormatEsp = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dateFormatEn = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaDate = dateFormatEn.parse(fecha);
        return dateFormatEsp.format(fechaDate);
    }

    public static String formatDateTimeSharedToEsp(String fecha) throws ParseException {
        DateFormat dateFormatEsp = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat dateFormatEsp2 = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaDate = dateFormatEsp.parse(fecha);
        return dateFormatEsp2.format(fechaDate);
    }
}
