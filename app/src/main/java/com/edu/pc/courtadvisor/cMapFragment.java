package com.edu.pc.courtadvisor;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class cMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {
    private static final int REQUEST_LOCATION = 2;
    private static final int MY_LOCATION_REQUEST_CODE = 1;

    // terrains en brut
    private ArrayList<Marker> markers = new ArrayList<Marker>();

    LatLng ECE_PARIS = new LatLng(48.852124100000005, 2.2873886);

    private MenuItem myActionMenuItem2;

    private ArrayList<Playground> listCourts = new ArrayList<Playground>();

    GoogleMap map;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    // search bar

    private MenuItem optSave;
    private MenuItem optSearch;
    private MenuItem optAdd;
    private MenuItem optLogOut;



    private SearchView mSearchView;
    private LatLng mSearchLoc;
    private Marker mSearchMarker;

    // My custom ResultReceiver to get the Address from the FetchAddressIntentService


    private FloatingActionButton fabValidate;
    private boolean addCourtclicked = false;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    public cMapFragment() {
        // Required empty public constructor

    }

    SupportMapFragment mapFragment;


    private FirebaseAuth mAuth;
    DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_map, container, false);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("PlayGrounds");

        fabValidate = view.findViewById(R.id.validateMarker);
        fabValidate.setVisibility(View.INVISIBLE);
        fabValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabValidate.setVisibility(view.INVISIBLE);
                addCourt();
            }
        });

        setHasOptionsMenu(true);

        //Toast.makeText(this.getActivity(), listCourts.get(0).getLat().toString() + listCourts.get(1).getLat().toString(), Toast.LENGTH_SHORT).show();

        return view;
    }

    private void addCourt() {
        markers.get(markers.size()-1).setDraggable(false);
        markers.get(markers.size()-1).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markers.get(markers.size()-1).setAlpha(1.0f);
        LatLng latLng = markers.get(markers.size()-1).getPosition();
        addCourtclicked = false;
        Intent intent = new Intent(this.getActivity(), AddCourtActivity.class);
        intent.putExtra("com.edu.pc.courtadvisor.latLng", latLng);
        //startActivityForResult(intent, 1);
        startActivity(intent);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menubar, menu);
        optSearch = menu.findItem( R.id.action_search);
        optAdd = menu.findItem( R.id.action_add);
//        optSave = menu.findItem(R.id.action_save);
        optLogOut = menu.findItem(R.id.action_log_out);

        optLogOut.setVisible(false);
//        optSave.setVisible(false);

        mSearchView = (SearchView) optSearch.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchString = mSearchView.getQuery().toString();
                Log.d(TAG, "The query bolote:" + searchString);
                geolocateSearch(searchString);
                optSearch.collapseActionView();
                return true;
            }
        });
        optAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(addCourtclicked == false) {
                    fabValidate.setVisibility(View.VISIBLE);
                    addCourtclicked = true;
                    addTerrain();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_add:
                if (addCourtclicked == false) {
                    fabValidate.setVisibility(View.VISIBLE);
                    addCourtclicked = true;
                    addTerrain();
                }
                break;
        }

            return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        enableMyLocation();
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnMarkerClickListener(this);

        mSearchMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(0,0)));
        mSearchMarker.setVisible(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ECE_PARIS, 13));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listCourts.clear();
                markers.clear();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Playground value = postSnapshot.getValue(Playground.class);
                    listCourts.add(value);
                    Log.d(TAG, "Value is: " + value.getAddress());
                }
                if(!listCourts.isEmpty()) initTerrains();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void addTerrain() {
        LatLng pos = map.getCameraPosition().target;
        final Marker newTerrain = map.addMarker(new MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .alpha(0.7f)
                .draggable(true));
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Dragging");
                LatLng markerLocation = arg0.getPosition();
                Log.d("MarkerPosition", markerLocation.toString());

            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                LatLng markerLocation = arg0.getPosition();
                Log.d("Marker", "finished");
                arg0.setPosition(markerLocation);
            }

            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("Marker", "Started");

            }
        });
        markers.add(newTerrain);
    }

    private void initTerrains() {
        /*
        // recuperer terrains de la bdd
        PlayGround tab[] = mDB.playGroundDao().loadAllPlayGrounds();
        Marker temp;
        if(tab.length > 0) {
            for (int i = 0; i < tab.length; i++) {
                listCourts.add(tab[i]);
                LatLng pos = new LatLng(listCourts.get(i).getLat(), listCourts.get(i).getLng());
                temp = map.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(listCourts.get(i).getAddress()));
                markers.add(temp);
            }
        }
        */
        LatLng pos;
        for(int i = 0; i < listCourts.size(); i++) {
            Log.d(TAG, "terrain dans array: " + i);
            pos =  new LatLng(listCourts.get(i).getLat(),listCourts.get(i).getLng());
            markers.add(map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(listCourts.get(i).getAddress())));
        }

        /*
        // temporaire terrains en brut
        listCourts.add(ECE);
        listCourts.add(Dupleix);
        markers.add(mEceParis);
        markers.add(mDupleix);

        mEceParis = map.addMarker(new MarkerOptions()
                .position(ECE_PARIS)
                .title("ECE Paris"));
        mDupleix = map.addMarker(new MarkerOptions()
                .position(DUPLEIX)
                .title("Dupleix"));
        */

    }

    private void geolocateSearch(String searchString) {
        Geocoder geocoder = new Geocoder(this.getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e) {
            Log.e(TAG, "geolocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0) {
            Address address = list.get(0);
            mSearchLoc = new LatLng(address.getLatitude(), address.getLongitude());


            mSearchMarker.setVisible(false);
            if(!mSearchMarker.isVisible()) {
                mSearchMarker.remove();
            }
            mSearchMarker = map.addMarker(new MarkerOptions()
                    .position(mSearchLoc)
                    .title("Ma recherche")
                    .alpha(0.7f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mSearchMarker.setVisible(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mSearchLoc, 15));


        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this.getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this.getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(this.getActivity(), "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getAlpha() == 1.0) {
            Playground temp = null;
            LatLng latLng =  marker.getPosition();
            for (int i = 0; i < listCourts.size(); i++) {
                if(listCourts.get(i).getLat() == latLng.latitude && listCourts.get(i).getLng() == latLng.longitude) {
                    temp = listCourts.get(i);
                }
            }
            Intent intent = new Intent(this.getActivity(), SelectedCourtActivity.class);
            intent.putExtra("com.edu.pc.courtadvisor.PlayGround", temp);
            startActivity(intent);
            return true;
        }
        return false;
    }


}

