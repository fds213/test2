import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * 청주대 셔틀버스 좌석 예약 프로그램입니다.
 *
 * @author fds213 (junwon12352@naver.com)
 * @version 2.2
 * @created 2024-12-17
 * @lastModified 2024-12-24
 * @changelog <ul>
 * <li>2024-12-17: 최초 생성 (fds213)</li>
 * <li>2024-12-23: scanner로 입출력하는 방식에서 gui 버전으로 수정</li>
 * </ul>
 */
public class ShuttleBusManager {
    private static final String FILE_PATH = "Students.txt";
    private static final int TOTAL_SEATS = 45;
    private static final Map<Integer, String[]> reservations = new HashMap<>();
    private static final Map<String, List<String>> schedule = new HashMap<>();

    private static JTextArea textArea;
    private static JComboBox<String> scheduleComboBox;
    private static JTextField seatNumberField;
    private static JTextField passengerNameField;
    private static JComboBox<String> timeComboBox;

    static {
        schedule.put("생활관-정문", Arrays.asList(
                "8:10 학교버스 2", "8:20 학교버스 1", "8:30 학교버스 2", "8:40 학교버스 1", "8:50 학교버스 2",
                "9:00 학교버스 1", "9:10 학교버스 2", "9:20 미래로관광", "9:30 학교버스 1", "9:40 학교버스 2",
                "9:50 학교버스 1", "10:00 학교버스 2", "10:10 학교버스 1", "10:30 학교버스 2", "10:50 학교버스 1",
                "11:10 학교버스 2", "11:20 학교버스 1", "11:40 학교버스 2", "12:40 미래로관광", "12:50 학교버스 1",
                "13:00 학교버스 2", "13:20 학교버스 1", "13:40 학교버스 2", "14:20 학교버스 1", "14:40 학교버스 2",
                "15:20 학교버스 1", "15:40 학교버스 2", "16:20 학교버스 1", "16:40 학교버스 2", "17:20 학교버스 1",
                "17:40 학교버스 2", "17:50 학교버스 1"
        ));
        schedule.put("정문-생활관", Arrays.asList(
                "8:10 학교버스 1", "8:20 학교버스 2", "8:30 학교버스 1", "8:40 학교버스 2", "8:50 학교버스 1",
                "9:00 학교버스 2", "9:10 학교버스 1", "9:20 학교버스 2", "9:30 미래로관광", "9:40 학교버스 1",
                "9:50 학교버스 2", "10:00 학교버스 1", "10:10 학교버스 2", "10:30 학교버스 1", "10:50 학교버스 2",
                "11:10 학교버스 1", "11:20 학교버스 2", "11:40 학교버스 1", "12:20 미래로관광", "12:50 학교버스 2",
                "13:00 학교버스 1", "13:20 학교버스 2", "13:40 학교버스 1", "14:20 학교버스 2", "14:40 학교버스 1",
                "15:20 학교버스 2", "15:40 학교버스 1", "16:20 학교버스 2", "16:40 학교버스 1", "17:20 학교버스 2",
                "17:40 학교버스 1"
        ));
    }

    public static void saveReservationsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<Integer, String[]> entry : reservations.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue()[0] + "," + entry.getValue()[1]);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "예약 정보가 파일에 저장되었습니다.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public static void loadReservationsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int seatNumber = Integer.parseInt(parts[0]);
                String passenger = parts[1];
                String time = parts[2];
                reservations.put(seatNumber, new String[]{passenger, time});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    public static void reserveSeat(int seatNumber, String passenger, String time) {
        if (reservations.containsKey(seatNumber)) {
            JOptionPane.showMessageDialog(null, "이미 예약된 좌석입니다.");
        } else if (seatNumber < 1 || seatNumber > TOTAL_SEATS) {
            JOptionPane.showMessageDialog(null, "잘못된 좌석 번호입니다.");
        } else {
            reservations.put(seatNumber, new String[]{passenger, time});
            JOptionPane.showMessageDialog(null, "좌석 " + seatNumber + "이(가) " + passenger + "님께 예약되었습니다.");
            updateReservationsDisplay();
        }
    }

    public static void cancelReservation(int seatNumber) {
        if (reservations.containsKey(seatNumber)) {
            reservations.remove(seatNumber);
            JOptionPane.showMessageDialog(null, "좌석 " + seatNumber + " 예약이 취소되었습니다.");
            updateReservationsDisplay();
        } else {
            JOptionPane.showMessageDialog(null, "취소할 예약이 존재하지 않습니다.");
        }
    }

    public static void updateReservationsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= TOTAL_SEATS; i++) {
            if (reservations.containsKey(i)) {
                String[] reservation = reservations.get(i);
                sb.append("좌석 ").append(i).append(": ").append(reservation[0]).append(" (시간: ").append(reservation[1]).append(")\n");
            } else {
                sb.append("좌석 ").append(i).append(": 비어 있음\n");
            }
        }
        textArea.setText(sb.toString());
    }

    public static void updateScheduleDisplay(String route) {
        List<String> times = schedule.get(route);
        if (times != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(route).append(" 시간표:\n");
            for (String time : times) {
                sb.append(time).append("\n");
            }
            textArea.setText(sb.toString());

            timeComboBox.removeAllItems();
            for (String time : times) {
                timeComboBox.addItem(time);
            }
        }
    }

    public static void viewReservationsByTime(String time) {
        StringBuilder sb = new StringBuilder();
        sb.append("선택한 시간(").append(time).append(")의 예약 정보:\n");
        boolean found = false;

        for (Map.Entry<Integer, String[]> entry : reservations.entrySet()) {
            int seatNumber = entry.getKey();
            String[] reservation = entry.getValue();
            if (reservation[1].equals(time)) {
                sb.append("좌석 ").append(seatNumber).append(": ").append(reservation[0]).append("\n");
                found = true;
            }
        }

        if (!found) {
            sb.append("선택한 시간에 예약된 좌석이 없습니다.");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public static void main(String[] args) {
        loadReservationsFromFile();

        JFrame frame = new JFrame("청주대 셔틀버스 좌석 예약");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        panel.add(new JLabel("노선 선택:"));
        scheduleComboBox = new JComboBox<>(schedule.keySet().toArray(new String[0]));
        scheduleComboBox.addActionListener(e -> {
            String selectedRoute = (String) scheduleComboBox.getSelectedItem();
            updateScheduleDisplay(selectedRoute);
        });
        panel.add(scheduleComboBox);

        panel.add(new JLabel("시간 선택:"));
        timeComboBox = new JComboBox<>();
        panel.add(timeComboBox);

        panel.add(new JLabel("좌석 번호:"));
        seatNumberField = new JTextField();
        panel.add(seatNumberField);

        panel.add(new JLabel("예약자 이름:"));
        passengerNameField = new JTextField();
        panel.add(passengerNameField);

        frame.add(panel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton reserveButton = new JButton("예약");
        reserveButton.addActionListener(e -> {
            try {
                int seatNumber = Integer.parseInt(seatNumberField.getText());
                String passenger = passengerNameField.getText();
                String time = (String) timeComboBox.getSelectedItem();
                reserveSeat(seatNumber, passenger, time);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "좌석 번호는 숫자로 입력해야 합니다.");
            }
        });

        JButton cancelButton = new JButton("예약 취소");
        cancelButton.addActionListener(e -> {
            try {
                int seatNumber = Integer.parseInt(seatNumberField.getText());
                cancelReservation(seatNumber);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "좌석 번호는 숫자로 입력해야 합니다.");
            }
        });


        JButton saveButton = new JButton("저장");
        saveButton.addActionListener(e -> saveReservationsToFile());

        buttonPanel.add(reserveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

    }
}
