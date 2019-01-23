package makecodework.roadscheck;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> {
    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;


    public ListRecyclerAdapter(List<BlogPost> blog_list) {
        this.blog_list = blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_item,
                viewGroup, false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.setIsRecyclable(false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        String user_id = blog_list.get(i).getUser_id();
        final String blogPostId = blog_list.get(i).BlogPostId;

        String defect_data = blog_list.get(i).getDefect();
        String localization_data = blog_list.get(i).getLocalization();
        viewHolder.getDefectData(defect_data,localization_data);

        String postImg = blog_list.get(i).getImage_url();
        viewHolder.setUserPostImage(postImg);


        if(user_id != null){
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            String userName = task.getResult().getString("name");
                            String userImage = task.getResult().getString("image");

//                            viewHolder.setUserData(userName, userImage);


                        }

//                        Toast.makeText(getContext(), "Message: " + name, Toast.LENGTH_SHORT).show();
                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(context, "(FIRESTORE Retrieve Error): " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }else{
            Toast.makeText(context, "You must be LogIn", Toast.LENGTH_SHORT).show();
        }

        // Link to edit post.
        viewHolder.editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editActivity = new Intent(context, EditPostActivity.class);
                editActivity.putExtra("blogId", blogPostId);
                context.startActivity(editActivity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View iView;


        private ImageView userPostImage;
        private TextView userPostTitle;
        private TextView userPostsLocation;
        private TextView userStatusDefect;
        private TextView editPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iView = itemView;
            editPost = iView.findViewById(R.id.edit_user_post);
        }

        public void getDefectData(String defect_data, String localization_data) {
            userPostTitle = iView.findViewById(R.id.user_post_title);
            userPostTitle.setText(defect_data);

            userPostsLocation = iView.findViewById(R.id.user_post_location);
            userPostsLocation.setText(localization_data);
        }

        public void setUserPostImage(String postImg) {
            userPostImage = iView.findViewById(R.id.user_post_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.post_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(postImg).into(userPostImage);
        }


    }
}
