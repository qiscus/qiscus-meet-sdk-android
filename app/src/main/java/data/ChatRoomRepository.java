package data;

import com.qiscus.rtc.sample.model.User;
import com.qiscus.rtc.sample.utils.Action;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

/**
 * Created by rajapulau on 3/13/18.
 */

public interface ChatRoomRepository {
    void getChatRooms(Action<List<QiscusChatRoom>> onSuccess, Action<Throwable> onError);

    void createChatRoom(User user, Action<QiscusChatRoom> onSuccess, Action<Throwable> onError);
}
