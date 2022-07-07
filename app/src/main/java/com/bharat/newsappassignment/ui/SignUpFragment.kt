package com.bharat.newsappassignment.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bharat.newsappassignment.R
import com.bharat.newsappassignment.databinding.FragmentSignUpBinding
import com.bharat.newsappassignment.utils.Utilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = Firebase.auth
        binding.registerButton.setOnClickListener {
            signUp()
        }
        binding.signInButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

   private fun signUp(){
       val email = binding.emailInput.text.toString()
       val password = binding.passwordInput.text.toString()
       val isAgreed = binding.checkbox.isChecked;
       val number = binding.numberInput.text.toString()

       if(Utilities.validate(email) && password.isNotEmpty() && number.isNotEmpty() && isAgreed) {
           firebaseAuth.createUserWithEmailAndPassword(email, password)
               .addOnCompleteListener() { task ->
                   if (task.isSuccessful) {
                       // Sign in success, update UI with the signed-in user's information
                       val intent = Intent(activity,HomePage::class.java)
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                       startActivity(intent)
                   } else {
                       // If sign in fails, display a message to the user.
                       Toast.makeText(context,"Failed try again",Toast.LENGTH_SHORT).show()
                   }
               }
       }else{
           Toast.makeText(context,"Fill all entry",Toast.LENGTH_SHORT).show()
       }
   }


}