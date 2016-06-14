package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseService;
import java.lang.reflect.Type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaseListAdapter extends RecyclerView.Adapter<CaseListAdapter.CaseListHolder>{

    private List<Case> caseList;
    private CaseService caseService;
    private Context context;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public CaseListAdapter() {
    }

    public CaseListAdapter(Context context) {
        this.context = context;
        caseList = new ArrayList<>();
        caseList = getAllCaseData();
    }

    @Override
    public CaseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.case_list_layout, parent, false);
        return new CaseListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CaseListHolder holder, int position) {
        Case caseItem = caseList.get(position);
        String caseJson = new String(caseItem.getContent().getBlob());
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        final Map<String, String> caseInfo = new Gson().fromJson(caseJson, type);
        holder.caseTitle.setText(caseItem.getUniqueId().substring(0, 7));
        holder.caseChildGender.setText(caseInfo.get("Sex"));
        holder.caseChildAge.setText(caseInfo.get("Age"));
        holder.caseCreateTime.setText(formatter.format(caseItem.getCreateAt()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseService.CaseValues.setValues(caseInfo);
                ((BaseActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, new CaseRegisterWrapperFragment())
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return caseList.size();
    }

    private List<Case> getAllCaseData() {
        caseService = CaseService.getInstance();
        return caseService.getCaseList();
    }

    public static class CaseListHolder extends RecyclerView.ViewHolder {
        protected TextView caseTitle;
        protected TextView caseChildGender;
        protected TextView caseChildAge;
        protected TextView caseCreateTime;
        protected ImageView caseImage;
        protected View view;

        public CaseListHolder(View itemView) {
            super(itemView);
            view = itemView;
            caseTitle = (TextView) itemView.findViewById(R.id.case_title);
            caseChildGender = (TextView) itemView.findViewById(R.id.case_child_gender);
            caseChildAge = (TextView) itemView.findViewById(R.id.case_child_age);
            caseCreateTime = (TextView) itemView.findViewById(R.id.case_create_time);
            caseImage = (ImageView) itemView.findViewById(R.id.case_image);
        }
    }
}
