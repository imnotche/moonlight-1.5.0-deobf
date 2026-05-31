package me.twerknation28.moonlight.util.traits;

import com.google.gson.JsonElement;

public interface Jsonable
{
    JsonElement toJson();
    
    void fromJson(final JsonElement p0);
    
    default String getFileName() {
        return "";
    }
}
