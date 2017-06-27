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

import static com.example.android.harvesthand.Contracts.BASE_URL;
import static com.example.android.harvesthand.Contracts.URL_BASE_SIGNUP;
import static com.example.android.harvesthand.Contracts.USER_SHARED_PREFS;
import static com.example.android.harvesthand.Contracts.USER_SP_ID;
import static com.example.android.harvesthand.Contracts.USER_SP_NUMBER;
import static com.example.android.harvesthand.Contracts.USER_SP_TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private static String URL;
    private final static int PROFI = 0;
    private final static int USER = 1;
    private EditText inputNumber;
    private RadioGroup usertypeRadioGroup;
    private String mNumber;
    private int mUserType;
    private Button mSignUpButton;
    private TextInputLayout mNumberTextInput;
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

        mSignUpButton = view.findViewById(R.id.bt_signup);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validNumberInput() && checkRadioButtons()) {
                    progressBar.setVisibility(View.VISIBLE);
                    sendSignUpRequest();
                }

            }
        });

        usertypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id) {
                    case R.id.rb_profi:
                        mUserType = PROFI;
                        break;
                    case R.id.rb_user:
                        mUserType = USER;
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }



    private boolean validNumberInput() {
        mNumber = inputNumber.getText().toString().trim();
        mNumberTextInput = getActivity().findViewById(R.id.signup_inputlayout_number);
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

    private boolean checkRadioButtons() {
        if (usertypeRadioGroup.getCheckedRadioButtonId() == -1) {
            contracts.showSnackbar(getView(), getActivity().getString(R.string.msg_choice_typeandgender), true, false);
            return false;
        }

        return true;
    }

    public void sendSignUpRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("phone_number", mNumber);
        params.put("user_type", Integer.toString(mUserType));
        URL = BASE_URL + URL_BASE_SIGNUP ;

        Log.i("Params: ", params.toString());

        final JsonObjectRequest request = new JsonObjectRequest(URL, new JSONObject(params),
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

    private void savePreferences(String id, int type, String number) {
        SharedPreferences sPref = getActivity().getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(USER_SP_ID, id);
        editor.putInt(USER_SP_TYPE, type);
        editor.putString(USER_SP_NUMBER, number);
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
