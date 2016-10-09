package com.example.nikumbh.fts;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    static String IP;
    static String name;
    static Socket sock;
    static DataInputStream dis;
    static DataOutputStream dos;
    LocationManager locationManager;
    static EditText et1, et2;
    static Button btn1, button2;
    static TextView tv1, tv2;
    static boolean flag = false;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(btnListener1);

        button2 = (Button) findViewById(R.id.button2);
        button2.setVisibility(View.INVISIBLE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&


                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }



        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListen);
      //  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListen);

    }



    View.OnClickListener btnListener1=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IP=et1.getText().toString();
            name=et2.getText().toString();
            t1.start();
        }
    };

    LocationListener locListen=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("hello","Location changed!!");
            final double lat=location.getLatitude();
            final double lon=location.getLongitude();
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp = "https://www.google.com/maps/preview/@"+ lat + "," + lon + ",14z";
                    Uri uri = Uri.parse(temp); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            button2.setVisibility(View.VISIBLE);
            tv2.setText("Current Location: Latitude - "+lat+", Longitude - "+ lon);
            String temp=name+"'s location - "+"Latitude:"+lat+", Longitude:"+lon;
            try {
                if(flag==true) {
                    Log.i("err", temp);
                    dos.writeUTF(temp);
                    dos.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("err",e.toString());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    Thread t1=new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                sock=new Socket(IP,12001);
                dis=new DataInputStream(sock.getInputStream());
                dos=new DataOutputStream(sock.getOutputStream());
                flag=true;
                t2.start();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("err",e.toString());
            }
        }
    });


    Thread t2=new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    final String rec=dis.readUTF();
                    Log.i("err", "Received - " + rec);
                    //tv1.append(rec);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv1.append("\n"+rec);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("err",e.toString());
                }
            }
        }
    });
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
