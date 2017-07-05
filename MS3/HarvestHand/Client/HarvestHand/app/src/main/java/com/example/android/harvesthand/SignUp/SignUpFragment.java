package com.example.android.harvesthand.SignUp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
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

import static com.example.android.harvesthand.Contracts.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private EditText inputNumber;
    private RadioGroup usertypeRadioGroup;
    private String mNumber;
    private int mUserType;
    private ProgressBar progressBar;
    private Contracts contracts;
    private TextToSpeech speaker;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        InitTTS tts = new InitTTS(getContext());
        speaker = tts.initTTS();
        contracts = new Contracts(speaker);

        // Inflate the layout for this fragment
        inputNumber = view.findViewById(R.id.signup_input_number);

        usertypeRadioGroup = view.findViewById(R.id.rg_usertype);

        progressBar = getActivity().findViewById(R.id.signup_progressbar);

        mNumber = inputNumber.getText().toString().trim();

        Button mSignUpButton = view.findViewById(R.id.bt_signup);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validNumberInput() && checkRadioButtons()) {
                    progressBar.setVisibility(View.VISIBLE);
                    sendSignUpRequest();
                }

            }
        });
        /**
         * Auswahl des Usertypes mit Radiobuttons
         */
        usertypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id) {
                    case R.id.rb_literate:
                        mUserType = USER_TYPE_LITERATE;
                        break;
                    case R.id.rb_illiterate:
                        mUserType = USER_TYPE_ILLITERATE;
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    /**
     * Validiere Userinput
     * @return - true, wenn Eingabe valide
     */
    private boolean validNumberInput() {
        mNumber = inputNumber.getText().toString().trim();
        TextInputLayout mNumberTextInput = getActivity().findViewById(R.id.signup_inputlayout_number);
        if (mNumber.isEmpty()) {
            mNumberTextInput.setErrorEnabled(true);
            mNumberTextInput.setError(getString(R.string.errmsg_number_required));
            inputNumber.setError(getString(R.string.errmsg_valid_input_required));
            inputNumber.requestFocus();
            return false;
        }
        mNumberTextInput.setErrorEnabled(false);
        return true;
    }

    /**
     * Prüfe, ob Radiobutton selected ist
     * @return - true, falls selected
     */
    private boolean checkRadioButtons() {
        if (usertypeRadioGroup.getCheckedRadioButtonId() == -1) {
            contracts.showSnackbar(getView(), getActivity().getString(R.string.msg_choice_typeandgender), true, false);
            return false;
        }

        return true;
    }

    /**
     * Request zum Registrieren im System
     */
    public void sendSignUpRequest() {
        //Body
        Map<String, String> params = new HashMap<>();
        params.put("phone_number", mNumber);
        params.put("user_type", Integer.toString(mUserType));
        String URL = BASE_URL + URL_BASE_SIGNUP;
        Log.i("Params: ", params.toString());

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            String user_id = response.getString("_id");
                            String user_number = response.getString("phone_number");
                            int user_type = response.getInt("user_type");

                            savePreferences(user_id, user_type, user_number);

                            Toast.makeText(getContext(), R.string.msg_signup_success, Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.INVISIBLE);
                            //Nach erfolgreicher Registration --> MainActivity
                            startActivity(new Intent(getContext(), MainActivity.class));
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
                                case 500:
                                    contracts.showSnackbar(getView(), getString(R.string.msg_internal_error), true, false);
                                    break;
                                case 404:
                                    contracts.showSnackbar(getView(), getString(R.string.msg_404_error), true, false);
                                    break;
                                case 409:
                                    contracts.showSnackbar(getView(), getActivity().getString(R.string.msg_number_exist), true, false);
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
     * User_id, phone_number und user_type werden permanent gespeichert
     */
    private void savePreferences(String id, int type, String number) {
        SharedPreferences sPref = getActivity().getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(USER_SHARED_PREFS_ID, id);
        USER_ID = id;
        editor.putInt(USER_SHARED_PREFS_TYPE, type);
        editor.putString(USER_SHARED_PREFS_NUMBER, number);
        USER_NUMBER = number;
        editor.apply();
        Log.i("Save User_id: ", id);
        Log.i("Save User_type: ", String.valueOf(type));
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
