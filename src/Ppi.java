// TODO
// We need Read() Write() for microprocessor
//      There are no restrictions here
// We need Read() Write() for external place
//      Restriction for reading/writing according to mode
// No initialize PPI which is Write(ports[3])
//      Manage IO / BSR mode when writing in ports[3]

class Ppi extends Thread {
    public Microprocessor up;
    public Register8[] ports;
    public Register8 baseAddress;

    // Get if the address belong to me or not
    private boolean isMine(Register8 addr) {
        int index = getIndex(addr);
        if ( index >= ports.length  || index < 0 )
            return false;
        return true;
    }

    public void debugGet(Register8 addr) {
        if ( !isMine(addr) )
            throw new IndexOutOfBoundsException();
        int index = getIndex(addr);
        System.out.println( ports[index].hex());
    }

    // Mode Selection for portA
    // controlRegister  PortA
    //    D[6]D[5]      Mode
    //    --------      ----
    //      00           0
    //      01           1
    //      1X           2

    // PortA mode verification
    private int portAMode() {
        if(ports[3].get(6))
            return 2;
        else {
            if (ports[3].get(5) == false )
                return 0;
            else
                return 1;
        }
    }
    // PortB mode verification
    private int portBMode() {
        if(ports[3].get(2))
            return 1;
        else
            return 0;
    }
    // Mode of port[index]
    // Mode of port[3] ie, portC is always 0
    private int ModeSelect(int index) {
        int mode = 0;
        if(index == 0) {
            mode = portAMode();
        }
        if(index == 1) {
            mode = portBMode();
        }
        return mode;
    }
    // returns the mode of PortC Lower as boolean
    // 0 = write(output)
    // 1 = read(input)
    public boolean portCLowerIOfunc() {
        return ports[3].get(0);
    }

    // returns the IO function of portC Upper as boolean
    // 0 = write(output)
    // 1 = read(input)
    public boolean portCUpperIOfunc() {
        return ports[3].get(3);
    }

    // returns the IO function of portA as boolean
    // 0 = write(output)
    // 1 = read(input)
    public boolean portAIOfunc() {
        return ports[3].get(4);
    }

    // returns the IO function of portB as boolean
    // 0 = write(output)
    // 1 = read(input)
    public boolean portBIOfunc() {
        return ports[3].get(1);
    }

    // Get the index according to address
    // Index      Ports
    // -----      -----
    // 1          PortA
    // 2          PortB
    // 3          PortC
    // 4          ControlRegister

    private int getIndex(Register8 addr) {
        return addr.get() - baseAddress.get();
    }

    // Ppi constructor
    public Ppi(Register8 baseAddr) {
        baseAddress = baseAddr.clone();
        ports = new Register8[4];
        for (int i = 0; i < ports.length; i++)
            ports[i] = new Register8(0);
    }

    // Handle Reading data from Port C based on Modes of PortA and Port B
    private int readPortC() {
        // Port A in SimpleIO mode
        int data = 0x0;
        if(ModeSelect(0) == 0) {
            //Port B in SimpleIO mode
            if(ModeSelect(1) == 0) {
                // Read from port C Upper or C Lower or Both
                if (portCUpperIOfunc())
                    data |= ports[2].get() & 0xf0;
                if (portCLowerIOfunc())
                    data |= ports[2].get() & 0x0f;
            }
            // Port B HandshakeIO mode
            else if(ModeSelect(1) == 1) {
                // Read from Port C Upper only
                // Port C Lower is used for performing Handshaking of Port B
                // with the Microprocessor or other Devices
                if(portCUpperIOfunc())
                    data = ports[2].get() & 0xf0;
            }
        }
        // Port A in HandshakeIO mode
        else if(ModeSelect(0) == 1) {
            // Port B in SimpleIO mode
            if(ModeSelect(1) == 0) {
                // Read from Port C Lower only
                // Port C Upper is used for performing Handshaking of Port A
                // with the Microprocessor or other Devices
                if(portCLowerIOfunc())
                    data = ports[2].get() & 0x0f;
            } else if (ModeSelect(1) == 1) {
                // PortC cannot be accessed
                // Port C Upper is used for performing Handshaking of Port A
                // Port C Lower is used for performing Handshaking of Port B
                data = 0x0;
            }
        }
        // Port A in Bidirectional Data Transfer Mode
        else {
            // Port C Lower 5 bits are used for Handshaking Port A
            // in Bidirectional Data Transfer Mode
            // Upper 3 bits of Port C can be used as Simple IO or for handshaking
            // Port B based on Mode of Port B

            // Port B in SimpleIO mode
            if(ModeSelect(2) == 0) {
                // Port C Upper 3 bits are used as simple IO
                if(portCUpperIOfunc())
                    data = ports[2].get() & 0xe0;
            }
            // PortB in HandshakeIO mode
            // Port C Upper 3 bits are used for Handshaking Port B
            else if(ModeSelect(2) == 1) {
                data = 0x0;
            }
        }
        return data;
    }
    // NOTE: we never read 4bit data from PPI
    // No use for reading Cl or Cu
    // Read from a port based on the index of ports
    private Register8 ReadPort(int index) {
        int data = 0x0;
        // Handle Port C
        if(index == 2) {
            data = readPortC();
        }
        // Read from port B
        else if (index == 1) {
            if(portBIOfunc()) {
                data = ports[index].get();
            }
        }
        // Read from port A
        else if(index == 0) {
            if(portAIOfunc()) {
                data = ports[index].get();
            }
        }
        // Read from Control Register
        else {
            data = ports[3].get();
        }
        return new Register8(data);
    }

    // Handle Writing data to Port C based on Modes of PortA and Port B
    private void writePortC(int value) {
        // Port A in SimpleIO mode
        if(ModeSelect(0) == 0) {
            //Port B in SimpleIO mode
            if(ModeSelect(1) == 0) {
                // Write to port C Upper or C Lower or Both
                if (portCUpperIOfunc() == false) {
                    int data = ports[2].get() & 0x0f;
                    data |= (value & 0xf0);
                    ports[2].set(data);
                }
                if (portCLowerIOfunc() == false) {
                    int data = ports[2].get() & 0xf0;
                    data |= (value & 0x0f);
                    ports[2].set(data);
                }
            }
            // Port B HandshakeIO mode
            else if(ModeSelect(1) == 1) {
                // Write to  Port C Upper only
                // Port C Lower is used for performing Handshaking of Port B
                // with the Microprocessor or other Devices
                if(portCUpperIOfunc() == false) {
                    int data = ports[2].get() & 0x0f;
                    data |= (value & 0xf0);
                    ports[2].set(data);
                }
            }
        }
        // Port A in HandshakeIO mode
        else if(ModeSelect(0) == 1) {
            // Port B in SimpleIO mode
            if(ModeSelect(1) == 0) {
                // Write to  Port C Lower only
                // Port C Upper is used for performing Handshaking of Port A
                // with the Microprocessor or other Devices
                if (portCLowerIOfunc() == false) {
                    int data = ports[2].get() & 0xf0;
                    data |= (value & 0x0f);
                    ports[2].set(data);
                }
            } else if (ModeSelect(1) == 1) {
                // PortC cannot be accessed
                // Port C Upper is used for performing Handshaking of Port A
                // Port C Lower is used for performing Handshaking of Port B
                // do nothing
            }
        }
        // Port A in Bidirectional Data Transfer Mode
        else {
            // Port C Lower 5 bits are used for Handshaking Port A
            // in Bidirectional Data Transfer Mode
            // Upper 3 bits of Port C can be used as Simple IO or for handshaking
            // Port B based on Mode of Port B

            // Port B in SimpleIO mode
            if(ModeSelect(2) == 0) {
                // Port C Upper 3 bits are used as simple IO
                if(portCUpperIOfunc() == false) {
                    int data = ports[2].get() & 0x1f;
                    data |= (value & 0x0e);
                    ports[2].set(data);
                }
            }
            // PortB in HandshakeIO mode
            // Port C Upper 3 bits are used for Handshaking Port B
            else if(ModeSelect(2) == 1) {
                // do nothing
            }
        }
    }

    // write on ports based on index positon
    // new data on ports[index] = value
    private void  WritePort(int index , int value) {
        // Write to ControlRegister
        if (index == 3) {
            ports[3].set(value);
            // if controlRegister bit D[7] == 0 it is in BSR mode
            if (ports[3].get(7) == false) {
                // select bit position of PortC based on bit
                //values D[3]D[2]D[1]  of controlRegister and sets or resets it
                int BitPos = ports[3].get(3, 1);
                boolean  val = ports[3].get(0);
                ports[2].set(BitPos, val);
            }
        }
        // Write to Port C
        else if(index == 2) {
            writePortC(value);
        }
        // Write to Port A
        else if (index == 0) {
            if(portAIOfunc() == false) {
                ports[index].set(value);
            }
        }
        // Write to PortB
        else {
            if(portBIOfunc() == false) {
                ports[index].set(value);
            }
        }
    }

    // Get data from IO port
    private Register8 get(Register8 position) {
        if ( !isMine(position) )
            throw new IndexOutOfBoundsException();
        return ReadPort(getIndex(position));
    }

    // Set data to IO port
    private void  set(Register8 position, Register8 reg) {
        if ( !isMine(position) )
            throw new IndexOutOfBoundsException();
        WritePort(getIndex(position), reg.get());
    }

    // External interface to Set Values of Ports
    public void externalSet(Register8 position, Register8 value) {
        if ( !isMine(position) )
            throw new IndexOutOfBoundsException();
        int index = getIndex(position);
        // External device can write to PortA, PortB, PortC only
        if(index > 2)
            throw new IndexOutOfBoundsException();
        // Set value of Port C
        if(index == 2) {
            if(portCLowerIOfunc()) {
                int data = value.get() & 0x0f;
                data |= ports[2].get() & 0xf0;
                ports[2].set(data);
            }
            if(portCUpperIOfunc()) {
                int data = value.get() & 0xf0;
                data |= ports[2].get() & 0x0f;
                ports[2].set(data);
            }
        }
        // Set value of Port B
        if (index == 1) {
            if(portBIOfunc())
                ports[1].set(value.get());
        }
        // Set value of Port A
        if (index == 0) {
            if(portAIOfunc())
                ports[0].set(value.get());
        }
    }

    // External interface to read values from ports
    public Register8 externalGet(Register8 position) {
        if ( !isMine(position) )
            throw new IndexOutOfBoundsException();
        int index = getIndex(position);
        // External device can read from portA , portB, portC only
        if(index > 2)
            throw new IndexOutOfBoundsException();
        // Get value of Port C
        if(index == 2) {
            if((portCLowerIOfunc() == false) | (portCUpperIOfunc() == false)) {
                return new Register8(ports[2].get());
            }
        }
        // Get value of Port B
        if (index == 1) {
            if(portBIOfunc() == false)
                return new Register8(ports[1].get());
        }
        // Get value of Port A
        if (index == 0) {
            if(portAIOfunc() == false)
                return new Register8(ports[0].get());
        }
        return new Register8(0x00);
    }

    public void run() {
        try {
            synchronized (up) {
                System.out.println("IO at " + baseAddress.hex() + " started!");
                while ( true ) {
                    up.wait();
                    if ( !(up.iom == true) )
                        continue;

                    if (up.write) {
                        Register8 taddress = up.busL.clone();
                        // Shouldn't notify if address doesn't belong
                        // to the io device
                        if (!isMine(taddress))
                            continue;
                        up.notify();
                        up.wait();
                        Register8 tdata = up.busL.clone();
                        set(taddress, tdata);
                        up.notify();
                    } else if (up.read) {
                        Register8 taddress = up.busL.clone();
                        // Shouldn't notify if address doesn't belong
                        // to the io device
                        if (!isMine(taddress))
                            continue;
                        up.busL = get(taddress);
                        up.notify();
                    } else {
                        if (up.read && up.write)
                            System.out.print("R/W signal error. ");
                        // if both of them are false then this may occur
                        // when forced to stop
                    }
                }
            }
        } catch (InterruptedException i) {
            System.out.println("IO at " + baseAddress.hex() + " released!");
        }
    }

    private  void print() {

    }
}

