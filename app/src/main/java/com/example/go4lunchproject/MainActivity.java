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
import com.example.go4lunchproject.data.api.UserApi;
import com.example.go4lunchproject.data.firebase.FirebaseCloudDatabase;
import com.example.go4lunchproject.model.User;
import com.example.go4lunchproject.util.UtilMethods;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Collections;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private Button signInGoogleButton;
    private Button signInFacebookButton;

    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFacebook();
        sendGoogleSignInRequest();

        setReferences();
        signInWithFacebook();
        signInWithGoogle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserAndFirebaseAuth();
        startHomePageActivityIfUserConnected(mAuth.getCurrentUser());
    }

    private void setReferences(){
        signInFacebookButton = findViewById(R.id.sign_in_facebook_button);
        signInGoogleButton = findViewById(R.id.sign_in_google_button);
    }

    private void setUserAndFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        User user = UtilMethods.getMyUserFromFirebaseUser();

        if (user != null){
            UserApi.getInstance().setUser(user);
            ActualWorkmateApi.getInstance().setWorkmate(UtilMethods.setWorkmateCorresponding(user));
            FirebaseCloudDatabase.getInstance().saveUser(UserApi.getInstance().getUser());
        }
    }

    private void startHomePageActivityIfUserConnected(FirebaseUser user){
        if (user != null) {
            Toast.makeText(MainActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomepageActivity.class));
            finish();
        }
        else
            Toast.makeText(MainActivity.this, "No user founded", Toast.LENGTH_SHORT).show();
    }

    private void initializeFacebook() {
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FACEBOOK", "onCancel: ");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("FACEBOOK", "onError: " + error);
                    }
                });
    }

    private void signInWithFacebook(){
        signInFacebookButton.setOnClickListener(v ->
                loginManager.logInWithReadPermissions(MainActivity.this, Collections.singletonList("public_profile"))
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void signInWithGoogle(){
        signInGoogleButton.setOnClickListener(v -> lunchGoogleIntentClient());
    }

    private void sendGoogleSignInRequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_outh_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void lunchGoogleIntentClient() {
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
                        setUserAndFirebaseAuth();
                        startHomePageActivityIfUserConnected(mAuth.getCurrentUser());
//                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("GOOGLEERROR", "firebaseAuthWithGoogle: " + task.getException());
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        setUserAndFirebaseAuth();
                        startHomePageActivityIfUserConnected(mAuth.getCurrentUser());
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("FACEBOOK", "firebaseAuthWithFacebook: " + task.getException());
                    }
                });
    }
}