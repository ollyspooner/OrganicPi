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

public class OrganicPi {
	public static void main( String[] args ) {
		// Load a MIDI file and play it with something...
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			if( sequencer == null ) {
				System.err.println( "Sequencer device not supported" );
				return;
			}
			sequencer.open();
			Sequence sequence = MidiSystem.getSequence( new File( "C:\\Users\\ollyspooner.SHELTERBOX\\Downloads\\Midi\\BottomG\\ring of fire.mid" ) );
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
