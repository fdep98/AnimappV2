package com.example.animapp.BottomSheetNavFragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.example.animapp.Activities.PostCommentaires;
import com.example.animapp.Activities.postMessage;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class BottomSheetCamera extends Fragment {

    private ImageView imgPicked;
    public static final int CAMERA_INTENT = 14;
    Uri image;
    FloatingActionButton camera;


    public BottomSheetCamera() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgPicked = view.findViewById(R.id.imgPicked);
        camera = view.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_INTENT);
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK){
            final Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            image= getImageUri(getContext(), imageBitmap);
            Picasso.get().load(image).into(imgPicked);
            Intent intent = new Intent("broadCastName");
            intent.putExtra("From_Camera",image);
            getActivity().sendBroadcast(intent);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
