package fr.shams.cadenaelectronique.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.shams.cadenaelectronique.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    //Déclaration d'une liste contenant les boutons
    private List<Button> mActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Affectation des widgets aux boutons de la classe
        mActionButton = Arrays.asList(
                findViewById(R.id.verrouillage_button),
                findViewById(R.id.enregistrement_button),
                findViewById(R.id.gps_button)
        );

        mActionButton.get(0).setOnClickListener(this);
        mActionButton.get(1).setOnClickListener(this);
        mActionButton.get(2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int index = mActionButton.indexOf((Button) view);

        //Portion de code permettant la gestion de l'action suite au clique sur le bouton verrouillage
        if(index == 0 ){

        }
        //Portion de code permettant la gestion de l'action suite au clique sur le bouton enregistrement de badge
        else if(index == 1){

        }
        //Portion de code permettant la gestion de l'action suite au clique sur le bouton coordonnées GPS
        else{

        }
    }
}