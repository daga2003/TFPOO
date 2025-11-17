package trabalho;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;

public class AppState {
    private List<Server> servers = new ArrayList<>();
    private String profileName = "Usuário";
    private String currentServer;
    private String currentChannel;
    private static final String DATA_FILE = "data.json";

    public AppState() {
        if (servers.isEmpty()) {
            Server s = new Server("Servidor da Turma", Arrays.asList("geral", "projetos"));
            servers.add(s);
        }
    }

    public static AppState load() {
        try (Reader reader = new FileReader(DATA_FILE)) {
            return new Gson().fromJson(reader, AppState.class);
        } catch (IOException e) {
            return new AppState();
        }
    }

    public void save() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrent(String server, String channel) {
        this.currentServer = server;
        this.currentChannel = channel;
    }

    public void addMessage(String message) {
        getCurrentServerObj().addMessage(currentChannel, message);
        save();
    }

    public List<String> getMessages() {
        return getCurrentServerObj().getMessages(currentChannel);
    }

    private Server getCurrentServerObj() {
        for (Server s : servers) {
            if (s.getName().equals(currentServer)) return s;
        }
        return servers.get(0);
    }

    // Getters e Setters
    public List<Server> getServers() { return servers; }
    public String getProfileName() { return profileName; }
    public void setProfileName(String n) { profileName = n; }
    public String getCurrentServer() { return currentServer; }
    public String getCurrentChannel() { return currentChannel; }
}