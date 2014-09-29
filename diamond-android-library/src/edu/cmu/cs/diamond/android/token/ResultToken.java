package edu.cmu.cs.diamond.android.token;

import edu.cmu.cs.diamond.android.TagEnum;

public class ResultToken extends Token {
    public double var;
    public ResultToken(double var) {
        super(TagEnum.RESULT);
        this.var = var;
    }
}