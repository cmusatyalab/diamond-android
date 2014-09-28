package edu.cmu.cs.diamond.android.token;

import edu.cmu.cs.diamond.android.TagEnum;

public class SetToken extends Token {
    public String var;
    public byte[] buf;
    public SetToken(String var, byte[] buf) {
        super(TagEnum.SET);
        this.var = var;
        this.buf = buf;
    }
}