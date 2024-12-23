import java.io.*;
import java.util.*;

public class ShuttleBusManager {
    private static final String FILE_PATH = "Students.txt";

    // 학생 정보를 파일에 저장
    public static void saveStudentsToFile(List<String> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String student : students) {
                writer.write(student);
                writer.newLine();
            }
            System.out.println("학생 정보가 저장되었습니다.");
        } catch (IOException e) {
            System.out.println("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // 파일에서 학생 정보를 읽음
    public static List<String> readStudentsFromFile() {
        List<String> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                students.add(line);
            }
            System.out.println("학생 정보를 불러왔습니다.");
        } catch (IOException e) {
            System.out.println("파일 읽기 중 오류 발생: " + e.getMessage());
        }
        return students;
    }

    // 사용자 입력으로 학생 추가
    public static void addStudentFromInput(List<String> students) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("추가할 학생 정보를 입력하세요. (형식: 학번, 이름, 좌석번호)");
        System.out.println("종료하려면 'exit'를 입력하세요.");

        while (true) {
            System.out.print("학생 정보: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            students.add(input);
        }
    }

    public static void main(String[] args) {
        // 초기화된 학생 리스트
        List<String> studentList = new ArrayList<>();

        // 사용자 입력으로 학생 추가
        addStudentFromInput(studentList);

        // 추가된 학생 정보를 파일에 저장
        saveStudentsToFile(studentList);

        // 저장된 학생 정보를 불러와 출력
        List<String> loadedStudents = readStudentsFromFile();
        System.out.println("현재 저장된 학생 정보:");
        for (String student : loadedStudents) {
            System.out.println(student);
        }
    }
}
