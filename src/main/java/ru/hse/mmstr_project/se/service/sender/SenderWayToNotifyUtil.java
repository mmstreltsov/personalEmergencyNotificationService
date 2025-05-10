package ru.hse.mmstr_project.se.service.sender;

import java.util.List;

public class SenderWayToNotifyUtil {


    private static final String TG = "tg";
    private static final String EMAIL = "email";
    private static final String SMS = "sms";

    public static List<String> correctWays = List.of(TG, EMAIL, SMS);

    public static boolean isCorrect(String way) {
        List<String> list = List.of(way);
        return isTgSender(list) || isEmailSender(list) || isSmsSender(list);
    }

    public static boolean isTgSender(List<String> wayToNotify) {
        return isSenderImpl(wayToNotify, TG);
    }

    public static boolean isTgSender(String wayToNotify) {
        return isTgSender(List.of(wayToNotify));
    }

    public static boolean isEmailSender(List<String> wayToNotify) {
        return isSenderImpl(wayToNotify, EMAIL);
    }

    public static boolean isEmailSender(String wayToNotify) {
        return isEmailSender(List.of(wayToNotify));
    }

    public static boolean isSmsSender(List<String> wayToNotify) {
        return isSenderImpl(wayToNotify, SMS);
    }

    public static boolean isSmsSender(String wayToNotify) {
        return isSmsSender(List.of(wayToNotify));
    }

    private static boolean isSenderImpl(List<String> wayToNotify, String text) {
        return wayToNotify.stream().anyMatch(way -> way.contains(text));
    }
}
