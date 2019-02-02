package makecodework.roadscheck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar homeToolbar;
    private CardView infoCard, defectCard, defectPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);
        getSupportActionBar().setTitle("Check Roads");



        infoCard = findViewById(R.id.info_card);
        defectCard = findViewById(R.id.defect_card);
        defectPhoto = findViewById(R.id.defect_photo_card);

        // Add click listenere
        infoCard.setOnClickListener(this);
        defectCard.setOnClickListener(this);
        defectPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()){
            case R.id.info_card:
                i = new Intent(this,NewInfoActivity.class);
                startActivity(i);
                break;
            case R.id.defect_card:
                i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.defect_photo_card:
                i = new Intent(this, LoginActivity.class);
                startActivity(i);
                break;
            default: break;
        }
    }
}
