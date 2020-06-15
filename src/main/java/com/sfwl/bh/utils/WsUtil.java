package com.sfwl.bh.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sfwl.bh.entity.BioFile;
import com.sfwl.bh.entity.ws.WsData;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/18 11:59
 */
public class WsUtil {

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    private static final WsData<String, String> INIT_DATA = new WsData<>(null, null, "init", null, null);

    private static <T, U> String obj2json(WsData<T, U> wsData) {
        return gson.toJson(wsData);
    }

    @Deprecated
    private static <T, U> WsData<T, U> json2obj(String pack) {
        return gson.fromJson(pack, new TypeToken<WsData<T, U>>() {
        }.getType());
    }

    public static String getInitData() {
        return obj2json(INIT_DATA);
    }

    public static String getStatusData(String blockName) {
        return obj2json(new WsData<>("run", null, "read", "status" + blockName, null));
    }

    public static String getStopData(String blockName) {
        return obj2json(new WsData<>("run", null, "write", "stop" + blockName, null));
    }

    public static String getPauseData(String blockName) {
        return obj2json(new WsData<>("run", null, "write", "pause" + blockName, null));
    }

    public static String getSkipData(String blockName) {
        return obj2json(new WsData<>("run", null, "write", "skip" + blockName, null));
    }

    public static String getRewindData(String blockName) {
        return obj2json(new WsData<>("run", null, "write", "rewind" + blockName, null));
    }

    public static String getFileData(String blockName) {
        return obj2json(new WsData<>("run", null, "read", "file" + blockName, null));
    }

    public static String getRunFile(String blockName, BioFile bioFile) {
        return obj2json(new WsData<>("run", null, "write", "run" + blockName, bioFile));
    }
}
