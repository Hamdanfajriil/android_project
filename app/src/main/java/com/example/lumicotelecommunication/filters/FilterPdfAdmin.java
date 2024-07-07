package com.example.lumicotelecommunication.filters;

import android.widget.Filter;

import com.example.lumicotelecommunication.adapters.AdapterPdfAdmin;
import com.example.lumicotelecommunication.adapters.AdapterProject;
import com.example.lumicotelecommunication.models.ModelPdf;
import com.example.lumicotelecommunication.models.ModelProject;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {

    ArrayList<ModelPdf> filterList;
    AdapterPdfAdmin adapterPdfAdmin;

    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++) {
                //validate
                if (filterList.get(i).getName_site().toUpperCase().contains(constraint)) {
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

        adapterPdfAdmin.pdfArrayList = (ArrayList<ModelPdf>)results.values;
        //notify
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
