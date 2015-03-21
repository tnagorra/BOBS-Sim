// TODO
// We need Read() Write() for microprocessor
//      There are no restrictions here
// We need Read() Write() for external place
//      Restriction for reading/writing according to mode
// No initialize PPI which is Write(ports[3])
//      Manage IO / BSR mode when writing in ports[3]

class Ppi extends Thread {

    Microprocessor up;
    Register8[] ports;
    Register8 baseAddress;

    // Get if the address belong to me or not
    private boolean isMine(Register8 addr) {
        int index = getIndex(addr);
        if ( index >= ports.length  || index < 0 )
            return false;
        return true;
    }

    // Get the index according to address
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

    // NOTE: we never read 4bit data from PPI
    // No use for reading Cl or Cu
    private Register8 ReadPort(int index) {
        int data = 0x0;
        if (index == 2) {
            if (ports[2].get(4))
                data |= ports[2].get() & 0xf0;
            else if (ports[2].get(0))
                data |= ports[2].get() & 0x0f;
        } else
            data = ports[index].get();
        return new Register8(data);
    }

    private void  WritePort(int index , int value) {
        if (index == 3) {
            ports[3].set(value);
            if (ports[3].get(7) == false) {
                int BitPos = ports[3].get(3, 1);
                boolean  val = ports[3].get(0);
                ports[2].set(BitPos, val);
            }
        } else if (index == 2) {
            if (ports[3].get(4) == false) {
                int data = (value & 0xf0) | (value & 0x0f);
                ports[2].set(data);
            }
            if (ports[3].get(0) == false) {
                int data = (value & 0xf0) | (value & 0x0f);
                ports[2].set(data);
            }
        } else
            ports[index].set(value);
    }

    // Get data from IO port
    public Register8 get(Register8 position) {
        if ( !isMine(position) )
            throw new IndexOutOfBoundsException();
        return ReadPort(getIndex(position));
    }

    // Set data to IO port
    public void  set(Register8 position, Register8 reg) {
        if ( !isMine(position) )
            throw new IndexOutOfBoundsException();
        WritePort(getIndex(position), reg.get());
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


