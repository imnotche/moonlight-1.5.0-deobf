package me.twerknation28.moonlight.features.settings;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Bind
implements Util {
    private int key;

    public Bind(int key) {
        this.key = key;
    }

    public static Bind none() {
        return new Bind(-1);
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isEmpty() {
        return this.key < 0;
    }

    public String toString() {
        return this.isEmpty() ? "None" : (this.key < 0 ? "None" : this.capitalise(InputUtil.fromKeyCode((int)this.key, (int)0).getTranslationKey()));
    }

    public boolean isDown() {
        return !this.isEmpty() && GLFW.glfwGetKey((long)mc.getWindow().getHandle(), (int)this.getKey()) == 1;
    }

    private String capitalise(String str) {
        if (str.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(str.charAt(0)) + (str.length() != 1 ? str.substring(1).toLowerCase() : "");
    }

    public static class BindConverter
    extends Converter<Bind, JsonElement> {
        @Override
        public JsonElement doForward(Bind bind) {
            return new JsonPrimitive(bind.toString());
        }

        @Override
        public Bind doBackward(JsonElement jsonElement) {
            String s = jsonElement.getAsString();
            if (s.equalsIgnoreCase("None")) {
                return Bind.none();
            }
            int key = -1;
            try {
                key = InputUtil.fromTranslationKey((String)s.toUpperCase()).getCode();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (key == 0) {
                return Bind.none();
            }
            return new Bind(key);
        }
    }
}
