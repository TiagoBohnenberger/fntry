package fntry.util;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;

public class DisplayNameGenerators {

    public static class ReplaceCamelCase extends DisplayNameGenerator.ReplaceUnderscores {
        @Override
        public String generateDisplayNameForClass(Class<?> testClass) {
            return replaceCamelCase(super.generateDisplayNameForClass(testClass));
        }

        @Override
        public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
            return replaceCamelCase(super.generateDisplayNameForNestedClass(nestedClass));
        }

        @Override
        public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
            return this.removeParentheses(this.replaceCamelCase(super.generateDisplayNameForMethod(testClass, testMethod)) +
                    DisplayNameGenerator.parameterTypesAsString(testMethod));
        }

        private String removeParentheses(String methodName) {
            return methodName.replaceAll("[\\(\\)]", "");
        }

        String replaceCamelCase(String camelCase) {
            StringBuilder result = new StringBuilder();
            result.append(camelCase.charAt(0));
            for (int i = 1; i < camelCase.length(); i++) {
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

}