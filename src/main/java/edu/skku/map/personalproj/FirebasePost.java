package edu.skku.map.personalproj;

import java.util.HashMap;
import java.util.Map;

public class FirebasePost {
    public String tag;
    public FirebasePost() { ; }

    public FirebasePost(String tag)
    {
        this.tag = tag;
    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tag", this.tag);
        return result;
    }
}
