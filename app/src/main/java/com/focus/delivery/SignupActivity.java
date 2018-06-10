package com.focus.delivery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    public static final int REQUEST_CODE = 0;

    // TODO: Redirecionar para Login após cadastro.

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_mobile)
    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
//        moveTaskToBack(true);
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Criando Conta...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String mobile = _mobileText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String reEnterPassword = _reEnterPasswordText.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context

        String url = "http://focusdelivery.com.br/clients/";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        // On complete call either onSignupSuccess or onSignupFailed
                                        // depending on success
                                        onSignupSuccess();
                                        // onSignupFailed();
                                        progressDialog.dismiss();
                                    }
                                }, 3000);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", error.toString());
                        Log.d("Error.Response", "  ERRORRRRR");
                        onSignupFailed();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Oficinag3!");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("usuario.username", mobile);
                params.put("usuario.password", password);
                params.put("usuario.first_name", name);
                params.put("cpf", " ");
                params.put("telefone", mobile);
                params.put("foto", "http://placehold.it/100x100");
                params.put("is_online", "false");

                return params;
            }
        };
        queue.add(postRequest);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Erro ao registrar", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Insira mais de 3 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() < 9) {
            _mobileText.setError("Não esqueça de colocar o 9 na frente");
            valid = false;
        } else if (mobile.isEmpty() || mobile.length() > 9) {
            _mobileText.setError("Não coloque o DDD! Apenas os 9 digitos");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Insira entre 4 e 10 caracteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Senhas devem ser iguais");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}