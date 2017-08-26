package demo.things.ap.it.Demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final String BUTTON_PIN_NAME = "BCM21"; // GPIO port wired to the button

    private Gpio mButtonGpio;
    TextView testobtn1;

    Led72xx Led;

    TextToSpeech t1;

    FirebaseStorage storage ;
    StorageReference storageRef ;

// Access the display:

    private BluetoothAdapter mBluetoothAdapter;
    private int intY = 0;
    private int intX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        testobtn1 = (TextView) findViewById(R.id.textView);

        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        PeripheralManagerService service = new PeripheralManagerService();

// Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DataIOT");

        myRef.setValue("Hello, World!");
        myRef.child("PHOTO").child("0").setValue("01.jpg");
        myRef.child("PHOTO").child("1").setValue("02.jpg");
        myRef.child("PHOTO").child("2").setValue("03.jpg");
        myRef.child("PHOTO").child("3").setValue("04.jpg");


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                t1.speak("Valore Database Aggiornato supercalifragilistichespiralidoso", TextToSpeech.QUEUE_FLUSH, null,null);
                Log.w(TAG, "mod.");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });





        testobtn1.setText("GPIO Disponibili " + service.getGpioList().toString());

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
            mButtonGpio = service.openGpio(BUTTON_PIN_NAME);
            // Step 2. Configure as an input.
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            // Step 3. Enable edge trigger events.
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            // Step 4. Register an event callback.
            mButtonGpio.registerGpioCallback(mCallback);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API ", e);

        }




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
    // Step 4. Register an event callback.
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO changed, button pressed.");
            testobtn1.setText("pulsante Premuto...");
            t1.speak("Tasto premuto", TextToSpeech.QUEUE_FLUSH, null,null);

            try {

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

         }


            final StorageReference islandRef = storageRef.child("images/11.jpg");

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }

            final File finalLocalFile = localFile;
            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    ImageView click = (ImageView)findViewById(R.id.imageView2);
                    Bitmap image = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());


                    click.setImageBitmap(image);
                    Log.i(TAG, "dwn ok"+islandRef.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e(TAG, "dwn err");
                }
            });


            // Step 5. Return true to keep callback active.
            return true;
        }
    };
    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "tts");
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    Log.d(TAG, "tts ok");
                    t1.setLanguage(Locale.ITALIAN);
                } else {Log.e(TAG, "tts ERR");

                }
            }
        });
        /*try {

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
        }*/
        try {

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

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Step 6. Close the resource
        if (mButtonGpio != null) {
            mButtonGpio.unregisterGpioCallback(mCallback);
            try {
                mButtonGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
        unregisterReceiver(mReceiver);
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