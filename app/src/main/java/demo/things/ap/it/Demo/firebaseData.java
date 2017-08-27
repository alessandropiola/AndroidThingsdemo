package demo.things.ap.it.Demo;

/**
 * Created by alessandro on 27/08/2017.
 */
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
public class firebaseData {


    public String counter;
    public String img;
    public Map<String, Boolean> stars = new HashMap<>();

    public firebaseData() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public firebaseData(String counter, String img) {
        this.counter = counter;
        this.img = img;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("counter", counter);
        result.put("img", img);


        return result;
    }
    // [END post_to_map]

}
// [END post_class]
