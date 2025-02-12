/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.ironriders.lights;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Flash yellow/gold and green
 */
public class LightsSubsystem extends SubsystemBase {

    enum Mode {

        BLINKING,
        SOLID_YELLOW,
        SOLID_GREEN,
        SCROLLING_GRADIENT

    }

    Mode mode = Mode.BLINKING;

    // have the LED strip here or something
    AddressableLED addressableLedStrip = new AddressableLED(LightsConstants.LED_STRIP_PORT);
    AddressableLEDBuffer addressableLEDBuffer = new AddressableLEDBuffer(LightsConstants.LED_STRIP_LENGTH);

    // initializing the colors for the strip
    LEDPattern yellow = LEDPattern.solid(Color.kYellow);
    LEDPattern green = LEDPattern.solid(Color.kRed);

    // periodic tracker
    int period = 0;

    // light state because i dont know how to check it
    int lightState = 0;
    
    // gradient pattern initialization
    private final LEDPattern gradient = LEDPattern.gradient(LEDPattern.GradientType.kContinuous, Color.kGreen, Color.kYellow);
    private static final Distance kLedSpacing = Meters.of(1.0 / LightsConstants.LED_STRIP_LENGTH);
    private final LEDPattern scrollingGradient = gradient.scrollAtAbsoluteSpeed(MetersPerSecond.of(1), kLedSpacing);


    public LightsSubsystem() {
        // initialize system

        addressableLedStrip.setLength(LightsConstants.LED_STRIP_LENGTH);
        addressableLedStrip.setData(addressableLEDBuffer);
        addressableLedStrip.start();
        yellow.applyTo(addressableLEDBuffer);
        addressableLedStrip.setData(addressableLEDBuffer);
        
    }

    @Override
    public void periodic() {
        period++;

        switch(mode) {

            case BLINKING:

                if(period % 5 == 0) {

                lightState++;

                if(lightState % 2 == 0){

                    yellow.applyTo(addressableLEDBuffer);

                    } else {

                        green.applyTo(addressableLEDBuffer);

                }
            
        }

            break;

            case SOLID_YELLOW:

            break;

            case SOLID_GREEN:

            break;

            case SCROLLING_GRADIENT:

                scrollingGradient.applyTo(addressableLEDBuffer);

            break;

        }
 

        addressableLedStrip.setData(addressableLEDBuffer);

    }

}
