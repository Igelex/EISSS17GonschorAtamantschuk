package com.example.android.harvesthand.SignUp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.harvesthand.EntryTutorialActivity;
import com.example.android.harvesthand.MainActivity;
import com.example.android.harvesthand.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.id.input;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private String url = "http://192.168.2.102:3001/signin";
    private Button signinButton;
    private EditText inputEmail;
    private EditText inputPass;
    private String mEmail;
    private String mPass;
    private ProgressBar progressBar;
    private SharedPreferences sPref;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        inputEmail = (EditText) view.findViewById(R.id.signin_input_email);
        inputPass = (EditText) view.findViewById(R.id.signin_input_pass);

        progressBar = (ProgressBar) getActivity().findViewById(R.id.signup_progressbar);

        signinButton = (Button) view.findViewById(R.id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validEmailInput() && validPassInput()) {
                    progressBar.setVisibility(View.VISIBLE);
                    sendSignInRequest();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void sendSignInRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("email", mEmail);
        params.put("pass", mPass);

        Log.i("Params: ", params.toString());

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            String user_id = response.getString("_id");
                            Log.i("User_id: ", user_id);
                            savePreferences(user_id);
                            Snackbar snackbarIE = Snackbar.make(getView(), getString(R.string.msg_login_successful), Snackbar.LENGTH_LONG);
                            View sbie = snackbarIE.getView();
                            TextView snackBarText = (TextView) sbie.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.rgb(71, 171, 75));
                            snackbarIE.show();

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 401:
                                    inputEmail.setError(getString(R.string.msg_email_or_passwort_incorrect));
                                    inputPass.setError(getString(R.string.msg_email_or_passwort_incorrect));
                                    Snackbar snackbarIE = Snackbar.make(getView(), getString(R.string.msg_email_or_passwort_incorrect), Snackbar.LENGTH_LONG);
                                    View sbie = snackbarIE.getView();
                                    TextView snackBarText = (TextView) sbie.findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText.setTextColor(Color.rgb(253, 86, 86));
                                    snackbarIE.show();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Snackbar snackbar = Snackbar.make(getView(), getString(R.string.connection_err), Snackbar.LENGTH_LONG);
                            View text = snackbar.getView();
                            TextView snackBarText = (TextView) text.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.rgb(253, 86, 86));
                            snackbar.show();
                        }
                    }
                });
        Volley.newRequestQueue(getContext()).add(request);
    }

    private boolean validPassInput() {
        mPass = inputPass.getText().toString().trim();
        if (mPass.isEmpty()) {
            inputPass.setError(getString(R.string.errmsg_pass_required));
            inputPass.requestFocus();
            return false;
        } else if (mPass.length() < 6) {
            inputPass.setError(mPass.length() + "/6");
            inputPass.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validEmailInput() {
        mEmail = inputEmail.getText().toString().trim();
        if (mEmail.isEmpty() /*|| Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()*/) {
            inputEmail.setError(getString(R.string.errmsg_email_required));
            inputEmail.requestFocus();
            return false;
        }
        return true;
    }

    private void savePreferences(String id) {
        sPref = getActivity().getSharedPreferences("User_id Pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("user_id", id);
        editor.apply();
        Log.i("Save User_id: ", id);
    }

}
