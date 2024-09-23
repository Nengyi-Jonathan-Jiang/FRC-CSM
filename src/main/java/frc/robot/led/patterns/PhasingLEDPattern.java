package frc.robot.led.patterns;

import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.led.LEDPattern;

public class PhasingLEDPattern implements LEDPattern {
    private final Color8Bit color;
    private final double phasingSpeed;

    public PhasingLEDPattern(Color8Bit color, double phasingSpeed) {
        this.color = color;
        this.phasingSpeed = phasingSpeed;
    }

    @Override
    public Color8Bit get(int led, double time) {

        double factor = Math.sin(time * phasingSpeed * Math.PI * 2) * 0.5 + 0.5;

        double red = color.red;
        double green = color.green;
        double blue = color.blue;

        return new Color8Bit((int) (red * factor), (int) (green * factor), (int) (blue * factor));
    }
}
