package makecodework.roadscheck;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ImageView logoImg;
    private EditText loginName;
    private EditText loginPass;
    private Button loginBtn;
    private Button toRegBtn;
    private ProgressBar crcProgress;

    private FirebaseAuth appAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logoImg = findViewById(R.id.logo_img);
        loginName = findViewById(R.id.login_name);
        loginPass = findViewById(R.id.login_pass);
        crcProgress = findViewById(R.id.circle_progress);
        loginBtn = findViewById(R.id.login_btn);
        toRegBtn = findViewById(R.id.sent_to_reg);

        appAuth = FirebaseAuth.getInstance();

        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lName = loginName.getText().toString();
                String lPass = loginPass.getText().toString();

                if(!TextUtils.isEmpty(lName) && !TextUtils.isEmpty(lPass)){
                    crcProgress.setVisibility(View.VISIBLE);
                    appAuth.signInWithEmailAndPassword(lName,lPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful() ){
                                sendToMain();
                            }else{

                                String e = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + e, Toast.LENGTH_LONG).show();
                                
                            }
                            crcProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "Fill all Fields and try again ", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = appAuth.getCurrentUser();

        if(user != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
