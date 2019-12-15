package OrganicPi;

import java.io.File;
import java.io.IOException;
<<<<<<< HEAD
import java.util.*;
=======
>>>>>>> d64205a213d07a4efe9e5b64092939c59fa10ee0
import javax.sound.midi.*;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.util.Console;
import com.pi4j.util.ConsoleColor;

public class OrganicPi {
	
<<<<<<< HEAD
	private static Sequencer sequencer = null;
	private static Transmitter sequencerTx = null;
	private static MidiDevice device = null;
	private static Receiver deviceRx = null;
	private static int endOffsetSeconds = 10;
	private static int dirListIndex0 = 0;
	private static int dirListIndex1 = 0;
	
	public static void main( String[] args ) throws InterruptedException {
		final Console console = new Console();
		final GpioController gpio = GpioFactory.getInstance();
		try {
			console.title( "<-- OrganicPi -->", "MIDI organ player" );
			MidiDevice.Info[] deviceInfo = MidiSystem.getMidiDeviceInfo();
			for( int i = 0; i < deviceInfo.length; i++ ){
				try {
					device = MidiSystem.getMidiDevice(deviceInfo[i]);
				} catch ( MidiUnavailableException e) {}
				if( device instanceof Synthesizer ) {
					console.println( "*** SYNTHESIZER ***" );
				} else if( device instanceof Sequencer ) {
					console.println( "*** SEQUENCER ***" );
				} else {
					console.println( "*** MIDI PORT ***" );
=======
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
>>>>>>> d64205a213d07a4efe9e5b64092939c59fa10ee0
				}
				int maxTx = device.getMaxTransmitters();
				if( maxTx == -1 ) {
					console.print( "[Tx] xU " );
				} else if( maxTx > 0 ) {
					console.print( "[Tx] x" + maxTx + " " );
				}
				int maxRx = device.getMaxReceivers();
				if( maxRx == -1 ) {
					console.print( "[Rx] xU " );
				} else if( maxRx > 0 ) {
					console.print( "[Rx] x" + maxRx + " " );
				}
				console.println();
				console.println( "Device " + i + ": " + deviceInfo[i].getName() );
				console.println( "     " + deviceInfo[i].getDescription() );
				console.println( "     " + deviceInfo[i].getVendor() );
				console.println( "     " + deviceInfo[i].getVersion() );
				console.println();
			}
<<<<<<< HEAD
			int selectdDevice = 2;
			console.println( "Selected device: " + deviceInfo[selectdDevice].getName() );
	
			device = MidiSystem.getMidiDevice( deviceInfo[0] );
			device.open();

			sequencer = MidiSystem.getSequencer();
//			sequencer = MidiSystem.getSequencer( false );
			sequencer.open();

			// Clear transmitters already connected to the sequencer...
//			List<Transmitter> transmittersToClose = sequencer.getTransmitters();
//			for( Transmitter transmitterToClose : transmittersToClose ) {
//				Receiver receiverToClose = transmitterToClose.getReceiver();
//				receiverToClose.close();
//				transmitterToClose.close();
//			}

			deviceRx = device.getReceiver();
			console.println( "Playing a note on the reciever..." );
			try {
				ShortMessage msgOn = new ShortMessage();
				msgOn.setMessage( ShortMessage.NOTE_ON, 1, 60, 127 );
				deviceRx.send( msgOn, -1 );
				Thread.sleep(5000);
				ShortMessage msgOff = new ShortMessage();
				msgOff.setMessage( ShortMessage.NOTE_OFF, 1, 60, 0 );
				deviceRx.send( msgOff, -1 );
			} catch( InvalidMidiDataException e ) {}
			
			sequencerTx = sequencer.getTransmitter();

			sequencerTx.setReceiver( deviceRx );	

			List<Transmitter> transmitters = sequencer.getTransmitters();
			console.println( transmitters.size() + " transmitters:" );
			for( Transmitter existingTransmitter : transmitters) {
				console.println( "    " + existingTransmitter.toString() );
				Receiver exTxRx = existingTransmitter.getReceiver();
				if( exTxRx != null ) {
					console.println( "    Receiver: " + exTxRx.toString() );
				}
			}

			List<Receiver> receivers = device.getReceivers();
			console.println( receivers.size() + " receivers:" );
			for( Receiver existingReceiver : receivers ) {
				console.println( "    " + existingReceiver.toString() );
			}

			Synthesizer synth = MidiSystem.getSynthesizer();
			MidiDevice.Info synthInfo = synth.getDeviceInfo();
			console.println();
			console.println( "Device synth: " + synthInfo.getName() );
			console.println( "     " + synthInfo.getDescription() );
			console.println( "     " + synthInfo.getVendor() );
			console.println( "     " + synthInfo.getVersion() );
			console.println();

			List<Receiver> synthReceivers = synth.getReceivers();
			console.println( synthReceivers.size() + " receivers for synth:" );
			for( Receiver existingSynthReceiver : synthReceivers ) {
				console.println( "    " + existingSynthReceiver.toString() );
			}

			if( sequencer == null ) {
				System.err.println( "Sequencer device not supported" );
				return;
			}


			File dir0 = new File( args[0] );
			File dir1 = new File( args[1] );

			File[] dirList0 = dir0.listFiles();
			File[] dirList1 = dir1.listFiles();

			console.promptForExit();
		
			// Listen for pin A or pin B

			final GpioPinDigitalInput coinslotA = gpio.provisionDigitalInputPin( RaspiPin.GPIO_01, PinPullResistance.PULL_UP );
			final GpioPinDigitalInput coinslotB = gpio.provisionDigitalInputPin( RaspiPin.GPIO_04, PinPullResistance.PULL_UP );

			coinslotA.setDebounce( 1000 );
			coinslotB.setDebounce( 1000 );

			coinslotA.setShutdownOptions( true );
			coinslotB.setShutdownOptions( true );
			
			coinslotA.addListener( new GpioPinListenerDigital() { 
				@Override
				public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {
					if( event.getState() == PinState.LOW ) {
		       				if( !sequencer.isRunning() ) {
					        	console.println( "A was pressed. Now playing file " + dirList0[ dirListIndex0 ] );
							try {
								Sequence sequence = getSequenceFromFile( dirList0[dirListIndex0] );
								sequencer.open();
								device.open();
								sequencer.setSequence( sequence );
								sequencer.start();
							} catch ( InvalidMidiDataException | MidiUnavailableException ex ) {
								ex.printStackTrace();
							}
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
			                    	if( !sequencer.isRunning() ) {
					        	console.println( "B was pressed. Now playing file " + dirList1[ dirListIndex1 ] );
							try { 
								Sequence sequence = getSequenceFromFile( dirList1[dirListIndex1] );
								sequencer.open();
								device.open();
								sequencer.setSequence( sequence );
								sequencer.start();
							} catch ( InvalidMidiDataException | MidiUnavailableException ex ) {
								ex.printStackTrace();
							}
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
			device.close();
			sequencer.close();
		} catch ( MidiUnavailableException ex ) {
			ex.printStackTrace();
		}
		console.waitForExit();
		gpio.shutdown();
	}
	
	public static Sequence getSequenceFromFile ( String fileToPlay ) {
		File sequenceFile = new File( fileToPlay );
		return getSequenceFromFile ( sequenceFile );
	}

	public static Sequence getSequenceFromFile ( File fileToPlay ) {
		try {
			Sequence sequence = MidiSystem.getSequence( fileToPlay );
			Track track = sequence.createTrack();

			// Insert an unplayable note at the start of the sequence
			track.add( makeEvent( ShortMessage.NOTE_ON, 1, 100, 64, 30 ) );
			track.add( makeEvent( ShortMessage.NOTE_OFF, 1, 100, 64, 30 + 70 ) );

			// Find out how long the sequence is in seconds
			long timeLength = sequence.getMicrosecondLength(); // 5900000 ms = 5900
			long tickLength = sequence.getTickLength();
			long msPerTick = timeLength / tickLength;
			long startTheEnd = ( timeLength - ( endOffsetSeconds * 1000000 ) ) / msPerTick;

			// Insert an unplayable note 5 seconds before the end of the sequence
			track.add( makeEvent( ShortMessage.NOTE_ON, 1, 105, 64, (int)startTheEnd  ) );
			track.add( makeEvent( ShortMessage.NOTE_OFF, 1, 105, 64, (int)startTheEnd + 70 ) );
				
			return sequence;
		} catch ( InvalidMidiDataException | IOException ex ) {
			return null;
=======
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
>>>>>>> d64205a213d07a4efe9e5b64092939c59fa10ee0
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

