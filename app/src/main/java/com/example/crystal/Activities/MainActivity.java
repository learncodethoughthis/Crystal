package com.example.crystal.Activities;

import com.example.crystal.R;
import com.example.crystal.WifiDirectBroadcastReceiver;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Method;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.crystal.Model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.crystal.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener{

    private ActivityMainBinding binding;
    Dialog popAddPost;
    TextView popupTitle, popupDescription;
    FloatingActionButton postCreateBtn, wifiCheckBtn;
    ImageView createPostBtn;

    Toolbar toolbar;
    Intent intent = getIntent();
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressBar popupClickProgress;
    FirebaseFirestore firebasestore;

    WifiManager wifiManager;
    WifiP2pManager p2pManager;
    WifiP2pManager.Channel p2pChannel;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    private WifiP2pDeviceList peers;
    private WifiP2pDevice ConectedPartner;
    private ArrayAdapter<String> WifiP2parrayAdapter;
    private String TAG = "##BoadcastReceiverAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar= findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);

        mAuth= FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        firebasestore= FirebaseFirestore.getInstance();
        //

        //Float button to popup Post
        postCreateBtn= findViewById(R.id.postCreateBtn);
        postCreateBtn.setOnClickListener((view) ->{
            popAddPost.show();
        });
        iniPopup();


        //Float button to access wifi
        wifiCheckBtn= findViewById(R.id.wifiCheckBtn);
        wifiCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(intent,1);
            }
        });
        //Main p2p activity
        initialP2p();


        //Bottom Navigation
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_advisor, R.id.navigation_requester)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

    private void initialP2p() {
        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        p2pManager= (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        p2pChannel= p2pManager.initialize(this,getMainLooper(),null);
        broadcastReceiver= new WifiDirectBroadcastReceiver(p2pManager, p2pChannel,this,peerListListener);
        if(p2pManager !=null && p2pChannel!=null){
            p2pManager.requestGroupInfo(p2pChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if(group !=null && p2pManager !=null && p2pChannel !=null){
                        p2pManager.removeGroup(p2pChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG,"remove group success");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG,"remove group success"+reason);
                            }
                        });
                    }
                }
            });
        }

        intentFilter= new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        p2pManager.discoverPeers(p2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });

    }

    //Post create when use fab
    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.pop_up_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popupTitle = popAddPost.findViewById(R.id.titleTopic);
        popupDescription = popAddPost.findViewById(R.id.descriptionTopic);
        createPostBtn = popAddPost.findViewById(R.id.createPostBtn);
        popupClickProgress=popAddPost.findViewById(R.id.popup_progressBar);
        DocumentReference reference;
        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createPostBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);
                String Title = popupTitle.getText().toString();
                String Description = popupDescription.getText().toString();
                String userId = mAuth.getCurrentUser().getUid();


                if (!Title.isEmpty() && !Description.isEmpty()) {
                    firebasestore.collection("user").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    final String username = documentSnapshot.getString("username");
                                    Post post = new Post(userId, username, Title, Description);
                                    uploadPost(post);
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    createPostBtn.setVisibility(View.VISIBLE);
                                    popAddPost.dismiss();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,"PostFailed=" +e.getMessage(),Toast.LENGTH_SHORT);
                            popupClickProgress.setVisibility(View.INVISIBLE);
                            createPostBtn.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    showPostMessage("Please verify all input fields");
                    createPostBtn.setVisibility(View.VISIBLE);
                    popAddPost.dismiss();
                }
            }


            //up post to cloudStore
            private void uploadPost(Post post) {
                firebasestore.collection("Post")
                        .add(post)
                        .addOnSuccessListener((OnSuccessListener) (documentReference) -> {
                            Toast.makeText(MainActivity.this, "added Successful", MainActivity.class.getModifiers()).show();

                        }).addOnFailureListener((e -> {
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }));
            }


            private void showPostMessage(String message) {

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

            }


        });
    }


    //P2p activity

    private WifiP2pManager.PeerListListener peerListListener=new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Log.d("INPeerListListener", "Works");
            ArrayList<WifiP2pDevice> peersNameFixed= new ArrayList<WifiP2pDevice>();

            for (WifiP2pDevice peer: peerList.getDeviceList()){
                String newDeviceName= peer.deviceName.replace("[phone]","");
                peer.deviceName=newDeviceName;
            }

            peers= new WifiP2pDeviceList(peerList);
            WifiP2parrayAdapter.clear();
            for(WifiP2pDevice peer: peerList.getDeviceList()){
                WifiP2parrayAdapter.add(peer.deviceName);
            }
        }
    };
    public void connectToPeer(final WifiP2pDevice wifiPeer){
        this.ConectedPartner=wifiPeer;
        final WifiP2pConfig config= new WifiP2pConfig();
        config.deviceAddress=wifiPeer.deviceAddress;
        p2pManager.connect(p2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Intent intent=new Intent()
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(TAG,"pre connect" +Boolean.toString(info.groupFormed));
        if(info.groupFormed)

    }
}