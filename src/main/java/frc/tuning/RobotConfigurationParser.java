package frc.tuning;

import frc.csm.PackagePrivate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@PackagePrivate
class RobotConfigurationParser {
    private final Stack<String> currentNamespace = new Stack<>();
    private final Map<String, ValueSource> target;
    private static final String numberMatcherRegex = "-?(0|[1-9]\\d*)(\\.\\d+)?";

    public RobotConfigurationParser(Map<String, ValueSource> target) {
        this.target = target;
    }

    public void parse(String string) {
        TokenStream tokens = tokenize(string);
        currentNamespace.clear();

        while (!tokens.isExhausted()) {
            String name = tokens.getNextMatching("[\\w-]+|\\}");
            if (name == null) break;

            if (name.equals("}")) {
                currentNamespace.pop();
                continue;
            }

            String op = tokens.getNextMatching("=|\\?=|\\{");
            if (op == null) break;

            currentNamespace.push(name);

            if (op.equals("{")) continue;

            parseValue(tokens, switch (op) {
                case "=" -> false;
                case "?=" -> true;
                default -> throw new Error("Unknown operator");
            });

            currentNamespace.pop();
        }
    }

    private void parseValue(TokenStream tokens, boolean makeTunable) {
        if (tokens.hasNextMatching(numberMatcherRegex)) {
            parseNumber(tokens, makeTunable);
        } else if (tokens.hasNextMatching("\\[")) {
            parseTable(tokens, makeTunable);
        } else tokens.skip();
    }

    private void parseNumber(TokenStream tokens, boolean makeTunable) {
        String number = tokens.getNextMatching(numberMatcherRegex);
        if (number == null) return;

        double value = Double.parseDouble(number);
        String fullName = String.join(".", currentNamespace);
        target.put(
            fullName,
            makeTunable
                ? new TunableNumberSource(fullName, value)
                : new ConstantNumberSource(fullName, value)
        );
    }

    private void parseTable(TokenStream tokens, boolean makeTunable) {
        String fullName = String.join(".", currentNamespace);
        // Table syntax:
        // [
        //     col1 : col2 : col3 ;
        //     val : val : val ;
        //     val : val : val ;
        //     ...
        //     val : val : val ;
        // ]

        if (!tokens.consumeNextMatchingIfPresent("\\[")) {
            System.out.println("Error parsing table " + fullName + ": Expected [");
            return;
        }

        // Parse column names
        List<String> colNames = new ArrayList<>();
        do {
            String colName = tokens.getNextMatching("[\\w-]+");
            if (colName == null) return;
            colNames.add(colName);
        } while (tokens.consumeNextMatchingIfPresent(":"));
        if (!tokens.consumeNextMatchingIfPresent(";")) {
            System.out.println("Error parsing table " + fullName + ": Expected ;");
            return;
        }
        int numCols = colNames.size();

        // Parse data
        List<double[]> data = new ArrayList<>();
        do {
            double[] row = new double[numCols];

            for (int i = 0; i < numCols; i++) {
                String number = tokens.getNextMatching(numberMatcherRegex);
                if (number == null) {
                    System.out.println("Error parsing table " + fullName + ": Not enough values");
                    return;
                }
                double value = Double.parseDouble(number);
                row[i] = value;
                if (!tokens.consumeNextMatchingIfPresent(":|;")) {
                    System.out.println("Error parsing table " + fullName + ": Expected : or ;");
                    return;
                }
            }

            data.add(row);
        } while (!tokens.hasNextMatching("\\]"));

        if (!tokens.consumeNextMatchingIfPresent("\\]")) {
            System.out.println("Error parsing table " + fullName + ": Expected ]");
            return;
        }

        // Finally make everything into an array
        String[] colNamesArr = colNames.toArray(String[]::new);
        double[][] dataArr = data.toArray(double[][]::new);
        int numRows = dataArr.length;

        target.put(
            fullName,
            makeTunable
                ? new TunableTableSource(fullName, numRows, numCols, colNamesArr, dataArr)
                : new ConstantTableSource(fullName, numRows, numCols, colNamesArr, dataArr)
        );
    }

    private TokenStream tokenize(String string) {
        TokenStream tokens = new TokenStream();
        for (String line : string.split("\\s*\n\\s*")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            tokens.insert(line.split("\\s+"));
        }

        return tokens;
    }
}
