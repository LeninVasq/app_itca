package sv.edu.itca.itca_fepade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import sv.edu.itca.itca_fepade.Validation.Login;

public class splash extends AppCompatActivity {

    boolean not_back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferencesusu = getSharedPreferences("token", MODE_PRIVATE);
        String token = sharedPreferencesusu.getString("token", "");


        if (token.isEmpty()) {
            Handler controller = new Handler();
            controller.postDelayed(() -> {
                if (!not_back) {
                    Intent login = new Intent(splash.this, Login.class);
                    startActivity(login);
                    finish();
                }

            }, 3000);
        }
        else{
            Handler controller = new Handler();
            controller.postDelayed(() -> {
                if (!not_back) {
                    Intent login = new Intent(splash.this, MainActivity.class);
                    startActivity(login);
                    finish();
                }

            }, 3000);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        not_back = true;
    }
}