package yashasvig.crawler;


import yashasvig.crawler.api.Crawler;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {

    private static final int MAX_INPUT_ERROR_ALLOWED = 5;
    private static int errorCount = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Hey there! Please enter a base url to start\n");
        while (true) {
            String baseUrl = sc.nextLine();
            try {
                new Crawler().crawl(baseUrl);
                break;
            } catch (URISyntaxException | MalformedURLException e) {
                errorCount++;
                if (errorCount == MAX_INPUT_ERROR_ALLOWED) {
                    System.out.print(
                            "Seems like you need a better understanding of what's a valid URL. See you next time.\n");
                    break;
                }
                System.out.print("Uh ah! Please enter a valid url\n");
            } catch (Exception e) {
                errorCount++;
                if (errorCount == MAX_INPUT_ERROR_ALLOWED) {
                    System.out.print(
                            "Seems like you need a better understanding of what's a valid URL. See you next time.\n");
                    break;
                }
                System.out.printf("There was an issue : %s. Please try again.\n", e.getMessage());
            }

        }
    }
}