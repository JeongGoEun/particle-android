package io.particle.android.sdk;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.Async;
import io.particle.sdk.app.R;

public class MainActivity extends AppCompatActivity {

    //view
    public ImageView []room;
    public Button reBtn4, reBtn5;
    public TextView txtV4, txtV5;

    public Boolean isAdd4, isAdd5;
    public String name4, name5;
    public static  ArrayList<Device>[] devices;
    public static ParticleDevice meshGateway;

    //sp
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smarthome_main);
        ParticleDeviceSetupLibrary.init(this.getApplicationContext());

        ActionBar bar = getSupportActionBar();
        bar.hide();

        //particleInit();
        setting();
        addData();
    }

    public void particleInit() {
        Log.d("log1","startparticle");
        ParticleCloudSDK.init(this);
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            private List<ParticleDevice> particleDevices;

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                Log.d("log1","startparticle1");

                try {
                    List<ParticleDevice> particleDevices;
                    JSONObject json = new JSONObject(loadJSONFromAsset());
                    Log.e("log1",json.getString("cloudEmail"));
                    Log.e("log1",json.getString("cloudPassword"));
                    particleCloud.logIn(json.getString("cloudEmail"), json.getString("cloudPassword"));

                    Calendar distantFuture = Calendar.getInstance();
                    distantFuture.add(Calendar.YEAR, 20);
                    particleCloud.setAccessToken(json.getString("cloudAccessToken"), distantFuture.getTime());

                    particleDevices = particleCloud.getDevices();
                    Log.e("log1",particleDevices.size()+"");

                    for (ParticleDevice device : particleDevices) {
                        //리스트 저장
                        int roomnum=Integer.parseInt(device.getStringVariable("roomNum"));
                        String name=device.getStringVariable("name");
                        int type=Integer.parseInt(device.getStringVariable("type"));
                        String state=device.getStringVariable("state");

                        if(name.equals("meshGateway")) {
                            meshGateway = device;
                        }

                        Device d=new Device();

                        //클라우드에서 가져오기
                        d.setDeviceRoom(roomnum,9);
                        d.setDeviceRoomNum(roomnum);
                        d.setDeviceState(state);
                        d.setDeviceName(name);
                        d.setDeviceType(type);

                        devices[roomnum].add(d);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("log1",e.toString());
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    e.printStackTrace();
                }

                return -1;

            }

            @Override
            public void onSuccess(@NonNull Object value) {
                Log.e("log1", "login success");
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                Log.d("log1", e.getBestMessage());
            }
        });
    }


    public void setting(){
        //view
        room = new ImageView[6];
        room[0] = findViewById(R.id.room0);
        room[1] = findViewById(R.id.room1);
        room[2] = findViewById(R.id.room2);
        room[3] = findViewById(R.id.room3);
        room[4] = findViewById(R.id.room4);
        room[5] = findViewById(R.id.room5);

        reBtn4 = findViewById(R.id.resetBtn1);
        reBtn5 = findViewById(R.id.resetBtn2);
        txtV4 = findViewById(R.id.txtv4);
        txtV5 = findViewById(R.id.txtv5);

        ///is add personalize room4,room5?
        pref = getSharedPreferences("IsAdd",MODE_PRIVATE);
        editor = pref.edit();
        isAdd4 = pref.getBoolean("IsAdd4",false);
        isAdd5 = pref.getBoolean("IsAdd5",false);
        name4 = pref.getString("Name4","");
        name5 = pref.getString("Name5","");

        txtV4.setText(name4);
        txtV5.setText(name5);

        if(isAdd4 == false) {
            room[4].setImageResource(R.drawable.plus);
            reBtn4.setEnabled(isAdd4);
            reBtn4.setVisibility(View.GONE);
        }
        else {
            room[4].setImageResource(R.drawable.person);
        }

        if(isAdd5 == false) {
            room[5].setImageResource(R.drawable.plus);
            reBtn5.setEnabled(isAdd5);
            reBtn5.setVisibility(View.GONE);
        }
        else {
            room[5].setImageResource(R.drawable.person);
        }

        //device
        devices = new ArrayList[6];
        for(int i=0; i<6; i++){
            devices[i] = new ArrayList<Device>();
        }
        //test
        //addData();
    }

    public void addData(){
        Device[] dd = new Device[10];
        for(int i=0; i<2; i++){
            dd[i] = new Device();
            dd[i].setDeviceRoom(0,0);
            dd[i].setDeviceName("led"+i);
            dd[i].setDeviceType(0);
            devices[0].add(dd[i]);
        }
        for(int i=2; i<4; i++){
            dd[i] = new Device();
            dd[i].setDeviceRoom(0,1);
            dd[i].setDeviceName("led"+i);
            dd[i].setDeviceType(0);
            devices[1].add(dd[i]);
        }
        for(int i=4; i<7; i++){
            dd[i] = new Device();
            dd[i].setDeviceRoom(0,2);
            dd[i].setDeviceName("led"+i);
            dd[i].setDeviceType(0);
            devices[2].add(dd[i]);
        }
        for(int i=7; i<10; i++){
            dd[i] = new Device();
            dd[i].setDeviceRoom(0,3);
            dd[i].setDeviceName("led"+i);
            dd[i].setDeviceType(0);
            devices[3].add(dd[i]);
        }
    }
    ///---------- add room button ------------///
    public void onClickRoom(View v){
        int roomNum = -1;
        Intent intent;
        for(int i=0; i<6; i++){
            if(v == room[i]){
                roomNum = i;
                break;
            }
        }

        //start addActivity
        if(roomNum == 4 && !isAdd4){
            intent = new Intent(this, AddActivity.class);
            //intent.putExtra("DEVICE",devices);
            //startActivityForResult(intent,4);
            //intent.putExtra("DEVICE",devices);
            intent.putExtra("ROOM_NUM",roomNum);
            startActivityForResult(intent,4);
            return;
        }
        if(roomNum == 5 && !isAdd5) {
            intent = new Intent(this, AddActivity.class);
            intent.putExtra("ROOM_NUM",roomNum);
            startActivityForResult(intent,5);
            return;
        }

        //start roomActivity
        String []roomName  = {"Living Room","BathRoom","Bedroom","Study Room",name4,name5};
        int [] roomImg = {R.drawable.livingroom, R.drawable.bathroom, R.drawable.bedroom, R.drawable.studyroom,R.drawable.person,R.drawable.person};
        intent = new Intent(this, RoomActivity.class);
        intent.putExtra("NAME",roomName[roomNum]);
        //intent.putExtra("DEVICE",devices[roomNum]);
        intent.putExtra("ROOM_NUM",roomNum);
        intent.putExtra("IMG",roomImg[roomNum]);
        startActivity(intent);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("infoConfig.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    ///----------- room reset butoon ------------------///
    public void onClickRe(View v){
        int roomNum=0;
        switch (v.getId()){
            case R.id.resetBtn1:{
                roomNum = 4;
                break;
            }
            case R.id.resetBtn2:{
                roomNum = 5;
                break;
            }
        }

        // check dialog //
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_check);
        Button btnOK = (Button)dialog.findViewById(R.id.check_OKbtn);
        Button btnCancel = (Button)dialog.findViewById(R.id.check_CancelBtn);

        final int finalRoomNum = roomNum;
        btnOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reset(finalRoomNum);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    public void reset(int roomNum){
        // room4 reset //
        if(roomNum == 4){
            isAdd4 = false;
            reBtn4.setEnabled(isAdd4);
            reBtn4.setVisibility(View.GONE);
            name4 = "";
            txtV4.setText(name4);
            editor.putString("Name4",name4);
            editor.putBoolean("IsAdd4",isAdd4);
            room[4].setImageResource(R.drawable.plus);
        }
        // room5 reset //
        else if(roomNum == 5){
            isAdd5 = false;
            reBtn5.setEnabled(isAdd5);
            reBtn5.setVisibility(View.GONE);
            name5 = "";
            txtV5.setText(name5);
            editor.putString("Name5",name5);
            editor.putBoolean("IsAdd5",isAdd5);
            room[5].setImageResource(R.drawable.plus);
        }
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode != RESULT_OK) return;

       // devices[requestCode] = (ArrayList<Device>) intent.getSerializableExtra("AddDevice");
        room[requestCode].setImageResource(R.drawable.person);

        // room4 set //
        if(requestCode == 4){
            isAdd4 = true;
            editor.putBoolean("IsAdd4",isAdd4);
            reBtn4.setEnabled(isAdd4);
            reBtn4.setVisibility(View.VISIBLE);
            name4 = intent.getStringExtra("AddName");
            txtV4.setText(name4);
            editor.putString("Name4",name4);
        }
        // room5 set //
        else if(requestCode == 5){
            isAdd5 = true;
            editor.putBoolean("IsAdd5",isAdd5);
            reBtn5.setEnabled(isAdd5);
            reBtn5.setVisibility(View.VISIBLE);
            name5 = intent.getStringExtra("AddName");
            txtV5.setText(name5);
            editor.putString("Name5",name5);
        }
        editor.commit();
    }


    public void plusDevice(View v)
    {
        Intent intent = new Intent(this, AddDeviceActivity.class);
        startActivity(intent);
    }
}

