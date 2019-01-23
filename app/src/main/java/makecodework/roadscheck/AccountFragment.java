package makecodework.roadscheck;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private RecyclerView userListView;
    private List<BlogPost> blog_list;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth appAuth;
    private ListRecyclerAdapter listRecyclerAdapter;

    private CircleImageView userImage;
    private Uri mainImageURI = null;
    private TextView userNameView;
    private TextView userPostsCount;
    private TextView userSetAccount;

    private Query getUserData;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        blog_list = new ArrayList<>();
        userListView = view.findViewById(R.id.user_list_view);

//        userNameView = view.findViewById(R.id.user_name);
//        userPostsCount = view.findViewById(R.id.user_post_count);
//        userSetAccount = view.findViewById(R.id.user_set_account);

        appAuth = FirebaseAuth.getInstance();

        user_id = appAuth.getCurrentUser().getUid();
        listRecyclerAdapter = new ListRecyclerAdapter(blog_list);
        userListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userListView.setAdapter(listRecyclerAdapter);

        if(user_id != null){
            firebaseFirestore = FirebaseFirestore.getInstance();

            userListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }



            });
                getUserData = firebaseFirestore.collection("Posts").whereEqualTo("user_id", user_id);

                getUserData.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(!documentSnapshots.isEmpty()){
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String blogPostId = doc.getDocument().getId();

                                    BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                                    blog_list.add(0, blogPost);

                                    listRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
        }

        return view;
    }

}
