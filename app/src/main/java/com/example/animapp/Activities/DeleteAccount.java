package com.example.animapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.animapp.Database.UserHelper;
import com.example.animapp.MainFragmentActivity;
import com.example.animapp.animapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class DeleteAccount extends AppCompatActivity {

    MaterialButton annuler, confirmer;
    public FirebaseAuth mAuth;
    private static final int DELETE_USER_TASK = 20;
    private static final int FROM_DELETE_ACCOUNT = 12;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_delete_user);
        confirmer =findViewById(R.id.frame_delete_confirm);
        annuler =  findViewById(R.id.frame_delete_cancel_button);
        mAuth = FirebaseAuth.getInstance();

        confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }


    // executer lorsque le bouton confirmer est pressé (FrameLayout)
    public void confirm(){

        UserHelper.deleteUser(mAuth.getCurrentUser().getUid());
        AuthUI.getInstance()
                .delete(this)
                .addOnSuccessListener(this, updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK);
    }

    // executer lorsque le bouton annuler est pressé (FrameLayout)
    public void cancel(){
        //Toast.makeText(getActivity(), "annuler", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DeleteAccount.this, MainFragmentActivity.class);
        intent.putExtra("FROM_DELETE_ACCOUNT",3);
        startActivity(intent);
    }

    // 3 - Create OnCompleteListener called after tasks ended
    public OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case DELETE_USER_TASK:
                        finish();
                        startActivity(new Intent(DeleteAccount.this, Connexion.class));
                        break;
                }
            }
        };
    }
}
