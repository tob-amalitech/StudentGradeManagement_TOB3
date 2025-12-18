package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdvancedSearch {
    private final DataStore store;

    public AdvancedSearch(DataStore store) { this.store = store; }

    public List<Student> searchByRegexOnField(String regex, String field, boolean caseInsensitive) {
        Pattern p = caseInsensitive ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
        List<Student> out = new ArrayList<>();
        for (Student s : store.getAllStudents()) {
            String target = switch (field.toLowerCase()) {
                case "email" -> s.getEmail() == null ? "" : s.getEmail();
                case "phone" -> s.getPhone() == null ? "" : s.getPhone();
                case "name" -> s.getName();
                case "id" -> s.getId();
                default -> "";
            };
            if (p.matcher(target).find()) out.add(s);
        }
        return out;
    }

    public List<String> highlightMatches(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        var m = p.matcher(text);
        List<String> parts = new ArrayList<>();
        int last = 0;
        while (m.find()) {
            if (m.start() > last) parts.add(text.substring(last, m.start()));
            parts.add("[" + text.substring(m.start(), m.end()) + "]");
            last = m.end();
        }
        if (last < text.length()) parts.add(text.substring(last));
        return parts;
    }

    public List<Student> findByEmailDomain(String domain) {
        String regex = ".*@" + Pattern.quote(domain) + "$";
        return searchByRegexOnField(regex, "email", true);
    }

    public List<Student> findByIdPattern(String wildcardPattern) {
        // convert wildcard '*' to '.*'
        String regex = wildcardPattern.replace("*", ".*");
        return searchByRegexOnField(regex, "id", false);
    }
}
