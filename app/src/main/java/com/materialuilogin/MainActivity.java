package com.materialuilogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {


    private EditText userName, password;
    private Button login;
    private TextView txtError;
    private static final String TAG = "MainActivity";
    private LinearLayout layoutLoading;
    ProgressBar progressBar;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        userName = (EditText)findViewById(R.id.inputEmail);
        password = (EditText)findViewById(R.id.inputPassword);
        login = (Button)findViewById(R.id.btnLogin);
        layoutLoading = (LinearLayout)findViewById(R.id.hrLayoutProgressBarTextLogin);
        progressBar = (ProgressBar)findViewById(R.id.loginProgresBar);
        TextView signUp = (TextView) findViewById(R.id.txtRegister);
        txtError = (TextView)findViewById(R.id.txtErrorMessage);

        
        login.setOnClickListener(v -> {
         userLogin();
        });

        signUp.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
    }

    private void userLogin(){

       txtError.setVisibility(View.INVISIBLE);
        String url = "https://asimwela-personal-blog.herokuapp.com/api/v1/auth/login";

        JSONObject data = new JSONObject();
        try{

            String username = userName.getText().toString().trim();
            String pass = password.getText().toString().trim();
            if(TextUtils.isEmpty(username)){
                txtError.setVisibility(View.VISIBLE);
                txtError.setText("input username");
                return;
            }

            if(TextUtils.isEmpty(pass)){
                txtError.setVisibility(View.VISIBLE);
                txtError.setText("input password");
                return;
            }
            data.put("userName", username);
            data.put("password", pass);

        }catch (JSONException e){
            txtError.setVisibility(View.VISIBLE);
            txtError.setText("failed to parse inputs");
            e.printStackTrace();
        }

        progressBar.animate().alpha(1);
        layoutLoading.setVisibility(View.VISIBLE);

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                    if(!response.isNull("user")){
                        try {
                            JSONArray userRoles = response.getJSONObject("user").getJSONArray("roles");

                            //admin has two roles
                            if(userRoles.length()>1){
                                Intent adminActivity = new Intent(getApplicationContext(), AdminActivity.class);
                                adminActivity.putExtra("admin", response.toString());
                                startActivity(adminActivity);
                                layoutLoading.setVisibility(View.INVISIBLE);


                            }else{
                                Intent userActivity = new Intent(getApplicationContext(), UserActivity.class);
                                userActivity.putExtra("user",response.toString());
                                startActivity(userActivity);
                                layoutLoading.setVisibility(View.INVISIBLE);

                            }


                        } catch (JSONException e) {
                            txtError.setVisibility(View.VISIBLE);
                            txtError.setText("Failed to parse Server Response");
                            layoutLoading.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }
                    }else{
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText("A Server responded with Error");
                        layoutLoading.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "error occured");
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtError.setVisibility(View.VISIBLE);
                txtError.setText("Login Failed");
                layoutLoading.setVisibility(View.INVISIBLE);
                assert error != null;
              Log.e(TAG, error.toString());
            }
        });
        password.setText("");
        userName.setText("");

        VolleyController.getInstance(this).addToRequestQueue(loginRequest);
    }

}