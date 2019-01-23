package makecodework.roadscheck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class EditPostActivity extends AppCompatActivity {
    private Toolbar editToolbar;
    private ImageView editImage;
    private EditText editTitle;
    private EditText editLocal;
    private EditText editPostText;
    private Button update_btn;
    private Uri postImageUri = null;
    private ProgressBar edit_progressBar;

    private StorageReference storageReference;
    FirebaseFirestore fireBase;

    private Bitmap compressedImageFile;
    private static final String TAG = "EditPostActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editImage = findViewById(R.id.edit_image);
        editTitle = findViewById(R.id.edit_title);
        editLocal = findViewById(R.id.edit_localization);
        editPostText = findViewById(R.id.editpost_text);
        update_btn = findViewById(R.id.update_btn);
        edit_progressBar = findViewById(R.id.edit_progressBar);


        editToolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(editToolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        fireBase = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        String blogId = i.getStringExtra("blogId");

        fireBase.collection("Posts").document(blogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String EditTitle = task.getResult().getString("road_defect");
                    String EditLocal = task.getResult().getString("localization");
                    String EditPostText = task.getResult().getString("desc");
                    String EditImage = task.getResult().getString("image_url");

                    setData(EditTitle,EditLocal,EditPostText,EditImage);

                }else{
                    Toast.makeText(EditPostActivity.this, "Empty", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setData(String EditTitle, String EditLocal, String EditPostText, String EditImage) {

        editImage = findViewById(R.id.edit_image);
        editTitle = findViewById(R.id.edit_title);
        editLocal = findViewById(R.id.edit_localization);
        editPostText = findViewById(R.id.editpost_text);

        editTitle.setText(EditTitle);
        editLocal.setText(EditLocal);
        editPostText.setText(EditPostText);

        RequestOptions placeholderRequest = new RequestOptions();

        placeholderRequest.placeholder(R.drawable.post_placeholder);

        Glide.with(getApplicationContext()).applyDefaultRequestOptions(placeholderRequest).load(EditImage).into(editImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(EditPostActivity.this);
            }
        });

        fireBase = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        final String blogId = i.getStringExtra("blogId");


        update_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String desc = editTitle.getText().toString();
                final String local = editLocal.getText().toString();
                final String defectText = editPostText.getText().toString();
//                final String defectImg = editImage.getDrawable().toString();
                edit_progressBar.setVisibility(View.VISIBLE);

                final String randomName = UUID.randomUUID().toString();
                storageReference = FirebaseStorage.getInstance().getReference();

                StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
                filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        final String downloadUri = task.getResult().getDownloadUrl().toString();
                        if(task.isSuccessful()){
                            File actualImageFile = new File(postImageUri.getPath());
                            try {
                                compressedImageFile = new Compressor(EditPostActivity.this)
                                        .setMaxWidth(230)
                                        .setMaxHeight(230)
                                        .setQuality(10)
                                        .compressToBitmap(actualImageFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumbData = baos.toByteArray();

                            UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg")
                                    .putBytes(thumbData);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();
                                    DocumentReference db = fireBase.collection("Posts").document(blogId);
                                    db.update(
                                            "road_defect", desc,
                                            "localization", local,
                                            "desc", defectText,
                                            "image_url",downloadUri,
                                            "image_thumb", downloadThumbUri

                                    ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(EditPostActivity.this, "Post successfully updated!", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(EditPostActivity.this, MainActivity.class );
                                                startActivity(i);
                                                finish();
                                            }else{

                                            }
                                            edit_progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
//                DocumentReference db = fireBase.collection("Posts").document(blogId);
//                db.update(
//                        "road_defect", desc,
//                        "localization", local,
//                        "desc", defectText
//                        )
//               .addOnSuccessListener(new OnSuccessListener<Void>() {
//
//                   @Override
//                   public void onSuccess(Void aVoid) {
//                       edit_progressBar.setVisibility(View.VISIBLE);
//
//                   }
//               })
//               .addOnFailureListener(new OnFailureListener() {
//                   @Override
//                   public void onFailure(@NonNull Exception e) {
//                       Toast.makeText(EditPostActivity.this, "Error updating document", Toast.LENGTH_LONG).show();
//
//                   }
//               });
//
//
//                edit_progressBar.setVisibility(View.INVISIBLE);
            }

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                editImage.setImageURI(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
