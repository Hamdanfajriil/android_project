package com.example.lumicotelecommunication;

import static com.example.lumicotelecommunication.Constants.MAX_BYTES_PDF;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lumicotelecommunication.adapters.AdapterPdfAdmin;
import com.example.lumicotelecommunication.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

//class aplikasi berjalan sebelum aktivitas peluncur
public class MyApplication extends Application {

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy",cal).toString();

        return date;
    }

    public static void deleteBook(Context context, String surveyId, String surveyUrl, String surveyTittle) {
        String TAG = "DELETE_SURVEY_TAG";

        Log.d(TAG, "deleteBook: Deleting...");
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Deletting "+surveyTittle+"...");
        progressDialog.show();

        Log.d(TAG, "deleteBook: Deleting From Storage...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(surveyUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleting from storage");

                        Log.d(TAG, "onSuccess: Now deleting info from db");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Surveys");
                        reference.child(surveyId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Deleted from db too");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Delete Survey Successfully", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failed to delete from db due to"+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from storage due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void loadPdfSize(String pdfUrl, String pdfTittle, TextView sizeVw) {
        String TAG = "PDF_SIZE_TAG";
        //mendapatkan file data dari firebase storage


        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //get size di bytes dan convert bytes ke KB dan MB
                        double bytes = storageMetadata.getSizeBytes();
                        Log.d(TAG, "onSucces: "+pdfTittle +" "+bytes);

                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if (mb >= 1) {
                            sizeVw.setText(String.format("%.2f", mb)+" MB");
                        }
                        else if (kb >= 1) {
                            sizeVw.setText(String.format("%.2f", kb)+" KB");
                        }
                        else {
                            sizeVw.setText(String.format("%.2f", bytes)+" bytes");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
    }

    public static void loadPdfFromUrlSingePage(String pdfUrl, String pdfTittle, PDFView pdfView, ProgressBar progressBar) {
        //mendapatkan file data dari firebase storage
        String TAG = "PDF_LOAD_SINGE_TAG";

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+pdfTittle+ " berhasil added file survey");

                        //set to pdfview
                        pdfView.fromBytes(bytes)
                                .pages(0) //menampilkan di page pertama
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onPageError: "+t.getMessage());
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdf loaded");
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onFailure: gagal mendapatkan file dari url karena"+e.getMessage());
                    }
                });
    }

    public static void loadProject(String projectId, TextView projectVw) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Project");
        ref.child(projectId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //set dan get project text view
                        String project = ""+snapshot.child("project").getValue();

                        projectVw.setText(project);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void incrementSurveyViewCount(String surveyId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys");
        ref.child(surveyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();

                        if (viewsCount.equals("") || viewsCount.equals("null")){
                            viewsCount = "0";
                        }

                        long newViewsCount = Long.parseLong(viewsCount) +1;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount", newViewsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Surveys");
                        reference.child(surveyId)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void downloadSuvey(Context context, String surveyId, String surveyTittle, String surveyUrl){
        Log.d(TAG_DOWNLOAD, "downloadSuvey: Downloading file survey...");

        String nameWithExtension = surveyTittle + ".pdf";
        Log.d(TAG_DOWNLOAD, "downloadSuvey: NAME: "+nameWithExtension);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Downloading " +nameWithExtension);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(surveyUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG_DOWNLOAD, "onSuccess: file downloaded");
                        Log.d(TAG_DOWNLOAD, "onSuccess: Saving file...");
                        
                        saveDownloadedSuvey(context, progressDialog, bytes, nameWithExtension, surveyId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to download due to"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private static void saveDownloadedSuvey(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String surveyId) {
        Log.d(TAG_DOWNLOAD, "saveDownloadedSuvey: Saving downloaded file");
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadFolder.mkdir();

            String filePath = downloadFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DOWNLOAD, "saveDownloadedSuvey: Saved to Download Folder");
            progressDialog.dismiss();

            incrementSurveyDownloadCount(surveyId);

        }
        catch (Exception e) {
            Log.d(TAG_DOWNLOAD, "saveDownloadedSuvey: Failed Saving to Download Folder due to");
            Toast.makeText(context, "Failed Saving to Download Folder due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }

    }

    private static void incrementSurveyDownloadCount(String surveyId) {
        Log.d(TAG_DOWNLOAD, "incrementSurveyDownloadCount: Incrementing file Download Count");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys");
        ref.child(surveyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String downloadCount = ""+snapshot.child("downloadCount").getValue();
                        Log.d(TAG_DOWNLOAD, "onDataChange: Download Count");

                        if (downloadCount.equals("") || downloadCount.equals("null")) {
                            downloadCount = "0";
                        }

                        long newDownloadCount = Long.parseLong(downloadCount) + 1;
                        Log.d(TAG_DOWNLOAD, "onDataChange: New Download Count");

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("downloadCount", newDownloadCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Surveys");
                        reference.child(surveyId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG_DOWNLOAD, "onSuccess: Download Count updated");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to update Downloads Count due to"+e.getMessage());
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}
