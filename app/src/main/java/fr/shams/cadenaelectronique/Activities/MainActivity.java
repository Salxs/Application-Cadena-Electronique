package fr.shams.cadenaelectronique.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.Set;


import fr.shams.cadenaelectronique.R;

public class MainActivity extends AppCompatActivity {

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
        //Déclaration des attributs pour les widgets
        CheckBox enable_bt = findViewById(R.id.enable_bt);
        CheckBox visible_bt = findViewById(R.id.visible_bt);
        Button button = findViewById(R.id.button_bt);
        Button buttonConnexion = findViewById(R.id.connection_bt);
        Button buttonLeave = findViewById(R.id.leave_btn);

        //Partie s'occupant de paramétrer la connexion Bluetooth
        BA = BluetoothAdapter.getDefaultAdapter();
        if (BA == null) {
            Toast.makeText(this, getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT).show();
            finish();
        }
        if (BA.isEnabled()) {
            enable_bt.setChecked(true);
        }
        buttonConnexion.setOnClickListener(view -> {
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
        button.setOnClickListener(view -> {
            Intent menuActivity = new Intent(MainActivity.this, MenuActivity.class );
            menuActivity.putExtra("bluetooth_device", mDevice);
            startActivity(menuActivity);
        });

        //Contrôle du bouton pour quitter l'utilisation
        buttonLeave.setOnClickListener(view -> finish());
    }
}