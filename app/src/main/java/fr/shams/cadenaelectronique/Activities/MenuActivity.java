package fr.shams.cadenaelectronique.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import fr.shams.cadenaelectronique.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    //Déclaration d'une liste contenant les boutons
    private List<Button> mActionButton;
    //Déclaration des attributs de la classe
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private BluetoothDevice mDevice;
    private volatile boolean stopWorker;
    private Thread workerThread;
    int readBufferPosition;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Affectation des widgets aux boutons de la classe
        mActionButton = Arrays.asList(
                findViewById(R.id.verrouillage_button),
                findViewById(R.id.enregistrement_button),
                findViewById(R.id.gps_button),
                findViewById(R.id.bouton_retour)
        );

        mActionButton.get(0).setOnClickListener(this);
        mActionButton.get(1).setOnClickListener(this);
        mActionButton.get(2).setOnClickListener(this);
        mActionButton.get(3).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDevice = (BluetoothDevice) extras.get("bluetooth_device");
            // utiliser l'objet device pour continuer la communication
        }

        UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try{
            mSocket = mDevice.createRfcommSocketToServiceRecord(my_uuid);
            mSocket.connect();
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            sendData("connecter");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        int index = mActionButton.indexOf((Button) view);

        //Portion de code permettant la gestion de l'action suite au clique sur le bouton verrouillage
        if(index == 0 ){
            sendData("verrouille");
            Toast.makeText(this, getString(R.string.demande_verrouillage), Toast.LENGTH_SHORT).show();
        }
        //Portion de code permettant la gestion de l'action suite au clique sur le bouton enregistrement de badge
        else if(index == 1){
            sendData("badge");
            Toast.makeText(this, getString(R.string.demande_enregistrement_badge), Toast.LENGTH_SHORT).show();
        }
        //Portion de code permettant la gestion de l'action suite au clique sur le bouton coordonnées GPS
        else if(index == 2){
            try {
                sendData("gps");
                receptionData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else{
            try {
                sendData("deconnexion");
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    public void sendData(String message){
        try {
            mOutputStream.write(message.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receptionData() throws IOException {
        final Handler handler = new Handler();
        final int delimiter = 10;

        stopWorker = false;
        readBufferPosition = 0;
        byte[] readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!workerThread.currentThread().isInterrupted() && !stopWorker){
                    try {
                        int byteAvailable = mInputStream.available();
                        if(byteAvailable > 0){
                            byte[] packetBytes = new byte[byteAvailable];
                            mInputStream.read(packetBytes);
                            for(int i =0; i < byteAvailable; i++){
                                byte b = packetBytes[i];
                                Log.i("data", String.valueOf(b));
                                if(b == delimiter){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes,0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    Log.i("data",data);
                                    readBufferPosition = 0;
                                    i = byteAvailable;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MenuActivity.this,data, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    } catch (IOException e) {
                        stopWorker = true;
                    }
                }
            }
        });
    }
}