package com.bharat.newsappassignment.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.bharat.newsappassignment.R
import com.bharat.newsappassignment.databinding.FragmentLoginBinding
import com.bharat.newsappassignment.utils.Utilities
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(),gso)
        binding.loginButton.setOnClickListener {
            logIn()
        }
        binding.registerButton.setOnClickListener {
            findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.googleLoginButton.setOnClickListener {
            googleSignIn()
        }

    }
    private fun logIn(){
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        if(Utilities.validate(email)){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if(task.isSuccessful){
                        Toast.makeText(context,"Login Success",Toast.LENGTH_SHORT).show()
                       moveToHomePage()
                    }else{
                        Toast.makeText(context,"Login Fail",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun googleSignIn(){
        val intent = gsc.signInIntent
        startActivityForResult(intent,1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000){
           val task: Task<GoogleSignInAccount>  = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                task.getResult(ApiException::class.java)
                moveToHomePage()
            }catch (e: ApiException){
                Toast.makeText(requireContext(),"something went wrong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun moveToHomePage(){
        val intent = Intent(activity,HomePage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(activity,HomePage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }


}

