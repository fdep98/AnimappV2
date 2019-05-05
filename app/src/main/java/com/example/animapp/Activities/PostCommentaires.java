package com.example.animapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.animapp.BottomSheetNavFragments.BottomSheetCamera;
import com.example.animapp.BottomSheetNavFragments.BottomSheetGallery;
import com.example.animapp.BottomSheetNavFragments.BottomSheetOnlineGallery;
import com.example.animapp.BottomSheetNavFragments.PagerAdapter;
import com.example.animapp.Database.CommentsHelper;
import com.example.animapp.Database.ImageHelper;
import com.example.animapp.Database.UserHelper;
import com.example.animapp.Model.ImageGalerie;
import com.example.animapp.Model.Post;
import com.example.animapp.Model.PostCommentaire;
import com.example.animapp.Model.User;
import com.example.animapp.PostCommentaireAdapter;
import com.example.animapp.PostListAdapter;
import com.example.animapp.ViewPagerAdapter;
import com.example.animapp.animapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import biz.laenger.android.vpbs.BottomSheetUtils;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostCommentaires extends AppCompatActivity {

    private List<PostCommentaire> usersComs = new ArrayList<>();
    ImageButton commenter, add;
    EditText commentaire;
    String currentDate;
    public FirebaseAuth mAuth;
    FirebaseUser currentUser;
    User currentMonit;
    String postId;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView commentairesRecyclerView;
    PostCommentaireAdapter commentaireAdapter;
    public static int nbrCommentaire = 0;
    Date date;
    Post postSend;
    ActionMode actionMode;
    FirebaseStorage storage;
    StorageReference storageRef;
    Uri imageFromCam, imageFromGallery;

    BottomSheetBehavior sheetBehavior;  // provides callbacks and make the BottomSheet work with CoordinatorLayout.

    @BindView(R.id.bottom_sheet_popup)
    LinearLayout layoutBottomSheet;

    @BindView(R.id.bottomSheetParent)
    CoordinatorLayout bottomSheetParent;

    @BindView(R.id.bottom_sheet_tabs)
    TabLayout bottomSheetTabLayout;

    @BindView(R.id.bottom_sheet_viewpager)
    ViewPager bottomSheetViewPager;

    private int[]icons = {R.drawable.ic_gallery,R.drawable.app_photo,R.drawable.ic_gallery};

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy"+" à "+ "HH:mm:ss");

    String fromCam,fromGal, fromOnlineGal;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentaire);
        ButterKnife.bind(this);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        commentairesRecyclerView = findViewById(R.id.recycler_view);
        commentaire = findViewById(R.id.commentaire);

        commenter = findViewById(R.id.commenter);
        add = findViewById(R.id.add);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        progressDialog = new ProgressDialog(PostCommentaires.this);

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //view.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                       // view.setText("Expand Sheet");
                        bottomSheetParent.setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); //permet d'éviter le dragging
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        setBottomSheet();
        //setupViewPager(bottomSheetViewPager);
        setupIcons();

        commentaire.setClickable(true);
        commentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetParent.setVisibility(View.GONE);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(PostCommentaires.this);
                        bottomSheetParent.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        postId = PostListAdapter.postId;


        commentairesRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        commentairesRecyclerView.setLayoutManager(layoutManager);


        if(getIntent().getExtras() != null){
            postSend = (Post) getIntent().getExtras().getSerializable("Post");
             //fromCam = getIntent().getStringExtra("From_Camera");
        }


        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                String imgUrlOnlineGal = b.getString("From_Online_Gallery");
                String imgUrlGal = b.getString("From_Gallery");
                fromOnlineGal = imgUrlOnlineGal;
                fromGal = imgUrlGal;
                imageFromCam = (Uri) b.get("From_Camera");
                imageFromGallery = (Uri) b.get("From_Gallery");
            }
        };

        registerReceiver(br, new IntentFilter("broadCastName"));

        /**
         * manually opening / closing bottom sheet on button click
         */

        bottomSheetParent.setVisibility(View.GONE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetParent.setVisibility(View.VISIBLE);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetParent.setVisibility(View.GONE);
                }
            }
        });


        //récupère les information sur l'utilisateur
        UserHelper.getCurrentUser(currentUser.getUid()).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots != null){
                            for(DocumentSnapshot doc : queryDocumentSnapshots){
                                currentMonit = doc.toObject(User.class);
                            }
                        }
                    }
                });

        CommentsHelper.getAllPostComments()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final List<PostCommentaire> com = new ArrayList<>();
                        PostCommentaire post;
                        if(queryDocumentSnapshots != null) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                post = (doc.toObject(PostCommentaire.class));
                                if(getIntent().getExtras() != null){
                                    if(post.getIdPost().equals(postSend.getId())){
                                        com.add(post);

                                    }
                                }else{
                                    if(post.getIdPost().equals(PostListAdapter.postId)){
                                        com.add(post);

                                    }
                                }

                            }
                            usersComs = com;
                        }
                        commenter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                date = new Date();
                                currentDate = df.format(date);

                                if (!commentaire.getText().toString().isEmpty() || fromOnlineGal != null) {
                                    PostCommentaire newComment;
                                    if (fromOnlineGal != null) {
                                        newComment = new PostCommentaire(currentMonit.getNom(), currentMonit.getId(), currentDate, commentaire.getText().toString(), postId, fromOnlineGal);
                                        newComment.setMonitPicUrl(currentMonit.getUrlPhoto());
                                    } else {
                                        newComment = new PostCommentaire(currentMonit.getNom(), currentMonit.getId(), currentDate, commentaire.getText().toString(), postId);
                                        newComment.setMonitPicUrl(currentMonit.getUrlPhoto());
                                    }
                                    com.add(newComment);

                                    CommentsHelper.createUserComment(newComment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            if (documentReference != null) {
                                                documentReference.update("idCommentaire", documentReference.getId());
                                            }
                                        }
                                    });
                                    commentaire.setText("");
                                } else if (!commentaire.getText().toString().isEmpty() || imageFromCam != null) {
                                    putImageInDb(imageFromCam, commentaire.getText().toString(), com);
                                } else if (!commentaire.getText().toString().isEmpty() || imageFromGallery != null) {
                                    putImageInDb(imageFromGallery, commentaire.getText().toString(), com);
                                } else if (commentaire.getText().toString().isEmpty() && imageFromGallery == null && imageFromCam == null && fromOnlineGal.isEmpty()) {
                                    Toast.makeText(PostCommentaires.this, "Veuillez entrer un commentaire avant l'envoi", Toast.LENGTH_SHORT).show();
                                }

                                usersComs = com;
                                nbrCommentaire = usersComs.size();
                            }

                        });

                        commentaireAdapter = new PostCommentaireAdapter(getApplicationContext(), usersComs);
                        commentairesRecyclerView.setAdapter(commentaireAdapter);
                        commentaireAdapter.setOnClickListener(new PostCommentaireAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(View view, PostCommentaire obj, int pos) {
                                if(commentaireAdapter.getSelectedItemCount() > 0){
                                    enableActionMode(pos);
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, PostCommentaire obj, int pos) {
                                enableActionMode(pos);
                            }
                        });

                    }

                });



    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void enableActionMode(final int position) {
        if (actionMode == null) {
            ActionMode.Callback actionModeCallback = new ActionMode.Callback(){

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.delete_post_toolbar, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                    int id = item.getItemId();
                    if(id == R.id.delete){
                        deleteComment();
                        mode.finish();
                        commentaireAdapter.notifyDataSetChanged();
                        return true;
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    commentaireAdapter.clearSelections();
                    actionMode = null;

                }
            };
            actionMode = startActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        commentaireAdapter.toggleSelection(position);
        int count = commentaireAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle("Supprimer un commentaire");
            actionMode.setSubtitle(String.valueOf(count)+" sélectionné");
            actionMode.invalidate();
        }
    }

    public void deleteComment(){
        final List<Integer> selectedItemPositions = commentaireAdapter.getSelectedItems();

        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            if(usersComs.get(i).getIdMoniteur().equals(currentUser.getUid())){
                commentaireAdapter.deleteComment(selectedItemPositions.get(i));
            }else{
                Toast.makeText(this, "Vous devez être l'auteur du commentaire pour le supprimer", Toast.LENGTH_SHORT).show();
            }
        }
        commentaireAdapter.notifyDataSetChanged();
    }

    public void setupIcons(){
        bottomSheetTabLayout.getTabAt(0).setIcon(icons[0]);
        bottomSheetTabLayout.getTabAt(1).setIcon(icons[1]);
        bottomSheetTabLayout.getTabAt(2).setIcon(icons[2]);
    }


    public void setBottomSheet(){
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        BottomSheetOnlineGallery bottomSheetOnlineGallery = new BottomSheetOnlineGallery();
        BottomSheetCamera bottomSheetCamera = new BottomSheetCamera();
        BottomSheetGallery bottomSheetGallery = new BottomSheetGallery();


        pagerAdapter.addFragment(bottomSheetOnlineGallery,null);
        pagerAdapter.addFragment(bottomSheetCamera,null);
        pagerAdapter.addFragment(bottomSheetGallery,null);


        bottomSheetViewPager.setOffscreenPageLimit(1);
        bottomSheetViewPager.setAdapter(pagerAdapter);
        bottomSheetTabLayout.setupWithViewPager(bottomSheetViewPager);
        BottomSheetUtils.setupViewPager(bottomSheetViewPager);
    }

    public void putImageInDb(Uri image, String msg,List<PostCommentaire> list){
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
                                PostCommentaire newComment = new PostCommentaire(currentMonit.getNom(), currentMonit.getId(),currentDate,msg,postId,uri.toString());
                                newComment.setMonitPicUrl(currentMonit.getUrlPhoto());
                                list.add(newComment);
                               // ImageGalerie newImage = new ImageGalerie(currentUser.getUid(), uri.toString(), currentDate);
                               /* ImageHelper.addImage(newImage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        if (documentReference != null) {
                                            documentReference.update("imgId", documentReference.getId());
                                        }

                                    }
                                });*/
                                CommentsHelper.createUserComment(newComment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        if(documentReference != null){
                                            documentReference.update("idCommentaire",documentReference.getId());
                                        }
                                        progressDialog.dismiss();
                                        commentaire.setText("");
                                    }
                                });

                            }
                        });
                    }
                });
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = this.getContentResolver();
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