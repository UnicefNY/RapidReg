package org.unicef.rapidreg.childcase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.rapidreg.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseListAdapter extends RecyclerView.Adapter<CaseListAdapter.CaseListHolder>{

    private List<Map<String, String>> caseList;

    public CaseListAdapter() {
        caseList = new ArrayList<>();
        initCaseListData();
    }

    @Override
    public CaseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.case_list_layout, parent, false);
        return new CaseListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CaseListHolder holder, int position) {
        Map<String, String> caseInfo = caseList.get(position);
        holder.caseTitle.setText(caseInfo.get("title"));
        holder.caseChildGender.setText(caseInfo.get("gender"));
        holder.caseChildAge.setText(caseInfo.get("age"));
//        holder.caseTitle.setText(caseInfo.get("title"));
    }

    @Override
    public int getItemCount() {
        return caseList.size();
    }

    private void initCaseListData() {
        Map<String, String> case1 = new HashMap<>();
        case1.put("title", "10xaby35");
        case1.put("gender", "BOY");
        case1.put("age", "7");
        case1.put("image", "case_list_img_holder.jpg");

        Map<String, String> case2 = new HashMap<>();
        case2.put("title", "2rqw768d");
        case2.put("gender", "BOY");
        case2.put("age", "8");
        case2.put("image", "case_list_img_holder.jpg");

        Map<String, String> case3 = new HashMap<>();
        case3.put("title", "3jvoip90");
        case3.put("gender", "GIRL");
        case3.put("age", "8");
        case3.put("image", "case_list_img_holder.jpg");

        Map<String, String> case4 = new HashMap<>();
        case4.put("title", "4809s8fp");
        case4.put("gender", "BOY");
        case4.put("age", "9");
        case4.put("image", "case_list_img_holder.jpg");

        Map<String, String> case5 = new HashMap<>();
        case5.put("title", "5rqw768d");
        case5.put("gender", "BOY");
        case5.put("age", "8");
        case5.put("image", "case_list_img_holder.jpg");

        Map<String, String> case6 = new HashMap<>();
        case6.put("title", "6jvoip90");
        case6.put("gender", "GIRL");
        case6.put("age", "8");
        case6.put("image", "case_list_img_holder.jpg");

        Map<String, String> case7 = new HashMap<>();
        case7.put("title", "7809s8fp");
        case7.put("gender", "BOY");
        case7.put("age", "9");
        case7.put("image", "case_list_img_holder.jpg");

        caseList.add(case1);
        caseList.add(case2);
        caseList.add(case3);
        caseList.add(case4);
        caseList.add(case5);
        caseList.add(case6);
        caseList.add(case7);
    }

    public static class CaseListHolder extends RecyclerView.ViewHolder {
        protected TextView caseTitle;
        protected TextView caseChildGender;
        protected TextView caseChildAge;
        protected ImageView caseImage;

        public CaseListHolder(View itemView) {
            super(itemView);
            caseTitle = (TextView) itemView.findViewById(R.id.case_title);
            caseChildGender = (TextView) itemView.findViewById(R.id.case_child_gender);
            caseChildAge = (TextView) itemView.findViewById(R.id.case_child_age);
            caseImage = (ImageView) itemView.findViewById(R.id.case_image);
        }
    }
}
