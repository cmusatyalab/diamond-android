package edu.cmu.cs.diamond.android.token;

import edu.cmu.cs.diamond.android.TagEnum;

public class LogToken extends Token {
    public int level;
    public String msg;
    public LogToken(int level, String msg) {
        super(TagEnum.LOG);
        this.level = level;
        this.msg = msg;
    }
}