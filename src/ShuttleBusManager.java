import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * 청주대 셔틀버스 좌석 예약 프로그램입니다.
 *
 * @author fds213 (junwon12352@naver.com)
 * @version 2.5
 * @created 2024-12-17
 * @lastModified 2024-12-25
 * @changelog
 * <ul>
 * <li>2024-12-17: 최초 생성 (fds213)</li>
 * <li>2024-12-23: scanner로 입출력하는 방식에서 gui 버전으로 수정</li>
 * <li>2024-12-24: 시간별 예약 조회기능 추가</li>
 * <li>2024-12-25: 예약 취소 버튼, 남은 좌석표시 개선</li>
 * </ul>
 */

public class ShuttleBusManager {
    private static final String FILE_PATH = "Students.txt"; // 예약 정보를 저장할 파일 경로
    private static final int TOTAL_SEATS = 45;  // 총 좌석 수
    private static final Map<String, Map<String, Map<Integer, String>>> reservations = new HashMap<>(); // 예약 정보를 저장할 맵
    private static final Map<String, List<String>> schedule = new HashMap<>(); // 셔틀버스의 노선과 시간을 저장하는 맵

    private static JTextArea textArea;
    private static JComboBox<String> scheduleComboBox;
    private static JTextField seatNumberField;
    private static JTextField passengerNameField;
    private static JComboBox<String> timeComboBox;
    private static JTextField studentIdField;

    /**
     * 프로그램 시작 시 예약 데이터를 파일에서 읽어오는 메소드입니다.
     * <p>
     * 파일에 저장된 예약 데이터를 읽어와 메모리에 로드합니다. 파일 형식에 맞춰 데이터를 파싱하고,
     * 예약 정보를 예약 맵에 저장합니다.
     * </p>
     */

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

    /**
     * 예약 정보를 파일에 저장합니다.
     * <p>
     * 현재까지 저장된 모든 예약 정보를 파일에 저장합니다. 각 예약은 노선, 시간, 좌석 번호,
     * 예약자 이름과 학번을 포함합니다.
     * </p>
     */

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

    /**
     * 예약 정보를 파일에서 로드합니다.
     * <p>
     * 예약 정보를 파일에서 읽어와 예약 맵에 저장합니다. 파일에 저장된 예약 데이터를 기반으로
     * 좌석 예약을 관리합니다.
     * </p>
     */

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

    /**
     * 좌석을 예약합니다.
     * <p>
     * 주어진 시간, 노선, 좌석 번호에 대해 예약을 수행합니다. 예약이 성공적으로 이루어지면
     * 예약 정보를 파일에 저장하고, 예약 현황을 화면에 표시합니다.
     * </p>
     *
     * @param seatNumber 예약할 좌석 번호
     * @param passenger 예약자의 이름
     * @param route 예약할 노선
     * @param time 예약할 시간
     * @param studentId 예약자의 학번
     */

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

    /**
     * 예약을 취소합니다.
     * <p>
     * 주어진 학번과 이름에 맞는 예약을 찾아 취소합니다. 예약 취소가 완료되면 파일에 저장되고,
     * 화면에 예약 현황이 갱신됩니다.
     * </p>
     *
     * @param studentId 예약자의 학번
     * @param passengerName 예약자의 이름
     * @param route 예약된 노선
     * @param time 예약된 시간
     */

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

    /**
     * 예약 현황을 갱신하여 화면에 표시합니다.
     * @param route 예약할 노선
     * @param time 예약할 시간
     */

    public static void updateReservationsDisplay(String route, String time) {
        StringBuilder sb = new StringBuilder();

        Map<Integer, String> seats = reservations.getOrDefault(route, Collections.emptyMap())
                .getOrDefault(time, Collections.emptyMap());

        int availableSeats = TOTAL_SEATS - seats.size();
        sb.append("남은 좌석 수: ").append(availableSeats).append(" / ").append(TOTAL_SEATS).append("\n\n");

        for (int i = 1; i <= TOTAL_SEATS; i++) {
            if (seats.containsKey(i)) {
                sb.append("좌석 ").append(i).append(": ").append(seats.get(i)).append("\n");
            } else {
                sb.append("좌석 ").append(i).append(": 비어 있음\n");
            }
        }
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }

    /**
     * 스케줄을 업데이트합니다.
     * 선택된 노선에 대한 시간 정보를 갱신합니다.
     * @param route 예약할 노선
     */

    public static void updateScheduleDisplay(String route) {
        timeComboBox.removeAllItems();
        if (route != null && !route.isEmpty()) {
            for (String time : schedule.get(route)) {
                timeComboBox.addItem(time);
            }
        }
    }

    /**
     * 프로그램을 실행하는 main 메소드입니다.
     * GUI를 설정하고 예약 및 취소 기능을 처리합니다.
     * @param args 실행 인자
     */

    public static void main(String[] args) {
        loadReservationsFromFile(); // 파일에서 기존 예약 정보를 로드

        // 프레임 설정: 창 제목 및 크기 설정
        JFrame frame = new JFrame("청주대 셔틀버스 좌석 예약");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        // 예약 정보 표시를 위한 텍스트 영역 설정
        textArea = new JTextArea();
        textArea.setEditable(false);    // 사용자가 수정할 수 없도록 설정
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 예약 관련 입력 폼 설정
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));  // 5행 2열의 그리드 레이아웃

        // 노선 선택 레이블 및 콤보박스
        panel.add(new JLabel("노선 선택:"));
        scheduleComboBox = new JComboBox<>(schedule.keySet().toArray(new String[0]));
        scheduleComboBox.addActionListener(e -> {
            String selectedRoute = (String) scheduleComboBox.getSelectedItem();
            updateScheduleDisplay(selectedRoute);  // 선택한 노선에 따른 시간 목록 업데이트

            String selectedTime = (String) timeComboBox.getSelectedItem();
            if (selectedRoute != null && selectedTime != null) {
                updateReservationsDisplay(selectedRoute, selectedTime); // 예약 현황 업데이트
            }
        });
        panel.add(scheduleComboBox);

        // 시간 선택 레이블 및 콤보박스
        panel.add(new JLabel("시간 선택:"));
        timeComboBox = new JComboBox<>();
        timeComboBox.addActionListener(e -> {
            String selectedTime = (String) timeComboBox.getSelectedItem();
            String selectedRoute = (String) scheduleComboBox.getSelectedItem();

            if (selectedRoute != null && selectedTime != null) {
                updateReservationsDisplay(selectedRoute, selectedTime); // 시간에 맞는 예약 현황 업데이트
            }
        });
        panel.add(timeComboBox);

        // 좌석 번호 입력 필드
        panel.add(new JLabel("좌석 번호:"));
        seatNumberField = new JTextField();
        panel.add(seatNumberField);

        // 이름 입력 필드
        panel.add(new JLabel("이름:"));
        passengerNameField = new JTextField();
        panel.add(passengerNameField);

        // 학번 입력 필드
        panel.add(new JLabel("학번:"));
        studentIdField = new JTextField();
        panel.add(studentIdField);

        // 입력 폼을 프레임에 추가
        frame.add(panel, BorderLayout.NORTH);

        // 버튼을 포함할 패널 설정
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // 예약 버튼
        JButton reserveButton = new JButton("예약");
        reserveButton.addActionListener(e -> {
            try {
                // 사용자 입력 값 받기
                int seatNumber = Integer.parseInt(seatNumberField.getText());   // 좌석 번호
                String passenger = passengerNameField.getText();    // 예약자 이름
                String studentId = studentIdField.getText();     // 예약자 학번
                String time = (String) timeComboBox.getSelectedItem();  // 선택한 시간
                String route = (String) scheduleComboBox.getSelectedItem();  // 선택한 노선

                // 이름과 학번이 입력되지 않았으면 경고 메시지 출력
                if (passenger.isEmpty() || studentId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "이름과 학번을 모두 입력해주세요.");
                    return;
                }

                // 노선과 시간 선택이 유효하면 좌석 예약
                if (route != null && time != null && !route.isEmpty() && !time.isEmpty()) {
                    reserveSeat(seatNumber, passenger, route, time, studentId); // 좌석 예약
                } else {
                    JOptionPane.showMessageDialog(null, "노선과 시간을 모두 선택해주세요.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "좌석 번호는 숫자로 입력해야 합니다.");
            }
        });

        // 예약 취소 버튼
        JButton cancelButton = new JButton("예약 취소");
        cancelButton.addActionListener(e -> {
            String route = (String) scheduleComboBox.getSelectedItem(); // 선택한 노선
            String time = (String) timeComboBox.getSelectedItem();  // 선택한 시간

            // 노선과 시간이 선택된 경우에만 예약 취소 가능
            if (route != null && time != null && !route.isEmpty() && !time.isEmpty()) {
                JTextField studentIdField = new JTextField();
                JTextField passengerNameField = new JTextField();
                Object[] message = {
                        "학번:", studentIdField,  // 학번 입력 필드
                        "이름:", passengerNameField   // 이름 입력 필드
                };

                // 예약 취소 다이얼로그 표시
                int option = JOptionPane.showConfirmDialog(null, message, "예약 취소",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );
                if (option == JOptionPane.OK_OPTION) {
                    String studentId = studentIdField.getText();     // 입력된 학번
                    String passengerName = passengerNameField.getText();    // 입력된 이름

                    // 학번과 이름이 모두 입력되었으면 예약 취소
                    if (!studentId.isEmpty() && !passengerName.isEmpty()) {
                        cancelReservation(studentId, passengerName, route, time);   // 예약 취소
                    } else {
                        JOptionPane.showMessageDialog(null, "학번과 이름을 모두 입력해주세요.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "노선과 시간을 모두 선택해주세요.");
            }
        });

        // 예약 및 취소 버튼을 버튼 패널에 추가
        buttonPanel.add(reserveButton);
        buttonPanel.add(cancelButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);  // 프레임 표시
    }
}