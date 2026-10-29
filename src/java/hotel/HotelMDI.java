package hotel;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Advanced Java - MDI (Multiple Document Interface) Desktop Application
public class HotelMDI extends JFrame {

    private final JDesktopPane desktopPane;
    private final HotelDao dao = new HotelDao();

    public HotelMDI() {
        setTitle("Hotel Room Reservation System - MDI Portal");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // MDI Desktop Container
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(44, 62, 80));
        add(desktopPane, BorderLayout.CENTER);

        // Build Menu Bar
        setJMenuBar(createMenuBar());

        // Automatically open the Room CRUD window on startup
        openRoomCrudFrame();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu 1: Rooms
        JMenu roomMenu = new JMenu("Rooms");
        JMenuItem manageRoomsItem = new JMenuItem("Manage Rooms (CRUD)");
        manageRoomsItem.addActionListener(e -> openRoomCrudFrame());
        roomMenu.add(manageRoomsItem);

        // Menu 2: Bookings
        JMenu bookingMenu = new JMenu("Bookings");
        JMenuItem searchBookItem = new JMenuItem("Search & Book Rooms");
        searchBookItem.addActionListener(e -> openBookingFrame());
        JMenuItem viewReservationsItem = new JMenuItem("View All Reservations");
        viewReservationsItem.addActionListener(e -> openReservationsFrame());
        bookingMenu.add(searchBookItem);
        bookingMenu.add(viewReservationsItem);

        // Menu 3: Exit
        JMenu exitMenu = new JMenu("System");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        exitMenu.add(exitItem);

        menuBar.add(roomMenu);
        menuBar.add(bookingMenu);
        menuBar.add(exitMenu);
        return menuBar;
    }

    // --- 1. CHILD WINDOW: ROOM CRUD ---
    private void openRoomCrudFrame() {
        JInternalFrame frame = new JInternalFrame("Room Management (CRUD)", true, true, true, true);
        frame.setSize(750, 480);
        frame.setLocation(30, 30);
        frame.setLayout(new BorderLayout(10, 10));

        // Table
        String[] columns = {"ID", "Room No", "Type", "Price / Night", "Capacity", "Active"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 6, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Room Details"));

        JTextField txtNo = new JTextField();
        JTextField txtType = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtCap = new JTextField();
        JCheckBox chkActive = new JCheckBox("Active", true);

        formPanel.add(new JLabel("Room No:"));
        formPanel.add(new JLabel("Type:"));
        formPanel.add(new JLabel("Price:"));
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(new JLabel("Status:"));
        formPanel.add(new JLabel("")); // Spacer

        formPanel.add(txtNo);
        formPanel.add(txtType);
        formPanel.add(txtPrice);
        formPanel.add(txtCap);
        formPanel.add(chkActive);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Add Room");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnRefresh = new JButton("Refresh");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(btnPanel, BorderLayout.SOUTH);
        frame.add(southPanel, BorderLayout.SOUTH);

        // Load Rooms Method
        Runnable loadRooms = () -> {
            try {
                model.setRowCount(0);
                List<Room> list = dao.getAllRooms();
                for (Room r : list) {
                    model.addRow(new Object[]{
                        r.getRoomId(), r.getRoomNumber(), r.getRoomType(),
                        r.getPricePerNight(), r.getCapacity(), r.isActive() ? "Yes" : "No"
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        // Table Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                txtNo.setText(model.getValueAt(row, 1).toString());
                txtType.setText(model.getValueAt(row, 2).toString());
                txtPrice.setText(model.getValueAt(row, 3).toString());
                txtCap.setText(model.getValueAt(row, 4).toString());
                chkActive.setSelected("Yes".equals(model.getValueAt(row, 5)));
            }
        });

        // Add
        btnAdd.addActionListener(e -> {
            try {
                Room r = new Room();
                r.setRoomNumber(txtNo.getText().trim());
                r.setRoomType(txtType.getText().trim());
                r.setPricePerNight(new BigDecimal(txtPrice.getText().trim()));
                r.setCapacity(Integer.parseInt(txtCap.getText().trim()));
                r.setActive(chkActive.isSelected());
                dao.addRoom(r);
                JOptionPane.showMessageDialog(frame, "Room added successfully!");
                loadRooms.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Validation/DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a room to update.");
                return;
            }
            try {
                int id = (int) model.getValueAt(row, 0);
                Room r = new Room(id, txtNo.getText().trim(), txtType.getText().trim(),
                        new BigDecimal(txtPrice.getText().trim()), Integer.parseInt(txtCap.getText().trim()), chkActive.isSelected());
                dao.updateRoom(r);
                JOptionPane.showMessageDialog(frame, "Room updated successfully!");
                loadRooms.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a room to delete.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(frame, "Delete room ID #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    dao.deleteRoom(id);
                    loadRooms.run();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRefresh.addActionListener(e -> loadRooms.run());

        loadRooms.run();
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    // --- 2. CHILD WINDOW: SEARCH & BOOK ---
    private void openBookingFrame() {
        JInternalFrame frame = new JInternalFrame("Search & Book Rooms", true, true, true, true);
        frame.setSize(750, 450);
        frame.setLocation(100, 60);
        frame.setLayout(new BorderLayout(10, 10));

        // Search Panel (North)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Check Dates"));
        JTextField txtIn = new JTextField(LocalDate.now().toString(), 10);
        JTextField txtOut = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
        JButton btnSearch = new JButton("Search Available Rooms");
        searchPanel.add(new JLabel("Check-in (YYYY-MM-DD):"));
        searchPanel.add(txtIn);
        searchPanel.add(new JLabel("Check-out (YYYY-MM-DD):"));
        searchPanel.add(txtOut);
        searchPanel.add(btnSearch);
        frame.add(searchPanel, BorderLayout.NORTH);

        // Available Rooms Table (Center)
        String[] columns = {"ID", "Room No", "Type", "Price / Night", "Capacity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Booking Panel (South)
        JPanel bookPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookPanel.setBorder(BorderFactory.createTitledBorder("Guest Booking"));
        JTextField txtGuest = new JTextField(12);
        JTextField txtPhone = new JTextField(10);
        JButton btnBook = new JButton("Confirm Reservation");
        bookPanel.add(new JLabel("Guest Name:"));
        bookPanel.add(txtGuest);
        bookPanel.add(new JLabel("Phone:"));
        bookPanel.add(txtPhone);
        bookPanel.add(btnBook);
        frame.add(bookPanel, BorderLayout.SOUTH);

        // Search Action
        btnSearch.addActionListener(e -> {
            try {
                LocalDate in = LocalDate.parse(txtIn.getText().trim());
                LocalDate out = LocalDate.parse(txtOut.getText().trim());
                if (!out.isAfter(in)) {
                    JOptionPane.showMessageDialog(frame, "Check-out must be after check-in.");
                    return;
                }
                model.setRowCount(0);
                List<Room> available = dao.getAvailableRooms(in, out);
                for (Room r : available) {
                    model.addRow(new Object[]{r.getRoomId(), r.getRoomNumber(), r.getRoomType(), r.getPricePerNight(), r.getCapacity()});
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format (use YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Book Action
        btnBook.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an available room from table.");
                return;
            }
            try {
                int roomId = (int) model.getValueAt(row, 0);
                String guest = txtGuest.getText().trim();
                String phone = txtPhone.getText().trim();
                LocalDate in = LocalDate.parse(txtIn.getText().trim());
                LocalDate out = LocalDate.parse(txtOut.getText().trim());

                if (guest.isEmpty() || phone.length() < 7) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid guest name and contact number.");
                    return;
                }

                dao.bookRoom(guest, phone, roomId, in, out);
                JOptionPane.showMessageDialog(frame, "Room booked successfully!");
                btnSearch.doClick(); // Refresh search
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Booking Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        desktopPane.add(frame);
        frame.setVisible(true);
    }

    // --- 3. CHILD WINDOW: VIEW RESERVATIONS ---
    private void openReservationsFrame() {
        JInternalFrame frame = new JInternalFrame("All Reservations", true, true, true, true);
        frame.setSize(800, 450);
        frame.setLocation(160, 90);
        frame.setLayout(new BorderLayout(10, 10));

        String[] columns = {"ID", "Guest Name", "Phone", "Room No", "Check-in", "Check-out", "Total (Rs.)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Controls
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel Selected Booking");
        JButton btnRefresh = new JButton("Refresh");
        btnPanel.add(btnCancel);
        btnPanel.add(btnRefresh);
        frame.add(btnPanel, BorderLayout.SOUTH);

        Runnable loadData = () -> {
            try {
                model.setRowCount(0);
                List<HotelDao.ReservationRecord> list = dao.getAllReservations();
                for (HotelDao.ReservationRecord r : list) {
                    model.addRow(new Object[]{
                        r.getReservationId(), r.getGuestName(), r.getPhone(),
                        r.getRoomNumber(), r.getCheckIn(), r.getCheckOut(),
                        r.getTotalAmount(), r.getBookingStatus()
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a reservation to cancel.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            String status = model.getValueAt(row, 7).toString();
            if ("Cancelled".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(frame, "This reservation is already cancelled.");
                return;
            }
            if (JOptionPane.showConfirmDialog(frame, "Cancel booking #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    dao.cancelReservation(id);
                    JOptionPane.showMessageDialog(frame, "Reservation #" + id + " cancelled!");
                    loadData.run();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRefresh.addActionListener(e -> loadData.run());

        loadData.run();
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Set Look and Feel to System native
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new HotelMDI().setVisible(true));
    }
}
