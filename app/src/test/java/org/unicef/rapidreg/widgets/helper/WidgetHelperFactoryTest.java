package org.unicef.rapidreg.widgets.helper;

import android.app.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.unicef.rapidreg.forms.childcase.CaseField;

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
        field.setType(CaseField.FieldType.TICK_BOX.name());
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);

        assertThat(widgetHelper, instanceOf(TickBoxHelper.class));
    }

    @Test
    public void should_get_separator_helper() {
        field.setType(CaseField.FieldType.SEPARATOR.name());
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);

        assertThat(widgetHelper, instanceOf(SeparatorHelper.class));
    }

    @Test
    public void should_get_generic_helper() {
        field.setType(CaseField.FieldType.DATE_FIELD.name());
        WidgetHelper widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);
        assertThat(widgetHelper, instanceOf(GenericHelper.class));

        field.setType(CaseField.FieldType.RADIO_BUTTON.name());
        widgetHelper = WidgetHelperFactory.getWidgetHelper(context, field);
        assertThat(widgetHelper, instanceOf(GenericHelper.class));

    }
}
