package makecodework.roadscheck;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseAuth appAuth;
    private FirebaseFirestore firebaseFirestore;
    private String currentuser_id;

    private BottomNavigationView mainBotNav;
    private FloatingActionButton addPostBtn;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        appAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        addPostBtn = findViewById(R.id.flAcBtn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Чиї Дороги?");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }
        });

//        if(appAuth.getCurrentUser() != null) {

            mainBotNav = findViewById(R.id.mainBotNav);

            // FRAGMENTS
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            replaceFragment(homeFragment);

            mainBotNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.bottom_home:
                            replaceFragment(homeFragment);
                            return true;
                        case R.id.bottom_notification:
                            replaceFragment(notificationFragment);
                            return true;
                        case R.id.bottom_account:
                            if(appAuth.getCurrentUser() != null){
                                replaceFragment(accountFragment);
                            }else{
                                sendToLogin();
                            }

                            return true;
                        default:
                            return false;
                    }


                }
            });

            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(appAuth.getCurrentUser() != null){
                        Intent i = new Intent(MainActivity.this, NewPostActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        sendToLogin();
                    }

                }
            });
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//        if(user == null) {
//            //sendToHome();
//            sendToLogin();
//        } else{
//            currentuser_id = appAuth.getCurrentUser().getUid();
//            firebaseFirestore.collection("Users").document(currentuser_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if(task.isSuccessful()){
//                        if(!task.getResult().exists()){
//                            Intent mainIntent = new Intent(MainActivity.this, SetupActivity.class);
//                            startActivity(mainIntent);
//
//                        }
//
//                    } else{
//                        String errorMess = task.getException().getMessage();
//                        Toast.makeText(MainActivity.this, "Error: " + errorMess, Toast.LENGTH_LONG).show();
//
//
//                    }
//                }
//            });
//        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mainmenu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            switch(item.getItemId()){
                case R.id.ac_settings:
                    if(appAuth.getCurrentUser() != null){
                        Intent i = new Intent(MainActivity.this,SetupActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        sendToLogin();
                    }
                    return true;
                case R.id.log_out:
                    logOut();
                    return true;

                default: return false;
            }

    }

    private void logOut() {
        appAuth.signOut();
        sendToHome();

    }

    private void sendToHome() {
        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    private void sendToLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }
}
