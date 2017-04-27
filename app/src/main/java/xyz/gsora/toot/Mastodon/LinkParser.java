package xyz.gsora.toot.Mastodon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
public class LinkParser {

    private static final Pattern URLPattern = Pattern.compile("<(.*?)>");
    private static final Pattern RelPattern = Pattern.compile("rel=\"(\\w+)\"");

    public static Link parseNext(String links) {
        String[] commas = splitStringByComma(links);
        return parseLinkRelationship(splitStringBySemicolon(commas[0]));
    }

    public static Link parsePrev(String links) {
        String[] commas = splitStringByComma(links);
        return parseLinkRelationship(splitStringBySemicolon(commas[1]));
    }

    private static String[] splitStringByComma(String links) {
        return links.split(",");
    }

    private static String[] splitStringBySemicolon(String link) {
        return link.split(";");
    }

    private static Link parseLinkRelationship(String[] linkRel) {
        Link l = new Link();
        Matcher URLMatcher = URLPattern.matcher(linkRel[0]);
        if (URLMatcher.find()) {
            l.setURL(URLMatcher.group(1));
            Matcher RelMatcher = RelPattern.matcher(linkRel[1]);
            RelMatcher.find();
            switch (RelMatcher.group(1)) {
                case "next":
                    l.setRel(Relationship.NEXT);
                    break;
                case "prev":
                    l.setRel(Relationship.PREV);
                    break;
            }
        }
        return l;
    }

    public enum Relationship {
        NEXT,
        PREV
    }
}