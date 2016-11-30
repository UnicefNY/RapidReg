package org.unicef.rapidreg.injection.component;

import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.childcase.caselist.CaseListFragment;
import org.unicef.rapidreg.childcase.caseregister.CaseMiniFormFragment;
import org.unicef.rapidreg.childcase.caseregister.CaseRegisterFragment;
import org.unicef.rapidreg.childcase.caseregister.CaseRegisterWrapperFragment;
import org.unicef.rapidreg.childcase.casesearch.CaseSearchFragment;
import org.unicef.rapidreg.injection.PerFragment;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.sync.SyncFragment;
import org.unicef.rapidreg.tracing.tracinglist.TracingListFragment;
import org.unicef.rapidreg.tracing.tracingregister.TracingMiniFormFragment;
import org.unicef.rapidreg.tracing.tracingregister.TracingRegisterFragment;
import org.unicef.rapidreg.tracing.tracingregister.TracingRegisterWrapperFragment;
import org.unicef.rapidreg.tracing.tracingsearch.TracingSearchFragment;

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
