package com.hazem.utilslib.libs.location;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class LocationAsync extends AsyncTask<Void, Void, LocationData> {

    private Geocoder gcd;
    private Location location;
    private DataReady dataReadyListener;
    private String language;

    public LocationAsync(String language, Geocoder gcd, Location location, DataReady dataReadyListener) {
        this.gcd = gcd;
        this.location = location;
        this.language = language;
        this.dataReadyListener = dataReadyListener;
    }

    public JSONObject getLocationInfo(String language, double lat, double lng) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JsonString = null;
        try {

            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + lat + "," + lng + "&language=" + language + "&sensor=true&key=AIzaSyDMXoxGSVmVtqFijzHD1teUJyaJ8L61aXA");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JsonString = buffer.toString();
        } catch (IOException e) {
            Log.e("Network Connection ", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Error", " closing stream", e);
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(JsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(LocationData locationData) {
        if (dataReadyListener != null) dataReadyListener.dataReady(locationData);
    }

    @Override
    protected LocationData doInBackground(Void... voids) {
        return getLocationLatLng();
    }

    private LocationData getLocationLatLng() {
        LocationData locationData = new LocationData();
        List<Address> addresses;
        StringBuilder fullAddress = new StringBuilder();
        locationData.latitude = location.getLatitude();
        locationData.longitude = location.getLongitude();

        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    fullAddress.append("-").append(addresses.get(0).getAddressLine(i));
                }
                locationData.fullAddress = fullAddress.toString();
                locationData.countryCode = addresses.get(0).getCountryCode();
                locationData.countryName = addresses.get(0).getCountryName();
                locationData.featureName = addresses.get(0).getFeatureName();

                locationData.city = addresses.get(0).getLocality();
                locationData.state = addresses.get(0).getAdminArea();
                locationData.postalCode = addresses.get(0).getPostalCode();

            } else {
                locationData = getCurrentLocationViaJSON(locationData);
            }
        } catch (IOException e) {
            locationData = getCurrentLocationViaJSON(locationData);
            e.printStackTrace();
        }

        return locationData;
    }

    private LocationData getCurrentLocationViaJSON(LocationData locationData) {
        try {
            JSONObject googleMapResponse;
            googleMapResponse = getLocationInfo(language, location.getLatitude(), location.getLongitude());
            if (googleMapResponse != null) {
                // many nested loops.. not great -> use expression instead
                // loop among all results
                JSONArray results = (JSONArray) googleMapResponse.get("results");
                // loop among all addresses within this result
                JSONObject result = results.getJSONObject(0);
                if (result.has("formatted_address")) locationData.fullAddress =
                        result.getString("formatted_address");
                if (result.has("address_components")) {
                    JSONArray addressComponents = result.getJSONArray("address_components");
                    // loop among all address component to find a 'locality' or 'sublocality'
                    for (int j = 0; j < addressComponents.length(); j++) {
                        JSONObject addressComponent = addressComponents.getJSONObject(j);
                        if (result.has("types")) {
                            JSONArray types = addressComponent.getJSONArray("types");

                            for (int k = 0; k < types.length(); k++) {
                                // get city name
                                if ("locality".equals(types.getString(k)) || "sublocality".equals(types.getString(k))
                                        || "administrative_area_level_1".equals(types.getString(k))) {
                                    if (addressComponent.has("long_name")) {
                                        locationData.city = addressComponent.getString("long_name");
                                    } else if (addressComponent.has("short_name")) {
                                        locationData.city = addressComponent.getString("short_name");
                                    }
                                }

                                // get countryCode and countryName
                                if ("country".equals(types.getString(k))) {
                                    if (addressComponent.has("long_name")) {
                                        locationData.countryName = addressComponent.getString("long_name");
                                    }
                                    if (addressComponent.has("short_name")) {
                                        locationData.countryCode = addressComponent.getString("short_name");
                                    }
                                }

                                // get administrative_area_level_2
                                if ("administrative_area_level_2".equals(types.getString(k))) {
                                    if (addressComponent.has("long_name")) {
                                        locationData.featureName = addressComponent.getString("long_name");
                                    } else if (addressComponent.has("short_name")) {
                                        locationData.featureName = addressComponent.getString("short_name");
                                    }
                                }

                                // get postal_code
                                if ("postal_code".equals(types.getString(k))) {
                                    if (addressComponent.has("long_name")) {
                                        locationData.postalCode = addressComponent.getString("long_name");
                                    }

                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return locationData;
    }

    public interface DataReady {
        void dataReady(LocationData data);
    }


}
