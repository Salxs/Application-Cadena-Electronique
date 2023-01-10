package fr.shams.cadenaelectronique.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import fr.shams.cadenaelectronique.R;

public class MainActivity extends AppCompatActivity {


    //Déclaration des attributs pour les widgets
    private CheckBox enable_bt, visible_bt;
    private ImageView search_bt;
    private TextView name_bt;
    private Button mButton, mButtonConnexion, mButtonLeave;
    private BluetoothDevice mDevice;


    //Attributs utilisés pour la méthode RegisterForActivityResult
    ActivityResultLauncher<Intent> mIntent;
    ActivityResultLauncher<Intent> mIntentVisible;

    //Déclaration des attributs Privée
    private BluetoothAdapter BA;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Affectation des widgets au différentes variables
        enable_bt = findViewById(R.id.enable_bt);
        visible_bt = findViewById(R.id.visible_bt);
        search_bt = findViewById(R.id.search_bt);
        name_bt = findViewById(R.id.name_bt);
        mButton = findViewById(R.id.button_bt);
        mButtonConnexion = findViewById(R.id.connection_bt);
        mButtonLeave = findViewById(R.id.leave_btn);

        //Partie s'occupant de paramétrer la connexion Bluetooth
        BA = BluetoothAdapter.getDefaultAdapter();
        if (BA == null) {
            Toast.makeText(this, getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT).show();
            finish();
        }
        if (BA.isEnabled()) {
            enable_bt.setChecked(true);
        }
        mButtonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for(BluetoothDevice bt : pairedDevices){
                        if(bt.getName().equals(getString(R.string.name_bt))){
                            mDevice = bt;
                            Toast.makeText(MainActivity.this, getString(R.string.connection_msg), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
        });

        //Portion de code ayant pour rôle de contrôler l'interface Bluetooth
        enable_bt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                BA.disable();
                Toast.makeText(MainActivity.this, getString(R.string.eteint), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mIntent.launch(intentOn);
            }
        });
        mIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 0) {
                        Toast.makeText(MainActivity.this, getString(R.string.allumer), Toast.LENGTH_SHORT).show();
                    }
                });

        visible_bt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                mIntentVisible.launch(getVisible);
            }
        });
        mIntentVisible = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 0) {
                        Toast.makeText(MainActivity.this, getString(R.string.visible), Toast.LENGTH_SHORT).show();
                    }
                });
        //Portion de code permettant l'éxécution de l'activité suivante à l'aide d'un bouton
        mButton.setOnClickListener(view -> {
            Intent menuActivity = new Intent(MainActivity.this, MenuActivity.class );
            menuActivity.putExtra("bluetooth_device", mDevice);
            startActivity(menuActivity);
        });

        //Contrôle du bouton pour quitter l'utilisation
        mButtonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}