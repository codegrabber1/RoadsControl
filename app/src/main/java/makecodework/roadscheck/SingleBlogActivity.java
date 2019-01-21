package makecodework.roadscheck;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SingleBlogActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;

    private Toolbar simpleToolbar;
    private ImageView getImage;
    private TextView defefectTitle;
    private TextView defectLocalization;
    private TextView getText;

    public SingleBlogActivity(){ }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog);

        simpleToolbar = findViewById(R.id.simple_toolbar);

        setSupportActionBar(simpleToolbar);
        getSupportActionBar().setTitle("Check Roads");

        Intent i = getIntent();
        String blogId = i.getStringExtra("blogId");

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Posts").document(blogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String getTitle = task.getResult().getString("road_defect");
                    String getLocalization = task.getResult().getString("localization");
                    String textDesc = task.getResult().getString("desc");
                    String textImage = task.getResult().getString("image_url");

                    getData(getTitle, getLocalization, textDesc, textImage);

                } else{

                    Toast.makeText(SingleBlogActivity.this, "Empty", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void getData(String getTitle, String getLocalization, String textDesc, String textImage) {

        defefectTitle = findViewById(R.id.get_title);
        defectLocalization = findViewById(R.id.get_localization);
        getText = findViewById(R.id.get_text_descr);
        getImage = findViewById(R.id.get_simple_image);

        defefectTitle.setText(getTitle);

        defectLocalization.setText(getLocalization);

        getText.setText(textDesc);

        RequestOptions placeholderRequest = new RequestOptions();

        placeholderRequest.placeholder(R.drawable.image_placeholder);

        Glide.with(getApplicationContext()).applyDefaultRequestOptions(placeholderRequest).load(textImage).into(getImage);

    }

}
