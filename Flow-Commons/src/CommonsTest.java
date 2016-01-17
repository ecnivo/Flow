import java.util.Scanner;

/**
 * Created by Netdex on 1/17/2016.
 */
public class CommonsTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();
        while (true) {
            int idx = sc.nextInt();
            text = text.substring(0, idx) + text.substring(idx + 1);
            System.out.println(text);
        }
    }
}
