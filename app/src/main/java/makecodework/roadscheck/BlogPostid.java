package makecodework.roadscheck;

import com.google.firebase.firestore.Exclude;

import android.support.annotation.NonNull;

public class BlogPostid {
    @Exclude
    public String BlogPostId;

    public <T extends BlogPostid> T withId(@NonNull final String id){
        this.BlogPostId = id;
        return (T) this;
    }
}
