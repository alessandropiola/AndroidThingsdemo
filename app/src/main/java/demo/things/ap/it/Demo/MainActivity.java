package demo.things.ap.it.Demo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.things.contrib.driver.ssd1306.Ssd1306;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import demo.things.ap.it.Demo.Led72xx;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final String BUTTON_PIN_NAME = "BCM21"; // GPIO port wired to the button

    private Gpio mButtonGpio;
    TextView testobtn1;

    Led72xx Led72xx;



// Access the display:

    private Ssd1306 mScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testobtn1 = (TextView) findViewById(R.id.textView);


        PeripheralManagerService service = new PeripheralManagerService();
        Log.d(TAG, "Available GPIO: " + service.getGpioList());
        Log.d(TAG, "Available GPIO: " + service.getI2cBusList());
        Log.d(TAG, "Available GPIO: " + service.getI2sDeviceList());
        Log.d(TAG, "Available GPIO: " + service.getSpiBusList());




        testobtn1.setText("GPIO Disponibili " + service.getGpioList().toString());


        try {
            Led72xx = new Led72xx("SPI0.0", 8);
            for (int i = 0; i < Led72xx.getDeviceCount(); i++) {
                Led72xx.setIntensity(i, 13);
                Led72xx.shutdown(i, false);
                Led72xx.clearDisplay(i);
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

    }

    // Step 4. Register an event callback.
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO changed, button pressed.");
            testobtn1.setText("pulsante Premuto...");


            // Step 5. Return true to keep callback active.
            return true;
        }
    };
    @Override
    protected void onStart(){
        super.onStart();

        try {
       /*     int ii = 0;
            Led72xx.setRow(ii,7,(byte) 0b10000000);
            Led72xx.setRow(ii,6,(byte) 0b11000000);
            Led72xx.setRow(ii,5,(byte) 0b11100000);
            Led72xx.setRow(ii,4,(byte) 0b11110000);
            Led72xx.setRow(ii,3,(byte) 0b11111000);
            Led72xx.setRow(ii,2,(byte) 0b11000000);
            Led72xx.setRow(ii,1,(byte) 0b11000000);
            Led72xx.setRow(ii,0,(byte) 0b11000000);

            ii = 1;
            Led72xx.setRow(ii,7,(byte) 0b00000011);
            Led72xx.setRow(ii,6,(byte) 0b00000011);
            Led72xx.setRow(ii,5,(byte) 0b00000011);
            Led72xx.setRow(ii,4,(byte) 0b00000011);
            Led72xx.setRow(ii,3,(byte) 0b00000011);
            Led72xx.setRow(ii,2,(byte) 0b00000011);
            Led72xx.setRow(ii,1,(byte) 0b00000011);
            Led72xx.setRow(ii,0,(byte) 0b00000011);

            ii = 4;
            Led72xx.setRow(ii,7,(byte) 0b11000000);
            Led72xx.setRow(ii,6,(byte) 0b11000000);
            Led72xx.setRow(ii,5,(byte) 0b11000000);
            Led72xx.setRow(ii,4,(byte) 0b11000000);
            Led72xx.setRow(ii,3,(byte) 0b11000000);
            Led72xx.setRow(ii,2,(byte) 0b11000000);
            Led72xx.setRow(ii,1,(byte) 0b11111100);
            Led72xx.setRow(ii,0,(byte) 0b11111100);

            ii = 5;
            Led72xx.setRow(ii,7,(byte) 0b00000011);
            Led72xx.setRow(ii,6,(byte) 0b00000011);
            Led72xx.setRow(ii,5,(byte) 0b00000011);
            Led72xx.setRow(ii,4,(byte) 0b00000011);
            Led72xx.setRow(ii,3,(byte) 0b00000011);
            Led72xx.setRow(ii,2,(byte) 0b00000011);
            Led72xx.setRow(ii,1,(byte) 0b00111111);
            Led72xx.setRow(ii,0,(byte) 0b00111111);*/

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


            Led72xx.setRow(device, rowp, frame[row]);
        }

            //Android Design
            int ii = 7;
            Led72xx.setRow(ii,7,(byte) 0b01000010);
            Led72xx.setRow(ii,6,(byte) 0b00100100);
            Led72xx.setRow(ii,5,(byte) 0b01111110);
            Led72xx.setRow(ii,4,(byte) 0b01011010);
            Led72xx.setRow(ii,3,(byte) 0b01111110);
            Led72xx.setRow(ii,2,(byte) 0b00000000);
            Led72xx.setRow(ii,1,(byte) 0b01111110);
            Led72xx.setRow(ii,0,(byte) 0b00000000);



            } catch (IOException e) {
            Log.e(TAG, "Error initializing LED matrix", e);
        }

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