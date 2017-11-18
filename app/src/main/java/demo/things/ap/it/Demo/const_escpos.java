package demo.things.ap.it.Demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alessandro on 19/12/2016.
 */

public class const_escpos {
    public static final byte[] stampaecambioliena = new byte[1];
    public static final byte[] init1 = new byte[2];
    public static final byte[] selezionatipostampa = new byte[3];
    public static final byte[] tagliocarta = new byte[4];
    public static final byte[] barre = new byte[8];
    public static final byte[] barre1 = new byte[4];


    public static byte[] getStampaecambioliena() {
        stampaecambioliena[0] = 0x0A;
        return stampaecambioliena;
    }
    public static byte[] getTab() {
        stampaecambioliena[0] = 0x09;
        return stampaecambioliena;
    }

    public static byte[] getselezionatipostampaFontA() {
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x21;
        selezionatipostampa[2] = 0x00;
        return selezionatipostampa;
    }
    public static byte[] getselezionatipostampaFontB() {
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x21;
        selezionatipostampa[2] = 0x01;
        return selezionatipostampa;
    }
    public static byte[] getselezionatipostampaEnfatizzato() {
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x21;
        selezionatipostampa[2] = 0x08;
        return selezionatipostampa;
    }
    public static byte[] getselezionatipostampaDoppiaAltezza() {
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x21;
        selezionatipostampa[2] = 0x10;
        return selezionatipostampa;
    }
    public static byte[] getselezionatipostampaDoppiaLunghezza() {
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x21;
        selezionatipostampa[2] = 0x20;
        return selezionatipostampa;
    }
    public static byte[] getselezionatipostampaSottolineato() {
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x21;
        selezionatipostampa[2] = (byte) 0x80;
        return selezionatipostampa;
    }
    public static byte[] getTaglioCarta() {
        //tagliocarta[0] = 0x1B;//inizializza printer
        //tagliocarta[1] = 0x40;//inizializza printer
        tagliocarta[0] = 0x1D;//cut
        tagliocarta[1] = 0x56;//cut
        tagliocarta[2] = 0x42;//0x30 taglio totale
        tagliocarta[3] = 0x00;//0x30 taglio totale
        return tagliocarta;
    }
    public static byte[] getRiavvolgo() {
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x65;
        selezionatipostampa[2] = 0x20;
        return selezionatipostampa;
    }
    public static byte[] getInit() {
        init1[0] = 0x1B;
        init1[1] = 0x40;
        return init1;
    }
    public static byte[] getLineSpace() {//non va
        selezionatipostampa[0] = 0x1B;
        selezionatipostampa[1] = 0x33;
        selezionatipostampa[2] = (byte) 0xC8;
        return selezionatipostampa;
    }


    public static byte[] getInitStampoBarre() {


        barre[0] = 0x1B;//inizializza printer
        barre[1] = 0x40;//inizializza printer

        barre[2] = 0x1D;//altezza
        barre[3] = 0x68;
        barre[4] = 0x35;

        barre[5] = 0x1D;//non stampo il codice in chiaro
        barre[6] = 0x48;//non stampo il codice in chiaro
        barre[7] = 0x0;

        return barre;
    }
    public static byte[] getBarreStampoBarre() {


        barre1[0] = 0x1D;
        barre1[1] = 0x6B;
        barre1[2] = 0x43;
        barre1[3] = 0xd;

        return barre1;
    }

    public static Integer getFrontNormale(String Desc, String Prezzo, String Codice, String KgLt) {
        int w = 580, h = 100;
        Paint pText = new Paint();
        pText.setColor(Color.BLACK);
        pText.setTextSize(50); // set font size
        Typeface currentTypeFace =   pText.getTypeface();
        Typeface bold = Typeface.create(currentTypeFace, Typeface.BOLD);
        Paint pText3 = new Paint();
        pText3.setColor(Color.BLACK);
        pText3.setTextSize(40); // set font size
        pText3.setTypeface(bold);

        Paint pText2 = new Paint();
        pText2.setColor(Color.BLACK);
        pText2.setTextSize(20); // set font size

        pText.setTypeface(bold);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);
        canvas.drawText(Desc,1,50,pText3);
        canvas.drawText(Prezzo,20,99,pText);
        canvas.drawText(Codice,400,95,pText2);
        canvas.drawText(KgLt,400,75,pText2);

        // Save Bitmap to File
        FileOutputStream fos = null;
        try
        {


            fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/"+"oriz.png");
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
            fos = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                    fos = null;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return 1;


    }

    public static Integer getFrontVert(String Desc, String PrezzoN, String PrezzoO, String DataScad, String KgLt, String grassetto) {
        int h = 680, w = 580;
        Paint pTextDe = new Paint();
        pTextDe.setColor(Color.BLACK);
        pTextDe.setTextSize(50); // set font siz
        Typeface currentTypeFace =   pTextDe.getTypeface();
        Typeface bold = Typeface.create(currentTypeFace, Typeface.BOLD);
        Paint pTextPrzO = new Paint();
        pTextPrzO.setColor(Color.BLACK);
        pTextPrzO.setTextSize(190); // set font size
        pTextPrzO.setTypeface(bold);
        Paint pTextPrzN = new Paint();
        pTextPrzN.setColor(Color.BLACK);
        pTextPrzN.setTextSize(90); // set font size
        pTextPrzN.setTypeface(bold);

        Paint pText2 = new Paint();
        pText2.setColor(Color.BLACK);
        pText2.setTextSize(20); // set font size

        pTextDe.setTypeface(bold);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);
        canvas.save();
        int dx = -50;
        int dy = 100;
        canvas.rotate(90f,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawText(KgLt,dx+10,dy+520,pText2);
        canvas.drawText(DataScad,dx+400,dy+520 ,pText2);
        canvas.drawText(Desc.substring( 0,20).trim(),dx+5,dy+30,pTextDe);//DESC1
        canvas.drawText(Desc.substring(21,40).trim(),dx+5,dy+75,pTextDe);//Desc2
        canvas.drawText(Desc.substring(41,Desc.length()).trim(),dx+5,dy+120,pTextDe);//Desc2
        int xPrezzN = 20;
        int yPrezzN = 230;
        //canvas.drawText(PrezzoN,dx+xPrezzN,dy+yPrezzN,pTextPrzN);//PREZZO NORMALE
        //canvas.drawLine(dx+xPrezzN,dy+yPrezzN,dx+xPrezzN+230,dy+yPrezzN-72,pTextPrzN);
        //canvas.drawLine(dx+xPrezzN,dy+yPrezzN-72,dx+xPrezzN+230,dy+yPrezzN,pTextPrzN);

        canvas.drawText(PrezzoO,dx+10,dy+430,pTextPrzO);//PREZZO OFF


        Paint mpaint= new Paint();
        mpaint.setColor(Color.RED);
        mpaint.setStyle(Paint.Style.FILL);
        Paint mTextW= new Paint();
        mTextW.setColor(Color.WHITE);
        mTextW.setTextSize(90);  //set text size


        canvas.drawRect(dx+xPrezzN+270, dy+yPrezzN+18,dx+xPrezzN+400+250, dy+yPrezzN-90, mpaint);
        canvas.drawText(grassetto, dx+xPrezzN+270, dy+yPrezzN, mTextW);


        canvas.restore();
        // Save Bitmap to File
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/"+"vert.png");
            Log.i("SSS", Environment.getExternalStorageDirectory() + "/"+"vert.png");

            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
            fos = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                    fos = null;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return 1;


    }



}
