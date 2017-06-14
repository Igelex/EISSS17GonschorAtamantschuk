package com.example.android.harvesthand.SignUp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.harvesthand.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.harvesthand.Contracts.BASE_URL;
import static com.example.android.harvesthand.Contracts.URL_BASE_SIGNUP;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private static String URL;
    private final JSONObject jsonBody = null;

    private final static int PROFI = 0;
    private final static int USER = 1;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPass;
    private RadioGroup usertypeRadioGroup;
    private String mName;
    private String mEmail;
    private String mPass;
    private int mUserType;
    private Button mSignUpButton;
    private TextInputLayout mEmailTextInput;
    private TextInputLayout mNameTextInput;
    private TextInputLayout mPassTextInput;
    private ProgressBar progressBar;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        // Inflate the layout for this fragment
        inputName = (EditText) view.findViewById(R.id.signup_input_name);
        inputEmail = (EditText) view.findViewById(R.id.signup_input_email);
        inputPass = (EditText) view.findViewById(R.id.signup_input_pass);

        usertypeRadioGroup = (RadioGroup) view.findViewById(R.id.rg_usertype);

        progressBar = (ProgressBar) getActivity().findViewById(R.id.signup_progressbar);

        mName = inputName.getText().toString().trim();
        mEmail = inputEmail.getText().toString().trim();
        mPass = inputPass.getText().toString().trim();

        mSignUpButton = (Button) view.findViewById(R.id.bt_signup);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validNameInput() && validEmailInput() && validPassInput() && checkRadioButtons()) {
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

    private boolean validEmailInput() {
        mEmail = inputEmail.getText().toString().trim();
        mEmailTextInput = (TextInputLayout) getActivity().findViewById(R.id.signup_inputlayout_email);
        if (mEmail.isEmpty() /*|| Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()*/) {
            mEmailTextInput.setErrorEnabled(true);
            mEmailTextInput.setError(getString(R.string.errmsg_email_required));
            inputEmail.setError(getString(R.string.errmsg_valid_input_required));
            inputEmail.requestFocus();
            return false;
        }
        mEmailTextInput.setErrorEnabled(false);
        return true;
    }

    private boolean validNameInput() {
        mName = inputName.getText().toString().trim();
        mNameTextInput = (TextInputLayout) getActivity().findViewById(R.id.signup_inputlayout_name);
        if (mName.isEmpty()) {
            mNameTextInput.setErrorEnabled(true);
            mNameTextInput.setError(getString(R.string.errmsg_name_required));
            inputName.setError(getString(R.string.errmsg_valid_input_required));
            inputName.requestFocus();
            return false;
        }
        mNameTextInput.setErrorEnabled(false);
        return true;
    }

    private boolean validPassInput() {
        mPass = inputPass.getText().toString().trim();
        mPassTextInput = (TextInputLayout) getActivity().findViewById(R.id.signup_inputlayout_pass);
        if (mPass.isEmpty()) {
            mPassTextInput.setErrorEnabled(true);
            mPassTextInput.setError(getString(R.string.errmsg_pass_required));
            inputPass.setError(getString(R.string.errmsg_valid_input_required));
            inputPass.requestFocus();
            return false;
        } else if (mPass.length() < 6) {
            mPassTextInput.setErrorEnabled(true);
            mPassTextInput.setError(mPass.length() + "/6");
            inputPass.setError(getString(R.string.errmsg_valid_input_required));
            inputPass.requestFocus();
            return false;
        }
        mPassTextInput.setErrorEnabled(false);
        return true;
    }

    private boolean checkRadioButtons() {
        if (usertypeRadioGroup.getCheckedRadioButtonId() == -1) {
            Snackbar snackbar = Snackbar.make(getView(), getActivity().getString(R.string.msg_choice_typeandgender), Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView snackBarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            snackBarText.setTextColor(Color.rgb(253, 86, 86));
            snackbar.show();
            return false;
        }

        return true;
    }

    public void sendSignUpRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("name", mName);
        params.put("email", mEmail);
        params.put("pass", mPass);
        params.put("user_type", Integer.toString(mUserType));
        URL = BASE_URL + URL_BASE_SIGNUP ;

        Log.i("Params: ", params.toString());

        JsonObjectRequest request = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);

                        try {
                            String user_id = response.getString("_id");
                            Log.i("User_id: ", user_id);
                            progressBar.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar.make(getView(), getActivity().getString(R.string.msg_signup_success), Snackbar.LENGTH_LONG);
                            View text = snackbar.getView();
                            TextView snackBarText = (TextView) text.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.rgb(71, 171, 75));
                            snackbar.show();
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
                                case 500:
                                    Snackbar snackbarIE = Snackbar.make(getView(), getActivity().getString(R.string.msg_internal_error), Snackbar.LENGTH_LONG);
                                    View sbie = snackbarIE.getView();
                                    TextView snackBarText = (TextView) sbie.findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText.setTextColor(Color.rgb(253, 86, 86));
                                    snackbarIE.show();
                                    break;
                                case 404:
                                    Snackbar snackbar404 = Snackbar.make(getView(), getActivity().getString(R.string.msg_404_error), Snackbar.LENGTH_LONG);
                                    View snackbarView404 = snackbar404.getView();
                                    TextView snackBarText404 = (TextView) snackbarView404.findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText404.setTextColor(Color.rgb(253, 86, 86));
                                    snackbar404.show();
                                    break;
                                case 409:
                                    Snackbar snackbarEmail = Snackbar.make(getView(), getActivity().getString(R.string.msg_email_exist), Snackbar.LENGTH_LONG);
                                    View snackbarView = snackbarEmail.getView();
                                    TextView snackBarTextEmail = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarTextEmail.setTextColor(Color.rgb(253, 86, 86));
                                    snackbarEmail.show();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Snackbar snackbar = Snackbar.make(getView(), getActivity().getString(R.string.connection_err), Snackbar.LENGTH_LONG);
                            View snackbarView = snackbar.getView();
                            TextView snackBarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.rgb(253, 86, 86));
                            snackbar.show();
                        }
                    }
                });
        Volley.newRequestQueue(getContext()).add(request);
    }
}
