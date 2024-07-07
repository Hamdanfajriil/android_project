package com.example.lumicotelecommunication.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumicotelecommunication.MyApplication;
import com.example.lumicotelecommunication.activities.PdfDetailActivity;
import com.example.lumicotelecommunication.activities.PdfEditActivity;
import com.example.lumicotelecommunication.databinding.RowPdfAdminBinding;
import com.example.lumicotelecommunication.filters.FilterPdfAdmin;
import com.example.lumicotelecommunication.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private Context context;

    public ArrayList<ModelPdf> pdfArrayList, filterList;

    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filter;

    private static final String TAG = "PDF_ADAPTER_TAG";

    private ProgressDialog progressDialog;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        //init progress diaolog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context));
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {

        //get data from model
        ModelPdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String projectId = model.getProjectId();
        String siteName = model.getName_site();
        String tglSrv = model.getTanggal_survei();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        //convert timestamp to dd/MM/yyyy
        String formattedDate = MyApplication.formatTimestamp(timestamp);
        holder.siteName.setText(siteName);
        holder.tglSrv.setText(tglSrv);
        holder.dateVw.setText(formattedDate);


        //load lebih lanjut yang ada di row pdf admin
        MyApplication.loadProject(
                ""+projectId,
                holder.projectVw
        );
        MyApplication.loadPdfFromUrlSingePage(
                ""+pdfUrl,
                ""+siteName,
                holder.pdfView,
                holder.progressBar
        );
        MyApplication.loadPdfSize(
                ""+pdfUrl,
                ""+siteName,
                holder.sizeVw
        );

        //handle action more button
        holder.moreVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionDialog(model, holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("surveyId", pdfId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionDialog(ModelPdf model, HolderPdfAdmin holder) {
        String surveyId = model.getId();
        String surveyUrl = model.getUrl();
        String surveyTittle = model.getName_site();
        String[] option = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pilih Opsi")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle dialog option
                        if (i==0){
                            //clik edit
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("surveyId", surveyId);
                            context.startActivity(intent);
                        }
                        else if (i==1) {
                            //clik delete
                            MyApplication.deleteBook(
                                    context,
                                    ""+surveyId,
                                    ""+surveyUrl,
                                    ""+surveyTittle
                            );
                        }
                    }
                })
                .show();
        
    }



    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterPdfAdmin(filterList, this);
        }
        return filter;
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder {

        PDFView pdfView;
        ProgressBar progressBar;
        TextView siteName, tglSrv, projectVw, sizeVw, dateVw;
        ImageButton moreVw;


        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            siteName = binding.siteName;
            tglSrv = binding.tglSrv;
            projectVw = binding.projectVw;
            sizeVw = binding.sizeVw;
            dateVw = binding.dateVw;
            moreVw = binding.moreVw;

        }
    }
}
