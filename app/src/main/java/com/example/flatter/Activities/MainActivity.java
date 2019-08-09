package com.example.flatter.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.flatter.Adapters.TabsAccessorAdapter;
import com.example.flatter.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserID="";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Flatter");


        viewPager = findViewById(R.id.main_tabs_viewpager);
        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessorAdapter);

        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mAuth =FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.menu_logout){
            mAuth.signOut();
            transferToLogin();
        }
//        if(item.getItemId() == R.id.menu_groups){
//
//        }
        if(item.getItemId() == R.id.menu_settings){

            transferToSettings();

        }
        if(item.getItemId() == R.id.menu_find_friends){

            trasferToFriends();
        }
        if(item.getItemId() == R.id.privacy_policy){

            trasferToPrivacyPolicy();

        }


        return true;
    }

    private void trasferToPrivacyPolicy() {
        Intent policyIntent = new Intent(MainActivity.this,PolicyActivity.class);

        startActivity(policyIntent);


    }

    private void trasferToFriends() {

        Intent friendsIntent = new Intent(MainActivity.this,FindFriendsActivity.class);

        startActivity(friendsIntent);


    }

    private void transferToSettings() {

        Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);

        startActivity(settingsIntent);
       
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (currentUser == null)
        {
            transferToLogin();
        }
        else
        {
            updateUserStatus("online");

            verifyUserExistence();
        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }


    private void updateUserStatus(String state)
    {

        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        if(currentUser != null){

            currentUserID = mAuth.getCurrentUser().getUid();
        }



        databaseReference.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

    private void verifyUserExistence() {

        String currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){

//                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    transferToSettings();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void transferToLogin() {
        Intent LoginIntent = new Intent(MainActivity.this,LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
    }
}
