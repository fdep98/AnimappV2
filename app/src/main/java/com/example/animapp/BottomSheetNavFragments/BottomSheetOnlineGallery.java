package com.example.animapp.BottomSheetNavFragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.animapp.Activities.PostCommentaires;
import com.example.animapp.Fragments.GalerieFragment;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.animapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetOnlineGallery extends Fragment {

    private ArrayList<String> listImg = new ArrayList<>(

    );
    private GridView grilleImages;
    private List<ImageGalerie> listImagGal = GalerieFragment.galerieList;
    public  String urlImgSelectedOnline;



    public BottomSheetOnlineGallery(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet_online_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        grilleImages = view.findViewById(R.id.grilleImages);

        for(ImageGalerie img : listImagGal){
            listImg.add(img.getImageUrl());
        }

            GrilleImagesAdapter adapter = new GrilleImagesAdapter(getActivity());

        grilleImages.setAdapter(adapter);
        grilleImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PostCommentaires.class);
                urlImgSelectedOnline = listImg.get(position);
                //intent.putExtra("From_Online_Gallery",);
                //startActivity(intent);

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.comment_image_picked_dialog);
                dialog.setTitle("Vous avez choisi...");
                ImageView imgPicked = dialog.findViewById(R.id.imgPicked);
                MaterialButton ok = dialog.findViewById(R.id.ok);
                Picasso.get().load(listImg.get(position)).into(imgPicked);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("broadCastName");
                        urlImgSelectedOnline = listImg.get(position);
                        intent.putExtra("From_Online_Gallery",urlImgSelectedOnline);
                        getActivity().sendBroadcast(intent);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }


    private class GrilleImagesAdapter extends BaseAdapter {


        private Activity context;

        public GrilleImagesAdapter(Activity localContext) {
            context = localContext;
        }

        public int getCount() {
            return listImg.size();
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

            Glide.with(context).load(listImg.get(position))
                    .centerCrop()
                    .into(picturesView);

            return picturesView;
        }

    }

}
