package frc.robot.led;

import edu.wpi.first.wpilibj.util.Color8Bit;

public interface LEDPattern {
    Color8Bit get(int led, double time);

    LEDPattern BLANK = (led, time) -> new Color8Bit(0, 0, 0);
}
