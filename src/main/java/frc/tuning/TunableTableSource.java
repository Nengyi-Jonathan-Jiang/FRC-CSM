package frc.tuning;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.csm.PackagePrivate;

import java.util.ArrayList;
import java.util.List;

@PackagePrivate
class TunableTableSource extends ConstantTableSource {
    private int currRow = 0;
    private final List<TableSourceListener> listeners = new ArrayList<>();

    public TunableTableSource(String name, int numRows, int numCols, String[] columnNames, double[][] data) {
        super(name, numRows, numCols, columnNames, data);
        // Add full display
        SmartDashboard.putData(name, builder -> {
            builder.setSmartDashboardType("Network Table Tree");
            for (int row = 0; row < numRows(); row++) {
                int finalRow = row;
                builder.addStringProperty("Row " + (row + 1), () -> {
                    StringBuilder s = new StringBuilder();
                    for (int col = 0; col < numColumns(); col++) {
                        s.append(getColumnName(col));
                        s.append("=");
                        s.append(getCellAsDouble(finalRow, col));
                        s.append(", ");
                    }
                    return s.toString();
                }, null);
            }
        });

        // Add row select
        SmartDashboard.putData("Row select for " + name, new SendableChooser<Integer>() {
            {
                setDefaultOption("Row 1", 1);
                for (int row = 1; row < numRows(); row++) {
                    addOption("Row " + (row + 1), row);
                }
                onChange(x -> updateCurrentRow(x));
            }
        });

        // Add column writers
        SmartDashboard.putData("Editor for " + name, builder -> {
            for(int column = 0; column < numColumns(); column++) {
                int finalColumn = column;
                builder.addDoubleProperty(
                    getColumnName(column),
                    () -> getCellAsDouble(currRow, finalColumn),
                    x -> {
                        data[currRow][finalColumn] = x;
                        for (TableSourceListener listener : listeners) {
                            listener.onTableChange(currRow, finalColumn, x);
                        }
                    }
                );
            }
        });
    }

    private void updateCurrentRow(int x) {
        currRow = x;
    }

    @Override
    public void addListener(TableSourceListener listener) {
        listeners.add(listener);
    }
}
