package frc.robot.led.patterns;

import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.led.LEDPattern;

public class BasicLEDPattern implements LEDPattern {
    private final int runLength;
    private final double speed;
    public final Color8Bit[] colors;

    public BasicLEDPattern(int runLength, Color8Bit... colors) {
        this(runLength, 0, colors);
    }

    public BasicLEDPattern(int runLength, double speed, Color8Bit... colors) {
        this.runLength = runLength;
        this.speed = speed;
        this.colors = colors;
    }

    @Override
    public Color8Bit get(int led, double time) {
        int index = (int) Math.floor(led + time * speed);

        int colorIndex = ((index / runLength) % colors.length + colors.length) % colors.length;
        return colors[(colorIndex % colors.length + colors.length) % colors.length];
    }
}
