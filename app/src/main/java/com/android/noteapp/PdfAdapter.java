package com.android.noteapp;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfViewHolder> {
    private List<Pdf> pdfFiles;
    private OnItemClickListener clickListener;

    public PdfAdapter(List<Pdf> pdfFiles) {
        this.pdfFiles = pdfFiles;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recycleview, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        Pdf pdf = pdfFiles.get(position);
        holder.txtFileName.setText(pdf.getFileName());
        holder.txtUploader.setText(pdf.getUploader());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the pdf using the fileUrl
                String fileUrl = pdf.getFileUrl();

                // Create an Intent object to open the PDF file
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }

    public void searchDataList(ArrayList<Pdf> searchFiles){
        pdfFiles = searchFiles;
        notifyDataSetChanged();
    }

    public void updatePdfFiles(List<Pdf> pdfFiles) {
        this.pdfFiles = pdfFiles;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String fileName);
    }


    class PdfViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtFileName;
        TextView txtUploader;

        PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFileName = itemView.findViewById(R.id.name);
            txtUploader = itemView.findViewById(R.id.txtUploadBy);
            itemView.setOnClickListener(this);
        }


        void bind(String fileName, String uploader) {
            txtFileName.setText(fileName);
            txtUploader.setText(uploader);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                Pdf pdf = pdfFiles.get(position);
                String fileName = pdf.getFileUrl();
                clickListener.onItemClick(v, fileName);
            }
        }
    }
}
