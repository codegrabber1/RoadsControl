package makecodework.roadscheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {

    private EditText regName;
    private EditText regPass;
    private EditText regConfirmPass;
    private Button regBtn;
    private Button sendToBtn;
    private ProgressBar rPBar;

    FirebaseAuth appAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regName = findViewById(R.id.reg_name);
        regPass = findViewById(R.id.reg_pass);
        regConfirmPass = findViewById(R.id.confirm_reg_pass);
        sendToBtn = findViewById(R.id.sent_to_log);
        regBtn = findViewById(R.id.reg_btn);
        rPBar = findViewById(R.id.reg_pBar);

        appAuth = FirebaseAuth.getInstance();

        sendToBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regName.getText().toString();
                String pass = regPass.getText().toString();
                String confirmPass = regConfirmPass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirmPass) ){
                    if(pass.equals(confirmPass)){
                        rPBar.setVisibility(View.VISIBLE);
                        appAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent i = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(i);
                                    finish();

                                }else{
                                    String errMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: " + errMessage, Toast.LENGTH_LONG).show();

                                }
                                rPBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }else{
                        Toast.makeText(RegisterActivity.this, "Password and Confirm Password Fields doesn't match", Toast.LENGTH_LONG).show();

                    }

                }else{
                    Toast.makeText(RegisterActivity.this, "You did not fill all fields ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = appAuth.getCurrentUser();
        if(user !=null){
            senToMain();
        }
    }

    private void senToMain() {
        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);

    }
}
