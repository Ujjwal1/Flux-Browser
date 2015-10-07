package com.ujjwal.webbrowser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import com.ujjwal.webbrowser.SimpleGestureFilter.SimpleGestureListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;



@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements OnClickListener{
    final Activity activity = this;
    private Button go;
    private WebView ourBrowser;
    private EditText url;
    private String filename = "Flux Download";
    public String APP_NAME = "Flux Browser";
    private static final String DEFAULT_URL = "https://www.google.com";
    private SimpleGestureFilter detector;
    private NetworkManager network ;
    Bundle inState;
    
    //AddTabs addtab;
   
    @SuppressWarnings({ "unused", "resource" })
	@SuppressLint({ "SetJavaScriptEnabled", "SdCardPath"}) @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        File wallpaperDirectory = new File("/sdcard/"+filename+"/");
        wallpaperDirectory.mkdirs();
        File outputFile = new File(wallpaperDirectory, filename);
        try {
			FileOutputStream fos = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e1) {
			Log.e("FileOutputStrem Error", "error in implementing function");
			e1.printStackTrace();
		}
        setContentView(R.layout.activity_main);
        network = new NetworkManager(getApplicationContext());
        ourBrowser = (WebView)findViewById(R.id.wvBrowser);
        ourBrowser = BrowserSettings(ourBrowser);
        go = (Button)findViewById(R.id.bgo);
        url = (EditText)findViewById(R.id.etUrm);
        url.setSingleLine(true);
        ourBrowser.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					url.setCursorVisible(false);
					return false;
				}
		});

        ourBrowser.requestFocus(View.FOCUS_DOWN);
        go.setOnClickListener(this);
        url.setOnClickListener(this);

        ourBrowser.setWebChromeClient(new myWebChromeClient());
  
        ourBrowser.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
            		url.setText("");
       	         	Toast.makeText(getApplicationContext(), "Not connected to Internet",
       	 	        Toast.LENGTH_SHORT).show();
            	
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
            	if(network.checkInternetConenction()){
            		if(savedInstanceState != null){
            			ourBrowser.restoreState(savedInstanceState);
            		}else
            			view.loadUrl(url);
            	}else{
                    ourBrowser.loadUrl("file:///android_asset/Connection_Issue.html");
                }
                return false;
            }
        });
         
        try{
        	if(network.checkInternetConenction()) {
                if (savedInstanceState != null) {
                    ourBrowser.restoreState(savedInstanceState);
                } else if (ourBrowser.canGoForward()) {
                    ourBrowser.goForward();
                } else if (ourBrowser.canGoBack()) {
                    ourBrowser.goBack();
                } else {
                    ourBrowser.loadUrl(DEFAULT_URL);
                }
            }else{
                ourBrowser.loadUrl("file:///android_asset/Connection_Issue.html");
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
    }


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		inState = outState;
		ourBrowser.saveState(inState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		ourBrowser.restoreState(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		if(ourBrowser.canGoBack()){
			ourBrowser.goBack();
		}else{
			AlertDialog.Builder alert= new AlertDialog.Builder(this);
			alert.setTitle("Exitting...")
			.setMessage("Do you want to clear cache?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	ourBrowser.clearCache(true);
					moveTaskToBack(true);
			        Toast.makeText(MainActivity.this, "Cache Cleared", Toast.LENGTH_SHORT).show();
			        
			    }})
			 .setNegativeButton(android.R.string.no,  new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {

	                 dialog.cancel();
	     			moveTaskToBack(true);
	             }}).create().show();
            activity.finish();
		}

	}


	@SuppressLint({ "NewApi", "InlinedApi" })
	public void onClick(View view){	
		switch (view.getId()) {
		case R.id.bgo:
						if(go.getText().equals("Go")){
								final String theWebSite = getWebsiteLink();
								ourBrowser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
								if(network.checkInternetConenction()){
									if(theWebSite.contains(".mp3") || theWebSite.contains(".mp4")|| theWebSite.contains(".pdf")){
										ourBrowser.setDownloadListener(new DownloadListener() {
									        public void onDownloadStart(String url, String userAgent,
									            String contentDisposition, String mimetype,
									            long contentLength) {
									        	Request request = new Request(Uri.parse(theWebSite));
									            request.allowScanningByMediaScanner();
									            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
									            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename); 
									            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
									            dm.enqueue(request);        

									        }
									    });
								}else{
									
										ourBrowser.loadUrl(theWebSite);
								}
								}else{
									go.setText("Go");
								}
								InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(url.getApplicationWindowToken(), 0);
						}
						else if(go.getText().equals("X")){
							ourBrowser.stopLoading();
							this.setProgress(100);
							go.setText("Go");
						}
						break;
		case R.id.etUrm:
                        url.setText(ourBrowser.getUrl());
						url.setCursorVisible(true);
						break;
		case R.id.wvBrowser: 
						url.setCursorVisible(false);
                        View focusedView = this.getCurrentFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
						break;
		}
    }

	private String getWebsiteLink() {
		String theWebSite = url.getText().toString().trim();
		go.setText("X");
		if(theWebSite.contains(" ")||(!theWebSite.contains("."))){
			
			theWebSite.replace(" ", "+");
			theWebSite = SearchLink(theWebSite);
		}else
		if(!theWebSite.contains("http")){
				theWebSite = "http://"+theWebSite;
		}
		return theWebSite;
	}


	@SuppressLint({ "SetJavaScriptEnabled", })
	private WebView BrowserSettings(WebView ourBrowser) {
		    	WebSettings settings = ourBrowser.getSettings();
		    	settings.setJavaScriptEnabled(true);
		    	settings.setLoadWithOverviewMode(true);
		    	settings.setLoadWithOverviewMode(true);
		    	settings.setBuiltInZoomControls(true);
		    	settings.setSupportMultipleWindows(true);
		    	settings.setPluginState(PluginState.ON);
		    	settings.setUseWideViewPort(true);
		    	settings.setAllowFileAccess(true);
		    	//ourBrowser.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
		        return ourBrowser;
		        
	}
	
	 private String SearchLink(String search) {
			// TODO Auto-generated method stub
			return "http://www.google.com/search?q="+search+"&sourceid=ie7&rls=com.microsoft:en-US&ie=utf8&oe=utf8";
		}
	 
	 public class myWebChromeClient extends WebChromeClient{

         public void onProgressChanged(WebView view, int progress)
         {
        	 if(ourBrowser.getTitle() != null)
        		 activity.setTitle("Loading "+ourBrowser.getTitle()+"...");
        	 else
        		 activity.setTitle(APP_NAME);
             activity.setProgress(progress * 100);
             if(!ourBrowser.getUrl().equalsIgnoreCase("file:///android_asset/Connection_Issue.html")) {
                 url.setText(ourBrowser.getUrl());
             }
             go.setText("X");

             if(progress == 100){
                        /*int fraction;
                        if(ourBrowser.getUrl().endsWith("\\")){
                            fraction = ourBrowser.getUrl().indexOf("\\",8);
                        }else{
                            fraction = ourBrowser.getUrl().length();
                        }
                        String domain = ourBrowser.getUrl().subSequence(8,fraction).toString();
                        url.setText("http://"+domain);*/
	                    activity.setTitle(ourBrowser.getTitle());
	                	go.setText("Go");
	                }
         }
         
         @Override
         public void onReceivedIcon(WebView view, Bitmap icon) {
             super.onReceivedIcon(view, icon);
             
         }
         
        public boolean shouldOverrideUrlLoading(WebView view, String url)
         {
         	WebChromeClient mWebChromeClient= new WebChromeClient();
         	if(network.checkInternetConenction()){
         		view.setWebChromeClient(mWebChromeClient);
         		view.loadUrl(url);
         	}else{
                ourBrowser.loadUrl("file:///android_asset/Connection_Issue.html");
            }
             return true;
         }
     
	 }

	public void onDoubleTap() {
		ClipboardManager ClipMan = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		ClipMan.setText(ClipMan.getText().toString());
		ourBrowser.findAll(ClipMan.getText().toString());
		try
		{
		    Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
		    m.invoke(ourBrowser, true);
		}
		catch (Throwable ignored){}
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_sourceCode:
           getSourceCode(ourBrowser.getUrl());
    	    break;
            case R.id.menu_settings:
                Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.menu_history:
                WebBackForwardList wbfl = ourBrowser.copyBackForwardList();
                String hist;
                if(wbfl.getSize()==0){
                    hist = "No History!";
                }else {
                    hist = "";
                }
                for(int a = 0; a< wbfl.getSize();a++){
                    hist += wbfl.getItemAtIndex(a).getOriginalUrl()+"\n\n";
                }
                i = new Intent(getApplicationContext(),History.class);
                i.putExtra("history",hist);
                startActivity(i);

            default:
	        
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	 public void showHTML(String html) {
         new AlertDialog.Builder(getApplicationContext()).setTitle("HTML").setMessage(html)
                 .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
     }

    private String getSourceCode(String url){
        String html = "";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);


            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null)
            {
                str.append(line);
            }
            in.close();
            html = str.toString();
        }catch (IOException e){
            Log.e("[GET REQUEST]","Network Exception",e);
        }
        return html;
    }
}
