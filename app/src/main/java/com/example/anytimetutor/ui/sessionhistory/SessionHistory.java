package com.example.anytimetutor.ui.sessionhistory;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anytimetutor.PastSessions;
import com.example.anytimetutor.R;
import com.example.anytimetutor.ViewRequestActivity;

public class SessionHistory extends Fragment {

    private SessionHistoryViewModel mViewModel;

    public static SessionHistory newInstance() {
        return new SessionHistory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.session_history_fragment, container, false);
        Intent in = new Intent(getActivity(), PastSessions.class);
        startActivity(in);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SessionHistoryViewModel.class);
        // TODO: Use the ViewModel
    }

}
