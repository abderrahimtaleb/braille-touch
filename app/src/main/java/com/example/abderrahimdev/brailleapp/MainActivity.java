package com.example.abderrahimdev.brailleapp;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.support.v7.app.ActionBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    LinearLayout pere = null;
    TableLayout left = null;
    TableLayout right = null;
    ArrayList<String> select = new ArrayList<>();
    HashMap<String,String> correspondances = new HashMap<>();
    TextView lettre = null;
    TextView TxtVphrase = null;
    Vibrator vibr = null;
    String phrase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pere =  findViewById(R.id.pere);
        left =  findViewById(R.id.left);
        right =  findViewById(R.id.right);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        lettre = findViewById(R.id.lettre);
        TxtVphrase = findViewById(R.id.phrase);


        String[] tabBraille = {"l1","l1l2","l1r1","l1r1r2","l1r2","l1l2r1","l1l2r1r2","l1l2r2","l2r1","r1r2l2","l1l3","l1l2l3","l1r1l3","l1r1r2l3","l1r2l3","l1l2l3r1","l1l2l3r1r2","l1l2l3r2","r1l2l3","r1r2l2l3","l1l3r3","l1l2l3r3","l2r1r2r3","l1r1l3r3","l1l3r1r2r3","l1l3r2r3"};
        int i = 0;
        for(char l = 'A'; l <= 'Z' && i < tabBraille.length ; l++ , i++)
        {
            correspondances.put(tabBraille[i],String.valueOf(l));
        }
        vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    String id = "";
    MyTimer tmr = null;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getActionMasked() != MotionEvent.ACTION_UP)
            return false;
        float x = event.getX();
        float y = event.getY();
        float colHeight = left.getChildAt(0).getHeight();
        TableLayout current = null;
        if(x >= 0 && x < left.getWidth())
        {
            current = left;
            id = "l";
        }
        else if( x >= pere.getWidth() - right.getWidth() && x < pere.getWidth())
        {
            current = right;
            id = "r";
        }



        for (int i = 0 ; i < current.getChildCount(); i++)
        {
            TableRow curTableRw = (TableRow) current.getChildAt(i);
            if(y >= curTableRw.getY() && y < curTableRw.getY() + colHeight)
            {
                curTableRw.setBackgroundColor(Color.BLACK);
                id += i+1;
                
                if(!select.contains(id))
                    select.add(id);

                if(tmr!=null)
                {
                    tmr.interrupt();
                    tmr = null;
                }

                tmr = new MyTimer();
                tmr.start();

            }

        }
        vibr.vibrate(200);
        return super.onTouchEvent(event);
    }

    class MyTimer extends Thread
    {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AfficheRes();
                    }
                });
            }
            catch (Exception ex){}

        }
    }


    public void AfficheRes()
    {
        Boolean isValide = false;
        for ( String key : correspondances.keySet() ) {
            if(key.length() == select.size()*2)
            {
                boolean estContient = true;
                for (String sel : select)
                {
                    if(!key.contains(sel))
                    {
                        estContient = false;
                        break;
                    }
                }

                if(estContient)
                {
                    lettre.setText(correspondances.get(key));
                    vibr.vibrate(1000);
                    phrase += correspondances.get(key);
                    TxtVphrase.setText(phrase);
                    init();
                    isValide = true;
                    break;
                }

            }
        }
        // si la correspondance n est pas trouvé
        if(!isValide)
        {
            Toast.makeText(this, "Correspondance invalide ! ",Toast.LENGTH_SHORT).show();
            init();
            lettre.setText("");
            vibr.vibrate(200);
        }
    }

    public void init()
    {
        select.clear();

        for (int i = 0 ; i < right.getChildCount(); i++)
        {
            TableRow rightRw = (TableRow) right.getChildAt(i);
            TableRow leftRw = (TableRow) left.getChildAt(i);

            rightRw.setBackgroundColor(Color.WHITE);
            leftRw.setBackgroundColor(Color.WHITE);
        }
    }
}