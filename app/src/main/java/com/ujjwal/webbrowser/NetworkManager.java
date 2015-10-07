package com.ujjwal.webbrowser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkManager {
	
	private Context context;
	   public NetworkManager(Context context) {
	      this.context = context;
	   }

	
	public boolean checkInternetConenction(){
	      ConnectivityManager check = null; 
	      check=(ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
	      if (check != null) 
	      {
	         NetworkInfo[] info = check.getAllNetworkInfo();
	         if (info != null) 
	            for (int i = 0; i < info.length; i++) 
	            if (info[i].getState() == NetworkInfo.State.CONNECTED)
	            {
	               return true;
	            }

	      }else{
	    	 Toast.makeText(context, "Not connected to Internet",
	         Toast.LENGTH_SHORT).show();
	      }
	         return false;
	   }

}
