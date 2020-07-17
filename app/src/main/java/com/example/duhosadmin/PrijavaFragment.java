package com.example.duhosadmin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PrijavaFragment extends Fragment {

    EditText user,password;
    ImageButton prijaviSe;
    ImageView actionBarImage;
    ImageButton idiNatrag,osvjeziButton;
    private View connectionFragmentView,prijavaFragmentView;
    String userID="duhosAdmin";
    String userPassword="Admin20Duhos";
    private boolean connectionFlag=false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBar mActionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.toolbar);
        mActionBar.setBackgroundDrawable(this.getResources().getDrawable(R.color.background));
        View view=mActionBar.getCustomView();
        actionBarImage=view.findViewById(R.id.prijavaImage);
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_prijavanaslov));
        idiNatrag=view.findViewById(R.id.idiNatrag);
        idiNatrag.setVisibility(View.GONE);
        checkInternetConnection();

        if(connectionFlag==true) {
            prijavaFragmentView = inflater.inflate(R.layout.fragment_prijava, container, false);


            user = prijavaFragmentView.findViewById(R.id.editTextUser);
            password = prijavaFragmentView.findViewById(R.id.editTextPassword);
            prijaviSe = prijavaFragmentView.findViewById(R.id.prijaviSeButton);



            user.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        user.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                        user.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user_active, 0, 0, 0);
                    } else {
                        user.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                        user.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                        password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password_active, 0, 0, 0);
                    } else {
                        password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                        password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            prijaviSe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userInput, passwordInput;
                    userInput = user.getText().toString();
                    passwordInput = password.getText().toString();
                    if (!userInput.equals(userID) || !userPassword.equals(passwordInput))
                        Toast.makeText(getContext(), "Pogrešan Korisnički ID ili lozinka!\nPokušaj ponovno!", Toast.LENGTH_SHORT).show();
                    else {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new KreirajFragment()).addToBackStack("kreirajFragment").commit();
                    }
                }
            });
            return prijavaFragmentView;
        }
        else{
            connectionFragmentView = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);
            osvjeziButton=connectionFragmentView.findViewById(R.id.osvjeziButton);
            osvjeziButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new PrijavaFragment()).addToBackStack("prijavaFragment").commit();
                }
            });
            return connectionFragmentView;
        }
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager=(ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
        if(null!=activeNetwork){
            if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI){
                connectionFlag=true;
            }
            else if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE){
                connectionFlag=true;
            }

        }
        else
        {
            connectionFlag=false;
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

}










