package spbstu.ktlo.task2.cli;

import java.lang.reflect.Field;
import java.util.*;

public class CLIParser {

    private Map<Character, Field> options;
    private Map<String, Field> parameters;
    private Field arguments;
    private Object tile;

    public CLIParser(Object object) {
        options = new HashMap<>();
        parameters = new HashMap<>();
        tile = object;

        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            Option option = field.getAnnotation(Option.class);
            if (option != null)
                options.put(option.name(), field);
            Parameter parameter = field.getAnnotation(Parameter.class);
            if (parameter != null)
                parameters.put(parameter.name(), field);
            Arguments args = field.getAnnotation(Arguments.class);
            if (args != null)
                arguments = field;
        }
    }

    public void parse(String[] args) throws CLIException, IllegalAccessException {
        List<String> tale = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                // Set values for all options with values
                if (arg.contains("=")) {
                    int n = arg.indexOf('=');
                    String parameterName = arg.substring(2, n);
                    if (parameters.containsKey(parameterName)) {
                        Field parameter = parameters.get(parameterName);
                        if (parameter.getType().equals(boolean.class))
                            throw new CLIException("the parameter '--" + parameterName + "' has no value");
                        parameter.set(tile, arg.substring(n + 1));
                    }
                    else
                        throw new CLIException("invalid parameter -- '--" + parameterName + '\'');
                }
                else {
                    String parameterName = arg.substring(2);
                    if (i + 1 == args.length)
                        throw new CLIException("insufficient argument list: no value for parameter '" + arg + '\'');
                    if (parameters.containsKey(parameterName)) {
                        Field parameter = parameters.get(parameterName);
                        if (parameter.getType().equals(boolean.class)) {
                            parameter.setBoolean(tile, true);
                        }
                        else {
                            i++;
                            parameter.set(tile, args[i]);
                        }
                    }
                    else
                        throw new CLIException("invalid parameter -- '" + arg + '\'');
                }
            }
            else if (arg.startsWith("-")) {
                // Set true for marked flags
                for (int j = 1, length = arg.length(); j < length; j++) {
                    char optionName = arg.charAt(j);
                    if (options.containsKey(optionName))
                        options.get(optionName).setBoolean(tile, true);
                    else
                        throw new CLIException("invalid option -- '-" + optionName + '\'');
                }
            }
            else {
                // Set other arguments
                tale.add(arg);
            }
        }
        if (arguments != null)
            arguments.set(tile, tale);
    }

    public String generateUsage() {
        StringBuilder builder = new StringBuilder();
        if (!options.isEmpty()) {
            builder.append("Flags:\n");
            for (Map.Entry<Character, Field> pair : options.entrySet()) {
                Option option = pair.getValue().getAnnotation(Option.class);
                builder.append("\t-")
                       .append(option.name())
                       .append('\t')
                       .append(option.usage())
                       .append('\n');
            }
        }
        if (!parameters.isEmpty()) {
            builder.append("Parameters:\n");
            for (Map.Entry<String, Field> pair : parameters.entrySet()) {
                Parameter parameter = pair.getValue().getAnnotation(Parameter.class);
                builder.append("\t--")
                       .append(parameter.name())
                       .append('\t')
                       .append(parameter.usage())
                       .append('\n');
            }
        }
        return builder.toString();
    }

}
