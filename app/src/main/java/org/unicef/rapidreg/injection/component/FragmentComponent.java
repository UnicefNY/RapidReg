package org.unicef.rapidreg.injection.component;

import org.unicef.rapidreg.base.RecordListFragment;
import org.unicef.rapidreg.base.RecordRegisterFragment;
import org.unicef.rapidreg.base.RecordSearchFragment;
import org.unicef.rapidreg.childcase.CaseListFragment;
import org.unicef.rapidreg.childcase.CaseMiniFormFragment;
import org.unicef.rapidreg.childcase.CaseRegisterFragment;
import org.unicef.rapidreg.childcase.CaseRegisterWrapperFragment;
import org.unicef.rapidreg.childcase.CaseSearchFragment;
import org.unicef.rapidreg.injection.PerFragment;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.sync.SyncFragment;
import org.unicef.rapidreg.tracing.TracingListFragment;
import org.unicef.rapidreg.tracing.TracingMiniFormFragment;
import org.unicef.rapidreg.tracing.TracingRegisterFragment;
import org.unicef.rapidreg.tracing.TracingRegisterWrapperFragment;
import org.unicef.rapidreg.tracing.TracingSearchFragment;

import dagger.Component;

@PerFragment
@Component(dependencies = {ApplicationComponent.class}, modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(CaseListFragment caseListFragment);

    void inject(CaseMiniFormFragment caseMiniFormFragment);

    void inject(CaseRegisterFragment caseRegisterFragment);

    void inject(CaseRegisterWrapperFragment caseRegisterWrapperFragment);

    void inject(CaseSearchFragment caseSearchFragment);

    void inject(RecordListFragment recordListFragment);

    void inject(RecordRegisterFragment recordRegisterFragment);

    void inject(RecordSearchFragment recordSearchFragment);

    void inject(SyncFragment syncFragment);

    void inject(TracingListFragment tracingListFragment);

    void inject(TracingMiniFormFragment tracingMiniFormFragment);

    void inject(TracingRegisterFragment tracingRegisterFragment);

    void inject(TracingRegisterWrapperFragment tracingRegisterWrapperFragment);

    void inject(TracingSearchFragment tracingSearchFragment);

}
