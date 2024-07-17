package com.example.crystal.ui.Advisor;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crystal.Activities.LoginActivity;
import com.example.crystal.Activities.MainActivity;
import com.example.crystal.Adapter.PostAdapter;
import com.example.crystal.Model.Post;
import com.example.crystal.R;
import com.example.crystal.databinding.FragmentAdvisorBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdvisorFragment extends Fragment{

    private FragmentAdvisorBinding binding;
    RecyclerView recyclerView;
    ArrayList<Post> postList;
    PostAdapter postAdapter;
    FirebaseFirestore firestore;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_advisor, container, false);
        recyclerView=fragmentView.findViewById(R.id.advisorPostRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
        firestore= FirebaseFirestore.getInstance();
        firestore.collection("Post").whereNotEqualTo("userId",mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d:list){
                            Post obj=d.toObject(Post.class);
                            postList.add(obj);
                        }
                        postAdapter.notifyDataSetChanged();
                    }
                });
        return fragmentView;
    }


}