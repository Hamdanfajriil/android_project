package com.example.lumicotelecommunication.filters;

import android.widget.Filter;

import com.example.lumicotelecommunication.adapters.AdapterProject;
import com.example.lumicotelecommunication.models.ModelProject;

import java.util.ArrayList;

public class FilterProject extends Filter {

    ArrayList<ModelProject> filterList;
    AdapterProject adapterProject;

    public FilterProject(ArrayList<ModelProject> filterList, AdapterProject adapterProject) {
        this.filterList = filterList;
        this.adapterProject = adapterProject;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelProject> filterModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++) {
                //validate
                if (filterList.get(i).getProject().toUpperCase().contains(constraint)) {
                    filterModels.add(filterList.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {

        adapterProject.projectArrayList = (ArrayList<ModelProject>)results.values;
        //notify
        adapterProject.notifyDataSetChanged();
    }
}
