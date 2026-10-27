package com.hotel.desktop;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

// Desktop application (Swing GUI) for room CRUD on the same hotel_db database.
// Button clicks use lambda expressions, rooms are kept in a Java List.
public class HotelDesktopApp extends JFrame {

    private final RoomDao roomDao = new RoomDao();
    private List<Room> rooms;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Room No", "Type", "Price", "Capacity", "Active"}, 0);
    private final JTable table = new JTable(model);

    private final JTextField roomNumberField = new JTextField();
    private final JTextField roomTypeField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField capacityField = new JTextField();
    private final JCheckBox activeBox = new JCheckBox("Active", true);

    public HotelDesktopApp() {
        super("Hotel Rooms - Desktop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Room Number:"));
        form.add(roomNumberField);
        form.add(new JLabel("Room Type:"));
        form.add(roomTypeField);
        form.add(new JLabel("Price Per Night:"));
        form.add(priceField);
        form.add(new JLabel("Capacity:"));
        form.add(capacityField);
        form.add(new JLabel(""));
        form.add(activeBox);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        // Lambda expressions for button clicks
        addButton.addActionListener(e -> addRoom());
        updateButton.addActionListener(e -> updateRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadRooms());

        // Lambda expression: copy the selected table row into the form
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadRooms();
    }

    private void loadRooms() {
        try {
            rooms = roomDao.getAllRooms();
            model.setRowCount(0);
            // Lambda expression: add one table row for every room in the list
            rooms.forEach(room -> model.addRow(new Object[]{
                room.getRoomId(), room.getRoomNumber(), room.getRoomType(),
                room.getPricePerNight(), room.getCapacity(), room.isActive() ? "Yes" : "No"
            }));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot load rooms. Check MySQL and the password in DBConnect.java\n" + ex.getMessage());
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0 || rooms == null || row >= rooms.size()) {
            return;
        }
        Room room = rooms.get(row);
        roomNumberField.setText(room.getRoomNumber());
        roomTypeField.setText(room.getRoomType());
        priceField.setText(room.getPricePerNight().toString());
        capacityField.setText(String.valueOf(room.getCapacity()));
        activeBox.setSelected(room.isActive());
    }

    private Room readForm() {
        Room room = new Room();
        room.setRoomNumber(roomNumberField.getText().trim());
        room.setRoomType(roomTypeField.getText().trim());
        room.setPricePerNight(new BigDecimal(priceField.getText().trim()));
        room.setCapacity(Integer.parseInt(capacityField.getText().trim()));
        room.setActive(activeBox.isSelected());
        return room;
    }

    private void addRoom() {
        try {
            roomDao.addRoom(readForm());
            loadRooms();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cannot add room.\n" + ex.getMessage());
        }
    }

    private void updateRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room first.");
            return;
        }
        try {
            Room room = readForm();
            room.setRoomId(rooms.get(row).getRoomId());
            roomDao.updateRoom(room);
            loadRooms();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cannot update room.\n" + ex.getMessage());
        }
    }

    private void deleteRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room first.");
            return;
        }
        try {
            roomDao.deleteRoom(rooms.get(row).getRoomId());
            loadRooms();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Cannot delete room.\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelDesktopApp().setVisible(true));
    }
}
