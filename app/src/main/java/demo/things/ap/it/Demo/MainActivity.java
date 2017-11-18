package demo.things.ap.it.Demo;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hoin.usbsdk.UsbController;
import com.hoin.wfsdk.PrintPic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MainActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "HomeActivity";

    private static final String BUTTON_PIN_print = "BCM21"; // GPIO port wired to the button
    private static final String BUTTON_PIN_next = "BCM20"; // GPIO port wired to the button

    private Gpio mButtonGpio_print;
    private Gpio mButtonGpio_next;
    TextView testobtn1;

    Led72xx Led;

    TextToSpeech t1;

    FirebaseStorage storage ;
    StorageReference storageRef ;

// Access the display:

    private BluetoothAdapter mBluetoothAdapter;
    private int intY = 0;
    private int intX = 0;
    private StorageReference islandRef;
    private int finalIi;
    JSONArray jsonArray = null;
    MyDataModel dataModel;
    UsbController usbCtrl = null;
    private int[][] u_infor;
    UsbDevice dev = null;
    private String fbCount;
    private String Android_id;
    private boolean varStart = false;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        //usb-----------------------------------
        usbCtrl = new UsbController(this,mHandler);
        u_infor = new int[5][2];
        u_infor[0][0] = 0x1CBE;
        u_infor[0][1] = 0x0003;
        u_infor[1][0] = 0x1CB0;
        u_infor[1][1] = 0x0003;
        u_infor[2][0] = 0x0483;
        u_infor[2][1] = 0x5740;
        u_infor[3][0] = 0x0493;
        u_infor[3][1] = 0x8760;
        u_infor[4][0] = 0x0471;
        u_infor[4][1] = 0x0055;
        //-USB

        testobtn1 = (TextView) findViewById(R.id.txtCodeLic);

        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        PeripheralManagerService service = new PeripheralManagerService();

        final String uuid  = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference licAIO = database.getReference("LicenzaAIO");
        licAIO.child("VAL").setValue("sblocco-logo223*91-sfondo1500*1200");

        licAIO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                Log.i(TAG, "dataSnapshot" + new JSONObject(value));
                try {
                    JSONObject jo = new JSONObject(value);
                    String sblocco = jo.getString(uuid).toString();
                    Log.w("dataSnapshot",sblocco);
                    final String[] separated = sblocco.split("-");
                    if (separated[0].toString().equals("YES".toString())) {
                        //dwn sfondo
                        islandRef = storageRef.child(separated[2]);
                        File localFile = null;
                        try {
                            localFile = File.createTempFile(separated[2], ".jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final File finalLocalFile = localFile;
                        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.i(TAG+"-DWN",  " dwn ok "+finalLocalFile.getName());
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        RelativeLayout rl = (RelativeLayout)findViewById(R.id.mainframe);
                                        final Bitmap bMap = BitmapFactory.decodeFile(getBaseContext().getCacheDir() + "/" + finalLocalFile.getName());

                                        Drawable dr = new BitmapDrawable(bMap);
                                        rl.setBackgroundDrawable(dr);

                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.e(TAG+"-DWN", "dwn err"+separated[1]);
                            }
                        });

                        //dwn logo
                        islandRef = storageRef.child(separated[1]);
                        localFile = null;
                        try {
                            localFile = File.createTempFile(separated[1], ".jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final File finalLocalFile2 = localFile;
                        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.i(TAG+"-DWN",  " dwn ok "+finalLocalFile2.getName());
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        final ImageView img = (ImageView) findViewById(R.id.logo);
                                        img.setVisibility(View.INVISIBLE);
                                        final Bitmap bMap = BitmapFactory.decodeFile(getBaseContext().getCacheDir() + "/" + finalLocalFile2.getName());
                                        img.setImageBitmap(bMap);
                                        img.setVisibility(View.VISIBLE);
                                    }
                                });


                                }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.e(TAG+"-DWN", "dwn err"+separated[1]);
                            }
                        });
                        //start Services
                        startService(new Intent(getApplicationContext(), AService.class));
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    licAIO.child(uuid).setValue("blocco-logo_ylh.png-sfondo1.png");

                }
                // Find the right array object



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference myRef = database.getReference("DataAIO"+uuid);
        DatabaseReference myCode = database.getReference("Promotion");

        myRef.child("PHOTO").child("1").setValue("qrcode.png");
        myRef.child("PHOTO").child("2").setValue("D04.jpg");
        myRef.child("PHOTO").child("3").setValue("D05.jpg");
        myRef.child("PHOTO").child("4").setValue("qrcode.png");
        myRef.child("PHOTO").child("5").setValue("D06.jpg");
        myRef.child("PHOTO").child("6").setValue("seguicisufacebook.png");
        myRef.child("PHOTO").child("9").setValue("D07.jpg");

        myRef.child("FBcount").setValue("");

        myCode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                Log.i(TAG, "dataSnapshot" + new JSONObject(value));
                JSONObject jsonPhoto = new JSONObject(value);
                // Find the right array object
                try {
                    MyDataModel dataModel=MyDataModel.getInstance();
                    final List<String> values = new ArrayList<String>();
                    for (int i=0; i< jsonPhoto.length(); i++) {
                        JSONObject jo = jsonPhoto.getJSONObject("mailcode");
                        Iterator<String> keys = jo.keys();
                        // get some_name_i_wont_know in str_Name
                        while (keys.hasNext()) {
                            String nodo = keys.next();
                            Log.w(TAG,nodo);
                            JSONObject jol = jo.getJSONObject(nodo);
                            Log.w(TAG, "pre dwn "+ jol.toString());
                            Log.w(TAG, "pre dwn "+ jol.getString("emailAddress"));
                            Log.w(TAG, "pre dwn "+ jol.getString("value"));
                        }


                        }
                    }
                catch (JSONException e) {
                e.printStackTrace();
            }

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Read from the database lettura da firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                t1.speak("CIAO, benvenuti, ecco la lista delle foto", TextToSpeech.QUEUE_FLUSH, null,null);
                Log.w(TAG, "mod.");

                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                Log.i(TAG, "dataSnapshot" + new JSONObject(value));
                JSONObject jsonPhoto = new JSONObject(value);
                // Find the right array object
                try {
                    fbCount = jsonPhoto.getString("FBcount").toString();
                    //JSONArray jsonArray = jsonPhoto.getJSONArray("PHOTO");
                    MyDataModel dataModel=MyDataModel.getInstance();
                    final List<String> values = new ArrayList<String>();
                    for (int i=0; i< jsonPhoto.length(); i++) {
                        jsonArray = jsonPhoto.getJSONArray("PHOTO");
                        for (int ii=0; ii< jsonArray.length(); ii++) {
                            Log.i(TAG+"-DWN", "pre dwn " +jsonArray.get(ii).toString());
                            finalIi = ii;
                            final int finalIi1 = ii;
                            String strName = jsonArray.get(finalIi1).toString();

                                Log.i(TAG+"-DWN","  dwn "+ finalIi + "images/"+strName);

                                    islandRef = storageRef.child("images/"+strName);
                                    File localFile = null;
                                    try {
                                        localFile = File.createTempFile("images", ".jpg");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    final File finalLocalFile = localFile;
                                    islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Log.i(TAG+"-DWN",  " dwn ok "+finalLocalFile.getName());
                                            values.add(finalLocalFile.getName());



                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                            Log.e(TAG+"-DWN", "dwn err");
                                        }
                                    });


                        }

                    }
                    dataModel.setLista(values);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
       testobtn1.setText(uuid);

       try {
            Led = new Led72xx("SPI0.0", 8);
            for (int i = 0; i < Led.getDeviceCount(); i++) {
                Led.setIntensity(i, 13);
                Led.shutdown(i, false);
                Led.clearDisplay(i);
            }
        } catch (IOException e) {
        Log.e(TAG, "Error initializing LED matrix", e);
    }




        try {
            // Step 1. Create GPIO connection.
            mButtonGpio_print = service.openGpio(BUTTON_PIN_print);
            // Step 2. Configure as an input.
            mButtonGpio_print.setDirection(Gpio.DIRECTION_IN);
            // Step 3. Enable edge trigger events.
            mButtonGpio_print.setEdgeTriggerType(Gpio.EDGE_FALLING);
            // Step 4. Register an event callback.
            mButtonGpio_print.registerGpioCallback(mCallback);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API ", e);

        }

        try {
            // Step 1. Create GPIO connection.
            mButtonGpio_next = service.openGpio(BUTTON_PIN_next);
            // Step 2. Configure as an input.
            mButtonGpio_next.setDirection(Gpio.DIRECTION_IN);
            // Step 3. Enable edge trigger events.
            mButtonGpio_next.setEdgeTriggerType(Gpio.EDGE_FALLING);
            // Step 4. Register an event callback.
            mButtonGpio_next.registerGpioCallback(mCallback);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API ", e);

        }
        ImageButton insertcode = (ImageButton) findViewById(R.id.imageButton);
        insertcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Stampo etichetta");
                if( CheckUsbPermission() == true ) {

                    usbCtrl.sendByte(const_escpos.getInit(), dev);
                    const_escpos.getFrontNormale("Buono Offerta","Sconto xyz","mail cliente","All-In-One");
                    printImage("oriz");
                    usbCtrl.sendMsg("-", "GBK", dev);

                    usbCtrl.sendMsg("Consegna questo buono per avere uno sconto", "GBK", dev);
                    usbCtrl.sendByte(const_escpos.getBarreStampoBarre(), dev);
                    usbCtrl.sendByte("8001435500013".getBytes(), dev);
                    usbCtrl.sendByte(const_escpos.getInit(), dev);
                    usbCtrl.sendMsg("valido fino al gg/mm/aaaa\n", "GBK", dev);
                    usbCtrl.sendByte(const_escpos.getTaglioCarta(), dev);


                }

            }
        });
        //loginButton.callOnClick();


/*
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            // Device does not support Bluetooth
            Log.i(TAG, "BT is on board ");
        }
        mBluetoothAdapter.enable();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 101);
        } else {
            Log.i(TAG, "BT is ok ");

        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, "Bonded: "+deviceName);
                t1.speak("Bonded", TextToSpeech.QUEUE_FLUSH, null,null);
            }
        }*/

        // Register for broadcasts when a device is discovered.
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(mReceiver, filter);



    }




    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i("TAG bt",deviceName+" "+deviceHardwareAddress);

                device = mBluetoothAdapter.getRemoteDevice("58:56:09:47:17:D0");
                String pin = "000";
                try {
                    device.setPin(pin.getBytes("UTF8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                device.createBond();
                t1.speak("ciao ho fatto il paring", TextToSpeech.QUEUE_FLUSH, null,null);

            }
        }
    };
    private int intSetphoto = 0;
    private long mLastClickTime;
    // Step 4. Register an event callback.
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {

            Log.i(TAG, "GPIO changed, button pressed."+ gpio.getName());
            // Preventing multiple clicks, using threshold of 1 second
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return true;
            }
            mLastClickTime = SystemClock.elapsedRealtime();



            if (gpio.getName().toString().contains("BCM21".toString())){
                t1.speak("Gastronomia serviamo il numero", TextToSpeech.QUEUE_FLUSH, null,null);


            }

            if (gpio.getName().toString().contains("BCM20".toString())){
                Log.i(TAG, "Stampo etichetta");
                if( CheckUsbPermission() == true ) {

                usbCtrl.sendMsg("D'ITALY", "GBK", dev);
                //usbCtrl.sendMsg("Codice: 808080 e altre cose non per il cliente\n","GBK", dev);
                usbCtrl.sendByte(const_escpos.getInit(), dev);
                const_escpos.getFrontVert("D'Italy                                                   ",
                        "Testo1", "1", "Testo2", "DItalyTower.it","GASTRON");
                printImage("vert");
                usbCtrl.sendMsg(".", "GBK", dev);
                usbCtrl.sendByte(const_escpos.getInitStampoBarre(), dev);
                usbCtrl.sendByte(const_escpos.getBarreStampoBarre(), dev);
                usbCtrl.sendByte("8001435500013".getBytes(), dev);
                usbCtrl.sendByte(const_escpos.getInit(), dev);
                usbCtrl.sendMsg("gg/mm/aaaa\n", "GBK", dev);
                usbCtrl.sendByte(const_escpos.getTaglioCarta(), dev);
                usbCtrl.sendMsg("D'ITALY", "GBK", dev);
                //usbCtrl.sendMsg("Codice: 808080 e altre cose non per il cliente\n","GBK", dev);
                usbCtrl.sendByte(const_escpos.getInit(), dev);
                const_escpos.getFrontNormale("D'Italy  GASTRONOMIA                                                 ",
                        "Num 1", "http://www.ditaly.it/", "seguici su");// "DItalyTower.it","GASTRON");
                printImage("oriz");
                usbCtrl.sendMsg(" ", "GBK", dev);
                usbCtrl.sendByte(const_escpos.getInitStampoBarre(), dev);
                usbCtrl.sendByte(const_escpos.getBarreStampoBarre(), dev);
                usbCtrl.sendByte("8001435500013".getBytes(), dev);
                usbCtrl.sendByte(const_escpos.getInit(), dev);
                usbCtrl.sendMsg("gg/mm/aaaa\n", "GBK", dev);
                usbCtrl.sendByte(const_escpos.getTaglioCarta(), dev);


                }
            }
            /*MyDataModel dataModel=MyDataModel.getInstance();
            List<String> lista = dataModel.getlista();
            Log.i(TAG, intSetphoto+" GPIO changed, button pressed.size:"+lista.size());
            if (intSetphoto > lista.size()-1) {
                intSetphoto=0;
                }
            Log.i(TAG, "GPIO changed, button pressed.photo:"+intSetphoto);
            Log.i(TAG, "lista"+getBaseContext().getCacheDir()+"/"+lista.get(intSetphoto));
            ImageView img = (ImageView)findViewById(R.id.imageView2);
            Bitmap bMap = BitmapFactory.decodeFile(getBaseContext().getCacheDir()+"/"+lista.get(intSetphoto));
            img.setImageBitmap(bMap);

            //RelativeLayout rl = (RelativeLayout) findViewById(R.id.mainframe);
            //Drawable backImg = Drawable.createFromPath(getBaseContext().getCacheDir()+"/"+lista.get(intSetphoto));
           // backImg.setAlpha(90);
            //rl.setBackground(backImg);
            intSetphoto++;*/








       /*     try {

                int ii = 2;

                Led.setRow(ii,7,(byte) 0b11000000);
                Led.setRow(ii,6,(byte) 0b11000000);
                Led.setRow(ii,5,(byte) 0b10000000);
                Led.setRow(ii,4,(byte) 0b10000000);
                Led.setRow(ii,3,(byte) 0b00000000);
                Led.setRow(ii,2,(byte) 0b00000000);
                Led.setRow(ii,1,(byte) 0b00000000);
                Led.setRow(ii,0,(byte) 0x31);
                ii = 3;

                Led.setRow(ii,7,(byte) 0b00000011);
                Led.setRow(ii,6,(byte) 0b00000011);
                Led.setRow(ii,5,(byte) 0b00000111);
                Led.setRow(ii,4,(byte) 0b00000111);
                Led.setRow(ii,3,(byte) 0b00001111);
                Led.setRow(ii,2,(byte) 0b00001111);
                Led.setRow(ii,1,(byte) 0b00011110);
                Led.setRow(ii,0,(byte) 0b00011110);
                ii = 6;

                Led.setRow(ii,7,(byte) 0b00000000);
                Led.setRow(ii,6,(byte) 0b00000000);
                Led.setRow(ii,5,(byte) 0b10000000);
                Led.setRow(ii,4,(byte) 0b10000000);
                Led.setRow(ii,3,(byte) 0b11000000);
                Led.setRow(ii,2,(byte) 0b11000000);
                Led.setRow(ii,1,(byte) 0b11111110);
                Led.setRow(ii,0,(byte) 0b11111110);
                ii = 7;

                Led.setRow(ii,7,(byte) 0b00001111);
                Led.setRow(ii,6,(byte) 0b00001111);
                Led.setRow(ii,5,(byte) 0b00000111);
                Led.setRow(ii,4,(byte) 0b00000111);
                Led.setRow(ii,3,(byte) 0b00000011);
                Led.setRow(ii,2,(byte) 0b00000011);
                Led.setRow(ii,1,(byte) 0b01111111);
                Led.setRow(ii,0,(byte) 0b01111111);

            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API ", e);

         }*/





            // Step 5. Return true to keep callback active.
            return true;
        }
    };
    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.i(TAG,"START");

        ImageView immage = (ImageView) findViewById(R.id.foto);
        immage.setVisibility(View.INVISIBLE);


       /* new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                Log.d(TAG, "file");
                SmbFile[] domains;
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, null, null);
                try {
                    domains = (new SmbFile("smb://192.168.0.111/AndPubblica/", auth.ANONYMOUS)).listFiles();
                    for (int i = 0; i < domains.length; i++) {
                        System.out.println(domains[i]);
                        SmbFile[] servers = domains[i].listFiles();
                        for (int j = 0; j < servers.length; j++) {
                            System.out.println("\t"+servers[j]);
                            Log.d(TAG, "file"+"\t"+servers[j]);
                        }
                    }
                } catch (SmbException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });//.start();

*/

        Log.d(TAG, "tts");
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    Log.d(TAG, "tts ok");
                    t1.setLanguage(Locale.ITALIAN);
                    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                } else {Log.e(TAG, "tts ERR");

                }
            }
        });
        try {

            byte[] frame = numbers.num1;

            for (int row = 0; row < 32; row++) {
                int device = 0;
                int rowp = row;
                if (row > 7) {
                    device = 1;
                    rowp = row-8;

                }
                if (row > 15) {
                    device = 4;
                    rowp = row-16;
                }
                if (row > 23) {
                    device = 5;
                    rowp = row-24;
                }


                Led.setRow(device, rowp, frame[row]);
            }





        } catch (IOException e) {
            Log.e(TAG, "Error initializing LED matrix", e);
        }


        dataModel = MyDataModel.getInstance();
        dataModel.addObserver(this);
/*        try {

        byte[] frame = numbers.startAll;


            for (int row = 0; row < 64; row++) {
                int device = 0;
                int rowp = row;
                if (row > 7) {
                    device = 1;
                    rowp = row-8;

                }
                if (row > 15) {
                    device = 2;
                    rowp = row-16;
                }
                if (row > 23) {
                    device = 3;
                    rowp = row-24;
                }
                if (row > 31) {
                    device = 4;
                    rowp = row-32;

                }
                if (row > 39) {
                    device = 5;
                    rowp = row-40;
                }
                if (row > 47) {
                    device = 6;
                    rowp = row-48;
                }
                if (row > 55) {
                    device = 7;
                    rowp = row-56;
                }



                Led.setRow(device, rowp, frame[row]);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error initializing LED matrix", e);
        }
       // mBluetoothAdapter.startDiscovery();

*/

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Step 6. Close the resource
        if (mButtonGpio_next != null) {
            mButtonGpio_next.unregisterGpioCallback(mCallback);
            try {
                mButtonGpio_next.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
        if (mButtonGpio_print != null) {
            mButtonGpio_print.unregisterGpioCallback(mCallback);
            try {
                mButtonGpio_print.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
        unregisterReceiver(mReceiver);
    }


    @Override
    public void update(Observable observable, Object o) {
        Log.i(TAG,"Observer");
        MyDataModel dataModel=MyDataModel.getInstance();
        final List<String> lista = dataModel.getlista();

        if (lista.size()>0) {
            runOnUiThread(new Runnable() {
                public void run() {

                    TextView fbc = (TextView)findViewById(R.id.txtLike);
                    fbc.setText(fbCount);
                    ImageView fbI = (ImageView)findViewById(R.id.imageView3);
                    if (fbCount.length() == 0){
                        fbI.setVisibility(View.INVISIBLE);
                    } else fbI.setVisibility(View.VISIBLE);
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    //int height = size.y;

                    Log.i(TAG, intSetphoto + " GPIO changed, button pressed.size:" + lista.size());
                    if (intSetphoto > lista.size() - 1) {
                        intSetphoto = 0;
                        //aggiorno pure facebook
                    }

                    Log.i(TAG, "GPIO changed, button pressed.photo:" + intSetphoto);
                    Log.i(TAG, "lista" + getBaseContext().getCacheDir() + "/" + lista.get(intSetphoto));
                    final ImageView img = (ImageView) findViewById(R.id.foto);
                    img.setVisibility(View.VISIBLE);
                    final Bitmap bMap = BitmapFactory.decodeFile(getBaseContext().getCacheDir() + "/" + lista.get(intSetphoto));
                    //Animation animationL = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.card_flip_left_out);
                    //img.startAnimation(animationL);
                    //img.setImageBitmap(bMap);
                    width = (width/2)-(bMap.getWidth()/2);
                    ObjectAnimator animStage1 = new ObjectAnimator();//(ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipstage2);
                    final ObjectAnimator animStage2 = new ObjectAnimator();//(ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipstage1);
                    animStage1.setTarget(img);
                    //animation = new ObjectAnimator();
                    animStage1.setPropertyName("X");
                    animStage1.setFloatValues(width, 2000);
                    //animation.setRepeatMode(ValueAnimator.REVERSE);
                    //animation.setRepeatCount(ValueAnimator.INFINITE);
                    animStage1.setDuration(1000);
                    animStage1.start();
                    final int finalWidth = width;
                    animStage1.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animStage2.setFloatValues(-2000, finalWidth);
                            animStage2.setPropertyName("X");
                            animStage2.setDuration(1000);
                            animStage2.setTarget(img);
                            animStage2.start();
                            img.setImageBitmap(bMap);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });



                    intSetphoto++;
                }

            });
        }


    }



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbController.USB_CONNECTED:

                    break;
                default:
                    break;
            }
        }
    };
    public boolean CheckUsbPermission(){
        usbCtrl.close();
        int  i = 0;
        for( i = 0 ; i < 5 ; i++ ){
            dev = usbCtrl.getDev(u_infor[i][0],u_infor[i][1]);
            if(dev != null)
                break;
        }
        if( dev != null ){
            if( !(usbCtrl.isHasPermission(dev))){
                //Log.d("usb����","����USB�豸Ȩ��.");
                usbCtrl.getPermission(dev);
            }else{

            }
        }
        if( dev != null ){
            if( usbCtrl.isHasPermission(dev)){
                return true;
            }
        }

        return false;
    }

    private void printImage(String NomeFile) {
        byte[] sendData = null;
        //String path = getBaseContext().getCacheDir() ;
        //File fileD = new File(path);
        //fileD.mkdirs();

        PrintPic pg = new PrintPic();
        int i = 0,s = 0,j = 0,index = 0,lines = 0;
        pg.initCanvas(580);
        pg.initPaint();
        pg.drawImage(0,0, Environment.getExternalStorageDirectory()+ "/"+NomeFile+".png");
        sendData = pg.printDraw();
        byte[] temp = new byte[(pg.getWidth() / 8)*5];
        byte[] dHeader = new byte[8];
        if(pg.getLength()!=0){
            dHeader[0] = 0x1D;
            dHeader[1] = 0x76;
            dHeader[2] = 0x30;
            dHeader[3] = 0x00;
            dHeader[4] = (byte)(pg.getWidth()/8);
            dHeader[5] = 0x00;
            dHeader[6] = (byte)(pg.getLength()%256);
            dHeader[7] = (byte)(pg.getLength()/256);
            usbCtrl.sendByte(dHeader,dev);
            for( i = 0 ; i < (pg.getLength()/5)+1 ; i++ ){         //ÿ��5�з���һ��ͼƬ����
                s = 0;
                if( i < pg.getLength()/5 ){
                    lines = 5;
                }else{
                    lines = pg.getLength()%5;
                }
                for( j = 0 ; j < lines*(pg.getWidth() / 8) ; j++ ){
                    temp[s++] = sendData[index++];
                }
                usbCtrl.sendByte(temp,dev);
                try {
                    Thread.sleep(1);                              //ÿ��һ����ʱ60����
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for(j = 0 ; j <(pg.getWidth()/8)*5 ; j++ ){         //����������
                    temp[j] = 0;
                }
            }
        }
    }
}

/*
                 //Android Design
                int ii = 0;
                Led72xx.setRow(ii,7,(byte) 0b01000010);
                Led72xx.setRow(ii,6,(byte) 0b00100100);
                Led72xx.setRow(ii,5,(byte) 0b01111110);
                Led72xx.setRow(ii,4,(byte) 0b01011010);
                Led72xx.setRow(ii,3,(byte) 0b01111110);
                Led72xx.setRow(ii,2,(byte) 0b00000000);
                Led72xx.setRow(ii,1,(byte) 0b01111110);
                Led72xx.setRow(ii,0,(byte) 0b00000000);
 */