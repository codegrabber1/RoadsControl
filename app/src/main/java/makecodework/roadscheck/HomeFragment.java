package makecodework.roadscheck;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blogListView;
    private List<BlogPost> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    final static String TAG = "myLogs";


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        blog_list = new ArrayList<>();
        blogListView = view.findViewById(R.id.blog_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blogListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        blogListView.setAdapter(blogRecyclerAdapter);


//        if(firebaseAuth.getCurrentUser() != null) {
//            firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
//                        if (doc.getType() == DocumentChange.Type.ADDED) {
//                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
//                            blog_list.add(blogPost);
//                            blogRecyclerAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//                }
//            });
//        }


//         if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(-1);

                    if (reachedBottom) {
                        String localization = lastVisible.getString("localization");

                        loadMorePosts();

                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);

            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (isFirstPageFirstLoad) {
                        if(!documentSnapshots.isEmpty()){
                            lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() -1);
                        }else if(firebaseAuth.getCurrentUser() != null) {
                            Toast.makeText(getActivity(), "Add a new post", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getActivity(), NewPostActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(getActivity(), "Add a new post", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            startActivity(i);
                        }

                    }
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();

                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                            if (isFirstPageFirstLoad) {
                                blog_list.add(blogPost);
                            } else {
                                blog_list.add(0, blogPost);
                            }

                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            });
//        }
        // Inflate the layout for this fragment
        return view;
    }

    public void loadMorePosts(){
//        if(firebaseAuth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(10);
            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size()-1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String blogPostId = doc.getDocument().getId();

                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                                blog_list.add(blogPost);
                                blogRecyclerAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                }
            });

//        }

    }

}
