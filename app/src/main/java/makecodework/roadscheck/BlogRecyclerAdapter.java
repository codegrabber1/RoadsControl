package makecodework.roadscheck;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blog_list){
        this.blog_list = blog_list;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item, viewGroup, false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.setIsRecyclable(false);

        firebaseAuth = FirebaseAuth.getInstance();

        final String blogPostId = blog_list.get(i).BlogPostId;
        final String currentUser_id = firebaseAuth.getCurrentUser().getUid();

        String defect_data = blog_list.get(i).getDefect();
        viewHolder.setDefectTitle(defect_data);

        String localization_data = blog_list.get(i).getLocalization();
        viewHolder.setLocalizationData(localization_data);

        String image_url = blog_list.get(i).getImage_url();
        String thumbUri = blog_list.get(i).getImage_thumb();
        viewHolder.setBlogImage(image_url, thumbUri);

        String user_id = blog_list.get(i).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    viewHolder.setUserData(userName, userImage);

                }else{
                    //Firebase Exeptions
                }
            }
        });

        viewHolder.blogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SingleBlogActivity.class);
                i.putExtra("blogId", blogPostId);
                context.startActivity(i);

            }
        });

        long milliseconds = blog_list.get(i).getTimestamp().getTime();
        String dateString = DateFormat.format("dd.MM.yyyy", new Date(milliseconds)).toString();

        viewHolder.setTime(dateString);

        // Likes count
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    int count = documentSnapshots.size();
                    viewHolder.updateLikesCount(count);
                }else{
                    viewHolder.updateLikesCount(0);
                }

            }
        });

        // Get likes
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUser_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    viewHolder.blogLikeBtn.setImageDrawable(context.getResources().getDrawable(R.mipmap.like_accent));

                }else{
                    viewHolder.blogLikeBtn.setImageDrawable(context.getResources().getDrawable(R.mipmap.like_grey));
                }

            }
        });

        //Likes feature
        viewHolder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUser_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUser_id).set(likesMap);
                        }else{
                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUser_id).delete();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View iView;
        private TextView defectView;
        private TextView localtView;
        private ImageView blogImageView;
        private TextView blogDate;

        private TextView blogAuthor;
        private CircleImageView blogUserImage;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        public ViewHolder(View itemView){
            super(itemView);
            iView = itemView;
            blogLikeBtn = iView.findViewById(R.id.blog_like);

        }

        public void setDefectTitle(String defectTitle){
            defectView = iView.findViewById(R.id.defect_title);
            defectView.setText(defectTitle);

        }

        public void setBlogImage(String downloadUri, String thumbUri){
            blogImageView = iView.findViewById(R.id.blog_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);


        }

        public void setTime(String date){
            blogDate = iView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        public void setUserData(String name, String image){
            blogUserImage = iView.findViewById(R.id.blog_user_image);
            blogAuthor = iView.findViewById(R.id.blog_user_name);

            blogAuthor.setText(name);

            RequestOptions placeholderRequest = new RequestOptions();

            placeholderRequest.placeholder(R.drawable.unnamed);

            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image).into(blogUserImage);


        }

        public void updateLikesCount(int count) {

            blogLikeCount = iView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + context.getString(R.string.likes));
        }


        public void setLocalizationData(String localization_data) {
            localtView = iView.findViewById(R.id.def_local);
            localtView.setText(localization_data);
        }
    }
}
