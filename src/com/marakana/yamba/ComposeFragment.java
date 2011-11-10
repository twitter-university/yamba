package com.marakana.yamba;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ComposeFragment extends DialogFragment {

  static ComposeFragment newInstance() {
    return new ComposeFragment();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.compose, container, false);
      final EditText statusText = (EditText)view.findViewById(R.id.status_text);
      
      // Watch for button clicks.
      Button button = (Button)view.findViewById(R.id.update_button);
      button.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
              // When button is clicked, call up to owning activity.
              ((MainActivity)getActivity()).postToTwitter(statusText.getText().toString());
          }
      });
      
      return view;
  }

}
