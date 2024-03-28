package app.getxray.xray;

import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayNameGenerator.Standard;

public class DCCustomDisplayNameGenerator extends Standard {

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return replaceCamelCase(replaceUndercoreBySpace(super.generateDisplayNameForClass(testClass)));
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return replaceCamelCase(replaceUndercoreBySpace(super.generateDisplayNameForNestedClass(nestedClass)));
    }
    
    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return this.replaceCamelCase(replaceUndercoreBySpace(testMethod.getName())) + " (DC)";
    }

    String replaceUndercoreBySpace(String s) {
        return s.replace("_", " ");
    }

    String replaceCamelCase(String camelCase) {
        StringBuilder result = new StringBuilder();
        result.append(camelCase.charAt(0));
        for (int i=1; i<camelCase.length(); i++) {
            if (Character.isUpperCase(camelCase.charAt(i))) {
                result.append(' ');
                result.append(Character.toLowerCase(camelCase.charAt(i)));
            } else {
                result.append(camelCase.charAt(i));
            }
        }
        return result.toString();
    }
}