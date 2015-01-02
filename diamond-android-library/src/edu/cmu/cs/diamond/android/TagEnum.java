/*
 *  Diamond Android - Diamond filters for the Android platform
 *
 *  Copyright (c) 2013-2014 Carnegie Mellon University
 *  All Rights Reserved.
 *
 *  This software is distributed under the terms of the Eclipse Public
 *  License, Version 1.0 which can be found in the file named LICENSE.
 *  ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS SOFTWARE CONSTITUTES
 *  RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT
 */

package edu.cmu.cs.diamond.android;

public enum TagEnum {
    INIT("init-success"),
    GET("get-attribute"),
    SET("set-attribute"),
    OMIT("omit-attribute"),
    SESSIONGET("get-session-variables"),
    SESSIONUPDATE("update-session-variables"),
    LOG("log"),
    RESULT("result"),
    UNKNOWN("unknown");

    public final String str;
    TagEnum(String str) { this.str = str; }

    public static TagEnum findByStr(String str) {
        for (TagEnum t : values()) {
            if (t.str.equals(str)) return t;
        }
        return UNKNOWN;
    }
}
