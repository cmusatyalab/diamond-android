package edu.cmu.cs.diamond.android;

import edu.cmu.cs.diamond.diamonddraid.R;

public enum FilterEnum {
    DOG_TEXTURE(R.raw.dog_texture),
    GABOR_TEXTURE(R.raw.gabor_texture),
    IMG_DIFF(R.raw.img_diff),
    NULL_FILTER(R.raw.null_filter),
    NUM_ATTR(R.raw.num_attr),
    OCV_FACE(R.raw.ocv_face),
    RGB_HISTOGRAM(R.raw.rgb_histogram),
    RGBIMG(R.raw.rgbimg),
    SHINGLING(R.raw.shingling),
    TEXT_ATTR(R.raw.text_attr),
    THUMBNAILER(R.raw.thumbnailer);

    public final int id;
    FilterEnum(int id) { this.id = id; }
}