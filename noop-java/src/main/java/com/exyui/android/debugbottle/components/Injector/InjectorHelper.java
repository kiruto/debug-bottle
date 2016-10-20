package com.exyui.android.debugbottle.components.injector;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by yuriel on 9/7/16.
 */

@SuppressWarnings("unused")
public interface InjectorHelper {
    ArrayList<Intent> putIntent();
    ArrayList<Runnable> putRunnable();
}
