package com.example.android.harvesthand.SignUp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.harvesthand.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private String url = "http://192.168.0.19:3001/signin";
    private Button signinButton;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signinButton = (Button) view.findViewById(R.id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Sedn request", Toast.LENGTH_SHORT).show();
                sendSignInRequest();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void sendSignInRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("email", "nvvb");
        params.put("pass", "vvvvvvvh");

        Log.i("Params: ", params.toString());

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String user_id = response.getString("_id");
                            Log.i("User_id: ", user_id);
                            Toast.makeText(getContext(), user_id, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 401:
                                    Snackbar snackbarIE = Snackbar.make(getView(), "Email or Password incorrect", Snackbar.LENGTH_LONG);
                                    View sbie = snackbarIE.getView();
                                    TextView snackBarText = (TextView) sbie.findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText.setTextColor(Color.RED);
                                    snackbarIE.show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
        Volley.newRequestQueue(getContext()).add(request);
    }

}
