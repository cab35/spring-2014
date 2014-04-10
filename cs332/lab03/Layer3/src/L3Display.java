import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * This class handles the GUI for L3.  It allows for the
 * sending and receiving of L3 packets.
 */
public class L3Display implements ActionListener, L3Listener {
	//Address bounds
	private static final int MAX_VALUE = 0xff;
	private static final int MIN_VALUE = 0x0;
	
	private L3Handler handler;
	private JTextField displayField;
	private JTextField destAddrField;
	private JTextField payloadField;

	/**
	 * Constructor: create a GUI for layer 3
	 * @param l3h, a L3Handler, handles incoming and 
	 * outgoing packets
	 */
	public L3Display( L3Handler l3h ) {
		this.handler = l3h;
		handler.setListener( this );

		JFrame frame = new JFrame("L3 Address: " + handler.toString());
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

		displayField = new JTextField(20);
		displayField.setEditable(false);
		frame.getContentPane().add(displayField);

		//Destination address - L3
		frame.getContentPane().add( new JLabel("Dest L3 (int 0-255, network.host):") );

		destAddrField = new JTextField(20);
		destAddrField.addActionListener(this);
		frame.getContentPane().add( destAddrField );

		//Payload - L3
		frame.getContentPane().add(new JLabel("Payload (bitString): "));

		payloadField = new JTextField(20);
		payloadField.addActionListener(this);
		payloadField.setText("1010");
		frame.getContentPane().add( payloadField );

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Called when the layer below receives a frame.
	 * Output the corresponding data to the GUI for L3
	 */
	@Override
	public void packetReceived(L3Handler l3h, L3Packet packet) {
		displayField.setText( "From: " + packet.getSrcAddr().toDottedDecimal() + 
				/*" TTL: " + Integer.toString( packet.getHopsLeft() ) +*/
				" Paylaod: " + packet.getPayload() );
	}

	/**
	 * On event (i.e. user hits ENTER). get values from fields, 
	 * create a L3Packet and transmit the message accordingly
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		displayField.setText("Sending...");
		new Thread()
		{
			public void run() {
				String l3DestAddr = destAddrField.getText();
				L3Address l3Addr = new L3Address(0, 0);
				l3Addr.fromDottedDecimal(l3DestAddr);
				//handle out of bounds addresses by bcast-ing
				if ( (l3Addr.getNetwork() > MAX_VALUE || l3Addr.getNetwork() < MIN_VALUE ) ||
						l3Addr.getHost() > MAX_VALUE || l3Addr.getHost() < MIN_VALUE )
				{
					l3Addr.fromDottedDecimal( Integer.toBinaryString(Layer2Frame.BCAST_ADDRESS) + "." +
							Integer.toBinaryString(Layer2Frame.BCAST_ADDRESS) );
				}
				String l3Payload = payloadField.getText();
				handler.send( new L3Packet( l3Addr, handler.gettAddr(), l3Payload) );
				displayField.setText( "" );
			}
		}.start();		
	}

}
