package com.trinetix.app.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Проверяем, есть ли интернет.
 * Created by fess on 5/19/14.
 */
public class InternetHelper
{
	public static boolean internetIsOn(Context context)
	{
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi != null)
		{
			if (mWifi.isConnected())
			{
				return true;
			}
		}

		NetworkInfo m3g = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return (m3g != null && m3g.isConnected());
	}
}
