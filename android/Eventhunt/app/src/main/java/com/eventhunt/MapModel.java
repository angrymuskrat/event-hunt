package com.eventhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapModel extends ViewModel {
    private MutableLiveData<CameraPosition> cameraPositionLiveData;
    private LocationManager mLocationManager;
    private Map<String, Boolean> permissionIsCalled;

    public MapModel() {
        cameraPositionLiveData = new MutableLiveData<>();
        permissionIsCalled = new HashMap<>();
    }

    public void setPermissionIsCalled(Set<String> permission) {
        for (String p : permission) {
            permissionIsCalled.put(p, false);
        }
    }

    public void callAfterPermission(String permission, PermissionTask permissionTask) {
        if (permissionIsCalled.containsKey(permission) || (permissionIsCalled.get(permission) != null && !permissionIsCalled.get(permission))) {
            permissionIsCalled.put(permission, true);
            permissionTask.task();
        }
    }

    public void setLocationManager(Context context) {
        mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public void setCameraPositionLiveData(CameraPosition cameraPosition) {
        cameraPositionLiveData.setValue(cameraPosition);
    }


    public LiveData<CameraPosition> getCameraPosition() {
        if (cameraPositionLiveData.getValue() == null) {
            updateCameraPosition();
        }
        return cameraPositionLiveData;
    }

    @SuppressLint("MissingPermission")
    private void updateCameraPosition() {
        Boolean perm = permissionIsCalled.get(Manifest.permission.ACCESS_FINE_LOCATION);
        if (perm != null && perm)
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(10).build();
                    cameraPositionLiveData.setValue(cameraPosition);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            }, null);
    }

}