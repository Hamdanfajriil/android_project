package com.example.lumicotelecommunication.filters;

import android.widget.Filter;

import com.example.lumicotelecommunication.adapters.AdapterPdfUser;
import com.example.lumicotelecommunication.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {

    ArrayList<ModelPdf> filterList;
    AdapterPdfUser adapterPdfUser;


    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence!=null || charSequence.length() > 0){

            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModel = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++){
                if (filterList.get(i).getName_site().toUpperCase().contains(charSequence)){
                    filteredModel.add(filterList.get(i));
                }
            }

            results.count = filteredModel.size();
            results.values = filteredModel;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {

        adapterPdfUser.pdfArrayList = (ArrayList<ModelPdf>)results.values;

        adapterPdfUser.notifyDataSetChanged();
    }
}
