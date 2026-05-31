package me.twerknation28.moonlight.util.traits;

import com.google.gson.JsonElement;

public interface Jsonable {
    public JsonElement toJson();

    public void fromJson(JsonElement var1);

    default public String getFileName() {
        return "";
    }
}
