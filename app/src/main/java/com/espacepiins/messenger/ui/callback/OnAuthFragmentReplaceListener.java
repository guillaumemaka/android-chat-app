package com.espacepiins.messenger.ui.callback;

import com.espacepiins.messenger.ui.AuthActivity;

/**
 * Authentication Fragment listener
 *
 */
public interface OnAuthFragmentReplaceListener {
    /**
     * Signal the auth activity that it need to replace its container fragment with the given fragment
     * @param fragment fragment tag/identifier
     */
    void onReplaceFragment(AuthActivity.AuthFragment fragment);
}
