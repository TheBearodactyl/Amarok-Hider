package deltazero.amarok.AppHider;

import static android.app.admin.DevicePolicyManager.DELEGATION_PACKAGE_ACCESS;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.rosan.dhizuku.api.Dhizuku;
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener;

import java.util.Arrays;
import java.util.Set;

import deltazero.amarok.R;

public class DhizukuAppHider extends IAppHider {

    private final DevicePolicyManager devicePolicyManager;

    public DhizukuAppHider(Context context) {
        super(context);
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public void hide(Set<String> pkgNames) {
        setDelegatedScopes();
        for (var pkgName : pkgNames) {
            devicePolicyManager.setApplicationHidden(null, pkgName, true);
        }
    }

    @Override
    public void unhide(Set<String> pkgNames) {
        setDelegatedScopes();
        for (var pkgName : pkgNames) {
            devicePolicyManager.setApplicationHidden(null, pkgName, false);
        }
    }

    @Override
    public void tryToActivate(ActivationCallbackListener activationCallbackListener) {

        if (!Dhizuku.init()) {
            Log.w("DhizukuHider", "Dhizuku not init.");
            activationCallbackListener.onActivateCallback(this.getClass(), false, R.string.dhizuku_not_init);
            return;
        }

        if (Dhizuku.getVersionCode() < 5) {
            Log.w("DhizukuHider", "Unsupported Dhizuku version: pre v5.x");
            activationCallbackListener.onActivateCallback(this.getClass(), false, R.string.dhizuku_pre_v5);
            return;
        }

        if (Dhizuku.isPermissionGranted()) {
            Log.i("DhizukuHider", "Dhizuku available.");
            activationCallbackListener.onActivateCallback(this.getClass(), true, 0);
            return;
        }

        Log.i("DhizukuHider", "Requesting permission...");
        Dhizuku.requestPermission(new DhizukuRequestPermissionListener() {
            @Override
            public void onRequestPermission(int grantResult) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Log.d("DhizukuHider", "Permission granted.");
                    activationCallbackListener.onActivateCallback(DhizukuAppHider.class, true, 0);
                } else {
                    Log.d("DhizukuHider", "Permission denied.");
                    activationCallbackListener.onActivateCallback(DhizukuAppHider.class, false, R.string.dhizuku_permission_denied);
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Dhizuku";
    }

    private void setDelegatedScopes() {
        if (Arrays.asList(Dhizuku.getDelegatedScopes())
                .contains(DELEGATION_PACKAGE_ACCESS))
            return;
        Dhizuku.setDelegatedScopes(new String[]{DELEGATION_PACKAGE_ACCESS});
    }
}
