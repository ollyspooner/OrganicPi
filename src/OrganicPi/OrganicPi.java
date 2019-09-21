package OrganicPi;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class OrganicPi {
	public static void main( String[] args ) {
		
		// Listen for pin A or pin B
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalInput coinslotA = gpio.provisionDigitalInputPin( RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN );
		coinslotA.setShutdownOptions( true );
		coinslotA.addListener( new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {
				if( event.getState() == PinState.HIGH ) {;
					addperfomanceAtoplaylist();
				}
			}
		} );
		
		
		
		playMidiFile( args[0] );
	}
	
	public static void addperfomanceAtoplaylist() {
		System.out.println( "A was pressed." );
	}

	public void addperfomanceBtoplaylist() {
		System.out.println( "B was pressed." );
	}
	
	public static void playMidiFile ( String fileToPlay ) {
		// Load a MIDI file and play it with something...
		try {
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
			long startTheEnd = ( timeLength - 5000000 ) / msPerTick;

			// Insert an unplayable note 5 seconds before the end of the sequence
			track.add( makeEvent( ShortMessage.NOTE_ON, 0, 105, 64, (int)startTheEnd  ) );
			track.add( makeEvent( ShortMessage.NOTE_OFF, 0, 105, 64, (int)startTheEnd + 70 ) );
			
			sequencer.setSequence( sequence );
			sequencer.start();
		} catch ( MidiUnavailableException | InvalidMidiDataException | IOException ex ) {
			ex.printStackTrace();
			
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
