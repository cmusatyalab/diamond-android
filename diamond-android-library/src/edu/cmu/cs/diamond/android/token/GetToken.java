package edu.cmu.cs.diamond.android.token;

import edu.cmu.cs.diamond.android.TagEnum;

public class GetToken extends Token {
    public String var;
    public GetToken(String var) {
        super(TagEnum.GET);
        this.var = var;
    }
}