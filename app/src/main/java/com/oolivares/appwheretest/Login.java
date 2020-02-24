package com.oolivares.appwheretest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    private TextInputEditText correo;
    private TextInputEditText password;
    private FloatingActionButton btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        correo = findViewById(R.id.inp_mail);
        password = findViewById(R.id.inp_pass);
        btn_login = findViewById(R.id.btn_login);
        final Context context  = this;
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (correo.getText().toString().length()>0 && password.getText().toString().length()>0 ){
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://166.62.33.53:4590/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    requestInterface interfaz =retrofit.create(requestInterface.class);
                    Call<ResponseBody> call = interfaz.login(correo.getText().toString(), password.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                String a = null;
                                try {
                                    a = response.body().string();
                                    JSONObject jsonObject = new JSONObject(a);
                                    if (jsonObject.getInt("status") == 1) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(context,jsonObject.getString("description"),Toast.LENGTH_LONG).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Log.d("response",response.body().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //SHOW USER ERROR
                        }
                    });
                }
            }
        });

    }
}
