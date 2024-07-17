package com.example.crystal.ui.Requester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crystal.Activities.PostDetailActivity;
import com.example.crystal.Adapter.PostAdapter;
import com.example.crystal.Model.Post;
import com.example.crystal.R;
import com.example.crystal.databinding.FragmentRequesterBinding;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class RequesterFragment extends Fragment{


    private FragmentRequesterBinding binding;
    RecyclerView recyclerView;
    ArrayList<Post> postList;
    PostAdapter postAdapter;
    FirebaseFirestore firestore;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();

    public RequesterFragment() {
        // Required empty public constructor
    }


    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_requester, container, false);
        recyclerView=fragmentView.findViewById(R.id.requestPostRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList=new ArrayList<>();
        postAdapter=new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
        firestore=FirebaseFirestore.getInstance();
        firestore.collection("Post").whereEqualTo("userId",mAuth.getCurrentUser().getUid()).get()
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