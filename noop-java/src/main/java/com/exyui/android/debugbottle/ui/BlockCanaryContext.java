package com.exyui.android.debugbottle.ui;

import android.content.Context;

/**
 * Created by yuriel on 9/7/16.
 */

public class BlockCanaryContext {
    public BlockCanaryContext(Object a) {}

    public int getConfigDuration() {
        return 99999;
    }

    public long getConfigBlockThreshold() {
        return 0;
    }

    public boolean isNeedDisplay() {
        return false;
    }

    public String getQualifier() {
        return "";
    }

    public String getUid() {
        return "";
    }

    public String getNetworkType() {
        return "";
    }

    public Context getContexr() {
        return null;
    }

    public String getLogPath() {
        return "";
    }

    public boolean zipLogFile(Object a, Object b) {
        return false;
    }

    public void uploadLogFile(Object a) {}

    public String getStackFoldPrefix() {
        return "";
    }

    public long getConfigDumpIntervalMillis() {
        return 0;
    }
}
