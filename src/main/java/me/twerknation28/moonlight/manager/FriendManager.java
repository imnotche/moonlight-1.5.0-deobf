package me.twerknation28.moonlight.manager;

import java.util.ArrayList;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import me.twerknation28.moonlight.util.traits.Jsonable;

public class FriendManager implements Jsonable
{
    private static final List<String> friends;
    
    public static boolean isFriend(final String name) {
        return FriendManager.friends.stream().anyMatch(friend -> friend.equalsIgnoreCase(name));
    }
    
    public boolean isFriend(final PlayerEntity player) {
        return isFriend(player.getGameProfile().getName());
    }
    
    public void addFriend(final String name) {
        FriendManager.friends.add(name);
    }
    
    public void removeFriend(final String name) {
        FriendManager.friends.removeIf(s -> s.equalsIgnoreCase(name));
    }
    
    public List<String> getFriends() {
        return FriendManager.friends;
    }
    
    @Override
    public JsonElement toJson() {
        final JsonObject object = new JsonObject();
        final JsonArray array = new JsonArray();
        for (final String friend : FriendManager.friends) {
            array.add(friend);
        }
        object.add("friends", array);
        return object;
    }
    
    @Override
    public void fromJson(final JsonElement element) {
        for (final JsonElement e : element.getAsJsonObject().get("friends").getAsJsonArray()) {
            FriendManager.friends.add(e.getAsString());
        }
    }
    
    @Override
    public String getFileName() {
        return "friends.json";
    }
    
    static {
        friends = new ArrayList<String>();
    }
}
