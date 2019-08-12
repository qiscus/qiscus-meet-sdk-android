package data;

import com.qiscus.rtc.sample.model.User;
import com.qiscus.rtc.sample.utils.Action;

import java.util.List;

/**
 * Created by rajapulau on 3/13/18.
 */

public interface UserRepository {
    void login(String name, String email, String password, Action<User> onSuccess, Action<Throwable> onError);

    void getCurrentUser(Action<User> onSuccess, Action<Throwable> onError);

    void getUsers(Action<List<User>> onSuccess, Action<Throwable> onError);

    void updateProfile(String name, Action<User> onSuccess, Action<Throwable> onError);

    void logout();
}
