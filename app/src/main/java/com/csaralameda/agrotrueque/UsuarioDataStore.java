package com.csaralameda.agrotrueque;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.google.gson.Gson;

import io.reactivex.rxjava3.core.Single;
import com.google.gson.Gson;

public class UsuarioDataStore {
    private static final String DATASTORE_NAME = "userpreferences";
    private static final Preferences.Key<String> USER_KEY = PreferencesKeys.stringKey("user");

    private final RxDataStore<Preferences> dataStore;
    private final Gson gson;
    //singleton
    private static UsuarioDataStore instance;

    public static synchronized UsuarioDataStore getInstance(Context context) {
        if (instance == null) {
            instance = new UsuarioDataStore(context.getApplicationContext());
        }
        return instance;
    }

    public UsuarioDataStore(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build();
        gson = new Gson();
    }
    public Single<Boolean> guardarUsuario(Usuario user) {
        String userJson = gson.toJson(user);
        return dataStore.updateDataAsync(preferences -> {
                    MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                    mutablePreferences.set(USER_KEY, userJson);
                    return Single.just(mutablePreferences);
                }).map(preferences -> true)
                .onErrorReturn(throwable -> false);
    }

    public Single<Usuario> getUser() {
        return dataStore.data().firstOrError().map(preferences -> {
            String userJson = preferences.get(USER_KEY);
            if (userJson != null) {
                return gson.fromJson(userJson, Usuario.class);
            }
            return null;
        });
    }


}
