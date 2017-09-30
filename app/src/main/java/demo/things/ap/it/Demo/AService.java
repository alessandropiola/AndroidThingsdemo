package demo.things.ap.it.Demo;


import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class AService extends Service {

   private static final String TAG_LOG = "ADV - SERVICE VIEW(A)";


   Timer timer=new Timer();
	MyDataModel dataModel=MyDataModel.getInstance();


	Handler handler;

   @Override
   public void onCreate() {
	   super.onCreate();

       
       

       handler=new Handler(){
           @Override
           public void handleMessage(Message msg) {
			   super.handleMessage(msg);


           }  };
   
           timer.schedule(new TimerTask() {
			   private Integer TempoImpressione = 10;

		@Override
           public void run()  {
            if (dataModel.isOk()) {
                dataModel.Update("upd");
                Log.i("service","UPD");
            }

           }

        	   
   },0,10000);
       //faccio partire il tread in background
       
  
 
   }

@Override
   public void onDestroy() {
           super.onDestroy();
           Log.i(TAG_LOG, "timer Videodestroy");
           timer.cancel(); // una volta che il servizio viene a meno. 'puliamo' il timer
   }

@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
	}
   
}


