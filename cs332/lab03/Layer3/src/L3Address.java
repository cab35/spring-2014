/**
 * This class defines the properties of the L3 Address scheme.
 * It is similar to IPv4 but on a much smaller scale and uses
 * the machines unique physical address for the host portion.
 * This eliminates the need for a protocol like ARP.
 */
public class L3Address {
	//Broadcast address - all 1s
	//Use the constant from Layer2Frame
	
	//Network and Host bit-sizes
	private static final int NETWORK_SIZE = 8;
	private static final int HOST_SIZE = 8;

	//value from 0 to 255 (0xff)
	private int myNetworkPart;
	private int myHostPart;

	//handle ints < 0 && ints > 255 - handled @ L3Display
	
	/**
	 * Constructor: create an L3 address from a network and host
	 * @param net, the network part of the address
	 * @param host, the host part of the address
	 */
	public L3Address( int net, int host ) {
		this.myNetworkPart = net;
		this.myHostPart = host;
	}

	/**
	 * create an L3 address from a string of bits
	 * @param bits, a 16-bit field per spec
	 */
	public L3Address( String bits ) {
		//split the string for network and host parts
		String net = bits.substring( 0, NETWORK_SIZE ),
				host = bits.substring( NETWORK_SIZE );
		assert ( host.length() == HOST_SIZE ): 
			"bitString Error! bitString bits was not 16-bits.";
		this.myNetworkPart = Integer.parseInt(net, 2);
		this.myHostPart = Integer.parseInt(host, 2);
		assert ( toString().length() == NETWORK_SIZE + HOST_SIZE ):
			"Address Error! bitString is not 16-bits.";
	}

	/**
	 * takes in a string which is of the form: "network.host"
	 * and parses out the integer values
	 * @param dottedAddress, the dotted address as a String
	 */
	public void fromDottedDecimal( String dottedAddress ) {
		//parse using the '.'
		int parseIndex = dottedAddress.indexOf( '.' );
		String net = dottedAddress.substring( 0, parseIndex ),
				host = dottedAddress.substring( parseIndex + 1 );
		this.myNetworkPart = Integer.parseInt( net );
		this.myHostPart = Integer.parseInt( host );
	}

	/**
	 * converts the network and host parts into a single, dotted string
	 * @return String of the form: "myNetworkPart.myHostPart"
	 */
	public String toDottedDecimal() {
		return Integer.toString(myNetworkPart) + "." + Integer.toString(myHostPart);
	}

	/**
	 * converts the address to a binary string acceptable for transmission
	 * @return 16-bit string representing the address
	 */
	public String toString() {
		//makes sure the string is 16-bits long, toBinaryString() 
		//does not pad zeros...
		return Layer2Frame.toBinary(myNetworkPart, NETWORK_SIZE) +
				Layer2Frame.toBinary(myHostPart, HOST_SIZE);
	}

	/**
	 * getters for network and host
	 * @return host or network part
	 */
	public int getHost() { return myHostPart; }
	public int getNetwork() { return myNetworkPart; }

	/**
	 * returns true if the host part is the BCAST address
	 * @return
	 */
	public boolean isBcast() {
		return myNetworkPart == Layer2Frame.BCAST_ADDRESS && 
				myHostPart == Layer2Frame.BCAST_ADDRESS;
	}

	/**
	 * Checks my address with another, returns true if identical
	 * @param other
	 * @return
	 */
	public boolean equals( L3Address other  ) {
		if ( this.myNetworkPart == other.getNetwork() && this.myHostPart == other.getHost() )
			return true;
		else 
			return false;
	}

}