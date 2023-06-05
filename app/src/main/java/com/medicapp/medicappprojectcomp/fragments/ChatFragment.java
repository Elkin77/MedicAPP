 package com.medicapp.medicappprojectcomp.fragments;

 import android.app.Activity;
 import android.content.Intent;
 import android.net.Uri;
 import android.os.Build;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.annotation.RequiresApi;
 import androidx.fragment.app.Fragment;
 import androidx.recyclerview.widget.LinearLayoutManager;

 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.database.ChildEventListener;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.google.firebase.storage.FirebaseStorage;
 import com.google.firebase.storage.StorageReference;
 import com.medicapp.medicappprojectcomp.activities.MainActivity;
 import com.medicapp.medicappprojectcomp.activities.PrincipalPatientActivity;
 import com.medicapp.medicappprojectcomp.adapters.MessageAdapter;
 import com.medicapp.medicappprojectcomp.databinding.FragmentChatBinding;
 import com.medicapp.medicappprojectcomp.models.Message;
 import com.medicapp.medicappprojectcomp.servicies.CameraService;
 import com.medicapp.medicappprojectcomp.servicies.FileServices;
 import com.medicapp.medicappprojectcomp.servicies.NotificationService;
 import com.medicapp.medicappprojectcomp.servicies.PermissionService;
 import com.medicapp.medicappprojectcomp.utils.DatabaseRoutes;

 import java.time.LocalDate;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;

 import javax.inject.Inject;

 import dagger.hilt.android.AndroidEntryPoint;

 @AndroidEntryPoint
 public class ChatFragment extends Fragment {

 FragmentChatBinding binding;
 public static final String TAG = MainActivity.class.getName();

 FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
 FirebaseDatabase database = FirebaseDatabase.getInstance();
 @Inject
 CameraService cameraService;
 @Inject
 FileServices fileService;
 @Inject
 PermissionService permissionHelper;

 FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
 FirebaseStorage mStorage = FirebaseStorage.getInstance();
 DatabaseReference mReference = null;
 ChildEventListener mValueEventListener;
 MessageAdapter adapter;

 List<Message> messages = new ArrayList<>();
 FirebaseUser user;

 public ChatFragment() {
  // Required empty public constructor
 }
  @RequiresApi(api = Build.VERSION_CODES.O)
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
   binding = FragmentChatBinding.inflate(getLayoutInflater());
   validUser();
   mReference=mDatabase.getReference(DatabaseRoutes.MESSAGES_PATH+"/"+user.getUid()+"/"+ LocalDate.now());
   adapter = new MessageAdapter(messages,user.getUid(),getContext());
   binding.viewChat.setHasFixedSize(true);
   LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
   binding.viewChat.setLayoutManager(layoutManager);
   binding.viewChat.setAdapter(adapter);

   mReference.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
     for (DataSnapshot child : snapshot.getChildren()) {
      Message message = child.getValue(Message.class);
      if (!messages.contains(message))
       messages.add(message);
     }
     adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
     Log.e(TAG, "onCancelled: ", error.toException());
    }
   });

   mValueEventListener = new ChildEventListener() {
    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
     Message message = snapshot.getValue(Message.class);
     if (!messages.contains(message)) {
      messages.add(message);
      adapter.notifyItemInserted(messages.size() - 1);
      binding.viewChat.scrollToPosition(messages.size() - 1);
     }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
   };


   //Message event
   binding.buttonSend.setOnClickListener(view -> {
    String message = binding.textMessage.getEditText().getText().toString();
    if (!message.isEmpty()) {
     binding.textMessage.getEditText().getText().clear();
     String key = mReference.push().getKey();
     DatabaseReference newMessageReference = mDatabase.getReference(DatabaseRoutes.getMessage(key, user.getUid()));
     Message msgToSend = new Message(key, message, false, null, firebaseAuth.getCurrentUser().getEmail(), (new Date().getTime()), false);
     newMessageReference.setValue(msgToSend);
    } else {
     binding.textMessage.setError("El mensaje no puede estar vacio.");
    }

   });
   //Camera Event
   binding.buttonAttach.setOnClickListener(view -> {
    if (!binding.textMessage.getEditText().getText().toString().isEmpty()) {
     if (permissionHelper.isMCameraPermissionGranted()) {
      cameraService.startCamera(this);
     } else {
      permissionHelper.getCameraPermission(getActivity());
      // alertsHelper.shortSimpleSnackbar(binding.getRoot(), "Looks like we don't have camera permission.");
     }
    } else {
     binding.textMessage.setError("El mensaje no puede estar vacio.");
    }
   });
   // File Event
   binding.buttonFile.setOnClickListener(view -> {
    if (!binding.textMessage.getEditText().getText().toString().isEmpty()) {
     if (permissionHelper.isMReadExternalStoragePermissionGranted()) {
       fileService.startFiles(this);
     } else {
      permissionHelper.getReadExternalStoragePermission(getActivity());
      // alertsHelper.shortSimpleSnackbar(binding.getRoot(), "Looks like we don't have camera permission.");
     }
    } else {
     binding.textMessage.setError("El mensaje no puede estar vacio.");
    }
   });

   //validateRoute();
   return binding.getRoot();

  }
 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
 }

 @Override
 public void onResume() {
  super.onResume();
  validUser();
  Intent intent = new Intent(getContext(), NotificationService.class);
  NotificationService.enqueueWork(getContext(), intent);
  if (!permissionHelper.isMCameraPermissionGranted())
   permissionHelper.getCameraPermission(getActivity());
   mReference.addChildEventListener(mValueEventListener);
 }

 @Override
 public void onPause() {
  super.onPause();
  mReference.removeEventListener(mValueEventListener);
 }

 @Override
 public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
 }


 @RequiresApi(api = Build.VERSION_CODES.O)
 @Override
 public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  StorageReference mStorageReference;
  if (resultCode == Activity.RESULT_OK) {
   switch (requestCode) {
    case PermissionService.PERMISSIONS_REQUEST_CAMERA:
     mStorageReference = mStorage.getReference(DatabaseRoutes.getMessage(cameraService.getPhotoName(), user.getUid()));
     mStorageReference.putFile(cameraService.getPhotoURI()).addOnSuccessListener(taskSnapshot -> {
      mStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {
       String key = mReference.push().getKey();
       DatabaseReference newMessageReference = mDatabase.getReference(DatabaseRoutes.getMessage(key, user.getUid()));
       Message msgToSend = new Message(key, binding.textMessage.getEditText().getText().toString(), true, uri.toString(), firebaseAuth.getCurrentUser().getEmail(), new Date().getTime(), false);
       newMessageReference.setValue(msgToSend);
      }).addOnFailureListener(e -> e.printStackTrace());
     }).addOnFailureListener(e -> e.printStackTrace());
     break;
    case PermissionService.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
     Uri fileUri = data.getData();
     String fileName = fileService.getFileName(fileUri);
     mStorageReference = mStorage.getReference(DatabaseRoutes.getMessage(fileName, user.getUid()));
     mStorageReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
      mStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {
       String key = mReference.push().getKey();
       DatabaseReference newMessageReference = mDatabase.getReference(DatabaseRoutes.getMessage(key, user.getUid()));
       Message msgToSend = new Message(key, binding.textMessage.getEditText().getText().toString(), false, DatabaseRoutes.getMessage(fileName, user.getUid()), firebaseAuth.getCurrentUser().getEmail(), new Date().getTime(), true);
       newMessageReference.setValue(msgToSend);
      }).addOnFailureListener(e -> e.printStackTrace());
     }).addOnFailureListener(e -> e.printStackTrace());
     break;
   }
  }
 }

  private void validUser() {
   if(firebaseAuth.getCurrentUser() == null){
    if (this.getContext() instanceof PrincipalPatientActivity) {
     PrincipalPatientActivity activity = (PrincipalPatientActivity) getContext();
     activity.exit();
    }
   } else {
    user = firebaseAuth.getCurrentUser();
   }
  }
 }