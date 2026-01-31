package com.example.misLugares.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.misLugares.R;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        // IMPORTANTE: Usa el Web client ID de tu google-services.json
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("165553765880-uuguj5u80rc1075r826u1jh1l07rkf10.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button btnSignIn = findViewById(R.id.btnGoogleSignIn);
        btnSignIn.setOnClickListener(v -> signIn());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Verificar si el usuario ya inició sesión
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Usuario ya autenticado: " + currentUser.getEmail());
            irAPantallaPrincipal();
        }
    }

    private void signIn() {
        // Cerrar sesión antes de iniciar el flujo de login para asegurar una cuenta limpia
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In exitoso, autenticar con Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Google Sign-In exitoso: " + account.getEmail());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In falló
                Log.e(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Error al iniciar sesión con Google: " + e.getStatusCode(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Autenticación exitosa
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Bienvenido " + user.getDisplayName(),
                                    Toast.LENGTH_SHORT).show();
                            irAPantallaPrincipal();
                        }
                    } else {
                        // Autenticación falló
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Autenticación fallida: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Error desconocido"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irAPantallaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}