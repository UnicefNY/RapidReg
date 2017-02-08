package org.unicef.rapidreg.service.cache;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ItemValuesMapTest {
    public static final String FULL_NAME = "name";
    public static final String FIRST_NAME = "name_first";
    public static final String MIDDLE_NAME = "name_middle";
    public static final String SURNAME = "name_last";
    public static final String NICKNAME = "name_nickname";
    public static final String OTHER_NAME = "name_other";

    @Test
    public void should_concat_multi_string_with_blank_when_no_one_exist_and_empty_key() throws Exception {
        assertThat(new ItemValuesMap().concatMultiStringsWithBlank(),is(""));
    }

    @Test
    public void should_concat_multi_string_with_blank_when_no_one_exist() throws Exception {
        assertThat(new ItemValuesMap().concatMultiStringsWithBlank(FULL_NAME),is(""));
    }

    @Test
    public void should_concat_multi_string_with_blank_when_only_one_exist() throws Exception {
        ItemValuesMap valuesMap = new ItemValuesMap();
        valuesMap.addStringItem(FULL_NAME, "SJ");
        assertThat(valuesMap.concatMultiStringsWithBlank(FULL_NAME),is("SJ"));
    }

    @Test
    public void should_concat_multi_string_with_blank_when_more_than_one() throws Exception {
        ItemValuesMap valuesMap = new ItemValuesMap();
        valuesMap.addStringItem(FULL_NAME, "Shenjian Yuan");
        valuesMap.addStringItem(FIRST_NAME, "Shen");
        valuesMap.addStringItem(MIDDLE_NAME, "jian");
        valuesMap.addStringItem(NICKNAME, "SJ");
        assertThat(valuesMap.concatMultiStringsWithBlank(FULL_NAME,FIRST_NAME,MIDDLE_NAME,NICKNAME),is("Shenjian Yuan Shen jian SJ"));
    }
}