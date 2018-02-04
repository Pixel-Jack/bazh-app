package fr.clement.rennsurrection.bluesound.Welcome;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;
import android.widget.RelativeLayout;

import fr.clement.rennsurrection.bluesound.Main.MainActivity;
import fr.clement.rennsurrection.bluesound.R;

/**
 * Created by Clément P on 08/02/2017.
 */

public class WelcomeActivity extends AppCompatActivity {

    private RelativeLayout screen = null;
    private static final int MY_PERMISSIONS_BLUETOOTH_PRIVILEGED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setExitTransition(new Explode());
        screen = (RelativeLayout) findViewById(R.id.ecranAccueil);
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(WelcomeActivity.this, MainActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this);
                startActivity(intent);
                finish();
            }
        });
    }
}