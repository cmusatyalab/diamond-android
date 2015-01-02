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

import edu.cmu.cs.diamond.android.R;

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
