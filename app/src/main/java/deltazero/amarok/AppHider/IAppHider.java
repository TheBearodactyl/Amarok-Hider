package deltazero.amarok.AppHider;

import android.content.Context;

import java.util.Set;

public abstract class IAppHider {
    public Context context;

    public IAppHider(Context context) {
        this.context = context;
    }

    public abstract void hide(Set<String> pkgNames);

    public abstract void unhide(Set<String> pkgNames);

    public abstract void tryToActivate(ActivationCallbackListener activationCallbackListener);

    public abstract String getName();

    public interface ActivationCallbackListener {
        void onActivateCallback(Class<? extends IAppHider> appHider, boolean success, int msgResID);
    }
}