package org.unicef.rapidreg.common.injection.component;

import org.unicef.rapidreg.common.injection.module.TestApplicationModule;
import org.unicef.rapidreg.injection.component.ApplicationComponent;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = TestApplicationModule.class)
public interface TestComponent extends ApplicationComponent {
}
