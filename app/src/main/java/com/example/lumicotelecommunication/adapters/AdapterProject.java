package com.example.lumicotelecommunication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumicotelecommunication.databinding.RowProjectBinding;
import com.example.lumicotelecommunication.filters.FilterProject;
import com.example.lumicotelecommunication.activities.PdfListAdminActivity;
import com.example.lumicotelecommunication.models.ModelProject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AdapterProject extends RecyclerView.Adapter<AdapterProject.HolderProject> implements Filterable {

    private Context context;
    public ArrayList<ModelProject> projectArrayList, filterList;
    private RowProjectBinding binding;

    private FilterProject filter;

    public AdapterProject(Context context, ArrayList<ModelProject> projectArrayList) {
        this.context = context;
        this.projectArrayList = projectArrayList;
        this.filterList = projectArrayList;
    }

    @NonNull
    @Override
    public HolderProject onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowProjectBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderProject(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProject holder, int position) {
        ModelProject model = projectArrayList.get(position);
        String id = model.getId();
        String project = model.getProject();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.tittle.setText(project);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+project, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Apakah ingin menghapus data ini?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                                deleteProject(model, holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        //handle item click, goto PdfListAdminActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PdfListAdminActivity.class);
                intent.putExtra("projectId", id);
                intent.putExtra("projectSiteName", project);
                context.startActivity(intent);
            }
        });
    }

    private void deleteProject(ModelProject model, HolderProject holder) {

        String id = model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Project");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //sukses
                        Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //gagal
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return projectArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProject(filterList, this);
        }
        return filter;
    }

    class HolderProject extends RecyclerView.ViewHolder {

        TextView tittle;
        ImageButton deleteBtn;

        public HolderProject(@NonNull View itemView) {
            super(itemView);

            //init ui views
            tittle = binding.projectVw;
            deleteBtn = binding.deleteBtn;
        }
    }
}
