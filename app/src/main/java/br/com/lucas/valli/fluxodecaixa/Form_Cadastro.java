package br.com.lucas.valli.fluxodecaixa;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.com.lucas.valli.fluxodecaixa.databinding.ActivityFormCadastroBinding;

public class Form_Cadastro extends AppCompatActivity {
    private ActivityFormCadastroBinding binding;
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.editCadastroNome.addTextChangedListener(cadastroTextWatcher);
        binding.editCadastroEmail.addTextChangedListener(cadastroTextWatcher);
        binding.editCadastroSenha.addTextChangedListener(cadastroTextWatcher);

        binding.btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CadastrarUsuario(v);
            }
        });

    }
    TextWatcher cadastroTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nome = binding.editCadastroNome.getText().toString();
            String email = binding.editCadastroEmail.getText().toString();
            String senha = binding.editCadastroSenha.getText().toString();

            if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty()){

                binding.btnCadastrar.setEnabled(true);
                binding.btnCadastrar.setBackgroundDrawable(getDrawable(R.drawable.button));
            } else{
                binding.btnCadastrar.setEnabled(false);
                binding.btnCadastrar.setBackgroundDrawable(getDrawable(R.drawable.button_desativado));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    public void CadastrarUsuario(View view){

        String email = binding.editCadastroEmail.getText().toString();
        String senha = binding.editCadastroSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    SalvarDadosUsuario();
                    Snackbar snackbar = Snackbar.make(view,"Cadastro realizado com SUCESSO!",Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {finish();}
                            });
                    snackbar.show();
                }else {
                    String erro;


                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Coloque uma senha com no mínimo 6 caractéres!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "E-mail inválido.";
                    }catch (FirebaseAuthUserCollisionException e){
                        erro = "Esta conta já está cadastrada.";
                    }catch (FirebaseNetworkException e){
                        erro = "Sem conexão com a Internet";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";
                    }
                    binding.txtMsgErro.setText(erro);
                }

            }
        });
}

    public void SalvarDadosUsuario(){

        String nome = binding.editCadastroNome.getText().toString();
        String email = binding.editCadastroEmail.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome",nome);
        usuarios.put("email", email);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection(usuarioID).document("usuario");
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}