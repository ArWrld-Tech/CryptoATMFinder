package com.cartoaware.crypto.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cartoaware.crypto.R;
import com.cartoaware.crypto.activity.ATMLocDetailsActivity;
import com.cartoaware.crypto.activity.MainActivity;
import com.cartoaware.crypto.api.Api;
import com.cartoaware.crypto.utils.Constants;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.functions.Function;
import com.mapbox.mapboxsdk.style.functions.stops.IdentityStops;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.mapbox.mapboxsdk.style.layers.Filter.eq;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionBase;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;

/**
 * Created by davidhodge on 12/8/17.
 */

public class ATMMapFragment extends BaseFragment {
    @Bind(R.id.map)
    MapView mapView;
    @Bind(R.id.card_view)
    CardView cardView;
    @Bind(R.id.preview_holder)
    RelativeLayout previewHlder;
    @Bind(R.id.preview_img)
    ImageView previewImg;
    @Bind(R.id.preview_text_name)
    TextView previewTextName;
    @Bind(R.id.preview_text_info)
    TextView previewTextInfo;

    private Context mContext;
    private MapboxMap map;
    private FillExtrusionLayer fillExtrusionLayer = null;
    private View view;
    private Location lastLocation;
    private ParseGeoPoint parseGeoPoint;
    private boolean firstCamera = true;

    private IconFactory iconFactory;
    private Icon defIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        iconFactory = IconFactory.getInstance(mContext);
        defIcon = iconFactory.fromResource(R.mipmap.ic_marker);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_atm_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (firstCamera) {
            setUpMap();
        }
        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

    private void setUpMap() {
//        ((MainActivity) getActivity()).getLoading().setVisibility(View.VISIBLE);
        mapView.setStyleUrl(getString(R.string.map_style));
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                map = mapboxMap;

                map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
//                        cardView.setVisibility(View.GONE);
                    }
                });

                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final @NonNull Marker marker) {
                        if (!marker.getTitle().contains("h___")) {
                            Api.fetchATMById(marker.getTitle(), new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (objects.size() > 0) {
                                        Intent intent = new Intent(mContext, ATMLocDetailsActivity.class);
                                        intent.putExtra("atmId", objects.get(0).getObjectId());
                                        startActivity(intent);
                                    }
                                }
                            });
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                map.setOnScrollListener(new MapboxMap.OnScrollListener() {
                    @Override
                    public void onScroll() {
//                        restrictMapToBoundingBox();
                    }
                });

                map.setOnFlingListener(new MapboxMap.OnFlingListener() {
                    @Override
                    public void onFling() {
//                        restrictMapToBoundingBox();
                    }
                });

                updateCamera();
            }
        });
    }

    private void updateCamera() {
        try {
            if(((MainActivity)getActivity()).lastLoc != null){
                lastLocation = ((MainActivity)getActivity()).lastLoc;
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                        .zoom(14)
                        .tilt(45)
                        .bearing(0)
                        .build();

                map.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 500, new MapboxMap.CancelableCallback() {
                    @Override
                    public void onCancel() {
                        Log.d("CAMERA", "CANCEL CALLED");
                        runQuery();
                    }

                    @Override
                    public void onFinish() {
                        Log.d("CAMERA", "FINISH CALLED");
                        if (firstCamera) {
                            firstCamera = false;
                            runQuery();
                        }
                    }
                });
            }else{
                runQuery();
            }
        } catch (Exception e) {
            Log.d("CAMERA", "EXCEPTION CALLED");
            runQuery();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }


    private void runQuery() {
        map.setMyLocationEnabled(true);
        map.getTrackingSettings().setDismissBearingTrackingOnGesture(true);
        Api.fetchNearbyATMs(null, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    map.clear();
                    map.removeAnnotations();

                    if (fillExtrusionLayer == null) {
                        fillExtrusionLayer = new FillExtrusionLayer("buildings", "composite");
                        fillExtrusionLayer.setSourceLayer("building");
                        fillExtrusionLayer.setFilter(eq("extrude", "true"));
                        fillExtrusionLayer.setMinZoom(14);

                        // Set data-driven styling properties
                        fillExtrusionLayer.setProperties(
                                fillExtrusionColor("#000000"),
                                fillExtrusionHeight(Function.property("height", new IdentityStops<Float>())),
                                fillExtrusionBase(Function.property("min_height", new IdentityStops<Float>())),
                                fillExtrusionOpacity(0.75f)
                        );
                        map.addLayer(fillExtrusionLayer);
                    }
                    try {
                        if (list.size() > 0) {
                            int listSize = list.size();
                            for (int i = 0; i < listSize; i++) {
                                drawMarker(list.get(i));
                            }
                        }

                    } catch (Exception e1) {
                        Log.e("error", e1.toString());
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
                try {
//                    ((MainActivity) getActivity()).getLoading().setVisibility(View.GONE);
                } catch (Exception e1) {

                }
            }
        });
    }

    private void drawMarker(final ParseObject parseObject) {
        final ParseGeoPoint parseGeoPoint = parseObject.getParseGeoPoint(Constants.ATM_LOCATION);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude()))
                .icon(defIcon)
                .title(parseObject.getObjectId()));
    }
}
