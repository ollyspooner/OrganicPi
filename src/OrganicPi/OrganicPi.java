package OrganicPi;

import java.io.File;
import java.io.IOException;
import javax.sound.midi.*;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;
import com.pi4j.util.ConsoleColor;

public class OrganicPi {
	
	private static int endOffsetSeconds = 10;
	private static boolean playing = false;
	
	public static void main( String[] args ) throws InterruptedException {
		final Console console = new Console();
		console.title( "<-- OrganicPi -->", "MIDI organ player" );
		console.promptForExit();
		
		// Listen for pin A or pin B
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalInput coinslotA = gpio.provisionDigitalInputPin( RaspiPin.GPIO_01, PinPullResistance.PULL_UP );
		coinslotA.setDebounce( 1000 );
		coinslotA.setShutdownOptions( true );
		coinslotA.addListener( new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {
                console.println(" --> GPIO PIN STATE CHANGE (EVENT): " + event.getPin() + " = " +
                        ConsoleColor.conditional(
                                event.getState().isHigh(), // conditional expression
                                ConsoleColor.GREEN,        // positive conditional colour
                                ConsoleColor.RED,          // negative conditional colour
                                event.getState()));        // text to display
				if( event.getState() == PinState.LOW ) {
					console.println( "A was pressed. Now playing " + args[0] );
					playMidiFile( args[0] );
				}
			}
		} );
		final GpioPinDigitalInput coinslotB = gpio.provisionDigitalInputPin( RaspiPin.GPIO_04, PinPullResistance.PULL_UP );
		coinslotB.setDebounce( 1000 );
		coinslotB.setShutdownOptions( true );
		coinslotB.addListener( new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {
                console.println(" --> GPIO PIN STATE CHANGE (EVENT): " + event.getPin() + " = " +
                        ConsoleColor.conditional(
                                event.getState().isHigh(), // conditional expression
                                ConsoleColor.GREEN,        // positive conditional colour
                                ConsoleColor.RED,          // negative conditional colour
                                event.getState()));        // text to display
				if( event.getState() == PinState.LOW ) {
					console.println( "B was pressed. Now playing " + args[1] );
					playMidiFile( args[1] );
				}
			}
		} );
		console.waitForExit();
        gpio.shutdown();
	}
	
	public static void playMidiFile ( String fileToPlay ) {
		if( !playing ) {
			System.out.println( "Playing track from " + fileToPlay + "...");
			// Load a MIDI file and play it with something...
			try {
				playing = true;
				Sequencer sequencer = MidiSystem.getSequencer();
				if( sequencer == null ) {
					System.err.println( "Sequencer device not supported" );
					return;
				}
				sequencer.open();
				Sequence sequence = MidiSystem.getSequence( new File( fileToPlay ) );
				Track track = sequence.createTrack();
	
				// Insert an unplayable note at the start of the sequence
				track.add( makeEvent( ShortMessage.NOTE_ON, 0, 100, 64, 30 ) );
				track.add( makeEvent( ShortMessage.NOTE_OFF, 0, 100, 64, 30 + 70 ) );
				
				// Find out how long the sequence is in seconds
				long timeLength = sequence.getMicrosecondLength(); // 5900000 ms = 5900
				long tickLength = sequence.getTickLength();
				long msPerTick = timeLength / tickLength;
				long startTheEnd = ( timeLength - ( endOffsetSeconds * 1000000 ) ) / msPerTick;
	
				// Insert an unplayable note {endOffsetSeconds} seconds before the end of the sequence
				track.add( makeEvent( ShortMessage.NOTE_ON, 0, 105, 64, (int)startTheEnd ) );
				track.add( makeEvent( ShortMessage.NOTE_OFF, 0, 105, 64, (int)startTheEnd + 70 ) );
				
				sequencer.setSequence( sequence );
				sequencer.start();
				while( sequencer.isRunning() ) {}
				playing = false;
				System.out.println( "The track finished playing!" );
			} catch ( MidiUnavailableException | InvalidMidiDataException | IOException ex ) {
				ex.printStackTrace();
				
			}
		} else {
			System.out.println( "There is already a track playing! Wait for it to finish before inserting coins." );
		}
	}
	
	public static MidiEvent makeEvent( int command, int channel, int note, int velocity, int tick ) {
		MidiEvent event = null;
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage( command, channel, note, velocity );
			event = new MidiEvent( message, tick );
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		return event;
	}
}
