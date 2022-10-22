package com.example.tarea2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tarea2.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    //Constantes
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configurar la autenticación con Google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail() //sólo se necesita el correo de la cuenta de Google
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //Autenticacion firebase
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()


        //Clic del botón de inicio de sesi´no en Google para iniciar
        binding.googleSignInBtn.setOnClickListener{
            //Iniciar el inicio de sesión
            Log.d(TAG,"onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun checkUser() {
        //Verificar si usuario está logueado o no
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser!= null){
            //Usuario ya se encuentra logeado
            //iniciar el profile activity
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN ){
            Log.d(TAG,"onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                //Login exitoso
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount (account)
            }
            catch (e: Exception){
                //Login fallido
                Log.d(TAG,"onActivityResult: ${e.message}")
            }
        }

    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG,"firebaseAuthWithGoogleAccount: begin firebase auth with google account")

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {  authResult ->
                //Inicio sesion exitoso
                Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")

                //Obteber el usuario que inició sesión
                val firebaseUser = firebaseAuth.currentUser
                //Obtener información del usuario
                val uid = firebaseUser!!.uid
                val email = firebaseUser!!.email

                Log.d(TAG,"firebaseAuthWithGoogleAccount: Uid: $uid")
                Log.d(TAG,"firebaseAuthWithGoogleAccount: Email: $email")

                //Verificar si el usuario es nuevo o existente
                if (authResult.additionalUserInfo!!.isNewUser){
                    //Usuario nuevo - se crea una nueva cuenta
                    Log.d(TAG,"firebaseAuthWithGoogleAccount: Account created.../n$email")
                    Toast.makeText(this@MainActivity, "Account created.../n$email", 
                        Toast.LENGTH_SHORT).show()
                }
                    else{
                        //Salida del usuario
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing user.../n$email")
                    Toast.makeText(this@MainActivity, "LoggedIn.../n$email",
                        Toast.LENGTH_SHORT).show()
                }

                //iniciar el profile activity
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                finish()
            }


            .addOnFailureListener{ e ->
                //Inicio de sesion falló
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Loggin Failed due to ${e.message}")
                Toast.makeText(this@MainActivity, "Loggin Failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }


}
