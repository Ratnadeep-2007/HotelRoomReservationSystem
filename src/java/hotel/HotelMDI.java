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
// 1-to-1 equivalent to the 3 JSP pages (index.jsp, rooms.jsp, reservations.jsp)
public class HotelMDI extends JFrame {

    private final JDesktopPane desktopPane;
    private final HotelDao dao = new HotelDao();

    // In-memory Session Guest object (Equivalent to HttpSession in index.jsp - Lab 8)
    private Person.Guest sessionGuest = null;

    public HotelMDI() {
        setTitle("Hotel Room Reservation System - MDI Portal");
        setSize(1050, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // MDI Desktop Container
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(244, 247, 246));
        add(desktopPane, BorderLayout.CENTER);

        // Build Menu Bar
        setJMenuBar(createMenuBar());

        // Startup window: exactly matches index.jsp
        openHomePortalFrame();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu navMenu = new JMenu("Navigation");
        JMenuItem homeItem = new JMenuItem("Home / Customer Portal (index.jsp)");
        homeItem.addActionListener(e -> openHomePortalFrame());
        JMenuItem manageRoomsItem = new JMenuItem("Manage Rooms CRUD (rooms.jsp)");
        manageRoomsItem.addActionListener(e -> openRoomCrudFrame());
        JMenuItem viewReservationsItem = new JMenuItem("View Reservations (reservations.jsp)");
        viewReservationsItem.addActionListener(e -> openReservationsFrame());

        navMenu.add(homeItem);
        navMenu.add(manageRoomsItem);
        navMenu.add(viewReservationsItem);

        JMenu systemMenu = new JMenu("System");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        systemMenu.add(exitItem);

        menuBar.add(navMenu);
        menuBar.add(systemMenu);
        return menuBar;
    }

    // =========================================================================
    // 1. HOME / CUSTOMER PORTAL (Exact Desktop Equivalent of index.jsp)
    // =========================================================================
    public void openHomePortalFrame() {
        JInternalFrame frame = new JInternalFrame("Home Portal (index.jsp equivalent)", true, true, true, true);
        frame.setSize(850, 620);
        frame.setLocation(40, 20);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Navigation Shortcut Buttons
        JPanel navShortcut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnGoRooms = new JButton("-> Manage Rooms (CRUD)");
        JButton btnGoRes = new JButton("-> View All Reservations");
        btnGoRooms.addActionListener(e -> openRoomCrudFrame());
        btnGoRes.addActionListener(e -> openReservationsFrame());
        navShortcut.add(new JLabel("Quick Links: "));
        navShortcut.add(btnGoRooms);
        navShortcut.add(btnGoRes);
        mainPanel.add(navShortcut);

        // --- SECTION 1: GUEST SESSION PROFILE (Lab 8) ---
        JPanel guestPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        guestPanel.setBorder(BorderFactory.createTitledBorder("1. Guest Session Profile (HttpSession - Lab 8)"));

        JTextField txtGuestName = new JTextField(sessionGuest != null ? sessionGuest.getName() : "");
        JTextField txtGuestPhone = new JTextField(sessionGuest != null ? sessionGuest.getPhone() : "");
        JComboBox<String> cmbRoomType = new JComboBox<>(new String[]{"Single", "Double", "Deluxe", "Suite"});
        if (sessionGuest != null && sessionGuest.getRoomType() != null) {
            cmbRoomType.setSelectedItem(sessionGuest.getRoomType());
        }
        JButton btnSaveGuest = new JButton("Save to Session");

        JLabel lblSessionStatus = new JLabel(sessionGuest != null
                ? "Active: " + sessionGuest.getName() + " (" + sessionGuest.getRoomType() + ")"
                : "No active guest in session.");
        lblSessionStatus.setForeground(sessionGuest != null ? new Color(17, 120, 100) : Color.GRAY);

        guestPanel.add(new JLabel("Full Name:"));
        guestPanel.add(new JLabel("Phone:"));
        guestPanel.add(new JLabel("Preference:"));
        guestPanel.add(new JLabel("Status:"));

        guestPanel.add(txtGuestName);
        guestPanel.add(txtGuestPhone);
        guestPanel.add(cmbRoomType);
        guestPanel.add(btnSaveGuest);

        mainPanel.add(guestPanel);
        mainPanel.add(lblSessionStatus);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- SECTION 2: SEARCH AVAILABLE ROOMS ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("2. Search & Book Rooms"));
        JTextField txtIn = new JTextField(LocalDate.now().toString(), 10);
        JTextField txtOut = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
        JButton btnSearch = new JButton("Check Availability");

        searchPanel.add(new JLabel("Check-in:"));
        searchPanel.add(txtIn);
        searchPanel.add(new JLabel("Check-out:"));
        searchPanel.add(txtOut);
        searchPanel.add(btnSearch);
        mainPanel.add(searchPanel);

        // --- SECTION 3: AVAILABLE ROOMS LIST ---
        String[] columns = {"ID", "Room No", "Type", "Price / Night", "Capacity"};
        DefaultTableModel searchModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable searchTable = new JTable(searchModel);
        JScrollPane scrollSearch = new JScrollPane(searchTable);
        scrollSearch.setPreferredSize(new Dimension(800, 180));
        scrollSearch.setBorder(BorderFactory.createTitledBorder("Available Rooms (Select one to book)"));
        mainPanel.add(scrollSearch);

        // --- SECTION 4: BOOKING CONFIRMATION FORM ---
        JPanel bookForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookForm.setBorder(BorderFactory.createTitledBorder("Confirm Booking"));
        JTextField txtBookName = new JTextField(txtGuestName.getText(), 14);
        JTextField txtBookPhone = new JTextField(txtGuestPhone.getText(), 12);
        JButton btnBook = new JButton("Book Selected Room");

        bookForm.add(new JLabel("Guest Name:"));
        bookForm.add(txtBookName);
        bookForm.add(new JLabel("Phone:"));
        bookForm.add(txtBookPhone);
        bookForm.add(btnBook);
        mainPanel.add(bookForm);

        // Action: Save Guest in Session
        btnSaveGuest.addActionListener(e -> {
            String name = txtGuestName.getText().trim();
            String phone = txtGuestPhone.getText().trim();
            String type = (String) cmbRoomType.getSelectedItem();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both name and phone number.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            sessionGuest = new Person.Guest(name, phone, type);
            txtBookName.setText(name);
            txtBookPhone.setText(phone);
            lblSessionStatus.setText("Active: " + name + " (" + type + ")");
            lblSessionStatus.setForeground(new Color(17, 120, 100));
            JOptionPane.showMessageDialog(frame, "Guest profile saved to session scope!");
        });

        // Action: Search Available Rooms
        btnSearch.addActionListener(e -> {
            try {
                LocalDate in = LocalDate.parse(txtIn.getText().trim());
                LocalDate out = LocalDate.parse(txtOut.getText().trim());
                if (in.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(frame, "Check-in date cannot be in the past.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!out.isAfter(in)) {
                    JOptionPane.showMessageDialog(frame, "Check-out date must be after check-in date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                searchModel.setRowCount(0);
                List<Room> available = dao.getAvailableRooms(in, out);
                for (Room r : available) {
                    searchModel.addRow(new Object[]{r.getRoomId(), r.getRoomNumber(), r.getRoomType(), "Rs. " + r.getPricePerNight(), r.getCapacity()});
                }
                if (available.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No rooms available for selected dates.");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Please use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action: Book Selected Room
        btnBook.addActionListener(e -> {
            int row = searchTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an available room from the table above.");
                return;
            }
            try {
                int roomId = (int) searchModel.getValueAt(row, 0);
                String name = txtBookName.getText().trim();
                String phone = txtBookPhone.getText().trim();
                LocalDate in = LocalDate.parse(txtIn.getText().trim());
                LocalDate out = LocalDate.parse(txtOut.getText().trim());

                if (name.isEmpty() || phone.length() < 7) {
                    JOptionPane.showMessageDialog(frame, "Enter a valid guest name and contact phone number (at least 7 digits).");
                    return;
                }

                dao.bookRoom(name, phone, roomId, in, out);
                JOptionPane.showMessageDialog(frame, "Room booked successfully! Opening reservations...");
                btnSearch.doClick(); // Refresh search table
                openReservationsFrame(); // Open reservations list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Booking Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    // =========================================================================
    // 2. ROOM CRUD WINDOW (Exact Desktop Equivalent of rooms.jsp)
    // =========================================================================
    public void openRoomCrudFrame() {
        JInternalFrame frame = new JInternalFrame("Room Management - CRUD (rooms.jsp equivalent)", true, true, true, true);
        frame.setSize(800, 500);
        frame.setLocation(80, 50);
        frame.setLayout(new BorderLayout(10, 10));

        // 1. Table
        String[] columns = {"ID", "Room No", "Type", "Price / Night", "Capacity", "Active"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. Add / Edit Form Panel (Directly below table, like rooms.jsp)
        JPanel formPanel = new JPanel(new GridLayout(2, 6, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Room Form"));

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
        formPanel.add(new JLabel(""));

        formPanel.add(txtNo);
        formPanel.add(txtType);
        formPanel.add(txtPrice);
        formPanel.add(txtCap);
        formPanel.add(chkActive);

        // Buttons
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

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a room to update.");
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

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "Select a room to delete.");
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

    // =========================================================================
    // 3. RESERVATIONS WINDOW (Exact Desktop Equivalent of reservations.jsp)
    // =========================================================================
    public void openReservationsFrame() {
        JInternalFrame frame = new JInternalFrame("Reservation Records (reservations.jsp equivalent)", true, true, true, true);
        frame.setSize(850, 480);
        frame.setLocation(120, 80);
        frame.setLayout(new BorderLayout(10, 10));

        String[] columns = {"ID", "Guest Name", "Phone", "Room No", "Check-in", "Check-out", "Total (Rs.)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new HotelMDI().setVisible(true));
    }
}
