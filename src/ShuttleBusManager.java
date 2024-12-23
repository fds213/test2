import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * 청주대 셔틀버스 좌석 예약 프로그램의 GUI 버전입니다.
 *
 * @author fds213 (junwon12352@naver.com)
 * @version 2.0
 *
 * @created 2024-12-17
 * @lastModified 2024-12-23
 *
 * @changelog
 * <ul>
 *     <li>2024-12-17: 최초 생성 (fds213)</li>
 *     <li>2024-12-23: scanner로 입출력하는 방식에서 gui 버전으로 수정</li>
 * </ul>
 */
public class ShuttleBusManager {
    private static final String FILE_PATH = "Students.txt";
    private static final int TOTAL_SEATS = 10;
    private static final Map<Integer, String> reservations = new HashMap<>();

    private static JTextArea textArea;
    private static JTextField seatNumberField;
    private static JTextField passengerNameField;

    public static void saveReservationsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<Integer, String> entry : reservations.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
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
                reservations.put(seatNumber, passenger);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    public static void reserveSeat(int seatNumber, String passenger) {
        if (reservations.containsKey(seatNumber)) {
            JOptionPane.showMessageDialog(null, "이미 예약된 좌석입니다.");
        } else if (seatNumber < 1 || seatNumber > TOTAL_SEATS) {
            JOptionPane.showMessageDialog(null, "잘못된 좌석 번호입니다.");
        } else {
            reservations.put(seatNumber, passenger);
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
                sb.append("좌석 ").append(i).append(": ").append(reservations.get(i)).append("\n");
            } else {
                sb.append("좌석 ").append(i).append(": 비어 있음\n");
            }
        }
        textArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        loadReservationsFromFile();

        JFrame frame = new JFrame("청주대 셔틀버스 좌석 예약");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

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
                reserveSeat(seatNumber, passenger);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "좌석 번호는 숫자로 입력해야 합니다.");
            }
        });

        JButton viewButton = new JButton("예약 조회");
        viewButton.addActionListener(e -> updateReservationsDisplay());

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
        buttonPanel.add(viewButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}