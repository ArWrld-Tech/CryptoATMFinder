package com.cartoaware.crypto.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cartoaware.crypto.R;
import com.cartoaware.crypto.api.Api;
import com.cartoaware.crypto.utils.Constants;
import com.cartoaware.crypto.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by davidhodge on 12/8/17.
 */

public class ATMLocDetailsActivity extends AppCompatActivity {

    @Bind(R.id.atm_img)
    ImageView atmImg;
    @Bind(R.id.atm_info)
    TextView atmInfo;
    @Bind(R.id.atm_website)
    ImageView atmWebsite;
    @Bind(R.id.atm_map)
    ImageView atmMap;

    Context mContext;
    String atmId;
    ParseObject parseObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_loc);
        ButterKnife.bind(this);
        mContext = this;

        atmId = getIntent().getStringExtra("atmId");

        Api.fetchATMById(atmId,
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                parseObject = objects.get(0);
                                setupViewInfo();
                            }
                        }
                    }
                });
    }

    private void setupViewInfo() {
        if (parseObject.containsKey(Constants.ATM_IMG)) {
            Glide.with(mContext).load(parseObject.getString(Constants.ATM_IMG)).into(atmImg);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher_background).into(atmImg);
        }

        String infoText = parseObject.getString(Constants.ATM_INFO);
        infoText = infoText + "\n\n\n<b>Name:</b> " + parseObject.getString(Constants.ATM_NAME);
        infoText = infoText + "\n<b>Address:</b> " + parseObject.getString(Constants.ATM_ADDRESS);
        infoText = infoText + "\n<b>Place:</b> " + parseObject.getString(Constants.ATM_LOC_NAME);
        infoText = infoText + "\n\n<b>Fees:</b> " + parseObject.getString(Constants.ATM_FEES);
        infoText = infoText + "\n<b>Limits:</b> " + parseObject.getString(Constants.ATM_LIMITS);
        infoText = infoText + "\n<b>Ops:</b> " + parseObject.getString(Constants.ATM_OPS);
        infoText = infoText + "\n<b>Currency:</b> " + parseObject.getJSONArray(Constants.ATM_SUPPORT_CURRENCY);
        atmInfo.setText(Html.fromHtml(infoText));

        atmWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.urlIntent(mContext, parseObject.getString(Constants.ATM_WEBSITE));
            }
        });

        atmMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseGeoPoint parseGeoPoint = parseObject.getParseGeoPoint(Constants.ATM_LOCATION);
                Utils.geoIntent(mContext, parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
            }
        });
    }
}
