package frc.robot.led.patterns;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

public class RainbowLEDPattern extends BasicLEDPattern {
    public RainbowLEDPattern(int cycleSize, double speed) {
        super(1, speed, getRainbowPattern(cycleSize));
    }

    private static Color8Bit[] getRainbowPattern(int cycleSize) {
        Color8Bit[] colors = new Color8Bit[cycleSize];
        for (int i = 0; i < cycleSize; i++) {
            Color hsv = Color.fromHSV(180 * i / cycleSize, 255, 255);
            colors[i] = new Color8Bit(hsv);
        }
        return colors;
    }
}
