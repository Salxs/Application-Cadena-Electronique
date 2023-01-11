package fr.shams.cadenaelectronique.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import fr.shams.cadenaelectronique.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    //Déclaration d'une liste contenant les boutons
    private List<Button> mActionButton;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private BluetoothDevice mDevice;

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
            sendData("gps");
        }

        else{
            try {
                sendData("déconnexion");
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
}