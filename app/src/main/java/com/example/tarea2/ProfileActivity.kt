package com.example.tarea2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tarea2.databinding.ActivityMainBinding
import com.example.tarea2.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityProfileBinding


    //Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()

        //Manejo del clic, cerrar sesión del usuario
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

    }

    private fun checkUser() {
        //Obtener el usuario actual
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser== null){
            //Usuario no está logueado
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            //El usuario está logueado
            //Obtener la info del usuario
            val email = firebaseUser.email
            //Setear el correo
            binding.emailTv.text= email
        }
    }
}