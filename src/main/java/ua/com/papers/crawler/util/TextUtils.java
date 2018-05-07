package ua.com.papers.crawler.util;

import lombok.NonNull;

/**
 * Created by Максим on 2/6/2017.
 */
public final class TextUtils {

    private TextUtils() {
        throw new RuntimeException("shouldn't be invoked");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNonEmpty(String str) {
        return !isEmpty(str);
    }

    public static String formatName(@NonNull String firstName, @NonNull String middleName, @NonNull String lastName) {
        return String.format("%s %s", capitalize(lastName), formatInitials(firstName, middleName));
    }

    public static String formatName(@NonNull String firstName, @NonNull String lastName) {
        return String.format("%s %s.", capitalize(lastName), formatInitials(firstName));
    }

    public static String formatInitials(@NonNull String firstName, @NonNull String middleName) {
        return String.format("%s %s", formatInitials(firstName), formatInitials(middleName));
    }

    public static String formatInitials(@NonNull String str) {
        return String.format("%s.", Character.toUpperCase(str.charAt(0)));
    }

    public static String capitalize(@NonNull String str) {
        return str.isEmpty() ? "" : (Character.toUpperCase(str.charAt(0)) + (str.length() > 1 ? str.substring(1) : ""));
    }

}
