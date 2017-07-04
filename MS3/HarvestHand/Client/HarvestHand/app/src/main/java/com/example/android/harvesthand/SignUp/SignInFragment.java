package com.example.android.harvesthand.SignUp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.harvesthand.Contracts;
import com.example.android.harvesthand.InitTTS;
import com.example.android.harvesthand.MainActivity;
import com.example.android.harvesthand.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.harvesthand.Contracts.URL_BASE_SIGNIN;
import static com.example.android.harvesthand.Contracts.URL_IP;
import static com.example.android.harvesthand.Contracts.URL_PORT;
import static com.example.android.harvesthand.Contracts.URL_PROTOCOL;
import static com.example.android.harvesthand.Contracts.USER_ID;
import static com.example.android.harvesthand.Contracts.USER_NUMBER;
import static com.example.android.harvesthand.Contracts.USER_SHARED_PREFS;
import static com.example.android.harvesthand.Contracts.USER_SHARED_PREFS_ID;
import static com.example.android.harvesthand.Contracts.USER_SHARED_PREFS_NUMBER;
import static com.example.android.harvesthand.Contracts.USER_SHARED_PREFS_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private static String URL;
    private EditText inputNumber;
    private String mNumber;
    private ProgressBar progressBar;
    private SharedPreferences sPref;
    TextToSpeech speaker;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        inputNumber = view.findViewById(R.id.signin_input_number);

        progressBar = getActivity().findViewById(R.id.signup_progressbar);

        Button signinButton = view.findViewById(R.id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validNumberInput()) {
                    progressBar.setVisibility(View.VISIBLE);
                    sendSignInRequest();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Request zum Login des users
     */
    public void sendSignInRequest() {
        InitTTS tts = new InitTTS(getContext());
        speaker = tts.initTTS();
        final Contracts contracts = new Contracts(speaker);
        //Phone_number im Body wird gesendet
        Map<String, String> params = new HashMap<>();
        params.put("phone_number", mNumber);
        URL = URL_PROTOCOL + URL_IP + URL_PORT + URL_BASE_SIGNIN;

        JsonObjectRequest request = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            String current_user_id = response.getString("_id");
                            String current_user_number = response.getString("phone_number");
                            int current_user_type = response.getInt("user_type");

                            savePreferences(current_user_id, current_user_type, current_user_number);

                            Toast.makeText(getContext(), R.string.msg_login_successful, Toast.LENGTH_SHORT ).show();
                            //Nach erfolgreichem Login --> MainActivity
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            contracts.showSnackbar(getView(), getString(R.string.msg_error), true, false);
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
                                    inputNumber.setError(getString(R.string.msg_number_incorrect));
                                    contracts.showSnackbar(getView(), getString(R.string.msg_number_incorrect), true, false);
                                    break;
                                case 404:
                                    contracts.showSnackbar(getView(), getString(R.string.msg_404_error), true, false);
                                    break;
                                case 500:
                                    contracts.showSnackbar(getView(), getString(R.string.msg_internal_error), true, false);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            contracts.showSnackbar(getView(), getString(R.string.connection_err), true, false);
                        }
                    }
                });
        Volley.newRequestQueue(getContext()).add(request);
    }

    /**
     * Validiert die User-Eingabe
     * @return - true, fals eingabe vaide
     */
    private boolean validNumberInput() {
        mNumber = inputNumber.getText().toString().trim();
        if (mNumber.isEmpty()) {
            inputNumber.setError(getString(R.string.errmsg_number_required));
            inputNumber.requestFocus();
            return false;
        }
        return true;
    }
    /**
     * User_id, phone_number und user_type werden permanent gespeichert
     */
    private void savePreferences(String id, int type, String number) {
        sPref = getActivity().getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(USER_SHARED_PREFS_ID, id);
        USER_ID = id;
        editor.putString(USER_SHARED_PREFS_NUMBER, number);
        USER_NUMBER = number;
        editor.putInt(USER_SHARED_PREFS_TYPE, type);
        editor.apply();
        Log.i("Save User_id: ", id);
        Log.i("Save User_type: ", ""+type);
    }

    @Override
    public void onDestroy() {
        if (speaker != null){
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }

}
