package org.unicef.rapidreg.common;

import android.content.Context;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.common.injection.component.DaggerTestComponent;
import org.unicef.rapidreg.common.injection.component.TestComponent;
import org.unicef.rapidreg.common.injection.module.TestApplicationModule;

public class TestComponentRule implements TestRule {

    private TestComponent testComponent;
    private final Context context;

    public TestComponentRule(Context context) {
        this.context = context;
        PrimeroApplication application = PrimeroApplication.get(context);
        testComponent = DaggerTestComponent.builder()
                .testApplicationModule(new TestApplicationModule(application))
                .build();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                PrimeroApplication application = PrimeroApplication.get(context);
                application.setApplicationComponent(testComponent);
                base.evaluate();
                application.setApplicationComponent(null);
            }
        };
    }
}
