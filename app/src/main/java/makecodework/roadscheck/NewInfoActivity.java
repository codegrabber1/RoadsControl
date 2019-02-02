package makecodework.roadscheck;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewInfoActivity extends AppCompatActivity {

    private Toolbar infoToolbar;

    private TextView addTitle;
    private TextView addPerformer;
    private TextView addDate;
    private TextView addPrice;
    private TextView addText;
    private Button addInfo;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_info);

        infoToolbar = findViewById(R.id.info_toolbar);
        setSupportActionBar(infoToolbar);
        getSupportActionBar().setTitle("Виконання робіт");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        infoToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),InfoActivity.class));
                finish();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        addTitle = findViewById(R.id.addinfo_title);
        addPerformer = findViewById(R.id.addinfo_performer);
        addDate = findViewById(R.id.addinfo_date);
        addPrice = findViewById(R.id.addinfo_price);
        addText = findViewById(R.id.addinfo_text);
        addInfo = findViewById(R.id.addInfo_btn);

        //DatePicker
        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dDialog = new DatePickerDialog(
                        NewInfoActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day
                );
                dDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dDialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
              month = month+1;
              String date = dayOfMonth + "." + month + "." + year;
                addDate.setText(date);
            }
        };

        addInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String infoTitle = addTitle.getText().toString();
                final String infoPerformer = addPerformer.getText().toString();
                final String infoDate = addDate.getText().toString();
                final String infoPrice = addPrice.getText().toString();
                final String infoText = addText.getText().toString();

                if(!TextUtils.isEmpty(infoTitle) && !TextUtils.isEmpty(infoPerformer) && !TextUtils.isEmpty(infoDate)
                        && !TextUtils.isEmpty(infoPrice) && !TextUtils.isEmpty(infoText)){
                    Map<String, Object> infoMap = new HashMap<>();
                    infoMap.put("info_title", infoTitle);
                    infoMap.put("info_performer", infoPerformer);
                    infoMap.put("info_date", infoDate);
                    infoMap.put("info_price", infoPrice);
                    infoMap.put("text", infoText);
                    infoMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Information").document("Roads").set(infoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewInfoActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(NewInfoActivity.this, MainActivity.class );
                            startActivity(i);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String error = e.getMessage();
                            Toast.makeText(NewInfoActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
                } else{
                    Toast.makeText(NewInfoActivity.this, "Fill in all the fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
