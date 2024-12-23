import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * 청주대 셔틀버스 좌석 예약 프로그램입니다.
 *
 * @author fds213 (junwon12352@naver.com)
 * @version 2.4
 * @created 2024-12-17
 * @lastModified 2024-12-25
 * @changelog <ul>
 * <li>2024-12-17: 최초 생성 (fds213)</li>
 * <li>2024-12-23: scanner로 입출력하는 방식에서 gui 버전으로 수정</li>
 * <li>2024-12-24: 시간별 예약 조회기능 추가</li>
 * <li>2024-12-25: 예약 취소 버튼 개선</li>
 * </ul>
 */
public class ShuttleBusManager {
    private static final String FILE_PATH = "Students.txt";
    private static final int TOTAL_SEATS = 45;
    private static final Map<String, Map<String, Map<Integer, String>>> reservations = new HashMap<>();
    private static final Map<String, List<String>> schedule = new HashMap<>();

    private static JTextArea textArea;
    private static JComboBox<String> scheduleComboBox;
    private static JTextField seatNumberField;
    private static JTextField passengerNameField;
    private static JComboBox<String> timeComboBox;
    private static JTextField studentIdField;

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
            for (Map.Entry<String, Map<String, Map<Integer, String>>> routeEntry : reservations.entrySet()) {
                String route = routeEntry.getKey();
                for (Map.Entry<String, Map<Integer, String>> timeEntry : routeEntry.getValue().entrySet()) {
                    String time = timeEntry.getKey();
                    for (Map.Entry<Integer, String> seatEntry : timeEntry.getValue().entrySet()) {
                        int seatNumber = seatEntry.getKey();
                        String passenger = seatEntry.getValue();
                        writer.write(route + "," + time + "," + seatNumber + "," + passenger);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public static void loadReservationsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String route = parts[0];
                    String time = parts[1];
                    int seatNumber = Integer.parseInt(parts[2]);
                    String passenger = parts[3];

                    reservations.putIfAbsent(route, new HashMap<>());
                    reservations.get(route).putIfAbsent(time, new HashMap<>());
                    reservations.get(route).get(time).put(seatNumber, passenger);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    public static void reserveSeat(int seatNumber, String passenger, String route, String time, String studentId) {
        reservations.putIfAbsent(route, new HashMap<>());
        reservations.get(route).putIfAbsent(time, new HashMap<>());

        Map<Integer, String> seats = reservations.get(route).get(time);

        if (seats.containsKey(seatNumber)) {
            JOptionPane.showMessageDialog(null, "이미 예약된 좌석입니다.");
        } else if (seatNumber < 1 || seatNumber > TOTAL_SEATS) {
            JOptionPane.showMessageDialog(null, "잘못된 좌석 번호입니다.");
        } else {
            seats.put(seatNumber, passenger + " (" + studentId + ")");
            saveReservationsToFile();
            JOptionPane.showMessageDialog(null, seatNumber + "번 좌석이 " + passenger + "님(" + studentId + ")으로 예약되었습니다.");
            updateReservationsDisplay(route, time);
        }
    }

    public static void cancelReservation(String studentId, String passengerName, String route, String time) {
        Map<Integer, String> seats = reservations.getOrDefault(route, Collections.emptyMap())
                .getOrDefault(time, Collections.emptyMap());

        boolean found = false;
        for (Map.Entry<Integer, String> entry : seats.entrySet()) {
            if (entry.getValue().equals(passengerName + " (" + studentId + ")")) {
                seats.remove(entry.getKey());
                found = true;
                saveReservationsToFile();
                JOptionPane.showMessageDialog(null, "예약이 취소되었습니다. 좌석 번호: " + entry.getKey());
                updateReservationsDisplay(route, time);
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "입력한 정보와 일치하는 예약이 없습니다.");
        }
    }

    public static void updateReservationsDisplay(String route, String time) {
        StringBuilder sb = new StringBuilder();

        Map<Integer, String> seats = reservations.getOrDefault(route, Collections.emptyMap())
                .getOrDefault(time, Collections.emptyMap());

        for (int i = 1; i <= TOTAL_SEATS; i++) {
            if (seats.containsKey(i)) {
                sb.append("좌석 ").append(i).append(": ").append(seats.get(i)).append("\n");
            } else {
                sb.append("좌석 ").append(i).append(": 비어 있음\n");
            }
        }
        textArea.setText(sb.toString());
    }

    public static void updateScheduleDisplay(String route) {
        timeComboBox.removeAllItems();
        if (route != null && !route.isEmpty()) {
            for (String time : schedule.get(route)) {
                timeComboBox.addItem(time);
            }
        }
    }

    public static Map<Integer, String> getReservationsForTime(String route, String time) {
        Map<Integer, String> reservationsForTime = new HashMap<>();
        Map<Integer, String> seats = reservations.getOrDefault(route, Collections.emptyMap())
                .getOrDefault(time, Collections.emptyMap());

        for (Map.Entry<Integer, String> entry : seats.entrySet()) {
            reservationsForTime.put(entry.getKey(), entry.getValue());
        }

        return reservationsForTime;
    }


    public static void viewReservationsByTime(String route, String time) {
        StringBuilder reservationsInfo = new StringBuilder("예약 내역 (" + route + " - " + time + "):\n");

        Map<Integer, String> seats = getReservationsForTime(route, time);
        if (seats.isEmpty()) {
            reservationsInfo.append("예약된 좌석이 없습니다.");
        } else {
            for (Map.Entry<Integer, String> entry : seats.entrySet()) {
                reservationsInfo.append("좌석 " + entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }


        textArea.setText(reservationsInfo.toString());
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
        panel.setLayout(new GridLayout(5, 2));

        panel.add(new JLabel("노선 선택:"));
        scheduleComboBox = new JComboBox<>(schedule.keySet().toArray(new String[0]));
        scheduleComboBox.addActionListener(e -> {
            String selectedRoute = (String) scheduleComboBox.getSelectedItem();
            updateScheduleDisplay(selectedRoute);
        });
        panel.add(scheduleComboBox);

        panel.add(new JLabel("시간 선택:"));
        timeComboBox = new JComboBox<>();
        timeComboBox.addActionListener(e -> {
            String selectedTime = (String) timeComboBox.getSelectedItem();
            String selectedRoute = (String) scheduleComboBox.getSelectedItem();
            if (selectedTime != null && selectedRoute != null && !selectedTime.isEmpty() && !selectedRoute.isEmpty()) {
                viewReservationsByTime(selectedRoute, selectedTime);
            }
        });
        panel.add(timeComboBox);

        panel.add(new JLabel("좌석 번호:"));
        seatNumberField = new JTextField();
        panel.add(seatNumberField);

        panel.add(new JLabel("예약자 이름:"));
        passengerNameField = new JTextField();
        panel.add(passengerNameField);

        panel.add(new JLabel("학번:"));
        studentIdField = new JTextField();
        panel.add(studentIdField);

        frame.add(panel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton reserveButton = new JButton("예약");
        reserveButton.addActionListener(e -> {
            try {
                int seatNumber = Integer.parseInt(seatNumberField.getText());
                String passenger = passengerNameField.getText();
                String studentId = studentIdField.getText();
                String time = (String) timeComboBox.getSelectedItem();
                String route = (String) scheduleComboBox.getSelectedItem();
                if (route != null && time != null && !route.isEmpty() && !time.isEmpty()) {
                    reserveSeat(seatNumber, passenger, route, time, studentId);
                } else {
                    JOptionPane.showMessageDialog(null, "노선과 시간을 모두 선택해주세요.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "좌석 번호는 숫자로 입력해야 합니다.");
            }
        });

        JButton cancelButton = new JButton("예약 취소");
        cancelButton.addActionListener(e -> {
            String route = (String) scheduleComboBox.getSelectedItem();
            String time = (String) timeComboBox.getSelectedItem();

            if (route != null && time != null && !route.isEmpty() && !time.isEmpty()) {
                JTextField studentIdField = new JTextField();
                JTextField passengerNameField = new JTextField();
                Object[] message = {
                        "학번:", studentIdField,
                        "이름:", passengerNameField
                };

                int option = JOptionPane.showConfirmDialog(null, message,"예약 취소",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );
                if (option == JOptionPane.OK_OPTION) {
                    String studentId = studentIdField.getText();
                    String passengerName = passengerNameField.getText();

                    if (!studentId.isEmpty() && !passengerName.isEmpty()) {
                        cancelReservation(studentId, passengerName, route, time);
                    } else {
                        JOptionPane.showMessageDialog(null, "학번과 이름을 모두 입력해주세요.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "노선과 시간을 모두 선택해주세요.");
            }
        });


        buttonPanel.add(reserveButton);
        buttonPanel.add(cancelButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
