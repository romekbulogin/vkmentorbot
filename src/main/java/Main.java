import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        Random random = new Random();
        Keyboard keyboard = new Keyboard();
        Database database = new Database();
        List<List<KeyboardButton>> allKey = new ArrayList<>();
        List<KeyboardButton> lineOne = new ArrayList<>();
        GroupActor actor = new GroupActor(213932615, "727a21e5aff72e263de80f9dfc135f24858365045809a8336b2ba4af19fd8d7c5bb13aafb4e20d725bccd");
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();

        while (true) {
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messages = historyQuery.execute().getMessages().getItems();

            if (!messages.isEmpty()) {
                messages.forEach(message -> {
                    System.out.println(message.toString());
                    try {
                        if (!message.getText().isEmpty()) {
                            try {
                                allKey.clear();
                                lineOne.clear();
                                var stmt = database.connect().createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT * FROM \"Question\"");
                                while (rs.next()) {
                                    if (rs.getString("question").toLowerCase(Locale.ROOT).contains(message.getText().toLowerCase(Locale.ROOT))) {
                                        lineOne.add(new KeyboardButton()
                                                .setAction(new KeyboardButtonAction()
                                                        .setLabel(rs.getString("question"))
                                                        .setType(TemplateActionTypeNames.TEXT))
                                                .setColor(KeyboardButtonColor.POSITIVE));
                                    }
                                }
                                if(!lineOne.isEmpty()){
                                    allKey.add(lineOne);
                                    keyboard.setButtons(allKey);
                                }
                                else {
                                    vk.messages()
                                            .send(actor)
                                            .message("Не удалось найти")
                                            .userId(message.getFromId())
                                            .keyboard(keyboard)
                                            .randomId(random.nextInt(10000))
                                            .execute();
                                }
                                vk.messages()
                                        .send(actor)
                                        .message("Что удалось найти")
                                        .userId(message.getFromId())
                                        .keyboard(keyboard)
                                        .randomId(random.nextInt(10000))
                                        .execute();
                                database.closeConnection();
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                        if (!message.getText().isEmpty()) {
                            var stmt = database.connect().createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT * FROM \"Question\"");
                            while (rs.next()) {
                                if (rs.getString("question").equals(message.getText())) {
                                    vk.messages()
                                            .send(actor)
                                            .message(rs.getString("answer"))
                                            .userId(message.getFromId())
                                            .randomId(random.nextInt(10000))
                                            .execute();
                                }
                            }
                            database.closeConnection();
                        }
                        if (message.getText().equals("Расшифровка аудитории")) {
                            vk.messages()
                                    .send(actor)
                                    .attachment("photo-213932615_457239020")
                                    .userId(message.getFromId())
                                    .randomId(random.nextInt(10000))
                                    .execute();
                        }
                        if (message.getText().equals("Стипендия")) {
                            vk.messages()
                                    .send(actor)
                                    .attachment("photo-213932615_457239017")
                                    .userId(message.getFromId())
                                    .randomId(random.nextInt(10000))
                                    .execute();
                        }
                        if (message.getText().equals("Кампус на Социалистической,162")) {
                            vk.messages()
                                    .send(actor)
                                    .attachment("photo-213932615_457239018")
                                    .userId(message.getFromId())
                                    .randomId(random.nextInt(10000))
                                    .execute();
                        }
                        if (message.getText().equals("Кампус на пл.Гагарина")) {
                            vk.messages()
                                    .send(actor)
                                    .attachment("photo-213932615_457239019")
                                    .userId(message.getFromId())
                                    .randomId(random.nextInt(10000))
                                    .execute();
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                });
            }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(500);
        }
    }
}
