package data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qiscus.rtc.sample.R;
import com.qiscus.rtc.sample.model.User;
import com.qiscus.rtc.sample.utils.Action;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.chat.core.data.model.QiscusAccount;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by rajapulau on 3/13/18.
 */

public class UserRepositoryImpl implements UserRepository {

    private Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public UserRepositoryImpl(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    @Override
    public void getCurrentUser(Action<User> onSuccess, Action<Throwable> onError) {
        getCurrentUserObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void getUsers(Action<List<User>> onSuccess, Action<Throwable> onError) {
        getUsersObservable()
                .flatMap(Observable::from)
                .filter(user -> !user.equals(getCurrentUser()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void updateProfile(String name, Action<User> onSuccess, Action<Throwable> onError) {
        Qiscus.updateUserAsObservable(name, getCurrentUser().getAvatarUrl())
                .map(this::mapFromQiscusAccount)
                .doOnNext(this::setCurrentUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void login(String name, String email, String password, Action<User> onSuccess, Action<Throwable> onError) {
        Qiscus.setUser(email, password)
                .withUsername(name)
                .save()
                .map(this::mapFromQiscusAccount)
                .doOnNext(this::setCurrentUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void logout() {
        Qiscus.clearUser();
        sharedPreferences.edit().clear().apply();
    }

    private void setCurrentUser(User user) {
        sharedPreferences.edit()
                .putString("current_user", gson.toJson(user))
                .apply();
    }

    private Observable<User> getCurrentUserObservable() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(getCurrentUser());
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private User getCurrentUser() {
        return gson.fromJson(sharedPreferences.getString("current_user", ""), User.class);
    }

    private Observable<List<User>> getUsersObservable() {
        return Observable.create(subscriber -> {
            try {
                List<User> users = gson.fromJson(getUsersData(), new TypeToken<List<User>>() {
                }.getType());
                subscriber.onNext(users);
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private String getUsersData() throws IOException, JSONException {
        Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.users);

        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        return new String(bytes);
    }

    private User mapFromQiscusAccount(QiscusAccount qiscusAccount) {
        User user = new User();
        user.setId(qiscusAccount.getEmail());
        user.setName(qiscusAccount.getUsername());
        user.setAvatarUrl(qiscusAccount.getAvatar());
        return user;
    }
}