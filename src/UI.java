import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class UI extends JFrame {

    private final int WINDOW_WIDTH = 970;
    private final int WINDOW_HEIGHT = 550;

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel userRoleLabel;
    private JLabel messageLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userRoleComboBox;
    private JButton registerUserButton;
    private JButton updateUserButton;
    private JButton deleteUserButton;
    private JButton clearFormButton;

    private final Controller controller = new Controller();
    private int selectedUserId;

    public UI() {
        super();
        initComponents();
    }

    private void initComponents() {
        final String[] userRoles = {"User", "Admin"};
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Initialize SWING components/controls
        titleLabel = new JLabel("ADD A NEW USER");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        userRoleLabel = new JLabel("Role:");
        messageLabel = new JLabel();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        userRoleComboBox = new JComboBox<>(userRoles);
        registerUserButton = new JButton("REGISTER");
        updateUserButton = new JButton("UPDATE");
        deleteUserButton = new JButton("DELETE");
        clearFormButton = new JButton("CLEAR FORM");

        // Set properties for the controls
        titleLabel.setBounds(40, 40, 280, 30);
        usernameLabel.setBounds(40, 90, 80, 30);
        usernameField.setBounds(120, 90, 200, 30);
        passwordLabel.setBounds(40, 150, 80, 30);
        passwordField.setBounds(120, 150, 200, 30);
        userRoleLabel.setBounds(40, 210, 80, 30);
        userRoleComboBox.setBounds(120, 210, 200, 30);
        registerUserButton.setBounds(120, 270, 120, 30);
        updateUserButton.setBounds(120, 310, 120, 30);
        deleteUserButton.setBounds(120, 350, 120, 30);
        clearFormButton.setBounds(120, 390, 120, 30);
        messageLabel.setBounds(120, 440, 200, 30);

        // Add components to the panel
        mainPanel.add(titleLabel);
        mainPanel.add(usernameLabel);
        mainPanel.add(passwordLabel);
        mainPanel.add(userRoleLabel);
        mainPanel.add(messageLabel);
        mainPanel.add(usernameField);
        mainPanel.add(passwordField);
        mainPanel.add(userRoleComboBox);
        mainPanel.add(registerUserButton);
        mainPanel.add(updateUserButton);
        mainPanel.add(deleteUserButton);
        mainPanel.add(clearFormButton);

        // display existing users
        try {
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Event listeners
        registerUserButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());
            int selectedIndex = userRoleComboBox.getSelectedIndex();
            String role = userRoleComboBox.getItemAt(selectedIndex);
            try {
                Boolean isUserRegistered = controller.registerUser(username, password, role);
                if(isUserRegistered) {
                    clearForm();
                    showSuccessMessage("New user added!");
                    controller.setTimeout(2500, this::removeSuccessMessage);
                    loadUsers(); // update users table
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        updateUserButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());
            String role = userRoleComboBox.getItemAt(userRoleComboBox.getSelectedIndex());
            try {
                Boolean updated = controller.updateUser(new User(selectedUserId, username, password, role));
                if(updated) {
                    clearForm();
                    showSuccessMessage("Update successful!");
                    controller.setTimeout(2500, this::removeSuccessMessage);
                    loadUsers();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        deleteUserButton.addActionListener(e -> {
            try {
                Boolean deleted = controller.deleteUser(selectedUserId);
                if(deleted) {
                    clearForm();
                    showSuccessMessage("User deleted!");
                    controller.setTimeout(2500, this::removeSuccessMessage);
                    loadUsers();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        clearFormButton.addActionListener(e -> {
            clearForm();
        });

        // window properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("User Registration");
        this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setLocationRelativeTo(null); // center window
        this.getContentPane().add(mainPanel);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

    private void loadUsers() throws SQLException {
        final String[] columnNames = {
                "ID",
                "Name",
                "Password",
                "Role"
        };

        ArrayList<User> userList = controller.queryUsers();
        int cols = columnNames.length;
        int rows = userList.size();
        String[][] tableData = new String[rows][cols];

        for(int i = 0; i < rows; i++) {
            User user = userList.get(i);
            tableData[i] = user.toStringArray();
        }

        JTable usersTable = new JTable(tableData, columnNames);
        usersTable.setBounds(0, 0, 500, 350);
        usersTable.setDefaultEditor(Object.class, null);

        usersTable.getSelectionModel().addListSelectionListener(e -> {
            int row = usersTable.getSelectedRow();
            selectedUserId = Integer.parseInt(usersTable.getValueAt(row, 0).toString());
            usernameField.setText(usersTable.getValueAt(row, 1).toString());
            passwordField.setText(usersTable.getValueAt(row, 2).toString());
            userRoleComboBox.setSelectedItem(usersTable.getValueAt(row, 3).toString());
        });

        // allow table to scroll
        JScrollPane sp = new JScrollPane(usersTable);
        sp.setBounds(400, 90, 500, 350);
        mainPanel.add(sp);
    }

    private void showSuccessMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setForeground(new Color(12, 184, 12));
    }

    private void removeSuccessMessage() {
        messageLabel.setText("");
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        userRoleComboBox.setSelectedIndex(0);
        usernameField.requestFocusInWindow();
    }
}
