package ru.romeo558.myprojectrebuild;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EduTatarLogin {
    private static final String TAG = "EduTatarLogin";
    private static final String BASE_URL = "https://edu.tatar.ru";
    private static final String LOGIN_URL = BASE_URL + "/login";
    private static final String DIARY_URL = BASE_URL + "/user/diary/week";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 11; SM-G960F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Mobile Safari/537.36";
    private static final String REFERER = BASE_URL + "/login/";

    private Map<String, String> headers;
    private String login;
    private String password;
    private Document diaryPage;

    public EduTatarLogin(String login, String password) {
        this.login = login;
        this.password = password;
        this.headers = new HashMap<>();
        this.headers.put("User-Agent", USER_AGENT);
        this.headers.put("Referer", REFERER);
    }

    public boolean authenticate() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("main_login2", login);
        loginData.put("main_password2", password);

        try {
            Document loginPage = Jsoup.connect(LOGIN_URL)
                    .headers(headers)
                    .data(loginData)
                    .method(org.jsoup.Connection.Method.POST)
                    .execute()
                    .parse();

            // Check if login was successful
            if (loginPage.select("form[action=\"/login/\"]").size() > 0) {
                Log.w(TAG, "Credentials are invalid. Try with another data.");
                return false;
            }

            // Get diary page after successful login
            diaryPage = Jsoup.connect(DIARY_URL)
                    .headers(headers)
                    .get();

            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to authenticate", e);
            return false;
        }
    }

    public Document getDiaryPage() {
        return diaryPage;
    }
}
