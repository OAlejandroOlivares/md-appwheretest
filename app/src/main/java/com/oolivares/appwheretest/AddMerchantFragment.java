package com.oolivares.appwheretest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
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

public class AddMerchantFragment extends Fragment {

    private TextInputEditText nombre;
    private TextInputEditText direccion;
    private TextInputEditText phone;
    private TextInputEditText latitud;
    private TextInputEditText longitud;
    private MaterialButton submit;

    public static AddMerchantFragment newInstance() {
        AddMerchantFragment f = new AddMerchantFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.agregar_layout, container, false);
        nombre = rootView.findViewById(R.id.inp_nombre);
        direccion = rootView.findViewById(R.id.inp_direccion);
        phone = rootView.findViewById(R.id.inp_phone);
        latitud = rootView.findViewById(R.id.inp_latitud);
        longitud = rootView.findViewById(R.id.inp_longitud);
        submit = rootView.findViewById(R.id.sumbit_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nombre.getText().toString().length()>0 &&
                        direccion.getText().toString().length()>0 &&
                        phone.getText().toString().length()>0 &&
                        latitud.getText().toString().length()>0 &&
                        longitud.getText().toString().length()>0){

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://166.62.33.53:4590/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    requestInterface interfaz =retrofit.create(requestInterface.class);
                    Merchants merchants = new Merchants();

                        merchants.setLatitude(Double.parseDouble(latitud.getText().toString()));
                        merchants.setLongitude(Double.parseDouble(longitud.getText().toString()));
                        merchants.setMerchantAddress(direccion.getText().toString());
                        merchants.setMerchantName(nombre.getText().toString());
                        merchants.setMerchantTelephone(phone.getText().toString());


                    Call<ResponseBody> call = interfaz.register(merchants);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String a = response.body().string();
                                JSONObject jsonObject = new JSONObject(a);
                                if (jsonObject.getInt("status")==1){
                                    Toast.makeText(getContext(),"Registro Exitoso!",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getContext(),jsonObject.getString("description"),Toast.LENGTH_LONG).show();
                                }
                                Log.d("Success",jsonObject.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                }
            }
        });
        return rootView;
    }
}
