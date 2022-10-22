package ar.edu.itba.pod.client;

import java.util.stream.Stream;

public final class Utils {

    private Utils() {

    }

    public static String parseParameter(String[] args, String requestedParam){
        return Stream.of(args).filter(arg -> arg.contains(requestedParam))
                .map(arg -> arg.substring(arg.indexOf("=") + 1))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Use: " + requestedParam + "=<value>")
                );
    }
}
