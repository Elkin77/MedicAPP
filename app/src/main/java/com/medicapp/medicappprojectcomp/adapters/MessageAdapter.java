package com.medicapp.medicappprojectcomp.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.databinding.AdapterMessageBinding;
import com.medicapp.medicappprojectcomp.models.Message;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private String uuid;
    public static final String TAG = MessageAdapter.class.getName();
    private final static String MESSAGE_DATE_FORMAT = "dd MMMM yyyy h:ma";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MESSAGE_DATE_FORMAT);
    Context context;

    public MessageAdapter(List<Message> messagesData, String uuid, Context context) {
        this.messages=messagesData;
        this.uuid=uuid;
        this.context=context;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        //Grab the person to render
        Message message = messages.get(position);
        //Format and Set the values in the view
        holder.binding.messageUser.setText(String.format("   %s :", message.getFrom()));
        holder.binding.messageContent.setText(message.getMessage());
        if(message.isHasImage()) {
            holder.binding.messageImage.setVisibility(View.VISIBLE);
            Glide.with(holder.binding.messageImage.getContext())
                    .load(message.getImagePath())
                    .fitCenter()
                    .transition(withCrossFade())
                    .into(holder.binding.messageImage);

            holder.binding.messageLink.setVisibility(View.GONE);
        }else if(message.isHasFile()) {
            holder.binding.messageLink.setText("Descargar Archivo ");
            holder.binding.messageLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFile(message);
                }
            });
            holder.binding.messageImage.setVisibility(View.GONE);
        }else {
            holder.binding.messageImage.setVisibility(View.GONE);
            holder.binding.messageLink.setVisibility(View.GONE);
        }
        holder.binding.messageDate.setText(simpleDateFormat.format(message.getStamp()));
        Drawable iconDrawable = holder.binding.userImage.getDrawable();
        Drawable mutableDrawable = DrawableCompat.wrap(iconDrawable).mutate();

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        ConstraintLayout.LayoutParams layoutParamsMedic = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParamsMedic.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.binding.materialCard.getLayoutParams();
        if (Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail(), message.getFrom())) {
            holder.binding.materialCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.binding.materialCard.setStrokeColor(Color.parseColor("#060B37"));
            holder.binding.userImage.setColorFilter(Color.parseColor("#0E1555"));;
            holder.binding.dateInfoUser.setLayoutParams(layoutParams);
            DrawableCompat.setTint(mutableDrawable,  Color.parseColor("#C2CADA"));
            params.leftMargin=90;
            holder.binding.materialCard.setLayoutParams(params);
        } else {
            DrawableCompat.setTint(mutableDrawable, Color.parseColor("#BFDFDF"));
            holder.binding.userImage.setColorFilter(Color.parseColor("#FF018786"));;
            holder.binding.materialCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"));;
            holder.binding.messageUser.setBackgroundColor(Color.parseColor("#FF018786"));;
            holder.binding.materialCard.setStrokeColor(Color.parseColor("#FF018786"));
            holder.binding.dateInfoUser.setLayoutParams(layoutParamsMedic);
            params.rightMargin=90;
            holder.binding.materialCard.setLayoutParams(params);
        }
        holder.binding.userImage.setImageDrawable(mutableDrawable);
    }

    private void loadFile(Message message) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String url=message.getImagePath();
        StorageReference fileRef = storage.getReference().child(message.getImagePath());
        File storageDir = this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Crea un intent con la acción de descargar
                String downloadUrl = uri.toString();

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url); // Cambia "archivo.pdf" por el nombre que desees para el archivo descargado
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ocurrió un error al obtener la URL de descarga del archivo
                // Maneja el error de acuerdo a tus necesidades
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AdapterMessageBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterMessageBinding.bind(itemView);
        }
    }

}
