package makecodework.roadscheck;


import java.util.Date;

public class BlogPost extends BlogPostid{
    public String user_id;
    public String image_url;
    public String desc;
    public String road_defect;
    public String localization;



    public String image_thumb;
    public Date timestamp;



    public BlogPost(){}



    public BlogPost(String user_id, String image_url, String desc, String defect, String localization, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.road_defect = defect;
        this.localization = localization;
        this.image_thumb = image_thumb;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDefect() {
        return road_defect;
    }
    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }
    public void setDefect(String defect) {
        this.road_defect = defect;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
