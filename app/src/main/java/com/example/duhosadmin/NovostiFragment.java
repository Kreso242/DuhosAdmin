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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NovostiFragment extends Fragment {
    ImageView actionBarImage;
    ImageButton idiNatrag,osvjeziButton,objaviButton;
    private View connectionFragmentView,novostiFragmentView;
    private boolean connectionFlag=false;
    DatabaseReference databaseReference;
    private String idNumberString;
    private int idNumberInt;
    EditText editTextNaslov;
    EditText editTextMedij;
    EditText editTextLinkNaObjavu;
    EditText editTextSadrzaj;

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
        actionBarImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_novostinaslov));
        idiNatrag=view.findViewById(R.id.idiNatrag);
        idiNatrag.setVisibility(View.VISIBLE);

        idiNatrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        checkInternetConnection();

        if(connectionFlag==true) {
            novostiFragmentView = inflater.inflate(R.layout.fragment_novosti, container, false);

            databaseReference= FirebaseDatabase.getInstance().getReference("Novosti");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.exists()) {
                            idNumberString= snapshot.getKey();
                            idNumberInt=Integer.parseInt(idNumberString);
                            idNumberInt++;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Greška u čitanju iz baze podataka", databaseError.toException());
                }
            });

            editTextNaslov = novostiFragmentView.findViewById(R.id.editTextNaslov);
            editTextMedij = novostiFragmentView.findViewById(R.id.editTextMedij);
            editTextLinkNaObjavu = novostiFragmentView.findViewById(R.id.editTextLinkNaObjavu);
            editTextSadrzaj = novostiFragmentView.findViewById(R.id.editTextSadrzaj);

            objaviButton = novostiFragmentView.findViewById(R.id.objaviButton);
            onTextChange();

            objaviButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextNaslov.length() == 0 || editTextMedij.length() == 0 || editTextLinkNaObjavu.length() == 0 || editTextSadrzaj.length() == 0) {
                        Toast.makeText(getContext(), "Unesi podatke u sva ponuđena polja!", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.child(String.valueOf(idNumberInt)).setValue(new Medij(editTextNaslov.getText().toString(),editTextSadrzaj.getText().toString(),editTextMedij.getText().toString(),editTextLinkNaObjavu.getText().toString()));

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter, new VratiSeFragment()).commit();
                    }
                }
            });
            return novostiFragmentView;
        }
        else {
            connectionFragmentView = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);
            osvjeziButton=connectionFragmentView.findViewById(R.id.osvjeziButton);
            osvjeziButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,new NovostiFragment()).addToBackStack("novostiFragment").commit();
                }
            });
            return connectionFragmentView;
        }
    }

    private void onTextChange() {
        editTextNaslov.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextNaslov.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextNaslov.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextMedij.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextMedij.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextMedij.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextLinkNaObjavu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextLinkNaObjavu.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextLinkNaObjavu.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextSadrzaj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    editTextSadrzaj.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.active_shape));
                } else {
                    editTextSadrzaj.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.no_active_shape));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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








