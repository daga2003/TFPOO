package trabalho;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DiscordApp extends JFrame {
    private AppState appState;
    private JTree serverTree;
    private JTable messageTable;
    private JTextField messageField;

    public DiscordApp() {
        setTitle("Discord");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        appState = AppState.load();

        // Painel lateral: lista de servidores e canais
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Servidores");
        for (Server s : appState.getServers()) {
            DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(s.getName());
            for (String c : s.getChannels()) {
                serverNode.add(new DefaultMutableTreeNode(c));
            }
            root.add(serverNode);
        }
        serverTree = new JTree(root);
        serverTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) serverTree.getLastSelectedPathComponent();
            if (node == null || node.isRoot() || node.getParent() == null) return;

            String server = node.getParent().toString();
            String channel = node.toString();
            appState.setCurrent(server, channel);
            updateMessages();
        });

        // Painel principal: mensagens
        messageTable = new JTable(new DefaultTableModel(new Object[]{"Mensagens"}, 0));
        messageField = new JTextField();
        JButton sendButton = new JButton("Enviar");

        sendButton.addActionListener(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty() && appState.getCurrentServer() != null && appState.getCurrentChannel() != null) {
                appState.addMessage(appState.getProfileName() + ": " + text);
                updateMessages();
                messageField.setText("");
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Menu superior
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenu menuEditar = new JMenu("Editar");
        JMenu menuAjuda = new JMenu("Ajuda");

        JMenuItem salvarItem = new JMenuItem("Salvar");
        salvarItem.addActionListener(e -> appState.save());
        menuArquivo.add(salvarItem);

        JMenuItem editarPerfilItem = new JMenuItem("Editar Perfil");
        editarPerfilItem.addActionListener(e -> {
            String novoNome = JOptionPane.showInputDialog(this, "Digite o novo nome do perfil:", appState.getProfileName());
            if (novoNome != null && !novoNome.trim().isEmpty()) {
                appState.setProfileName(novoNome.trim());
                appState.save();
            }
        });
        menuEditar.add(editarPerfilItem);

        JMenuItem sobreItem = new JMenuItem("Sobre");
        sobreItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Discord Simples\nFeito em Java Swing\nTrabalho de POO", "Sobre", JOptionPane.INFORMATION_MESSAGE));
        menuAjuda.add(sobreItem);

        menuBar.add(menuArquivo);
        menuBar.add(menuEditar);
        menuBar.add(menuAjuda);
        setJMenuBar(menuBar);

        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(serverTree),
                new JScrollPane(messageTable));
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        updateMessages();

        // Ao fechar, salvar dados
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                appState.save();
            }
        });
    }

    private void updateMessages() {
        DefaultTableModel model = (DefaultTableModel) messageTable.getModel();
        model.setRowCount(0);
        for (String msg : appState.getMessages()) {
            model.addRow(new Object[]{msg});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DiscordApp().setVisible(true));
    }
}
