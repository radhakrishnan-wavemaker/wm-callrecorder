package com.example.plugin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import com.callrecord.app.Storage;
import com.callrecord.services.RecordingService;


public class Callrecord extends CordovaPlugin {
    public static final int RESULT_CALL = 1;
    CallbackContext mCallbackContext;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        mCallbackContext = callbackContext;
        if (action.equals("startRecordingService")) {
            cordova.requestPermissions(this, RESULT_CALL,RecordingService.PERMISSIONS );
            return true;
        } else if(action.equals("openAccessibility")){
            openAccessibility();
            return true;
        } else if(action.equals("accessibilityStatus")){
            openAppSetting();
            return true;
        }

        return false;
    }

    public  void openAccessibility(){
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        this.cordova.getActivity().startActivity(intent);
        mCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
    }

  public  void openAppSetting(){
        Intent appIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", cordova.getActivity().getPackageName(), null);
        appIntent.setData(uri);
        cordova.getActivity().startActivity(appIntent);
    }



    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException    {
        switch(requestCode)
        {
            case RESULT_CALL:
                if (Storage.permitted(this.cordova.getContext(), RecordingService.MUST)) {
                    RecordingService.start(this.cordova.getContext());
                    mCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                } else {
                    Toast.makeText(this.cordova.getContext(), "Not permitteed", Toast.LENGTH_SHORT).show();
                    if (!Storage.permitted(this.cordova.getContext(), RecordingService.MUST)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.cordova.getContext());
                        builder.setTitle("Permissions");
                        builder.setMessage("Call permissions must be enabled manually");
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openAppSetting();
                            }
                        });
                        builder.show();
                        mCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                    }
                }
                break;
        }
    }

}