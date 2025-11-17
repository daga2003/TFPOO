package trabalho;

import java.util.*;

public class Server {
    private String name;
    private List<String> channels;
    private Map<String, List<String>> messages = new HashMap<>();

    public Server(String name, List<String> channels) {
        this.name = name;
        this.channels = new ArrayList<>(channels);
        for (String c : channels) {
            messages.put(c, new ArrayList<>(List.of("Sistema: Bem-vindo ao canal " + c + "!")));
        }
    }

    public String getName() { return name; }
    public List<String> getChannels() { return channels; }

    public void addMessage(String channel, String message) {
        messages.computeIfAbsent(channel, k -> new ArrayList<>()).add(message);
    }

    public List<String> getMessages(String channel) {
        return messages.getOrDefault(channel, new ArrayList<>());
    }
}