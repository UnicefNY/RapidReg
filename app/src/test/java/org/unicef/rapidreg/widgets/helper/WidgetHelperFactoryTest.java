package org.unicef.rapidreg.widgets.helper;

import android.app.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class WidgetHelperFactoryTest {
    private Application context = RuntimeEnvironment.application;
    private CaseField field;

    @Before
    public void setUp() throws Exception {
        field = new CaseField();
    }

    @Test
    public void should_get_tick_box_helper() {
        field.setType(Case.TYPE_TICK_BOX);
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);

        assertThat(widgetHelper, instanceOf(TickBoxHelper.class));
    }

    @Test
    public void should_get_separator_helper() {
        field.setType(Case.TYPE_SEPARATOR);
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);

        assertThat(widgetHelper, instanceOf(SeparatorHelper.class));
    }

    @Test
    public void should_get_generic_helper() {
        field.setType(Case.TYPE_SELECT_BOX);
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);
        assertThat(widgetHelper, instanceOf(GenericHelper.class));

        field.setType(Case.TYPE_TEXT_FIELD);
        widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);
        assertThat(widgetHelper, instanceOf(GenericHelper.class));

    }
}
