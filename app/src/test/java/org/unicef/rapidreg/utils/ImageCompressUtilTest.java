package org.unicef.rapidreg.utils;

import android.graphics.BitmapFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.unicef.rapidreg.utils.ImageCompressUtil.calculateInSampleSize;

@RunWith(PowerMockRunner.class)
public class ImageCompressUtilTest {

    @Test
    public void should_return_sample_size_when_give_req_width_and_height() throws Exception {
        BitmapFactory.Options options = createBitmapFactoryOptions(4, 4);
        int actual = calculateInSampleSize(options, 2, 2);
        assertThat("should return 2 when ratio is 2", actual, is(2));
    }

    @Test
    public void should_return_sample_size_when_give_smaller_req_width_and_height() throws Exception {
        BitmapFactory.Options options = createBitmapFactoryOptions(4, 4);
        int actual = calculateInSampleSize(options, 3, 3);
        assertThat("Should return 1 when ratio is smaller than 2.", actual, is(1));
    }

    @Test
    public void should_return_sample_size_when_give_req_which_ratio_is_more_than_2_less_than_4() throws Exception {
        BitmapFactory.Options options = createBitmapFactoryOptions(9, 6);
        int actual = calculateInSampleSize(options, 3, 2);
        assertThat("Should return 3 when ratio is 3.", actual, is(3));

    }

    @Test
    public void should_return_sample_size_when_give_req_which_ratio_is_more_than_2() throws Exception {
        BitmapFactory.Options options = createBitmapFactoryOptions(8, 12);
        int actual = calculateInSampleSize(options, 2, 3);
        assertThat("Should return 4 when ratio is 4.", actual, is(4));
    }



    private BitmapFactory.Options createBitmapFactoryOptions(int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = height;
        options.outWidth = width;

        return options;
    }
}