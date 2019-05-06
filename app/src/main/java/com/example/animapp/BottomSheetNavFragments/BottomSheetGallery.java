package com.example.animapp.BottomSheetNavFragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.animapp.Activities.PostCommentaires;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class BottomSheetGallery extends Fragment {

    private ArrayList<String> listImages;
    private ArrayList<Uri> listUriImages = new ArrayList<>();
    private GridView grilleImages;
    public String urlImgSelected;
    FirebaseStorage storage;
    StorageReference storageRef;
    ProgressDialog progressDialog;
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;
    public static String imgUrl;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet__gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        grilleImages = view.findViewById(R.id.grilleImages);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        progressDialog = new ProgressDialog(getContext());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        grilleImages.setAdapter(new GrilleImagesAdapter(getActivity()));
        grilleImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                urlImgSelected = listImages.get(position);

                //startActivity(intent);
                //Toast.makeText(getActivity(), listImages.get(position), Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.comment_image_picked_dialog);
                dialog.setTitle("Vous avez choisi...");
                ImageView imgPicked = dialog.findViewById(R.id.imgPicked);
                MaterialButton ok = dialog.findViewById(R.id.ok);
                dialog.show();
                Picasso.get().load(listUriImages.get(position)).into(imgPicked);
                //imgPicked.setImageURI(Uri.fromFile(new File(listImages.get(position))));

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        putImageInDb(listUriImages.get(position));
                        dialog.dismiss();
                    }
                });


                //Toast.makeText(getActivity(), String.valueOf(listUriImages), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class GrilleImagesAdapter extends BaseAdapter {


        private Activity context;

        public GrilleImagesAdapter(Activity localContext) {
            context = localContext;
            listUriImages = getAllShownImagesPath(context);
            Collections.reverse(listUriImages);
        }

        public int getCount() {
            return listImages.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView
                        .setLayoutParams(new GridView.LayoutParams(270, 270));

            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context).load(listImages.get(position))
                    .centerCrop()
                    .into(picturesView);

            return picturesView;
        }

        /**
         * Getting All Images Path.
         *
         * @param activity
         *            the activity
         * @return ArrayList with images Path
         */
        private ArrayList<Uri> getAllShownImagesPath(Activity activity) {
            Uri uri1, uri2;
            Cursor cursor;
            MergeCursor mergeCursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<>();
            String pathOfImage;
            uri1 = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            uri2 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            cursor = activity.getContentResolver().query(uri1, projection, null,
                    null, null);

            mergeCursor = new MergeCursor(new Cursor[]{activity.getContentResolver().query(uri1, projection, null,
                    null, null),
                    activity.getContentResolver().query(uri2, projection, null,
                            null, null)});
            mergeCursor.moveToFirst();

            /*column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);*/

            while (!mergeCursor.isAfterLast()){
                pathOfImage = mergeCursor.getString(mergeCursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                //int lastPoint = pathOfImage.lastIndexOf(".");
                //pathOfImage = pathOfImage.substring(0,lastPoint)+pathOfImage.substring(lastPoint).toLowerCase();
                listOfAllImages.add(pathOfImage);
                listUriImages.add(Uri.fromFile(new File(pathOfImage)));
                mergeCursor.moveToNext();

                //pathOfImage = cursor.getString(column_index_data);
            }
            listImages = listOfAllImages;
            Collections.reverse(listImages);
            return listUriImages;
        }
    }

    public void putImageInDb(Uri image){

        progressDialog.setTitle("Téléchargement");
        progressDialog.setMessage("Téléchargement de l'image...");
        progressDialog.show();
        final long currentTime = System.currentTimeMillis();
        storageRef.child(currentUser.getUid()).child(currentTime + "." + getFileExtension(image))
                .putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageRef.child(currentUser.getUid()).child(currentTime + "." + getFileExtension(image))
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imgUrl = uri.toString();
                                Intent intent = new Intent("broadCastName");
                                intent.putExtra("From_Gallery",imgUrl);
                                getActivity().sendBroadcast(intent);
                            }
                        });
                        progressDialog.dismiss();

                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}