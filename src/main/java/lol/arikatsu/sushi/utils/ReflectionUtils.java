package lol.arikatsu.sushi.utils;

import lol.arikatsu.sushi.Sushi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionUtils {
    private ReflectionUtils() {
        // This class is not meant to be instantiated.
    }

    /**
     * Returns all classes of the given type that are annotated with the given annotation.
     *
     * @param annotation The annotation to search for.
     * @return A collection of classes.
     */
    public static List<Class<?>> getAllOf(Class<? extends Annotation> annotation) {
        return new ArrayList<>(Sushi.getReflector().getTypesAnnotatedWith(annotation));
    }
}
