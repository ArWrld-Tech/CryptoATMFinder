package com.cartoaware.crypto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cartoaware.crypto.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by davidhodge on 12/8/17.
 */

public class ExtrasFragment extends BaseFragment {

    @Bind(R.id.donate_btc)
    LinearLayout donateBtc;
    @Bind(R.id.donate_eth)
    LinearLayout donateEth;
    @Bind(R.id.donate_ltc)
    LinearLayout donateLtc;
    @Bind(R.id.email_us)
    LinearLayout emailUs;


    View view;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_extras, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        donateBtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(mContext, getString(R.string.btc_wallet));
            }
        });

        donateEth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(mContext, getString(R.string.eth_wallet));
            }
        });

        donateLtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(mContext, getString(R.string.ltc_wallet));
            }
        });
    }

    private void setClipboard(Context context, String text) {
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "Saved wallet ID to clipboard!", Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e){
            Toast.makeText(mContext, "Error attempting to save wallet ID to clipboard!", Toast.LENGTH_SHORT).show();
        }
    }
}
