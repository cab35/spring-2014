/**
 * This class handles the protocol type for layer 3.
 * When sending packets down to layer 2 it prepends
 * a 2-bit field designating the protocol type of 
 * layer 3.  When a packet comes in it strips the
 * 2-bit field and passes the remaining packet to the 
 * proper layer 3 protocol (i.e. L3, NDP, ect).
 */
public class L3Shim implements Layer2Listener {
	public static final int L3TYPE_L3 = 0x0;
	public static final int L3TYPE_NDP = 0x1;	//Future use
	public static final int L3TYPE_02 = 0x2;	//Future protocol
	public static final int L3TYPE_03 = 0x3;	//Future protocol
	
	private static final int L3TYPE_FIELD = 2;	//shim field size
	private static final int L3SHIM_MAX_VALUE = 4;	//shim field values 

	private Layer2Endpoint myL2E;	//extends the Layer2Handler (REMEMBER THIS!)
	private L3ShimListener[] myListener = new L3ShimListener[L3SHIM_MAX_VALUE];

	/**
	 * Constructor: stores a L2Endpoint and sets it as a
	 * listener for frames from layer 2.
	 * @param l2e, the layer 2 endpoint
	 */
	public L3Shim( Layer2Endpoint l2e ) {
		this.myL2E = l2e;		
		this.myL2E.setListener( this );
	}

	/**
	 * receives an incoming frame and determines which layer 3 protocol
	 * to send the packet to (L3, NDP, ect).  Drops the packet if the 
	 * protocol type is invalid.
	 * @param handler
	 * @param frame
	 */
	@Override
	public void frameReceived( Layer2Handler handler, Layer2Frame frame ) {
		//Debug
		//System.out.println("Frame Received...L3Shim...");
		
		//grab the first two bits to check protocol type
		int protocol = Integer.parseInt(frame.getPayload().substring(0, 2), 2);
		if ( protocol == L3TYPE_L3 ) 
		{	
			try
			{
				//Debug
				//System.out.println("L3 Protocol Chosen...");
				//Get the remaining portion of the frame payload, it is the L3Packet
				String packetString = frame.getPayload().substring(2);
				//Pass to the L3Handler
				if ( myListener[L3TYPE_L3] != null ) 
					myListener[L3TYPE_L3].packetReceived( myL2E, packetString );
			}
			catch ( Exception e )
			{
				System.err.println("Bad frame payload.  Dropping...");
			}			
		}
		else if ( protocol == L3TYPE_NDP ) 
		{
			System.out.println("NDP Protocol Chosen");
			//TODO - stub for NDP
		}
		else		//Invalid protocol type - drop the packet
		{
			System.err.println("Invalid Protocol Type. Dropping...");
		}
		
	}

	/**
	 * Create and send a Layer2Frame which contains a L3Packet,
	 * with the L3Shim on the front
	 * @param bits, the L3 bit-string
	 * @param protocol, which L3 protocol is being used
	 * @param destMAC, MAC the frame needs to be sent to
	 */
	public void send( String bits, int protocol, int destMAC ) {
		//Debug
		//System.out.println("Sending Packet...L3Shim...");
		
		/* srcMAC is the host part of source L3Address...duh
		 * get it from the bits String (assume bits is a L3Packet)
		 * should be the 4th octet (bits 24-31).
		 * We can't do the same with the destMAC because there is a 
		 * chance that the protocol being used at layer 3 isn't L3 
		 * in which case the destMAC may not even exist, so it needs
		 * to be a parameter instead.
		 */
		int vlanID = 0,
				srcMAC = Integer.parseInt( bits.substring(24, 32), 2 );
		String L3ShimHeader = "";
		if ( protocol == L3TYPE_L3 )
			L3ShimHeader = Layer2Frame.toBinary( L3TYPE_L3, L3TYPE_FIELD );
		else if ( protocol == L3TYPE_NDP )
			L3ShimHeader = Layer2Frame.toBinary( L3TYPE_NDP, L3TYPE_FIELD );
		else	
			//unknown protocol type, assume L3 and let the receiver
			//handle any errors (i.e. drop the packet)
			L3ShimHeader = Integer.toBinaryString( L3TYPE_L3 );
		//Debug
		//System.out.println(L3ShimHeader);
		
		//create and send the L2Frame
		Layer2Frame l2f = new Layer2Frame( destMAC, srcMAC, vlanID, L3ShimHeader + bits );
		myL2E.send( l2f );
	}

	/**
	 * register a listener for the protocol (L3 or
	 * NDP, possibly others) with the index protoType
	 * @param l, the listener
	 * @param protocolType, the protocol type and array index
	 */
	public void setListener( L3ShimListener l, int protocolType ) {
		assert (myListener[protocolType] == null): "LISTENER ALREADY SET. ERROR!";
		myListener[protocolType] = l;		
	}

}
