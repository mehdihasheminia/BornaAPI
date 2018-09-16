package com.bornaapp.calendar;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;;

/**
 * Created by Hashemi on 04/04/2018.<br>
 * Returns a time/onlineDate from internet server
 * <br>
 * <h1>Warning:</h1>
 * Do not query more than once every 4 second, or ntp server may block you
 * <br>
 * <h1>Example:</h1>
 * final Date[] netDate;<br>
 * new NTPDate().get(new NTPDate.ResultListener() {<br>
 * Override<br> public void OnSuccess(Date onlineDate) {<br>
 * netDate[0] = onlineDate;<br>
 * System.out.println("Mehdi," + netDate[0].toString());<br>
 * }<br>
 * Override<br> public void OnFailure() {}<br>
 * });<br>
 * <p>
 * https://stackoverflow.com/questions/10662805/inetaddress-in-android
 */

public class NTPDate {

    private String TAG = "Mehdi(NTPDate):";

    private int currentServerIndex = 0;
    private ArrayList<String> ntpServers = new ArrayList<String>();

    private final int NTP_SERVER_TIMEOUT_MS = 4000;

    public NTPDate() {
        ntpServers.add("pool.ntp.org");
        ntpServers.add("0.pool.ntp.org");
        ntpServers.add("1.pool.ntp.org");
        ntpServers.add("ntp.day.ir");
        ntpServers.add("0.ir.pool.ntp.org");
        ntpServers.add("1.ir.pool.ntp.org");
        ntpServers.add("time.nist.gov");
        ntpServers.add("0.asia.pool.ntp.org");
        ntpServers.add("1.asia.pool.ntp.org");
        ntpServers.add("3.asia.pool.ntp.org");
    }

    public void get(final ResultListener listener) {

        new Thread(new Runnable() {
            public void run() {

                try {
                    final NTPUDPClient client = new NTPUDPClient();
                    client.setDefaultTimeout(NTP_SERVER_TIMEOUT_MS);

                    final InetAddress ipAddress = InetAddress.getByName(ntpServers.get(currentServerIndex));

                    TimeInfo timeInfo = null;
                    timeInfo = client.getTime(ipAddress);

                    long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                    final Date date = new Date(serverTime);
                    listener.OnSuccess(date);

                } catch (UnknownHostException e) {
                    listener.OnFailure();
                    System.out.println(TAG + e.getMessage());
                    e.printStackTrace();
                    tryNextServer();
                } catch (IOException e) {
                    listener.OnFailure();
                    System.out.println(TAG + e.getLocalizedMessage() + " ,on " + ntpServers.get(currentServerIndex));
                    e.printStackTrace();
                    tryNextServer();
                }
            }
        }).start();
    }

    private void tryNextServer() {
        currentServerIndex++;
        if (currentServerIndex >= ntpServers.size()) {
            currentServerIndex = 0;
        }
    }

    public interface ResultListener {
        void OnSuccess(Date date);

        void OnFailure();
    }
}
