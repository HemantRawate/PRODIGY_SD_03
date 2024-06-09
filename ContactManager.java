import java.io.Serializable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Contact implements Serializable {
    private String name;
    private String phoneNumber;
    private String email;

    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return name + " - " + phoneNumber + " - " + email;
    }
}

class ContactManager {
    private List<Contact> contacts;
    private static final String FILE_NAME = "contacts.ser";

    public ContactManager() {
        contacts = new ArrayList<>();
        loadContacts();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
        saveContacts();
    }

    public void editContact(int index, Contact contact) {
        contacts.set(index, contact);
        saveContacts();
    }

    public void deleteContact(int index) {
        contacts.remove(index);
        saveContacts();
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    private void saveContacts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContacts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            contacts = (List<Contact>) ois.readObject();
        } catch (FileNotFoundException e) {
            // No action needed, file will be created on save
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

public class ContactManagerGUI extends JFrame {
    private ContactManager contactManager;
    private DefaultListModel<Contact> contactListModel;
    private JList<Contact> contactList;
    private JTextField nameField, phoneField, emailField;
    private JButton addButton, editButton, deleteButton;

    public ContactManagerGUI() {
        contactManager = new ContactManager();
        createView();

        setTitle("Contact Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createView() {
        JPanel panel = new JPanel(new BorderLayout());
        getContentPane().add(panel);

        contactListModel = new DefaultListModel<>();
        contactManager.getContacts().forEach(contactListModel::addElement);

        contactList = new JList<>(contactListModel);
        panel.add(new JScrollPane(contactList), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);
        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        addButton.addActionListener(new AddButtonActionListener());
        buttonPanel.add(addButton);
        editButton = new JButton("Edit");
        editButton.addActionListener(new EditButtonActionListener());
        buttonPanel.add(editButton);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new DeleteButtonActionListener());
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private class AddButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
                Contact contact = new Contact(name, phone, email);
                contactManager.addContact(contact);
                contactListModel.addElement(contact);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(ContactManagerGUI.this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class EditButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = contactList.getSelectedIndex();
            if (selectedIndex != -1) {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
                    Contact contact = new Contact(name, phone, email);
                    contactManager.editContact(selectedIndex, contact);
                    contactListModel.set(selectedIndex, contact);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(ContactManagerGUI.this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(ContactManagerGUI.this, "Please select a contact to edit", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = contactList.getSelectedIndex();
            if (selectedIndex != -1) {
                contactManager.deleteContact(selectedIndex);
                contactListModel.remove(selectedIndex);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(ContactManagerGUI.this, "Please select a contact to delete", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ContactManagerGUI().setVisible(true);
        });
    }
}

