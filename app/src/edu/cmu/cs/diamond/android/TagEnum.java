package edu.cmu.cs.diamond.android;

public enum TagEnum {
    INIT("init-success"),
    GET("get-attribute"),
    SET("set-attribute"),
    OMIT("omit-attribute"),
    SESSIONGET("get-session-variables"),
    SESSIONUPDATE("update-session-variables"),
    LOG("log"),
    RESULT("result");

    public final String str;
    TagEnum(String str) { this.str = str; }

    public static TagEnum findByStr(String str) {
        for (TagEnum t : values()) {
            if (t.str.equals(str)) return t;
        }
        return null;
    }
}