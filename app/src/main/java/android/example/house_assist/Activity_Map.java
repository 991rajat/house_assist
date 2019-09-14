package android.example.house_assist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.sql.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class Activity_Map extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener,MapboxMap.OnMapClickListener {

        private static final String TAG ="TAG" ;
        private String FULL_DETAILS="";
        private MapView mapView;
        private PermissionsManager permissionsManager;
        private MaterialSearchView searchView;
        private MapboxMap mapboxMap;
        private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
        private Button selectLocationButton;
        private Marker destinantionMarker;
        private ImageView hoveringMarker;
        private Layer droppedMarkerLayer;
        private FirebaseFirestore mydb;
        private String type,email;
        private Toolbar toolbar;
        private ImageButton accessmylocation;
        private double lat,lng;
        private Point destinantionPosition,originLocation;
        private FusedLocationProviderClient mFusedLocationClient;
        private Style mapstyle;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Mapbox.getInstance(this, getString(R.string.access_token));
            setContentView(R.layout.activity__map);

            //Toolbar
            toolbar = findViewById(R.id.activity_map_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Location");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            searchView = findViewById(R.id.search_view);
            //intialize map UI
            mapView = findViewById(R.id.mapView);
            selectLocationButton = findViewById(R.id.select_location_button);
            accessmylocation = findViewById(R.id.access_my_location);
            mydb = FirebaseFirestore.getInstance();
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            //Get Details
            Intent intent = getIntent();
            type = intent.getStringExtra("type");
            email = intent.getStringExtra("email");
            accessmylocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchLocation();
                }
            }) ;
            selectLocationButton.setEnabled(false);
            selectLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Map<String,Object> map = new HashMap<>();
                    try {
                        JSONObject jsonObject = new JSONObject(FULL_DETAILS);
                        JSONArray coordinates = jsonObject.getJSONObject("geometry").getJSONArray("coordinates");
                        String FullAddress = jsonObject.getString("place_name");
                        map.put("fulladdress",FullAddress);
                        map.put("lng",coordinates.get(0));
                        map.put("lat",coordinates.get(1));
                        Log.d(TAG,FullAddress+" "+ String.valueOf(coordinates.get(0)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (type.equals("service")) {
                        mydb.collection("service_providers").document(email).update(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "location Updated");
                                        startActivity(new Intent(Activity_Map.this, MainActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Location Updation Failed" + e.toString());
                            }
                        });
                    } else {
                        mydb.collection("users").document(email).update(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "location Updated");
                                        startActivity(new Intent(Activity_Map.this, MainActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Location Updation Failed" + e.toString());
                            }
                        });
                    }

                }
                });

            //Search View Events
            searchView.closeSearch();
            searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    forwardGeocode(query);
                    searchView.closeSearch();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Do some magic
                    return false;
                }
            });

            searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
                @Override
                public void onSearchViewShown() {
                    //Do some magic
                }

                @Override
                public void onSearchViewClosed() {
                    //Do some magic
                }
            });

        }


        @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {
            Activity_Map.this.mapboxMap = mapboxMap;
            mapboxMap.addOnMapClickListener(this);
            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/rajat8385660/ck0cjp13f00641cmlfq3yxp56"),
                    new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            mapstyle = style;
                            fetchLocation();
                        }
                    });


        }


        @SuppressWarnings( {"MissingPermission"})
        private void enableLocationComponent(@NonNull Style loadedMapStyle) {
                // Check if permissions are enabled and if not request
            if (PermissionsManager.areLocationPermissionsGranted(this)) {

                // Get an instance of the component
                LocationComponent locationComponent = mapboxMap.getLocationComponent();;

                // Activate with options
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

                // Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);
                //Log.d(TAG,String.valueOf(locationComponent.getLastKnownLocation().getLongitude())+String.valueOf(locationComponent.getLastKnownLocation().getLatitude()));
                //reverseGeocode(Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(), locationComponent.getLastKnownLocation().getLatitude()));
                // Set the component's render mode

                locationComponent.setRenderMode(RenderMode.COMPASS);

            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        }

        @Override
        public boolean onMapClick(@NonNull LatLng point) {
            if(destinantionMarker!=null) {
                mapboxMap.removeMarker(destinantionMarker);
            }
                destinantionMarker = mapboxMap.addMarker(new MarkerOptions().position(point));
                destinantionPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                reverseGeocode(destinantionPosition);
                selectLocationButton.setEnabled(false);
                Log.d(TAG, String.valueOf(point.getLongitude()));

            return false;
        }



        private void forwardGeocode(final String Query)
        {

            MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.access_token))
                    .query(Query)
                    .build();
            Log.d(TAG,"YO");
            mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    List<CarmenFeature> results = response.body().features();

                    if (results.size() > 0) {

                        // Log the first results Point.

                        Point firstResultPoint = results.get(0).center();

                        Log.d(TAG,"YO"+firstResultPoint.toString());

                        Log.d(TAG, "onResponse: " + firstResultPoint.toString());
                        if(destinantionMarker!=null)
                            mapboxMap.removeMarker(destinantionMarker);
                        LatLng point = new LatLng();
                        point.setLatitude(firstResultPoint.latitude());
                        point.setLongitude(firstResultPoint.longitude());
                        point.setAltitude(firstResultPoint.altitude());
                        destinantionMarker = mapboxMap.addMarker(new MarkerOptions().position(point));
                        destinantionPosition = Point.fromLngLat(point.getLongitude(),point.getLatitude());

                        reverseGeocode(firstResultPoint);

                    } else {

                        // No result for your request were found.
                        Toast.makeText(Activity_Map.this, "No Location Found!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: No result found");

                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });

        }

        private void addMarker() {
        }

        private void reverseGeocode(final Point point) {
            try {
                MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                        .accessToken(getString(R.string.access_token))
                        .query(Point.fromLngLat(point.longitude(),point.latitude()))
                        .build();

                mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
                    @Override
                    public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                            List<CarmenFeature> results = response.body().features();
                            if (results.size() > 0) {
                                CarmenFeature feature = results.get(0);
                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(((Point) feature.geometry()).latitude(),
                                            ((Point) feature.geometry()).longitude())).zoom(12).build()),4000);
                                    Toast.makeText(Activity_Map.this,
                                            "Result "+feature.placeName(), Toast.LENGTH_SHORT).show();
                                    selectLocationButton.setEnabled(true);
                                    Log.d(TAG,"Result"+feature.toJson().toString());
                                    FULL_DETAILS = feature.toJson();
                            } else {
                                Toast.makeText(Activity_Map.this,
                                        "No Results", Toast.LENGTH_SHORT).show();
                            }

                    }

                    @Override
                    public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                        Timber.e("Geocoding Failure: %s", throwable.getMessage());
                    }
                });
            } catch (ServicesException servicesException) {
                Timber.e("Error geocoding: %s", servicesException.toString());
                servicesException.printStackTrace();
            }
        }
        private void fetchLocation() {
            Log.d(TAG,"Location Manager");
            Dexter.withActivity(Activity_Map.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).
                    withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Log.d(TAG,"Permission Granted");
                            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        // Logic to handle location object
                                        Double latittude = location.getLatitude();
                                        Double longitude = location.getLongitude();
                                        if(destinantionMarker!=null)
                                            mapboxMap.removeMarker(destinantionMarker);
                                        LatLng point = new LatLng();
                                        point.setLatitude(latittude);
                                        point.setLongitude(longitude);
                                        point.setAltitude(location.getAltitude());
                                        destinantionMarker = mapboxMap.addMarker(new MarkerOptions().position(point));
                                        destinantionPosition = Point.fromLngLat(point.getLongitude(),point.getLatitude());
                                        reverseGeocode(destinantionPosition);


                                        Toast.makeText(Activity_Map.this,"Latitude = "+latittude + "\nLongitude = " + longitude,Toast.LENGTH_LONG).show();

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"location denied"+e.toString());
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            Log.d(TAG,"onPermissionRationaleShouldBeShown");
                        }
                    }).check();
        }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.map_location_search, menu);
            MenuItem item = menu.findItem(R.id.action_search);
            searchView.setMenuItem(item);
            return true;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        @Override
        public void onExplanationNeeded(List<String> permissionsToExplain) {
            Toast.makeText(this, "User Permission Granted", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPermissionResult(boolean granted) {
            if(granted)
            {   mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
            } else {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_LONG).show();
                finish();
            }

        }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Map.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

        @Override
        public void onStart() {
            super.onStart();
            mapView.onStart();
        }

        @Override
        public void onResume() {
            super.onResume();
            mapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            mapView.onPause();
        }

        @Override
        public void onStop() {
            super.onStop();
            mapView.onStop();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            mapView.onDestroy();
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }



}


// If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
//                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
//                                @Override
//                                public void onStyleLoaded(@NonNull Style style) {
//                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
//                                        Toast.makeText(Activity_Map.this,
//                                                String.format("name result",
//                                                        feature.placeName()), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });