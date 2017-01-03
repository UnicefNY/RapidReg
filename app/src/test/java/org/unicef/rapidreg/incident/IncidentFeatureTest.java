package org.unicef.rapidreg.incident;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.R;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.unicef.rapidreg.incident.IncidentFeature.ADD_FULL;
import static org.unicef.rapidreg.incident.IncidentFeature.ADD_MINI;
import static org.unicef.rapidreg.incident.IncidentFeature.DETAILS_FULL;
import static org.unicef.rapidreg.incident.IncidentFeature.DETAILS_MINI;
import static org.unicef.rapidreg.incident.IncidentFeature.EDIT_FULL;
import static org.unicef.rapidreg.incident.IncidentFeature.EDIT_MINI;
import static org.unicef.rapidreg.incident.IncidentFeature.LIST;
import static org.unicef.rapidreg.incident.IncidentFeature.SEARCH;

@RunWith(PowerMockRunner.class)
public class IncidentFeatureTest {

    @Test
    public void should_return_title_id_correctly() throws Exception {
        assertThat("Title id should be Incidents", LIST.getTitleId(), is(R.string.incidents));
        assertThat("Title id should be New Incident", ADD_MINI.getTitleId(), is(R.string
                .new_incident));
        assertThat("Title id should be New Incident", ADD_FULL.getTitleId(), is(R.string
                .new_incident));
        assertThat("Title id should be Edit", EDIT_MINI.getTitleId(), is(R.string.edit));
        assertThat("Title id should be Edit", EDIT_FULL.getTitleId(), is(R.string.edit));
        assertThat("Title id should be incident details", DETAILS_MINI.getTitleId(), is(R
                .string.incident_details));
        assertThat("Title id should be incident details", DETAILS_FULL.getTitleId(), is(R
                .string.incident_details));
        assertThat("Title id should be Search", SEARCH.getTitleId(), is(R.string.search));
    }

    @Test
    public void should_return_fragment_correctly() throws Exception {
        assertThat("Fragment should be IncidentListFragment", LIST.getFragment().getClass()
                .getSimpleName(), is("IncidentListFragment"));
        assertThat("Fragment should be IncidentMiniFormFragment", ADD_MINI.getFragment().getClass
                ().getSimpleName(), is("IncidentMiniFormFragment"));
        assertThat("Fragment should be IncidentMiniFormFragment", EDIT_MINI.getFragment().getClass()
                .getSimpleName(), is("IncidentMiniFormFragment"));
        assertThat("Fragment should be IncidentMiniFormFragment", DETAILS_MINI.getFragment()
                .getClass().getSimpleName(), is("IncidentMiniFormFragment"));
        assertThat("Fragment should be IncidentRegisterWrapperFragment", ADD_FULL.getFragment()
                .getClass().getSimpleName(), is("IncidentRegisterWrapperFragment"));
        assertThat("Fragment should be IncidentRegisterWrapperFragment", EDIT_FULL.getFragment()
                .getClass().getSimpleName(), is("IncidentRegisterWrapperFragment"));
        assertThat("Fragment should be IncidentRegisterWrapperFragment", DETAILS_FULL.getFragment
                ().getClass().getSimpleName(), is("IncidentRegisterWrapperFragment"));
        assertThat("Fragment should be IncidentSearchFragment", SEARCH.getFragment().getClass()
                .getSimpleName(), is("IncidentSearchFragment"));
    }

    @Test
    public void should_return_edit_mode() throws Exception {
        assertTrue("Edit mode should be true", ADD_MINI.isEditMode());
        assertTrue("Edit mode should be true", ADD_FULL.isEditMode());
        assertTrue("Edit mode should be true", EDIT_MINI.isEditMode());
        assertTrue("Edit mode should be true", EDIT_FULL.isEditMode());

        assertFalse("Edit mode should be false", LIST.isEditMode());
        assertFalse("Edit mode should be false", DETAILS_MINI.isEditMode());
        assertFalse("Edit mode should be false", DETAILS_FULL.isEditMode());
        assertFalse("Edit mode should be false", SEARCH.isEditMode());
    }

    @Test
    public void should_return_list_mode() throws Exception {
        assertTrue("List mode should be true", LIST.isListMode());

        assertFalse("List mode should be false", ADD_MINI.isListMode());
        assertFalse("List mode should be false", ADD_FULL.isListMode());
        assertFalse("List mode should be false", EDIT_MINI.isListMode());
        assertFalse("List mode should be false", EDIT_FULL.isListMode());
        assertFalse("List mode should be false", DETAILS_MINI.isListMode());
        assertFalse("List mode should be false", DETAILS_FULL.isListMode());
        assertFalse("List mode should be false", SEARCH.isListMode());
    }

    @Test
    public void should_return_detail_mode() throws Exception {
        assertTrue("Detail mode should be true", DETAILS_MINI.isDetailMode());
        assertTrue("Detail mode should be true", DETAILS_FULL.isDetailMode());

        assertFalse("Detail mode should be false", LIST.isDetailMode());
        assertFalse("Detail mode should be false", SEARCH.isDetailMode());
        assertFalse("Detail mode should be false", ADD_MINI.isDetailMode());
        assertFalse("Detail mode should be false", ADD_FULL.isDetailMode());
        assertFalse("Detail mode should be false", EDIT_MINI.isDetailMode());
        assertFalse("Detail mode should be false", EDIT_FULL.isDetailMode());
    }

    @Test
    public void should_return_add_mode() throws Exception {
        assertTrue("Add mode should be true", ADD_MINI.isAddMode());
        assertTrue("Add mode should be true", ADD_FULL.isAddMode());

        assertFalse("Add mode should be false", LIST.isAddMode());
        assertFalse("Add mode should be false", EDIT_MINI.isAddMode());
        assertFalse("Add mode should be false", EDIT_FULL.isAddMode());
        assertFalse("Add mode should be false", DETAILS_MINI.isAddMode());
        assertFalse("Add mode should be false", DETAILS_FULL.isAddMode());
        assertFalse("Add mode should be false", SEARCH.isAddMode());
    }

}