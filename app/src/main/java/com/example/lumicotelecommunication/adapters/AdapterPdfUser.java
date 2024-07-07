package com.example.lumicotelecommunication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumicotelecommunication.MyApplication;
import com.example.lumicotelecommunication.activities.PdfDetailActivity;
import com.example.lumicotelecommunication.databinding.RowPdfUserBinding;
import com.example.lumicotelecommunication.filters.FilterPdfUser;
import com.example.lumicotelecommunication.models.ModelPdf;
import com.example.lumicotelecommunication.models.ModelProject;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {

    private Context context;
    public ArrayList<ModelPdf> pdfArrayList, filterList;
    private FilterPdfUser filter;

    private RowPdfUserBinding binding;
    private static final String TAG = "ADAPTER_PDF_USER_TAG";

    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {

        //get data
        ModelPdf model = pdfArrayList.get(position);
        String surveyId = model.getId();
        String tittle = model.getName_site();
        String tglSur = model.getTanggal_survei();
        String pdfUrl = model.getUrl();
        String projectId = model.getProjectId();
        long timestamp = model.getTimestamp();
        String date = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.siteName.setText(tittle);
        holder.tglSrv.setText(tglSur);
        holder.dateVw.setText(date);

        MyApplication.loadPdfFromUrlSingePage(
                ""+pdfUrl,
                ""+tittle,
                holder.pdfView,
                holder.progressBar
        );
        MyApplication.loadProject(
                ""+projectId,
                holder.projectVw
        );
        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+tittle,
                holder.sizeVw
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("surveyId", surveyId);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPdfUser(filterList, this);
        }

        return filter;
    }

    class HolderPdfUser extends RecyclerView.ViewHolder{

        TextView siteName, tglSrv, projectVw, sizeVw, dateVw;
        PDFView pdfView;
        ProgressBar progressBar;

        public HolderPdfUser(View itemView) {
            super(itemView);

            siteName = binding.siteName;
            tglSrv = binding.tglSrv;
            projectVw = binding.projectVw;
            sizeVw = binding.sizeVw;
            dateVw = binding.dateVw;
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;

        }
    }
}
