package org.unicef.rapidreg.forms;

import java.util.List;

public interface RecordForm {
    List<Section> getSections();

    void setSections(List<Section> sections);
}
