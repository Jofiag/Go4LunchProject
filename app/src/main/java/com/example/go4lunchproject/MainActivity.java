package com.example.go4lunchproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunchproject.controller.HomepageActivity;
import com.example.go4lunchproject.data.api.ActualWorkmateApi;
import com.example.go4lunchproject.data.firebase.MyFirebaseDatabase;
import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.model.Workmate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private Button signInFacebookButton;
    private Button signInGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendGoogleSignInRequest();

        setReferences();
        signInWithFacebook();
        signInWithGoogle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserAndFirebaseAuth();

        startHomePageActivityIfUserConnected(mAuth.getCurrentUser());  //updateUI(user)//DO WANT NEEDED WITH USER
    }

    private void setReferences(){
        signInFacebookButton = findViewById(R.id.sign_in_facebook_button);
        signInGoogleButton = findViewById(R.id.sign_in_google_button);
    }

    private void setUserAndFirebaseAuth(){
        User user = new User();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null){
            user.setId(firebaseUser.getDisplayName() + "_" +  firebaseUser.getUid());
            user.setFirebaseId(firebaseUser.getUid());
            user.setUserEmail(firebaseUser.getEmail());
            user.setName(firebaseUser.getDisplayName());
            user.setImageUri(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());
            UserApi.getInstance().setUser(user);
            ActualWorkmateApi.getInstance().setWorkmate(setWorkmateCorresponding(user));
            MyFirebaseDatabase.getInstance().saveUser(UserApi.getInstance().getUser());
        }
    }

    private Workmate setWorkmateCorresponding(User user){
        Workmate workmate = new Workmate();
        workmate.setName(user.getName());
        workmate.setImageUri(user.getImageUri());
        workmate.setRestaurantChosen(user.getRestaurantChosen());

        return workmate;
    }

    private void startHomePageActivityIfUserConnected(FirebaseUser user){
        if (user != null) {
            Toast.makeText(MainActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
//            finish();
        }
        else
            Toast.makeText(MainActivity.this, "No user founded", Toast.LENGTH_SHORT).show();
    }

    private void signInWithFacebook(){
        signInFacebookButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
            finish();
        });
    }

    private void signInWithGoogle(){
        signInGoogleButton.setOnClickListener(v -> signIn());
    }

    private void sendGoogleSignInRequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_outh_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mGetContent.launch(signInIntent);

    }

    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("USERID", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.d("USERID", "Google Auth Error :" + e);
                }
            });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(MainActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                        startHomePageActivityIfUserConnected(mAuth.getCurrentUser());
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}