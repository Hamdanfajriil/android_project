package com.example.lumicotelecommunication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lumicotelecommunication.models.Dataclass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class LaporanProposal extends AppCompatActivity {

    private TextView nProj;
    private EditText alamat, lattitude, longtitude, jtower, ttower, plokasi, nP, sI, sN,NP, tglS;
    private ImageButton Uploudimage;
    private Button btnsubmit, btnprint;
    private FirebaseFirestore db;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private Uri uri;
    private String imegeURL;

    @SuppressLint("MissingInflatedId")

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_proposal);

        alamat = findViewById(R.id.alamat);
        lattitude = findViewById(R.id.Laparameter);
        longtitude = findViewById(R.id.Loparameter);
        jtower = findViewById(R.id.jenisTower);
        ttower = findViewById(R.id.tinggiTower);
        plokasi = findViewById(R.id.uSingkat);
        nP = findViewById(R.id.nameProject);
        sI = findViewById(R.id.siteId);
        sN = findViewById(R.id.siteName);
        NP = findViewById(R.id.NoPo);
        tglS = findViewById(R.id.tglSur);
        Uploudimage = findViewById(R.id.btnuploud);
        db = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(LaporanProposal.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Menyimpan....");
        progressDialog.setCancelable(false);

        btnsubmit = findViewById(R.id.btnsubmit);
        btnsubmit.setOnClickListener(view -> {
            submitdata();
            saveimage();
            finish();
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK)
                        {
                            Intent data = result.getData();
                            uri = data.getData();
                            Uploudimage.setImageURI(uri);
                        } else  {
                            Toast.makeText(LaporanProposal.this, "No", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Uploudimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicher = new Intent(Intent.ACTION_PICK);
                photoPicher.setType("image/*");
                activityResultLauncher.launch(photoPicher);
            }
        });
    }

    private void saveimage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Image").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(LaporanProposal.this);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlimage = uriTask.getResult();
                imegeURL = urlimage.toString();
                submitdata();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });

    }

    private void submitdata() {
        String nameproject = nP.getText().toString();
        String siteid = sI.getText().toString();
        String sitename = sN.getText().toString();
        String noPo = NP.getText().toString();
        String tglsurvei = tglS.getText().toString();
        String aalamat = alamat.getText().toString();
        String latt = lattitude.getText().toString();
        String longt = longtitude.getText().toString();
        String jenist = jtower.getText().toString();
        String tinggit = ttower.getText().toString();
        String lokasi = plokasi.getText().toString();

        Dataclass project = new Dataclass(aalamat, latt, longt, tinggit, jenist, lokasi,
                nameproject,siteid,sitename,noPo,tglsurvei);

        FirebaseDatabase.getInstance().getReference().child(nameproject)
                .setValue(project);
        db.collection("Project").document(nameproject).set(project)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LaporanProposal.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LaporanProposal.this, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}