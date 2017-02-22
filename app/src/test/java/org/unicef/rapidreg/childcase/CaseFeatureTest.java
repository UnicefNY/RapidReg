package org.unicef.rapidreg.childcase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.R;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.unicef.rapidreg.childcase.CaseFeature.ADD_CP_FULL;
import static org.unicef.rapidreg.childcase.CaseFeature.ADD_CP_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.ADD_GBV_FULL;
import static org.unicef.rapidreg.childcase.CaseFeature.ADD_GBV_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.DETAILS_CP_FULL;
import static org.unicef.rapidreg.childcase.CaseFeature.DETAILS_CP_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.DETAILS_GBV_FULL;
import static org.unicef.rapidreg.childcase.CaseFeature.DETAILS_GBV_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.EDIT_FULL;
import static org.unicef.rapidreg.childcase.CaseFeature.EDIT_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.LIST;
import static org.unicef.rapidreg.childcase.CaseFeature.SEARCH;

@RunWith(PowerMockRunner.class)
public class CaseFeatureTest {

    @Test
    public void should_return_title_id_correctly() throws Exception {
        assertThat("Title id should be Cases", LIST.getTitleId(), is(R.string.cases));
        assertThat("Title id should be New CP Case", ADD_CP_MINI.getTitleId(), is(R.string.new_cp_case));
        assertThat("Title id should be New CP Case", ADD_CP_FULL.getTitleId(), is(R.string.new_cp_case));
        assertThat("Title id should be New GBV Case", ADD_GBV_MINI.getTitleId(), is(R.string.new_gbv_case));
        assertThat("Title id should be New GBV Case", ADD_GBV_FULL.getTitleId(), is(R.string.new_gbv_case));
        assertThat("Title id should be Edit", EDIT_MINI.getTitleId(), is(R.string.edit));
        assertThat("Title id should be Edit", EDIT_FULL.getTitleId(), is(R.string.edit));
        assertThat("Title id should be CP case details", DETAILS_CP_MINI.getTitleId(), is(R.string.cp_case_details));
        assertThat("Title id should be CP case details", DETAILS_CP_FULL.getTitleId(), is(R.string.cp_case_details));
        assertThat("Title id should be GBV case details", DETAILS_GBV_MINI.getTitleId(), is(R.string.gbv_case_details));
        assertThat("Title id should be GBV case details", DETAILS_GBV_FULL.getTitleId(), is(R.string.gbv_case_details));
        assertThat("Title id should be Search", SEARCH.getTitleId(), is(R.string.search));
    }

    @Test
    public void should_return_fragment_correctly() throws Exception {
        assertThat("Fragment should be CaseListFragment", LIST.getFragment().getClass().getSimpleName(), is("CaseListFragment"));
        assertThat("Fragment should be CaseMiniFormFragment", ADD_CP_MINI.getFragment().getClass().getSimpleName(), is("CaseMiniFormFragment"));
        assertThat("Fragment should be CaseMiniFormFragment", ADD_GBV_MINI.getFragment().getClass().getSimpleName(), is("CaseMiniFormFragment"));
        assertThat("Fragment should be CaseMiniFormFragment", EDIT_MINI.getFragment().getClass().getSimpleName(), is("CaseMiniFormFragment"));
        assertThat("Fragment should be CaseMiniFormFragment", DETAILS_CP_MINI.getFragment().getClass().getSimpleName(), is("CaseMiniFormFragment"));
        assertThat("Fragment should be CaseMiniFormFragment", DETAILS_GBV_MINI.getFragment().getClass().getSimpleName(), is("CaseMiniFormFragment"));
        assertThat("Fragment should be CaseRegisterWrapperFragment", ADD_CP_FULL.getFragment().getClass().getSimpleName(), is("CaseRegisterWrapperFragment"));
        assertThat("Fragment should be CaseRegisterWrapperFragment", ADD_GBV_FULL.getFragment().getClass().getSimpleName(), is("CaseRegisterWrapperFragment"));
        assertThat("Fragment should be CaseRegisterWrapperFragment", EDIT_FULL.getFragment().getClass().getSimpleName(), is("CaseRegisterWrapperFragment"));
        assertThat("Fragment should be CaseRegisterWrapperFragment", DETAILS_CP_FULL.getFragment().getClass().getSimpleName(), is("CaseRegisterWrapperFragment"));
        assertThat("Fragment should be CaseRegisterWrapperFragment", DETAILS_GBV_FULL.getFragment().getClass().getSimpleName(), is("CaseRegisterWrapperFragment"));
        assertThat("Fragment should be CaseSearchFragment", SEARCH.getFragment().getClass().getSimpleName(), is("CaseSearchFragment"));
    }

    @Test
    public void should_return_edit_mode() throws Exception {
        assertTrue("Edit mode should be true", ADD_CP_MINI.isEditMode());
        assertTrue("Edit mode should be true", ADD_CP_FULL.isEditMode());
        assertTrue("Edit mode should be true", ADD_GBV_MINI.isEditMode());
        assertTrue("Edit mode should be true", ADD_GBV_FULL.isEditMode());
        assertTrue("Edit mode should be true", EDIT_MINI.isEditMode());
        assertTrue("Edit mode should be true", EDIT_FULL.isEditMode());

        assertFalse("Edit mode should be false", LIST.isEditMode());
        assertFalse("Edit mode should be false", DETAILS_CP_MINI.isEditMode());
        assertFalse("Edit mode should be false", DETAILS_CP_FULL.isEditMode());
        assertFalse("Edit mode should be false", DETAILS_GBV_MINI.isEditMode());
        assertFalse("Edit mode should be false", DETAILS_GBV_FULL.isEditMode());
        assertFalse("Edit mode should be false", SEARCH.isEditMode());
    }

    @Test
    public void should_return_list_mode() throws Exception {
        assertTrue("List mode should be true", LIST.isListMode());

        assertFalse("List mode should be false", ADD_CP_MINI.isListMode());
        assertFalse("List mode should be false", ADD_CP_FULL.isListMode());
        assertFalse("List mode should be false", ADD_GBV_MINI.isListMode());
        assertFalse("List mode should be false", ADD_GBV_FULL.isListMode());
        assertFalse("List mode should be false", EDIT_MINI.isListMode());
        assertFalse("List mode should be false", EDIT_FULL.isListMode());
        assertFalse("List mode should be false", DETAILS_CP_MINI.isListMode());
        assertFalse("List mode should be false", DETAILS_CP_FULL.isListMode());
        assertFalse("List mode should be false", DETAILS_GBV_MINI.isListMode());
        assertFalse("List mode should be false", DETAILS_GBV_FULL.isListMode());
        assertFalse("List mode should be false", SEARCH.isListMode());
    }

    @Test
    public void should_return_detail_mode() throws Exception {
        assertTrue("Detail mode should be true", DETAILS_CP_MINI.isDetailMode());
        assertTrue("Detail mode should be true", DETAILS_CP_FULL.isDetailMode());
        assertTrue("Detail mode should be true", DETAILS_GBV_MINI.isDetailMode());
        assertTrue("Detail mode should be true", DETAILS_GBV_FULL.isDetailMode());

        assertFalse("Detail mode should be false", LIST.isDetailMode());
        assertFalse("Detail mode should be false", SEARCH.isDetailMode());
        assertFalse("Detail mode should be false", ADD_CP_MINI.isDetailMode());
        assertFalse("Detail mode should be false", ADD_CP_FULL.isDetailMode());
        assertFalse("Detail mode should be false", ADD_GBV_MINI.isDetailMode());
        assertFalse("Detail mode should be false", ADD_GBV_FULL.isDetailMode());
        assertFalse("Detail mode should be false", EDIT_MINI.isDetailMode());
        assertFalse("Detail mode should be false", EDIT_FULL.isDetailMode());
    }

    @Test
    public void should_return_add_mode() throws Exception {
        assertTrue("Add mode should be true", ADD_CP_MINI.isAddMode());
        assertTrue("Add mode should be true", ADD_CP_FULL.isAddMode());
        assertTrue("Add mode should be true", ADD_GBV_MINI.isAddMode());
        assertTrue("Add mode should be true", ADD_GBV_FULL.isAddMode());

        assertFalse("Add mode should be false", LIST.isAddMode());
        assertFalse("Add mode should be false", EDIT_MINI.isAddMode());
        assertFalse("Add mode should be false", EDIT_FULL.isAddMode());
        assertFalse("Add mode should be false", DETAILS_CP_MINI.isAddMode());
        assertFalse("Add mode should be false", DETAILS_CP_FULL.isAddMode());
        assertFalse("Add mode should be false", DETAILS_GBV_MINI.isAddMode());
        assertFalse("Add mode should be false", DETAILS_GBV_FULL.isAddMode());
        assertFalse("Add mode should be false", SEARCH.isAddMode());
    }

    @Test
    public void should_check_whether_feature_is_cp_case() throws Exception {
        assertTrue("CP case should be true", ADD_CP_MINI.isCPCase());
        assertTrue("CP case should be true", ADD_CP_FULL.isCPCase());
        assertTrue("CP case should be true", DETAILS_CP_MINI.isCPCase());
        assertTrue("CP case should be true", DETAILS_CP_FULL.isCPCase());

        assertFalse("CP case should be false", ADD_GBV_MINI.isCPCase());
        assertFalse("CP case should be false", ADD_GBV_FULL.isCPCase());
        assertFalse("CP case should be false", DETAILS_GBV_MINI.isCPCase());
        assertFalse("CP case should be false", DETAILS_GBV_FULL.isCPCase());
    }
}