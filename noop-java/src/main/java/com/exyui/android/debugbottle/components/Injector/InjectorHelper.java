package com.exyui.android.debugbottle.components.Injector;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by yuriel on 9/7/16.
 */

public interface InjectorHelper {
    ArrayList<Intent> putIntent();
    ArrayList<Runnable> putRunnable();
}
