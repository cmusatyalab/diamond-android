package edu.cmu.cs.diamond.android.token;

import edu.cmu.cs.diamond.android.TagEnum;

public class OmitToken extends Token {
    public String var;
    public OmitToken(String var) {
        super(TagEnum.OMIT);
        this.var = var;
    }
}