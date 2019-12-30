package OrganicPi;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;
import com.pi4j.util.ConsoleColor;

public class OrganicPi {
	
	private static int endOffsetSeconds = 10;
	private static int dirListIndex0 = 0;
	private static int dirListIndex1 = 0;
	private static Process process = null;
	
	public static void main( String[] args ) throws InterruptedException {
		final Console console = new Console();
		final GpioController gpio = GpioFactory.getInstance();
		//try {
			console.title( "<-- OrganicPi -->", "MIDI organ player" );

			File dir0 = new File( args[0] );
			File dir1 = new File( args[1] );

			File[] dirList0 = dir0.listFiles();
			File[] dirList1 = dir1.listFiles();

			console.promptForExit();
		
			// Listen for pin A or pin B

			final GpioPinDigitalOutput readyLed = gpio.provisionDigitalOutputPin( RaspiPin.GPIO_03, "Ready indicator", PinState.HIGH );
			final GpioPinDigitalInput coinslotA = gpio.provisionDigitalInputPin( RaspiPin.GPIO_01, PinPullResistance.PULL_UP );
			final GpioPinDigitalInput coinslotB = gpio.provisionDigitalInputPin( RaspiPin.GPIO_04, PinPullResistance.PULL_UP );

			coinslotA.setDebounce( 1000 );
			coinslotB.setDebounce( 1000 );

			coinslotA.setShutdownOptions( true );
			coinslotB.setShutdownOptions( true );
			
			ProcessBuilder processBuilder = new ProcessBuilder();

			coinslotA.addListener( new GpioPinListenerDigital() { 
				@Override
				public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {
					if( event.getState() == PinState.LOW ) {
						Boolean playing = false;
		       				try {
							process.exitValue();
							playing = false;
						} catch ( IllegalThreadStateException itse ) {
							playing = true;
						} catch ( NullPointerException npe ) {
							playing = false;
						}
						if( !playing ) {
					        	console.println( "A was pressed. Now playing file " + dirList0[ dirListIndex0 ] );
							processBuilder.command( "bash", "-c", "aplaymidi --port 20:0 " + dirList0[ dirListIndex0 ] );
							try {
								process = processBuilder.start();
							} catch ( IOException e ) {}
							if( dirListIndex0 < dirList0.length - 1 ) {
								dirListIndex0++;
							} else {
								dirListIndex0 = 0;
							}
						} else {
							console.println( "There is already a track playing! " +
								"Wait for it to finish before inserting coins." );
						}
					}
				}
			} );
			coinslotB.addListener( new GpioPinListenerDigital() {
				@Override
				public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {
					if( event.getState() == PinState.LOW ) {
						Boolean playing = false;
		       				try {
							process.exitValue();
							playing = false;
						} catch ( IllegalThreadStateException itse ) {
							playing = true;
						} catch ( NullPointerException npe ) {
							playing = false;
						}
						if( !playing ) {
					        	console.println( "B was pressed. Now playing file " + dirList1[ dirListIndex1 ] );
							processBuilder.command( "bash", "-c", "aplaymidi --port 20:0 " + dirList1[ dirListIndex1 ] );
							try {
								process = processBuilder.start();
							} catch ( IOException e ) {}
							if( dirListIndex1 < dirList1.length - 1 ) {
								dirListIndex1++;
							} else {
								dirListIndex1 = 0;
							}
						} else {
							console.println( "There is already a track playing! " +
								"Wait for it to finish before inserting coins." );
						}
					}
				}
			} );
		//} catch ( MidiUnavailableException ex ) {
	//		ex.printStackTrace();
		//}
		console.waitForExit();
		readyLed.low();
		gpio.shutdown();
	}
}

